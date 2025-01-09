package com.back.sousa.service;

import com.back.sousa.repositories.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final UserLoginRepository userLoginRepository;
    private final UserLoginService userLoginService;

    public void sendVerificationEmail(String to, String token) throws IOException {
        // Lê variáveis de ambiente (ou podes usar @Value no Spring)
        String mailtrapToken = System.getenv("MAIL_PASSWORD"); // Ex.: "4c4c0f8a5f262..."
        String fromEmail = System.getenv("api@demomailtrap.com");         // Ex.: "hello@demomailtrap.com"
        String fromName = System.getenv("MAIL_USERNAME");           // Ex.: "Mailtrap Test"

        // Prepara o texto do email
        String subject = "Verificação de Email";
        String text = "Clica neste link para verificar: https://teu-site/verify?token=" + token;

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");

        // Cria o JSON do corpo
        String jsonPayload = String.format("""
      {
        "from": {
          "email": "%s",
          "name": "%s"
        },
        "to": [
          {
            "email": "%s"
          }
        ],
        "subject": "%s",
        "text": "%s",
        "category": "Integration Test"
      }
      """, fromEmail, fromName, to, subject, text);

        RequestBody body = RequestBody.create(mediaType, jsonPayload);

        Request request = new Request.Builder()
                .url("https://send.api.mailtrap.io/api/send")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + mailtrapToken)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        // Podes verificar se response.isSuccessful() e agir conforme
        response.close();
    }

    public void verifyEmail(String token) {
        var userOpt = userLoginRepository.findByVerificationToken(token);
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            // Generate a random token and set it to the user

            // Check if token is expired
            if (user.getVerificationTokenExpiration().isBefore(LocalDateTime.now())) {
                user.setVerificationToken(UUID.randomUUID().toString());
                user.setVerificationTokenExpiration(LocalDateTime.now().plusHours(24));
                // Save the user
                userLoginService.createUser(user);

                throw new RuntimeException("Verification Token expired! A new one was sent to your email.");
            }
            user.setEmailVerified(true);
            user.setVerificationToken(null);
            user.setVerificationTokenExpiration(null);
            userLoginRepository.save(user);
        } else {
            throw new RuntimeException("Token inválido!");
        }
    }
}
