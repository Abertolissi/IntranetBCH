#!/bin/bash

# Script para ejecutar después de que SQL Server haya iniciado
# Este debe ejecutarse desde docker exec o como un proceso separado

# Espera a que SQL Server inicie
echo "Esperando a que SQL Server inicie..."
COUNTER=0
MAX_ATTEMPTS=120
while [ $COUNTER -lt $MAX_ATTEMPTS ]; do
  if /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -C -Q "SELECT 1" &>/dev/null; then
    echo "✓ SQL Server está listo"
    break
  fi
  COUNTER=$((COUNTER + 1))
  if [ $((COUNTER % 10)) -eq 0 ]; then
    echo "Intento $COUNTER/$MAX_ATTEMPTS..."
  fi
  sleep 1
done

if [ $COUNTER -eq $MAX_ATTEMPTS ]; then
  echo "✗ SQL Server no respondió en 120 segundos"
  exit 1
fi

# Ejecuta los scripts SQL
echo "Ejecutando scripts SQL..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -C -i /scripts/01_create_database.sql
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -C -d banco -i /scripts/02_create_tables.sql
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -C -d banco -i /scripts/03_seed_data.sql

echo "✓ Scripts SQL ejecutados correctamente"
