# Intelligent Payment Gateway Routing Application

## 🚀 Quick Start with Docker (Recommended)

Run the entire system (PostgreSQL, pgbouncer, Redis, and Spring Boot app) with one command:

```bash
docker-compose up --build -d
```

This will start:
- ✅ Spring Boot Application (port 8080)
- ✅ PostgreSQL Database (port 5432)
- ✅ pgbouncer Connection Pooling (port 6432)
- ✅ Redis Cache (port 6379)

**Verify it's running:**
```bash
docker ps
docker-compose logs -f app
curl http://localhost:8080/actuator/health
```

## 🔧 Alternative: Run Locally

1. **Start infrastructure:**
   ```bash
   docker-compose up -d postgres-master pgbouncer-master redis
   ```

2. **Run application:**
   ```bash
   mvn spring-boot:run
   ```

## 📚 Documentation

- **[DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md)** - Complete Docker guide with troubleshooting
- **[DATABASE_SETUP.md](DATABASE_SETUP.md)** - Database configuration details
- **[REPLICATION_SETUP.md](REPLICATION_SETUP.md)** - Adding read replicas

---

## 📡 API Usage

### 1. Initiate Transaction for Order
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

### 2. Update Transaction Status

**Success:**
```bash
curl --location 'localhost:8080/transactions/callback' \
--header 'Content-Type: application/json' \
--data '{
    "order_id": "ORD123",
    "status": "SUCCESS",
    "gateway": "Razorpay",
    "reason": "Payment Completed"
}'
```

**Failure:**
```bash
curl --location 'localhost:8080/transactions/callback' \
--header 'Content-Type: application/json' \
--data '{
    "order_id": "ORD123",
    "status": "FAILURE",
    "gateway": "Razorpay",
    "reason": "Customer Cancelled"
}'
```

## 🛠️ Common Commands

```bash
# View all logs
docker-compose logs -f

# View application logs only
docker-compose logs -f app

# Rebuild after code changes
docker-compose up --build -d app

# Stop all services
docker-compose down

# Clean restart (removes all data)
docker-compose down -v && docker-compose up --build -d
```

## 🏗️ Architecture

The application uses:
- **PostgreSQL** for transaction and gateway status persistence
- **Redis** for real-time payment gateway statistics (sorted sets)
- **pgbouncer** for database connection pooling
- **HikariCP** for application-level connection pooling
- **Read/Write datasource routing** (ready for replicas)

## 📊 Monitoring

```bash
# Database access
docker exec -it platinumrx-postgres-master psql -U postgres -d platinumrx

# Redis access
docker exec -it platinumrx-redis redis-cli

# View gateway stats
docker exec -it platinumrx-redis redis-cli ZRANGE gateway:stats:Razorpay:total 0 -1 WITHSCORES
```
