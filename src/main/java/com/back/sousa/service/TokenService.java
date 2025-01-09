package com.back.sousa.service;

import com.back.sousa.models.database.login.TokenMO;
import com.back.sousa.models.database.login.UserLoginMO;
import com.back.sousa.models.enums.TokenType;
import com.back.sousa.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public void saveUserToken(UserLoginMO user, String jwtToken) {
        var token = TokenMO.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(UserLoginMO user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getCcNumber());
        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }
}
