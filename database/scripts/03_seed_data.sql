IF NOT EXISTS (SELECT 1 FROM permisos WHERE codigo = 'USUARIOS_VER')
BEGIN
	INSERT INTO permisos (codigo, nombre, descripcion, modulo, activo)
	VALUES
	('USUARIOS_VER', 'Ver usuarios', 'Permite consultar usuarios', 'USUARIOS', 1),
	('USUARIOS_EDITAR', 'Editar usuarios', 'Permite crear y editar usuarios', 'USUARIOS', 1),
	('NOTICIAS_VER', 'Ver noticias', 'Permite consultar noticias', 'NOTICIAS', 1),
	('NOTICIAS_EDITAR', 'Editar noticias', 'Permite crear y editar noticias', 'NOTICIAS', 1),
	('DOCUMENTOS_VER', 'Ver documentos', 'Permite consultar documentos', 'DOCUMENTOS', 1),
	('DOCUMENTOS_EDITAR', 'Editar documentos', 'Permite crear y editar documentos', 'DOCUMENTOS', 1),
	('AUDITORIA_VER', 'Ver auditoría', 'Permite consultar auditoría', 'AUDITORIA', 1),
	('ROLES_VER', 'Ver roles', 'Permite consultar roles y permisos', 'ADMIN', 1);
END
GO

IF NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'ADMIN')
BEGIN
	INSERT INTO roles (nombre, descripcion, activo)
	VALUES
	('ADMIN', 'Administrador del sistema', 1),
	('EDITOR', 'Editor de noticias y documentos', 1),
	('USUARIO', 'Usuario estándar de intranet', 1);
END
GO

IF NOT EXISTS (SELECT 1 FROM rol_permiso)
BEGIN
	INSERT INTO rol_permiso (rol_id, permiso_id)
	SELECT r.id, p.id
	FROM roles r
	CROSS JOIN permisos p
	WHERE r.nombre = 'ADMIN';

	INSERT INTO rol_permiso (rol_id, permiso_id)
	SELECT r.id, p.id
	FROM roles r
	JOIN permisos p ON p.codigo IN ('NOTICIAS_VER', 'NOTICIAS_EDITAR', 'DOCUMENTOS_VER', 'DOCUMENTOS_EDITAR')
	WHERE r.nombre = 'EDITOR';

	INSERT INTO rol_permiso (rol_id, permiso_id)
	SELECT r.id, p.id
	FROM roles r
	JOIN permisos p ON p.codigo IN ('NOTICIAS_VER', 'DOCUMENTOS_VER')
	WHERE r.nombre = 'USUARIO';
END
GO

IF NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@banco.local')
BEGIN
	INSERT INTO usuarios (
		nombre, apellido, email, numero_empleado, contrasena,
		departamento, puesto, activo, cuenta_bloqueada, intentos_fallidos,
		usuario_creacion
	) VALUES (
		'Administrador', 'Sistema', 'admin@banco.local', 'EMP001', 'Password123!',
		'TI', 'Administrador', 1, 0, 0,
		'SYSTEM'
	);
END
GO

IF NOT EXISTS (
	SELECT 1 FROM usuario_rol ur
	JOIN usuarios u ON u.id = ur.usuario_id
	JOIN roles r ON r.id = ur.rol_id
	WHERE u.email = 'admin@banco.local' AND r.nombre = 'ADMIN'
)
BEGIN
	INSERT INTO usuario_rol (usuario_id, rol_id)
	SELECT u.id, r.id
	FROM usuarios u
	JOIN roles r ON r.nombre = 'ADMIN'
	WHERE u.email = 'admin@banco.local';
END
GO

IF NOT EXISTS (SELECT 1 FROM noticias)
BEGIN
	INSERT INTO noticias (
		titulo, contenido, activa, fecha_publicacion, autor_id, autor_nombre,
		numero_lecturas, prioridad, categoria, usuario_creacion
	)
	SELECT
		'Bienvenidos a la Intranet Bancaria',
		'Portal corporativo activo y disponible para todos los colaboradores.',
		1,
		SYSUTCDATETIME(),
		u.id,
		'Administrador Sistema',
		0,
		'ALTA',
		'INSTITUCIONAL',
		'SYSTEM'
	FROM usuarios u
	WHERE u.email = 'admin@banco.local';
END
GO

IF NOT EXISTS (SELECT 1 FROM documentos)
BEGIN
	INSERT INTO documentos (
		titulo, descripcion, tipo, ruta_archivo, extension, tamanio,
		clasificacion, activo, numero_descargas, autor_id, autor_nombre,
		departamento, etiquetas, version, usuario_creacion
	)
	SELECT
		'Manual de Seguridad',
		'Documento inicial de políticas de seguridad.',
		'PDF',
		'/storage/documents/manual-seguridad.pdf',
		'pdf',
		102400,
		'INTERNO',
		1,
		0,
		u.id,
		'Administrador Sistema',
		'TI',
		'seguridad,politicas',
		'1.0',
		'SYSTEM'
	FROM usuarios u
	WHERE u.email = 'admin@banco.local';
END
GO