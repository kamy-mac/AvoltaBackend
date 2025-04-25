FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
# Afficher le contenu du répertoire target pour debug
RUN ls -la /app/target/

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copier tout le contenu du répertoire target
COPY --from=build /app/target/ ./target/
# Afficher le contenu pour vérifier
RUN ls -la ./target/
# Utiliser un point d'entrée qui liste les fichiers avant de démarrer
ENTRYPOINT ["sh", "-c", "ls -la /app/target && java -jar /app/target/avolta-backend-1.0.0.jar"]