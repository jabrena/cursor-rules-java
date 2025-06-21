# Essential Maven Goals:

```bash
# Analyze dependencies
./mvnw dependency:tree
./mvnw dependency:analyze
./mvnw dependency:resolve

./mvnw clean validate -U
./mvnw buildplan:list-phase
./mvnw license:third-party-report

# Clean the project
./mvnw clean

# Clean and package in one command
./mvnw clean package

# Run integration tests
./mvnw clean verify
./mvnw clean verify -Pjacoco

# Check for dependency updates
./mvnw versions:display-property-updates
./mvnw versions:display-dependency-updates
./mvnw versions:display-plugin-updates

# Generate project reports
./mvnw clear site
jwebserver -p 8005 -d "$(pwd)/target/site/"

./mvnw clean spring-boot:run -Dspring-boot.run.profiles=local
curl "http://localhost:8080/api/v1/films?startsWith=A" 
open http://localhost:8080/api/v1/swagger-ui.html

docker compose up -d
``` 