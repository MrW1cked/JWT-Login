FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY src src
COPY pom.xml ./
RUN mvn clean install

# PACKAGE STAGE
FROM eclipse-temurin:17-jdk
COPY --from=build /target/sousa-1.0.0-SNAPSHOT.jar sousa-1.0.0-SNAPSHOT.jar
CMD ["java", "-jar", "sousa-1.0.0-SNAPSHOT.jar"]