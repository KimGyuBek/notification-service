# notification-service
### Threadly 플랫폼의 백엔드 서비스입니다.

**플랫폼은 MSA 구조로 구성되어 있으며,**

`notification-service`는 팔로우, 댓글, 좋아요 등 사용자 활동에 대한 

**실시간 알림을 처리하는 알림 서비스 입니다.**

<br>

> 전체 서비스 아키텍쳐 및 시스템 구성은 메인 레포에서 확인할 수 있습니다.


### 메인 레포: https://github.com/KimGyuBek/Threadly
### Wiki 문서: https://github.com/KimGyuBek/Threadly/wiki

### threadly-service(API): https://github.com/KimGyuBek/threadly-service

<br>

### Threadly 서비스: https://threadly.kr

---

## 시스템 구성도
![architecture](docs/images/msa_architecture.png)

---

## 모듈 구조

```
notification-service/
├── notification-core/              # 비즈니스 로직 계층
│   ├── core-domain/               # 도메인 엔티티 및 비즈니스 규칙
│   ├── core-service/              # 비즈니스 로직 구현 (UseCase)
│   └── core-port/                 # 어댑터 인터페이스 정의 (Port)
│
├── notification-adapters/          # 인프라 계층
│   ├── adapter-persistence/       # 데이터베이스 영속성 (MongoDB)
│   ├── adapter-redis/             # 캐시 및 세션 관리
│   ├── adapter-kafka/             # 메시지 큐 이벤트 처리
│   ├── adapter-smtp/              # 이메일 전송 (SMTP)
│   └── adapter-websocket/         # 실시간 웹소켓 통신
│
├── notification-apps/              # 애플리케이션 계층
│   └── app-api/                   # REST API 서버
│
└── notification-commons/           # 공통 유틸리티 및 공유 컴포넌트
```

---

## 기술 스택

**Backend Framework**
- Java 17
- Spring Boot 3.3.3
- Spring Cloud 2023.0.3

**Database & Cache**
- MongoDB
- Redis

**Message Queue**
- Kafka (Spring Cloud Stream)

**Communication**
- WebSocket (Spring WebSocket)
- SMTP (Spring Mail)

**Security & Auth**
- Spring Security
- JWT (0.11.5)

**API & Documentation**
- Spring Web
- SpringDoc OpenAPI 2.6.0
- Spring REST Docs

**Utilities**
- Lombok
- MapStruct 1.5.3
- Guava 31.1
- Apache Commons (Lang3, Collections4)
- Jackson 2.17.1

**Testing**
- JUnit 5
- Mockito 5.1.1
- AssertJ 3.25.1

**Monitoring**
- Spring Actuator
- Micrometer + Prometheus

**Build**
- Gradle (Kotlin DSL)

