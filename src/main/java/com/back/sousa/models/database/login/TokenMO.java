package com.back.sousa.models.database.login;

import com.back.sousa.models.database.auditable.Auditable;
import com.back.sousa.models.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TOKEN")
public class TokenMO extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_seq")
  @SequenceGenerator(name = "token_seq", sequenceName = "TOKEN_SEQ", allocationSize = 1)
  private BigInteger id;

  @Column(unique = true)
  private String token;

  @Enumerated(EnumType.STRING)
  @Column(name = "tokentype")
  private TokenType tokenType = TokenType.BEARER;

  private boolean revoked;

  private boolean expired;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "CC_NUMBER")
  private UserLoginMO user;
}
