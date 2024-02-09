# Start with a base image containing Java runtime
FROM openjdk:21

# Add a volume pointing to /tmp
VOLUME /tmp

# Available port to the world outside this container
EXPOSE 8080

# The application's jar file
ARG JAR_FILE=target/java-jokes-0.0.1-SNAPSHOT.jar

# Copy the JAR file into the container
COPY target/java-jokes-0.0.1-SNAPSHOT.jar app.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]