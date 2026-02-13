# spring-security-jwt-ssongplate

본 프로젝트는 `Java 21`, `Spring Boot 4.x` 기준으로 개발하였습니다.<br/>
`Spring Security` + `JWT`를 활용한 인증 인가 템플릿입니다.<br/>
소프트웨어 아키텍처로 `레이어드 아키텍처`를 사용합니다.

![img.png](docs/img.png)

# 개발 환경 세팅 가이드
## 1. JWT 비대칭키 생성
액세스 토큰, 리프레시 토큰 별도로 JWT 비대칭키 생성이 필요합니다.
openssl 명령어를 활용해 비대칭키를 생성합니다.

만약 RSA 공개키 방식이 아닌 시크릿키 방식을 희망하는 경우 `JwtDecoder`, `JwtEncoder` 빈 생성 로직을 수정해주세요.

### openssl 명령어를 이용하여 비대칭키 생성
아래 명령어은 비대칭키를 생성하는 명령어 예시입니다.

#### 개인키 생성
```bash
openssl genrsa -out {원하는개인키파일명} 2048
```
#### 개인키를 이용해 공개키 생성
```bash
openssl rsa -in {개인키파일명} -out {원하는공개키명}.pub -pubout
```
액세스 토큰용, 리프레시 토큰용으로 비대칭키 2쌍 생성이 필요합니다.
액세스 토큰용은 `accessKey` 라는 파일명으로, 리프레시 토큰용은 `refreshKey`라는 파일명으로 생성합니다.

그리고 `resources/secret` 디렉토리 하위에 위치시킵니다.
예제는 `resources/secret-example`을 참고해주세요.

## Gradle Build
```bash
./gradlew clean bootJar
```
## Java Run
```bash
java -Dserver.port=8080 -jar build/libs/jwt-0.0.1-SNAPSHOT.jar
```
## Swagger 접속
```
http://localhost:8080/docs/swagger-ui/index.html
```
Swagger를 통해 유저 등록, 인증, 토큰 갱신, 토큰 뮤효화 등 API 스펙 확인이 가능합니다.
### openapi spec 추출
```
http://localhost:8080/docs
```
