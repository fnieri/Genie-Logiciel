FROM python:3.9-alpine

ARG LOGS_DIR=/var/log/quick_mem_serv
ARG STATIC_DIR=/app/static

# Create django user
RUN addgroup -S django
RUN adduser -S -H django -G django

# Create directories
WORKDIR /app
RUN mkdir -p ${LOGS_DIR}
RUN mkdir -p ${STATIC_DIR}

# Copy Django Application in app directory
COPY . .

# Install dependencies
RUN pip install -r requirements.txt

# NOTE: It's important here to collect the static files BEFORE setting the
# environment variable. If we are are using the production settings, Django
# will attempt to parse environment variables required for the Postgres Database
RUN python manage.py collectstatic --noinput

# NOTE - Windows: this is required otherwise the file will not have the 
# correct file ending and will not be able to run. Uncomment those lines if
# you are on Windows.
# RUN apk add --no-cache dos2unix
# RUN dos2unix /app/entrypoint.sh

# Permissions: django user owns most of the app, except for the static files
RUN chown -R django:django /app
RUN chown -R django:django ${LOGS_DIR}
RUN chown -R root:root ${STATIC_DIR}
RUN chmod -R 744 ${STATIC_DIR}

# Switch to django user
USER django

# Environment variables
ENV DEBUG=False
ENV DJANGO_SETTINGS_MODULE=quick_mem_serv.production
ENV STATIC_ROOT=${STATIC_DIR}
ENV LOGS_DIR=${LOGS_DIR}

ENTRYPOINT ["sh", "/app/entrypoint.sh"]