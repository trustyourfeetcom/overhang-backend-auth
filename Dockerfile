### Build Stage ###
FROM maven:3.9.8-amazoncorretto-21-al2023 AS build

COPY src /home/app/src
COPY pom.xml /home/app

ENV SPRING_PROFILES_ACTIVE dev

RUN mvn -f /home/app/pom.xml clean package

### Run Stage ###
FROM amazoncorretto:21-alpine3.18-jdk
COPY --from=build /home/app/target/overhang-backend-auth-0.0.1-SNAPSHOT.jar /usr/local/lib/overhang-backend-auth.jar
ENV SPRING_PROFILES_ACTIVE dev
ENTRYPOINT ["java", "-jar", "/usr/local/lib/overhang-backend-auth.jar"]