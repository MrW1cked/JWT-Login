package com.back.sousa.repositories;

import com.back.sousa.models.database.login.TokenMO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenMO, BigInteger> {

  @Query(value = """
      select t from TokenMO t inner join UserLoginMO u\s
      on t.user.ccNumber = u.ccNumber\s
      where u.ccNumber = :id and (t.expired = false or t.revoked = false)\s
      """)
  List<TokenMO> findAllValidTokenByUser(Integer id);

  Optional<TokenMO> findByToken(String token);
}
