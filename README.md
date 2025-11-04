# Secure REST API - InfoSecurityLab1

Приватный Spring Boot 3.x проект, демонстрирующий защищённый REST API с JWT аутентификацией, защитой от OWASP Top 10 и интеграцией security-сканеров в CI/CD.

## 📋 Обзор проекта

Это учебное приложение для практики информационной безопасности на Java. Приложение реализует:

- **JWT-based аутентификацию** с BCrypt хешированием паролей
- **XSS защиту** через OWASP Java Encoder
- **SQL Injection защиту** через Spring Data JPA (parameterized queries)
- **Role-based Access Control** (RBAC)
- **Security scanning** в CI/CD (OWASP Dependency-Check, SpotBugs)
- **REST API** для управления постами и пользователями

## 🏗 Архитектура

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

## 🔐 Механизмы безопасности

### 1. Аутентификация (Authentication)

- **JWT (JSON Web Tokens)** - Stateless токены для каждого пользователя
- **BCrypt** - Криптографическое хеширование паролей с солью (12 rounds)
- **Token Expiration** - Токены действуют 1 час (⚠️ требует оптимизации)

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
.csrf(csrf -> csrf.disable())  // ✓ Correct for REST API (stateless)
```

### 6. HTTP Security Headers

```java
.headers(h -> h
    .frameOptions(f -> f.sameOrigin())
    .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
)
```

## 🚀 API Endpoints

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
  "content": "<script>alert('xss')</script>"  // Will be escaped
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

## ⚙️ Установка и запуск

### Требования

- **Java 17+**
- **Maven 3.8+**
- **Git**

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
| `user` | `password123` | USER |
| `admin` | `admin123` | USER, ADMIN |

## 🐛 Тестирование API

### Используя cURL

```bash
# 1.登録 (Регистрация)
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

### Используя Postman

1. Импортировать `openapi.yaml` в Postman
2. Set Environment: `base_url = http://localhost:8080`
3. Запустить запросы из коллекции

## 🔍 Security Scanning

### OWASP Dependency-Check

Сканирует известные уязвимости в зависимостях:

```bash
mvn org.owasp:dependency-check-maven:check
```

**Результаты:** `target/dependency-check-report.html`

### SpotBugs (Static Code Analysis)

Находит потенциальные баги:

```bash
mvn spotbugs:gui
```

## 📊 CI/CD Pipeline

GitHub Actions автоматически запускает при каждом push:

1. ✅ **Maven Build & Test** (`mvn -B verify`)
2. ✅ **OWASP Dependency-Check** (найти уязвимости в зависимостях)
3. ✅ **SpotBugs** (статический анализ кода)

Конфигурация: `.github/workflows/ci.yml`

## 🚨 Известные проблемы и рекомендации

### CRITICAL 🔴

1. **Hardcoded JWT Secret** (SEC-001)
   - ⚠️ JWT секрет хардкодирован в коде (только base64)
   - ✅ FIX: Переместить в `application-prod.properties` или environment variable
   ```bash
   export JWT_SECRET=$(openssl rand -base64 32)
   ```

2. **H2 Console Enabled** (SEC-003)
   - ⚠️ H2 console доступна всем и позволяет выполнять SQL запросы
   - ✅ FIX: Использовать profiles
   ```yaml
   spring:
     h2:
       console:
         enabled: false  # disable in production
   ```

### HIGH 🟠

3. **Password Hashing Strength** (SEC-004)
   - ⚠️ BCrypt использует 12 rounds (минимум)
   - ✅ FIX: Увеличить до 13-14 rounds
   ```java
   return new BCryptPasswordEncoder(13);  // Stronger
   ```

4. **User Data Exposure** (SEC-002)
   - ⚠️ `/api/data` выдаёт список всех пользователей
   - ✅ FIX: Использовать DTO, скрывать хеши паролей

5. **Default Test Credentials** (SEC-005)
   - ⚠️ Тестовые пользователи создаются автоматически
   - ✅ FIX: Использовать `@Profile("dev")`

### MEDIUM 🟡

6. **Long JWT Expiration** (SEC-006)
   - ⚠️ Токены действуют 1 час (длинный срок)
   - ✅ FIX: Сократить до 15-30 минут, добавить refresh tokens

7. **No HTTPS Enforcement** (SEC-007)
   - ⚠️ Нет SSL/HTTPS конфигурации
   - ✅ FIX: Добавить в `application-prod.properties`:
   ```properties
   server.ssl.key-store=classpath:keystore.jks
   server.ssl.key-store-password=password
   server.ssl.key-store-type=JKS
   ```

8. **No Rate Limiting** (SEC-010)
   - ⚠️ Отсутствует защита от brute force
   - ✅ FIX: Добавить Bucket4j или Resilience4j

9. **No CORS Configuration** (SEC-008)
   - ⚠️ Нет явной конфигурации CORS
   - ✅ FIX: Добавить в `SecurityConfig`:
   ```java
   @Bean
   public WebMvcConfigurer corsConfigurer() {
       return new WebMvcConfigurer() {
           @Override
           public void addCorsMappings(CorsRegistry registry) {
               registry.addMapping("/api/**")
                   .allowedOrigins("https://yourdomain.com")
                   .allowedMethods("GET", "POST", "PUT", "DELETE")
                   .allowCredentials(true)
                   .maxAge(3600);
           }
       };
   }
   ```

### LOW 🔵

10. **Generic Exception Handling** (SEC-012)
    - ⚠️ Выдаются подробности ошибок
    - ✅ FIX: Скрывать детали в production

## 📦 Зависимости

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

## 📚 Использованные стандарты

- **OWASP Top 10 (2021)** - Основные уязвимости
- **NIST Cybersecurity Framework** - Security practices
- **CWE/SANS Top 25** - Common Weakness Enumeration
- **Spring Security Best Practices** - Framework-specific security

## 🔗 Полезные ссылки

- [Spring Security Official Docs](https://spring.io/projects/spring-security)
- [OWASP Java Encoder](https://owasp.org/www-project-java-encoder/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [OWASP Top 10](https://owasp.org/Top10/)
- [NIST Password Guidelines](https://pages.nist.gov/800-63-3/sp800-63b.html)

## 📄 Лицензия

MIT License - See LICENSE file for details

## 👥 Автор

**Anastasia Proshka** - InfoSecurityLab1

---

**⚠️ Disclaimer:** Это учебное приложение создано в целях обучения информационной безопасности. Не используйте в production без полного аудита безопасности и внедрения всех рекомендаций.

**Последнее обновление:** Ноябрь 2025
