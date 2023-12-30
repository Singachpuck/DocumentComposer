FROM maven:3.9.6-eclipse-temurin-17-alpine

WORKDIR /usr/local/app

COPY ./ ./

ENV spring_profiles_active=prod

EXPOSE 8080

ENTRYPOINT ["mvn","spring-boot:run","-DskipTests"]