FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/json-extraction-challenge-0.0.1-SNAPSHOT.jar json-extraction-challenge.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "json-extraction-challenge.jar"]














