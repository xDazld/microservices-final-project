# DNS Filtering and Security Service

A comprehensive microservices-based DNS service providing DNS resolution, filtering, and security
features similar to AdGuard Home, NextDNS, and Control D. This service allows users to configure
custom DNS filtering rules, monitor DNS queries in real-time, and receive security alerts for
potential threats.

## Features

- **DNS Resolution Service**: High-performance DNS query handling with support for standard DNS
  protocols
- **Filtering Service**: Custom filtering rules engine with blocklists, allowlists, and pattern
  matching
- **Logging Service**: Real-time DNS query logging with Kafka event streaming
- **Administrative Service**: REST API for configuration, monitoring, and statistics
- **Security Features**: Threat intelligence integration, malware detection, and security alerts
- **Monitoring & Metrics**: Prometheus metrics with Grafana dashboards for visualization
- **Scalable Architecture**: Kubernetes orchestration for automatic scaling and high availability
- **Authentication**: JWT-based authentication and authorization for all APIs

## Architecture

This application consists of several microservices:

1. **DNS Resolution Service** - Handles DNS queries and responses
2. **Filtering Service** - Applies filtering rules to DNS queries
3. **Logging Service** - Records DNS queries and responses via Kafka
4. **Administrative Service** - Provides REST API for management
5. **Statistics Service** - Aggregates and provides usage statistics

## Technology Stack

- **Framework**: Quarkus (Java 21)
- **Messaging**: Apache Kafka for event streaming
- **Database**: MariaDB/MySQL for configuration and logs
- **Authentication**: SmallRye JWT
- **Orchestration**: Kubernetes
- **Monitoring**: Prometheus + Grafana + OpenTelemetry
- **Containerization**: Docker/Podman

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
mvn quarkus:dev
```

> **_NOTE:_** Quarkus now ships with a Dev UI, which is available in dev mode only
> at <http://localhost:8080/q/dev/>.

## Running with Docker Compose

The easiest way to run the complete stack is using Docker Compose:

```bash
# Build the application
mvn package

# Start all services (DNS service, MariaDB, Kafka, Prometheus, Grafana)
docker-compose up -d

# View logs
docker-compose logs -f dns-service

# Stop all services
docker-compose down
```

### Service URLs:

- **DNS Service API**: http://localhost:8080/api/v1
- **Grafana Dashboard**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090
- **Metrics Endpoint**: http://localhost:8080/metrics

## Deploying to Kubernetes

### Prerequisites:

- Kubernetes cluster (Minikube, Kind, or cloud provider)
- kubectl configured

### Deployment steps:

```bash
# Build the container image
mvn package -Dquarkus.container-image.build=true

# Apply Kubernetes configurations
kubectl apply -f kubernetes/deployment.yml
kubectl apply -f kubernetes/monitoring.yml

# Check deployment status
kubectl get pods -n dns-service
kubectl get services -n dns-service

# Access the service
kubectl port-forward -n dns-service service/dns-service 8080:8080

# Scale the deployment
kubectl scale deployment dns-service-deployment --replicas=5 -n dns-service

# Delete the deployment
kubectl delete namespace dns-service
```

## API Endpoints

### DNS Resolution

- `POST /api/v1/dns/resolve` - Resolve a single DNS query
- `POST /api/v1/dns/resolve/batch` - Batch resolve multiple domains

Example:

```bash
curl -X POST http://localhost:8080/api/v1/dns/resolve \
  -H "Content-Type: application/json" \
  -d '{"domain": "example.com", "queryType": "A"}'
```

### Filter Management (Admin only)

- `GET /api/v1/filters` - Get all filter rules
- `POST /api/v1/filters` - Create a new filter rule
- `PUT /api/v1/filters/{id}` - Update a filter rule
- `DELETE /api/v1/filters/{id}` - Delete a filter rule
- `GET /api/v1/filters/stats` - Get filtering statistics

Example:

```bash
# Create a blocking rule
curl -X POST http://localhost:8080/api/v1/filters \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Block Ads",
    "pattern": "*.ads.example.com",
    "type": "BLOCK",
    "category": "ads",
    "priority": 100
  }'
