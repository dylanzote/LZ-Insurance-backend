FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom
COPY pom.xml .

# Copy all module directories (needed for Maven to resolve modules)
COPY common-utils ./common-utils
COPY user-service ./user-service
COPY api-gateway ./api-gateway
COPY discovery-server ./discovery-server
COPY keycloak-adapter ./keycloak-adapter
COPY notification-adapter ./notification-adapter
COPY resourcebundle-adapter ./resourcebundle-adapter

# Download dependencies
RUN mvn dependency:go-offline -B -pl discovery-server -am

# Build the application
RUN mvn clean package -DskipTests -pl discovery-server

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# FIX: add curl for healthcheck
#RUN apk add --no-cache curl

# Copy the built JAR
COPY --from=build /app/discovery-server/target/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]


