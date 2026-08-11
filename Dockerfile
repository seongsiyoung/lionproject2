FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

COPY build/libs/lionproject2-backend-0.0.1-SNAPSHOT.jar app.jar

RUN chown -R app:app /app

USER app

EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=70 -XX:InitialRAMPercentage=50"
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
