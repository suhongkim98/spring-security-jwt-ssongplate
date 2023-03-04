/* account 스키마 */
CREATE TABLE account (
    account_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(127),
    password VARCHAR(255),
    token_weight BIGINT,
    nickname VARCHAR(127),
    activated BOOLEAN
);

/* authority 스키마 */
CREATE TABLE authority (
    authority_name VARCHAR(127) NOT NULL PRIMARY KEY
);

/* 다대다표현 account_authority 스키마 */
CREATE TABLE account_authority (
    account_id BIGINT NOT NULL,
    authority_name VARCHAR(127) NOT NULL,
    FOREIGN KEY (account_id) REFERENCES account (account_id),
    FOREIGN KEY (authority_name) REFERENCES authority (authority_name)
);

/* 초기 필수 권한 */
INSERT INTO AUTHORITY (AUTHORITY_NAME) values ('ROLE_MEMBER');
INSERT INTO AUTHORITY (AUTHORITY_NAME) values ('ROLE_ADMIN');