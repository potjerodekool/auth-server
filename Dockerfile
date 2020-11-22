FROM openjdk:14-jdk-alpine
ARG JAR_FILE=auth-server-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]