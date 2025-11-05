# Secure REST API - InfoSecurityLab1

Приватный Spring Boot 3.x проект, демонстрирующий защищённый REST API с JWT аутентификацией, защитой от OWASP Top 10 и интеграцией security-сканеров в CI/CD.

## Обзор проекта

Это учебное приложение для практики информационной безопасности на Java. Приложение реализует:

- JWT-based аутентификацию с BCrypt хешированием паролей
- XSS защиту через OWASP Java Encoder
- SQL Injection защиту через Spring Data JPA (parameterized queries)
- Role-based Access Control (RBAC)
- Security scanning в CI/CD (OWASP Dependency-Check, SpotBugs)
- REST API для управления постами и пользователями

## Архитектура

```
src/
├── main/
│   ├── java/com/security/api/
│   │   ├── config/              # Security & application configuration
│   │   │   ├── SecurityConfig.java
│   │   │   └── DataInitializer.java
│   │   ├── controller/          # REST endpoints
│   │   │   ├── AuthController.java
│   │   │   ├── PostController.java
│   │   │   └── DataController.java
│   │   ├── service/             # Business logic
│   │   │   ├── UserService.java
│   │   │   ├── PostService.java
│   │   │   ├── JwtService.java
│   │   │   └── UserDetailsServiceImpl.java
│   │   ├── entity/              # JPA entities
│   │   │   ├── User.java
│   │   │   └── Post.java
│   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── AuthRequest.java
│   │   │   ├── AuthResponse.java
│   │   │   ├── UserRegistrationRequest.java
│   │   │   └── PostRequest.java
│   │   ├── repository/          # Data access layer
│   │   │   ├── UserRepository.java
│   │   │   └── PostRepository.java
│   │   ├── filter/              # JWT filter
│   │   │   └── JwtAuthFilter.java
│   │   ├── exception/           # Exception handling
│   │   │   └── GlobalExceptionHandler.java
│   │   └── SecureRestApiApplication.java
│   └── resources/
│       ├── application.properties
│       └── schema.sql (опционально)
└── test/
    └── java/...
```

## Механизмы безопасности

### 1. Аутентификация (Authentication)

- **JWT (JSON Web Tokens)** - Stateless токены для каждого пользователя
- **BCrypt** - Криптографическое хеширование паролей с солью (12 rounds)
- **Token Expiration** - Токены действуют 1 час (требует оптимизации)

```java
// Генерация JWT токена
String token = Jwts.builder()
    .setSubject(username)
    .setIssuedAt(new Date())
    .setExpiration(new Date(System.currentTimeMillis() + 3600000))
    .signWith(key(), SignatureAlgorithm.HS256)
    .compact();
```

### 2. Авторизация (Authorization)

