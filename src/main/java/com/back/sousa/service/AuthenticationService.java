package com.back.sousa.service;

import com.back.sousa.config.JwtService;
import com.back.sousa.exceptions.UserBlockedException;
import com.back.sousa.helpers.messages.I18NKeys;
import com.back.sousa.helpers.messages.MessagesService;
import com.back.sousa.models.auth.AuthenticationRequest;
import com.back.sousa.models.auth.AuthenticationResponse;
import com.back.sousa.models.auth.RegisterRequest;
import com.back.sousa.models.database.login.UserLoginMO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MessagesService messagesService;
    private final ValidationService validationService;
    private final TokenService tokenService;
    private final EmailService emailService;

    private final UserLoginService userLoginService;
    private final LoginAttemptService loginAttemptService;

    public AuthenticationResponse register(RegisterRequest request) throws IOException {
        AuthenticationResponse errors = validationService.validateUser(request);

        if (existErrors(errors)) {
            return errors;
        }

        // Build user from request
        var user = userLoginService.buildUserLoginMO(request);

        // Define user as not verified by default
        user.setEmailVerified(false);

        // Generate a random token and set it to the user
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationTokenExpiration(LocalDateTime.now().plusHours(24));

        // Save the user
        UserLoginMO savedUser = userLoginService.createUser(user);

        //send verification email
        sendEmailVerificationToken(savedUser);

        // Generate JWT token
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        tokenService.saveUserToken(savedUser, jwtToken);

        return buildAuthenticationResponse(jwtToken, refreshToken);
    }

    private void sendEmailVerificationToken(UserLoginMO savedUser) throws IOException {
        emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getVerificationToken());
    }

    private AuthenticationResponse buildAuthenticationResponse(String jwtToken, String refreshToken) {
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private boolean existErrors(AuthenticationResponse errors) {
        return !errors.getErrorMessage().isEmpty();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            loginAttemptService.checkIfsBlocked(request.getCcNumber());

            authenticationManager.authenticate(
                    buildAuthentication(request)
            );

            UserLoginMO user = userLoginService.checkIfUserExists(request.getCcNumber());

            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            tokenService.revokeAllUserTokens(user);
            tokenService.saveUserToken(user, jwtToken);

            loginAttemptService.resetLoginAttemptsIfNecessary(request.getCcNumber());

            return buildAuthenticationResponse(jwtToken, refreshToken);
        } catch (BadCredentialsException e) {
            loginAttemptService.incrementLoginAttempts(request.getCcNumber());
            throw new UserBlockedException(messagesService.getMessage(I18NKeys.UserMessage.INVALID_CREDENTIALS));
        }
    }

    private UsernamePasswordAuthenticationToken buildAuthentication(AuthenticationRequest request) {
        return new UsernamePasswordAuthenticationToken(
                request.getCcNumber(),
                request.getPassword()
        );
    }


    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String refreshToken = authHeader.substring(7);
        Integer ccNumber = Integer.valueOf(jwtService.extractUsername(refreshToken));
        if (ccNumber != null) {
            var user = userLoginService.checkIfUserExists(ccNumber);
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                tokenService.revokeAllUserTokens(user);
                tokenService.saveUserToken(user, accessToken);
                var authResponse = buildAuthenticationResponse(accessToken, refreshToken);
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}