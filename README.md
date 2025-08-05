# User Support App (Quarkus + MySQL)

This is a REST API backend for a user support application. It allows customers to send messages in different "rooms" and operators to respond via a web interface. It is built using **Quarkus** and uses **MySQL** as its database.

---

## Features

- Two user roles: `CUSTOMER` and `OPERATOR`
- Users can:
    - View rooms
    - Start conversations
    - Send and receive messages
- Operators can:
    - View pending/taken conversations
    - Take over pending conversations
    - Reply to messages
- HTTP Basic authentication
- OpenAPI/Swagger documentation
- Dockerized setup

---

## Running the App via Docker

### Prerequisites

- Docker
- Docker Compose

### Steps

1. Build the Quarkus application:

```bash
./mvnw clean package -DskipTests
```

2. Start the services:

```bash
docker-compose up --build
```

> This will:
> - Launch a MySQL database
> - Run the Quarkus app on `http://localhost:8080`
> - Create initial users in the database via `import.sql`

---

## API Testing

### Swagger UI (OpenAPI)

After the app starts, open:

**[http://localhost:8080/q/swagger-ui](http://localhost:8080/q/swagger-ui)**

Here you can check all the endpoints and test the API interactively.

---

### Postman Collection

You can also use the provided Postman collection to test the API. The collection has pre-set variables. 

#### Files:

- `postman/user-support-api.postman_collection.json`

#### Variables Used:

| Variable             | Example Value          |
|----------------------|------------------------|
| `url`               | `http://localhost:8080`|
| `conversationId`    | `1`                    |
| `operator_username` | `operator`             |
| `operator_password` | `operator_pass`        |
| `customer_username` | `customer`             |
| `customer_password` | `customer_pass`        |

#### How to Use:

1. Import the collection in Postman.
2. Select a request and hit **Send** (authentication is already set with the variables for both customer and operator endpoints).

---

## Default Users (from `import.sql`)

| Username  | Password       | Role     |
|-----------|----------------|----------|
| operator  | operator_pass  | OPERATOR |
| customer  | customer_pass  | CUSTOMER |

Users added using `import.sql` are also used in the Postman collection.

---

## Project Structure

```bash
.
├── Dockerfile
├── docker-compose.yml
├── postman/
│   └── user-support-api.postman_collection.json
└── src/
```