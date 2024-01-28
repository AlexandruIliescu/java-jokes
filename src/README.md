# Java Jokes Microservice
## Description
This Java Jokes Microservice is a Spring Boot-based RESTful API designed to fetch and serve jokes. Utilizing the official joke API as a source, it fetches jokes in batches and stores them in a MongoDB database. The service is equipped with retry capabilities to handle transient failures and a fallback mechanism to serve jokes from the database when the external API is unavailable.

## Prerequisites
- Java 21
- Maven
- Docker 
- MongoDB
 
## Setup and Run
### Build the Project
First, ensure you are in the project's root directory. Then, build the project using Maven with the development profile activated. This profile is configured to use the appropriate application properties for development.

`mvn clean package -D spring.profiles.active=dev`
### Build Docker Image
After successfully building the project, create a Docker image named java-jokes-app. This image will contain the compiled application and all necessary dependencies.

`docker build -t java-jokes-app .`


### Run Docker Image
Once the Docker image is built, you can run the microservice inside a Docker container. The following command maps port 8080 of the container to port 8080 on your host, allowing you to access the API through localhost:8080.

`docker run -p 8080:8080 java-jokes-app`

## Additional Docker Commands

### List Running Containers
To view all currently running Docker containers, use:
`docker ps`

### Stop a Container
To stop a running container, you need the container's ID or name. You can find this information using the docker ps command. To stop the container, use:

`docker stop <container_id_or_name>`

### Environment Configuration
The application is set up to run in a development environment by default, which is specified during the build process. The main application.properties file contains settings for production or general use, including the MongoDB URI and the Joke API URL. The application-dev.properties file overrides these settings for development purposes, especially changing the MongoDB URI to connect to a local MongoDB instance.

### MongoDB URI
For Docker environments, the MongoDB URI in the main application.properties file uses host.docker.internal to allow the containerized application to connect to MongoDB running on the host machine.

`spring.data.mongodb.uri=mongodb://host.docker.internal:27017/java_jokes`

In contrast, the development environment (application-dev.properties) expects a local MongoDB instance:

`spring.data.mongodb.uri=mongodb://localhost:27017/java_jokes`

Ensure that MongoDB is running and accessible at the specified URI before starting the application.

## Usage

Once the application is running, you can fetch jokes by accessing the /api/jokes endpoint through your web browser or a tool like curl. The service allows specifying the number of jokes to fetch via a query parameter, for example:

`curl http://localhost:8080/api/jokes?count=10`