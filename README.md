# DNS Shield - Microservices DNS Filtering Service

A cloud-native DNS filtering service built with Quarkus that provides DNS-over-HTTP (DoH) resolution
with threat detection, content filtering, and AI-powered security analysis.

## Features

- **DNS over HTTP (DoH)** - RFC 8484 compliant DNS resolution
- **Content Filtering** - Block ads, tracking, malware, phishing, and custom domains
- **AI Security Agent** - Intelligent threat analysis powered by LLM
- **Real-time Statistics** - Comprehensive metrics and monitoring
- **Web Dashboard** - Modern UI for managing filters and monitoring
- **Event Streaming** - Kafka integration for security events
- **Container Ready** - Docker and Kubernetes deployment support
- **Secure by Default** - JWT authentication with role-based access control

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker (optional, for containerized deployment)
- Kubernetes cluster (optional, for k8s deployment)

### Running Locally

```bash
# Development mode with hot reload
./mvnw quarkus:dev

# The application will be available at:
# - Frontend UI: http://localhost:8080/ui
# - Metrics: http://localhost:8080/metrics
```

### Building

```bash
# Build the application
./mvnw package

# Build native executable (requires GraalVM)
./mvnw package -Dnative

# Build container image
./mvnw package -Dquarkus.container-image.build=true
```

## Frontend Access

| Page         | URL                              | Description                   | Auth Required               |
|--------------|----------------------------------|-------------------------------|-----------------------------|
| Dashboard    | http://localhost:8080/ui         | Overview and quick DNS lookup | No                          |
| Filter Rules | http://localhost:8080/ui/filters | Manage blocking/allow rules   | View: No, Edit: Yes (admin) |
| DNS Query    | http://localhost:8080/ui/query   | Test DNS resolution           | No                          |
| AI Agent     | http://localhost:8080/ui/agent   | AI-powered threat analysis    | Yes (user/admin)            |
| Admin Panel  | http://localhost:8080/ui/admin   | System administration         | Yes (admin only)            |

## API Endpoints

### DNS Resolution (Public Access)

| Method | Path                                            | Description           |
|--------|-------------------------------------------------|-----------------------|
| GET    | `/dns-query?dns={base64}`                       | DoH GET (RFC 8484)    |
| POST   | `/dns-query`                                    | DoH POST (RFC 8484)   |
| GET    | `/api/v1/dns/query?domain={domain}&type={type}` | JSON DNS query        |
| POST   | `/api/v1/dns/query`                             | JSON DNS query (POST) |
| POST   | `/api/v1/dns/batch`                             | Batch DNS queries     |

### Filter Management (Authenticated)

| Method | Path                          | Description           | Roles       |
|--------|-------------------------------|-----------------------|-------------|
| GET    | `/api/v1/filters`             | List all filter rules | admin, user |
| GET    | `/api/v1/filters/{id}`        | Get specific rule     | admin, user |
| POST   | `/api/v1/filters`             | Create new rule       | admin       |
| PUT    | `/api/v1/filters/{id}`        | Update rule           | admin       |
| DELETE | `/api/v1/filters/{id}`        | Delete rule           | admin       |
| PATCH  | `/api/v1/filters/{id}/toggle` | Toggle rule enabled   | admin       |

### Administration (Authenticated)

| Method | Path                           | Description         | Roles       |
|--------|--------------------------------|---------------------|-------------|
| GET    | `/api/v1/admin/stats`          | Overall statistics  | admin, user |
| GET    | `/api/v1/admin/cache/stats`    | Cache statistics    | admin, user |
| POST   | `/api/v1/admin/cache/clear`    | Clear expired cache | admin       |
| GET    | `/api/v1/admin/security/stats` | Security statistics | admin, user |

### AI Agent (Authenticated)

| Method | Path                              | Description                | Roles       |
|--------|-----------------------------------|----------------------------|-------------|
| POST   | `/api/v1/agent/analyze`           | Analyze domain threat      | admin, user |
| POST   | `/api/v1/agent/recommend-filters` | Get filter recommendations | admin       |
| POST   | `/api/v1/agent/correlate-events`  | Correlate security events  | admin       |

## Security & Authentication

This application uses **JWT-based authentication** with role-based access control (RBAC).

### Roles

- **admin** - Full access to all features including system administration
- **user** - Read access to most features, limited write access

### Public Endpoints

- DNS resolution endpoints (`/dns-query`)
- Static resources (CSS, JS)
- Frontend dashboard (view only)
- Public metrics

### Protected Endpoints

- Admin panel (`/ui/admin`) - Admin only
- AI Agent features - User/Admin
- Filter management (write operations) - Admin only
- System administration - Admin only

