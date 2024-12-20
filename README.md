# Project Overview
This project contains a Java Maven backend service and a React frontend application, both of which are containerized using Docker. The applications can be run together using Docker Compose for local development or Kubernetes for production deployments.

## Folder Structure
```plaintext
project-folder/
├── IncidentManagementSystem/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── incident-management/
│   ├── Dockerfile
│   ├── package.json
│   ├── package-lock.json
│   └── src/
├── docker-compose.yml
└── k8s/
    ├── backend-deployment.yaml
    ├── frontend-deployment.yaml
    └── service.yaml
```

## Prerequisites
- Docker
- Docker Compose
- Kubernetes (kubectl and a cluster, e.g., Minikube or a cloud provider)

## Running Locally with Docker Compose

### Steps
1. Navigate to the project folder:
   ```bash
   cd project-folder
   ```

2. Build and start the containers:
   ```bash
   docker-compose up --build
   ```

3. Access the applications:
    - **Frontend**: [http://localhost:3000](http://localhost:3000)
    - **Backend**: [http://localhost:8080](http://localhost:8080)

4. Stop the containers:
   ```bash
   docker-compose down
   ```

## Notes
- Use `docker-compose.yml` for local development.
- Update configuration files (`application.properties`, `.env`, etc.) as needed.

## Troubleshooting
- Ensure Docker is properly installed and running.
- Check logs for errors:
  ```bash
  docker-compose logs
  ```
- Verify service connectivity and environment variable configurations.

## Future Enhancements
- Add CI/CD pipelines for automated builds and deployments.
- Use Kubernetes ConfigMaps and Secrets for managing configurations securely.

