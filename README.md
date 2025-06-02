## Prerequisites

- Java 24 (with preview features enabled)
- Maven 3.x
- PostgreSQL database

## Tech Stack

### Backend
- Spring Boot 3.5.0
- Spring Security with JWT authentication
- Spring Data JPA
- PostgreSQL
- Project Lombok
- ModelMapper

### For admin interface
- HTML5/CSS3
- JavaScript
- Bootstrap 5.3.0
- Bootstrap Icons

## Setup & Installation

1. Clone the repository:
```batch
git clone https://github.com/teddbug-00/lingo_backend.git
cd duolingo
```

1. Configure the database:
   - Create a PostgreSQL database
   - Update `src/main/resources/application.properties` with your database credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_db_name
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

2. Build and run the application:
```batch
mvn clean install
mvn spring-boot:run
```

1. Access the application:
   - Admin panel: http://localhost:8080
   - Login page: http://localhost:8080/login.html

## Features

### Implemented
- User authentication with JWT
- Course management (create, edit, delete)
- Lesson management
- Exercise management with multiple types:
  - Translation exercises
  - Multiple choice questions
  - Matching exercises
- Progress tracking with XP system
- Hearts system
- Streak tracking

### Coming Soon
- User profiles with avatars
- Email verification
- Additional exercise types
- Virtual currency (gems/lingots)
- Social features
- Premium features

## Development

### Running in Development Mode

The application includes Spring Boot DevTools for hot reloading:

```batch
mvn spring-boot:run
```

### Building for Production

```batch
mvn clean package
```

The built JAR will be in `target/duolingo-0.0.1-SNAPSHOT.jar`

## Running Tests

```batch
mvn test
```

## API Documentation

The API documentation will be available at: http://localhost:8080/swagger-ui.html

## Environment Variables

Required environment variables:

```properties
JWT_SECRET=your_jwt_secret_key
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin_password
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request
