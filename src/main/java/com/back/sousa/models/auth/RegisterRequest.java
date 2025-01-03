package com.back.sousa.models.auth;

import com.back.sousa.helpers.custom_interfaces.ValidCCNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class RegisterRequest {

  @NonNull
  @ValidCCNumber
  private Integer ccNumber;

  @NonNull
  @Size(min = 2, max = 50)
  private String firstName;

  @NonNull
  @Size(min = 2, max = 50)
  private String lastName;

  @NonNull
  @Email
  private String email;

  @NonNull
  @Size(min = 6, max = 50)
  private String password;
}
