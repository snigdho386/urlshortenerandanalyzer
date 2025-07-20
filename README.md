# URL Shortener and Analyzer

A Spring Boot application that provides URL shortening functionality with click analytics and statistics tracking.

## 🚀 Features

- **URL Shortening**: Convert long URLs into short, manageable codes
- **Click Analytics**: Track clicks with detailed information (IP, referrer, user agent)
- **Statistics**: View detailed analytics for each shortened URL
- **RESTful API**: Clean REST endpoints for all operations
- **Database Persistence**: PostgreSQL database for data storage
- **Real-time Tracking**: Automatic click tracking on URL access

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.5.3
- **Database**: PostgreSQL 17.3
- **ORM**: Hibernate/JPA
- **Build Tool**: Maven
- **Java Version**: 21
- **Lombok**: For reducing boilerplate code
- **Testing**: JUnit 5, Mockito, Spring Boot Test, H2 Database

## 📋 Prerequisites

- Java 21 or higher
- PostgreSQL 17.3 or higher
- Maven 3.6+

## 🗄️ Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE urlshortener;
```

2. Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## 🚀 Running the Application

### Using Maven Wrapper
```bash
./mvnw spring-boot:run
```

### Using Maven
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

## 📚 API Endpoints

### 1. Create Short URL
**POST** `/api/shorten`
```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"originalUrl": "https://www.example.com"}'
```

**Response:**
```json
{
  "id": 1,
  "shortCode": "abc123",
  "originalUrl": "https://www.example.com",
  "createdAt": "2025-07-20T10:30:00.000",
  "clickStats": []
}
```

### 2. Redirect to Original URL
**GET** `/api/{shortCode}`
```bash
curl -I http://localhost:8080/api/abc123
```
Returns HTTP 302 redirect to the original URL.

### 3. Get All URLs
**GET** `/api/urls`
```bash
curl http://localhost:8080/api/urls
```

### 4. Get URL Statistics
**GET** `/api/stats/{shortCode}`
```bash
curl http://localhost:8080/api/stats/abc123
```

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/
│   │   ├── com/urlshorteneanalyser/urlshortenerandanalyzer/
│   │   │   └── UrlshortenerandanalyzerApplication.java  # Main application class
│   │   ├── Controller/
│   │   │   └── UrlController.java                       # REST API endpoints
│   │   ├── Service/
│   │   │   └── UrlService.java                          # Business logic
│   │   ├── Repository/
│   │   │   ├── ShortUrlRepository.java                  # URL data access
│   │   │   └── ClickStatsRepository.java                # Click stats data access
│   │   ├── Model/
│   │   │   ├── ShortUrl.java                            # URL entity
│   │   │   └── ClickStats.java                          # Click statistics entity
│   │   └── DTO/
│   │       └── UrlRequest.java                          # Request DTO
│   └── resources/
│       └── application.properties                       # Configuration
└── test/
    └── java/                                            # Test classes
```

## 🧪 Testing

This project includes comprehensive test coverage across all layers of the application. The testing strategy follows the testing pyramid with unit tests, integration tests, and repository tests.

### 🏃‍♂️ Running Tests

#### Run All Tests
```bash
./mvnw test
```

#### Run Specific Test Classes
```bash
# Service layer tests
./mvnw test -Dtest="Service.UrlServiceTest"

# Repository tests
./mvnw test -Dtest="Repository.ShortUrlRepositoryTest"
./mvnw test -Dtest="Repository.ClickStatsRepositoryTest"

# Controller tests
./mvnw test -Dtest="Controller.UrlControllerTest"
```

#### Run Tests with Coverage Report
```bash
./mvnw test jacoco:report
```

### 📋 Test Structure

```
src/test/
├── java/
│   ├── Service/
│   │   └── UrlServiceTest.java              # Service layer unit tests
│   ├── Controller/
│   │   └── UrlControllerTest.java           # Controller integration tests
│   └── Repository/
│       ├── ShortUrlRepositoryTest.java      # URL repository tests
│       └── ClickStatsRepositoryTest.java    # Click stats repository tests
└── resources/
    └── application-test.properties          # Test-specific configuration
```

### 🧪 Test Categories

