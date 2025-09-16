# Fitness Microservices App

Fitness Microservices App is a cloud-native application designed to track and manage user fitness activities. It leverages **Spring Boot**, **Spring Cloud**, **Keycloak**, **React**, and **AI-based recommendations** using Gemini. The architecture is based on microservices with dedicated responsibilities and communication via **RabbitMQ** and **Spring Cloud Gateway**.

---

## **Technologies Used**

- **Backend:** Spring Boot, Spring Cloud Gateway, Spring Eureka (Netflix OSS), Spring Config Server  
- **Authentication & Security:** OAuth2, Keycloak (PKCE flow), JWT  
- **Databases:** PostgreSQL (user-service), MongoDB (activity-service and ai-service)  
- **Message Queue:** RabbitMQ  
- **AI Service:** Gemini  
- **Frontend:** React (port 5173)  
- **Microservices Architecture:** Independent services with service discovery via Eureka  

---

## **Microservices Overview**

### **1. User-Service**
- **Purpose:** Handles user authentication and management.
- **Port:** 8081  
- **Database:** PostgreSQL (stores user data)  
- **Authentication:** OAuth2 with Keycloak (PKCE flow, JWT tokens)  
- **Responsibilities:**  
  - Register and authenticate users  
  - Manage user profiles  

### **2. Activity-Service**
- **Purpose:** Tracks user activities like walking, running, cardio, etc.  
- **Port:** 8082  
- **Database:** MongoDB (stores activity data: activity-id, user_id, created_at, duration, etc.)  
- **Responsibilities:**  
  - Create, read, and manage user activity data  
  - Validate users via web clients  
  - Send activity data to AI-Service via RabbitMQ  

### **3. AI-Service**
- **Purpose:** Provides AI-based activity recommendations using Gemini  
- **Port:** 8083  
- **Responsibilities:**  
  - Consume activity data from RabbitMQ  
  - Process activities using Gemini AI  
  - Return recommendations to the frontend  
  - Use web clients for RabbitMQ and user validation  

### **4. Eureka Server**
- **Purpose:** Service discovery for all microservices  
- **Port:** 8761  
- **Responsibilities:**  
  - Register all services  
  - Enable service-to-service communication via service names  

### **5. API Gateway**
- **Purpose:** Single entry point for frontend clients  
- **Port:** 8080  
- **Responsibilities:**  
  - Route requests to respective microservices  
  - Secure endpoints for client access  

### **6. Config Server**
- **Purpose:** Centralized configuration management for all microservices  
- **Responsibilities:**  
  - Maintain configuration properties for backend services  

---

## **Frontend**
- **Framework:** React  
- **Port:** 5173  
- **Purpose:** Provides user interface for login, activity tracking, and AI recommendations.  
- **Integration:** Communicates with API Gateway for backend services.  

---

## **Architecture Flow**

1. User logs in via the React frontend (Keycloak OAuth2 PKCE authentication).  
2. API Gateway routes requests to the respective microservices:  
   - User management → User-Service  
   - Activity management → Activity-Service  
   - AI recommendations → AI-Service  
3. Activity-Service stores activity data in MongoDB and sends messages to AI-Service through RabbitMQ.  
4. AI-Service uses Gemini to analyze the activity and sends recommendations back to the frontend.  
5. All microservices are registered on Eureka for service discovery.  
6. Configurations for all services are centralized in Config Server.  

---

## **Ports Summary**

| Service          | Port | Database/Notes                                  |
|-----------------|------|-----------------------------------------------|
| User-Service     | 8081 | PostgreSQL                                     |
| Activity-Service | 8082 | MongoDB, RabbitMQ integration                 |
| AI-Service       | 8083 | RabbitMQ, Gemini AI                            |
| Eureka Server    | 8761 | Service Discovery                              |
| API Gateway      | 8080 | Routes all client requests                     |
| Frontend         | 5173 | React App                                     |

---

## **Setup Instructions**

1. Clone the repository.  
2. Start **PostgreSQL** and **MongoDB** for user and activity services.  
3. Start **RabbitMQ** for message queue communication.  
4. Start **Config Server**.  
5. Start **Eureka Server** (port 8761).  
6. Start microservices in the following order:  
   - User-Service  
   - Activity-Service  
   - AI-Service  
7. Start **API Gateway** (port 8080).  
8. Start **React Frontend** (port 5173).  
9. Access the application via `http://localhost:5173`.  

---

## **Key Features**

- Microservices architecture for scalability  
- User authentication using Keycloak OAuth2 with PKCE  
- Activity tracking with MongoDB  
- AI-based activity recommendations via Gemini  
- Centralized configuration using Spring Config Server  
- Service discovery via Eureka  
- Secure API routing via Spring Cloud Gateway  

---

## **Author**

**Akshit Panwar**  
Backend Developer | Java | Spring Boot | Microservices | AI Integration  

