FROM ubuntu
EXPOSE 8080
RUN apt-get -y update
RUN apt-get -y install openjdk-8-jdk
WORKDIR /app/
COPY vertx-jdbc/ /app
ENTRYPOINT ["./gradlew","vertxRun"]
