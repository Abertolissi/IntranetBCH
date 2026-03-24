# 📐 ARQUITECTURA TÉCNICA DETALLADA

## Diagrama de Capas

```
┌─────────────────────────────────────────────────────────┐
│              CAPA DE PRESENTACIÓN                       │
│  Angular 17 + Angular Material (Responsive, Mobile)     │
│  └─ Componentes Standalone │ Lazy Loading              │
└──────────────────┬──────────────────────────────────────┘
                   │ HTTP/REST + JWT
┌──────────────────▼──────────────────────────────────────┐
│           CAPA DE API / CONTROLLERS                     │
│  Spring Boot 3.2                                        │
│  ├─ AuthController       (POST /auth/login)            │
│  ├─ UsuarioController    (CRUD Usuarios)               │
│  ├─ NoticiaController    (CRUD Noticias)               │
│  ├─ DocumentoController  (CRUD Documentos)             │
│  ├─ RolController        (CRUD Roles)                  │
│  ├─ AuditoriaController  (Logs)                        │
│  └─ DashboardController  (Estadísticas)                │
└──────────────────┬──────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────┐
│         CAPA DE SERVICIOS / LÓGICA NEGOCIO             │
│  ├─ AuthService          (Autenticación + JWT)         │
│  ├─ UsuarioService       (Gestión Usuarios)            │
│  ├─ NoticiaService       (Gestión Noticias)            │
│  ├─ DocumentoService     (Gestión Documentos)          │
│  ├─ RolService           (Control Acceso)              │
│  ├─ AuditoriaService     (Auditoría de Acciones)       │
│  └─ DashboardService     (Estadísticas)                │
└──────────────────┬──────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────┐
│          CAPA DE PERSISTENCIA / ACCESO DATOS            │
│  Spring Data JPA Repositories                           │
│  ├─ UsuarioRepository    │ RolRepository               │
│  ├─ NoticiaRepository    │ DocumentoRepository         │
│  ├─ PermisoRepository    │ AuditoriaRepository         │
│  └─ Entidades JPA                                       │
└──────────────────┬──────────────────────────────────────┘
                   │ JDBC / JPA
┌──────────────────▼──────────────────────────────────────┐
│              BASE DE DATOS SQL SERVER                   │
│  ├─ Usuarios (160M registros max)                      │
│  ├─ Roles (5-10 registros)                             │
│  ├─ Permisos (50-100 registros)                        │
│  ├─ Noticias (5,000M registros max)                    │
│  ├─ Documentos (10,000 registros max)                  │
│  └─ Auditoría (sin límite, archivable)                 │
└────────────────────────────────────────────────────────┘
```

## Componentes de Seguridad

```
┌─────────────────────────────────────────────────┐
│     Solicitud HTTP desde Cliente                │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
        ┌─────────────────────────────┐
        │  CORS Filter                │
        │  (Validar Origen)           │
        └────────┬────────────────────┘
                 │
                 ▼
        ┌─────────────────────────────┐
        │  JwtAuthenticationFilter    │
        │  (Validar JWT)              │
        └────────┬────────────────────┘
                 │
                 ▼
        ┌─────────────────────────────┐
        │  SecurityConfig             │
        │  (aplicar @PreAuthorize)    │
        └────────┬────────────────────┘
                 │
                 ▼
        ┌─────────────────────────────┐
        │  GlobalExceptionHandler     │
        │  (Manejo de Errores)        │
        └────────┬────────────────────┘
                 │
                 ▼
        ┌─────────────────────────────┐
        │  Controlador                │
        │  (Procesar Lógica)          │
        └────────┬────────────────────┘
                 │
                 ▼
        ┌─────────────────────────────┐
        │  AuditoriaService           │
        │  (Registrar Acción)         │
        └────────┬────────────────────┘
                 │
                 ▼
        ┌─────────────────────────────┐
        │  Respuesta JSON             │
        │  (ApiResponseDTO)           │
        └─────────────────────────────┘
```

## Flujo de Autenticación

```
1. Usuario ingresa credenciales
   ↓
2. [POST] /auth/login + {usuario, contraseña}
   ↓
3. AuthService.login()
   - Buscar usuario por email/número_empleado
   - Verificar contraseña (BCrypt)
   - Validar estado (activo, cuenta bloqueada)
   ↓
4. JwtTokenProvider.generateToken()
   - Crear JWT con claims (email, userId)
   - Firmado con HS512
   - Expiration: 24 horas
   ↓
5. Response con token + refreshToken
   ↓
6. Client almacena token en localStorage
   ↓
7. Solicitudes posteriores incluyen: Authorization: Bearer {token}
   ↓
8. JwtAuthenticationFilter extrae y valida token
   ↓
9. SecurityContext establece usuario autenticado
   ↓
10. Controlador procesa con usuario validado
```

## Patrón de DTOs (Data Transfer Object)

```java
// DTO para transferencia segura (sin contraseñas)
@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    // NO incluir: contrasena, fechas_sistema
}

// Entity JPA (mapeable a BD)
@Entity
public class UsuarioEntity {
    @Id private Long id;
    private String nombre;
    private String contrasena; // Nunca en DTOs públicos
}

// Mapeo automático con MapStruct
@Mapper
public interface UsuarioMapper {
    UsuarioDTO toDTO(UsuarioEntity entity);
    UsuarioEntity toEntity(UsuarioDTO dto);
}
```

## Políticas de Caché

```
Recurso                 TTL      Caché Lado
─────────────────────────────────────────
Datos Usuario          15 min    Servidor
Noticias               5 min     Cliente
Documentos             24 horas  CDN
Configuración          30 min    Servidor
Session/JWT            24 horas  Cliente (localStorage)
```

## Rate Limiting (Implementación Futura)

```properties
# Por IP
spring.security.ratelimit.ip.requests=100
spring.security.ratelimit.ip.duration=1m

# Por Usuario
spring.security.ratelimit.user.requests=1000
spring.security.ratelimit.user.duration=1h

# Por Endpoint
spring.security.ratelimit.login.requests=5
spring.security.ratelimit.login.duration=5m
```

## Modelo Relacional BD

```sql
Usuarios (1) ─── (M) Usuario_Rol ─── (1) Roles
   │
   └─ (1) ─── (M) Familia_Roles ─── (1) Permisos
   
Usuarios (1) ─── (M) Noticias
Usuarios (1) ─── (M) Documentos
Usuarios (1) ─── (M) Auditoría
```

---

**Documentación Versión:** 1.0.0  
**Última Actualización:** Marzo 2026
