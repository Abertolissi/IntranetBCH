# ENVIRONMENT CONFIGURATION - INTRANET BANCARIA

Guía de configuración de variables de entorno para diferentes escenarios.

## 📋 Tabla de Contenidos

1. [Development (.env.local)](#development-envlocal)
2. [Docker (.env.docker)](#docker-envdocker)  
3. [Production (.env.production)](#production-envproduction)
4. [Backend Properties](#backend-properties)
5. [Frontend Environment](#frontend-environment)
6. [Database Connection](#database-connection)

---

## Development (.env.local)

Archivo: `.env.local` (raíz del proyecto)

```bash
# ============================================================
# INTRANET BANCARIA - DEVELOPMENT ENVIRONMENT
# ============================================================

# Backend Configuration
BACKEND_PORT=8080
BACKEND_URL=http://localhost:8080

# Frontend Configuration
FRONTEND_PORT=4200
FRONTEND_URL=http://localhost:4200
ANGULAR_ENV=development

# Database - SQL Server
DATABASE_HOST=localhost
DATABASE_PORT=1433
DATABASE_NAME=IntranetBancaria
DATABASE_USER=sa
DATABASE_PASSWORD=YourPassword123!
DATABASE_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-min-256-bits-please-change-in-production!
JWT_EXPIRATION=86400000  # 24 hours in milliseconds
JWT_REFRESH_EXPIRATION=604800000  # 7 days

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:4200
CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS
CORS_ALLOWED_HEADERS=Content-Type,Authorization

# Logging
LOG_LEVEL=DEBUG
LOG_FILE=logs/application.log

# File Upload
MAX_FILE_SIZE=10485760  # 10 MB in bytes
UPLOAD_DIRECTORY=./uploads

# Email (para notificaciones futuras)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASSWORD=your-app-password
SMTP_FROM=noreply@banco.local

# Security
FAILED_LOGIN_ATTEMPTS=5
ACCOUNT_LOCK_DURATION=3600  # 1 hour in seconds
PASSWORD_MIN_LENGTH=8
PASSWORD_REQUIRE_UPPERCASE=true
PASSWORD_REQUIRE_NUMBERS=true
PASSWORD_REQUIRE_SPECIAL=true

# Feature Flags (para Etapa 2+)
FEATURE_RAG_CHATBOT=false
FEATURE_COMMUNICATIONS=false
FEATURE_ANALYTICS=false

# API Keys (futuras integraciones)
OPENAI_API_KEY=sk-your-key-here
PINECONE_API_KEY=your-key-here
SENDGRID_API_KEY=your-key-here
```

---

## Docker (.env.docker)

Archivo: `.env.docker` (o en docker-compose.yml directo)

```bash
# ============================================================
# INTRANET BANCARIA - DOCKER ENVIRONMENT
# ============================================================

# SQL Server
SA_PASSWORD=YourDockerPassword456!@
MSSQL_PID=Express
MSSQL_SA_PASSWORD=YourDockerPassword456!@

# Backend Service
SPRING_DATASOURCE_URL=jdbc:sqlserver://mssql-server:1433;databaseName=IntranetBancaria
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=YourDockerPassword456!@
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_PROFILES_ACTIVE=production

# Backend Java Options
JAVA_OPTS=-Xmx512m -Xms256m
SERVER_PORT=8080

# JWT (MISMO QUE DEVELOPMENT, O CAMBIAR EN PRODUCCIÓN)
APPLICATION_SECURITY_JWT_SECRET=your-docker-secret-key-256-bits-minimum-required!
APPLICATION_SECURITY_JWT_EXPIRATION=86400000

# CORS
APPLICATION_SECURITY_CORS_ALLOWED_ORIGINS=http://localhost:4200,http://nginx

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_BANCO_INTRANET=INFO

# Frontend
ANGULAR_APP_API_URL=http://localhost:8080
NODE_ENV=production

# Nginx
NGINX_PORT=80
BACKEND_SERVICE=backend:8080
FRONTEND_SERVICE=frontend:4200

# General
TIMEZONE=UTC
ENVIRONMENT=docker
```

---

## Production (.env.production)

Archivo: `.env.production` (GITIGNORED - NO COMMITEAR)

```bash
# ============================================================
# INTRANET BANCARIA - PRODUCTION ENVIRONMENT
# ⚠️  SENSITIVE - NUNCA COMMITEAR ESTE ARCHIVO
# ============================================================

# Backend Configuration
BACKEND_PORT=8080
BACKEND_URL=https://api.banco.local
SPRING_PROFILES_ACTIVE=production

# Frontend Configuration
FRONTEND_PORT=443
FRONTEND_URL=https://intranet.banco.local
ANGULAR_ENV=production
ANGULAR_PRODUCTION=true

# Database - SQL Server (REPLICA PRODUCCIÓN)
DATABASE_HOST=prod-db-server.banco.local
DATABASE_PORT=1433
DATABASE_NAME=IntranetBancaria_PROD
DATABASE_USER=sa_prod
DATABASE_PASSWORD=SUPER_SECRET_PASSWORD_MIN_32_CHARS_PRODUCED_BY_DevOps!
DATABASE_CONNECTION_POOL_SIZE=20
DATABASE_MAX_LIFETIME=1800000

# JWT Configuration (GENERAR NUEVAS CLAVES PARA PRODUCCIÓN)
JWT_SECRET=production-jwt-secret-key-256-bits-minimum-absolutely-required-and-unique!
JWT_EXPIRATION=86400000  # 24 hours
JWT_REFRESH_EXPIRATION=604800000  # 7 days

# CORS - SOLO DOMINIO PRODUCCIÓN
CORS_ALLOWED_ORIGINS=https://intranet.banco.local
CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS
CORS_ALLOWED_HEADERS=Content-Type,Authorization
CORS_ALLOW_CREDENTIALS=true

# SSL/TLS Configuration
SSL_ENABLED=true
SSL_KEY_STORE=/etc/ssl/certificates/keystore.p12
SSL_KEY_STORE_PASSWORD=keystore-password-here
SSL_KEY_ALIAS=banco-intranet

# Logging - IMPORTANTE PARA AUDIT
LOG_LEVEL=WARN  # Cambiar a INFO para debugging
LOG_FILE=/var/log/intranet/application.log
LOG_MAX_SIZE=100MB
LOG_MAX_HISTORY=30  # Guardar 30 días

# File Upload
MAX_FILE_SIZE=52428800  # 50 MB in bytes
UPLOAD_DIRECTORY=/var/data/intranet/uploads
BACKUP_DIRECTORY=/var/backups/intranet

# Email Configuration (SendGrid o equivalente)
SMTP_HOST=smtp.sendgrid.net
SMTP_PORT=587
SMTP_USER=apikey
SMTP_PASSWORD=SG.production-sendgrid-key-here
SMTP_FROM=noreply@banco.local
SMTP_TLS_ENABLED=true

# Security - HARDENED
FAILED_LOGIN_ATTEMPTS=3  # Más restrictivo
ACCOUNT_LOCK_DURATION=1800  # 30 minutes
PASSWORD_MIN_LENGTH=12  # NUNCA 8
PASSWORD_REQUIRE_UPPERCASE=true
PASSWORD_REQUIRE_LOWERCASE=true
PASSWORD_REQUIRE_NUMBERS=true
PASSWORD_REQUIRE_SPECIAL=true
PASSWORD_EXPIRATION_DAYS=90
ENCRYPTION_ALGORITHM=AES-256-GCM

# Rate Limiting
RATE_LIMIT_ENABLED=true
RATE_LIMIT_REQUESTS=100
RATE_LIMIT_WINDOW_MINUTES=1

# Clustering/HA (si aplicable)
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=20
SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=5

# Monitoring & Alerting
MONITORING_ENABLED=true
MONITORING_ENDPOINT=https://monitoring.banco.local
NEW_RELIC_LICENSE_KEY=new-relic-key-here
DATADOG_API_KEY=datadog-key-here

# Backup Configuration
BACKUP_ENABLED=true
BACKUP_SCHEDULE=0 2 * * * # 2 AM daily
BACKUP_RETENTION_DAYS=30
BACKUP_ENCRYPTION=true

# Feature Flags
FEATURE_RAG_CHATBOT=false  # Habilitar cuando esté listo
FEATURE_COMMUNICATIONS=false
FEATURE_ANALYTICS=true

# API Integrations (Seguras)
OPENAI_API_KEY=${OPENAI_KEY}  # De secrets manager
PINECONE_API_KEY=${PINECONE_KEY}
SENDGRID_API_KEY=${SENDGRID_KEY}

# Infrastructure
ENVIRONMENT=production
DEPLOYMENT_VERSION=1.0.0
GIT_COMMIT_SHA=from-ci-pipeline
ENVIRONMENT_REGION=us-east-1
TIMEZONE=America/New_York
```

---

## Backend Properties

### application.properties (Main)

```properties
# ============================================================
# INTRANET BANCARIA - SPRING BOOT MAIN CONFIGURATION
# ============================================================

# Server Configuration
server.port=8080
server.servlet.context-path=/
server.error.include-message=always
server.error.include-binding-errors=always

# Database Configuration (SQL Server)
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=IntranetBancaria;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=${DATABASE_PASSWORD}
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA / Hibernate
spring.jpa.database-platform=org.hibernate.dialect.SQLServer2012Dialect
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Connection Pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000

# Jackson Configuration
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.time-zone=UTC
spring.jackson.default-property-inclusion=non_null

# Logging
logging.level.root=WARN
logging.level.com.banco.intranet=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.file.name=logs/application.log
logging.file.max-size=10MB
logging.file.max-history=30

# JWT Configuration
application.security.jwt.secret=${JWT_SECRET}
application.security.jwt.expiration=${JWT_EXPIRATION:86400000}
application.security.jwt.refresh-expiration=${JWT_REFRESH_EXPIRATION:604800000}

# CORS Configuration
application.security.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:4200}
application.security.cors.allowed-methods=${CORS_ALLOWED_METHODS:GET,POST,PUT,DELETE,OPTIONS}
application.security.cors.allowed-headers=${CORS_ALLOWED_HEADERS:Content-Type,Authorization}
application.security.cors.allow-credentials=true
application.security.cors.max-age=3600

# File Upload
application.file.upload.directory=${UPLOAD_DIRECTORY:./uploads}
application.file.max-size=${MAX_FILE_SIZE:10485760}

# Audit Configuration
application.audit.enabled=true
application.audit.log-all-requests=true

# Request Logging
application.logging.request-response.enabled=true
application.logging.request-response.log-body=true
```

### application-dev.properties (Development)

```properties
# ============================================================
# INTRANET BANCARIA - DEVELOPMENT PROFILE
# ============================================================

# Override properties for development

# Logging - More verbose
logging.level.root=INFO
logging.level.com.banco.intranet=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Database - Local
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=IntranetBancaria;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YourPassword123!

# JPA / Hibernate - Show SQL
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# CORS - Allow localhost
application.security.cors.allowed-origins=http://localhost:4200,http://localhost:3000

# File Upload
application.file.upload.directory=./uploads

# Actuator endpoints (for health checks)
management.endpoints.web.exposure.include=health,metrics,prometheus
management.endpoint.health.show-details=always
```

### application-prod.properties (Production)

```properties
# ============================================================
# INTRANET BANCARIA - PRODUCTION PROFILE
# ============================================================

# Override properties for production (USE .env.production variables)

# Database - Production Server
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}

# Connection Pool - Production optimized
spring.datasource.hikari.maximum-pool-size=30
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000

# JPA - No SQL logging in production
spring.jpa.show-sql=false
logging.level.root=WARN
logging.level.com.banco.intranet=INFO
logging.level.org.springframework.web=WARN

# File Upload - Production path
application.file.upload.directory=${UPLOAD_DIRECTORY:/var/data/intranet/uploads}

# SSL/TLS
server.ssl.enabled=true
server.ssl.key-store=${SSL_KEY_STORE}
server.ssl.key-store-password=${SSL_KEY_STORE_PASSWORD}
server.ssl.key-alias=${SSL_KEY_ALIAS}
server.ssl.protocol=TLSv1.2

# Security - Strict CORS
application.security.cors.allowed-origins=${CORS_ALLOWED_ORIGINS}

# Actuator - Minimal exposure
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=when-authorized
```

---

## Frontend Environment

### environment.ts (Development)

```typescript
// src/environments/environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
  tokenKey: 'token',
  refreshTokenKey: 'refreshToken',
  userKey: 'user',
  logLevel: 'debug',
  requestTimeout: 30000,
  features: {
    ragChatbot: false,
    communications: false,
    analytics: false
  }
};
```

### environment.prod.ts (Production)

```typescript
// src/environments/environment.prod.ts
export const environment = {
  production: true,
  apiUrl: 'https://api.banco.local',
  tokenKey: 'token',
  refreshTokenKey: 'refreshToken',
  userKey: 'user',
  logLevel: 'error',
  requestTimeout: 30000,
  features: {
    ragChatbot: false,
    communications: false,
    analytics: true
  }
};
```

---

## Database Connection

### Connection String Templates

```csharp
// C# / .NET (para futuras integraciones)
// Development
Server=localhost,1433;Database=IntranetBancaria;User Id=sa;Password=YourPassword123!;Encrypt=true;TrustServerCertificate=true;

// Production
Server=prod-db-server.banco.local,1433;Database=IntranetBancaria_PROD;User Id=sa_prod;Password=SECURE_PASSWORD;Encrypt=true;TrustServerCertificate=false;
```

```python
# Python (para futuras integraciones)
# Development
pyodbc.connect(
    'Driver={ODBC Driver 17 for SQL Server};'
    'Server=localhost,1433;'
    'Database=IntranetBancaria;'
    'UID=sa;'
    'PWD=YourPassword123!;'
    'Encrypt=yes;'
    'TrustServerCertificate=yes;'
)

# Production
pyodbc.connect(
    'Driver={ODBC Driver 17 for SQL Server};'
    'Server=prod-db-server.banco.local,1433;'
    'Database=IntranetBancaria_PROD;'
    'UID=sa_prod;'
    'PWD=SECURE_PASSWORD;'
    'Encrypt=yes;'
    'TrustServerCertificate=no;'
)
```

---

## 🚀 Setup Rápido

### 1. Development (Local)

```bash
# Frontend
export ANGULAR_ENV=development
export ANGULAR_APP_API_URL=http://localhost:8080

# Backend
export SPRING_PROFILES_ACTIVE=dev
export DATABASE_PASSWORD=YourPassword123!

# Run
./setup.sh  # o setup.bat
```

### 2. Docker

```bash
# Cargar variables
export $(cat .env.docker | xargs)

# Ejecutar
docker-compose up -d
```

### 3. Production (DevOps)

```bash
# Load from secure vault (HashiCorp Vault, AWS Secrets Manager, etc.)
# NUNCA cometer .env.production

# Deployment
docker run \
  -e SPRING_PROFILES_ACTIVE=production \
  -e DATABASE_PASSWORD=$(get-secret production/db-password) \
  -e JWT_SECRET=$(get-secret production/jwt-secret) \
  intranet-backend:1.0
```

---

## ✅ Checklist de Variables

### Mínimo para Development
- [ ] `DATABASE_HOST`
- [ ] `DATABASE_USER`
- [ ] `DATABASE_PASSWORD`
- [ ] `JWT_SECRET` (puede ser cualquier cosa)
- [ ] `CORS_ALLOWED_ORIGINS` = `http://localhost:4200`

### Requerido para Production
- [ ] Todas las de development
- [ ] `SSL_KEY_STORE` & `SSL_KEY_STORE_PASSWORD`
- [ ] `JWT_SECRET` (256+ bits, complejo)
- [ ] `SMTP_HOST` & `SMTP_PASSWORD`
- [ ] `DATABASE_PASSWORD` (Super segura)
- [ ] Monitoreo (New Relic, DataDog, etc)
- [ ] Backup configuration

---

## 🔐 Security Best Practices

1. **NUNCA commitear .env.production**
   ```bash
   echo ".env.production" >> .gitignore
   ```

2. **Usar secrets manager**
   ```bash
   # AWS Secrets Manager
   aws secretsmanager get-secret-value --secret-id prod/intranet/jwt-secret
   
   # HashiCorp Vault
   vault kv get secrets/intranet/jwt-secret
   ```

3. **Rotación de secretos**
   ```bash
   # JWT secret cada 90 días
   # Database password cada 30 días
   # SSL certificates: validar 30 días antes de expiración
   ```

4. **Validación de variables**
   ```bash
   # Script de validación
   if [ -z "$JWT_SECRET" ] || [ ${#JWT_SECRET} -lt 256 ]; then
       echo "ERROR: JWT_SECRET inválido o muy corto"
       exit 1
   fi
   ```

---

Consultar: [README.md](README.md) | [CHEATSHEET.md](CHEATSHEET.md) | [DEPLOYMENT.md](DEPLOYMENT.md)