#### 1. Service Layer Tests (`UrlServiceTest.java`)
**Purpose**: Unit tests for business logic
**Coverage**: 8 test methods
- ✅ URL creation with unique short codes
- ✅ Duplicate short code handling
- ✅ URL retrieval and redirection
- ✅ Click statistics recording
- ✅ Statistics retrieval
- ✅ Error handling for invalid short codes
- ✅ Empty URL list handling
- ✅ Statistics for non-existent URLs

**Key Features**:
- Uses Mockito for mocking dependencies
- Tests all public methods in UrlService
- Validates business logic in isolation
- Covers edge cases and error scenarios

#### 2. Repository Layer Tests
**Purpose**: Integration tests for database operations

**ShortUrlRepositoryTest.java**:
- ✅ Entity persistence and retrieval
- ✅ Custom finder methods
- ✅ Unique constraint validation
- ✅ Timestamp initialization
- ✅ Relationship handling

**ClickStatsRepositoryTest.java**:
- ✅ Click statistics persistence
- ✅ Relationship with ShortUrl entity
- ✅ Optional field handling
- ✅ Timestamp management
- ✅ Data integrity validation

#### 3. Controller Layer Tests (`UrlControllerTest.java`)
**Purpose**: Integration tests for REST endpoints
**Coverage**: 6 test methods
- ✅ POST /shorten endpoint
- ✅ GET /{shortCode} redirect endpoint
- ✅ GET /urls endpoint
- ✅ GET /stats/{code} endpoint
- ✅ Error handling for invalid requests
- ✅ Response format validation

**Key Features**:
- Uses MockMvc for HTTP request simulation
- Tests JSON request/response handling
- Validates HTTP status codes
- Tests endpoint behavior with mocked services

### 🔧 Test Configuration

#### Test Database
Tests use H2 in-memory database for fast execution:
```properties
# src/test/resources/application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
```

#### Test Annotations
- `@DataJpaTest`: For repository tests with embedded database
- `@ExtendWith(MockitoExtension.class)`: For unit tests with Mockito
- `@WebMvcTest`: For controller tests with MockMvc
- `@ContextConfiguration`: For specifying test configuration

### 📊 Test Coverage Metrics

| Layer | Test Class | Methods | Status |
|-------|------------|---------|--------|
| Service | UrlServiceTest | 8 | ✅ Passing |
| Repository | ShortUrlRepositoryTest | 5 | ✅ Passing |
| Repository | ClickStatsRepositoryTest | 4 | ✅ Passing |
| Controller | UrlControllerTest | 6 | ✅ Passing |
| **Total** | **4 Classes** | **23 Methods** | **✅ All Passing** |

### 🎯 Test Best Practices Implemented

1. **Arrange-Act-Assert Pattern**: All tests follow the AAA pattern
2. **Descriptive Test Names**: Method names clearly describe what is being tested
3. **Isolation**: Each test is independent and doesn't affect others
4. **Mocking**: External dependencies are properly mocked
5. **Edge Cases**: Tests cover both happy path and error scenarios
6. **Documentation**: Each test method includes detailed comments

### 🚀 Running Tests in CI/CD

For continuous integration, the tests can be run with:
```bash
# Clean build and test
./mvnw clean test

# Run tests with verbose output
./mvnw test -X

# Run tests and generate reports
./mvnw test jacoco:report surefire-report:report
```

### 🔍 Debugging Tests

To debug failing tests:
```bash
# Run specific test with debug output
./mvnw test -Dtest="Service.UrlServiceTest#createShortUrl_ShouldCreateValidShortUrl" -X

# Run tests with detailed logging
./mvnw test -Dlogging.level.org.springframework.test=DEBUG
```

## 📊 Database Schema

### ShortUrl Table
- `id`: Primary key
- `short_code`: Unique short code for the URL
- `original_url`: The original long URL
- `created_at`: Timestamp when URL was created

### ClickStats Table
- `id`: Primary key
- `clicked_at`: Timestamp of the click
- `ip_address`: IP address of the visitor
- `referrer`: Referrer URL
- `user_agent`: Browser/device information
- `short_url_id`: Foreign key to ShortUrl

## 🔧 Configuration

Key configuration options in `application.properties`:

```properties
# Server configuration
server.port=8080
server.servlet.context-path=/api

# Database configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/urlshortener
spring.datasource.username=postgres
spring.datasource.password=8250

# JPA configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions, please open an issue in the repository. 