FROM openjdk:14-jdk-alpine
ARG JAR_FILE
ADD $JAR_FILE app.jar
ENTRYPOINT ["java","-jar","/app.jar"]