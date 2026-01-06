# Sniffy: 경찰청 유실물 조회 및 알림 서비스

## 프로젝트 소개

Sniffy는 경찰청 유실물 통합포털(LOST112)의 데이터를 기반으로, 유실물 검색기능과 웹 푸시 알림을 제공하는 서비스입니다.

- 배포 주소: https://sniffy.64bit.kr
- 테스트 계정 (ID / PW): `sniffy` / `sniffy@test`

## 개발 기간

2025.10.28 ~ 2025.12.24

## 목차

- [주요 기능](#주요-기능)
- [스크린샷](#스크린샷)
- [개발 환경](#개발-환경)
- [프로젝트 구조](#프로젝트-구조)
- [실행 방법](#실행-방법)

## 주요 기능

- 유실물 데이터 동기화 및 검색
    - 공공데이터포털 API를 활용한 유실물 데이터 주기적 수집
    - 카테고리, 날짜, 보관 장소, 색상 등 정밀 검색 필터를 통한 유실물 검색

- 내 주변 유실물 센터 찾기
    - 사용자 위치 (위도/경도) 기반 주변 유실물 센터 검색
    - 자주 찾아보는 기관 즐겨찾기 기능

- 유실물 관련 통계
    - 일간/주간/월간 유실물 등록 현황 제공
    - 가장 많이 분실되는 물품 카테고리 및 상위 유실물 습득 기관 제공

- 키워드 기반 알림
    - 사용자가 등록한 키워드(예: "휴대폰")가 포함된 유실물 등록 시 푸시 알림 발송
    - 사용자가 즐겨찾기한 기관 우선 알림 기능

## 스크린샷

<table>
  <tr>
    <td width="50%">
      <img src="https://github.com/user-attachments/assets/37f2aba8-ab94-4fb5-9e8d-fe0b404513b7" />
    </td>
    <td width="50%">
      <img src="https://github.com/user-attachments/assets/b54c1a00-51ba-4796-af18-4a4692a58444" />
    </td>
  </tr>
  <tr>
    <th>메인 화면</th>
    <th>검색 화면</th>
  </tr>
</table>

<table>
  <tr>
    <td width="50%">
      <img src="https://github.com/user-attachments/assets/b9907507-15d7-4073-b855-6d5c9f3bd402" />
    </td>
    <td width="50%">
      <img src="https://github.com/user-attachments/assets/6d5d7d57-7dc6-4b50-a346-cbf97f4bc14a" />
    </td>
  </tr>
  <tr>
    <th>위치 검색</th>
    <th>유실물 센터 선택</th>
  </tr>
</table>

<table>
  <tr>
    <td width="50%">
      <img src="https://github.com/user-attachments/assets/fdd502d7-4576-4102-ae9e-a55d13be2047" />
    </td>
    <td width="50%">
      <img src="https://github.com/user-attachments/assets/aa90dbf0-30b7-42bd-91cb-898219fdd277" />
    </td>
  </tr>
  <tr>
    <th>유실물 연관 통계</th>
    <th>유실물 등록 현황</th>
  </tr>
</table>

<table>
  <tr>
    <td width="50%">
      <img src="https://github.com/user-attachments/assets/c96a3891-b8f9-4fef-8774-d1573bee274d" />
    </td>
    <td width="50%">
      <img src="https://github.com/user-attachments/assets/1649ac26-9f63-4317-add8-8c078466e822" />
    </td>
  </tr>
  <tr>
    <th>키워드 등록</th>
    <th>푸시 알림</th>
  </tr>
</table>

## 개발 환경

- Language: Java 17
- Framework: Spring Boot, Spring Security, Spring Data JPA
- Database: MariaDB, MyBatis
- DevOps: Docker, Jenkins

## 프로젝트 구조

### 패키지 구조

```
src/main
├── java/com/salmontaker/sniffy
│   ├── admin       # 관리자 기능 (데이터 수동 동기화)
│   ├── advice      # 전역 예외 처리 및 공통 응답 포맷팅 (ControllerAdvice)
│   ├── agency      # 보관소(경찰서) 도메인 및 위치 기반 검색
│   ├── auth        # 로그인/로그아웃 및 인증 관련
│   ├── common      # 공통 DTO(Page, API Response) 및 BaseEntity
│   ├── config      # 특정 도메인에 속하지 않는 설정 (EnableScheduling)
│   ├── founditem   # 습득물 데이터 관리, 외부 API 연동 및 배치 처리
│   ├── notice      # 알림 생성 및 조회 도메인
│   ├── push        # Web Push 구독 관리 및 발송
│   ├── security    # Spring Security 설정 (FilterChain, Handler)
│   ├── stats       # 통계 데이터 집계 및 조회
│   └── user        # 사용자 관리, 키워드 등록, 환경 설정 도메인
│
└── resources
    ├── mapper                  # MyBatis XML Mapper (통계 쿼리 분리)
    │   └── StatsMapper.xml
    ├── application.properties  # 설정 파일
    └── data.sql                # 초기 데이터 (전국 보관소 및 샘플 데이터)
```

### ERD

<img src="https://github.com/user-attachments/assets/57a36f1b-f1a8-4d11-9727-b4ded9ee8bbd" />

## 실행 방법

로컬 환경에서 프로젝트를 실행하기 위한 가이드입니다.

### 1. 사전 요구 사항

- JDK 17 이상
- MariaDB
- [공공데이터포털](https://www.data.go.kr/index.do) API 키
  및 [경찰청 유실물 정보 조회 서비스](https://www.data.go.kr/data/15058696/openapi.do) 활용 신청
- VAPID 키 ([web-push](https://github.com/web-push-libs/webpush-java?tab=readme-ov-file#cli) 라이브러리등으로 생성 가능)

### 2. 환경 설정 및 실행

실행 방식에 따라 환경 변수 설정 방법이 다릅니다. 편하신 방법을 선택하세요.

#### 방법 1: Gradle로 실행하기

1. `src/main/resources/application.properties` 파일을 열어주세요.

```properties
spring.datasource.url=jdbc:mariadb://${DB_HOST}:${DB_PORT}/${DB_NAME}?characterEncoding=UTF-8&serverTimezone=Asia/Seoul&rewriteBatchedStatements=true
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
# ...
```

2. `${...}`로 되어 있는 부분을 실제 값으로 변경합니다. (예시)

```properties
spring.datasource.url=jdbc:mariadb://locahost:3306/sniffy?characterEncoding=UTF-8&serverTimezone=Asia/Seoul&rewriteBatchedStatements=true
spring.datasource.username=sniffy
spring.datasource.password=password
# ...
```

3. 터미널에서 아래 명령어를 실행합니다.

```shell
# Windows
./gradlew.bat bootRun

# Mac/Linux
./gradlew bootRun
```

#### 방법 2: Docker로 실행하기

1. 프로젝트 최상단에 `.env` 파일을 생성하여 값을 입력해주세요.

```properties
# Database
DB_HOST=[DB 호스트]
DB_PORT=[DB 포트]
DB_NAME=[DB 이름]
DB_USER=[DB 사용자]
DB_PASSWORD=[DB 비밀번호]

# External API (공공데이터포털)
DATA_GO_KR_API_KEY=[Encoding된 API Key]

# Web Push
VAPID_PUBLIC_KEY=[공개키]
VAPID_PRIVATE_KEY=[개인키]
VAPID_SUBJECT=mailto:[이메일주소]
```

2. 터미널에서 아래 명령어를 실행합니다.

```shell
# 이미지 빌드
./gradlew build -x test

# .env 파일을 주입하여 실행
docker build -t sniffy .
docker run -d -p 8080:8080 --env-file .env --name sniffy sniffy
```

### 3. 데이터 동기화

#### 1. 초기 데이터 로드

애플리케이션 실행 시 `src/main/resources/data.sql`을 통해 초기 데이터가 자동으로 로드됩니다.

- [전국 유실물 관할센터](https://www.lost112.go.kr/prevent/lostCenterList.do) 중 경찰관서 2782건
- 2026년 1월 5일자 유실물 샘플 데이터 100건

#### 2. 수동 데이터 동기화

습득물 데이터는 매일 새벽 2시에 스케줄러에 의해 자동으로 수집되지만, 관리자 api를 통해 수동 동기화를 요청할 수 있습니다.

> 공공데이터포털 API의 불안정성으로 인해 시간대에 따라 2~3분 이상 소요될 수 있습니다.

> 관리자 API는 보안을 위해 localhost에서만 호출 가능합니다.

```shell
curl -X POST http://localhost:8080/api/admin/found-items/sync
```
