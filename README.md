# 📦 ReturnRemind Service 📦

By: Mira Mohan

### ReturnRemind is a Spring Boot microservice that tracks online purchases, computes return deadlines, and schedules proactive reminders (7 days and 1 day beforehand). It’s designed like something a credit card company or fintech might use to reduce disputes and improve customer experience.

### It’s also inspired by a deeply personal problem:
I do my fair share of online shopping and have missed more return deadlines than I’d like to admit, so this project is both a learning exercise and an act of self-defense.

## Features

### Track Purchases
- Store merchant, item, purchase date, and return window
- Automatically compute the return deadline

### Scheduled Notifications
- Automatically schedule:
- 7-day-before reminder
- 1-day-before reminder
- Background scheduler picks up due notifications and “sends” them (via console logging for now)

### Clean Data Modeling
- Entities: User, Purchase, Notification
- Notification lifecycle: PENDING → SENT

### REST API
- Create purchases via a clean REST endpoint
- H2 Console available for viewing data

### Modern Spring Boot Service
- Java 17
- Spring Boot 3.x
- Spring Web + JPA + Scheduling
- In-memory H2 database

## Why I Built This

Return deadlines cause frustration for customers and result in avoidable chargebacks for card issuers.
This project explores how a backend service can proactively:
  - Reduce disputes
  - Improve transparency
  - Strengthen customer experience

It also reflects how I think about systems design, APIs, scheduling, and data modeling — all key areas in fintech and backend engineering.

Plus… it helps keep me from forgetting returns ever again.

## Tech Stack
- Java 17
- Spring Boot (Web, Data JPA, Scheduling, Actuator)
- H2 Database
- Maven
- Built as a runnable JAR microservice

## Running the Service
Clone the repository:
```bash
git clone https://github.com/YOUR_USERNAME/return-remind-service.git
cd return-remind-service
```

Run with Maven:
```bash
mvn spring-boot:run
```

Service will start at:
```bash
http://localhost:8080
```

## Example API Usage
### Create a Purchase
(Assuming you've added a user with ID = 1)
```bash
curl -X POST http://localhost:8080/api/purchases \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "merchantName": "Amazon",
    "itemName": "Headphones",
    "purchaseDate": "2025-01-15",
    "returnWindowDays": 30
  }'
```
This will:
  - Store the purchase
  - Compute the return deadline
  - Schedule two notifications
  - Insert them into the DB as PENDING

When the scheduled job runs, you'll see logs like:
```bash
[NOTIFICATION] SEVEN_DAYS_BEFORE for purchase 5 at 2025-02-07T00:00
```

## Project Structure
```bash
src/main/java/com/mira/returnremind/
  ├── ReturnRemindApplication.java
  ├── model/
  │     ├── User.java
  │     ├── Purchase.java
  │     ├── Notification.java
  │     ├── NotificationType.java
  │     └── NotificationStatus.java
  ├── repo/
  │     ├── UserRepository.java
  │     ├── PurchaseRepository.java
  │     └── NotificationRepository.java
  ├── service/
  │     ├── PurchaseService.java
  │     └── NotificationService.java
  └── controller/
        └── PurchaseController.java
```
       
## Future Enhancements
  - Email/SMS notification delivery
  - Merchant-specific return policies
  - Frontend dashboard
  - Weekly summary of upcoming deadlines
  - Importing real transactions (mocked)
  - API for viewing upcoming notifications

## Inspiration
  - American Express open-source projects (Synapse, FlowRet)
  - Real-world fintech dispute prevention
  - My own questionable online shopping habits
