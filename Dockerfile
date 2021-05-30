FROM openjdk:8

WORKDIR /app

COPY target/financas-0.0.1-SNAPSHOT.jar /app/financas.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "financas.jar"]