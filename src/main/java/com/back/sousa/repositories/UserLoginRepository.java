package com.back.sousa.repositories;

import com.back.sousa.models.database.login.UserLoginMO;
import com.back.sousa.models.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserLoginRepository extends JpaRepository<UserLoginMO, Integer> {

    Optional<UserLoginMO> findByVerificationToken(String token);

    List<UserLoginMO> findByEmail(String email);
}
