#!/bin/sh

# Start Cloud SQL Proxy in background if CLOUD_SQL_INSTANCE is set
if [ -n "$CLOUD_SQL_INSTANCE" ]; then
    ./cloud_sql_proxy -instances=$CLOUD_SQL_INSTANCE=tcp:5432 &
    sleep 5  # Wait for proxy to start
fi

# Start Spring Boot app
exec java -jar app.jar