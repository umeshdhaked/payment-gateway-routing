# Intelligent Payment Gateway Routing Application

Application will run on port 8080

Application configuration is in application.properties

To run the application:
```aiignore
mvn spring-boot:run
```
Or run using docker
```aiignore
docker compose up --build
```

To Use the APIs:
1. Initiate Transaction for Order
```aiignore
curl --location 'localhost:8080/transactions/initiate' \
--header 'Content-Type: application/json' \
--data '{
    "order_id": "ORD123",
    "amount": 499.0,
    "payment_instrument": {
        "type": "card",
        "card_number": "****",
        "expiry": "2036-02-20T10:15:31Z"
    }
}'
```

2. Update the Transaction Status for the Order

Success
```aiignore
curl --location 'localhost:8080/transactions/callback' \
--header 'Content-Type: application/json' \
--data '{
    "order_id": "ORD123",
    "status": "SUCCESS",
    "gateway": "Razorpay",
    "reason": "Customer Cancelled"
}'
```
Failure
```aiignore
curl --location 'localhost:8080/transactions/callback' \
--header 'Content-Type: application/json' \
--data '{
    "order_id": "ORD123",
    "status": "FAILURE",
    "gateway": "Razorpay",
    "reason": "Customer Cancelled"
}'
```
