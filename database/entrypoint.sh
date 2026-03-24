#!/bin/bash

# Ejecuta el entrypoint original de SQL Server en background
/opt/mssql/bin/sqlservr &
PID=$!

# Espera a que SQL Server inicie (máximo 120 segundos)
echo "Esperando a que SQL Server inicie..."
COUNTER=0
MAX_ATTEMPTS=120
while [ $COUNTER -lt $MAX_ATTEMPTS ]; do
  if /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -C -Q "SELECT 1" &>/dev/null; then
    echo "✓ SQL Server está listo"
    break
  fi
  COUNTER=$((COUNTER + 1))
  echo "Intento $COUNTER/$MAX_ATTEMPTS..."
  sleep 1
done

if [ $COUNTER -eq $MAX_ATTEMPTS ]; then
  echo "✗ SQL Server no respondió en 120 segundos"
  kill $PID
  exit 1
fi

# Ejecuta los scripts SQL
echo "Ejecutando scripts SQL..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -C -i /scripts/01_create_database.sql
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -C -d banco -i /scripts/02_create_tables.sql
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -C -d banco -i /scripts/03_seed_data.sql

echo "✓ Scripts SQL ejecutados correctamente"

# Mantiene SQL Server corriendo
wait $PID