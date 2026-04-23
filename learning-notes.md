# Banking System — Learning Notes
## Complete Revision: 10 Topics

---

## #1 — Project Structure & Spring Boot Basics

### Monolith vs Microservices
In a monolith, everything (controllers, services, DAOs) lives in one project, one WAR, one database.
In microservices, each service is an **independent Spring Boot application** — its own `pom.xml`, its own `main()`, its own database config, its own port, its own JVM.

```
banking-system/
├── user-service/        → port 8081, handles auth & users
├── account-service/     → port 8082, handles accounts
└── transaction-service/ → port 8083, handles transfers
```

### Key Dependencies (pom.xml)

| Dependency | Purpose |
|---|---|
| `spring-boot-starter-web` | REST controller support |
| `spring-boot-starter-data-jpa` | ORM — replaces JDBC boilerplate |
| `mysql-connector-j` | JDBC driver for MySQL |
| `spring-boot-starter-security` | Authentication & authorization |
| `spring-boot-starter-validation` | `@NotNull`, `@Email` on DTOs |
| `jjwt-api/impl/jackson` | JWT creation, parsing, JSON support (3 JARs) |
| `spring-kafka` | Kafka producer/consumer |
| `spring-boot-starter-data-redis` | Redis client for caching |
| `lombok` | Eliminates boilerplate (getters, setters, constructors) |

### `UserServiceApplication.java` Annotations

| Annotation | What it does |
|---|---|
| `@SpringBootApplication` | Combines `@Configuration` + `@ComponentScan` + `@EnableAutoConfiguration` |
| `@EnableJpaAuditing` | Enables auto-population of `@CreatedDate`, `@LastModifiedDate` fields |
| `@EnableCaching` | Activates Spring's caching abstraction (backed by Redis) |

### Why `PasswordEncoder` bean is defined in main class
`SecurityConfig` → needs → `UserServiceImpl` → needs → `PasswordEncoder` → defined in → `SecurityConfig`
= circular dependency. Defining `PasswordEncoder` in the main class breaks the cycle — it becomes neutral territory built first.

### `application.yml` Key Properties

| Property | Value | Meaning |
|---|---|---|
| `ddl-auto: update` | update | Hibernate auto-creates/updates tables |
| `show-sql: true` | true | Prints all SQL to console (dev only) |
| `createDatabaseIfNotExist=true` | in JDBC URL | Creates DB automatically if missing |
| `jwt.secret` / `jwt.expiration` | custom | Read via `@Value` in JwtUtil |

### `ddl-auto` Options

| Value | Behavior | Use when |
|---|---|---|
| `create` | Drops & recreates tables on start | Early dev (loses data) |
| `create-drop` | Creates on start, drops on shutdown | Integration tests |
| `update` | Adds missing columns/tables, never deletes | Dev/learning |
| `validate` | Crashes if entity doesn't match DB | Staging/UAT |
| `none` | Does nothing — you manage schema | Production |

> **Production rule:** Use Flyway or Liquibase with versioned SQL migration scripts instead of `ddl-auto`.

---

## #2 — JPA Entity + Repository + MySQL

### Old JDBC vs JPA
Old: `Connection → PreparedStatement → ResultSet → manual mapping`
New: Annotate your class → Hibernate generates the SQL automatically.

### `Users` Entity — Annotations Explained

| Annotation | Purpose |
|---|---|
| `@Data` | Generates getters, setters, toString, equals, hashCode |
| `@AllArgsConstructor` | Constructor with all fields |
| `@NoArgsConstructor` | No-arg constructor — **required by JPA** (Hibernate creates empty object then sets fields) |
| `@Entity` | Maps this class to a DB table |
| `@Id` | Marks the primary key |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | Maps to MySQL AUTO_INCREMENT |
| `@EntityListeners(AuditingEntityListener.class)` | Hooks Spring auditing — auto-populates `@CreatedDate` fields |
| `@CreatedDate` | Auto-set to current timestamp on first save |
| `implements Serializable` | Required for Redis caching — converts object to bytes |

