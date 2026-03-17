[![LinkedIn][linkedin-shield]][linkedin-url]

[![CI][ci-shield]][ci-url]

<br />
<div align="center">
<h3 align="center">Payment Service</h3>

  <p align="center">
    Event-driven microservice responsible for payment authorization within the Order Fulfillment Platform.
    <br />
    <br />
    <a href="https://github.com/order-fulfillment-platform">View Organization</a>
  </p>
</div>

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about">About</a></li>
    <li><a href="#built-with">Built With</a></li>
    <li><a href="#architecture">Architecture</a></li>
    <li><a href="#api">API</a></li>
    <li><a href="#events">Events</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

## About

The Payment Service is responsible for authorizing payments within the Order Fulfillment Platform. It consumes `STOCK_RESERVED` events from Kafka, processes payment authorization and publishes the result as a domain event using the **Outbox Pattern**.

The service implements an **idempotency check** to prevent double payments — if a payment already exists for a given order, the event is silently discarded.

In this MVP implementation, payment authorization is mocked: orders below €1000 are authorized, orders above €1000 are rejected.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Built With

[![Spring Boot][springboot-shield]][springboot-url]
[![Apache Kafka][kafka-shield]][kafka-url]
[![PostgreSQL][postgres-shield]][postgres-url]
[![Docker][docker-shield]][docker-url]
[![Java][java-shield]][java-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Architecture

### Event Flow
```
[Kafka] stock.reserved
        │
        ▼
StockReservedConsumer
        │
        ▼
PaymentService
  → idempotency check (skip if payment already exists)
  → mock payment authorization
      amount < 1000 → AUTHORIZED
      amount >= 1000 → FAILED
  → save payment record
        │
        ▼
[Transaction]
  INSERT payments
  INSERT outbox_events
        │
        ▼
@Scheduled every 5s
  → publish PAYMENT_AUTHORIZED or PAYMENT_FAILED to Kafka
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## API

| Method | Endpoint | Description |
|---|---|---|
| GET | /api/v1/payments/order/{orderId} | Get payment status by order ID |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Events

### Consumed

| Topic | Event | Description |
|---|---|---|
| stock.reserved | STOCK_RESERVED | Triggers payment authorization process |

### Published

| Topic | Event | Description |
|---|---|---|
| payment.authorized | PAYMENT_AUTHORIZED | Payment successfully authorized |
| payment.failed | PAYMENT_FAILED | Payment authorization failed |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- Docker

### Run with Docker Compose

Start the full platform from the [infrastructure](https://github.com/order-fulfillment-platform/infrastructure) repository:
```bash
docker-compose up -d --build
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Contact

Eros Burelli — [LinkedIn](https://www.linkedin.com/in/eros-burelli-a458b1145/)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS -->
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/eros-burelli-a458b1145/
[springboot-shield]: https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white
[springboot-url]: https://spring.io/projects/spring-boot
[kafka-shield]: https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white
[kafka-url]: https://kafka.apache.org/
[postgres-shield]: https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white
[postgres-url]: https://www.postgresql.org/
[docker-shield]: https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white
[docker-url]: https://www.docker.com/
[java-shield]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[java-url]: https://www.java.com/
[ci-shield]: https://github.com/order-fulfillment-platform/payment-service/actions/workflows/ci.yml/badge.svg
[ci-url]: https://github.com/order-fulfillment-platform/payment-service/actions/workflows/ci.yml