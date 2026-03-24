# CHEATSHEET - INTRANET BANCARIA

Referencia rápida de comandos y snippets frecuentes.

## 🚀 Startup Rápido

```bash
# Setup inicial (primera vez)
./setup.sh (Linux/Mac)
setup.bat (Windows)

# Backend (development)
cd backend
mvn spring-boot:run -Dspring-boot.run.arguments='--spring.profiles.active=dev'

# Frontend (development)
cd frontend
npm install
npm start

# Backend compilar + jar
mvn clean install
java -jar target/intranet-1.0.0.jar

# Frontend build production
npm run build
```

---

## 📊 Docker

```bash
# Build all images
docker-compose build

# Run all services
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop all
docker-compose down

# Rebuild single service
docker-compose up -d --build backend

# SSH into container
docker exec -it intranet-backend /bin/bash
```

---

## 🗄️ Database

### SQL Server Commands

```sql
-- Connect
sqlcmd -S localhost,1433 -U sa -P YourPassword

-- List databases
SELECT name FROM sys.databases;

-- Select database
USE IntranetBancaria;

-- Common queries
SELECT * FROM usuarios WHERE activo = 1;
SELECT * FROM usuarios WHERE email = 'admin@banco.local';
SELECT COUNT(*) FROM noticias WHERE activa = 1;
SELECT * FROM auditoria ORDER BY fecha_accion DESC;

-- Reset admin password (hashed)
UPDATE usuarios 
SET contrasena = '$2a$10$N9qo8uLO...' 
WHERE email = 'admin@banco.local';

-- Unlock user
UPDATE usuarios 
SET cuenta_bloqueada = 0, intentos_fallidos = 0 
WHERE email = 'user@banco.local';

-- Check audit trail
SELECT usuario, accion, modulo, resultado, fecha_accion 
FROM auditoria 
WHERE usuario_id = 1 
ORDER BY fecha_accion DESC;

-- Database size
EXEC sp_spaceused 'IntranetBancaria';

-- Check indexes
SELECT * FROM sys.dm_db_index_usage_stats 
WHERE database_id = DB_ID('IntranetBancaria');
```

---

## 🔐 API Testing

### Postman Collection (Quick URLs)

```
Base URL: http://localhost:8080

POST /auth/login
Body: {
  "usuario": "admin@banco.local",
  "contrasena": "AdminPassword123!"
}

GET /api/usuarios
Header: Authorization: Bearer {TOKEN}

POST /api/usuarios
Header: Authorization: Bearer {TOKEN}
Body: {
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@banco.local",
  "numeroEmpleado": "EMP002"
}

GET /api/noticias?page=0&size=10
Header: Authorization: Bearer {TOKEN}

POST /api/noticias
Header: Authorization: Bearer {TOKEN}
Body: {
  "titulo": "News Title",
  "contenido": "Content here",
  "prioridad": "ALTA",
  "categoria": "MANTENIMIENTO"
}

GET /dashboard/datos
Header: Authorization: Bearer {TOKEN}
```

### cURL Commands

```bash
# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": "admin@banco.local",
    "contrasena": "AdminPassword123!"
  }'

# Get usuarios (save TOKEN from login response)
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $TOKEN"

# Create usuario
curl -X POST http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Carlos",
    "apellido": "García",
    "email": "carlos@banco.local",
    "numeroEmpleado": "EMP003",
    "departamento": "Créditos",
    "puesto": "Officer"
  }'

# Create noticia
curl -X POST http://localhost:8080/api/noticias \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "System Maintenance",
    "contenido": "Mantenimiento previsto",
    "prioridad": "ALTA"
  }'
```

---

## 🧪 Testing

```bash
# Backend - Run all tests
cd backend
mvn test

# Backend - Run single test
mvn test -Dtest=UsuarioServiceTest

# Backend - Run with coverage
mvn clean test jacoco:report

# Frontend - Run tests
cd frontend
npm test

# Frontend - Run single spec
npm test -- --include='**/usuario.service.spec.ts'

# Frontend - E2E tests
npm run e2e
```

---

## 📝 Logging

### Backend - View Logs

```bash
# Real-time logs
docker-compose logs -f backend

# Last 100 lines
docker logs backend --tail 100

# Save to file
docker logs backend > backend_logs.txt

# Application log file location
cat logs/application.log
```

### Frontend - DevTools

```javascript
// Browser Console
console.log('Debug message');

// Check stored data
localStorage.getItem('token')
localStorage.key(0)

// Angular Debug
ng.probe($0).componentInstance  // Get component instance
```

---

## 🔧 Development Shortcuts

### Java/Spring Boot

```java
// Lombok getters/setters
@Data
@NoArgsConstructor
@AllArgsConstructor

// Logging
@Slf4j
log.info("Message: {}", variable);
log.error("Error: ", exception);

// Repository custom query
@Query("SELECT u FROM UsuarioEntity u WHERE u.email = :email")
Optional<UsuarioEntity> findByEmail(@Param("email") String email);

// Service exception handling
throw new AppException("Mensaje", HttpStatus.NOT_FOUND);

// Controller endpoint
@PostMapping
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ApiResponseDTO<DTO>> crear(@Valid @RequestBody DTO dto)

// Pagination
Page<DTO> page = service.listar(PageRequest.of(0, 10));
```

