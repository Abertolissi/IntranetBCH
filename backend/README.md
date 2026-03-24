# Intranet Bancaria Backend

Este documento proporciona información sobre el backend de la aplicación Intranet Bancaria, que está construida utilizando Spring Boot y Maven. El backend se ejecuta en un contenedor Docker y se comunica con el frontend y la base de datos a través de una API REST.

## Estructura del Proyecto

El backend está organizado de la siguiente manera:

- **src/main/java/com/banco/config**: Clases de configuración para la aplicación Spring Boot, incluyendo configuraciones de seguridad y de base de datos.
- **src/main/java/com/banco/controllers**: Clases de controladores que manejan las solicitudes y respuestas HTTP.
- **src/main/java/com/banco/services**: Clases de servicio que contienen la lógica de negocio de la aplicación.
- **src/main/java/com/banco/repositories**: Interfaces de repositorio para el acceso a datos, típicamente extendiendo repositorios de Spring Data JPA.
- **src/main/java/com/banco/entities**: Clases de entidad que representan las tablas de la base de datos.
- **src/main/resources/application.yml**: Configuración principal para la aplicación Spring Boot.
- **src/main/resources/application-docker.yml**: Configuraciones específicas para Docker de la aplicación Spring Boot.
- **src/main/resources/logback-spring.xml**: Configuraciones de logging para la aplicación.
- **src/test**: Clases de prueba para la aplicación backend.

## Docker

El backend se puede construir y ejecutar en un contenedor Docker. Asegúrate de tener Docker instalado y configurado en tu máquina. Para construir la imagen Docker, utiliza el siguiente comando en el directorio del backend:

```bash
docker build -t intranet-bancaria-backend .
```

Para ejecutar el contenedor, utiliza:

```bash
docker run -p 8080:8080 intranet-bancaria-backend
```

## Almacenamiento de Documentos

Para el almacenamiento de documentos, se recomienda utilizar un servicio de almacenamiento dedicado (como AWS S3 o Azure Blob Storage) para escalabilidad y fiabilidad. Las carpetas locales `storage/documents` y `storage/uploads` se pueden utilizar para archivos temporales o menos críticos.

## Contribuciones

Las contribuciones son bienvenidas. Si deseas contribuir, por favor abre un issue o un pull request en el repositorio.

## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo LICENSE para más detalles.