- **Role-based Access Control** - ROLE_USER, ROLE_ADMIN
- **Spring Security SecurityFilterChain** - URL pattern matching
- **@PreAuthorize** - Method-level authorization (можно добавить)

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/auth/**", "/h2-console/**").permitAll()
    .anyRequest().authenticated()
)
```

### 3. XSS (Cross-Site Scripting) защита

Используется **OWASP Java Encoder** для экранирования HTML:

```java
public Post create(PostRequest req, String author) {
    Post p = new Post();
    p.setTitle(Encode.forHtml(req.getTitle()));      // XSS protection
    p.setContent(Encode.forHtml(req.getContent()));  // XSS protection
    return repo.save(p);
}
```

### 4. SQL Injection защита

Spring Data JPA использует **PreparedStatements** автоматически:

```java
// Не уязвимо для SQL injection
Optional<User> user = repo.findByUsername(username);
```

### 5. CSRF Protection

CSRF отключена явно (правильно для REST API):

```java
.csrf(csrf -> csrf.disable())  // Correct for REST API (stateless)
```

### 6. HTTP Security Headers

```java
.headers(h -> h
    .frameOptions(f -> f.sameOrigin())
    .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
)
```

## API Endpoints

### Authentication Endpoints

#### 1. Регистрация пользователя
```http
POST /auth/register
Content-Type: application/json

{
  "username": "newuser",
  "password": "SecurePassword123!",
  "email": "user@example.com"
}
```

**Response (201 Created):**
```json
{
  "token": null,
  "message": "User registered successfully",
  "username": "newuser"
}
```

#### 2. Аутентификация (Вход)
```http
POST /auth/login
Content-Type: application/json

{
  "username": "user",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNzMwNjM0NTQwLCJleHAiOjE3MzA2MzgxNDB9.abc123...",
  "message": "Authentication successful",
  "username": "user"
}
```

### Protected Endpoints (требуется JWT)

#### 3. Получить данные приложения
```http
GET /api/data
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
{
  "message": "Data retrieved successfully",
  "requestedBy": "user",
  "users": [...],
  "count": 2
}
```

#### 4. Создать пост
```http
POST /api/posts
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "title": "Security Best Practices",
  "content": "<script>alert('xss')</script>"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Security Best Practices",
  "content": "&lt;script&gt;alert('xss')&lt;/script&gt;",
  "author": "user"
}
```

#### 5. Получить все посты
```http
GET /api/posts
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
[
  {
    "id": 1,
    "title": "Security Best Practices",
    "content": "Content here...",
    "author": "user"
  }
]
```

## Установка и запуск

### Требования

- Java 17 или выше
- Maven 3.8 или выше
- Git

### Шаги для запуска

1. **Клонировать репозиторий**
   ```bash
   git clone https://github.com/ProshkaAnastasia/InfoSecurityLab1.git
   cd InfoSecurityLab1
   ```

2. **Собрать проект**
   ```bash
   mvn clean install
   ```

3. **Запустить приложение**
   ```bash
   mvn spring-boot:run
   ```

   Или:
   ```bash
   java -jar target/secure-rest-api-1.0.0.jar
   ```

4. **Проверить статус**
   ```bash
   curl http://localhost:8080/h2-console
   ```

### Тестовые учётные данные

Приложение автоматически создаёт двух пользователей при запуске:

| Username | Password | Role |
|----------|----------|------|
| user | password123 | USER |
| admin | admin123 | USER, ADMIN |

## Тестирование API

### Используя cURL

```bash
# 1. Регистрация
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Test123!","email":"test@example.com"}'

# 2. Аутентификация
RESPONSE=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password123"}')

TOKEN=$(echo $RESPONSE | jq -r '.token')

# 3. Получить данные (требует JWT)
curl -X GET http://localhost:8080/api/data \
  -H "Authorization: Bearer $TOKEN"

# 4. Создать пост
curl -X POST http://localhost:8080/api/posts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Post","content":"This is a test post"}'

# 5. Получить все посты
curl -X GET http://localhost:8080/api/posts \
  -H "Authorization: Bearer $TOKEN"
```

## Security Scanning

### OWASP Dependency-Check

Сканирует известные уязвимости в зависимостях:

```bash
mvn org.owasp:dependency-check-maven:check
```

Результаты: `target/dependency-check-report.html`

### SpotBugs (Static Code Analysis)

Находит потенциальные баги:

```bash
mvn spotbugs:gui
```

## CI/CD Pipeline

GitHub Actions автоматически запускает при каждом push:

1. Maven Build & Test (`mvn -B verify`)
2. OWASP Dependency-Check (поиск уязвимостей в зависимостях)
3. SpotBugs (статический анализ кода)

Конфигурация находится в файле `.github/workflows/ci.yml`

## Зависимости

```xml
<!-- Spring Boot -->
<spring-boot-version>3.2.0</spring-boot-version>

<!-- Security & JWT -->
<spring-boot-starter-security>3.2.0</spring-boot-starter-security>
<jjwt-api>0.12.3</jjwt-api>

<!-- Database -->
<h2>runtime</h2>
<spring-boot-starter-data-jpa>3.2.0</spring-boot-starter-data-jpa>

<!-- XSS Protection -->
<owasp-encoder>1.2.3</owasp-encoder>

<!-- Validation -->
<spring-boot-starter-validation>3.2.0</spring-boot-starter-validation>

<!-- Security Scanning -->
<dependency-check-maven>8.4.0</dependency-check-maven>
```
