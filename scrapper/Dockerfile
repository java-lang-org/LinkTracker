FROM openjdk:23-jdk-oracle

RUN groupadd -r scrapper && useradd -r -g scrapper user
USER user

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
