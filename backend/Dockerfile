FROM eclipse-temurin:17-jdk-alpine AS build
RUN apk add --no-cache maven
WORKDIR /app

COPY backend/pom.xml .
COPY backend/src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache netcat-openbsd bash
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY backend/wait-for-db.sh /wait-for-db.sh
RUN chmod +x /wait-for-db.sh

ENTRYPOINT ["/bin/bash", "-c", "/wait-for-db.sh && java -jar app.jar --spring.profiles.active=docker"]