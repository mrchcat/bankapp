FROM eclipse-temurin:21.0.6_7-jdk
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar","app.jar"]