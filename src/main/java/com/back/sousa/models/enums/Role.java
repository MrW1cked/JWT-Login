package com.back.sousa.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.back.sousa.models.enums.Permissions.*;

@Getter
@RequiredArgsConstructor
public enum Role {

  PATIENT(
          Set.of(
                  PATIENT_READ,
                  PATIENT_UPDATE
  )),
  ADMIN(
          Set.of(
                  ADMIN_READ,
                  ADMIN_UPDATE,
                  ADMIN_DELETE,
                  ADMIN_CREATE
          )
  ),
  MEDIC(
          Set.of(
                  MANAGER_READ,
                  MANAGER_UPDATE,
                  MANAGER_DELETE,
                  MANAGER_CREATE
          )
  ),
  NURSE(
          Set.of(
                  MANAGER_READ,
                  MANAGER_UPDATE
          )
  )

  ;

  private final Set<Permissions> permissions;

  public List<SimpleGrantedAuthority> getAuthorities() {
    var authorities = getPermissions()
            .stream()
            .map(permissions -> new SimpleGrantedAuthority(permissions.getPermission()))
            .collect(Collectors.toList());
//    authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
    authorities.add(new SimpleGrantedAuthority(this.name()));
    return authorities;
  }
}
