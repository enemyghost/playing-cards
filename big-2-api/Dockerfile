# Alpine Linux with OpenJDK JRE
FROM openjdk:11-jre-slim-stretch
# copy WAR into image
COPY target/big-2-api-1.0.0.jar /app.jar
# expose port 8080
EXPOSE 8080
# run application with this command line
CMD ["/usr/bin/java", "-jar", "-Dspring.profiles.active=default", "/app.jar"]
