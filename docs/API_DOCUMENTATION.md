# 🔌 DOCUMENTACIÓN API REST - INTRANET BANCARIA

**Versión API:** 1.0.0  
**Base URL:** `http://localhost:8080/api` (local) | `https://api.banco.local` (producción)  
**Formato:** JSON  
**Autenticación:** JWT (Bearer Token)  

---

## 📋 Tabla de Contenidos

1. [Headers Requeridos](#headers-requeridos)
2. [Códigos de Respuesta](#códigos-de-respuesta)
3. [Endpoints - Autenticación](#endpoints---autenticación)
4. [Endpoints - Usuarios](#endpoints---usuarios)
5. [Endpoints - Noticias](#endpoints---noticias)
6. [Endpoints - Documentos](#endpoints---documentos)
7. [Endpoints - Auditoría](#endpoints---auditoría)
8. [Endpoints - Dashboard](#endpoints---dashboard)
9. [Pagination](#pagination)
10. [Error Handling](#error-handling)

---

## 📝 HEADERS REQUERIDOS

```http
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json
Accept: application/json
X-Request-ID: 550e8400-e29b-41d4-a716-446655440000  (Opcional)
```

---

## 📊 CÓDIGOS DE RESPUESTA

| Código | Descripción |
|--------|-------------|
| `200` | OK - Solicitud exitosa |
| `201` | Created - Recurso creado |
| `204` | No Content - Sin contenido |
| `400` | Bad Request - Solicitud inválida |
| `401` | Unauthorized - No autenticado |
| `403` | Forbidden - Acceso denegado |
| `404` | Not Found - Recurso no existe |
| `409` | Conflict - Recurso duplicado |
| `500` | Internal Server Error |

---

## 🔐 ENDPOINTS - AUTENTICACIÓN

### 1. Login

```http
POST /auth/login
Content-Type: application/json

{
  "usuario": "admin@banco.local",
  "contrasena": "AdminPassword123!"
}
```

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
    "noNombreUsuario": "ADM001",
    "email": "admin@banco.local",
    "nombreCompleto": "Admin Sistema",
    "usuarioId": 1
  },
  "timestamp": "2026-03-22T14:30:00"
}
```

**Respuesta 401:**
```json
{
  "success": false,
  "message": "Usuario o contraseña incorrectos",
  "errorCode": "BAD_CREDENTIALS",
  "timestamp": "2026-03-22T14:30:00"
}
```

---

### 2. Refrescar Token

```http
POST /auth/refresh-token
Authorization: Bearer {refreshToken}
```

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Token refrescado exitosamente",
  "data": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "timestamp": "2026-03-22T14:30:00"
}
```

---

## 👥 ENDPOINTS - USUARIOS

### 1. Listar Usuarios (ADMIN)

```http
GET /usuarios?page=0&size=20&sort=id,desc
Authorization: Bearer {token}
```

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Operación exitosa",
  "data": {
    "content": [
      {
        "id": 1,
        "nombre": "Admin",
        "apellido": "Sistema",
        "email": "admin@banco.local",
        "numeroEmpleado": "ADM001",
        "departamento": "Sistemas",
        "puesto": "Administrador",
        "activo": true,
        "cuentaBloqueada": false,
        "roles": ["ADMIN"],
        "fechaUltimoLogin": "2026-03-22T14:30:00",
        "fechaCreacion": "2026-03-01T08:00:00"
      }
    ],
    "totalElements": 150,
    "totalPages": 8,
    "currentPage": 0
  },
  "timestamp": "2026-03-22T14:30:00"
}
```

---

### 2. Obtener Usuario por ID

```http
GET /usuarios/{id}
Authorization: Bearer {token}
```

**Respuesta 200:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "nombre": "Admin",
    "apellido": "Sistema",
    "email": "admin@banco.local",
    ...
  }
}
```

---

### 3. Crear Usuario (ADMIN)

```http
POST /usuarios
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan.perez@banco.local",
  "numeroEmpleado": "EMP001",
  "departamento": "Operaciones",
  "puesto": "Ejecutivo"
}
```

**Respuesta 201:**
```json
{
  "success": true,
  "message": "Usuario creado exitosamente",
  "data": {
    "id": 2,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan.perez@banco.local",
    "numeroEmpleado": "EMP001",
    "activo": true,
    "fechaCreacion": "2026-03-22T14:30:00"
  }
}
```

---

### 4. Actualizar Usuario

```http
PUT /usuarios/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Juan",
  "apellido": "Pérez García",
  "departamento": "Ventas",
  "puesto": "Gerente de Ventas"
}
```

---

### 5. Cambiar Contraseña

```http
POST /usuarios/{id}/cambiar-contrasena
Authorization: Bearer {token}
Content-Type: application/json

{
  "contrasenaActual": "OldPassword123!",
  "contrasenaNueva": "NewPassword456!",
  "confirmacion": "NewPassword456!"
}
```

---

### 6. Desactivar Usuario (ADMIN)

```http
DELETE /usuarios/{id}
Authorization: Bearer {token}
```

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Usuario desactivado exitosamente"
}
```

---

## 📰 ENDPOINTS - NOTICIAS

### 1. Listar Noticias Activas

```http
GET /noticias?page=0&size=10&sort=fechaPublicacion,desc
```

**Respuesta 200:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "titulo": "Nuevas políticas de seguridad 2026",
        "contenido": "Se implementarán nuevas políticas...",
        "activa": true,
        "fechaPublicacion": "2026-03-20T10:00:00",
        "autorNombre": "Admin Sistema",
        "numeroLecturas": 245,
        "prioridad": "ALTA",
        "categoria": "Seguridad",
        "imagenUrl": "/images/security.jpg",
        "fechaCreacion": "2026-03-20T09:00:00"
      }
    ],
    "totalElements": 150
  }
}
```

---

### 2. Buscar Noticias

```http
GET /noticias/buscar?titulo=políticas&page=0&size=10
```

---

### 3. Obtener Noticia Detallada

```http
GET /noticias/{id}
```

*Nota: Incrementa automáticamente contador de lecturas*

---

### 4. Crear Noticia (EDITOR, ADMIN)

```http
POST /noticias
Authorization: Bearer {token}
Content-Type: application/json

{
  "titulo": "Nueva noticia importante",
  "contenido": "Contenido de la noticia...",
  "prioridad": "ALTA",
  "categoria": "Empresarial",
  "imagenUrl": "/images/noticia.jpg",
  "fechaExpiracion": "2026-04-22T23:59:59"
}
```

---

### 5. Actualizar Noticia (EDITOR, ADMIN)

```http
PUT /noticias/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "titulo": "Título actualizado",
  "contenido": "Contenido actualizado...",
  "prioridad": "MEDIA"
}
```

---

### 6. Desactivar Noticia (ADMIN)

```http
DELETE /noticias/{id}
Authorization: Bearer {token}
```

---

## 📄 ENDPOINTS - DOCUMENTOS

### 1. Listar Documentos

```http
GET /documentos?clasificacion=CONFIDENCIAL&page=0&size=20
Authorization: Bearer {token}
```

**Parámetros Query:**
- `clasificacion`: PUBLIC, INTERNAL, CONFIDENTIAL, RESTRICTED
- `tipo`: PDF, DOCX, XLSX, etc.
- `departamento`: Filtrar por departamento
- `etiquetas`: Buscar por etiquetas

---

### 2. Obtener Documento

```http
GET /documentos/{id}
Authorization: Bearer {token}
```

---

### 3. Subir Documento

```http
POST /documentos
Authorization: Bearer {token}
Content-Type: multipart/form-data

File: documento.pdf
titulo: Política de Privacidad 2026
descripcion: Documento con políticas...
clasificacion: CONFIDENTIAL
tipo: PDF
departamento: Legales
etiquetas: política,privacidad,2026
```

---

### 4. Descargar Documento

```http
GET /documentos/{id}/descargar
Authorization: Bearer {token}
```

*Nota: Incrementa contador de descargas*

---

### 5. Actualizar Documento

```http
PUT /documentos/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "titulo": "Título actualizado",
  "clasificacion": "INTERNAL",
  "etiquetas": "etiqueta1,etiqueta2"
}
```

---

## 📑 ENDPOINTS - AUDITORÍA

### 1. Obtener Auditoría por Usuario

```http
GET /auditoria/usuario/{usuarioId}?page=0&size=50
Authorization: Bearer {token}  (ADMIN o mismo usuario)
```

---

### 2. Obtener Auditoría por Acción

```http
GET /auditoria/accion/{accion}?page=0&size=50
Authorization: Bearer {token}  (ADMIN)
```

**Acciones:**
- LOGIN
- CREATE
- UPDATE
- DELETE
- VIEW
- DOWNLOAD
- EXPORT

---

### 3. Obtener Auditoría por Fecha

```http
GET /auditoria/fechas?fecha_inicio=2026-03-01&fecha_fin=2026-03-31&page=0&size=100
Authorization: Bearer {token}  (ADMIN)
```

---

### 4. Obtener Auditoría General

```http
GET /auditoria?modulo=USUARIOS&resultado=SUCCESS&page=0&size=100
Authorization: Bearer {token}  (ADMIN)
```

**Campos de Auditoría:**
```json
{
  "id": 1,
  "usuario": "admin@banco.local",
  "usuarioId": 1,
  "accion": "LOGIN",
  "modulo": "AUTENTICACION",
  "resultado": "SUCCESS",
  "detalles": "Login exitoso del usuario ADM001",
  "direccionIp": "192.168.1.100",
  "navegador": "Chrome 118",
  "sistemaOperativo": "Windows 10",
  "idTransaccion": "txn_550e8400_e29b_41d4",
  "fechaAccion": "2026-03-22T14:30:00"
}
```

---

## 📊 ENDPOINTS - DASHBOARD

### 1. Obtener Datos del Dashboard

```http
GET /dashboard/datos
Authorization: Bearer {token}
```

**Respuesta:**
```json
{
  "success": true,
  "data": {
    "totalUsuarios": 150,
    "usuariosActivos": 145,
    "noticiasRecientes": 12,
    "documentosDisponibles": 287,
    "estadisticas": {
      "loginesHoy": 234,
      "operacionesHoy": 1203
    }
  }
}
```

---

### 2. Obtener Widgets

```http
GET /dashboard/widgets
Authorization: Bearer {token}
```

---

## 📍 PAGINATION

Soportado en endpoints que listan recursos:

```http
GET /usuarios?page=0&size=20&sort=nombre,asc

Parámetros:
- page: Número de página (0-indexed)
- size: Cantidad de elementos por página
- sort: Campo a ordenar + dirección (asc/desc)
```

**Respuesta Paginada:**
```json
{
  "content": [...],
  "totalElements": 150,
  "totalPages": 8,
  "currentPage": 0,
  "size": 20,
  "hasNext": true,
  "hasPrevious": false
}
```

---

## 🚨 ERROR HANDLING

### Formato de Error Estándar

```json
{
  "success": false,
  "message": "Descripción del error",
  "errorCode": "ERROR_CODE",
  "path": "/api/usuarios",
  "timestamp": "2026-03-22T14:30:00"
}
```

### Códigos de Error Comunes

| Código | Descripción |
|--------|-------------|
| `USER_NOT_FOUND` | Usuario no existe |
| `USER_DUPLICATE_EMAIL` | Email ya registrado |
| `BAD_CREDENTIALS` | Credenciales inválidas |
| `ACCOUNT_LOCKED` | Cuenta bloqueada |
| `INVALID_REFRESH_TOKEN` | Token de refresco inválido |
| `ACCESS_DENIED` | Acceso denegado |
| `VALIDATION_ERROR` | Error de validación |
| `INTERNAL_ERROR` | Error interno del servidor |

### Error de Validación

```json
{
  "success": false,
  "message": "Validación fallida",
  "errorCode": "VALIDATION_ERROR",
  "data": {
    "email": "Email debe ser válido",
    "nombre": "Nombre es requerido"
  }
}
```

---

## 🔄 RATE LIMITING (Futuro)

```
Límites por tipo:
- Login: 5 intentos / 5 minutos
- API General: 1000 requests / 1 hora
- Upload: 10 archivos / 1 hora
```

---

**Versión Documentación API:** 1.0.0  
**Última Actualización:** Marzo 2026
