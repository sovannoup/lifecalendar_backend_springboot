FROM openjdk:18.0.1.1
LABEL maintainer="noupsovan18@gmail.com"
VOLUME /main-app
ADD build/libs/spring-boot-postgresql-base-project-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 3031
ENTRYPOINT ["java", "-jar","/app.jar"]