package com.back.sousa.mappers;

import com.back.sousa.models.database.login.UserLoginMO;
import com.back.sousa.models.dto.UserLogin;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDataMapper {

    default UserLoginMO mapToUserLoginMO(UserLogin source) {
        return UserLoginMO.builder()
                .ccNumber(source.getCcNumber())
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .email(source.getEmail())
                .password(source.getPassword())
                .role(source.getRole())
                .memberStartingDate(source.getMemberStartingDate())
                .memberEndingDate(source.getMemberEndingDate())
                .hasParish(source.getHasParish())
                .wasDispatched(source.getWasDispatched())
                .build();
    }
}
