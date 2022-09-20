FROM openjdk
WORKDIR /app
COPY target/life_calendar-0.0.1-SNAPSHOT.jar ./life_calendar-0.0.1-SNAPSHOT.jar
EXPOSE 3001
CMD ["java", "-jar", "life_calendar-0.0.1-SNAPSHOT.jar"]