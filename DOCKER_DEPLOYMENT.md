# Running PlatinumRX on Docker

## Quick Start

### 1. Build and Start All Services

```bash
# Build and start everything (PostgreSQL, pgbouncer, Redis, and the application)
docker-compose up --build -d

# View logs
docker-compose logs -f

# View only application logs
docker-compose logs -f app
```

### 2. Verify Services

```bash
# Check all services are running
docker ps

# You should see:
# - platinumrx-postgres-master
# - platinumrx-pgbouncer-master
# - platinumrx-redis
# - platinumrx-app
```

### 3. Test the Application

```bash
# Check application health
curl http://localhost:8080/actuator/health

# Test transaction endpoint (example)
curl -X POST http://localhost:8080/api/transaction/initiate \
  -H "Content-Type: application/json" \
  -d '{
    "order_id": "test123",
    "amount": 100.0,
    "payment_method": "UPI",
    "payment_instrument": "CARD"
  }'
```

## Services and Ports

| Service | Container Name | Port | Purpose |
|---------|---------------|------|---------|
| Application | platinumrx-app | 8080 | Spring Boot API |
| PostgreSQL | platinumrx-postgres-master | 5432 | Database |
| pgbouncer | platinumrx-pgbouncer-master | 6432 | Connection pooling |
| Redis | platinumrx-redis | 6379 | Statistics cache |

## Common Commands

### Start Services
```bash
# Start all services
docker-compose up -d

# Start specific service
docker-compose up -d app
```

### Stop Services
```bash
# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: deletes all data)
docker-compose down -v
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f app
docker-compose logs -f postgres-master
docker-compose logs -f redis
```

### Rebuild Application
```bash
# Rebuild after code changes
docker-compose up --build -d app

# Force rebuild
docker-compose build --no-cache app
docker-compose up -d app
```

### Restart Services
```bash
# Restart all
docker-compose restart

# Restart specific service
docker-compose restart app
```

## Development Workflow

### Making Code Changes

1. **Edit your code**
2. **Rebuild and restart:**
   ```bash
   docker-compose up --build -d app
   ```
3. **View logs:**
   ```bash
   docker-compose logs -f app
   ```

### Database Access

```bash
# Connect to PostgreSQL via pgbouncer
docker exec -it platinumrx-pgbouncer-master psql -h localhost -U postgres -d platinumrx

# Or connect directly to PostgreSQL
docker exec -it platinumrx-postgres-master psql -U postgres -d platinumrx

# View tables
\dt

# Query data
SELECT * FROM payment_gateway_status;
SELECT * FROM order_transactions;
```

### Redis Access

```bash
# Connect to Redis CLI
docker exec -it platinumrx-redis redis-cli

# Check stats
ZRANGE gateway:stats:Razorpay:total 0 -1 WITHSCORES
ZRANGE gateway:stats:Razorpay:success 0 -1 WITHSCORES

# Check all keys
KEYS *
```

## Troubleshooting

### Application Won't Start

```bash
# Check logs
docker-compose logs app

# Common issues:
# 1. Database not ready - wait a few seconds and check logs
# 2. Port 8080 already in use - stop other services using that port
# 3. Build failed - check for compilation errors in logs
```

### Database Connection Issues

```bash
# Verify PostgreSQL is running
docker exec platinumrx-postgres-master pg_isready -U postgres

# Check pgbouncer
docker exec platinumrx-pgbouncer-master psql -p 5432 -U postgres -c "SHOW POOLS;"

# Restart database services
docker-compose restart postgres-master pgbouncer-master
```

### Redis Connection Issues

```bash
# Test Redis
docker exec platinumrx-redis redis-cli ping

# Should return: PONG

# Restart Redis
docker-compose restart redis
```

### Clean Start

```bash
# Stop everything and remove volumes
docker-compose down -v

# Remove application image
docker rmi platinumrx-app

# Start fresh
docker-compose up --build -d
```

## Environment Variables

You can override configuration by editing `docker-compose.yml` or creating a `.env` file:

```env
# .env file example
POSTGRES_PASSWORD=your_secure_password
REDIS_PASSWORD=your_redis_password
SPRING_PROFILES_ACTIVE=production
```

## Production Considerations

For production deployment:

1. **Use secrets management** instead of plain text passwords
2. **Enable SSL/TLS** for database connections
3. **Set resource limits** in docker-compose:
   ```yaml
   deploy:
     resources:
       limits:
         cpus: '2'
         memory: 2G
   ```
4. **Use managed services** for PostgreSQL and Redis
5. **Enable monitoring** (Prometheus, Grafana)
6. **Configure logging** to external systems

## Health Checks

The application includes health checks:

```bash
# Application health
curl http://localhost:8080/actuator/health

# Database health
docker exec platinumrx-postgres-master pg_isready -U postgres

# Redis health
docker exec platinumrx-redis redis-cli ping
```

## Scaling

To scale the application (requires load balancer):

```bash
# Run multiple instances
docker-compose up -d --scale app=3

# Note: You'll need to configure a load balancer (nginx, HAProxy)
# to distribute traffic across instances
```
