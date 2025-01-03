--************************************************************
-- Version: 1
CREATE TABLE USER_LOGIN (
                            CC_NUMBER INT PRIMARY KEY,
                            FIRST_NAME VARCHAR(255),
                            LAST_NAME VARCHAR(255),
                            EMAIL VARCHAR(255),
                            PASSWORD VARCHAR(255),
                            ROLE VARCHAR(255),
                            CREATED_DATE DATE,
                            CREATED_USER VARCHAR(255),
                            UPDATED_DATE DATE,
                            UPDATED_USER VARCHAR(255));

CREATE TABLE TOKEN (
                       id INT PRIMARY KEY,
                       token VARCHAR(255) UNIQUE,
                       tokenType VARCHAR(50),
                       revoked BOOLEAN,
                       expired BOOLEAN,
                       CC_NUMBER INT,
                       CREATED_DATE DATE,
                       CREATED_USER VARCHAR(255),
                       UPDATED_DATE DATE,
                       UPDATED_USER VARCHAR(255),
                       FOREIGN KEY (CC_NUMBER) REFERENCES USER_LOGIN(CC_NUMBER));

CREATE SEQUENCE IF NOT EXISTS token_seq
    INCREMENT 1
        START 1
        MINVALUE 1
        MAXVALUE 9223372036854775807
        CACHE 1;

ALTER TABLE USER_LOGIN
    ADD COLUMN MEMBER_STARTING_DATE DATE;
ALTER TABLE USER_LOGIN
    ADD COLUMN MEMBER_ENDING_DATE DATE;
ALTER TABLE USER_LOGIN
    ADD COLUMN HAS_PARISH BOOLEAN;
ALTER TABLE USER_LOGIN
    ADD COLUMN WAS_DISPATCHED BOOLEAN;

CREATE TABLE LOGIN_ATTEMPTS (
    CC_NUMBER INT,
    ATTEMPTS INT,
    LAST_ATTEMPT DATE,
    CREATED_DATE DATE,
    CREATED_USER VARCHAR(255),
    BANNED BOOLEAN NULL,
    UPDATED_DATE DATE,
    UPDATED_USER VARCHAR(255),
    PRIMARY KEY (CC_NUMBER),
    FOREIGN KEY (CC_NUMBER) REFERENCES USER_LOGIN(CC_NUMBER));

ALTER TABLE login_attempts ALTER COLUMN last_attempt TYPE timestamp USING last_attempt::timestamp;

ALTER TABLE USER_LOGIN ADD TERMS_READED BOOLEAN NULL;
