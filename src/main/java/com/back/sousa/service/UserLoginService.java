package com.back.sousa.service;

import com.back.sousa.exceptions.UserNotFoundException;
import com.back.sousa.helpers.messages.I18NKeys;
import com.back.sousa.helpers.messages.MessagesService;
import com.back.sousa.mappers.UserDataMapper;
import com.back.sousa.models.auth.RegisterRequest;
import com.back.sousa.models.database.login.UserLoginMO;
import com.back.sousa.repositories.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.back.sousa.models.dto.UserLogin.createNewUserByRequest;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final UserLoginRepository userRepository;
    private final MessagesService messagesService;
    private final UserDataMapper userDataMapper;

    public UserLoginMO checkIfUserExists(Integer ccNumber) {
        Optional<UserLoginMO> user = userRepository.findById(ccNumber);

        if (user.isEmpty()) {
            throw new UserNotFoundException(messagesService.getMessage(I18NKeys.UserMessage.NOT_FOUND));
        }

        return user.get();
    }

    public UserLoginMO buildUserLoginMO(RegisterRequest request) {
        return userDataMapper.mapToUserLoginMO(createNewUserByRequest(request));
    }


    public UserLoginMO createUser(UserLoginMO user) {
        return userRepository.save(user);
    }
}
