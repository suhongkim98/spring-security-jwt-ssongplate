# spring-security-jwt-ssongplate

본 프로젝트는 `Java 11`버전을 기준으로 개발하였습니다.<br/>
`Spring Security` + `JWT`를 활용한 인증 인가 템플릿입니다.<br/>
`spring REST Docs`를 통해 API 문서화가 되어있습니다.<br/>
`flyway` DB 마이그레이션 도구를 사용합니다.

### Maven Build
```bash
./mvnw clean package
```
### Maven Run
```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments='-Dserver.port=8080' -Dspring-boot.run.profiles=local
```
### spring REST Docs 접속
```
http://localhost:8080/docs/index.html
```

### 일반 유저 등록 API
```
POST /api/v1/member
```
#### request
```
{
    username: String,
    password: String,
    nickname: String
}
```
* `ROLE_MEMBER` 권한을 부여하여 `Account` 생성
* 리프레시 토큰의 초기 가중치는 1로 설정

### 계정 인증 API
```
POST /api/v1/auth/authenticate
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

### 액세스 토큰, 리프레시 토큰 갱신 API
```
PUT /api/v1/auth/token
```
#### request
```
{
    refreshToken: String
}
```
* 리프레시 토큰을 검증(유효성, 리프레시 토큰 가중치)
* 액세스 토큰, 리프레시 토큰 발급

### 리프레시 토큰 무효화 API
호출자의 `ROLE_ADMIN` 권한 보유여부 확인
```
DELETE /api/auth/token/{username}
```
* 해당 `username`의 토큰 가중치를 1 증가시킴으로써 이전에 발급된 해당 `username`에 대한 모든 리프레시 토큰 무효화

