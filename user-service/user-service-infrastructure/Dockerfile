FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom
# COPY pom.xml .

# # Copy all module directories (needed for Maven to resolve modules)
# COPY common-utils ./common-utils
# COPY user-service ./user-service
# COPY api-gateway ./api-gateway
# COPY discovery-server ./discovery-server
# COPY keycloak-adapter ./keycloak-adapter
# COPY notification-adapter ./notification-adapter
# COPY resourcebundle-adapter ./resourcebundle-adapter

# # Install the domain and api modules to local repository (creates .jar files in local repo)
# RUN mvn clean install -DskipTests -pl user-service/user-service-domain,user-service/user-service-api

# # Download dependencies
# RUN mvn dependency:go-offline -B -pl user-service/user-service-infrastructure -am

# # Build the application
# RUN mvn clean package -DskipTests -pl user-service/user-service-infrastructure

# Copy everything
COPY . .

# Build the entire project to resolve all dependencies
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built JAR
COPY --from=build /app/user-service/user-service-infrastructure/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]


