# spring-security-jwt-ssongplate

Spring Security + JWT를 활용한 인증 인가 템플릿입니다.


### 유저 회원가입 API
```
POST /api/user/signup
```
#### request
```
{
    username: String,
    password: String,
    nickname: String
}
```
* `ROLE_USER` 권한을 부여하여 `Account` 생성
* 리프레시 토큰의 초기 가중치는 1로 설정

### 계정 인증 API
```
POST /api/authenticate
```
#### request
```
{
    username: String,
    password: String
}
```
* `username`과 `password`를 통해 계정 인증
* 해당 사용자 권한으로 액세스토큰 리프레시 토큰 반환

### 액세스 토큰 갱신 API
```
POST /api/token/refresh
```
#### request
```
{
    token: String
}
```
* 리프레시 토큰을 검증(유효성, 리프레시 토큰 가중치)
* 액세스 토큰 발급

### 리프레시 토큰 무효화 API
호출자의 `ROLE_ADMIN` 권한 보유여부 확인
```
DELETE /api/token/refresh/{username}
```
* 해당 `username`의 토큰 가중치를 1 증가시킴으로써 이전에 발급된 해당 `username`에 대한 모든 리프레시 토큰 무효화

