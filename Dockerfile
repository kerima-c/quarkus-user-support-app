FROM eclipse-temurin:17-jdk-jammy

RUN apt-get update && apt-get install -y netcat

WORKDIR /work/

COPY target/quarkus-app/lib/ /work/lib/
COPY target/quarkus-app/app/ /work/app/
COPY target/quarkus-app/quarkus/ /work/quarkus/
COPY target/quarkus-app/quarkus-run.jar /work/

EXPOSE 8080

CMD ["java", "-Dquarkus.profile=dev", "-jar", "quarkus-run.jar"]
