FROM maven:3.9.4-eclipse-temurin-21-alpine

WORKDIR /app

#COPY ./target/demo-0.0.1-SNAPSHOT.jar .
#
#CMD ["java","-jar","demo-0.0.1-SNAPSHOT.jar"]


COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src

CMD ["./mvnw","spring-boot:run"]