# Frontend Documentation for Intranet Bancaria

## Overview

This directory contains the frontend application for the Intranet Bancaria project, built using Angular. The frontend communicates with the backend services to provide a seamless user experience for banking operations.

## Project Structure

- **src/**: Contains the source code for the Angular application.
  - **app/**: Contains the main application components, services, and models.
    - **components/**: Angular components that make up the user interface.
    - **services/**: Services for handling data fetching and business logic.
    - **models/**: TypeScript models defining the structure of data used in the application.
    - **app.component.ts**: The root component of the Angular application.
  - **assets/**: Static assets such as images and stylesheets.
  - **environments/**: Environment-specific configuration files.

## Getting Started

### Prerequisites

- Node.js (version 18 or higher)
- Angular CLI

### Installation

1. Clone the repository:
   ```
   git clone <repository-url>
   cd intranet-bancaria/frontend
   ```

2. Install dependencies:
   ```
   npm install
   ```

### Running the Application

To start the development server, run:
```
npm start
```
The application will be available at `http://localhost:4200`.

### Building for Production

To build the application for production, run:
```
ng build --prod
```
The output will be in the `dist/` directory.

## Best Practices for Document Storage

For document storage, it is recommended to use a dedicated storage service (e.g., AWS S3 or Azure Blob Storage) for scalability and reliability. The local `storage/documents` and `storage/uploads` directories should be used for temporary or less critical files.

## Contributing

Contributions are welcome! Please submit a pull request or open an issue for any enhancements or bug fixes.

## License

This project is licensed under the MIT License. See the LICENSE file for details.