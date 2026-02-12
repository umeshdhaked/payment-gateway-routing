# PostgreSQL Replication Setup Guide

## Current Status

✅ **Application is fully configured for replication:**
- Read/write datasource routing implemented
- HikariCP connection pooling configured
- Repository layer ready for replica reads

⚠️ **Docker replication setup encountered challenges:**
- PostgreSQL replication in Docker requires careful permission management
- The replicas need to run as the `postgres` user, not root
- For production, managed PostgreSQL services (AWS RDS, Google Cloud SQL) handle replication automatically

## Current Working Setup

**What's Running:**
- 1 PostgreSQL Master (port 5432)
- 1 pgbouncer for Master (port 6432)
- 1 Redis instance (port 6379)

**Configuration:**
- Both read and write operations currently use the master via pgbouncer
- Application code supports read/write routing (ready for replicas)

## Option 1: Use Current Setup (Recommended for Development)

The current setup works perfectly for development and testing:

```bash
# Start services
docker-compose up -d

# Verify
docker ps
```

**Pros:**
- Simple and reliable
- All features work
- Easy to debug
- pgbouncer provides connection pooling

**Cons:**
- No read scaling (but fine for development)

## Option 2: Manual Replication Setup

If you need replication for testing, here's a manual approach:

### Step 1: Start Master Only

```yaml
# Simplified docker-compose.yml (master only)
services:
  postgres-master:
    image: postgres:15-alpine
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: platinumrx
    command: |-
      postgres
      -c wal_level=replica
      -c max_wal_senders=10
      -c max_replication_slots=10
    ports:
      - "5432:5432"
```

### Step 2: Create Replication User

```bash
docker exec -it postgres-master psql -U postgres -c \
  "CREATE USER replicator WITH REPLICATION ENCRYPTED PASSWORD 'replicator_password';"
```

### Step 3: Configure pg_hba.conf

```bash
docker exec -it postgres-master bash -c \
  "echo 'host replication replicator 0.0.0.0/0 md5' >> /var/lib/postgresql/data/pg_hba.conf"

docker-compose restart postgres-master
```

### Step 4: Create Replica Manually

```bash
# Stop replica if running
docker-compose stop postgres-replica1

# Remove old data
docker volume rm platinumrx_postgres-replica1-data

# Create base backup
docker run --rm --network platinumrx_platinumrx-network \
  -v platinumrx_postgres-replica1-data:/var/lib/postgresql/data \
  postgres:15-alpine \
  pg_basebackup -h postgres-master -D /var/lib/postgresql/data \
  -U replicator -v -P -W

# Configure standby
docker run --rm -v platinumrx_postgres-replica1-data:/data \
  alpine sh -c "touch /data/standby.signal && \
  echo \"primary_conninfo = 'host=postgres-master port=5432 user=replicator password=replicator_password'\" >> /data/postgresql.auto.conf"

# Start replica
docker-compose up -d postgres-replica1
```

## Option 3: Use Managed PostgreSQL (Recommended for Production)

For production, use managed services:

### AWS RDS PostgreSQL
- Automatic replication setup
- Read replicas with one click
- Automatic failover
- Backup management

### Google Cloud SQL
- Built-in replication
- Read replicas
- Automatic backups

### Azure Database for PostgreSQL
- Replication included
- Read replicas
- High availability

**Application Configuration:**
```properties
# Master (write)
spring.datasource.write.jdbc-url=jdbc:postgresql://master-endpoint:5432/platinumrx

# Read replica
spring.datasource.read.jdbc-url=jdbc:postgresql://replica-endpoint:5432/platinumrx
```

No code changes needed - just update the connection strings!

## Verifying Replication (When Set Up)

```bash
# Check replication status on master
docker exec postgres-master psql -U postgres -c \
  "SELECT application_name, state, sync_state FROM pg_stat_replication;"

# Check replica is in recovery mode
docker exec postgres-replica1 psql -U postgres -c \
  "SELECT pg_is_in_recovery();"

# Test read from replica
psql -h localhost -p 5433 -U postgres -d platinumrx -c "SELECT * FROM payment_gateway_status;"
```

## Current Application Configuration

Your application is configured to use:
- **Write:** localhost:6432 (master via pgbouncer)
- **Read:** localhost:6433 (currently points to master, ready for replica)

To switch to replica when available, just update `application.properties`:
```properties
spring.datasource.read.jdbc-url=jdbc:postgresql://localhost:6433/platinumrx
```

## Summary

✅ **What Works Now:**
- PostgreSQL with pgbouncer connection pooling
- Redis for statistics
- Full application functionality
- Read/write routing infrastructure

🔄 **For Replication:**
- Use managed PostgreSQL services (recommended)
- Or follow manual setup above for local testing
- Application code requires NO changes

The current setup is production-ready for single-node deployments. For scaling reads, add managed read replicas.
