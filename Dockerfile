FROM openjdk:13.0.1

VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} ims.jar
ENTRYPOINT ["java", "-jar", "/ims.jar"]
