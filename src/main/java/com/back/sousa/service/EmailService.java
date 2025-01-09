package com.back.sousa.service;

import com.back.sousa.repositories.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    /**
     * This class is responsible for sending emails to the user with the verification token
     * In order to use the MailTrap API, you need to register on their website and get the API key
     * <p>
     * <a href="https://mailtrap.io/">...</a>
     * <p>
     * Once registed you can get the API key and set it as an environment variable
     */

    private final UserLoginRepository userLoginRepository;
    private final UserLoginService userLoginService;

    public void sendVerificationEmail(String to, String token) throws IOException {

        // Token to be sent to the user
        String passwordToken = "Bearer " + System.getenv("MAIL_PASSWORD");

        String jsonPayload = getEmailPayload(to, token);

        // Create a new OkHttpClient
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonPayload);

        Request request = new Request.Builder()
                .url("https://send.api.mailtrap.io/api/send")
                .method("POST", body)
                .addHeader("Authorization", passwordToken)
                .addHeader("Content-Type", "application/json")
                .build();

        // Executa and close the response to avoid memory leaks
        Response response = client.newCall(request).execute();
        response.close();
    }

    @NotNull
    private String getEmailPayload(String to, String token) {
        String fromName = "Mailtrap Test";
        String fromEmail = "hello@demomailtrap.com";
        String subject = "Token Confirmation email";
        String text = "This test email was sent using the free Mailtrap! " +
                "Use this token to verify your account in the next 24 hours: " + token;
        String category = "Integration Test";

        return String.format("""
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
          "category": "%s"
        }
        """,
                fromEmail, fromName, to, subject, text, category
        );
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
            throw new RuntimeException("Token not valid!");
        }
    }
}