#!/bin/bash

# Script para esperar a que la base de datos esté disponible
DB_URL=${DB_URL:-jdbc:sqlserver://db:1433}
DB_USERNAME=${DB_USERNAME:-sa}
DB_PASSWORD=${DB_PASSWORD:-YourP@ssword1}

# Extrae host y puerto de la URL JDBC
DB_HOST=$(echo $DB_URL | sed 's/.*:\/\/\([^:]*\).*/\1/')
DB_PORT=$(echo $DB_URL | sed 's/.*:\([0-9]*\).*/\1/')

echo "Esperando a que SQL Server esté disponible en $DB_HOST:$DB_PORT..."

COUNTER=0
MAX_ATTEMPTS=60

until [ $COUNTER -ge $MAX_ATTEMPTS ]; do
  nc -z $DB_HOST $DB_PORT 2>/dev/null
  if [ $? -eq 0 ]; then
    echo "✓ Base de datos disponible"
    exit 0
  fi
  COUNTER=$((COUNTER + 1))
  echo "Intento $COUNTER/$MAX_ATTEMPTS..."
  sleep 2
done

echo "✗ No se pudo conectar a la base de datos después de $MAX_ATTEMPTS intentos"
exit 1
