# 같이 일하는 규칙

노션 8조 페이지의 「커밋 및 PR, 브랜치 사용 규칙(GitHub)」(매니저님 배포)을 따릅니다. 여기에는 매일 쓰는 부분만 옮겼습니다. 둘이 다르면 노션 원문을 따릅니다.

## 브랜치

```
main      최종 제출 · 발표용 안정 버전
develop   기능이 모이는 통합 브랜치 (기본 브랜치)
feat/*    기능 하나당 브랜치 하나. 예) feat/login, feat/job-list
fix/* · docs/* · style/* · refactor/* · chore/*
```

- `main` · `develop`에는 직접 push하지 않습니다. 저장소 설정으로 막혀 있습니다
- 작업 시작 전에 항상 `develop`을 최신으로 받습니다

```bash
git checkout develop
git pull origin develop
git checkout -b feat/login
```

## 커밋 메시지

```
<type>(<scope>): <설명>
```

- type: `feat` · `fix` · `docs` · `style` · `refactor` · `chore` · `test` (소문자, 콜론 뒤 한 칸, 끝에 마침표 없음)
- 다른 사람 코드가 깨지는 변경은 콜론 앞에 `!`. 예) `refactor(auth)!: 토큰 전달 방식 변경`
- 임시 저장은 `wip: ...`. **PR 보내기 전에 하나로 합칩니다**
- 분류가 애매하면 `chore`

## PR

- `feat/*` → `develop`으로 보냅니다
- 제목은 커밋 메시지 형식과 같습니다
- 본문은 아래 「PR 본문 쓰는 법」을 따릅니다
- 승인 1명 이상이어야 병합됩니다(저장소 설정)
- 병합되면 브랜치는 자동으로 지워집니다

## PR 본문 쓰는 법

프론트 PR #1 형식을 팀 표준으로 씁니다. 절은 셋이고 스크린샷이 없으면 그 절은 지웁니다.

```
# 수정사항
- 변경 한 덩어리당 한 줄

# 스크린샷
이미지. 백엔드는 Swagger 화면

# 메모
- 확인이 필요한 것, 다음으로 넘기는 것
```

쓸 때 지킬 것입니다.

| | 이렇게 | 이러지 말고 |
|---|---|---|
| 명사형으로 끝냅니다 | `공통 의존성 추가` | `추가했습니다` · `추가함` |
| 불릿 하나에 변경 한 덩어리를 담습니다 | `husky, PR 템플릿 추가` | 파일마다 한 줄씩 |
| 불릿은 한 줄로 끝냅니다 | | 줄바꿈해서 여러 줄 |
| 바꾼 것은 화살표로 씁니다 | `npm → yarn 전환` | `npm을 yarn으로 바꿈` |
| 같은 성격은 슬래시로 나열합니다 | `eslint / prettier / stylelint` | `eslint, prettier, stylelint` |
| 근거는 「기준」으로 씁니다 | `Figma variables 기준 정리` | `피그마 보고 정리` |

이모지와 높임말은 쓰지 않습니다. 부탁할 게 있으면 `(확인 요망)` 으로 씁니다.

백엔드 PR 예시입니다.

```
feat: 팀 공통 백엔드 뼈대 세팅

# 수정사항
- Spring Boot 3.4.5 / Java 17 기준 Gradle 세팅, springdoc / JWT / Flyway / MariaDB 의존성 추가
- features/{도메인}/ctrl · service · repository · domain 폴더 구조 구성
- commons 하위에 config / exception / filter / handler / token 배치
- Spotless(google-java-format) 도입, Git hook으로 pre-commit 포맷 검사 · commit-msg 규칙 검사

# 스크린샷
(Swagger UI)

# 메모
- 표를 만드는 SQL은 DB 담당이 따로 올립니다
```

| 정할 것 | 값 |
|---|---|
| 리뷰어 | `[9/18 회의에서 정한다]` |
| 리뷰 대기 시간 | `[9/18 회의에서 정한다]` |
| wip 정리를 「Squash and merge」로 대신해도 되는지 | `[매니저님께 확인]` |

## 충돌이 났을 때

혼자 지우지 않고, 그 파일을 고친 사람과 같이 봅니다.

```bash
git checkout develop && git pull origin develop
git checkout feat/login
git merge develop
# 정리 후
git commit -m "fix(merge): develop 충돌 해결"
```

## 규격을 바꿔야 할 때

`docs/api.md` · `docs/data.md`는 혼자 고치지 않습니다. 영향받는 담당과 먼저 이야기하고, 합의한 뒤 파일을 고쳐 PR로 올립니다.

## 올리면 안 되는 것

- `.env`: 값이 든 파일. `.gitignore`가 막고 있지만 강제로 올리지 않습니다
- API 키 · 비밀번호 · 토큰을 코드에 직접 쓰지 않습니다
- 이 저장소는 **공개**입니다. 올린 것은 누구나 봅니다
