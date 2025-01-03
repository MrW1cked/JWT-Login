package com.back.sousa.models.database.login;

import com.back.sousa.models.database.auditable.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LOGIN_ATTEMPTS")
public class LoginAttemptsMO extends Auditable {

    @Id
    private Integer ccNumber;

    private Integer attempts;
    private Timestamp lastAttempt;
    private Boolean banned;

}
