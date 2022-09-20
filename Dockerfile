FROM openjdk
WORKDIR /app/src
COPY ./target/life_calendar-0.0.1-SNAPSHOT.jar /app/src/life_calendar-0.0.1-SNAPSHOT.jar
EXPOSE 3001
ENTRYPOINT ["java", "-jar", "life_calendar-0.0.1-SNAPSHOT.jar"]