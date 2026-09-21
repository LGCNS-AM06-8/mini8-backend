# mini8-backend

기술블로그 읽기 가이드 서버입니다. Spring Boot 3.4.5 · Java 17 · MariaDB · Flyway · JWT.
구조는 수업 프로젝트(inspire_jpa)와 같습니다.

지금은 빈 뼈대입니다. 폴더와 빌드 설정만 있고 코드는 없습니다. 담당이 정해지면 각자 자기 폴더를 채우시면 됩니다.

## 시작하기

```
git clone https://github.com/LGCNS-AM06-8/mini8-backend.git
cd mini8-backend
git checkout develop

cp .env.example .env                    # 값 채우기 (DB · JWT_SECRET · GOOGLE_CLIENT_ID · GEMINI_API_KEY)
git config core.hooksPath .githooks     # 처음 한 번만. 커밋 검사 켜기

.\gradlew bootRun                       # http://localhost:8000/swagger-ui/index.html
.\gradlew test                          # MariaDB 없이 H2로 기동 확인
```

Windows PowerShell 은 `./gradlew` 가 아니라 `.\gradlew` 입니다.
JDK 는 17 이어야 합니다. `build.gradle` toolchain 이 17 고정이라 21 단독 설치 시 빌드가 실패합니다.

MariaDB 에 `mini8` 데이터베이스를 만들어 두시면 됩니다.
`CREATE DATABASE mini8 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
문자셋을 지정하지 않으면 글 제목의 한글과 이모지가 깨집니다. 표는 `db/mariadb/V1__init.sql` 이 들어오면 Flyway가 기동할 때 만듭니다. 지금은 SQL이 없어서 표가 안 만들어집니다.

## 커밋할 때 자동으로 도는 검사

`git config core.hooksPath .githooks` 를 한 번 쳐 두면 커밋할 때마다 두 가지를 봅니다.

| 언제 | 무엇 | 막히면 |
|---|---|---|
| 커밋 직전 | 코드 모양이 구글 표준과 같은지 | `./gradlew spotlessApply` 로 고치고 다시 커밋 |
| 메시지 작성 직후 | `feat(auth): 설명` 형식인지 | 형식에 맞게 다시 쓰기 |

코드 모양은 손으로 맞추지 않아도 됩니다. `./gradlew spotlessApply` 가 전부 정렬합니다. 프론트의 prettier와 같은 역할입니다.

## 폴더 구조

```
com.mini8.backend
├─ commons/            공통
│  ├─ config/          SecurityConfig · SwaggerConfig
│  ├─ filter/          요청 헤더 토큰을 사용자로
│  ├─ token/           JWT 발급 · 검증
│  ├─ exception/       ErrorCode · BusinessException
│  └─ handler/         전역 예외 처리 · 오류 응답
└─ features/{도메인}/
   ├─ ctrl/            컨트롤러 (경로와 상태코드만, 로직 없음)
   ├─ service/         업무 로직
   ├─ repository/      JPA
   └─ domain/          entity · dto
```

도메인은 여섯입니다. `user` · `tech` · `company` · `post` · `guide` · `bookmark`.

## 규약

- 정상 응답은 봉투 없이 DTO 그대로 내립니다. 오류만 `{code, message, field}` 입니다
- 토큰은 응답 헤더 `Authorization: Bearer …` 와 `Refresh-Token` 으로 보냅니다. 프론트는 `Authorization` 헤더로 보냅니다
- 컨트롤러에서 사용자는 `@AuthenticationPrincipal Long userId` 로 받습니다
- 표를 바꿀 때는 엔티티가 아니라 `db/mariadb/V{n}__*.sql` 을 추가합니다 (`ddl-auto: none`)
- 이름은 Java camelCase · 클래스 PascalCase · DB snake_case · JSON camelCase · URL 소문자 복수형

## 아직 없는 것

담당이 정해진 뒤에 만듭니다.

| 무엇 | 어디에 |
|---|---|
| 표를 만드는 SQL | `db/mariadb/V1__init.sql` |
| 인증 설정과 JWT | `commons/` |
| 도메인 여섯의 컨트롤러 · 서비스 · 리포지토리 · 엔티티 · DTO | `features/{도메인}/` |

스키마 초안은 디스코드 `db-task` 포럼의 `D1  V1 스키마` 게시물에 첨부돼 있습니다.
커밋 `35163f9` 에도 초안이 남아 있으나 **구버전입니다.** 그쪽을 쓰면 나중에 Flyway 체크섬이 어긋납니다.
엔티티·DTO는 `35163f9` 를 참고용으로 볼 수 있습니다. 스키마는 반드시 D1 첨부본을 쓰십시오.

설계 근거는 노션 ERD와 API 명세서에 있습니다.
수집 데이터 수치(기업 8곳 · 402편 · 섹션 4,514개)는 디스코드 작업 포럼 게시물에 필요한 값만 적어 두었습니다.
