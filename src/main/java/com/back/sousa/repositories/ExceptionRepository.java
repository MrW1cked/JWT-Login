package com.back.sousa.repositories;

import com.back.sousa.models.database.login.ExceptionMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface ExceptionRepository extends JpaRepository<ExceptionMO, BigInteger> {
}