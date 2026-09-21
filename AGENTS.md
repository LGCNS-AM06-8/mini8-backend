# AI에게 주는 규칙

AI 도구(Claude Code · Cursor · ChatGPT 등)가 이 저장소에서 일할 때 읽는 파일입니다.

## 이 프로젝트

- 주제: 경력 년수 · 보유 기술 · 관심 기술 · 희망 직무를 입력하면 기술블로그를 가진 기업을 추천하고, 글을 고르면 그 글을 내 수준에 맞게 어떻게 읽을지 AI가 안내하는 서비스
- 기간 2026-09-21 ~ 09-30. 작업일은 나흘(9/21 · 9/22 · 9/28 · 9/29). 9/23~27은 휴강과 주말, 9/30은 발표
- 스택: React · Spring Boot 3.4.5 · Java 17 · **MariaDB** · JPA(ddl-auto) · JWT
- 6명이 파트(DB·환경 / 서버·프롬프트 / 수집 / 화면)로 나눠 구현하고 `develop`에서 합칩니다

**2026-09-18에 채용 공고 기능을 전부 뺐습니다.** 공고 데이터를 허용된 경로로 받을 수 없어서입니다.
`job_posting` 같은 이름이 나오는 문서를 보면 그것은 옛 규격입니다. 따르지 마십시오.

## 규격의 원천

| 무엇 | 어디 |
|---|---|
| API 규격 | **노션 API 명세서 DB** (16행). 저장소 안에 사본을 두지 않습니다 |
| 테이블 | **`database/entity/` 의 JPA 엔티티** (DB 담당 한 사람만 고친다). 노션 ERD 페이지는 제출용 사본 |
| 화면 | **Figma `Design` 페이지** |
| 브랜치·커밋·PR | `CONTRIBUTING.md` |

규격과 다르게 만들어야 하면 코드를 먼저 고치지 말고 **디스코드 이슈 포럼에 올립니다**(`backend-issue` · `frontend-issue`).

## 손대면 안 되는 것

- `.env`: 값이 든 파일. 읽지도 쓰지도 않습니다
- 다른 파트가 맡은 폴더
- `database/entity/` 의 엔티티. DB 담당(노건우) 외에는 고치지 않습니다. 칸이 필요하면 `backend-issue` 포럼에 올립니다
- `features/*/domain/entity/` 에 엔티티를 새로 만들지 않습니다. 엔티티는 한 자리에 한 벌만 있습니다

## 누가 어디를 고치나 (소유 지도)

| 경로 | 주인 | 내용 |
|---|---|---|
| `commons/config` · `filter` · `token` · `exception` · `handler` | 박준우 | Security · CORS · Swagger · JWT · 오류 응답 |
| `database/entity/` | 노건우 | JPA 엔티티 10개. 표는 여기서만 |
| `features/user` · `tech` · `company` | 박준우 | 로그인 · 프로필 · 기술 칩 · 기업 목록 · 기업 상세 |
| `features/post` · `guide` | 류지범 | 글 원문 · 글 목록(추천) · AI 가이드 |
| `features/bookmark` | 노건우 | 북마크 3 |
| `features/collect` · `features/admin` | 신해원 | 수집 · 관리자 API |
| `src/main/resources/prompts/` | 류지범 | 가이드 프롬프트 |
| `application*.yml` · `build.gradle` · 이 문서들 | 신해원 | 설정 · 의존성. 바꿔야 하면 `backend-issue` |

각 `features/<도메인>/` 은 `ctrl · service · repository · domain/dto` 만 가집니다. Repository 의 제네릭은 `database/entity` 의 클래스를 씁니다.

## 오류 응답

`{code, message, field}` 하나입니다. 스택 트레이스를 응답에 싣지 않습니다.

## 커밋 · PR

- 커밋 메시지는 `type(scope): 설명` 형식(`CONTRIBUTING.md`)
- `main` · `develop`에 직접 push하지 않습니다
- PR 본문은 `# 수정사항` · `# 스크린샷` · `# 메모`. 서버는 Swagger 응답 화면, 화면은 캡처를 넣습니다
- AI를 썼으면 무엇을 시켰는지 `# 메모`에 한 줄 적습니다

## 실행 · 확인

```bash
cp .env.example .env      # 값 채우기
./gradlew bootRun         # http://localhost:8000/swagger-ui/index.html
./gradlew test            # MariaDB 없이 H2로 기동 확인
```

## 작업을 마칠 때

- 사람이 확인할 부분(판단이 갈린 곳 · 확신이 없는 곳)을 PR 본문에 적습니다
- 실행 확인 없이 「완료」라고 쓰지 않습니다
