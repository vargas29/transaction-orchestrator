FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar archivo JAR compilado
COPY target/transaction-orchestrator-1.0.0.jar app.jar

# Exponer puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

