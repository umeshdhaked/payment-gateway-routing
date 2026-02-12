#!/bin/bash
set -e

echo "Initializing replica from master..."

# Wait for master to be ready
until pg_isready -h postgres-master -p 5432 -U postgres; do
  echo 'Waiting for master to be ready...'
  sleep 2
done

# Check if replica is already initialized
if [ ! -s '/var/lib/postgresql/data/PG_VERSION' ]; then
  echo 'Setting up replica from master...'
  rm -rf /var/lib/postgresql/data/*
  
  # Create base backup from master
  PGPASSWORD=replicator_password pg_basebackup -h postgres-master -D /var/lib/postgresql/data -U replicator -v -P -W
  
  # Create standby signal
  touch /var/lib/postgresql/data/standby.signal
  
  # Configure replication
  echo "primary_conninfo = 'host=postgres-master port=5432 user=replicator password=replicator_password application_name=$1'" >> /var/lib/postgresql/data/postgresql.auto.conf
  echo "hot_standby = on" >> /var/lib/postgresql/data/postgresql.auto.conf
fi

# Start PostgreSQL in standby mode
exec postgres
