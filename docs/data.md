# 테이블 · ERD

**이 파일은 규격이 아닙니다.**

| 무엇 | 어디 |
|---|---|
| 실제 스키마 (원천) | `src/main/java/com/mini8/backend/database/entity/` 의 JPA 엔티티. 서버가 뜰 때 `ddl-auto: update` 로 표를 만든다 |
| 표 · 칸 이름 | 노션 API 명세서의 칸 이름 기준. 표 이름은 소문자 snake_case |
| ERD · 테이블 명세서 (제출용) | 노션 산출물 체크리스트 5번. 엔티티에서 옮겨 적는다 |

엔티티는 DB 담당 한 사람이 고칩니다. 다른 파트는 칸이 필요하면 `backend-issue` 포럼에 올립니다.
SQL 마이그레이션 파일(Flyway)은 쓰지 않습니다 (09-21 결정).
