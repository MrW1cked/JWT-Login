package com.back.sousa.models.database.login;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LOGS")
public class LogsMO{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "logs_seq")
    @SequenceGenerator(name = "logs_seq", sequenceName = "logs_seq", allocationSize = 1)
    private BigInteger id;

    @Column(name = "REQUEST_IP")
    private String requestIp;

    @Column(name = "METHOD_NAME")
    private String methodName;

    @Column(name = "USER_CC_NUMBER")
    private Integer userCCNumber;

    @Column(name = "REQUEST_DATE_TIME")
    private OffsetDateTime requestDateTime;

}