**📖 See [AUTHENTICATION.md](AUTHENTICATION.md) for complete authentication guide, token generation,
and security best practices.**

## Use Cases

### 1. Block Tracking Domains (Requires Admin Token)

```bash
curl -X POST http://localhost:8080/api/v1/filters \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Block Analytics Tracker",
    "pattern": "*.analytics.example.com",
    "type": "BLOCK",
    "category": "tracking",
    "priority": 90
  }'
```

### 2. Query DNS via JSON API (Public)

```bash
curl "http://localhost:8080/api/v1/dns/query?domain=google.com&type=A"
```

### 3. Analyze Domain for Threats (Requires User/Admin Token)

```bash
curl -X POST http://localhost:8080/api/v1/agent/analyze \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"domain": "suspicious-site.com"}'
```

### 4. View Statistics (Requires User/Admin Token)

```bash
curl http://localhost:8080/api/v1/admin/stats \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Configuration

Key configuration options in `application.properties`:

```properties
# Server
quarkus.http.port=8080
# Database
quarkus.datasource.db-kind=mariadb
# Kafka
mp.messaging.outgoing.dns-query-logs.topic=dns-query-logs
mp.messaging.outgoing.dns-security-alerts.topic=dns-security-alerts
# Metrics
quarkus.micrometer.export.prometheus.path=/metrics
# Security
quarkus.security.jaxrs.deny-unannotated-endpoints=false
```

## Kubernetes Deployment

```bash
# Generate Kubernetes manifests
./mvnw package -Dquarkus.kubernetes.deploy=true

# Apply to cluster
kubectl apply -f target/kubernetes/kubernetes.yml
```

## Monitoring

The application exposes Prometheus metrics at `/metrics`. Key metrics include:

- `dns_query_count` - Total DNS queries processed
- `dns_query_resolution_seconds` - Query resolution time
- `dns_filter_checks` - Filter rule evaluations
- `dns_security_threats_detected` - Detected threats

## Project Structure

```
src/
├── main/
│   ├── java/dev/pacr/dns/
│   │   ├── api/          # REST endpoints (FrontendResource, AdminResource, etc.)
│   │   ├── agent/        # AI agent
│   │   ├── messaging/    # Kafka messaging
│   │   ├── model/        # Data models
│   │   ├── service/      # Business logic
│   │   └── util/         # Utilities
│   ├── resources/
│   │   ├── META-INF/resources/  # Static files (CSS, JS)
│   │   ├── templates/           # Qute templates (dashboard, admin, etc.)
│   │   └── application.properties
│   ├── docker/           # Dockerfiles
│   └── kubernetes/       # K8s manifests
└── test/
    └── java/             # Unit/Integration tests
```

## Development Tips

### Running Without Authentication (Development Only)

For easier testing during development, authentication is currently set to permissive mode. For
production:

1. Generate JWT signing keys
2. Configure JWT issuer and public key
3. Implement login mechanism in frontend
4. Enable strict authentication enforcement

See [AUTHENTICATION.md](AUTHENTICATION.md) for production security setup.

### Hot Reload

Run in dev mode for automatic code reloading:

```bash
./mvnw quarkus:dev
```

### Testing

```bash
# Run all tests
./mvnw test

# Run specific test
./mvnw test -Dtest=DNSServiceIntegrationTest
```

## Architecture

```
┌─────────────────┐     ┌──────────────────────────────────────────┐
│   Web Browser   │────▶│           DNS Shield Service             │
│   (Frontend)    │     │  ┌─────────────────────────────────────┐ │
└─────────────────┘     │  │         REST API Layer              │ │
                        │  │  /ui/* - Frontend (Auth: Mixed)     │ │
┌─────────────────┐     │  │  /dns-query - DoH (Public)          │ │
│   DNS Clients   │────▶│  │  /api/v1/* - JSON APIs (Auth: JWT)  │ │
│  (DoH Enabled)  │     │  └─────────────────────────────────────┘ │
└─────────────────┘     │  ┌─────────────────────────────────────┐ │
                        │  │         Service Layer               │ │
┌─────────────────┐     │  │  DNSOrchestrator, DNSResolver,      │ │
│  Kafka Events   │◀───▶│  │  DNSFilterService, SecurityService  │ │
│                 │     │  └─────────────────────────────────────┘ │
└─────────────────┘     │  ┌─────────────────────────────────────┐ │
                        │  │         AI Agent Layer              │ │
                        │  │  Threat Analysis, Recommendations   │ │
                        │  └─────────────────────────────────────┘ │
                        └──────────────────────────────────────────┘
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

MIT License

## Support

For issues and questions:

- Open an issue on GitHub
- Check [AUTHENTICATION.md](AUTHENTICATION.md) for auth-related questions
- Review Quarkus documentation at https://quarkus.io