> `@EnableJpaAuditing` in main class is required for `@CreatedDate` to work.

### `UserRepo` — JPA Repository

```java
public interface UserRepo extends JpaRepository<Users, Integer>
```
- First type param = Entity type
- Second type param = Primary key type
- Spring generates the implementation at runtime — you write zero SQL

**Naming convention → auto-generated SQL:**

| Method name | Generated SQL |
|---|---|
| `findByMail(String mail)` | `SELECT * FROM users WHERE mail = ?` |
| `existsByMail(String mail)` | `SELECT COUNT(*) > 0 FROM users WHERE mail = ?` |

**Free methods from JpaRepository:**

| Method | Action |
|---|---|
| `save(entity)` | INSERT or UPDATE |
| `findById(id)` | SELECT by PK |
| `findAll()` | SELECT all |
| `deleteById(id)` | DELETE by PK |
| `count()` | COUNT(*) |

> `findById` is already in `JpaRepository` — redeclaring it in your interface is redundant (not a bug, just unnecessary).

---

## #3 — DTOs, Service Layer & Controller

### Why DTOs?
The `Users` entity has sensitive fields and internal IDs. If you accept/return entities directly at the API level:
- Caller can manipulate the primary key
- Hashed passwords get exposed in responses

**DTOs are boundary objects** — define exactly what comes IN and goes OUT.

### Validation Annotations
- `@NotBlank` — cannot be null, empty, or whitespace
- `@Email` — validates email format
- These do **nothing** without `@Valid` on the controller method parameter.

### Why Service Interface?
- **Loose coupling** — controller depends on the interface, not the implementation
- **Testability** — easy to mock in unit tests
- If implementation changes, controller doesn't change

### `UserServiceImpl` — Key Logic

**`save()` method:**
1. Check if email already exists (`existsByMail`)
2. Create new `Users` entity
3. Encode password: `passwordEncoder.encode(rawPassword)` → BCrypt hash
4. Save to DB

**`login()` method:**
1. Find user by email (`orElseThrow` handles not found)
2. Verify password: `passwordEncoder.matches(rawPassword, storedHash)` → true/false
3. Generate JWT token

**`getUser()` method:**
```java
@Cacheable(value = "users", key = "#id")
```
- Cache miss → hits DB → stores result in Redis → returns
- Cache hit → returns from Redis directly, DB never called
- `System.out.println` inside = diagnostic. If you see it twice for same ID → caching broken.

> BCrypt is one-way — you can never decrypt. You can only verify using `matches()`.

### Controller

| Annotation | Purpose |
|---|---|
| `@RestController` | `@Controller` + `@ResponseBody` — all methods return JSON |
| `@RequestMapping("/api/users")` | Base path for all endpoints |
| `@Valid` | Activates DTO validation annotations |
| `@RequestBody` | Deserializes JSON body to Java object |
| `@PathVariable` | Binds `{id}` from URL to method parameter |
| `ResponseEntity` | Gives control over HTTP status code |

**HTTP Status codes used:**
- `HttpStatus.CREATED` (201) — resource created
- `ResponseEntity.ok()` (200) — successful read

### Registration Flow
```
POST /api/users/register
  → @Valid checks DTO
  → userService.save(dto)
  → existsByMail → MySQL
  → passwordEncoder.encode()
  → map DTO to entity
  → userRepo.save() → INSERT
  → return 201 CREATED
```

---

## #4 — Spring Security + JWT

### Session-based vs JWT (Stateless)
**Session-based:** Server stores session, browser sends cookie. Cannot share sessions across microservices (different JVMs).

**JWT:** Server generates a token, client stores it, sends it on every request. Each service validates the token **locally** — no shared state needed.

### JWT Structure
```
header.payload.signature
xxxxx.yyyyy.zzzzz
```
- Payload is Base64 encoded — anyone can decode and read it
- **Signature** is what makes it tamper-proof — generated using the secret key
- If payload is changed, signature won't match → rejected

### `JwtUtil` Methods

