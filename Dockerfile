FROM eclipse-temurin:17-jdk AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests dependency:go-offline

COPY src src

RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

RUN mkdir -p /app/data

COPY --from=build /workspace/target/avicole-stock-ia-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
