# ⚡ Asynchronous Event-Driven E-Commerce Platform

A full-stack e-commerce application built to demonstrate **asynchronous, event-driven backend architecture** using **Spring Boot, Apache Kafka, PostgreSQL, React, TypeScript, and Docker**.

The system separates the immediate responsibilities of an HTTP request from downstream business processing. Orders are persisted through a REST API and then propagated through Kafka events, allowing payment, shipping, and notification workflows to operate asynchronously and independently.

---

## 📌 Table of Contents

- [Overview](#-overview)
- [Why This Project](#-why-this-project)
- [Architecture](#-architecture)
- [End-to-End Event Flow](#-end-to-end-event-flow)
- [Order Creation Flow](#1-order-creation-flow)
- [Payment Initialization](#2-payment-initialization)
- [Payment Completion](#3-payment-completion)
- [Shipping and Notification](#4-shipping-and-notification)
- [Idempotent Payment Processing](#-idempotent-payment-processing)
- [Kafka Consumer Groups](#-kafka-consumer-groups)
- [Retry and Dead Letter Handling](#-retry-and-dead-letter-handling)
- [Database Design](#-database-design)
- [Order and Payment Lifecycle](#-order-and-payment-lifecycle)
- [Frontend](#-frontend)
- [Backend](#-backend)
- [REST API](#-rest-api)
- [Project Structure](#-project-structure)
- [Technology Stack](#-technology-stack)
- [Running the Project](#-running-the-project)
- [Testing the Event Flow](#-testing-the-event-flow)
- [Engineering Concepts](#-engineering-concepts-demonstrated)
- [Future Improvements](#-future-improvements)
- [Resume Description](#-resume-description)
- [Author](#-author)

---

# 🚀 Overview

The application simulates an e-commerce order-processing system where an order passes through several independent stages.

Instead of making one large synchronous request perform every operation, the system uses **Apache Kafka as an event backbone**.

The simplified workflow is:

```text
                        ┌──────────────────────┐
                        │      React UI        │
                        │  TypeScript + Vite   │
                        └──────────┬───────────┘
                                   │
                              HTTP / REST
                                   │
                                   ▼
                        ┌──────────────────────┐
                        │    Spring Boot      │
                        │      REST API       │
                        └──────────┬───────────┘
                                   │
                         ┌─────────┴─────────┐
                         │                   │
                         ▼                   ▼
                  ┌────────────┐       ┌────────────┐
                  │ PostgreSQL │       │   Kafka    │
                  └────────────┘       └─────┬──────┘
                                             │
                         ┌───────────────────┼───────────────────┐
                         │                   │                   │
                         ▼                   ▼                   ▼
                   Payment Consumer    Shipping Consumer   Notification
                                                            Consumer
```

The important architectural idea is that the components communicate through **events rather than direct synchronous calls** for the asynchronous parts of the workflow.

---

# 🎯 Why This Project?

Traditional synchronous order processing might look like:

```text
Client
  ↓
Create Order
  ↓
Process Payment
  ↓
Start Shipping
  ↓
Send Notification
  ↓
Return Response
```

This tightly couples multiple operations to the same request.

This project explores an event-driven alternative:

```text
Client
  ↓
Create Order
  ↓
Save Order
  ↓
Publish Event
  ↓
Return / Continue
        │
        ▼
      Kafka
        │
        ├── Payment
        ├── Shipping
        └── Notification
```

Kafka acts as the event transport layer between independent processing components.

This makes the individual workflows easier to separate and allows consumers to process events independently.

---

# 🏗️ Architecture

## High-Level Architecture

> **ADD YOUR FINAL ARCHITECTURE DIAGRAM HERE**

You can place your architecture image in:

```text
docs/architecture.png
```

and reference it here:

```markdown
![System Architecture](docs/architecture.png)
```

### Architecture Diagram Placeholder

```text
┌──────────────────────────────────────────────────────────────────────┐
│                         FRONTEND                                    │
│                    React + TypeScript                               │
│                                                                      │
│  Product Selection → Create Order → Payment → Order Tracking       │
└───────────────────────────────┬──────────────────────────────────────┘
                                │
                                │ REST API
                                ▼
┌──────────────────────────────────────────────────────────────────────┐
│                         SPRING BOOT                                 │
│                                                                      │
│  OrderController      PaymentController      OrderService          │
│        │                     │                      │                │
│        └─────────────────────┼──────────────────────┘                │
│                              │                                       │
└──────────────────────────────┼───────────────────────────────────────┘
                               │
                 ┌─────────────┴──────────────┐
                 │                            │
                 ▼                            ▼
          ┌──────────────┐             ┌──────────────┐
          │ PostgreSQL   │             │    Kafka     │
          │              │             │              │
          │ Orders       │             │ orders       │
          │ OrderItems   │             │ payment-     │
          │ Products     │             │ complete     │
          │ Payments     │             │ payment-     │
          └──────────────┘             │ successful   │
                                       └──────┬───────┘
                                              │
                            ┌─────────────────┼──────────────────┐
                            │                 │                  │
                            ▼                 ▼                  ▼
                    Payment Consumer   Shipping Consumer   Notification
                                                            Consumer
```

---

# 🔄 End-to-End Event Flow

The complete application flow can be represented as:

```text
PLACE ORDER
     │
     ▼
OrderController
     │
     ▼
OrderService
     │
     ├── Build Order
     ├── Build OrderItems
     ├── Calculate Total
     └── Save Order
             │
             ▼
        PostgreSQL
             │
             ▼
     OrderCreatedEvent
             │
             ▼
       Kafka: orders
             │
             ▼
   PaymentPendingConsumer
             │
             ▼
      Payment = PENDING
             │
             ▼
        PostgreSQL


USER CLICKS PAY NOW
             │
             ▼
PaymentController
             │
             ▼
PaymentCompleteProducer
             │
             ▼
   Kafka: payment-complete
             │
             ▼
      PaymentConsumer
             │
             ├── Payment PENDING → SUCCESS
             ├── Order CREATED → PAID
             │
             ▼
   PaymentSuccessfulEvent
             │
             ▼
   Kafka: payment-successful
             │
             ├───────────────┐
             ▼               ▼
       Shipping         Notification
       Consumer          Consumer
             │               │
             ▼               ▼
       Order → SHIPPED   Notification
```

---

# 1. Order Creation Flow

The frontend sends an order request to the backend:

```http
POST /api/orders
```

Example request:

```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 4,
      "quantity": 1
    }
  ]
}
```

The request reaches:

```text
React
  ↓
POST /api/orders
  ↓
OrderController
  ↓
OrderService
```

The `OrderService`:

1. Creates the `Order`.
2. Looks up each `Product`.
3. Creates `OrderItem` records.
4. Calculates the total amount.
5. Saves the order and its items.
6. Creates an `OrderCreatedEvent`.
7. Publishes the event to Kafka.

The database operation happens before the event is published:

```text
OrderService
    │
    ├── orderRepository.save(order)
    │
    ▼
PostgreSQL
    │
    ▼
OrderCreatedEvent
    │
    ▼
Kafka: orders
```

---

# 2. Payment Initialization

The `PaymentPendingConsumer` listens to the `orders` topic.

```java
@KafkaListener(
    topics = "orders",
    groupId = "payment-pending-group"
)
```

When an `OrderCreatedEvent` arrives, the consumer creates a payment record.

```text
OrderCreatedEvent
       │
       ├── orderId
       ├── totalAmount
       └── createdAt
              │
              ▼
    PaymentPendingConsumer
              │
              ▼
          Payment
              │
       ┌──────┼─────────┐
       ▼      ▼         ▼
    orderId amount    PENDING
              │
              ▼
         PostgreSQL
```

The payment is initially stored as:

```text
PENDING
```

This means that the order exists and the payment record has been initialized, but payment has not yet been completed.

---

# 3. Payment Completion

When the user clicks **PAY NOW**, the frontend sends:

```http
POST /api/payments/{orderId}/complete
```

The backend creates a small event containing the order ID:

```java
new PaymentCompleteEvent(orderId)
```

The event is published to:

```text
payment-complete
```

The flow is:

```text
React
  │
  ▼
PaymentController
  │
  ▼
PaymentCompleteProducer
  │
  ▼
Kafka: payment-complete
  │
  ▼
PaymentConsumer
```

The `PaymentConsumer` uses the `orderId` from the event to retrieve the existing payment from PostgreSQL.

```text
PaymentCompleteEvent
        │
        ▼
     orderId
        │
        ▼
PaymentRepository.findByOrderId(orderId)
        │
        ▼
      Payment
        │
        ▼
PENDING → SUCCESS
```

The corresponding order is then updated:

```text
CREATED → PAID
```

Finally, a `PaymentSuccessfulEvent` is published.

---

# 4. Shipping and Notification

After successful payment:

```text
PaymentSuccessfulEvent
        │
        ▼
Kafka: payment-successful
        │
        ├─────────────────────┐
        │                     │
        ▼                     ▼
ShippingConsumer       NotificationConsumer
        │                     │
        ▼                     ▼
Order → SHIPPED        Notification sent
```

These consumers operate independently.

The notification workflow does not need to call the shipping consumer.

Similarly, the shipping consumer does not need to wait for the notification consumer.

This is one of the main benefits of the event-driven design.

---

# 🔌 Backend Connections

The frontend communicates with the backend through REST APIs.

## Create Order

```text
Frontend
   │
   │ POST /api/orders
   │
   │ {
   │   items: [...]
   │ }
   ▼
Spring Boot
```

The backend responds with an `OrderResponse` containing information such as:

```json
{
  "id": 14,
  "totalAmount": 3999.00,
  "status": "CREATED",
  "createdAt": "...",
  "items": [...]
}
```

The frontend then uses the returned order ID to navigate to the payment page.

---

## Complete Payment

```text
Frontend
   │
   │ POST /api/payments/14/complete
   ▼
Spring Boot
   │
   ▼
Kafka
```

The frontend does not directly update the payment or order in PostgreSQL.

Instead:

```text
Frontend
    ↓
REST API
    ↓
Kafka Event
    ↓
Consumer
    ↓
Database Update
```

---

## Order Status

The frontend periodically requests:

```http
GET /api/orders/status/{orderId}
```

The backend reads the current order status from PostgreSQL.

```text
React
  │
  │ GET /api/orders/status/14
  ▼
OrderController
  │
  ▼
OrderService
  │
  ▼
PostgreSQL
  │
  ▼
OrderStatus
```

This allows the UI to show the asynchronous state transition:

```text
CREATED
   ↓
PAID
   ↓
SHIPPED
```

---

# 🔁 Idempotent Payment Processing

One of the important engineering concepts demonstrated by the project is **idempotency**.

An event may potentially be delivered or processed more than once.

For example:

```text
PaymentCompleteEvent
       │
       ├──────────────► Event #1
       │
       └──────────────► Event #2
```

The payment consumer checks the current payment state:

```java
if (payment.getStatus() == PaymentStatus.SUCCESS) {
    return;
}
```

The first event performs:

```text
PENDING
   ↓
SUCCESS
```

The duplicate event sees:

```text
SUCCESS
```

and does not perform the payment transition again.

Therefore:

```text
First event      → Process
Duplicate event  → Ignore
```

The project also exposes a developer testing endpoint:

```http
POST /api/payments/{orderId}/duplicate
```

This intentionally publishes the same payment event twice to demonstrate the idempotency logic.

---

# 📨 Kafka Consumer Groups

Kafka consumer groups are used to control how events are distributed.

For payment processing:

```text
payment-complete
       │
       ▼
payment-group
       │
   ┌───┼───┐
   ▼   ▼   ▼
  C1  C2  C3
```

If multiple instances use:

```java
groupId = "payment-group"
```

Kafka can distribute partitions among those instances.

For payment initialization:

```text
orders
  │
  ▼
payment-pending-group
  │
  └── PaymentPendingConsumer
```

The `payment-pending-group` and `payment-group` are separate consumer groups with different responsibilities.

---

# 🔐 Kafka Event Design

The project uses separate event types for different stages of the workflow.

## `OrderCreatedEvent`

Represents:

> An order has been created.

Contains information such as:

```text
orderId
totalAmount
createdAt
```

Published to:

```text
orders
```

---

## `PaymentCompleteEvent`

Represents:

> A payment for an existing order should be processed.

Contains:

```text
orderId
```

Published to:

```text
payment-complete
```

---

## `PaymentSuccessfulEvent`

Represents:

> Payment processing completed successfully.

Contains:

```text
orderId
amount
```

Published to:

```text
payment-successful
```

This separation keeps the events focused on the state transition they represent.

---

# 🛡️ Retry and Dead Letter Handling

The backend uses Spring Kafka's `DefaultErrorHandler`.

Current configuration:

```text
Initial attempt
      ↓
Failure
      ↓
Retry #1
      ↓
1 second delay
      ↓
Retry #2
      ↓
1 second delay
      ↓
Failure
      ↓
Dead Letter Topic
```

The application uses:

```java
FixedBackOff(1000L, 2L)
```

which represents a 1-second interval with two retry attempts.

A `DeadLetterPublishingRecoverer` is used to publish unrecoverable records to a corresponding `.DLT` topic.

For example:

```text
payment-complete
       │
       ▼
payment-complete.DLT
```

This provides a mechanism for isolating events that could not be successfully processed.

---

# 🗄️ Database Design

The application uses PostgreSQL with Spring Data JPA and Hibernate.

## Product

```text
Product
──────────────
id
name
price
```

Example:

```text
1 | Mechanical Keyboard | 3999.00
2 | Wireless Mouse      | 1499.00
3 | Monitor              | 15999.00
4 | Headphones           | 7499.00
5 | Mobile               | 24999.00
6 | Tablet               | 29999.00
```

---

## Order

```text
Order
────────────────
id
totalAmount
status
createdAt
```

---

## OrderItem

```text
OrderItem
────────────────
id
quantity
price
product_id
order_id
```

Relationships:

```text
Order
  │
  │ 1
  │
  └─────────── *
             OrderItem
                 │
                 │ *
                 │
                 └────────── 1 Product
```

---

## Payment

```text
Payment
────────────────
id
orderId
amount
status
createdAt
```

The `orderId` is unique:

```text
One Order
   │
   └── One Payment
```

This also supports the idempotency logic by allowing the consumer to find the existing payment for an order.

---

# 📊 Order and Payment Lifecycle

## Order Status

The application uses:

```text
CREATED
PAYMENT_PENDING
PAID
PROCESSING
SHIPPED
DELIVERED
CANCELLED
```

The current implemented flow is primarily:

```text
CREATED
   │
   ▼
PAID
   │
   ▼
SHIPPED
```

---

## Payment Status

```text
PENDING
SUCCESS
FAILED
```

Current implemented success flow:

```text
PENDING
   │
   ▼
SUCCESS
```

---

# 🎨 Frontend

The frontend is implemented using:

- React
- TypeScript
- Vite
- Axios
- React Router
- CSS

The UI provides:

### Product Selection

Users can:

- View products
- Increase quantity
- Decrease quantity
- Review order total
- Place an order

### Payment Page

The payment page provides:

- Order ID
- Payment status
- Pay Now action
- Duplicate event testing
- Placeholder for future failure simulation

### Order Tracking

The order tracking page periodically queries the backend and displays the current order state.

Example:

```text
ORDER #14

✓ CREATED
✓ PAID
✓ SHIPPED

CURRENT STATUS: SHIPPED
```

---

# 🔧 Backend

The backend is organized around standard Spring Boot layers.

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

Kafka producers and consumers extend the architecture:

```text
                    ┌── Kafka Producer
                    │
Controller → Service ┤
                    │
                    └── Repository → PostgreSQL


Kafka Topic
    ↓
Kafka Consumer
    ↓
Service / Repository
    ↓
PostgreSQL
```

Important backend components include:

```text
OrderController
OrderService

PaymentController
PaymentConsumer
PaymentPendingConsumer

ShippingConsumer
NotificationConsumer

OrderProducer
PaymentCompleteProducer
PaymentProducer
```

---

# 🌐 REST API

## Create Order

```http
POST /api/orders
```

Request:

```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 4,
      "quantity": 1
    }
  ]
}
```

---

## Get Order Status

```http
GET /api/orders/status/{orderId}
```

Example:

```http
GET /api/orders/status/14
```

Response:

```text
PAID
```

---

## Complete Payment

```http
POST /api/payments/{orderId}/complete
```

Example:

```http
POST /api/payments/14/complete
```

Response:

```text
Payment event sent
```

---

## Duplicate Payment Event

```http
POST /api/payments/{orderId}/duplicate
```

Example:

```http
POST /api/payments/14/duplicate
```

Response:

```text
Duplicate payment events sent
```

This endpoint is intended for development/testing of idempotent event handling.

---

# 📁 Project Structure

```text
Asynchronous-Event-Processing-Ecommerce-System/
│
├── backend/
│   │
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── ...
│   │       │
│   │       └── resources/
│   │           └── application.properties
│   │
│   └── pom.xml
│
├── frontend/
│   │
│   ├── src/
│   │   ├── assets/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── types/
│   │   ├── App.tsx
│   │   └── main.tsx
│   │
│   └── package.json
│
├── docker-compose.yml
│
└── README.md
```

---

# 🧰 Technology Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Backend | Spring Boot |
| REST | Spring Web |
| ORM | JPA / Hibernate |
| Messaging | Apache Kafka |
| Kafka Integration | Spring Kafka |
| Database | PostgreSQL |
| Frontend | React |
| Frontend Language | TypeScript |
| Build Tool | Vite |
| HTTP Client | Axios |
| Routing | React Router |
| Containerization | Docker |
| Build | Maven |
| Boilerplate Reduction | Lombok |

---

# 🐳 Running the Project

## Prerequisites

Install:

- Java 21
- Maven
- Node.js
- npm
- PostgreSQL
- Docker
- Docker Compose

---

## 1. Start Kafka

Start the Kafka infrastructure:

```bash
docker compose up -d
```

Verify the container:

```bash
docker ps
```

The application expects Kafka to be available at:

```text
localhost:9092
```

---

# 2. Configure PostgreSQL

Create the project database and configure the datasource in:

```text
backend/src/main/resources/application.properties
```

Example configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.kafka.bootstrap-servers=localhost:9092
```

Use your own PostgreSQL credentials.

---

# 3. Start the Backend

```bash
cd backend
./mvnw spring-boot:run
```

On systems where the Maven wrapper is not available:

```bash
mvn spring-boot:run
```

Backend:

```text
http://localhost:8080
```

---

# 4. Start the Frontend

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

Frontend:

```text
http://localhost:5173
```

---

# 🧪 Testing the Event Flow

A complete manual test can be performed as follows.

## Step 1 — Start Infrastructure

```bash
docker compose up -d
```

---

## Step 2 — Start Backend

```bash
cd backend
./mvnw spring-boot:run
```

---

## Step 3 — Start Frontend

```bash
cd frontend
npm run dev
```

---

## Step 4 — Create an Order

Select products and quantities from the UI.

Click:

```text
PLACE ORDER
```

The frontend sends:

```text
POST /api/orders
```

---

## Step 5 — Verify Order Creation

The backend creates the order in PostgreSQL.

Then:

```text
OrderCreatedEvent
       ↓
Kafka: orders
```

---

## Step 6 — Verify Payment Initialization

The payment consumer receives the event:

```text
PaymentPendingConsumer
       ↓
Payment created
       ↓
PENDING
```

---

## Step 7 — Complete Payment

Click:

```text
PAY NOW
```

The frontend sends:

```text
POST /api/payments/{orderId}/complete
```

Then:

```text
PaymentCompleteEvent
       ↓
Kafka
       ↓
PaymentConsumer
       ↓
Payment SUCCESS
       ↓
Order PAID
```

---

## Step 8 — Observe Downstream Events

The successful payment publishes:

```text
PaymentSuccessfulEvent
```

which is consumed by:

```text
ShippingConsumer
NotificationConsumer
```

The order eventually becomes:

```text
SHIPPED
```

---

# 🧠 Engineering Concepts Demonstrated

This project was designed around several backend engineering concepts.

### Event-Driven Architecture

Uses domain events to communicate state transitions asynchronously.

### Asynchronous Processing

Moves downstream work away from the original HTTP request.

### Kafka Producers

Publish events to Kafka topics.

### Kafka Consumers

React to events and perform independent business operations.

### Consumer Groups

Allow multiple consumer instances to coordinate processing.

### Event Decoupling

Consumers do not need to directly call one another.

### Idempotency

Duplicate payment events do not repeatedly transition an already-successful payment.

### Persistence

Uses PostgreSQL to maintain durable order, product, order-item, and payment state.

### JPA Relationships

Models relationships between:

```text
Product
   ↓
OrderItem
   ↓
Order
```

and:

```text
Order
   ↓
Payment
```

### Retry Handling

Failed Kafka processing can be retried automatically.

### Dead Letter Topics

Events that cannot be processed after retries can be redirected to a DLT.

### Docker

Kafka infrastructure is containerized for local development.

### REST API Design

The frontend communicates with the Spring Boot backend through REST endpoints.

---

# 📈 Why Kafka?

Kafka is used as the event transport layer because the project requires asynchronous communication between independent processing components.

Instead of:

```text
Payment → directly calls Shipping
Payment → directly calls Notification
```

the application uses:

```text
Payment
   │
   ▼
PaymentSuccessfulEvent
   │
   ▼
Kafka
   │
   ├── ShippingConsumer
   │
   └── NotificationConsumer
```

This allows additional consumers to be introduced without requiring the payment consumer to directly know about every downstream operation.

---

# 🔮 Future Improvements

The project currently focuses on the core asynchronous order-processing workflow.

Possible future improvements include:

- Implement payment failure simulation.
- Complete the Dead Letter Topic testing workflow.
- Add request validation using Bean Validation.
- Replace generic runtime exceptions with custom exceptions.
- Add centralized exception handling with `@ControllerAdvice`.
- Add authentication and authorization.
- Add automated unit and integration tests.
- Add Kafka integration tests.
- Add structured logging.
- Add monitoring and metrics.
- Add distributed tracing.
- Add production deployment.
- Add persistent notification history.
- Improve order-status transition validation.
- Add an administrator dashboard.
- Add real payment gateway integration.

---

# 📋 Project Status

### Implemented

- [x] Product management
- [x] Order creation
- [x] Order and OrderItem persistence
- [x] PostgreSQL integration
- [x] Kafka producer integration
- [x] Kafka consumer integration
- [x] Asynchronous payment initialization
- [x] Payment completion workflow
- [x] Shipping consumer
- [x] Notification consumer
- [x] Idempotent payment processing
- [x] Kafka consumer groups
- [x] Retry configuration
- [x] Dead Letter Topic configuration
- [x] React frontend
- [x] Payment UI
- [x] Order tracking UI
- [x] Dockerized Kafka infrastructure

### Planned

- [ ] Payment failure simulation
- [ ] Automated tests
- [ ] Request validation
- [ ] Global exception handling
- [ ] Authentication
- [ ] Production deployment
- [ ] Monitoring / observability

---

# 📌 Key Design Decisions

## Why is the order saved before publishing the event?

The order must exist in PostgreSQL before downstream consumers receive an event referencing it.

```text
Save Order
   ↓
Order ID generated
   ↓
Create Event with Order ID
   ↓
Publish Event
```

This gives consumers a persistent order identifier they can use when processing the event.

---

## Why does `PaymentCompleteEvent` only contain `orderId`?

The payment already exists in PostgreSQL.

Therefore the event acts as a command-like message telling the payment consumer which payment should be processed:

```text
PaymentCompleteEvent
        │
        └── orderId
              │
              ▼
       Find Payment in DB
              │
              ▼
      Process existing Payment
```

The payment amount is retrieved from the persisted payment rather than being trusted from the frontend event.

---

## Why are Shipping and Notification separate consumers?

Both are downstream effects of successful payment.

Instead of:

```text
PaymentConsumer
      │
      ├── call Shipping
      │
      └── call Notification
```

the application publishes:

```text
PaymentSuccessfulEvent
          │
          ▼
        Kafka
       /     \
      ▼       ▼
 Shipping  Notification
```

Each consumer can therefore evolve independently.

---

# 📄 Resume Description

### Asynchronous Event-Driven E-Commerce Platform

- **Architected** an asynchronous e-commerce platform using **Spring Boot, Apache Kafka, PostgreSQL, JPA/Hibernate, React, and Docker**, decoupling order, payment, shipping, and notification workflows through event-driven communication.
- **Implemented** Kafka producers, consumers, and **consumer groups** to asynchronously process `OrderCreated`, `PaymentComplete`, and `PaymentSuccessful` events across the order lifecycle.
- **Engineered** **idempotent payment processing** using persisted payment state and duplicate-event detection, preventing repeated `PaymentCompleteEvent` messages from causing duplicate state transitions.
- **Configured** Spring Kafka **retry and Dead Letter Topic handling** with `DefaultErrorHandler`, while integrating a React/TypeScript frontend with REST APIs for order creation, payment initiation, and asynchronous order-status tracking.

---

# 👨‍💻 Author

**Tenzing Gyalpo Tamang**

B.Tech — Information Technology  
IIEST Shibpur

---

# ⭐ Project Summary

This project demonstrates how a conventional e-commerce workflow can be implemented using an **asynchronous event-driven architecture**.

The central flow is:

```text
                    ┌───────────────────────┐
                    │       React UI        │
                    └───────────┬───────────┘
                                │
                                ▼
                    ┌───────────────────────┐
                    │     Spring Boot       │
                    │       REST API        │
                    └───────────┬───────────┘
                                │
                    ┌───────────┴───────────┐
                    │                       │
                    ▼                       ▼
               PostgreSQL                Kafka
                                            │
                              ┌─────────────┼──────────────┐
                              │             │              │
                              ▼             ▼              ▼
                           Payment      Shipping      Notification
                           Consumer     Consumer       Consumer
```

The project combines synchronous REST communication at the API boundary with asynchronous Kafka-based processing for downstream business workflows.

It was built to provide practical experience with **Spring Boot backend development, relational persistence, event-driven architecture, Kafka messaging, idempotency, retry handling, Docker, and frontend-backend integration**.