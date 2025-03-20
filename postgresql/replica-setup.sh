#!/bin/bash
set -e

# Remove any existing postgresql data (since we'll replicate from master)
rm -rf /var/lib/postgresql/data/*

# Stop postgres (it's started automatically by Docker)
pg_ctl -D /var/lib/postgresql/data stop -m fast

# Create recovery.conf equivalent configuration
cat > /var/lib/postgresql/data/postgresql.conf << EOF
primary_conninfo = 'host=postgres-master port=5432 user=postgres password=postgres'
restore_command = 'cp /var/lib/postgresql/wal_archive/%f "%p"'
promote_trigger_file = '/var/lib/postgresql/data/promote_to_master'
hot_standby = on
EOF

# Create standby.signal file to indicate this is a replica
touch /var/lib/postgresql/data/standby.signal

# Start postgres again
pg_ctl -D /var/lib/postgresql/data start
