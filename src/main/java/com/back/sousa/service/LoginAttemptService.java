package com.back.sousa.service;

import com.back.sousa.exceptions.UserBlockedException;
import com.back.sousa.helpers.messages.I18NKeys;
import com.back.sousa.helpers.messages.MessagesService;
import com.back.sousa.models.database.login.LoginAttemptsMO;
import com.back.sousa.models.database.login.UserLoginMO;
import com.back.sousa.repositories.LoginAttemptsRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    @Value("${login.max-attempts}")
    private int MAX_ATTEMPTS = 5;
    @Value("${login.block-duration-hours}")
    private long BLOCK_DURATION_HOURS = 1;

    private final UserLoginService userLoginService;
    private final LoginAttemptsRepository loginAttemptsRepository;
    private final MessagesService messagesService;

    public void incrementLoginAttempts(String ccNumber) {
        userLoginService.checkIfUserExists(ccNumber);

        LoginAttemptsMO loginAttempts = getLoginAttempts(ccNumber);
        if (loginAttempts == null) {
            loginAttempts = createLoginAttemptForUser(ccNumber);
        } else {
            loginAttempts.setAttempts(loginAttempts.getAttempts() + 1);
            loginAttempts.setLastAttempt(Timestamp.valueOf(LocalDateTime.now()));
        }
        loginAttemptsRepository.save(loginAttempts);
    }

    private LoginAttemptsMO createLoginAttemptForUser(String ccNumber) {
        return LoginAttemptsMO.builder()
                .ccNumber(ccNumber)
                .attempts(1)
                .lastAttempt(Timestamp.valueOf(LocalDateTime.now()))
                .banned(false)
                .build();
    }

    public LoginAttemptsMO getLoginAttempts(String ccNumber) {
        return loginAttemptsRepository.findById(ccNumber).orElse(null);
    }

    public void resetLoginAttemptsIfNecessary(String ccNumber) {
        LoginAttemptsMO loginAttempts = getLoginAttempts(ccNumber);
        if (loginAttempts != null && loginAttempts.getAttempts() > 0) {
            resetLoginAttempts(loginAttempts);
        }
    }

    public void resetLoginAttempts(LoginAttemptsMO loginAttempts) {
        loginAttempts.setAttempts(0);
        loginAttemptsRepository.save(loginAttempts);
    }

    public void checkIfsBlocked(String ccNumber) {
        LoginAttemptsMO loginAttempts = getLoginAttempts(ccNumber);

        if (loginAttempts != null) {
            if (Boolean.TRUE.equals(isPermanentlyBanned(loginAttempts))) {
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

    public Boolean isPermanentlyBanned(LoginAttemptsMO loginAttempts) {
        if (loginAttempts.getBanned() == null || !loginAttempts.getBanned()) {
            return false;
        }
        return null;
    }

    public boolean isMoreThanBlockDurationAgo(LoginAttemptsMO loginAttempts) {
        return loginAttempts.getLastAttempt().toLocalDateTime().isBefore(LocalDateTime.now().minusHours(BLOCK_DURATION_HOURS));
    }

    public boolean hasExceededMaxAttempts(LoginAttemptsMO loginAttempts) {
        return loginAttempts.getAttempts() >= MAX_ATTEMPTS;
    }

    public void banUsers(List<String> patientsCCNumber) {
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

    public void checkAccountStatus(@NonNull String ccNumber) {
        checkIfsBlocked(ccNumber);
        checkIfAsValidatedEmailToken(ccNumber);
    }

    private void checkIfAsValidatedEmailToken(@NonNull String ccNumber) {
        UserLoginMO user = userLoginService.checkIfUserExists(ccNumber);
        if (!user.getEmailVerified()) {

            //check if the token time is still valid and if not generate a new token
            if(user.getVerificationTokenExpiration().isBefore(LocalDateTime.now())){
                userLoginService.generateNewToken(user);
                throw new UserBlockedException(messagesService.getMessage(I18NKeys.UserMessage.EMAIL_NOT_VALIDATED_NEW_TOKEN));
            }
            throw new UserBlockedException(messagesService.getMessage(I18NKeys.UserMessage.EMAIL_NOT_VALIDATED));
        }
    }
}
