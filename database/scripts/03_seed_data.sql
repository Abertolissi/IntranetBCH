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
		'Administrador', 'Sistema', 'admin@banco.local', 'EMP001', 'AdminPassword123!',
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

INSERT INTO documentos (
	titulo, descripcion, tipo, ruta_archivo, extension, tamanio,
	clasificacion, activo, numero_descargas, autor_id, autor_nombre,
	departamento, etiquetas, version, usuario_creacion
)
SELECT
	d.titulo,
	d.descripcion,
	d.tipo,
	d.ruta_archivo,
	d.extension,
	d.tamanio,
	d.clasificacion,
	1,
	0,
	u.id,
	'Administrador Sistema',
	d.departamento,
	d.etiquetas,
	'1.0',
	'SYSTEM'
FROM usuarios u
CROSS APPLY (
	VALUES
		('Normativa de Seguridad de la Informacion', 'Lineamientos base de seguridad institucional.', 'PDF', '/storage/documents/normativa-seguridad.pdf', 'pdf', 102400, 'Normativas', 'Tecnología', 'normativa,seguridad'),
		('Procedimiento de Alta de Usuarios', 'Pasos para alta y validacion de usuarios internos.', 'PDF', '/storage/documents/procedimiento-alta-usuarios.pdf', 'pdf', 84320, 'Procedimientos', 'Recursos Humanos', 'procedimiento,usuarios'),
		('Manual de Uso de la Intranet', 'Guia operativa para el uso diario de la plataforma.', 'PDF', '/storage/documents/manual-intranet.pdf', 'pdf', 95600, 'Manuales', 'Operaciones', 'manual,intranet'),
		('Auditoria de Accesos Trimestral', 'Resumen de control y trazabilidad de accesos.', 'PDF', '/storage/documents/auditoria-accesos.pdf', 'pdf', 120512, 'Auditoria', 'Riesgos', 'auditoria,accesos'),
		('Minuta de Comite de Auditoria', 'Acuerdos y seguimiento del comite de auditoria.', 'PDF', '/storage/documents/minuta-comite-auditoria.pdf', 'pdf', 76800, 'Minutas', 'Auditoria', 'minuta,comite')
) AS d(titulo, descripcion, tipo, ruta_archivo, extension, tamanio, clasificacion, departamento, etiquetas)
WHERE u.email = 'admin@banco.local'
	AND NOT EXISTS (
		SELECT 1
		FROM documentos doc
		WHERE doc.titulo = d.titulo
			AND doc.clasificacion = d.clasificacion
	);
GO