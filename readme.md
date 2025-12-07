# notification-service

>  Threadly 백엔드의 **알림 전용 서비스**입니다.

### 알림 서비스는 다음 기능을 담당합니다.
- 게시글 좋아요, 댓글, 팔로우 등 사용자 활동에 대한 실시간 알림 생성
- 회원가입 시 인증 메일, 가입 완료 후 환영 메일 등 이메일 발송
- 메인 서비스(`threadly-service`)에서 발행한 도메인 이벤트를 `Kafka` 기반 비동기 메시지로 수신해 알림을 저장, 전달
- 알림 조회, 읽음 처리 등 `/api/notifications/*` REST API 요청 처리

## 관련 레포 및 서비스

> 전체 아키텍처, 운영 구조, 트러블슈팅 기록은 Threadly 메인 레포 및 Wiki에서 확인할 수 있습니다.

- **Threadly 메인 레포**: https://github.com/KimGyuBek/Threadly
- **Wiki 문서**: https://github.com/KimGyuBek/Threadly/wiki
- **메인 서비스**: https://github.com/KimGyuBek/threadly-service
- **Threadly 서비스**: https://threadly.kr
- **Threadly API**: https://api.threadly.kr


## 백엔드 시스템 구성도

![architecture](docs/images/msa.png)


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


## 사용 기술 스택

### 백엔드

`Java 17` `Spring Boot 3.3.3` `Spring Security` `Spring Data MongoDB`  `Spring Cloud Stream`

### DB / 캐시

`MongoDB` `Redis`

### 인프라 / 메시징

`Kafka` `Docker` `AWS EC2`  `GitHub Actions`  `Grafana`

### 통신
`SMTP` `WebSocket`

### 테스트 및 품질

`JUnit 5` `k6` `JaCoCo` `Mockito`
