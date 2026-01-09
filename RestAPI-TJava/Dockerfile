FROM openjdk:24

ARG FILE_JAR=target/hung...jar

ADD ${FILE_JAR} api-service.jar

ENTRYPOINT ["java", "-jar", "api-service.jar"]

EXPOSE 8080