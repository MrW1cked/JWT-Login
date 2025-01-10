package com.back.sousa.models.auth;

import com.back.sousa.helpers.custom_interfaces.ValidCCNumber;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

  @NonNull
  @ValidCCNumber
  private String ccNumber;

  @NonNull
  @Size(min = 6, max = 50)
  private String password;
}
