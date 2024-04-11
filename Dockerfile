FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY ./build/libs/dainius-sp-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 9090

ENTRYPOINT java $JAVA_OPTS -jar app.jar