### Angular/TypeScript

```typescript
// Create component quickly
ng generate component features/modulo/components/nombre

// Service injection
constructor(private service: MyService) { }

// Observable pattern
items$: Observable<Item[]>;
items$.subscribe(items => { });

// Async pipe
{{ items$ | async }}

// Form validation
form = new FormGroup({
  email: new FormControl('', [Validators.required, Validators.email])
});

// Routing programmatic
this.router.navigate(['/dashboard']);

// Guards
canActivate(): boolean { return this.authService.isAuthenticated(); }
```

---

## 🐛 Common Issues & Fixes

### Backend Issues

| Problem | Solution |
|---------|----------|
| 401 Unauthorized | Asegurar token en header: `Authorization: Bearer {TOKEN}` |
| 403 Forbidden | Verificar rol en @PreAuthorize |
| Database connection error | Verificar SQL Server running, credentials en application.properties |
| ClassNotFoundException | Run `mvn clean install` para descargar dependencies |
| CORS error | Verificar CORS config permite localhost:4200 |

### Frontend Issues

| Problem | Solution |
|---------|----------|
| Blank page | Verificar console para errores, run `npm install` |
| Route not working | Verificar routes en app.routes.ts y canActivate |
| API 404 | Verificar backend running, URL API correcta |
| Token expired | Refresh token automático o re-login |
| Material no aplica estilos | Verificar import en styles.css |

### Database Issues

| Problem | Solution |
|---------|----------|
| Connection refused | `telnet localhost 1433` para verificar SQL Server |
| Duplicate key | Email/número_empleado debe ser único |
| Foreign key violation | Verificar entidad relacionada existe |
| Query slow | Verificar índices: `SELECT * FROM sys.indexes` |

---

## 📦 Build & Deployment

### Backend Build

```bash
# Create jar
mvn clean package

# Build with profile
mvn clean package -Pproduction

# Run jar
java -jar target/intranet-1.0.0.jar

# Run with custom properties
java -jar target/intranet-1.0.0.jar \
  --spring.datasource.url=jdbc:sqlserver://prod-db:1433;databaseName=Intranet \
  --server.port=8080
```

### Frontend Build

```bash
# Development build
npm run build -- --configuration development

# Production build
npm run build -- --configuration production

# Serve production build
npx http-server dist/frontend -p 4200

# Analyze bundle
npm run build -- --stats-json
npx webpack-bundle-analyzer dist/frontend/stats.json
```

### Docker Deployment

```bash
# Build image
docker build -t intranet-backend:1.0 backend/

# Run container
docker run -d -p 8080:8080 \
  -e DATABASE_URL=jdbc:sqlserver://db:1433 \
  -e DATABASE_USER=sa \
  -e DATABASE_PASSWORD=YourPassword \
  intranet-backend:1.0

# Push to registry
docker tag intranet-backend:1.0 registry.example.com/intranet-backend:1.0
docker push registry.example.com/intranet-backend:1.0
```

---

## 🔄 Git Workflow

```bash
# Clone repo
git clone <repo-url>
cd intranet

# Create feature branch
git checkout -b feature/nueva-funcionalidad

# Commit changes
git add .
git commit -m "feat: agregar nueva funcionalidad"

# Push branch
git push origin feature/nueva-funcionalidad

# Create pull request (GitHub/GitLab)

# After merge, pull main
git checkout main
git pull origin main

# Cleanup local branch
git branch -d feature/nueva-funcionalidad
```

---

## 📚 File Locations Reference

| Archivo | Uso |
|---------|-----|
| `application.properties` | Config principal (DB, JWT, CORS) |
| `application-dev.properties` | Config desarrollo |
| `pom.xml` | Dependencias Maven |
| `package.json` | Dependencias npm |
| `SecurityConfig.java` | Configuración seguridad Spring |
| `GlobalExceptionHandler.java` | Manejo global errores |
| `app.routes.ts` | Rutas frontend |
| `auth.guard.ts` | Protección rutas |
| `01_create_database.sql` | Script base de datos |
| `.env` | Variables de entorno |

---

## 💡 Tips & Tricks

```bash
# Buscar en archivos backend
grep -r "UsuarioService" backend/src

# Contar líneas de código
find . -name "*.java" -o -name "*.ts" | xargs wc -l

# Ver cambios Git no guardados
git diff

# Ver commits recientes
git log --oneline -10

# Revertir cambio archivo
git checkout -- src/file.ts

# Format code Java
mvn spotless:apply

# Format code TypeScript
npm run lint -- --fix

# Limpiar caché npm
npm cache clean --force

# Reinstalar dependencies
rm package-lock.json node_modules -rf
npm install
```

---

## 📞 Quick Help

- **Backend API docs**: http://localhost:8080/swagger-ui.html (si Springdoc agregado)
- **Frontend dev server**: http://localhost:4200
- **Database UI**: Azure Data Studio o SQL Server Management Studio
- **Git help**: `git help <comando>`
- **npm help**: `npm help <comando>`
- **Maven help**: `mvn help:describe -Dcmd=command`

---

Más info: README.md | ARCHITECTURE.md | API_DOCUMENTATION.md | DEVELOPMENT_GUIDE.md
