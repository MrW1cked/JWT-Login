package com.back.sousa.models.domain;

import com.back.sousa.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountsPrivileges {

    private Integer ccNumber;
    private Role role;

}