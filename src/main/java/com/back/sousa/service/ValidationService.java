package com.back.sousa.service;

import com.back.sousa.exceptions.DataExistentException;
import com.back.sousa.exceptions.GlobalExceptionHandler;
import com.back.sousa.models.auth.AuthenticationResponse;
import com.back.sousa.models.auth.RegisterRequest;
import com.back.sousa.repositories.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class ValidationService {

    private final UserLoginRepository userRepository;
    private final GlobalExceptionHandler globalExceptionHandler;

    public AuthenticationResponse validateUser(RegisterRequest request) {
        StringBuilder sb = new StringBuilder();

        if (userRepository.existsById(request.getCcNumber())) {
            sb.append("\n- Numero de Cartão de Cidadão já existe.");
        }

        if (request.getPassword().length() < 10) {
            sb.append("\n- A password deve ter no mínimo 10 caracteres.");
        }

        if ((request.getCcNumber().length()) > 9) {
            sb.append("\n- Numero de Cartão de Cidadão inválido.");
        }

        if (!userRepository.findByEmail(request.getEmail()).isEmpty()) {
            sb.append("\n- Email já existe.");
        }

        // Constrói a mensagem final
        String errorMessage = sb.isEmpty()
                ? ""
                : "Erro nos seguintes campos:" + sb;

        AuthenticationResponse response = AuthenticationResponse.builder()
                .errorMessage(errorMessage)
                .build();

        buildLogMessage(response);

        return response;
    }

    private void buildLogMessage(AuthenticationResponse response) {
        DataExistentException dataExistentException = new DataExistentException(response.getErrorMessage());

        if (!response.getErrorMessage().isEmpty()) {
            globalExceptionHandler.buildExceptionLog(dataExistentException);
        }
    }
}