# 🖨️ Smart Printing Kiosk

<p align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen?style=for-the-badge&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6-green?style=for-the-badge)
![MySQL](https://img.shields.io/badge/MySQL-9-blue?style=for-the-badge&logo=mysql)
![Hibernate](https://img.shields.io/badge/Hibernate-ORM-brown?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-Build-red?style=for-the-badge&logo=apachemaven)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

</p>

---

# 📌 Project Overview

**Smart Printing Kiosk** is a **Full Stack Spring Boot Application** that allows customers to upload PDF documents through a QR code, simulate payment, receive a queue number, and track print status in real time.

The system also provides an **Admin Dashboard** for monitoring print jobs, managing the queue, viewing analytics, and securely handling print requests.

This project demonstrates real-world concepts such as:

- Spring Boot REST APIs
- Spring Security Authentication
- Queue Management
- File Upload
- Scheduler
- MySQL Database
- JavaScript Frontend
- MVC Architecture

---

# 🚀 Features

## 👤 Customer Module

- 📄 Upload PDF Documents
- 📑 Automatic PDF Page Counting
- 🎨 Select Black & White / Color Printing
- 💰 Automatic Price Calculation
- 💳 Simulated Payment
- 🧾 Digital Receipt
- 🔢 Queue Number Generation
- 📍 Real-Time Print Tracking
- 📱 QR Code Based Access

---

## 👨‍💼 Admin Module

- 🔐 Secure Login
- 📊 Dashboard
- 📋 View All Print Jobs
- 🖨 Queue Management
- 📜 Print History
- 🔍 Search Jobs
- ↕ Sorting
- 📄 Pagination
- 📈 Analytics
- 📂 PDF Preview

---

## ⚙ Backend Features

- Spring Boot REST APIs
- Spring Security + BCrypt
- MySQL Database
- Spring Data JPA
- Hibernate ORM
- Global Exception Handling
- Scheduler
- SLF4J Logging
- Swagger Documentation
- Automatic File Cleanup

---

# 🏗️ System Architecture

```
                  CUSTOMER

                     │
                     ▼

              Scan QR Code

                     │
                     ▼

              Upload PDF File

                     │
                     ▼

             Spring Boot Backend

                     │

       ┌─────────────┴─────────────┐

       ▼                           ▼

   MySQL Database             Upload Folder

       │

       ▼

   Queue Scheduler

       │

       ▼

  Admin Dashboard

       │

       ▼

   Printer (Future)
```

---

# 📸 Screenshots

Create a folder named:

```
screenshots/
```

Add these screenshots:

```
screenshots/
│
├── home.png
├── upload.png
├── payment.png
├── tracking.png
├── admin-login.png
├── dashboard.png
├── jobs.png
├── queue.png
├── history.png
└── analytics.png
```

Then display them:

```markdown
## Home

![Home](screenshots/home.png)

## Upload

![Upload](screenshots/upload.png)

## Dashboard

![Dashboard](screenshots/dashboard.png)

## Queue

![Queue](screenshots/queue.png)
```

---

# 🛠️ Tech Stack

### Backend

- Java 21
- Spring Boot 3.5.4
- Spring Security
- Spring Data JPA
- Hibernate

### Frontend

- HTML5
- CSS3
- Bootstrap 5
- JavaScript

### Database

- MySQL

### Tools

- Maven
- Git
- GitHub
- Swagger
- VS Code
- MySQL Workbench

### Libraries

- Apache PDFBox
- BCrypt Password Encoder
- Chart.js

---

# 📂 Project Structure

```
SmartPrintingKiosk
│
├── database/
│      setup.sql
│
├── screenshots/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   ├── resources/
│   │   │      application.properties
│   │   │
│   │   └── static/
│   │          admin/
│   │          customer/
│   │          js/
│   │          css/
│   │          images/
│
├── uploads/
│
├── pom.xml
│
├── README.md
│
└── .gitignore
```

---

# 📡 REST APIs

## Customer APIs

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | /printjobs/upload | Upload PDF |
| POST | /printjobs | Create Print Job |
| GET | /printjobs/{id} | Track Print Job |

---

## Admin APIs

| Method | Endpoint |
|---------|----------|
| GET | /printjobs |
| PUT | /printjobs/status/{id} |
| DELETE | /printjobs/{id} |
| GET | /printjobs/history |
| GET | /printjobs/page |
| GET | /printjobs/sort |

---

# 🗄️ Database

Main Table

```
print_jobs
```

Columns

- job_id
- file_name
- file_path
- pages
- copies
- print_type
- amount
- status
- payment_status
- queue_number

---

# 🔐 Security

- Spring Security
- BCrypt Password Encryption
- Session Management
- Protected Admin Pages
- Secure Login & Logout

---

# 🔄 Queue Workflow

```
Upload PDF
      │
      ▼
Payment
      │
      ▼
Create Job
      │
      ▼
PENDING
      │
      ▼
Scheduler
      │
      ▼
PRINTING
      │
      ▼
COMPLETED
```

---

# ⚙️ Installation

## Clone Repository

```bash
git clone https://github.com/tarunsai4411/Smart-Printing-Kiosk.git
```

---

## Open Project

```bash
cd Smart-Printing-Kiosk
```

---

## Configure Database

Create MySQL Database

```sql
CREATE DATABASE print;
```

---

## Environment Variables

```properties
DB_USERNAME=root
DB_PASSWORD=your_password
APP_ADMIN_USERNAME=admin
APP_ADMIN_PASSWORD=admin123
```

---

## Run

```bash
mvn clean spring-boot:run
```

---

# 🌐 URLs

Customer

```
http://localhost:8080/customer/index.html
```

Admin

```
http://localhost:8080/admin/admin-login.html
```

Swagger

```
http://localhost:8080/swagger-ui/index.html
```

---

# 📊 Future Enhancements

- ✅ Razorpay Integration
- ✅ Stripe Integration
- ✅ Email Notifications
- ✅ SMS Notifications
- ✅ Cloud Deployment
- ✅ Multiple Printers
- ✅ AI Print Optimization
- ✅ Print Cost Estimation
- ✅ User Accounts

---

# 📖 Learning Outcomes

This project demonstrates:

- Spring Boot
- Spring Security
- REST APIs
- JPA & Hibernate
- MySQL
- JavaScript
- MVC Architecture
- Scheduler
- Authentication
- File Upload
- Exception Handling
- Queue Management

---

# 👨‍💻 Author

## M. Tarunsai

🎓 B.Tech Graduate

💻 Java Full Stack Developer

📊 Data Analyst

### GitHub

https://github.com/tarunsai4411

### LinkedIn

(Add your LinkedIn profile)

---

# ⭐ Support

If you found this project useful,

⭐ Star this repository on GitHub.

---

# 📜 License

This project is licensed under the MIT License.