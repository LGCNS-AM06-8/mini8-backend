# mini8-backend

기술블로그 읽기 가이드 서버입니다. Spring Boot 3.4.5 · Java 17 · MariaDB · JPA · JWT.
구조는 수업 프로젝트(inspire_jpa)와 같습니다.

누가 어디를 고치는지는 `AGENTS.md` 「소유 지도」에 있습니다.

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
문자셋을 지정하지 않으면 글 제목의 한글과 이모지가 깨집니다. 표는 서버가 뜰 때 `database/entity` 의 엔티티에서 만들어집니다(`ddl-auto: update`).

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
├─ commons/            공통 (박준우)
│  ├─ config/          SecurityConfig · SwaggerConfig
│  ├─ filter/          요청 헤더 토큰을 사용자로
│  ├─ token/           JWT 발급 · 검증
│  ├─ exception/       ErrorCode · BusinessException
│  └─ handler/         전역 예외 처리 · 오류 응답
├─ database/entity/    JPA 엔티티 10개 (노건우). 표의 원천, 한 자리에 한 벌
└─ features/{도메인}/
   ├─ ctrl/            컨트롤러 (경로와 상태코드만, 로직 없음)
   ├─ service/         업무 로직
   ├─ repository/      JPA (제네릭은 database/entity 의 클래스)
   └─ domain/dto/      요청 · 응답 DTO
```

도메인은 일곱입니다. `user` · `tech` · `company` · `post` · `guide` · `bookmark` · `collect`.

## 규약

- 정상 응답은 봉투 없이 DTO 그대로 내립니다. 오류만 `{code, message, field}` 입니다
- 토큰은 응답 헤더 `Authorization: Bearer …` 와 `Refresh-Token` 으로 보냅니다. 프론트는 `Authorization` 헤더로 보냅니다
- 컨트롤러에서 사용자는 `@AuthenticationPrincipal Long userId` 로 받습니다
- 표를 바꿀 때는 `database/entity` 의 엔티티를 고칩니다 (DB 담당). SQL 파일은 쓰지 않습니다
- 이름은 Java camelCase · 클래스 PascalCase · DB snake_case · JSON camelCase · URL 소문자 복수형

## 아직 없는 것

작업 목록과 순서는 디스코드 `backend-task` · `db-task` · `crawling-task` 포럼에 있습니다. 설계 근거는 노션 ERD와 API 명세서에 있습니다.
