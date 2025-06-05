# 📝 Dataset Annotation Platform

A robust and user-friendly platform for annotating datasets, tracking tasks, training AI models, and preparing labeled data for machine learning projects.

---

## 🔍 Overview

This platform is designed to simplify the process of annotating datasets for machine learning and other AI-related projects. It manages the entire lifecycle of annotation projects: uploading datasets, assigning tasks to annotators, tracking progress, and exporting annotated datasets. Additionally, it provides features for testing and training AI models directly on annotated datasets or custom uploaded datasets. The frontend, designed with *React.js* and *Tailwind CSS*, ensures a modern and responsive user experience.

---

## ✨ Features

### 🧑‍💻 Annotator Management
- Create, update, activate/deactivate annotators.
- 🛡 Mark annotators as spammers for quality control.
- ✉ Notify annotators via email when accounts are created or for password reset.

### 📋 Task Management
- Assign tasks to annotators, monitor their progress, and ensure fairness in task distribution.
- Track task statuses, including **Not Started**, **In Progress**, and **Completed**.

### 📂 Dataset Management
- Upload datasets in various formats (*CSV, **JSON, or **XLSX*).
- Retrieve datasets by completion status (100% annotated or incomplete).
- 📤 Export completed datasets for external analysis.

### ✏ Annotation Features
- Annotate text pairs by assigning predefined labels.
- ✔ Enable admin annotations for supervised labeling.
- 🔍 Detect spam annotations by duplicating specific text pairs.

### 📊 Model Training and Testing
- Use annotated datasets to train and test AI models directly through the platform.
- ⚡ Evaluate model performance on uploaded datasets after completing annotations.

### 📨 Email Service Integration
- Automates email notifications for:
    - *Account creation* (sendAccountCreationEmail).
    - *Password reset* (sendPasswordRestEmail).
- Powered by *JavaMailSender* with customizable email templates and sender configuration.

### 📈 Quick Annotation Statistics
- Get live updates on completed and pending annotation tasks.
- Track annotation counts and performance metrics dynamically.

---

## 🛠 Tech Stack

### Backend:
- *☕ Spring Boot (Java)*: Implements controllers, services, and repository layers.
- *🌐 Jakarta EE*: Provides enterprise-scale frameworks for workflows like task and dataset management.
- *🔒 Spring Security: Ensures role-based access control (Super Admin, Admin, Annotator*).
- *📧 JavaMailSender*: Implements the email notification system.
- *📦 Apache POI: Supports **XLSX* file parsing to upload spreadsheet-based datasets.
- *🚀 FastAPI: Independently manages **training* and *testing machine learning models* on prepared datasets.
- *💾 H2 Database*: Provides a simple file/in-memory database functionality for rapid testing and prototyping.

### Frontend:
- *React.js* : Provides a modern and interactive user interface.
- *Tailwind CSS* : Improves styling with responsive design.
- *Axios* : Handles API communication effectively.
- *Chart.js* : Used to create responsive and dynamic graphs for the *admin dashboard*.

### Database:
- **H2**: An in-memory/file database, ideal for development and testing. For production, **PostgreSQL** or **MySQL** can be configured.

---

## 🚀 Installation Guide

### Prerequisites:
- *Java*: JDK 17 or later.
- *Maven*: Dependency management tool.
- *Node.js*: For running the frontend.

### Steps:

#### Backend:
1. *Clone the repository*:
   ```bash
   git https://github.com/ELhadratiOth/Gestion-Des-Annotateurs.git
   cd Gestion-Des-Annotateurs
   ```


2. *Update database and email properties* in src/main/resources/application.properties:
   properties
   # Database configuration (Default: H2 in-memory database)
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driver-class-name=org.h2.Driver
   spring.datasource.username=root
   spring.datasource.password=root

   # Email configuration
   spring.mail.host=smtp.example.com
   spring.mail.port=587
   spring.mail.username=your-email@example.com
   spring.mail.password=your-email-password //the used password in the file is invalid
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true


3. *Build and run the backend*:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```


4. *Access the backend services*:
    - API Endpoint: http://localhost:8080/api/... or  access this URL : http://localhost:8080/swagger-ui/index.html (the  default User is a Super Admin : username/password == othman/othman)
    - H2 Console: http://localhost:8080/h2-console (Default credentials: user = root, password = root)

#### Frontend:
1. *Clone the frontend repository*:
   ```bash
   git clone https://github.com/ELhadratiOth/Gestion-Des-Annotateurs.git
   cd Gestion-Des-Annotateurs
    ```

2. *Install dependencies*:
   ```bash
   npm install
   ```


3. *Configure API base URL* in src/api/index.js:
   javascript
   const BASE_URL = "http://localhost:8080/api";


4. *Start the development server*:
   ```bash
   npm start
   ```


---

## 📚 API Documentation

### Annotator Endpoints:
- GET /api/annotators - Retrieve all annotators.
- POST /api/annotators - Register a new annotator (with email notification).
- PUT /api/annotators/{id} - Update annotator details.
- DELETE /api/annotators/{id} - Delete an annotator.

### Dataset Endpoints:
- POST /api/datasets - Upload datasets (*CSV, **JSON, or **XLSX* formats).
- GET /api/datasets - List datasets.
- GET /api/datasets/advancement/1 - Retrieve 100% annotated datasets.
- DELETE /api/datasets/{idDataset} - Delete a dataset.

### Task Endpoints:
- POST /api/tasks - Assign tasks to annotators.
- GET /api/tasks/{taskId}/coupletexts - Retrieve the text pairs associated with a task.

### Annotation Endpoints:
- POST /api/annotations/tasks/{taskId} - Submit annotations for a task.
- GET /api/annotations/count-last-24h - Get the count of annotations submitted in the last 24 hours.

For the complete API reference, explore the link : http://localhost:8080/swagger-ui/index.html


---

---