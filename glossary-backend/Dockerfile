FROM eclipse-temurin:17.0.4.1_1-jre as builder

VOLUME /tmp

ARG JAR_VERISON=0.0.3-SNAPSHOT
ARG JAR_FILE=build/libs/glossary-${JAR_VERISON}.jar

COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]