package com.back.sousa.service;

import com.back.sousa.exceptions.UserNotFoundException;
import com.back.sousa.helpers.messages.I18NKeys;
import com.back.sousa.helpers.messages.MessagesService;
import com.back.sousa.models.auth.RegisterRequest;
import com.back.sousa.models.database.login.UserLoginMO;
import com.back.sousa.models.dto.UserLogin;
import com.back.sousa.models.enums.Role;
import com.back.sousa.repositories.UserLoginRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final UserLoginRepository userRepository;
    private final MessagesService messagesService;
    private final PasswordEncoder passwordEncoder;

    public UserLoginMO checkIfUserExists(Integer ccNumber) {
        Optional<UserLoginMO> user = userRepository.findById(ccNumber);

        if (user.isEmpty()) {
            throw new UserNotFoundException(messagesService.getMessage(I18NKeys.UserMessage.NOT_FOUND));
        }

        return user.get();
    }

    public UserLoginMO buildUserLoginMO(RegisterRequest request) {
        return createNewUserByRequest(request);
    }


    public UserLoginMO createUser(UserLoginMO user) {
        return userRepository.save(user);
    }


    public UserLoginMO createNewUserByRequest(RegisterRequest request){
        return UserLoginMO.builder()
                .ccNumber(request.getCcNumber())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.PATIENT)
                .hasParish(false)
                .wasDispatched(false)
                .memberStartingDate(LocalDate.now())
                .memberEndingDate(null)
                .build();
    }

    public void generateNewToken(@NonNull UserLoginMO user) {
        user.setVerificationToken(java.util.UUID.randomUUID().toString());
        user.setVerificationTokenExpiration(java.time.LocalDateTime.now().plusHours(24));
        userRepository.save(user);
    }

    public Integer getLoggedOnUserCCNumber() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        return Integer.valueOf(userDetails.getUsername());
    }
}
