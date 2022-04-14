/*
 임베디드 h2가 생성될 때마다 초기 데이터 생성
*/

INSERT INTO ACCOUNT (ACCOUNT_ID, USERNAME, PASSWORD, NICKNAME, ACTIVATED, TOKEN_WEIGHT)
VALUES (1, 'admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'admin', 1, 1);

INSERT INTO ACCOUNT (ACCOUNT_ID, USERNAME, PASSWORD, NICKNAME, ACTIVATED, TOKEN_WEIGHT)
VALUES (2, 'user', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'user', 1, 1);

INSERT INTO AUTHORITY (AUTHORITY_NAME) values ('ROLE_USER');
INSERT INTO AUTHORITY (AUTHORITY_NAME) values ('ROLE_ADMIN');

INSERT INTO ACCOUNT_AUTHORITY (ACCOUNT_ID, AUTHORITY_NAME) values (1, 'ROLE_USER');
INSERT INTO ACCOUNT_AUTHORITY (ACCOUNT_ID, AUTHORITY_NAME) values (1, 'ROLE_ADMIN');
INSERT INTO ACCOUNT_AUTHORITY (ACCOUNT_ID, AUTHORITY_NAME) values (2, 'ROLE_USER');