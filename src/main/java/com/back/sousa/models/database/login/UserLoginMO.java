package com.back.sousa.models.database.login;

import com.back.sousa.models.enums.Role;
import com.back.sousa.models.database.auditable.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "USER_LOGIN")
public class UserLoginMO extends Auditable implements UserDetails {

  @Id
  @Column(name="CC_NUMBER")
  private Integer ccNumber;

  @Column(name="FIRST_NAME")
  private String firstName;

  @Column(name="LAST_NAME")
  private String lastName;

  @Column(name="EMAIL")
  private String email;

  @Column(name="PASSWORD")
  private String password;

  @Column(name="ROLE")
  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
  private List<TokenMO> tokens;

  @Column(name="MEMBER_STARTING_DATE")
  private LocalDate memberStartingDate;

  @Column(name="MEMBER_ENDING_DATE")
  private LocalDate memberEndingDate;

  @Column(name="HAS_PARISH")
  private Boolean hasParish;

  @Column(name="WAS_DISPATCHED")
  private Boolean wasDispatched;
  
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return ccNumber.toString();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
