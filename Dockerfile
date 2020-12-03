FROM openjdk:13-jdk-alpine
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

# intended image name: com.smec/account-exercise:1.0.0
EXPOSE 8080
