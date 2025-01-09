package com.back.sousa.service;

import com.back.sousa.models.auth.AuthenticationResponse;
import com.back.sousa.models.auth.RegisterRequest;
import com.back.sousa.repositories.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final UserLoginRepository userRepository;

    public AuthenticationResponse validateUser(RegisterRequest request) {
        String errorMessage = "";
        if (userRepository.existsById(request.getCcNumber())) {
            errorMessage += "Numero de Cartão de Cidadão já existe. " + System.lineSeparator();
        }
        if (request.getPassword().length() < 6) {
            errorMessage += "A password deve ter no minimo 6 caracteres. " + System.lineSeparator();
        }

        if ((request.getCcNumber().compareTo(99999999)) == 1) {
            errorMessage += "Numero de Cartão de Cidadão inválido. " + System.lineSeparator();
        }

        if (!userRepository.findByEmail(request.getEmail()).isEmpty()) {
            errorMessage += "Email já existe. " + System.lineSeparator();
        }

        return AuthenticationResponse.builder()
                .errorMessage(errorMessage.isEmpty() ? "" : "Erro nos seguintes campos: " + System.lineSeparator()
                        + errorMessage)
                .build();
    }
}
