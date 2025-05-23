FROM eclipse-temurin:23.0.2_7-jre
EXPOSE 8010
ADD target/analytics-0.0.1-SNAPSHOT.jar analytics-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/analytics-0.0.1-SNAPSHOT.jar"]