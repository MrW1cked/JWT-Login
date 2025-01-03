package com.back.sousa.models.dto;

import com.back.sousa.models.auth.RegisterRequest;
import com.back.sousa.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLogin {

    private Integer ccNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @Builder.Default
    private Role role = Role.PATIENT;
    @Builder.Default
    private LocalDate memberStartingDate = LocalDate.now();
    @Builder.Default
    private LocalDate memberEndingDate = null;
    @Builder.Default
    private Boolean hasParish = false;
    @Builder.Default
    private Boolean wasDispatched = false;


    public static UserLogin createNewUserByRequest(RegisterRequest registerRequest){
        return UserLogin.builder()
                .ccNumber(registerRequest.getCcNumber())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .build();
    }
}
