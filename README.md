# Enquiry API - Spring Boot with MongoDB

A Spring Boot REST API for handling enquiries with file attachments, built with Java 8, Spring Data MongoDB, and containerized with Docker.

## Features

- ✅ Submit enquiries with validation
- ✅ File attachment support (Base64 encoded)
- ✅ MongoDB persistence
- ✅ RESTful API endpoints
- ✅ Error handling and validation
- ✅ Docker containerization
- ✅ Azure deployment with GitHub Actions
- ✅ Health checks and monitoring

## API Endpoints

### Submit Enquiry
```http
POST /api/enquiries
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "company": "Example Corp",
  "message": "This is a test enquiry",
  "fileName": "document.pdf",
  "fileType": "application/pdf",
  "fileSize": 1024,
  "fileData": "base64-encoded-file-data"
}
```

### Get All Enquiries
```http
GET /api/enquiries
```

### Get Enquiry by ID
```http
GET /api/enquiries/{id}
```

### Get Enquiries by Status
```http
GET /api/enquiries/status/new
```

### Health Check
```http
GET /api/enquiries/health
```

## Required Environment Variables

- `MONGODB_URI`: MongoDB connection string (default: `mongodb://localhost:27017/enquiry_db`)

## Local Development

### Prerequisites
- Java 8+
- Maven 3.6+
- Docker and Docker Compose (optional)

### Run with Maven
```bash
# Install dependencies and build
mvn clean package

# Run the application
mvn spring-boot:run
```

### Run with Docker Compose
```bash
# Start MongoDB and API
docker-compose up -d

# View logs
docker-compose logs -f enquiry-api
```

### Run with Docker only
```bash
# Build the image
docker build -t enquiry-api .

# Run with external MongoDB
docker run -d -p 8080:8080 \
  -e MONGODB_URI="mongodb://your-mongodb:27017/enquiry_db" \
  enquiry-api
```

## Azure Deployment

### Prerequisites
Set up the following secrets in your GitHub repository:

- `AZURE_CREDENTIALS`: Azure service principal credentials
- `AZURE_REGISTRY_USERNAME`: Azure Container Registry username
- `AZURE_REGISTRY_PASSWORD`: Azure Container Registry password
- `MONGODB_URI`: MongoDB connection string for production

### Update Workflow Variables
Edit `.github/workflows/azure-deploy.yml` and update:
- `AZURE_CONTAINER_REGISTRY`: Your ACR name
- `RESOURCE_GROUP`: Your Azure resource group
- `CONTAINER_INSTANCE_NAME`: Desired container instance name

### Manual Deployment Commands
```bash
# Build and push to ACR
docker build -t your-registry.azurecr.io/enquiry-api:latest .
docker push your-registry.azurecr.io/enquiry-api:latest

# Deploy to Azure Container Instance
az container create \
  --resource-group your-resource-group \
  --name enquiry-api-instance \
  --image your-registry.azurecr.io/enquiry-api:latest \
  --registry-login-server your-registry.azurecr.io \
  --registry-username $ACR_USERNAME \
  --registry-password $ACR_PASSWORD \
  --dns-name-label enquiry-api-unique \
  --ports 8080 \
  --environment-variables MONGODB_URI="your-mongodb-uri" \
  --cpu 1 \
  --memory 2
```

## MongoDB Atlas Setup (Recommended for Production)

1. Create a MongoDB Atlas cluster
2. Get the connection string
3. Set the `MONGODB_URI` environment variable:
   ```
   MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/enquiry_db?retryWrites=true&w=majority
   ```

## Testing the API

```bash
# Health check
curl http://localhost:8080/api/enquiries/health

# Submit enquiry
curl -X POST http://localhost:8080/api/enquiries \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "message": "Test message"
  }'

# Get all enquiries
curl http://localhost:8080/api/enquiries
```

## Architecture

```
├── src/main/java/com/example/enquiry/
│   ├── EnquiryApiApplication.java          # Main application class
│   ├── controller/
│   │   └── EnquiryController.java          # REST endpoints
│   ├── service/
│   │   └── EnquiryService.java             # Business logic
│   ├── repository/
│   │   └── EnquiryRepository.java          # Data access
│   ├── model/
│   │   └── Enquiry.java                    # Entity model
│   ├── dto/
│   │   ├── EnquiryRequest.java             # Request DTO
│   │   └── EnquiryResponse.java            # Response DTO
│   └── exception/
│       └── GlobalExceptionHandler.java     # Error handling
├── src/main/resources/
│   └── application.yml                     # Configuration
├── Dockerfile                              # Docker configuration
├── docker-compose.yml                     # Local development
└── .github/workflows/azure-deploy.yml     # CI/CD pipeline
```

This API provides the same functionality as the original TypeScript/React enquiry system, with robust error handling, validation, and production-ready deployment options.