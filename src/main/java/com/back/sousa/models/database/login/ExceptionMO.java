package com.back.sousa.models.database.login;

import com.back.sousa.models.database.EncryptionConverter;
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
@Table(name = "EXCEPTIONS")
public class ExceptionMO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exceptions_seq")
    @SequenceGenerator(name = "exceptions_seq", sequenceName = "exceptions_seq", allocationSize = 1)
    private BigInteger id;

    @Column(name = "REQUEST_IP")
    @Convert(converter = EncryptionConverter.class)
    private String requestIp;

    @Column(name = "EXCEPTION_NAME")
    @Convert(converter = EncryptionConverter.class)
    private String methodName;

    @Column(name = "EXCEPTION_MESSAGE")
    @Convert(converter = EncryptionConverter.class)
    private String exceptionMessage;

    @Column(name = "USER_CC_NUMBER")
    @Convert(converter = EncryptionConverter.class)
    private String userCCNumber;

    @Column(name = "REQUEST_DATE_TIME")
    private OffsetDateTime requestDateTime;

    @Column(name = "READED")
    private Boolean readed;

}
