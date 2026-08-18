# Todo Backend

A Spring Boot REST API for user authentication and todo management, with JWT-based security, SQL Server persistence, Docker support, and Kubernetes/Helm deployment.

## Tech Stack

- Java 21
- Spring Boot 3.5.3
- Spring Web
- Spring Security
- Spring Data JPA / Hibernate
- Microsoft SQL Server
- JWT (JJWT)
- BCrypt
- Maven
- Docker / Docker Compose
- Kubernetes / Helm
- NGINX Ingress
- cert-manager

## Features

- User registration and login
- BCrypt password hashing
- Stateless JWT authentication
- User-specific todo isolation
- Todo CRUD operations
- SQL Server persistence
- Dockerized application
- Two-replica Kubernetes deployment
- Persistent SQL Server storage
- Kubernetes Ingress with TLS
- cert-manager-managed development certificate

## Project Structure

```text
todo-backend/
├── src/
│   ├── main/java/com/neeraj/todo_backend/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   └── main/resources/application.properties
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── .env.example

todo-k8s/
├── todo-app/                 # Helm chart
│   ├── Chart.yaml
│   ├── values.yaml
│   └── templates/
├── todo-secret.example.yaml  # Template only; contains no real secret
└── ...
```

## Configuration and Secrets

Secrets are intentionally not stored in source control.

The application expects:

| Variable | Purpose |
|---|---|
| `DB_URL` | JDBC connection string |
| `DB_USER` | Database username |
| `DB_PASS` | Database password |
| `ENCRYPTION_KEY` | AES key; 16, 24, or 32 characters |
| `JWT_SECRET` | Base64-encoded random key of at least 256 bits |
| `DB_DIALECT` | Hibernate dialect |
| `HIBERNATE_DDL_AUTO` | Hibernate schema strategy |
| `SERVER_PORT` | HTTP port |

Copy `.env.example` to `.env` for local development and replace the placeholders. `.env` is ignored by Git.

Generate a strong JWT secret with OpenSSL:

```bash
openssl rand -base64 32
```

For the AES key, generate 16, 24, or 32 random bytes and use an appropriate encoded/managed value. Do not commit the key.

> **Security:** If credentials or keys from an older version of this project were ever committed to Git, rotate them. Removing them from the latest commit does not remove them from Git history.

## Run Locally with Maven

Prerequisites:

- JDK 21
- Maven 3.9+ (or use the Maven wrapper)
- SQL Server

Set the required environment variables, then run:

```bash
./mvnw clean spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd clean spring-boot:run
```

The API runs on `http://localhost:9090` by default.

## Run with Docker Compose

Create `.env` from `.env.example` and set at least:

```text
DB_PASS=<strong-password>
ENCRYPTION_KEY=<16-or-24-or-32-character-key>
JWT_SECRET=<base64-encoded-256-bit-secret>
```

Then:

```bash
docker compose up --build
```

This starts:

- Todo Backend on port `9090`
- SQL Server on port `1433`

The SQL Server data is stored in a named Docker volume.

## API

### Register

```http
POST /api/auth/register
Content-Type: application/json
```

Example:

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "passwordHash": "password123"
}
```

### Login

```http
POST /api/login
Content-Type: application/json
```

Example:

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

The response contains a JWT. Send it with protected requests:

```http
Authorization: Bearer <JWT_TOKEN>
```

### Todo endpoints

| Method | Endpoint | Authentication |
|---|---|---|
| `POST` | `/api/todos` | Required |
| `GET` | `/api/todos` | Required |
| `GET` | `/api/todos/{id}` | Required |
| `PUT` | `/api/todos/{id}` | Required |
| `DELETE` | `/api/todos/{id}` | Required |

## Kubernetes / Helm

The Helm chart is under `todo-k8s/todo-app`.

### 1. Create the Kubernetes Secret

Do not put real credentials in `values.yaml` or commit a Secret containing real values.

You can use the example file:

```bash
cp todo-k8s/todo-secret.example.yaml todo-k8s/todo-secret.yaml
```

Replace the placeholder values, then apply it:

```bash
kubectl apply -f todo-k8s/todo-secret.yaml
```

Delete the local file when it is no longer needed:

```bash
rm todo-k8s/todo-secret.yaml
```

For production, prefer a secrets manager or External Secrets rather than storing a Secret manifest locally.

### 2. Install the chart

```bash
helm upgrade --install todo-app ./todo-k8s/todo-app
```

Check the deployment:

```bash
kubectl get pods
kubectl get svc
kubectl get ingress
```

The backend is configured with two replicas.

### 3. TLS

The chart uses cert-manager with a self-signed `ClusterIssuer` for the development hostname `todo.local`.

The TLS private key is generated and stored by Kubernetes/cert-manager; it is **not** stored in Git.

A self-signed certificate is suitable for local development/testing, not public production traffic.

## Kubernetes Architecture

```text
                    NGINX Ingress
                          |
                          v
                 todo-backend Service
                    /           \
                   v             v
            Backend Pod 1   Backend Pod 2
                    \           /
                     \         /
                      v       v
                    SQL Server
                        |
                        v
                 Persistent Volume
```

The backend receives database credentials and security keys through Kubernetes Secrets. Non-sensitive connection configuration is provided through a ConfigMap.

## Testing

Run tests:

```bash
./mvnw test
```

Run the complete Maven verification:

```bash
./mvnw clean verify
```

## Security Notes

- Never commit `.env` files.
- Never commit Kubernetes Secret manifests containing real values.
- Never commit private TLS keys or certificates.
- JWT signing keys are externally configured so multiple backend replicas can validate the same token.
- AES encryption keys are externally configured instead of being hard-coded in Java source.
- Rotate any credentials that were previously exposed in Git history.
- For production, use a dedicated secret-management solution and a trusted TLS certificate.

## Future Improvements

- Flyway or Liquibase database migrations
- OpenAPI/Swagger documentation
- Liveness and readiness probes
- Centralized exception handling
- Request validation
- Structured logging and distributed tracing
- Kubernetes resource requests/limits and HPA
- External Secrets / cloud secret manager
- Refresh tokens and key rotation
- CI/CD with GitHub Actions

## Author

**Neeraj Mathur**

Java Backend Engineer | Spring Boot | REST APIs | Microservices | Docker | Kubernetes

## Repository Layout

The repository keeps the application and Kubernetes deployment configuration as separate directories:

```text
.
├── README.md
├── todo-backend/     # Spring Boot application
└── todo-k8s/         # Kubernetes manifests and Helm chart
```
