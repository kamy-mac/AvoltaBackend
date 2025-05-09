FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .

# Téléchargez les dépendances séparément (cette étape sera mise en cache)
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -X
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

EXPOSE 8090
# Utilisez un script shell pour permettre la substitution de variables
CMD java -jar app.jar