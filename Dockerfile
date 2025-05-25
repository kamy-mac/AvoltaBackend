# Dockerfile corrigé pour le backend Spring Boot

FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier d'abord le fichier pom.xml pour optimiser le cache Docker
COPY pom.xml .

# Télécharger les dépendances (cette étape sera mise en cache si pom.xml ne change pas)
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Construire l'application en mode verbose pour débugger si nécessaire
RUN mvn clean package -DskipTests -q

# Afficher le contenu du répertoire target pour vérification
RUN ls -la /app/target/

# Étape de production avec une image JRE plus légère
FROM eclipse-temurin:21-jre-alpine

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR construit depuis l'étape précédente
COPY --from=build /app/target/avolta-backend-1.0.0.jar app.jar

# Changer la propriété du fichier vers l'utilisateur non-root
RUN chown appuser:appgroup app.jar

# Basculer vers l'utilisateur non-root
USER appuser

# Exposer le port de l'application
EXPOSE 8090

# Configuration JVM optimisée pour le conteneur
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Point d'entrée pour démarrer l'application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Healthcheck pour vérifier que l'application fonctionne
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8090/api/health || exit 1