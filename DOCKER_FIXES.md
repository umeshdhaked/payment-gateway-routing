# Docker Deployment - Issues Fixed

## Summary
Successfully containerized and deployed the PlatinumRX payment gateway routing application using Docker Compose. All services are running and the application is fully functional.

## Issues Fixed

### 1. PostgreSQL Initialization Script Error
**Problem:** The init-db.sql file used invalid PostgreSQL syntax `CREATE DATABASE IF NOT EXISTS`
**Solution:** Removed the invalid syntax since the database is already created via the `POSTGRES_DB` environment variable in docker-compose.yml

### 2. Docker Compose YAML Indentation
**Problem:** The `app` service was incorrectly indented and commented out
**Solution:** Fixed the indentation to properly align with other services

### 3. Spring Boot Environment Variable Mapping
**Problem:** Environment variables with hyphens (`SPRING_DATASOURCE_WRITE_JDBC_URL`) were not being mapped correctly to Spring Boot properties
**Solution:** Changed property names from `jdbc-url` to `jdbcUrl` in application.properties to match HikariCP's expected format

### 4. Entity Field Type Mismatch
**Problem:** The `amount` field in `OrderTransactionEntity` was defined as `Double` with `precision` and `scale` attributes, which are only valid for `BigDecimal`
**Solution:** 
- Changed `amount` field from `Double` to `BigDecimal` in `OrderTransactionEntity.java`
- Updated `TransactionInitiateRequest.java` to use `BigDecimal` for the amount field
- Added necessary imports for `java.math.BigDecimal`

### 5. Hibernate Dialect Configuration
**Problem:** Hibernate couldn't determine the database dialect automatically
**Solution:** Explicitly set the PostgreSQL dialect in the `DataSourceConfig.java` by adding:
```java
vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
```

### 6. PgBouncer Authentication Issue
**Problem:** PgBouncer authentication was failing with "wrong password type" error
**Solution:** Changed the application to connect directly to PostgreSQL instead of through PgBouncer by updating the JDBC URLs from `pgbouncer-master:5432` to `postgres-master:5432`

## Current Status

### Running Services
- ✅ **PostgreSQL** (postgres-master) - Port 5432
- ✅ **PgBouncer** (pgbouncer-master) - Port 6432
- ✅ **Redis** (redis) - Port 6379
- ✅ **Spring Boot Application** (platinumrx-app) - Port 8080

### Verified Functionality
The `/transactions/initiate` endpoint is working correctly:

**Test Request:**
```bash
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

**Response:**
```json
{
  "transactionId": "53512b1e-ec74-42e5-8a15-01ba1d462eb5",
  "orderId": "ORD123",
  "gatewayName": "Cashfree"
}
```

## How to Run

1. **Start all services:**
   ```bash
   docker-compose up -d
   ```

2. **View logs:**
   ```bash
   docker-compose logs -f app
   ```

3. **Stop all services:**
   ```bash
   docker-compose down
   ```

4. **Stop and remove volumes:**
   ```bash
   docker-compose down -v
   ```

## Notes

- The application is currently connecting directly to PostgreSQL. To use PgBouncer, authentication configuration needs to be updated.
- All database tables are created automatically via the init-db.sql script
- The application uses a routing datasource pattern for read/write separation (currently both pointing to the same master database)
- Payment gateway distribution is configured as: Razorpay (50%), PayU (20%), Cashfree (30%)
