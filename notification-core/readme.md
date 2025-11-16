# notification-core

비즈니스 로직 계층 (Domain Layer)

```
notification-core/
├── core-domain/     # 도메인 엔티티 및 비즈니스 규칙
├── core-service/    # 비즈니스 로직 구현 (UseCase)
└── core-port/       # 어댑터 인터페이스 정의 (Port)
```

## 설명

- **core-domain**: Notification 등 핵심 도메인 모델과 비즈니스 규칙
- **core-service**: 알림 생성, 전송, 조회 등 유스케이스 구현
- **core-port**: 외부 의존성(DB, 캐시, 메시징 등)과의 인터페이스 정의 (Hexagonal Architecture)
