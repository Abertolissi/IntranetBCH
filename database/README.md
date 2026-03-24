# Database Setup Documentation

This directory contains the SQL scripts and Dockerfile necessary for setting up the database for the Intranet Bancaria project.

## SQL Scripts

- **01_create_database.sql**: This script creates the main database for the application. It should be executed first to establish the database environment.
  
- **02_create_tables.sql**: This script creates the necessary tables within the database. It should be run after the database has been created.
  
- **03_seed_data.sql**: This script seeds the database with initial data required for the application to function properly. It should be executed after the tables have been created.

## Docker Setup

The Dockerfile in this directory is used to build the Docker image for the database. It ensures that the database environment is consistent and can be easily deployed.

## Best Practices for Document Storage

For optimal document storage, consider the following best practices:

1. **Use Dedicated Storage Services**: For scalability and reliability, utilize cloud storage solutions such as AWS S3 or Azure Blob Storage for storing critical documents. This approach provides better performance and durability.

2. **Local Storage for Temporary Files**: Use the local `storage/documents` and `storage/uploads` directories for temporary or less critical files. This allows for quick access during development and testing.

3. **Backup and Recovery**: Implement regular backup procedures for your database and document storage to prevent data loss.

4. **Access Control**: Ensure that proper access controls are in place to protect sensitive documents and data.

5. **Versioning**: Consider implementing versioning for documents to keep track of changes and maintain historical records.

By following these guidelines, you can ensure that your document storage is efficient, secure, and scalable.