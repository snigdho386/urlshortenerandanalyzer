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

Run tests using Maven:
```bash
./mvnw test
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