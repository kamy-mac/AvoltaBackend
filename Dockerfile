FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE ${PORT}
CMD ["sh", "-c", "java -Dserver.port=${PORT} -Dlogging.level.root=DEBUG -jar app.jar"]
RUN mvn clean package -DskipTests
RUN ls -la target/
