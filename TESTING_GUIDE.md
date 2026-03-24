# TESTING GUIDE - INTRANET BANCARIA

Guía completa para testing del sistema funcionando end-to-end.

## 📋 Tabla de Contenidos

1. [Setup de Testing](#setup-de-testing)
2. [Testing Manual](#testing-manual)
3. [Testing de API](#testing-de-api)
4. [Testing de Seguridad](#testing-de-seguridad)
5. [Casos de Prueba Críticos](#casos-de-prueba-críticos)
6. [Performance Testing](#performance-testing)

---

## Setup de Testing

### Requisitos Previos

```bash
# Backend ejecutando
cd backend
mvn spring-boot:run -Dspring-boot.run.arguments='--spring.profiles.active=dev'

# Frontend ejecutando
cd frontend
npm start

# Base de datos SQL Server operativa
# - Servidor: localhost:1433
# - BD: IntranetBancaria
# - Usuario: sa
```

### Verificar Conectividad

```bash
# Backend health check
curl http://localhost:8080/auth/health

# Respuesta esperada:
# {"status":"UP","timestamp":"2024-01-15T10:30:45"}

# Frontend cargando
# Abre: http://localhost:4200 en navegador
```

---

## Testing Manual

### 1️⃣ Autenticación (Auth Flow)

#### Caso 1.1: Login Exitoso

**Pasos:**
1. Abre http://localhost:4200
2. Ingresa usuario: `admin@banco.local`
3. Ingresa contraseña: `AdminPassword123!`
4. Click en "Ingresar"

**Resultado Esperado:**
```
✅ Redirige a /auth/dashboard
✅ Muestra el nombre del usuario en toolbar
✅ Token guardado en localStorage
✅ Sidebar carga con opciones (Dashboard, Noticias, Documentos, Admin)
```

**Verificar en DevTools (F12):**
```javascript
// Console → ApplicationStorage → LocalStorage
localStorage.getItem('token')        // Debe retornar token JWT
localStorage.getItem('user')         // Debe retornar user JSON
```

#### Caso 1.2: Login Fallido (Credenciales Incorrectas)

**Pasos:**
1. Ingresa usuario: `admin@banco.local`
2. Contraseña: `WrongPassword123!`
3. Click Ingresar

**Resultado Esperado:**
```
✅ Mensaje de error: "Credenciales inválidas"
✅ Permanece en /auth/login
❌ No se almacena token
```

#### Caso 1.3: Account Locking (5 Intentos Fallidos)

**Pasos:**
1. Repite Caso 1.2 cinco veces seguidas

**Resultado Esperado:**
```
✅ Después del 5to intento fallido:
   "Cuenta bloqueada. Contacte al administrador"
✅ Campo usuario deshabilitado
❌ No se puede ingresar aunque contraseña sea correcta
```

#### Caso 1.4: Token Refresh

**Pasos:**
1. Login exitoso
2. Abre DevTools → Application → LocalStorage
3. Guarda valor de `refreshToken`
4. Espera 5 minutos (o hasta que token expire)
5. Haz request a cualquier endpoint protegido

**Resultado Esperado:**
```
✅ Sistema automáticamente obtiene nuevo token
✅ LocalStorage token se actualiza
✅ Seguir navegando sin salir/entrar
```

---

### 2️⃣ Dashboard

**Pasos:**
1. Login exitoso
2. Navega a Dashboard

**Resultado Esperado:**
```
✅ Muestra 4 widgets:
   - Usuarios Activos: 145/150
   - Noticias: 12
   - Documentos: 287
   - Sistema: OPERATIVO
✅ Fondo con gradientes visibles
✅ Números actualizados desde BD
```

---

### 3️⃣ Gestión de Usuarios (Admin Only)

**Pasos:**
1. Login como admin
2. Navega a Admin → Usuarios

**Resultado Esperado:**
```
✅ Tabla con todos los usuarios
✅ Botón "Nuevo Usuario"
✅ Acciones: Ver, Editar, Desactivar
❌ "Eliminar" no disponible (audit trail)
```

**Crear nuevo usuario:**
```
Nombre: Juan
Apellido: Pérez
Email: juan.perez@banco.local
Número Empleado: EMP002
Departamento: Cajas
Puesto: Cajero
Rol: USUARIO
```

**Resultado:**
```
✅ Usuario creado con contraseña temporal
✅ Email debe ser único (rechazo si duplica)
✅ Aparece en tabla inmediatamente
✅ Entra en auditoria
```

---

### 4️⃣ Noticias (Editor + Admin)

**Pasos:**
1. Login como usuario con rol EDITOR
2. Navega a Noticias

**Resultado Esperado:**
```
✅ Tabla con noticias publicadas
✅ Botón "Nueva Noticia" (solo EDITOR/ADMIN)
✅ Contador de lecturas visible
```

**Crear noticia:**
```
Título: "Actualización de Sistemas Bancarios"
Contenido: "Se realizará mantenimiento el domingo..."
Prioridad: ALTA (color rojo)
Categoría: MANTENIMIENTO
Fecha Publicación: hoy
Fecha Expiración: +7 días
```

**Resultado:**
```
✅ Noticia se publica inmediatamente
✅ Aparece en tabla con prioridad destacada
✅ Se registra acción en auditoria
✅ Contador de lecturas = 0 (incrementa con acceso)
```

---

### 5️⃣ Documentos

**Pasos:**
1. Login como cualquier usuario
2. Navega a Documentos

**Resultado Esperado:**
```
✅ Tabla con documentos disponibles
✅ Filtrar por clasificación: PÚBLICO, INTERNO, CONFIDENCIAL
✅ Búsqueda por título/etiquetas
```

**Descargar documento:**
```
1. Click en documento clasificado PUBLIC
2. Click botón "Descargar"
```

**Resultado:**
```
✅ Descarga archivo
✅ Contador de descargas incrementa
❌ Documentos CONFIDENCIAL solo admin
```

---

## Testing de API

### Tool: Postman / cURL

### Endpoint: Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": "admin@banco.local",
    "contrasena": "AdminPassword123!"
  }'
```

**Respuesta Exitosa (200):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "usuarioId": 1,
  "email": "admin@banco.local",
  "nombreCompleto": "Administrador Sistema"
}
```

### Endpoint: Get Usuarios (Protegido)

```bash
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer {TOKEN_AQUI}"
```

**Respuesta (200):**
```json
{
  "success": true,
  "message": "Usuarios obtenidos exitosamente",
  "data": [
    {
      "id": 1,
      "nombre": "Administrador",
      "apellido": "Sistema",
      "email": "admin@banco.local",
      "departamento": "TI",
      "puesto": "Admin",
      "activo": true
    }
  ]
}
```

### Endpoint: Create Usuario (Admin Only)

```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer {TOKEN_ADMIN}" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Carlos",
    "apellido": "García",
    "email": "carlos.garcia@banco.local",
    "numeroEmpleado": "EMP003",
    "departamento": "Créditos",
    "puesto": "Oficial Crédito",
    "rol": "USUARIO"
  }'
```

**Respuesta (201):**
```json
{
  "success": true,
  "message": "Usuario creado exitosamente",
  "data": {
    "id": 2,
    "nombre": "Carlos",
    "email": "carlos.garcia@banco.local",
    "contrasenaGenerada": "TempPass123!@"
  }
}
```

### Endpoint: Cambiar Contraseña

```bash
curl -X PUT http://localhost:8080/api/usuarios/{usuarioId}/cambiar-contrasena \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "contrasenaActual": "AdminPassword123!",
    "nuevaContrasena": "NewPassword456!@",
    "confirmacion": "NewPassword456!@"
  }'
```

**Respuesta (200):**
```json
{
  "success": true,
  "message": "Contraseña actualizada exitosamente"
}
```

---

## Testing de Seguridad

### 1️⃣ CORS Policy

**Test: Request desde origen no autorizado**

```bash
curl -X OPTIONS http://localhost:8080/api/usuarios \
  -H "Origin: http://evil.com" \
  -H "Access-Control-Request-Method: GET"
```

**Resultado Esperado:**
```
❌ Header `Access-Control-Allow-Origin` NO contiene evil.com
✅ Solo localhost:4200 permitido
```

### 2️⃣ JWT Validation

**Test: Token inválido**

```bash
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer invalid.token.here"
```

**Resultado Esperado (401):**
```json
{
  "success": false,
  "errorCode": "INVALID_TOKEN",
  "message": "Token inválido o expirado"
}
```

### 3️⃣ Role-Based Access Control

**Test: Usuario sin permiso intentando acceso admin**

```bash
# Login como usuario normal
curl -X POST http://localhost:8080/auth/login \
  -d '{"usuario":"user@banco.local","contrasena":"password"}'

# Obtener token de respuesta anterior

# Intentar crear usuario (solo admin)
curl -X POST http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer {TOKEN_USER}" \
  -d '{...datos del usuario}'
```

**Resultado Esperado (403):**
```json
{
  "success": false,
  "errorCode": "FORBIDDEN",
  "message": "No tienes permiso para acceder a este recurso"
}
```

### 4️⃣ SQL Injection Test

**Intento de ataque:**
```bash
curl -X GET "http://localhost:8080/api/usuarios?buscar=admin' OR '1'='1"
```

**Resultado Esperado:**
```
✅ Query utiliza parameterized queries (JPA)
❌ Inyección bloqueada
✅ Retorna solo resultados legítimos
```

### 5️⃣ Password Hashing Verification

**Test: Contraseña no almacenada en texto plano**

```bash
# Conectar a BD:
SELECT contrasena FROM usuarios WHERE email='admin@banco.local'
```

**Resultado Esperado:**
```
✅ Contraseña es hash BCrypt (comienza con $2a$10$)
❌ NUNCA en texto plano
```

---

## Casos de Prueba Críticos

### Test Suite Esencial

| ID | Funcionalidad | Pasos | Resultado | Estado |
|----|---------------|-------|-----------|--------|
| TC001 | Login válido | usuario+contraseña correcto | Acceso dashboard | 🟢 |
| TC002 | Login inválido | contraseña incorrecta | Msg error, sin acceso | 🟢 |
| TC003 | Account Lock | 5 intentos fallidos | Cuenta se bloquea | 🟢 |
| TC004 | Crear usuario | POST /usuarios + admin | Usuario creado | 🟢 |
| TC005 | Cambiar contraseña | PUT con contraseña actual | Contraseña actualizada | 🟢 |
| TC006 | Crear noticia | POST /noticias + editor | Noticia publicada | 🟢 |
| TC007 | Auditoria log | Cualquier acción | Registrada en BD | 🟢 |
| TC008 | Role denial | User intenta admin action | 403 Forbidden | 🟢 |
| TC009 | Token expired | Request con token expirado | 401 Unauthorized | 🟢 |
| TC010 | CORS violation | Request desde otro dominio | 403 CORS error | 🟢 |

---

## Performance Testing

### Load Test con Apache JMeter

```xml
<!-- jmeter-testplan.jmx -->
<jmeterTestPlan version="1.2">
  <hashTree>
    <ThreadGroup>
      <elementProp name="ThreadGroup.main_controller">
        <elementProp name="Arguments" guiclass="ArgumentsPanel">
          <collectionProp name="Arguments.arguments">
            <elementProp name="threads">
              <stringProp name="Argument.value">50</stringProp>
            </elementProp>
            <elementProp name="duration">
              <stringProp name="Argument.value">300</stringProp>
            </elementProp>
          </collectionProp>
        </elementProp>
      </elementProp>
    </ThreadGroup>
    
    <HTTPSampler>
      <elementProp name="HTTPsampler.Arguments">
        <collectionProp name="Arguments.arguments">
          <elementProp name="method">
            <stringProp name="Argument.value">GET</stringProp>
          </elementProp>
          <elementProp name="path">
            <stringProp name="Argument.value">/api/usuarios</stringProp>
          </elementProp>
        </collectionProp>
      </elementProp>
    </HTTPSampler>
  </hashTree>
</jmeterTestPlan>
```

**Métricas a Monitorear:**
```
Throughput: > 1000 req/sec
Response Time P99: < 500ms
Error Rate: 0%
Connection Pool: < 100 conexiones activas
Memory: < 2GB
CPU: < 80%
```

### Database Performance

```sql
-- Verificar índices
SELECT * FROM sys.dm_db_index_usage_stats
WHERE database_id = DB_ID('IntranetBancaria')
ORDER BY user_seeks + user_scans DESC;

-- Query slow queries
SELECT * FROM sys.dm_exec_query_stats
ORDER BY total_elapsed_time DESC;
```

---

## ✅ Checklist de Testing Completo

- [ ] Login exitoso con token persistencia
- [ ] Login fallido bloquea acceso
- [ ] Account locking después 5 fallos
- [ ] Dashboard carga estadísticas
- [ ] CRUD usuarios (admin only)
- [ ] CRUD noticias (editor+)
- [ ] CRUD documentos
- [ ] Acceso denegado a roles sin permiso
- [ ] Auditoria registra todas acciones
- [ ] Token refresh automático
- [ ] CORS permite solo localhost:4200
- [ ] SQL Injection prevenido
- [ ] Contraseñas hasheadas (BCrypt)
- [ ] Errores retornan código HTTP correcto
- [ ] Performance > 1000 req/sec

---

## 🐛 Troubleshooting Testing

### Error: Connection Refused (localhost:8080)

```bash
# Backend no está ejecutándose
cd backend
mvn spring-boot:run -Dspring-boot.run.arguments='--spring.profiles.active=dev'
```

### Error: 401 Unauthorized sin token

```bash
# Agregar header Authorization:
-H "Authorization: Bearer {TOKEN_AQUI}"
```

### Error: java.sql.SQLException

```bash
# Verificar SQL Server:
# 1. Puerto: 1433
# 2. Base de datos: IntranetBancaria
# 3. Script ejecutado: 01_create_database.sql
```

### Error: CORS blocked

```
Access to XMLHttpRequest blocked by CORS policy
```

**Solución:**
- Abre `application.properties`
- Verifica `application.security.cors.allowed-origins=http://localhost:4200`

---

## Conclusión

Una vez completado este testing guide:

1. ✅ Sistema autenticado y autorizado
2. ✅ APIs funcionan correctamente
3. ✅ Seguridad validada
4. ✅ Performance aceptable
5. ✅ **LISTA PARA PRODUCCIÓN** ✨

Para siguiente fase ver: [DEPLOYMENT.md](DEPLOYMENT.md)
