FROM openjdk:18-jdk-slim-buster

EXPOSE 8099

ADD target/DIAS-0.0.1-SNAPSHOT.jar RTE.jar 
ENTRYPOINT ["java","-jar","RTE.jar"]  