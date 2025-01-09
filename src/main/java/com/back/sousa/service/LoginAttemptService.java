package com.back.sousa.service;

import com.back.sousa.exceptions.UserBlockedException;
import com.back.sousa.helpers.messages.I18NKeys;
import com.back.sousa.helpers.messages.MessagesService;
import com.back.sousa.models.database.login.LoginAttemptsMO;
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

    public void incrementLoginAttempts(Integer ccNumber) {
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

    private LoginAttemptsMO createLoginAttemptForUser(Integer ccNumber) {
        return LoginAttemptsMO.builder()
                .ccNumber(ccNumber)
                .attempts(1)
                .lastAttempt(Timestamp.valueOf(LocalDateTime.now()))
                .banned(false)
                .build();
    }

    public LoginAttemptsMO getLoginAttempts(Integer ccNumber) {
        return loginAttemptsRepository.findById(ccNumber).orElse(null);
    }

    public void resetLoginAttemptsIfNecessary(Integer ccNumber) {
        LoginAttemptsMO loginAttempts = getLoginAttempts(ccNumber);
        if (loginAttempts != null && loginAttempts.getAttempts() > 0) {
            resetLoginAttempts(loginAttempts);
        }
    }

    public void resetLoginAttempts(LoginAttemptsMO loginAttempts) {
        loginAttempts.setAttempts(0);
        loginAttemptsRepository.save(loginAttempts);
    }

    public void checkIfsBlocked(Integer ccNumber) {
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

    public void checkAccoutStatus(@NonNull Integer ccNumber) {
        checkIfsBlocked(ccNumber);
        checkIfAsValidatedEmailToken(ccNumber);
    }

    private void checkIfAsValidatedEmailToken(@NonNull Integer ccNumber) {
        if (!userLoginService.checkIfUserExists(ccNumber).getEmailVerified()) {
            throw new UserBlockedException(messagesService.getMessage(I18NKeys.UserMessage.EMAIL_NOT_VALIDATED));
        }
    }
}
