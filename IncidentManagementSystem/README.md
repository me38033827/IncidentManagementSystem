# Incident Management System

## Overview
The Incident Management System provides a REST API for managing incidents. It allows creating, retrieving, updating, and deleting incidents while leveraging in-memory caching for improved performance.

---

## Imported Dependencies and Their Purpose

### **1. Spring Boot Starter Web**
- **Dependency**: `org.springframework.boot:spring-boot-starter-web`
- **Purpose**: Provides the necessary components for building and running a RESTful web service, including Spring MVC and embedded Tomcat server.

### **2. Caffeine**
- **Dependency**: `com.github.ben-manes.caffeine:caffeine`
- **Purpose**: Implements an in-memory caching layer for optimizing read-heavy APIs and reducing latency by caching frequently accessed data.

### **3. Spring Boot Starter Test**
- **Dependency**: `org.springframework.boot:spring-boot-starter-test`
- **Scope**: Test
- **Purpose**: Provides tools for testing, including JUnit 5, Mockito, and Spring TestContext Framework.

### **4. Spring Boot Starter Validation**
- **Dependency**: `org.springframework.boot:spring-boot-starter-validation`
- **Purpose**: Enables annotation-based validation for REST API inputs using `@Valid`.

### **5. Lombok**
- **Dependency**: `org.projectlombok:lombok`
- **Purpose**: Reduces boilerplate code in Java classes by generating getters, setters, constructors, and more at compile time.

---

## APIs Description

### **1. Create Incident**
- **Method**: `POST`
- **Endpoint**: `/api/incidents`
- **Description**: Creates a new incident with a unique ID. Here we only checked if the incident is duplicated to avoid duplication.
- **Request Body**:
  ```json
  {
    "title": "string",
    "description": "string",
    "status": "OPEN | IN_PROGRESS | CLOSED"
  }
  ```
- **Response**:
  ```json
  {
    "id": 1,
    "title": "string",
    "description": "string",
    "status": "OPEN"
  }
  ```
- **Validation**:
    - `title` and `description` must not be blank.
    - If `status` is not provided, it defaults to `OPEN`.

### **2. Retrieve All Incidents**
- **Method**: `GET`
- **Endpoint**: `/api/incidents`
- **Description**: Fetches all incidents.
- **Response**:
  ```json
  [
    {
      "id": 1,
      "title": "string",
      "description": "string",
      "status": "OPEN"
    },
    {
      "id": 2,
      "title": "string",
      "description": "string",
      "status": "CLOSED"
    }
  ]
  ```

### **3. Retrieve Incident by ID**
- **Method**: `GET`
- **Endpoint**: `/api/incidents/{id}`
- **Description**: Retrieves details of an incident by its unique ID.
- **Response**:
  ```json
  {
    "id": 1,
    "title": "string",
    "description": "string",
    "status": "OPEN"
  }
  ```
- **Validation**:
    - `id` must be a positive number.

### **4. Update Incident**
- **Method**: `PUT`
- **Endpoint**: `/api/incidents/{id}`
- **Description**: Updates an existing incident.
- **Request Body**:
  ```json
  {
    "title": "string",
    "description": "string",
    "status": "OPEN | IN_PROGRESS | CLOSED"
  }
  ```
- **Response**:
  ```json
  {
    "id": 1,
    "title": "string",
    "description": "string",
    "status": "IN_PROGRESS"
  }
  ```
- **Validation**:
    - `id` must exist.
    - `title` and `description` must not be blank.

### **5. Delete Incident**
- **Method**: `DELETE`
- **Endpoint**: `/api/incidents/{id}`
- **Description**: Deletes an incident by its unique ID.
- **Response**:
  ```
  HTTP 204 No Content
  ```
- **Validation**:
    - `id` must exist.

---

## Build and Run Instructions

### **Prerequisites**
- Java 17 or later
- Maven

### **Build**
Run the following command to package the application:
```bash
mvn clean package
```

### **Run**
Run the following command to start the application:
```bash
java -jar target/IncidentManagementSystem-1.0-SNAPSHOT.jar
```

The application will start on `http://localhost:8080` by default.

---

## Testing
Run the following command to execute tests:
```bash
mvn test
```


## Improvement ideas
* 认证授权
* 数据库
* 分布式缓存
* 分页
* 上传图片
* ES 关键字搜索
* 批处理
* log4j
* metrics
* 。。。
