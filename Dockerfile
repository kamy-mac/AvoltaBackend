FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Utilisez le nom exact de votre fichier JAR
COPY --from=build /app/target/avolta-backend-1.0.0.jar app.jar
EXPOSE 8090
ENV PORT=8090
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]