```

### Administration

- `GET /api/v1/admin/stats` - Get overall statistics
- `GET /api/v1/admin/cache/stats` - Get cache statistics
- `POST /api/v1/admin/cache/clear` - Clear expired cache
- `POST /api/v1/admin/security/analyze` - Analyze domain for threats
- `GET /api/v1/admin/security/threats/domains` - Get malicious domains
- `GET /api/v1/admin/health` - Health check endpoint

Example:

```bash
# Get overall statistics
curl http://localhost:8080/api/v1/admin/stats
```

## Features in Detail

### DNS Resolution

- High-performance DNS query resolution
- Support for A, AAAA, and other record types
- Built-in caching with configurable TTL
- Response time tracking

### Filtering Engine

- **Pattern Matching**: Wildcard support (e.g., `*.ads.com`)
- **Rule Types**: Block, Allow, Redirect
- **Categories**: ads, tracking, malware, custom
- **Priority System**: Higher priority rules evaluated first
- **Real-time Updates**: Rules can be modified without restart

### Security Features

- **Threat Intelligence**: Malicious domain database
- **Heuristic Analysis**: DGA detection, suspicious patterns
- **Security Alerts**: Real-time alerts via Kafka
- **IP Reputation**: Check resolved IPs against threat lists

### Logging & Monitoring

- **Kafka Integration**: Real-time event streaming
- **Prometheus Metrics**: Query rates, response times, cache hits
- **Grafana Dashboards**: Visual monitoring and analytics
- **OpenTelemetry**: Distributed tracing support

### Scalability

- **Kubernetes Orchestration**: Auto-scaling based on load
- **Horizontal Pod Autoscaler**: CPU and memory-based scaling
- **Load Balancing**: Distribute traffic across replicas
- **Stateless Design**: Easy to scale horizontally

## Configuration

Key configuration options in `application.properties`:

```properties
# HTTP Port
quarkus.http.port=8080
# Kafka
kafka.bootstrap.servers=localhost:9092
# Database
quarkus.datasource.jdbc.url=jdbc:mariadb://localhost:3306/dnsservice
quarkus.datasource.username=dnsuser
quarkus.datasource.password=dnspassword
# Monitoring
quarkus.micrometer.export.prometheus.enabled=true
quarkus.otel.enabled=true
# Kubernetes
quarkus.kubernetes.replicas=2
quarkus.kubernetes.service-type=LoadBalancer
```

## Performance Testing

Test the service under load:

```bash
# Install Apache Bench or similar tool
# Test single query performance
ab -n 1000 -c 10 -p query.json -T application/json \
  http://localhost:8080/api/v1/dns/resolve

