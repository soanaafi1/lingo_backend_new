# Build stage: use Maven with Java 24
FROM maven:3.9.9-eclipse-temurin-24 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage: use JRE 24 for a smaller image
FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /app/target/duolingo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
