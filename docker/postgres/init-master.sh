#!/bin/bash
set -e

# This script initializes the PostgreSQL master node for replication

echo "Configuring PostgreSQL master for replication..."

# Create replication user
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER replicator WITH REPLICATION ENCRYPTED PASSWORD 'replicator_password';
EOSQL

# Configure pg_hba.conf to allow replication connections from any host in Docker network
echo "host    replication     replicator      0.0.0.0/0               md5" >> "$PGDATA/pg_hba.conf"
echo "host    all             all             0.0.0.0/0               md5" >> "$PGDATA/pg_hba.conf"

# Configure postgresql.conf for replication
cat >> "$PGDATA/postgresql.conf" <<EOF

# Replication settings
wal_level = replica
max_wal_senders = 10
max_replication_slots = 10
hot_standby = on
listen_addresses = '*'
EOF

echo "Master configuration complete."
