

FROM eclipse-temurin:21-jre-alpine
LABEL authors="Kiruba Nithi"

WORKDIR /app


COPY target/*.jar app.jar


EXPOSE 8080


ENTRYPOINT ["java","-jar","app.jar"]
