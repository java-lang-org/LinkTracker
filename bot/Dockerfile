FROM openjdk:23-jdk-oracle

RUN groupadd -r bot && useradd -r -g bot user
USER user

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
