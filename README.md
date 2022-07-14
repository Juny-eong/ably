# ably-assignment

# 

## 0. 개발 환경


- JAVA 17
- Spring Boot v2.7.2
- Gradle v7.4.1
- DBMS: H2 v2.1.210

#

## 1. DDL / ERD


### - **엔티티 관계 다이어그램**

![12345](https://user-images.githubusercontent.com/58846192/179011900-c297173a-c04e-49bb-a04c-8f7f13c93c1c.png)

#

### - **TABLE DDL**

```sql
create table USERS
(
    ID           BIGINT auto_increment primary key,
    CREATED_AT   TIMESTAMP,
    MODIFIED_AT  TIMESTAMP,
    EMAIL        CHARACTER VARYING(255)
        constraint UK_USERS_EMAIL unique,
    GENDER       CHARACTER VARYING(255),
    NAME         CHARACTER VARYING(255) not null,
    NICKNAME     CHARACTER VARYING(255),
    PASSWORD     CHARACTER VARYING(255) not null,
    PHONE_NUMBER CHARACTER VARYING(255)
        constraint UK_USERS_PHONE_NUMBER unique
);

create table VERIFICATION
(
    ID           BIGINT auto_increment primary key,
    CODE         INTEGER not null,
    CREATED_AT   TIMESTAMP,
    PHONE_NUMBER CHARACTER VARYING(255)
);

--------------------------- INDEX ---------------------------

create index IDX_USERS_EMAIL on USERS (EMAIL);

create index IDX_USERS_PHONE_NO on USERS (PHONE_NUMBER);

create index IDX_VERIFICATION_PHONE_NO on VERIFICATION (PHONE_NUMBER);
```

#

## 2. 애플리케이션 수행 및 부가 설명

```
1. IDE 로 project open -> run application

2. http client(ex. postman) 로 테스트 해주시면 됩니다.

-- in-momory h2 db 와 함께 [localhost:8080](http://localhost:8080) 에서 애플리케이션이 실행됩니다.
```

#

- **개인정보 암호화 적용**
    - 이메일, 전화번호 저장 시 암호화, 조회 시 복호화 기능 적용
        - SEED 암호화 알고리즘 적용
            - [https://seed.kisa.or.kr/kisa/algorithm/EgovSeedInfo.do](https://seed.kisa.or.kr/kisa/algorithm/EgovSeedInfo.do)
    - 비밀번호 저장 시 단방향 해시 알고리즘 암호화
        - DelegatingPasswordEncoder, default = bcrypt
        
#

    
- **다중 식별자 로그인 구현**
    - 이메일 외에 전화번호로 로그인 시도 시, 별도의 인증 로직 수행
    - spring security 인증객체(Authentication) 및 인증제공자(AuthenticationProvider) 추가 구현
        - [`PhoneNumberAuthenticationProvider`](https://github.com/Juny-eong/ably/blob/master/src/main/java/com/ably/assignment/global/config/security/authentication/PhoneNumberAuthenticationProvider.java#L17)
        - [`PhoneNumberPasswordAuthenticationToken`](https://github.com/Juny-eong/ably/blob/master/src/main/java/com/ably/assignment/global/config/security/authentication/PhoneNumberPasswordAuthenticationToken.java#L14)
        - [`SecurityConfig`](https://github.com/Juny-eong/ably/blob/master/src/main/java/com/ably/assignment/global/config/security/SecurityConfig.java#L70)


#

- **JWT 를 이용한 인증방식 구현**
    - Security filter chain 내에 jwt 필터를 구현해서 token claim parsing 후 Authentication 구성
    - 토큰 만료 기한은 발급 시간으로부터 30분으로 설정
   
#
   
- **UNIT / INTEGRATION TEST 작성**
    - ![image](https://user-images.githubusercontent.com/58846192/179015329-85e3b296-6518-4f8c-a26b-088cca200a85.png)
#

## 3. API 명세 및 테스트 예시

아래의 엔드포인트와 HTTP 메서드, 요청 본문으로 테스트 가능

#

- api 명세 간단 요약

| Method | Request URI | Description |
| --- | --- | --- |
| POST | /verification/code | 핸드폰 번호로 본인인증코드 발급 |
| POST | /users/sign-up | 인증코드를 기반으로 회원가입 및 유저 생성 |
| POST | /login | 식별자와 비밀번호로 로그인 후 jwt 발급 |
| GET | /users | jwt 로 본인 인증 및 개인 정보 조회 |
| PATCH | /users/password | 본인인증코드로 비밀번호 초기화 및  새로 등록 |

#

### **1) 본인인증코드 발급 API**

핸드폰 번호로 본인인증코드를 발급받는다.

- 코드는 10분동안 유효하며, 기간 내에 인증에 사용된 적이 없다면 동일한 코드를 반환한다.
- 코드가 인증에 한번이라도 사용되었거나 만료된 경우에는 새로운 코드를 발급한다.

```json
POST localhost:8080/verification/code?phone-number=01012345678

----------------------- ↓ response ↓ -----------------------

{
    "message": "[Web발신] [에이블리] 인증번호[824852]를 입력해 주세요."
}
```

#

### **2) 회원가입 API**

발급받은 인증코드를 포함한 개인정보를 입력해 유저를 생성한다.

- nickname 외에 나머지는 모두 필수값
- 유효하지 않은 인증코드를 사용하는 경우 회원가입 실패
    - 유효하지 않은 경우: 기간이 만료되거나 이미 인증에 사용된 경우

```json
POST localhost:8080/users/sign-up

{
    "email": "adipy1470@naver.com",
    "password": "qwer1234!",
    "name": "jyl",
    "phoneNumber": "01012345678",
    "verificationCode": 824852,
    "gender": "male"
}

----------------------- ↓ response ↓ -----------------------

{
    "status": 200,
    "message": "create user success",
    "data": {
        "email": "adipy1470@naver.com",
        "name": "jyl",
        "nickname": null,
        "phoneNumber": "01012345678",
        "gender": "male",
        "createdAt": "2022-07-14 22:16:44",
        "modifiedAt": "2022-07-14 22:16:44"
    }
}
```

#

### **3) 로그인 API**

식별자와 비밀번호로 로그인을 한다.

- 식별자 : 이메일 주소 또는 핸드폰 번호 모두 로그인 가능

```json
POST localhost:8080/login

{
    "identifier": "adipy1470@naver.com",
    "password": "qwer1234!"
}

// 또는

{
    "identifier": "01012345678",
    "password": "qwer1234!"
}

----------------------- ↓ response ↓ -----------------------

{
    "status": 200,
    "message": "login success",
    "data": {
        "tokenType": "Bearer",
        "token": "eyJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJOSSt0dGtCTFJaQlNMRExGaE5jMjJRPT0iLCJzdWIiOiJLVmFUU3pFcEtCVlQ4U0svT1RsVWduakltNTlIY0xtTGJJdCs1R2xPSDFZPSIsImlhdCI6MTY1NzgwNDYyMSwiZXhwIjoxNjU3ODA2NDIxfQ.YXS0N3sJBLSwvZjePlQOzsQJaK3OM4h7Xp971vIIIk6Yy1RCiVglvMqyBL7NDQN7cME2YAKtAFdcyO7Rh6igPw"
    }
}
```

#

### 4**) 회원정보 조회 API**

로그인 시 발급받은 jwt로 회원 정보를 조회한다.

http 요청 헤더에 `Authorization: Bearer {JWT}` 형식으로 입력

```json
GET  localhost:8080/users

// header
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJOSSt0dGtCTFJaQlNMRExGaE5jMjJRPT0iLCJzdWIiOiJLVmFUU3pFcEtCVlQ4U0svT1RsVWduakltNTlIY0xtTGJJdCs1R2xPSDFZPSIsImlhdCI6MTY1NzgwNDYyMSwiZXhwIjoxNjU3ODA2NDIxfQ.YXS0N3sJBLSwvZjePlQOzsQJaK3OM4h7Xp971vIIIk6Yy1RCiVglvMqyBL7NDQN7cME2YAKtAFdcyO7Rh6igPw

----------------------- ↓ response ↓ -----------------------

{
    "status": 200,
    "message": "get user success",
    "data": {
        "email": "adipy1470@naver.com",
        "name": "jyl",
        "nickname": null,
        "phoneNumber": "01012345678",
        "gender": "male",
        "createdAt": "2022-07-14 22:16:44",
        "modifiedAt": "2022-07-14 22:16:44"
    }
}
```

#

### **5) 비밀번호 재설정 API**

본인 인증코드로 인증 후 비밀번호를 재설정 한다.

- 위의 인증코드 발급 api 로 코드를 재발급 받은 뒤, 아래 요청의 verificationCode에 입력

```json
// POST localhost:8080/verification/code?phone-number=01012345678 -> 311628

PATCH  localhost:8080/users/password

{
    "email": "adipy1470@naver.com",
    "password": "qwer1234@@",
    "verificationCode": 311628
}

----------------------- ↓ response ↓ -----------------------

{
    "status": 200,
    "message": "reset password success",
    "data": null
}
```
