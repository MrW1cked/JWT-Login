package com.back.sousa.service;

import com.back.sousa.config.JwtService;
import com.back.sousa.exceptions.UserBlockedException;
import com.back.sousa.exceptions.UserNotFoundException;
import com.back.sousa.helpers.messages.I18NKeys;
import com.back.sousa.helpers.messages.MessagesService;
import com.back.sousa.mappers.UserDataMapper;
import com.back.sousa.models.auth.AuthenticationRequest;
import com.back.sousa.models.auth.AuthenticationResponse;
import com.back.sousa.models.auth.RegisterRequest;
import com.back.sousa.models.database.login.LoginAttemptsMO;
import com.back.sousa.models.database.login.TokenMO;
import com.back.sousa.models.database.login.UserLoginMO;
import com.back.sousa.models.enums.TokenType;
import com.back.sousa.repositories.LoginAttemptsRepository;
import com.back.sousa.repositories.TokenRepository;
import com.back.sousa.repositories.UserLoginRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.back.sousa.models.dto.UserLogin.createNewUserByRequest;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_HOURS = 1;
    private final UserLoginRepository userRepository;
    private final LoginAttemptsRepository loginAttemptsRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MessagesService messagesService;
    private final ValidationService validationService;
    private final UserDataMapper userDataMapper;

    public AuthenticationResponse register(RegisterRequest request) {
        AuthenticationResponse errors = validationService.validateUser(request);

        if (existErrors(errors)) {
            return errors;
        }

        var user = buildUserLoginMO(request);

        UserLoginMO savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private static boolean existErrors(AuthenticationResponse errors) {
        return !errors.getErrorMessage().isEmpty();
    }

    private UserLoginMO buildUserLoginMO(RegisterRequest request) {
        return userDataMapper.mapToUserLoginMO(createNewUserByRequest(request));
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            checkIfsBlocked(request.getCcNumber());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getCcNumber(),
                            request.getPassword()
                    )
            );

            UserLoginMO user = checkIfUserExists(request.getCcNumber());

            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            resetLoginAttemptsIfNecessary(request.getCcNumber());

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (BadCredentialsException e) {
            incrementLoginAttempts(request.getCcNumber());
            throw new UserBlockedException(messagesService.getMessage(I18NKeys.UserMessage.INVALID_CREDENTIALS));
        }
    }

    private void checkIfsBlocked(Integer ccNumber) {
        LoginAttemptsMO loginAttempts = getLoginAttempts(ccNumber);

        if (loginAttempts != null) {
            if (Boolean.TRUE.equals(isPermenantlyBanned(loginAttempts))) {
                throw new UserBlockedException(messagesService.getMessage(I18NKeys.UserMessage.PERMANENTLY_BLOCKED));
            }

            if (hasExceededMaxAttempts(loginAttempts)) {
                if (isMoreThanBlockDurationAgo(loginAttempts)) {
                    resetLoginAttempts(loginAttempts);
                } else {
                    throw new UserBlockedException(messagesService.getMessage(I18NKeys.UserMessage.HOUR_BLOCKED));
                }
            }
        }
    }

    private Boolean isPermenantlyBanned(LoginAttemptsMO loginAttempts) {
        if (loginAttempts.getBanned() == null || !loginAttempts.getBanned()) {
            return false;
        }
        return null;
    }

    private LoginAttemptsMO getLoginAttempts(Integer ccNumber) {
        return loginAttemptsRepository.findById(ccNumber).orElse(null);
    }

    private boolean isMoreThanBlockDurationAgo(LoginAttemptsMO loginAttempts) {
        return loginAttempts.getLastAttempt().toLocalDateTime().isBefore(LocalDateTime.now().minusHours(BLOCK_DURATION_HOURS));
    }

    private boolean hasExceededMaxAttempts(LoginAttemptsMO loginAttempts) {
        return loginAttempts.getAttempts() >= MAX_ATTEMPTS;
    }

    private void resetLoginAttempts(LoginAttemptsMO loginAttempts) {
        loginAttempts.setAttempts(0);
        loginAttemptsRepository.save(loginAttempts);
    }

    private void resetLoginAttemptsIfNecessary(Integer ccNumber) {
        LoginAttemptsMO loginAttempts = getLoginAttempts(ccNumber);
        if (loginAttempts != null && loginAttempts.getAttempts() > 0) {
            resetLoginAttempts(loginAttempts);
        }
    }

    private void incrementLoginAttempts(Integer ccNumber) {
        checkIfUserExists(ccNumber);

        LoginAttemptsMO loginAttempts = getLoginAttempts(ccNumber);
        if (loginAttempts == null) {
            loginAttempts = LoginAttemptsMO.builder()
                    .ccNumber(ccNumber)
                    .attempts(1)
                    .lastAttempt(Timestamp.valueOf(LocalDateTime.now()))
                    .banned(false)
                    .build();
        } else {
            loginAttempts.setAttempts(loginAttempts.getAttempts() + 1);
            loginAttempts.setLastAttempt(Timestamp.valueOf(LocalDateTime.now()));
        }
        loginAttemptsRepository.save(loginAttempts);
    }

    private UserLoginMO checkIfUserExists(Integer ccNumber) {
        Optional<UserLoginMO> user = userRepository.findById(ccNumber);

        if (user.isEmpty()) {
            throw new UserNotFoundException(messagesService.getMessage(I18NKeys.UserMessage.NOT_FOUND));
        }

        return user.get();
    }

    private void saveUserToken(UserLoginMO user, String jwtToken) {
        var token = TokenMO.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(UserLoginMO user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getCcNumber());
        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String refreshToken = authHeader.substring(7);
        Integer ccNumber = Integer.valueOf(jwtService.extractUsername(refreshToken));
        if (ccNumber != null) {
            var user = checkIfUserExists(ccNumber);
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public void banUsers(List<Integer> patientsCCNumber) {
        patientsCCNumber.forEach(ccNumber -> {
            LoginAttemptsMO loginAttempts = getLoginAttempts(ccNumber);
            if (loginAttempts == null) {
                loginAttempts = LoginAttemptsMO.builder()
                        .ccNumber(ccNumber)
                        .attempts(0)
                        .lastAttempt(Timestamp.valueOf(LocalDateTime.now()))
                        .banned(true)
                        .build();
            } else {
                loginAttempts.setBanned(true);
            }
            loginAttemptsRepository.save(loginAttempts);
        });
    }
}