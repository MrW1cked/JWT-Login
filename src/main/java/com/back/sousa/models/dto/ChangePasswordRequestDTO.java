package com.back.sousa.models.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Builder
@Validated
public class ChangePasswordRequestDTO {

    @NonNull
    @Size(min = 6, max = 50)
    private String currentPassword;

    @NonNull
    @Size(min = 6, max = 50)
    private String newPassword;

    @NonNull
    @Size(min = 6, max = 50)
    private String confirmationPassword;
}
