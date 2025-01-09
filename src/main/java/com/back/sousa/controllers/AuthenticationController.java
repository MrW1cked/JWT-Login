package com.back.sousa.controllers;

import com.back.sousa.models.auth.AuthenticationRequest;
import com.back.sousa.models.auth.AuthenticationResponse;
import com.back.sousa.models.auth.RegisterRequest;
import com.back.sousa.models.dto.ChangePasswordRequestDTO;
import com.back.sousa.service.AuthenticationService;
import com.back.sousa.service.EmailService;
import com.back.sousa.service.PasswordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authenticationService;
  private final PasswordService passwordService;
  private final EmailService emailService;


  @PostMapping("/register")

  public ResponseEntity<AuthenticationResponse> register(
          @Valid  @RequestBody RegisterRequest request
  ) throws IOException {
    return ResponseEntity.ok(authenticationService.register(request));
  }
 
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
          @Valid @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(authenticationService.authenticate(request));
  }

 
  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    authenticationService.refreshToken(request, response);
  }

  @PatchMapping("/users/change-password")
  public ResponseEntity<?> changePassword(
          @Valid @RequestBody ChangePasswordRequestDTO request,
          Principal connectedUser
  ) {
    passwordService.changePassword(request, connectedUser);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/verify-email")
  public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
    emailService.verifyEmail(token);
    return ResponseEntity.ok("Email verificado com sucesso!");
  }
}
