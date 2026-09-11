# Compact Disc REST API

A Spring Boot REST API application for managing a compact disc catalog with full CRUD operations.

## Overview

This application provides a RESTful web service for managing a music compact disc catalog. Users can retrieve, add, update, and delete compact discs from the database. The application includes Swagger UI for API documentation and is built with Spring Boot and MySQL.

## Features

- **Get all compact discs** - Retrieve the complete catalog
- **Get disc by ID** - Retrieve a specific disc with error handling
- **Add new disc** - Insert a new compact disc to the catalog
- **Delete disc** - Remove a disc from the catalog
- **API Documentation** - Integrated Swagger UI for easy API exploration
- **Logging** - Log4j2 logging for monitoring and debugging
- **CORS Support** - Cross-origin requests enabled for web clients
- **Database Persistence** - MySQL database with JPA/Hibernate ORM

## Technology Stack

- **Framework**: Spring Boot 2.5.3
- **Language**: Java 11
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA / Hibernate
- **API Documentation**: Springfox Swagger 2.9.2
- **Logging**: Log4j2
- **Build Tool**: Maven
- **Testing**: JUnit, Mockito

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- MySQL Server (version 8.0+)
- Port 8080 available (configurable in `application.properties`)

## Installation

1. **Clone or download the project**
   ```bash
   cd no-readme
   ```

2. **Set up the MySQL database**
   - Ensure MySQL is running on `localhost:3306`
   - Run the SQL schema and initial data:
     ```bash
     mysql -u root -p < sql/createTables.sql
     ```
   - Default credentials are configured in `src/main/resources/application.properties`
   - Database name: `conygre`
   - Default database schema includes `compact_discs` and `tracks` tables

3. **Build the project**
   ```bash
   mvn clean install
   ```

## Configuration

### Database Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/conygre?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=c0nygre1
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
```

### Server Port

To change the default port (8080):
```properties
server.port=8081
```

### Logging

- Logs are written to `myapplication.log`
- Configure logging levels in `application.properties`

## Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using Java directly

```bash
mvn clean package
java -jar target/CompactDiscRestDataBoot-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## API Endpoints

All endpoints are prefixed with `/api/compactdiscs`

### Get all compact discs

```http
GET /api/compactdiscs
```

**Response**: JSON array of all compact discs

### Get compact disc by ID

```http
GET /api/compactdiscs/{id}
```

**Response**: JSON object of the compact disc

### Get compact disc by ID (with 404 handling)

```http
GET /api/compactdiscs/404/{id}
```

**Response**: JSON object with HTTP 200 OK or HTTP 404 NOT_FOUND

### Add a new compact disc

```http
POST /api/compactdiscs
Content-Type: application/json

{
  "title": "Sweet Caroline",
  "artist": "Neil Diamond",
  "price": 13.99,
  "tracks": 1
}
```

### Delete a compact disc by ID

```http
DELETE /api/compactdiscs/{id}
Content-Type: application/json
```

### Delete a compact disc (by object)

```http
DELETE /api/compactdiscs
Content-Type: application/json

{
  "id": 14,
  "title": "Echo Park",
  "artist": "Feeder",
  "tracks": 12,
  "price": 13.99
}
```

## API Documentation

Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

This provides an interactive interface to explore and test all API endpoints.

## Database Schema

### compact_discs table

| Column | Type | Constraints |
|--------|------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT |
| title | VARCHAR(50) | |
| artist | VARCHAR(30) | |
| tracks | INT | |
| price | DOUBLE | |

### tracks table

| Column | Type | Constraints |
|--------|------|-------------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT |
| cd_id | INT | NOT NULL, FOREIGN KEY (compact_discs.id) |
| title | VARCHAR(50) | |

## Sample Data

The database comes pre-populated with sample compact discs:

- "Is This It" by The Strokes
- "Just Enough Education to Perform" by Stereophonics
- "Parachutes" by Coldplay
- "White Ladder" by David Gray
- "Greatest Hits" by Penelope
- "Echo Park" by Feeder
- "Mezzanine" by Massive Attack
- "Spice World" by Spice Girls

## Testing

Run the test suite with:

```bash
mvn test
```

Tests use H2 in-memory database for isolation and use Mockito for mocking dependencies.

## Monitoring and Logging

- Application logs are written to `myapplication.log`
- Log levels can be configured per package in `application.properties`
- Use the application logs to monitor requests and identify issues

## Deployment

### Docker Deployment

An alternative configuration file is available for Docker environments:
- `src/main/resources/application-docker.properties`

Set the active profile when running in Docker:
```bash
java -jar app.jar --spring.profiles.active=docker
```

## Project Structure

```
no-readme/
├── pom.xml                          # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/com/conygre/spring/boot/
│   │   │   ├── AppConfig.java       # Spring Boot entry point
│   │   │   ├── SwaggerConfig.java   # Swagger configuration
│   │   │   ├── rest/
│   │   │   │   └── CompactDiscController.java   # REST endpoints
│   │   │   ├── services/            # Business logic
│   │   │   ├── repos/               # Data access layer
│   │   │   └── entities/            # JPA entities
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── log4j2.properties
│   │       └── static/              # HTML/CSS/JS files
│   └── test/java/                   # Unit tests
├── sql/
│   └── createTables.sql             # Database schema
└── rest/                            # REST client test files
    ├── postcd.rest
    └── deletecd.rest
```

## Troubleshooting

### Connection Error: "Can't connect to MySQL server"

- Verify MySQL is running
- Check database credentials in `application.properties`
- Ensure the database `conygre` exists

### Port Already in Use

- Change the server port in `application.properties`:
  ```properties
  server.port=8081
  ```

### No Entities Found

- Run the SQL schema script: `mysql -u root -p < sql/createTables.sql`
- Verify the database connection

## License

Not specified - Contact project maintainers for details.

## Support

For issues and questions, please contact the development team.