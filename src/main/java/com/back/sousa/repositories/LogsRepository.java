package com.back.sousa.repositories;

import com.back.sousa.models.database.login.LogsMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface LogsRepository extends JpaRepository<LogsMO, BigInteger> {
}