# query.json content:
# {"domain": "example.com", "queryType": "A"}
```

## Monitoring and Metrics

### Key Metrics Available:

- `dns_query_count_total` - Total DNS queries processed
- `dns_query_resolution_seconds` - DNS resolution time histogram
- `dns_filter_checks` - Number of filter checks performed
- `dns_filter_matches_total` - Filter matches by type and category
- `dns_security_threats_detected_total` - Security threats detected

### Grafana Dashboard

Access the pre-configured dashboard at http://localhost:3000

- Default credentials: admin/admin
- Dashboard shows: query rates, blocked queries, response times, cache hit rate

## Security

### Authentication

The service uses JWT for authentication. Admin endpoints require admin role.

### Production Recommendations:

1. Change default passwords in `docker-compose.yml` and Kubernetes secrets
2. Configure proper JWT keys in production
3. Enable TLS/HTTPS for all endpoints
4. Use network policies in Kubernetes
5. Implement rate limiting
6. Regular security updates

## Troubleshooting

### Common Issues:

**Service won't start:**

```bash
# Check logs
docker-compose logs dns-service
kubectl logs -n dns-service -l app=dns-filtering-service
```

**Database connection issues:**

```bash
# Check database status
docker-compose exec mariadb mysql -u dnsuser -p
```

**Kafka issues:**

```bash
# Check Kafka topics
docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092
```

## Development

### Running tests:

```bash
mvn test
```

### Building native image:

```bash
mvn package -Dnative
```

### Hot reload in dev mode:

Changes to Java code are automatically reloaded in dev mode.

## Contributing

This is an academic project. Contributions should follow the project requirements:

- Maintain microservices architecture
- Ensure proper API documentation
- Add tests for new features
- Update Grafana dashboards for new metrics

## License

Educational project for Microservices & Cloud Computing course.

## Packaging and running the application

The application can be packaged using:

```shell script
mvn package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the
`target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
mvn package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
mvn package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container
using:

```shell script
mvn package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/final-project-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please
consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- Messaging ([guide](https://quarkus.io/guides/messaging)): Produce and consume messages and
  implement event driven and data streaming applications
- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time
  processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or
  any of the extensions that depend on it.
- Container Image Podman ([guide](https://quarkus.io/guides/container-image)): Build container
  images of your application using Podman
- Messaging - Kafka Connector ([guide](https://quarkus.io/guides/kafka-getting-started)): Connect to
  Kafka with Reactive Messaging
- JDBC Driver - MariaDB ([guide](https://quarkus.io/guides/datasource)): Connect to the MariaDB
  database via JDBC
- SmallRye JWT ([guide](https://quarkus.io/guides/security-jwt)): Secure your applications with JSON
  Web Token
- Observability ([guide](https://quarkus.io/guides/observability-devservices-lgtm)): Serve and
  consume Observability Dev Services
- Reactive MySQL client ([guide](https://quarkus.io/guides/reactive-sql-clients)): Connect to the
  MySQL database using the reactive pattern
- REST JSON-B ([guide](https://quarkus.io/guides/rest#json-serialisation)): JSON-B serialization
  support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or
  any of the extensions that depend on it.
- REST Qute ([guide](https://quarkus.io/guides/qute-reference#rest_integration)): Qute integration
  for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of
  the extensions that depend on it.
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): Jackson serialization
  support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or
  any of the extensions that depend on it
- Kubernetes Client ([guide](https://quarkus.io/guides/kubernetes-client)): Interact with Kubernetes
  and develop Kubernetes Operators
- OpenTelemetry ([guide](https://quarkus.io/guides/opentelemetry)): Use OpenTelemetry to trace
  services
- Micrometer OpenTelemetry
  Bridge ([guide](https://quarkus.io/guides/telemetry-micrometer-to-opentelemetry)): Micrometer
  registry implemented by the OpenTelemetry SDK
- Apache Kafka Streams ([guide](https://quarkus.io/guides/kafka-streams)): Implement stream
  processing applications based on Apache Kafka
- REST JAXB ([guide](https://quarkus.io/guides/resteasy-reactive#xml-serialisation)): JAXB
  serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy
  extension, or any of the extensions that depend on it.
- Reactive Routes ([guide](https://quarkus.io/guides/reactive-routes)): REST framework offering the
  route model to define non blocking endpoints
- Minikube ([guide](https://quarkus.io/guides/kubernetes)): Generate Minikube resources from
  annotations
- Apache Kafka Client ([guide](https://quarkus.io/guides/kafka)): Connect to Apache Kafka with its
  native API
- Java Flight Recorder (JFR) ([guide](https://quarkus.io/guides/jfr)): Monitor your applications
  with Java Flight Recorder
- SmallRye Metrics ([guide](https://quarkus.io/guides/smallrye-metrics)): Expose metrics for your
  services
- Kubernetes ([guide](https://quarkus.io/guides/kubernetes)): Generate Kubernetes resources from
  annotations
- Micrometer metrics ([guide](https://quarkus.io/guides/micrometer)): Instrument the runtime and
  your application with dimensional metrics using Micrometer.
- Micrometer Registry Prometheus ([guide](https://quarkus.io/guides/micrometer)): Enable Prometheus
  support for Micrometer
- REST Links ([guide](https://quarkus.io/guides/resteasy-reactive#web-links-support)): Web Links
  support for Quarkus REST. Inject web links into response HTTP headers by annotating your endpoint
  resources.
- WebSockets Client ([guide](https://quarkus.io/guides/websockets)): Client for WebSocket
  communication channel
- WebSockets ([guide](https://quarkus.io/guides/websockets)): WebSocket communication channel
  support
- Kubernetes Config ([guide](https://quarkus.io/guides/kubernetes-config)): Read runtime
  configuration from Kubernetes ConfigMaps and Secrets
- LangChain4j Agentic ([guide](https://docs.quarkiverse.io/quarkus-langchain4j/dev/index.html)):
  Provides integration with LangChain4j's Agentic module

## Provided Code

### LGTM Observability

Create your first LGTM Observability application

[Related guide section...](https://quarkus.io/guides/observability-devservices-lgtm)

### Messaging codestart

Use Quarkus Messaging

[Related Apache Kafka guide section...](https://quarkus.io/guides/kafka-reactive-getting-started)

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)

### REST Qute

Create your web page using Quarkus REST and Qute

[Related guide section...](https://quarkus.io/guides/qute#type-safe-templates)

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)

### WebSockets

WebSocket communication channel starter code

[Related guide section...](https://quarkus.io/guides/websockets)
