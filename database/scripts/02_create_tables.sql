IF OBJECT_ID('permisos', 'U') IS NULL
BEGIN
    CREATE TABLE permisos (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        codigo VARCHAR(100) NOT NULL UNIQUE,
        nombre VARCHAR(200) NOT NULL,
        descripcion VARCHAR(500) NULL,
        modulo VARCHAR(50) NULL,
        activo BIT NOT NULL DEFAULT 1,
        fecha_creacion DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );
END
GO

IF OBJECT_ID('roles', 'U') IS NULL
BEGIN
    CREATE TABLE roles (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nombre VARCHAR(50) NOT NULL UNIQUE,
        descripcion VARCHAR(500) NULL,
        activo BIT NOT NULL DEFAULT 1,
        fecha_creacion DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        fecha_actualizacion DATETIME2 NULL
    );
END
GO

IF OBJECT_ID('usuarios', 'U') IS NULL
BEGIN
    CREATE TABLE usuarios (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nombre VARCHAR(100) NOT NULL,
        apellido VARCHAR(100) NOT NULL,
        email VARCHAR(100) NOT NULL UNIQUE,
        numero_empleado VARCHAR(15) NOT NULL UNIQUE,
        contrasena VARCHAR(255) NOT NULL,
        departamento VARCHAR(20) NULL,
        puesto VARCHAR(50) NULL,
        activo BIT NOT NULL DEFAULT 1,
        cuenta_bloqueada BIT NOT NULL DEFAULT 0,
        intentos_fallidos INT NULL DEFAULT 0,
        fecha_ultimo_login DATETIME2 NULL,
        fecha_cambio_contrasena DATETIME2 NULL,
        fecha_creacion DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        fecha_actualizacion DATETIME2 NULL,
        usuario_creacion VARCHAR(50) NOT NULL,
        usuario_actualizacion VARCHAR(50) NULL
    );
END
GO

IF OBJECT_ID('rol_permiso', 'U') IS NULL
BEGIN
    CREATE TABLE rol_permiso (
        rol_id BIGINT NOT NULL,
        permiso_id BIGINT NOT NULL,
        CONSTRAINT PK_rol_permiso PRIMARY KEY (rol_id, permiso_id),
        CONSTRAINT FK_rol_permiso_rol FOREIGN KEY (rol_id) REFERENCES roles(id),
        CONSTRAINT FK_rol_permiso_permiso FOREIGN KEY (permiso_id) REFERENCES permisos(id)
    );
END
GO

IF OBJECT_ID('usuario_rol', 'U') IS NULL
BEGIN
    CREATE TABLE usuario_rol (
        usuario_id BIGINT NOT NULL,
        rol_id BIGINT NOT NULL,
        CONSTRAINT PK_usuario_rol PRIMARY KEY (usuario_id, rol_id),
        CONSTRAINT FK_usuario_rol_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
        CONSTRAINT FK_usuario_rol_rol FOREIGN KEY (rol_id) REFERENCES roles(id)
    );
END
GO

IF OBJECT_ID('noticias', 'U') IS NULL
BEGIN
    CREATE TABLE noticias (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        titulo VARCHAR(200) NOT NULL,
        contenido NVARCHAR(MAX) NULL,
        activa BIT NOT NULL DEFAULT 1,
        fecha_publicacion DATETIME2 NOT NULL,
        fecha_expiracion DATETIME2 NULL,
        autor_id BIGINT NULL,
        autor_nombre VARCHAR(100) NULL,
        numero_lecturas INT NOT NULL DEFAULT 0,
        prioridad VARCHAR(20) NULL,
        categoria VARCHAR(50) NULL,
        imagen_url VARCHAR(500) NULL,
        fecha_creacion DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        fecha_actualizacion DATETIME2 NULL,
        usuario_creacion VARCHAR(50) NOT NULL
    );
END
GO

IF OBJECT_ID('documentos', 'U') IS NULL
BEGIN
    CREATE TABLE documentos (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        titulo VARCHAR(200) NOT NULL,
        descripcion VARCHAR(500) NULL,
        tipo VARCHAR(50) NOT NULL,
        ruta_archivo VARCHAR(500) NOT NULL,
        extension VARCHAR(20) NOT NULL,
        tamanio BIGINT NOT NULL,
        clasificacion VARCHAR(50) NOT NULL,
        activo BIT NOT NULL DEFAULT 1,
        numero_descargas INT NOT NULL DEFAULT 0,
        autor_id BIGINT NULL,
        autor_nombre VARCHAR(100) NULL,
        departamento VARCHAR(100) NULL,
        etiquetas VARCHAR(500) NULL,
        version VARCHAR(20) NULL,
        fecha_vigencia_inicio DATETIME2 NULL,
        fecha_vigencia_fin DATETIME2 NULL,
        fecha_creacion DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        fecha_actualizacion DATETIME2 NULL,
        usuario_creacion VARCHAR(50) NOT NULL
    );
END
GO

IF OBJECT_ID('auditoria', 'U') IS NULL
BEGIN
    CREATE TABLE auditoria (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        usuario VARCHAR(50) NOT NULL,
        usuario_id BIGINT NOT NULL,
        accion VARCHAR(100) NOT NULL,
        modulo VARCHAR(100) NOT NULL,
        detalles NVARCHAR(MAX) NULL,
        nivel VARCHAR(50) NULL,
        resultado VARCHAR(20) NOT NULL,
        codigo_resultado VARCHAR(10) NULL,
        mensaje_error NVARCHAR(MAX) NULL,
        direccion_ip VARCHAR(45) NULL,
        navegador VARCHAR(200) NULL,
        sistema_operativo VARCHAR(100) NULL,
        id_transaccion VARCHAR(100) NULL,
        fecha_accion DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );
END
GO