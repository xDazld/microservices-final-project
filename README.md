# final-project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only
> at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the
`target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container
using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
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