| Method | Purpose |
|---|---|
| `getSigningKey()` | Converts secret string → cryptographic key |
| `generateToken(email)` | Builds JWT with subject, issuedAt, expiration, signature |
| `getClaims(token)` | Parses token, verifies signature — throws if expired/tampered |
| `isTokenValid(token)` | Calls getClaims in try-catch → returns true/false |
| `extractEmail(token)` | Gets subject (email) from token claims |

**Token expiration:** `System.currentTimeMillis() + 86400000` = now + 24 hours.

### `JwtFilter` — extends `OncePerRequestFilter`
Runs on **every HTTP request**, exactly once.

```
1. Read "Authorization" header
2. Does it start with "Bearer"? → No: skip, pass request along
3. Extract token (substring after index 7)
4. isTokenValid(token)? → No: skip, pass request along
5. extractEmail(token)
6. Create UsernamePasswordAuthenticationToken(email, null, List.of())
7. Set in SecurityContextHolder
8. Call filterChain.doFilter() — always, regardless
```

`SecurityContextHolder` = Spring Security's per-request in-memory store for authentication.

> `filterChain.doFilter()` must always be called — it passes the request to the next filter. Without it, the request stops here and the client gets no response.

### `SecurityConfig`

```java
.csrf(csrf -> csrf.disable())                          // JWT = stateless, no CSRF needed
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/users/register",
                     "/api/users/login",
                     "/api/users/{id}").permitAll()    // public endpoints
    .anyRequest().authenticated()                       // all others need JWT
)
.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
```

> **Bug fixed:** `/register` and `/login` must be in `permitAll()` — users need these to get a token, so they can't require a token.

### Full JWT Flow
```
POST /login → returns JWT to client
Client stores JWT
GET /api/accounts → Header: Authorization: Bearer <token>
  JwtFilter validates token → sets authentication
  SecurityConfig → authenticated → allows
  Controller handles request
```

---

## #5 — Feign Client & Inter-Service Communication

### Problem
`account-service` needs user data from `user-service` — different JVM, different port.

### Options
| Approach | Problem |
|---|---|
| Shared DB | Tight coupling, breaks service independence |
| Manual RestTemplate | Verbose — URL building, header setting, response parsing |
| Feign Client | Declare interface, Spring generates HTTP call — clean and minimal |

### `UserClient`
```java
@FeignClient(name="user-service", url="${user.service.url}")
public interface UserClient {
    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable Integer id);
}
```
- Mirror the exact endpoint from the target service
- Spring generates the implementation at runtime
- `@EnableFeignClients` on main class is required — without it, nothing works

### `UserDto` in account-service
Each service owns a local copy of only the fields it needs from other services.
Never share entity classes across service boundaries.

### `FeignClientConfig` — JWT Forwarding
**Problem:** Feign calls have no Authorization header by default → user-service returns 401.

**Solution:** `RequestInterceptor` — runs before every outgoing Feign request.
1. Gets current incoming request from `RequestContextHolder`
2. Reads Authorization header from it
3. Copies it to the outgoing Feign request

> **Limitation:** `RequestContextHolder` uses ThreadLocal. If the Feign call is made from an `@Async` method (different thread), the ThreadLocal is empty → JWT forwarding fails.

### `@EnableFeignClients` on AccountServiceApplication
Without this annotation, `@FeignClient` interfaces are just plain interfaces — no HTTP calls are generated.

---

## #6 — Async Config & Thread Pool

### Why Async?
By default, one thread handles everything for one HTTP request.
If Kafka publishing is slow or Kafka is down, the request thread hangs — user waits.

**Fix:** Publish Kafka events on a background thread. Request thread responds immediately.

### `AsyncConfig` — Thread Pool Settings

```java
executor.setCorePoolSize(5);     // always-ready workers
executor.setMaxPoolSize(10);     // max workers during spike
executor.setQueueCapacity(100);  // tasks waiting if all 10 busy
executor.setThreadNamePrefix("banking-async");
```

**When all limits hit:** core(10) threads busy + queue(100) full + new task → `RejectedExecutionException`

