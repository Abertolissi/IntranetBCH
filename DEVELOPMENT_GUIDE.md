# DEVELOPMENT GUIDE - INTRANET BANCARIA

Guía completa para desarrolladores que extensionan o mantienen el código.

## 📋 Tabla de Contenidos

1. [Convenciones de Código](#convenciones-de-código)
2. [Estructura de Proyecto](#estructura-de-proyecto)
3. [Workflow de Desarrollo](#workflow-de-desarrollo)
4. [Creando Nuevos Módulos](#creando-nuevos-módulos)
5. [Testing Unitario](#testing-unitario)
6. [Debugging](#debugging)
7. [Best Practices](#best-practices)

---

## Convenciones de Código

### Backend Java

#### Naming Conventions

```java
// Clases
public class UsuarioService { }      // PascalCase
public class UsuarioDTO { }          // PascalCase + sufijo DTO
public class UsuarioEntity { }       // PascalCase + sufijo Entity

// Variables
private String nombreCompleto;       // camelCase
private static final Logger LOG = LoggerFactory.getLogger(...); // UPPER_SNAKE_CASE

// Métodos
public Usuario obtenerPorId(Long id) { }  // camelCase, verbos descriptivos
public void actualizarUsuario(Usuario usuario) { }
public boolean esAdmin(Usuario usuario) { }
```

#### Anotaciones y Decoradores

```java
// DTOs
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    @NotBlank(message = "El nombre es requerido")
    private String nombre;
    
    @Email(message = "Email debe ser válido")
    private String email;
}

// Entidades
@Entity
@Table(name = "usuarios", indexes = {
    @Index(name = "idx_email", columnList = "email", unique = true)
})
@EntityListeners(AuditingEntityListener.class)
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
}

// Service
@Service
@Transactional
@Slf4j
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new AppException("Usuario no encontrado", HttpStatus.NOT_FOUND));
    }
}

// Controller
@RestController
@RequestMapping("/api/usuarios")
@Slf4j
@Validated
public class UsuarioController {
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<UsuarioDTO>> crear(
        @Valid @RequestBody UsuarioDTO usuarioDTO) {
        
        Usuario usuario = usuarioService.crear(usuarioDTO);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponseDTO.success("Usuario creado", usuarioDTO));
    }
}
```

### Frontend Angular/TypeScript

#### Naming Conventions

```typescript
// Componentes
export class UsuarioListComponent implements OnInit { }  // {Feature}Component
export class EditarUsuarioComponent { }

// Servicios
export class UsuarioService { }      // {Feature}Service

// Modelos/Interfaces
export interface Usuario { }         // Singular, PascalCase
export interface LoginRequest { }    // {Action}Request

// Directivas
export class HighlightDirective { }  // {Adjective}Directive

// Métodos
ngOnInit() { }                        // camelCase
cargarUsuarios(): void { }
obtenerDetalles(id: number): void { }

// Variables
private usuarios$ = new BehaviorSubject<Usuario[]>([]);  // $ sufijo para Observable
private cargando = false;            // boolean con prefijo is/can/should
private estadoFiltro: EstadoFiltro;  // enums UPPER_SNAKE_CASE
```

#### Decoradores TypeScript

```typescript
@Injectable({
  providedIn: 'root'  // Tree-shakeable
})
export class UsuarioService {
  constructor(private http: HttpClient) { }
}

@Component({
  selector: 'app-usuario-list',
  templateUrl: './usuario-list.component.html',
  styleUrls: ['./usuario-list.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UsuarioListComponent implements OnInit {
  @Input() usuarios: Usuario[] = [];    // Propiedades de entrada
  @Output() usuarioSeleccionado = new EventEmitter<Usuario>();  // Output
  
  ngOnInit(): void { }
}
```

---

## Estructura de Proyecto

### Backend

```
backend/
├── pom.xml                                      # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/com/banco/intranet/
│   │   │   ├── IntranetApplication.java        # Spring Boot entry point
│   │   │   ├── config/                         # Configuration
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── CorsConfig.java
│   │   │   │   └── WebMvcConfig.java
│   │   │   ├── security/                       # Security related
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   ├── common/                         # Shared utilities
│   │   │   │   ├── dto/
│   │   │   │   │   └── ApiResponseDTO.java
│   │   │   │   ├── exception/
│   │   │   │   │   ├── AppException.java
│   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   └── interceptor/
│   │   │   │       └── RequestLoggingInterceptor.java
│   │   │   ├── auth/                           # Auth Module
│   │   │   │   ├── controller/
│   │   │   │   │   └── AuthController.java
│   │   │   │   ├── service/
│   │   │   │   │   └── AuthService.java
│   │   │   │   └── dto/
│   │   │   │       ├── LoginRequestDTO.java
│   │   │   │       └── LoginResponseDTO.java
│   │   │   ├── usuario/                        # Usuario Module (CRUD)
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   └── dto/
│   │   │   ├── noticia/                        # News Module
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   └── dto/
│   │   │   ├── documento/                      # Document Module
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   └── dto/
│   │   │   ├── auditoria/                      # Audit Module
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── entity/
│   │   │   │   └── repository/
│   │   │   ├── rol/                            # Role & Permission Module
│   │   │   │   ├── entity/
│   │   │   │   └── repository/
│   │   │   └── dashboard/                      # Dashboard Module
│   │   │       ├── controller/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── application.properties          # Main config
│   │       └── application-dev.properties      # Dev profile
│   └── test/                                    # Unit tests
│       └── java/com/banco/intranet/
└── Dockerfile
```

### Frontend

```
frontend/
├── angular.json                     # Angular build config
├── package.json                     # npm dependencies
├── tsconfig.json                    # TypeScript config
├── src/
│   ├── index.html                   # Entry HTML
│   ├── main.ts                      # Bootstrap
│   ├── styles.css                   # Global styles
│   ├── app/
│   │   ├── app.component.ts         # Root component
│   │   ├── app.routes.ts            # Routing config
│   │   ├── app.component.html
│   │   ├── app.component.css
│   │   ├── core/                    # Core module (shared)
│   │   │   ├── services/
│   │   │   │   ├── auth.service.ts
│   │   │   │   └── usuario.service.ts
│   │   │   ├── interceptors/
│   │   │   │   ├── http-auth.interceptor.ts
│   │   │   │   └── http-error.interceptor.ts
│   │   │   ├── guards/
│   │   │   │   └── auth.guard.ts
│   │   │   ├── models/
│   │   │   │   ├── auth.models.ts
│   │   │   │   └── app.models.ts
│   │   │   └── constants/          # Constants & config
│   │   │       └── app.constants.ts
│   │   ├── shared/                  # Shared components/directives
│   │   │   ├── components/
│   │   │   │   └── header.component.ts
│   │   │   └── directives/
│   │   │       └── highlight.directive.ts
│   │   ├── features/                # Feature modules
│   │   │   ├── auth/
│   │   │   │   ├── login.component.ts
│   │   │   │   ├── login.component.html
│   │   │   │   ├── login.component.css
│   │   │   │   └── auth.module.ts
│   │   │   ├── dashboard/
│   │   │   │   ├── dashboard.component.ts
│   │   │   │   ├── dashboard.component.html
│   │   │   │   ├── dashboard.component.css
│   │   │   │   └── dashboard.module.ts
│   │   │   ├── usuario/             # New feature example
│   │   │   │   ├── components/
│   │   │   │   ├── pages/
│   │   │   │   └── usuario.module.ts
│   │   │   ├── layout/
│   │   │   │   ├── layout.component.ts
│   │   │   │   ├── layout.component.html
│   │   │   │   └── layout.component.css
│   │   │   └── admin/              # Admin feature
│   │   │       └── admin.module.ts
│   │   └── assets/
│   │       ├── images/
│   │       └── icons/
└── Dockerfile
```

---

## Workflow de Desarrollo

### 1. Crear Nuevo Módulo en Backend

#### Paso 1: Crear Entity

```java
// usuario/entity/UsuarioEntity.java
@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
```

#### Paso 2: Crear Repository

```java
// usuario/repository/UsuarioRepository.java
@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByEmail(String email);
    
    @Query("SELECT u FROM UsuarioEntity u WHERE u.email LIKE %:busqueda%")
    Page<UsuarioEntity> buscarPorEmail(@Param("busqueda") String busqueda, Pageable pageable);
}
```

#### Paso 3: Crear DTO

```java
// usuario/dto/UsuarioDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    
    @NotBlank(message = "Nombre es requerido")
    private String nombre;
    
    @Email
    @NotBlank
    private String email;
}
```

#### Paso 4: Crear Mapper

```java
// usuario/mapper/UsuarioMapper.java
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioDTO toDTO(UsuarioEntity entity);
    UsuarioEntity toEntity(UsuarioDTO dto);
    List<UsuarioDTO> toDTOList(List<UsuarioEntity> entities);
}
```

#### Paso 5: Crear Service

```java
// usuario/service/UsuarioService.java
@Service
@Transactional
@Slf4j
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private UsuarioMapper usuarioMapper;
    
    public UsuarioDTO obtenerPorId(Long id) {
        UsuarioEntity entity = usuarioRepository.findById(id)
            .orElseThrow(() -> new AppException("Usuario no encontrado", HttpStatus.NOT_FOUND));
        return usuarioMapper.toDTO(entity);
    }
    
    public Page<UsuarioDTO> listar(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
            .map(usuarioMapper::toDTO);
    }
    
    public UsuarioDTO crear(UsuarioDTO dto) {
        // Validar duplicado
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new AppException("Email ya existe", HttpStatus.BAD_REQUEST);
        }
        
        UsuarioEntity entity = usuarioMapper.toEntity(dto);
        UsuarioEntity guardado = usuarioRepository.save(entity);
        return usuarioMapper.toDTO(guardado);
    }
    
    public UsuarioDTO actualizar(Long id, UsuarioDTO dto) {
        UsuarioEntity entity = usuarioRepository.findById(id)
            .orElseThrow(() -> new AppException("Usuario no encontrado", HttpStatus.NOT_FOUND));
        
        entity.setNombre(dto.getNombre());
        // mapper.updateEntityFromDto(dto, entity);
        
        UsuarioEntity actualizado = usuarioRepository.save(entity);
        return usuarioMapper.toDTO(actualizado);
    }
    
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new AppException("Usuario no encontrado", HttpStatus.NOT_FOUND);
        }
        usuarioRepository.deleteById(id);
    }
}
```

#### Paso 6: Crear Controller

```java
// usuario/controller/UsuarioController.java
@RestController
@RequestMapping("/api/usuarios")
@Slf4j
@Validated
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    public ResponseEntity<ApiResponseDTO<UsuarioDTO>> obtenerPorId(
        @PathVariable Long id) {
        
        UsuarioDTO usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(
            ApiResponseDTO.success("Usuario obtenido", usuario)
        );
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Page<UsuarioDTO>>> listar(
        @PageableDefault(size = 10) Pageable pageable) {
        
        Page<UsuarioDTO> usuarios = usuarioService.listar(pageable);
        return ResponseEntity.ok(
            ApiResponseDTO.success("Usuarios obtenidos", usuarios)
        );
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<UsuarioDTO>> crear(
        @Valid @RequestBody UsuarioDTO usuarioDTO) {
        
        UsuarioDTO creado = usuarioService.crear(usuarioDTO);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponseDTO.success("Usuario creado", creado));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @usuarioService.esElMismo(#id)")
    public ResponseEntity<ApiResponseDTO<UsuarioDTO>> actualizar(
        @PathVariable Long id,
        @Valid @RequestBody UsuarioDTO usuarioDTO) {
        
        UsuarioDTO actualizado = usuarioService.actualizar(id, usuarioDTO);
        return ResponseEntity.ok(
            ApiResponseDTO.success("Usuario actualizado", actualizado)
        );
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> eliminar(
        @PathVariable Long id) {
        
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
```

#### Paso 7: Registrar Rutas

```typescript
// app.routes.ts
export const routes: Routes = [
  // ...existing routes
  {
    path: 'app',
    component: LayoutComponent,
    canActivate: [AuthGuard],
    children: [
      // ...
      {
        path: 'usuario',
        loadChildren: () => import('./features/usuario/usuario.module')
          .then(m => m.UsuarioModule)
      }
    ]
  }
];
```

---

## Creando Nuevos Módulos

### Frontend - Nuevo Feature Module

#### Paso 1: Generar componente CLI (opcional)

```bash
ng generate component features/usuario/components/usuario-list
ng generate component features/usuario/pages/usuario-detail
ng generate service features/usuario/services/usuario
```

#### Paso 2: Crear componentes

```typescript
// usuario-list.component.ts
@Component({
  selector: 'app-usuario-list',
  templateUrl: './usuario-list.component.html',
  styleUrls: ['./usuario-list.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UsuarioListComponent implements OnInit {
  usuarios$: Observable<Usuario[]>;
  cargando$ = new BehaviorSubject(false);
  
  constructor(private usuarioService: UsuarioService) {
    this.usuarios$ = this.usuarioService.obtenerTodos();
  }
  
  ngOnInit(): void {
    this.cargar();
  }
  
  cargar(): void {
    this.cargando$.next(true);
    this.usuarios$ = this.usuarioService.obtenerTodos()
      .pipe(
        finalize(() => this.cargando$.next(false))
      );
  }
  
  editar(usuario: Usuario): void {
    // Navigate to edit page
  }
  
  eliminar(usuario: Usuario): void {
    if (confirm('¿Eliminar usuario?')) {
      this.usuarioService.eliminar(usuario.id).subscribe(
        () => this.cargar()
      );
    }
  }
}
```

#### Paso 3: Crear servicio

```typescript
// usuario.service.ts
@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private apiUrl = environment.apiUrl + '/usuarios';
  
  constructor(private http: HttpClient) { }
  
  obtenerTodos(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.apiUrl);
  }
  
  obtenerPorId(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/${id}`);
  }
  
  crear(usuario: Usuario): Observable<Usuario> {
    return this.http.post<Usuario>(this.apiUrl, usuario);
  }
  
  actualizar(id: number, usuario: Usuario): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${id}`, usuario);
  }
  
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
```

#### Paso 4: Crear módulo

```typescript
// usuario.module.ts
@NgModule({
  declarations: [
    UsuarioListComponent,
    UsuarioDetailComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    RouterModule.forChild([
      {
        path: '',
        component: UsuarioListComponent
      },
      {
        path: ':id',
        component: UsuarioDetailComponent
      }
    ])
  ]
})
export class UsuarioModule { }
```

---

## Testing Unitario

### Backend - JUnit + Mockito

```java
// usuario/service/UsuarioServiceTest.java
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @Mock
    private UsuarioMapper usuarioMapper;
    
    @InjectMocks
    private UsuarioService usuarioService;
    
    private UsuarioEntity usuarioEntity;
    private UsuarioDTO usuarioDTO;
    
    @BeforeEach
    void setup() {
        usuarioEntity = UsuarioEntity.builder()
            .id(1L)
            .nombre("John")
            .email("john@banco.local")
            .build();
            
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setNombre("John");
    }
    
    @Test
    void testObtenerPorId_Success() {
        // Arrange
        when(usuarioRepository.findById(1L))
            .thenReturn(Optional.of(usuarioEntity));
        when(usuarioMapper.toDTO(usuarioEntity))
            .thenReturn(usuarioDTO);
        
        // Act
        UsuarioDTO resultado = usuarioService.obtenerPorId(1L);
        
        // Assert
        assertNotNull(resultado);
        assertEquals("John", resultado.getNombre());
        verify(usuarioRepository, times(1)).findById(1L);
    }
    
    @Test
    void testObtenerPorId_NotFound() {
        // Arrange
        when(usuarioRepository.findById(999L))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(AppException.class, () -> 
            usuarioService.obtenerPorId(999L)
        );
    }
}
```

### Frontend - Jasmine + Karma

```typescript
// usuario.service.spec.ts
describe('UsuarioService', () => {
  let service: UsuarioService;
  let httpMock: HttpTestingController;
  
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UsuarioService]
    });
    
    service = TestBed.inject(UsuarioService);
    httpMock = TestBed.inject(HttpTestingController);
  });
  
  afterEach(() => {
    httpMock.verify();
  });
  
  it('should fetch usuarios', () => {
    // Arrange
    const mockUsuarios: Usuario[] = [
      { id: 1, nombre: 'John', email: 'john@banco.local' }
    ];
    
    // Act
    service.obtenerTodos().subscribe(usuarios => {
      // Assert
      expect(usuarios.length).toBe(1);
      expect(usuarios[0].nombre).toBe('John');
    });
    
    // Assert HTTP call
    const req = httpMock.expectOne('/api/usuarios');
    expect(req.request.method).toBe('GET');
    req.flush(mockUsuarios);
  });
});
```

---

## Debugging

### Backend

```bash
# Run with debug mode
mvn spring-boot:run -Dspring-boot.run.arguments='--spring.jvm.arguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005'

# En IDE (IntelliJ/VS Code):
# Debug → Edit Configurations → Remote
# Host: localhost, Port: 5005
```

### Frontend

```bash
# Chrome DevTools
ng serve --source-map  # Include source maps

# Breakpoints:
# 1. Sources → webpack://
# 2. Search component file
# 3. Click line number
# 4. Step over/into/out
```

### SQL Server

```sql
-- Enable query tracing
SET STATISTICS IO ON;
SET STATISTICS TIME ON;

-- Slow query
SELECT * FROM usuarios WHERE email LIKE '%@banco.local%';

-- Check execution plan
CTRL + L (before executing query)
```

---

## Best Practices

### Backend

1. **Always use DTOs** - Never expose entities directly
2. **Validate input** - Use @Valid and @Validated
3. **Handle exceptions globally** - Use @RestControllerAdvice
4. **Implement pagination** - Always for lists
5. **Use transactions** - @Transactional for multi-DB operations
6. **Log operations** - Use SLF4J with Lombok @Slf4j
7. **Security first** - @PreAuthorize on all endpoints
8. **Test CRUD** - Unit test at least happy path

### Frontend

1. **Use OnPush change detection** - Improves performance
2. **Unsubscribe properly** - Use async pipe or takeUntil
3. **Lazy load modules** - Don't load all routes upfront
4. **Type everything** - Strict TypeScript mode
5. **Separate concerns** - Services for HTTP, Components for UI
6. **Reuse components** - DRY principle
7. **Material Design** - Consistent UI/UX
8. **Test components** - At least specs for critical logic

---

Consultar README.md y ARCHITECTURE.md para contexto adicional.
