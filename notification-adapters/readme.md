# notification-adapters

인프라 계층 (Infrastructure Layer)

```
notification-adapters/
├── adapter-persistence/   # 데이터베이스 영속성 (MongoDB)
├── adapter-redis/         # 캐시 및 세션 관리
├── adapter-kafka/         # 메시지 큐 이벤트 처리
├── adapter-smtp/          # 이메일 전송 (SMTP)
└── adapter-websocket/     # 실시간 웹소켓 통신
```

## 설명

- **adapter-persistence**: MongoDB 기반 알림 데이터 영속화
- **adapter-redis**: Redis 기반 캐싱 및 실시간 알림 임시 저장
- **adapter-kafka**: Kafka를 통한 알림 이벤트 구독 및 처리
- **adapter-smtp**: 이메일 알림 전송
- **adapter-websocket**: WebSocket을 통한 실시간 알림 푸시
