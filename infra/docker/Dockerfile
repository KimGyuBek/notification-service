FROM --platform=$BUILDPLATFORM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
#COPY ../.. .
COPY . .
RUN ./gradlew clean :notification-apps:app-api:bootJar -x test --no-daemon
RUN ls -al /app/notification-apps/app-api/build/libs

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/notification-apps/app-api/build/libs/app*.jar /app/notification-service.jar
ENTRYPOINT ["java", "-jar", "/app/notification-service.jar"]

