# ---------- Etapa 1: build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Se copia el pom primero para aprovechar el cache de capas de Docker:
# si solo cambia el código fuente, no se vuelven a descargar las dependencias.
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
RUN mvn -q -B package -DskipTests

# ---------- Etapa 2: runtime ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 9769
ENTRYPOINT ["java", "-jar", "app.jar"]
