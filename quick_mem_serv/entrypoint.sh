#!/usr/bin/env sh

set -e

# Wait for postgres to be healthy
echo "Waiting for postgres..."

while ! nc -z $POSTGRES_HOST 5432; do
  sleep 0.1
done

echo "PostgreSQL started"

python3 manage.py migrate

# Check if environment variables are set
if [ -z "$SUPERUSER_USERNAME" ] || [ -z "$SUPERUSER_PASSWORD" ]; then
    echo "Error: SUPERUSER_USERNAME and SUPERUSER_PASSWORD environment variables must be set."
    exit 1
fi


# Set default value for SUPERUSER_EMAIL if it's not set
SUPERUSER_EMAIL="${SUPERUSER_EMAIL:-admin@example.com}"

# Create superuser if it doesn't exist
echo "from django.contrib.auth import get_user_model; User = get_user_model(); user = User.objects.filter(username='$SUPERUSER_USERNAME').first(); user = User.objects.create_superuser('$SUPERUSER_USERNAME', '$SUPERUSER_EMAIL', '$SUPERUSER_PASSWORD') if user is None else None; print('Superuser created:', user)" | python3 manage.py shell

python3 -m gunicorn quick_mem_serv.wsgi:application --bind 0.0.0.0:80
