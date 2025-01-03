package com.back.sousa.service;

import com.back.sousa.helpers.custom_interfaces.TransactionalService;
import com.back.sousa.models.dto.ChangePasswordRequestDTO;
import com.back.sousa.models.database.login.UserLoginMO;
import com.back.sousa.repositories.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;

@TransactionalService
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserLoginRepository repository;
    public void changePassword(ChangePasswordRequestDTO request, Principal connectedUser) {

        var user = (UserLoginMO) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Password incorrecta");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("As Passwords não são iguais!");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }
}
