package com.back.sousa.exceptions;

import com.back.sousa.models.database.login.ExceptionMO;
import com.back.sousa.repositories.ExceptionRepository;
import com.back.sousa.service.RequestService;
import com.back.sousa.service.UserLoginService;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final HttpServletRequest requestIp;
    private final ExceptionRepository exceptionRepository;
    private final RequestService requestService;
    private final UserLoginService userLoginService;

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleException(UserNotFoundException e) {
        buildExceptionLog(e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<String> handleException(UserBlockedException e) {
        buildExceptionLog(e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<String> handleException(SignatureException e) {
        buildExceptionLog(e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put("fieldName", errorMessage);
        });
        buildExceptionLog(ex);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        buildExceptionLog(e);
        return new ResponseEntity<>("A operaçäo gerou um erro de sistema. Por favor informe o seu administrador", HttpStatus.INTERNAL_SERVER_ERROR);
}

    public void buildExceptionLog(Exception e) {
        String ipAddress = requestService.getClientIp(requestIp);
        String methodName = e.getStackTrace()[0].getMethodName();
        Integer patientCCNumber;
        if (methodName.equals("register")
                || methodName.equals("authenticate")
                || methodName.equals("resolveArgument")
                || methodName.equals("verifyEmail")
                || methodName.equals("buildLogMessage"))
        {
            patientCCNumber = 0;
        } else patientCCNumber = userLoginService.getLoggedOnUserCCNumber();

        ExceptionMO exception = ExceptionMO.builder()
                .requestIp(ipAddress)
                .methodName(methodName)
                .exceptionMessage(e.getMessage().length() > 65000 ? e.getMessage().substring(0, 65000) : e.getMessage())
                .userCCNumber(patientCCNumber)
                .requestDateTime(OffsetDateTime.now())
                .build();

        exceptionRepository.save(exception);
    }

}
