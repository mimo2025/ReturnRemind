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

### Lifecycle Management
- Purchases automatically archive once their return deadline passes
- Archived purchases move to history and no longer receive reminders
- Active and archived purchases are cleanly separated

### Clean Data Modeling
- Entities: User, Purchase, Notification
- Notification lifecycle: PENDING → SENT → SKIPPED
- Designed to be extensible for real delivery (email/SMS/push)

### REST API
- Create and view purchases via a clean REST endpoint
- Separate endpoints for active purchases vs. purchase history
- Tested via curl and Postman
- H2 Console available for viewing data

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
The examples below use curl for simplicity, but the same requests can be tested using Postman.
A Postman collection is included in the repository for easier exploration.
### Create a Purchase
(Assuming you've added a user with id = 1)
```bash
curl -X POST http://localhost:8080/users/1/purchases \
  -H "Content-Type: application/json" \
  -d '{
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
[NOTIFICATION] SEVEN_DAYS_BEFORE for purchase 5 at 2025-02-07T09:00
```
### View Active Purchases
Returns purchases that are still within their return window:
```bash
curl http://localhost:8080/users/1/purchases
```
### View Purchase History
Returns archived purchases whose return deadlines have passed:
```bash
curl http://localhost:8080/users/1/purchases/history
```

## API Testing
- Requests can be tested using:
  - curl (examples above)
  - Postman (recommended for exploration)
- Postman collection location: /postman/ReturnRemind.postman_collection.json

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
## Why I Built This
Return deadlines cause frustration for customers and result in avoidable chargebacks for card issuers.
This project explores how a backend service can proactively:
- Reduce disputes
- Improve transparency
- Strengthen customer experience

It also reflects how I think about systems design, APIs, scheduling, and data modeling — all key areas in fintech and backend engineering.

Plus… it helps keep me from forgetting returns ever again.
       
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
