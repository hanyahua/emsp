This project implements an **Electric Mobility Service Provider (eMSP) Account & Card Service**, providing a set of RESTful APIs to manage `Accounts` and their associated `Cards`. The system supports operations such as account creation, activation, deactivation, and card assignment.
## Links and References
- **API Documentation:** [View Full APIs Documentation](APIs.md)

## Table of Contents
1. [Features](#features)
2. [Technologies Used](#technologies-used)
3. [How to Run](#how-to-run)
4. [API Endpoints](#api-endpoints)
5. [CI/CD Pipeline](#cicd-pipeline)
6. [Deployment](#deployment)
7. [Directory Structure](#directory-structure)
8. [Contact](#contact)

## Features
1. Manage `Accounts`:
    - Create an account.
    - Query accounts with optional filters (`lastUpdatedFrom`, `lastUpdatedTo`, pagination).
    - Change account status ( `Activated`, `Deactivated`).

2. Manage `Cards`:
    - Create cards with unique identifiers (RFID, EMAID).
    - Assign cards to accounts.
    - Change card status (`Assigned`, `Activated`, `Deactivated`). 
    - Query card lists with optional filters (`lastUpdatedFrom`, `lastUpdatedTo`, pagination).

3. Data Validations:
    - Enforce proper data formatting such as valid `email`, `EMAID` standards, and required fields.

4. **Extras**:
    - Unit and Integration Tests.
    - Docker Support (with `Dockerfile`).
    - CI/CD Pipeline (GitHub Actions).

## Technologies Used
### Backend:
- **Java** (Java 21)
- **Spring Boot** (REST API, Validation, Data JPA)
- **Jakarta EE** for core annotations and features
- **H2 Database** (in-memory for quick start and local testing)
- **Redis** (for caching and lock management)
- **Swagger/OpenAPI** (API documentation)

### Tools:
- **Maven** (Build & Dependency Management)
- **JUnit 5** and **Mockito** (Testing)
- **Docker** (For containerization)
- **GitHub Actions** (CI/CD Pipeline)

## How to Run
### Prerequisites:
1. Install **Java 21**.
2. Install **Maven**.
3. Install **Docker** (for containerized execution).

### Quick Start (H2 Database):
1. **Clone the Repository**:
``` bash
   git clone https://github.com/hanyahua/emsp.git
   cd emsp
```
2. **Run Locally**: Start the application with H2 as the default in-memory database:
``` bash
   mvn spring-boot:run
```
The APIs will be available on [http://localhost:8080](http://localhost:8080).
3. **Run in Docker**: Build the Docker image:
``` bash
   docker build -t emsp .
```
Start the container:
``` bash
   docker run -p 8080:8080 emsp
```
4. **Run Tests**: Execute unit and integration tests:
``` bash
   mvn test
```
## API Endpoints
### Account Management:
- **Create Account**: `POST /api/accounts`
- **Get Accounts**: `GET /api/accounts?lastUpdatedFrom=...&lastUpdatedTo=...&pageNumber=...&pageSize=...`
- **Get Account by ID**: `GET /api/accounts/{id}`
- **Change Account Status**: `PATCH /api/accounts/{id}/status`

### Card Management:
- **Create Card**: `POST /api/cards`
- **Get Cards**: `GET /api/cards?lastUpdatedFrom=...&lastUpdatedTo=...&pageNumber=...&pageSize=...`
- **Get Card by ID**: `GET /api/cards/{id}`
- **Change Card Status**: `PATCH /api/cards/{id}/status`


> Full details of the endpoints, request format, and responses can be found in the [APIs Documentation](APIs.md).
>

## CI/CD Pipeline
CI/CD is implemented using **GitHub Actions** to automate the build, test, and deployment processes. The following workflows are set up:
1. **Continuous Integration (CI)**:
    - Automatically triggered on every pull request or push to the `main` branch.
    - Runs all unit tests and integration tests using Maven.
    - Ensures code quality and build stability.

2. **Continuous Deployment (CD)**:
    - Builds and pushes the Docker image to a container registry (e.g., Docker Hub or AWS ECR).
    - Deploys the application to the target environment automatically after passing the CI stage.

### Using GitHub Secrets
The deployment workflow makes use of **GitHub Secrets** to securely store sensitive configuration values. These secrets must be set up in your own repository before running the pipeline. Below are the commonly used secrets:
1. `DOCKER_USERNAME`: Your Docker Hub or container registry username.
2. `DOCKER_PASSWORD`: The access token or password for your container registry.
3. `AWS_ACCESS_KEY_ID`: AWS access key (used if deploying to AWS).
4. `AWS_SECRET_ACCESS_KEY`: AWS secret key (used if deploying to AWS).
5. `DB_USERNAME`: The username for the database instance.
6. `DB_PASSWORD`: The password for the database instance.
7. `EC2_KEY_NAME` *(optional)*: The key EC2 instances to store Terraform state. If this secret is not provided, you must remove the following portion from the `main.tf` file in the Terraform deployment:
    ```yaml
    -var="key_name=${{ secrets.EC2_KEY_NAME }}"
    ```


### Setting Up Secrets
To configure GitHub Secrets in your repository:
1. Navigate to your repository in GitHub.
2. Go to **Settings** > **Secrets and variables** > **Actions**.
3. Create the secrets listed above with the appropriate values for your environment.

Replace the placeholders in `.github/workflows/` with the secrets you set up to ensure proper functionality.

### CI/CD Files:
The workflows are defined in `.github/workflows`:
``` yaml
# DeployToECS.yml
```
The deploy pipeline are defined in `terraform`:
``` tf
# main.tf
# variables.tf
# outputs.tf
# init-ecs.sh
```
## Deployment
Currently, this project is set up for deployment in any Docker-supported environment (e.g., AWS EC2, Kubernetes).
### Deployment Steps:
1. Ensure Docker is installed on your deployment environment.
2. Pull the Docker image from the registry:
``` bash
   docker pull hanyahua/emsp:latest
```
3. Start the container:
``` bash
   docker run -p 8080:8080 hanyahua/emsp:latest
```
4. Access the application at `http://<your-server-ip>:8080`.

## Directory Structure
``` 
.
├── src/
│   ├── main/
│   │   ├── java/           # Java source code
│   │   ├── resources/      # Spring configuration, application.yml
│   ├── test/
│       ├── java/           # Unit and Integration tests
├── .github/
│   ├── workflows/          # CI/CD pipeline files
├── APIs.md                 # API documentation
├── Dockerfile              # Docker setup
├── pom.xml                 # Maven dependencies
└── README.md               # Project documentation
```
## Contact
For support or queries, please contact:
- **Team Name:** eMSP Support Team
- **Email:** hanyahua@outlook.com