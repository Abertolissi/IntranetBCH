# Intranet Bancaria

Este proyecto es una aplicación de intranet bancaria que se ejecuta en contenedores Docker separados para el backend, el frontend y la base de datos. A continuación se detallan las características y la estructura del proyecto.

## Estructura del Proyecto

```
intranet-bancaria
├── backend                # Aplicación backend en Spring Boot
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com
│   │   │   │       └── banco
│   │   │   │           ├── config
│   │   │   │           ├── controllers
│   │   │   │           ├── services
│   │   │   │           ├── repositories
│   │   │   │           └── entities
│   │   │   └── resources
│   │   │       ├── application.yml
│   │   │       ├── application-docker.yml
│   │   │       └── logback-spring.xml
│   │   └── test
│   ├── Dockerfile
│   ├── pom.xml
│   └── README.md
├── frontend               # Aplicación frontend en Angular
│   ├── src
│   │   ├── app
│   │   │   ├── components
│   │   │   ├── services
│   │   │   ├── models
│   │   │   └── app.component.ts
│   │   ├── assets
│   │   └── environments
│   ├── Dockerfile
│   ├── package.json
│   ├── angular.json
│   └── README.md
├── database               # Scripts y configuración de la base de datos
│   ├── scripts
│   │   ├── 01_create_database.sql
│   │   ├── 02_create_tables.sql
│   │   └── 03_seed_data.sql
│   ├── Dockerfile
│   └── README.md
├── storage                # Almacenamiento de documentos y archivos subidos
│   ├── documents
│   ├── uploads
│   └── README.md
├── docker-compose.yml     # Configuración de Docker Compose
├── .env                   # Variables de entorno
└── README.md              # Documentación general del proyecto
```

## Requisitos

- **Java 17+**: Necesario para el backend.
- **Maven 3.8+**: Para la gestión de dependencias del backend.
- **Node.js 18+**: Para el frontend.

## Configuración

1. **Base de Datos**: Asegúrate de que SQL Server esté ejecutándose en `localhost:1433`. Ejecuta el script `database/scripts/01_create_database.sql` para crear la base de datos.
2. **Backend**: Navega a la carpeta `backend` y ejecuta `mvn spring-boot:run` para iniciar el servidor.
3. **Frontend**: Navega a la carpeta `frontend` y ejecuta `npm start` para iniciar el servidor de desarrollo.

## Almacenamiento de Documentos

Para el almacenamiento de documentos, se recomienda utilizar un servicio de almacenamiento dedicado (como AWS S3 o Azure Blob Storage) para escalabilidad y fiabilidad. Las carpetas locales `storage/documents` y `storage/uploads` se pueden utilizar para archivos temporales o menos críticos.

## Acceso

- **Backend**: http://localhost:8080 (API)
- **Frontend**: http://localhost:4200

## Credenciales

- **Usuario**: admin@banco.local
- **Contraseña**: AdminPassword123!

## Notas

Este proyecto está diseñado para facilitar el desarrollo y la implementación de una aplicación bancaria segura y eficiente. Asegúrate de seguir las mejores prácticas de seguridad y gestión de datos al implementar en un entorno de producción.