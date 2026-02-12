# PlatinumRX Payment Gateway Routing - Database Setup

## Quick Start

### 1. Start Infrastructure

```bash
# Start PostgreSQL, pgbouncer, and Redis
docker-compose up -d

# Verify services are running
docker ps
```

### 2. Initialize Database (if needed)

```bash
# Tables are created automatically on first run
# To manually run the init script:
docker exec platinumrx-postgres-master psql -U postgres -d platinumrx -f /docker-entrypoint-initdb.d/01-init-db.sql
```

### 3. Run Application

```bash
# Build and run
./mvnw clean install
./mvnw spring-boot:run
```

## Architecture

- **PostgreSQL Master** (port 5432): Primary database for transactions and gateway status
- **pgbouncer** (port 6432): Connection pooling for PostgreSQL
- **Redis** (port 6379): High-performance statistics tracking

## Repository Implementations

### PostgreSQL Repositories
- `PostgresOrderTransactionRepo`: Transaction persistence with read/write routing
- `PostgresPaymentGatewayStatusRepo`: Gateway status management

### Redis Repository
- `RedisPaymentGatewayStatsRepo`: Real-time statistics using sorted sets
  - Efficient time-window queries
  - Automatic cleanup of old data
  - O(log N) performance

## Configuration

Key settings in `application.properties`:

```properties
# PostgreSQL (via pgbouncer)
spring.datasource.write.jdbc-url=jdbc:postgresql://localhost:6432/platinumrx
spring.datasource.read.jdbc-url=jdbc:postgresql://localhost:6432/platinumrx

# HikariCP Connection Pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.lettuce.pool.max-active=20

# Gateway Stats
gateway.stats.window-duration-seconds=900  # 15 minutes
```

## Database Schema

### order_transactions
```sql
CREATE TABLE order_transactions (
    order_id VARCHAR(255) PRIMARY KEY,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    gateway VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### payment_gateway_status
```sql
CREATE TABLE payment_gateway_status (
    id BIGSERIAL PRIMARY KEY,
    gateway VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    last_updated TIMESTAMP NOT NULL
);
```

## Testing

### Database Connection
```bash
# Connect via pgbouncer
psql -h localhost -p 6432 -U postgres -d platinumrx

# Check tables
\dt

# View data
SELECT * FROM payment_gateway_status;
```

### Redis Connection
```bash
# Test Redis
docker exec platinumrx-redis redis-cli ping

# View stats (example)
docker exec platinumrx-redis redis-cli ZRANGE gateway:stats:Razorpay:total 0 -1 WITHSCORES
```

## Future: PostgreSQL Replication

The application is ready for read replicas:
1. Set up PostgreSQL streaming replication
2. Update `spring.datasource.read.jdbc-url` to point to replica
3. No code changes required - routing is already implemented

## Troubleshooting

**Services not starting:**
```bash
docker-compose logs -f
```

**Database connection issues:**
```bash
# Check PostgreSQL
docker logs platinumrx-postgres-master

# Test direct connection
psql -h localhost -p 5432 -U postgres -d platinumrx
```

**Redis issues:**
```bash
# Check Redis
docker logs platinumrx-redis

# Test connection
redis-cli -h localhost -p 6379 ping
```

## Stopping Services

```bash
# Stop services
docker-compose down

# Stop and remove data (WARNING: deletes all data)
docker-compose down -v
```

## See Also

- [Walkthrough](file:///.gemini/antigravity/brain/c649f33c-493b-4084-9180-cf884f309cfe/walkthrough.md) - Detailed implementation walkthrough
- [Implementation Plan](file:///.gemini/antigravity/brain/c649f33c-493b-4084-9180-cf884f309cfe/implementation_plan.md) - Original implementation plan
