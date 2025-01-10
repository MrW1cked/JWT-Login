package com.back.sousa.repositories;

import com.back.sousa.models.database.login.LoginAttemptsMO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginAttemptsRepository extends JpaRepository<LoginAttemptsMO, String> {

}