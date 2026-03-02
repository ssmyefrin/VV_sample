# AI RAG Sample App

AI RAG(Retrieval-Augmented Generation)를 위한 샘플 애플리케이션입니다.  
회원가입 및 로그인 기능과 공지사항 게시판 기능을 제공합니다.

## 🛠 기술 스택 (Tech Stack)

- **Language**: Java 21
- **Framework**: Spring Boot 3.4.2
- **Build Tool**: Gradle (Groovy)
- **Architecture**: DDD (Domain-Driven Design)
- **Security**: Spring Security, JWT
- **Database**: H2 (Embedded)
- **ORM**: JPA (Spring Data JPA)
- **API Docs**: Swagger (SpringDoc OpenAPI)
- **Utils**: Lombok, MapStruct

## 📂 프로젝트 구조 (Architecture)

이 프로젝트는 **DDD (Domain-Driven Design)** 원칙에 따라 설계되었습니다.

- **Structure**
  ```
  src/main/java/com/example/sample
  ├── global    # 전역 공통 설정 (Config, Security, Exception 등)
  ├── user      # 회원 도메인 (회원가입, 로그인)
  └── notice    # 공지사항 도메인
  ```

## 🚀 실행 방법 (How to Run)

### 1. 빌드 및 실행
```bash
./gradlew clean build
./gradlew bootRun
```

### 2. 주요 접속 URL
- **Swagger UI (API 문서)**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **H2 Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:file:./data/mydb`
  - User Name: `sa`
  - Password: (비어있음)

## ✨ 주요 기능
1. **회원 (User)**
   - 회원가입
   - 로그인 (JWT 발급)
   - 내 정보 조회

2. **공지사항 (Notice)**
   - 공지사항 목록 조회
   - 공지사항 상세 조회
   - 공지사항 등록/수정/삭제 (관리자)
