package com.back.sousa.config;

import com.back.sousa.models.database.login.LogsMO;
import com.back.sousa.repositories.LogsRepository;
import com.back.sousa.service.RequestService;
import com.back.sousa.service.UserLoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogIpAspect {

    private final HttpServletRequest requestIp;
    private final RequestService requestService;
    private final LogsRepository logsRepository;
    private final UserLoginService userLoginService;

    @Pointcut("@annotation(com.back.sousa.helpers.custom_interfaces.LogIpIntoTable)")
    public void logIpPointcut() {
    }

    @AfterReturning("logIpPointcut()")
    public void logIpAfterMethod(JoinPoint joinPoint) {
        String ipAddress = requestService.getClientIp(requestIp);
        String methodName = joinPoint.getSignature().getName();
        Integer patientCCNumber;

        if (methodName.equals("register")) {
            patientCCNumber = 0;
        } else if (methodName.equals("authenticate")) {
            patientCCNumber = 0;
        } else if (methodName.equals("verifyEmail")) {
            patientCCNumber = 0;
        } else if (methodName.equals("buildLogMessage")) {
            patientCCNumber = 0;
        } else {
            patientCCNumber = userLoginService.getLoggedOnUserCCNumber();
        }

        log.info("IP Address: {}, Method: {}, Logged On Patient CC Number: {}", ipAddress, methodName, patientCCNumber);

        saveLogIntoTable(ipAddress, methodName, patientCCNumber);
    }

    private void saveLogIntoTable(String ipAddress, String methodName, Integer patientCCNumber) {
        LogsMO log = LogsMO.builder()
                .requestIp(ipAddress)
                .methodName(methodName)
                .userCCNumber(patientCCNumber)
                .requestDateTime(OffsetDateTime.now())
                .build();

        logsRepository.save(log);
    }
}