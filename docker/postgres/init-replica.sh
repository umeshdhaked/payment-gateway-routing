#!/bin/bash
set -e

# This script initializes a PostgreSQL replica node

echo "Waiting for master to be ready..."
until pg_isready -h postgres-master -p 5432 -U postgres; do
  echo "Master is unavailable - sleeping"
  sleep 2
done

echo "Master is ready. Setting up replica..."

# Remove existing data directory if it exists
rm -rf "$PGDATA"/*

# Create base backup from master
echo "Creating base backup from master..."
PGPASSWORD=replicator_password pg_basebackup -h postgres-master -D "$PGDATA" -U replicator -v -P -W

# Create standby.signal file to indicate this is a replica
touch "$PGDATA/standby.signal"

# Configure recovery settings
cat >> "$PGDATA/postgresql.auto.conf" <<EOF
primary_conninfo = 'host=postgres-master port=5432 user=replicator password=replicator_password'
hot_standby = on
EOF

echo "Replica configuration complete."