### `@Async("taskExecuter")` on `publishAccountCreated()`
- Calling thread returns immediately — task is handed to the pool
- Pool thread (`banking-async-1`) runs the method independently
- `System.out.println` with thread name = diagnostic to verify it's running on pool thread

### Two methods, two behaviors

| Method | Async? | Why |
|---|---|---|
| `publishAccountCreated()` | Yes (`@Async`) | Notification — not critical to response, fire & forget |
| `publishEvent()` | No | Saga events — timing and order matter, must stay on request thread |

### `@EnableAsync` — Required
Declared both in `AccountServiceApplication` and `AsyncConfig`. Works fine, just slightly redundant.

---

## #7 — Kafka: Producers, Consumers & Events

### Core Concepts

| Term | Meaning |
|---|---|
| Topic | Named channel — like a named queue |
| Producer | Publishes messages to a topic |
| Consumer | Reads messages from a topic |
| Consumer Group | Group of consumers sharing work — each message goes to only ONE consumer in the group |
| `auto-offset-reset: earliest` | New consumer starts reading from beginning of topic |

### Our Topics

| Topic | Producer | Consumer |
|---|---|---|
| `account-created` | account-service | user-service |
| `debit-request` | transaction-service | account-service |
| `debit-success` | account-service | transaction-service |
| `debit-failed` | account-service | transaction-service |
| `credit-request` | transaction-service | account-service |
| `credit-success` | account-service | ❌ nobody (bug) |
| `credit-failed` | account-service | transaction-service |
| `compensate-debit` | transaction-service | account-service |

### Message Format
Plain string with key=value pairs:
```
transactionId=123,fromAccount=ACC001,amount=500.0
```
Parsed manually using the `extract()` helper method.

### `KafkaTemplate` — Producer
```java
kafkaTemplate.send("topic-name", message);
```
Auto-configured from `application.yaml`. Just `@Autowired` it.

### `@KafkaListener` — Consumer
```java
@KafkaListener(topics = "debit-request", groupId = "account-service-group")
public void handleDebitRequest(String message) { ... }
```
Spring creates a background polling thread. When a message arrives, this method is called automatically.

### Consumer Group Isolation
Each service has its own group ID:
- `account-service-group` — only account-service reads its topics
- `transaction-service-group` — only transaction-service reads its topics
- `user-service-group` — only user-service reads its topics

### What happens if Kafka is down?

| Scenario | Behavior |
|---|---|
| Producer publishes, Kafka down | Retries, then fails — transaction stays PENDING forever |
| Consumer running, Kafka goes down | Auto-reconnects, reads missed messages when Kafka recovers |
| Kafka recovers | Messages were stored on disk — consumers catch up |

> **Production fix:** Outbox Pattern — save event to DB in same transaction as business data, background process publishes to Kafka. Guarantees no events are lost.

### Bug: Missing `handleCreditSuccess()`
`credit-success` topic has no listener in `TransactionEventConsumer`.
After a fully successful transfer, transaction stays `PENDING` forever.

**Fix:** Add:
```java
@KafkaListener(topics = "credit-success", groupId = "transaction-service-group")
public void handleCreditSuccess(String message) {
    Integer transactionId = extractId(message);
    Transaction transaction = transactionRepo.findById(transactionId).orElseThrow();
    transaction.setStatus("SUCCESS");
    transactionRepo.save(transaction);
}
```

---

## #8 — Saga Pattern

### Why Saga?
`@Transactional` works on a single database connection. In microservices, debit and credit happen on separate JVMs — you cannot have one `@Transactional` spanning multiple services.

**Saga = sequence of local transactions, each publishing an event, with compensating transactions to undo steps on failure.**

### Choreography vs Orchestration

| Type | How it works | Our choice? |
|---|---|---|
| Choreography | Each service reacts to events independently, no central brain | No |
| Orchestration | One coordinator tells others what to do step by step | Yes |

**`transaction-service` is the orchestrator.** Account-service is a participant — executes commands, reports back.

