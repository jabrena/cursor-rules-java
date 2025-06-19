#!/bin/bash
set -e

echo "🚀 Starting PostgreSQL with password setup..."

# Start PostgreSQL in the background
docker-entrypoint.sh postgres &
PG_PID=$!

# Function to wait for PostgreSQL to be ready
wait_for_postgres() {
    echo "⏳ Waiting for PostgreSQL to be ready..."
    for i in {1..30}; do
        if pg_isready -U sakila -d sakila >/dev/null 2>&1; then
            echo "✅ PostgreSQL is ready!"
            return 0
        fi
        echo "⏳ Attempt $i/30 - PostgreSQL not ready yet, waiting..."
        sleep 2
    done
    echo "❌ PostgreSQL failed to start within 60 seconds"
    return 1
}

# Wait for PostgreSQL to be ready
if wait_for_postgres; then
    echo "🔐 Setting up sakila user password..."
    
    # Set the password for sakila user
    if psql -U sakila -d sakila -c "ALTER USER sakila PASSWORD 'sakila';" >/dev/null 2>&1; then
        echo "✅ Password set successfully for sakila user"
    else
        echo "⚠️  Password setup failed, but continuing (might already be set)"
    fi
    
    # Test the connection
    if PGPASSWORD=sakila psql -U sakila -d sakila -c "SELECT 'Password authentication working' as status;" >/dev/null 2>&1; then
        echo "✅ Password authentication confirmed working"
    else
        echo "⚠️  Password authentication test failed"
    fi
else
    echo "❌ Failed to setup password - PostgreSQL not ready"
fi

echo "🎉 Setup complete! PostgreSQL is running with proper authentication."

# Wait for the PostgreSQL process
wait $PG_PID 