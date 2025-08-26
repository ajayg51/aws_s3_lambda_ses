FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY target/s3-copy-batch-1.0.0.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