### `initiateTransfer()` — Saga Kickoff
```
1. Feign → validate fromAccount exists
2. Feign → validate toAccount exists
3. Pre-check balance (optimization, not authoritative)
4. Save Transaction(status=PENDING) to DB
5. Publish "debit-request" to Kafka
6. Return HTTP 202 ACCEPTED immediately
```

**HTTP 202 ACCEPTED** — "request received and processing, not done yet." More honest than 200.

### Happy Path
```
initiateTransfer() → debit-request
  → account-service debits fromAccount → debit-success
  → transaction-service gets debit-success → publishes credit-request
  → account-service credits toAccount → credit-success
  → ❌ nobody handles credit-success (bug — transaction stays PENDING)
```

### Debit Fails
```
debit-request → balance insufficient → debit-failed
  → transaction-service marks transaction = FAILED
  → done (nothing to compensate — money never left)
```

### Credit Fails (Rollback)
```
credit-request → account not found → credit-failed
  → transaction-service marks transaction = FAILED
  → publishes compensate-debit
  → account-service adds amount back to fromAccount (refund)
```

### Full Diagram
```
initiateTransfer()
    │
    ▼
[PENDING saved]
    │
    ▼
debit-request ──────────► account-service debits
                                  │
                       ┌──────────┴──────────┐
                   debit-success         debit-failed
                       │                     │
                       ▼                     ▼
                credit-request          [FAILED saved]
                       │
              account-service credits
                       │
            ┌──────────┴──────────┐
        credit-success        credit-failed
            │                     │
        ❌ nobody            [FAILED saved]
        handles (bug)             │
                          compensate-debit
                                  │
                          account-service refunds
```

### Bugs Identified

| Bug | Location | Fix |
|---|---|---|
| No `handleCreditSuccess()` | `TransactionEventConsumer` | Add listener, mark transaction SUCCESS |
| `status` is plain String | `Transaction` entity | Use enum `TransactionStatus { PENDING, SUCCESS, FAILED }` |

### Compensation Failure
If `compensate-debit` itself fails → money is lost. No automatic fix.
Production solution: Dead Letter Queue (DLQ) + retries + alerts for manual intervention.

---

## #9 — Docker Compose

### What it solves
- Packages all services + infrastructure into containers
- One command starts everything: `docker-compose up`
- "Works on my machine" problem eliminated — runs the same everywhere

### Service Block Structure
```yaml
service-name:
  image: ...         # Docker image to use
  container_name:    # friendly name
  ports:             # hostPort:containerPort
  environment:       # env vars injected into container
  depends_on:        # start order
```

### Kafka — Two Listeners (Docker Compose)
Inside Docker, two types of clients need to reach Kafka:

| Client | Network | Address |
|---|---|---|
| Spring Boot services (containers) | Docker internal | `kafka:9092` (INTERNAL) |
| Local machine (Postman, terminal) | External | `localhost:9093` (EXTERNAL) |

```yaml
KAFKA_LISTENERS: INTERNAL://0.0.0.0:9092,EXTERNAL://0.0.0.0:9093
KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka:9092,EXTERNAL://localhost:9093
```

### Environment Variables Override `application.yaml`
Spring config priority (highest to lowest):
1. Environment variables
2. `application.yaml`
3. Default values

Spring auto-converts: `SPRING_DATASOURCE_URL` → `spring.datasource.url`

So same JAR runs locally (reads yaml) and in Docker (env vars override yaml).

### `depends_on` Limitation
Only waits for container to **start** — not for the service inside to be **ready**.
MySQL takes ~10 seconds to initialize. App may crash trying to connect before MySQL is ready.

**Production fix:** Health checks with `condition: service_healthy`.

### Bug in `docker-compose.yml`
```yaml
# Wrong:
ACCOUNT_SERVICE_URL: http://account-service-9092

# Correct:
ACCOUNT_SERVICE_URL: http://account-service:8082
```
`account-service` = Docker internal DNS name, `8082` = internal port.

---

## #10 — Kubernetes

### Docker Compose vs Kubernetes

| Problem | Docker Compose | Kubernetes |
|---|---|---|
| Container crashes | Stays dead | Auto-restarts |
| High traffic | Manual scaling | Auto-scaling |
| Rolling updates | Downtime required | Zero-downtime |
| Multiple machines | Not supported | Designed for it |
| Health monitoring | None | Built-in |

### Core K8s Concepts

| Concept | Analogy | What it does |
|---|---|---|
| Pod | One running app instance | One or more containers running together |
| Deployment | Job posting for N workers | Maintains N pod replicas, recreates on crash |
| Service | Team email address | Stable network endpoint for a set of pods |

### Deployment File — Key Fields

| Field | Purpose |
|---|---|
| `replicas: 1` | Number of pod instances — change to 3 for load balancing |
| `selector.matchLabels` | How Deployment identifies its pods (must match template labels) |
| `imagePullPolicy: Never` | Use local Docker image (Minikube) — change to `Always` in production |
| `env` | Environment variable overrides — same pattern as Docker Compose |

### Service File — Key Fields

| Field | Purpose |
|---|---|
| `selector` | Routes traffic to pods with matching label |
| `port` | Port other services use to reach this Service |
| `targetPort` | Actual port the container listens on |
| `type: ClusterIP` | Only reachable inside the cluster |

**Service types:**
- `ClusterIP` — internal only (what we use)
- `NodePort` — accessible from outside on a node port
- `LoadBalancer` — external load balancer (cloud providers)
- `Ingress` — production standard for external HTTP routing

### Kafka in K8s vs Docker Compose
In K8s, all Spring Boot services run as pods inside the cluster.
No need for an external listener — one listener is enough:
```yaml
KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
```

### MySQL Deployment — Two Problems

| Problem | Fix |
|---|---|
| No persistent storage — data lost on pod restart | Add `PersistentVolumeClaim` |
| Password in plain text in YAML | Use K8s Secrets with `secretKeyRef` |

### Environment Comparison

| Config | Local Dev | Docker Compose | Kubernetes |
|---|---|---|---|
| Kafka | `localhost:9093` | `kafka:9092` | `kafka:9092` |
| MySQL | `localhost:3306` | `mysql:3306` | `mysql:3306` |
| Redis | `localhost:6379` | `redis` | `redis` |
| Config source | `application.yaml` | `environment:` block | `env:` in deployment yaml |

Same JAR. Only config changes across environments.

---

## Bugs Found Across the System

| # | Bug | Location | Fix |
|---|---|---|---|
| 1 | `/register` and `/login` missing from `permitAll()` | `user-service SecurityConfig` | Add both to `requestMatchers().permitAll()` |
| 2 | `findById` redeclared unnecessarily | `UserRepo` | Remove — already inherited from JpaRepository |
| 3 | No `handleCreditSuccess()` listener | `TransactionEventConsumer` | Add listener, mark transaction SUCCESS |
| 4 | `Transaction.status` is plain String | `Transaction` entity | Replace with enum + `@Enumerated(EnumType.STRING)` |
| 5 | Wrong account-service URL in Docker Compose | `docker-compose.yml` | Change to `http://account-service:8082` |
| 6 | No persistent storage for MySQL in K8s | `mysql-deployment.yaml` | Add PersistentVolumeClaim |
| 7 | Passwords hardcoded in K8s YAML | All deployment yamls | Use K8s Secrets |
| 8 | No compensation failure handling | `SagaEventConsumer` | Add DLQ + retry + alerting |

## Production Gaps (Not Bugs, But Missing)

| Gap | What's needed |
|---|---|
| Kafka failure on publish | Outbox Pattern |
| `depends_on` not waiting for readiness | Health checks |
| No `handleCreditSuccess()` | Add listener |
| Direct Kafka publish without guarantee | Outbox Pattern |
| No API Gateway | Single entry point, JWT validation at edge |
| No centralized logging | ELK stack or similar |
| No distributed tracing | Zipkin / Jaeger — trace requests across services |