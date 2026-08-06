# Smart Printing Kiosk

A full-stack Java application where customers upload PDFs, complete a simulated payment, receive a queue number, and track printing. Paid jobs are processed automatically by a scheduler. Administrators can securely monitor jobs, queue, history and analytics.

## Tech Stack

- Java 21
- Spring Boot 3.5.4
- Spring Security + BCrypt
- Spring Data JPA / Hibernate
- MySQL
- Apache PDFBox
- HTML, Bootstrap, JavaScript
- Chart.js
- Swagger / OpenAPI

## Main Features

- PDF upload with automatic page counting
- Black & White and Color pricing
- Simulated payment and receipt
- Automatic queue processing for paid jobs
- Customer tracking page
- Secure admin login and logout
- Job search, sort and CSV export
- Print history and analytics
- Inline PDF preview for active files
- File deletion after completion/cancellation
- Global exception handling and SLF4J logging

## Local Setup

1. Install Java 21, Maven and MySQL.
2. Create or allow the application to create the `print` database.
3. Set database credentials:

```bash
export DB_USERNAME=root
export DB_PASSWORD='your_mysql_password'
```

4. Optional admin credentials:

```bash
export APP_ADMIN_USERNAME=admin
export APP_ADMIN_PASSWORD=admin123
```

5. Run:

```bash
mvn clean spring-boot:run
```

6. Open:

- Customer: `http://localhost:8080/customer/index.html`
- Admin: `http://localhost:8080/admin/admin-login.html`
- Display: `http://localhost:8080/display/index.html`
- Swagger: `http://localhost:8080/swagger-ui/index.html`

Default development admin: `admin / admin123`.

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | MySQL JDBC URL | local `print` database |
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | empty |
| `APP_ADMIN_USERNAME` | initial admin username | `admin` |
| `APP_ADMIN_PASSWORD` | initial admin password | `admin123` |
| `PORT` | server port | `8080` |
| `SCHEDULER_DELAY_MS` | queue scheduler delay | `30000` |

## GitHub Notes

The repository intentionally ignores `uploads/`, `target/`, local IDE files and secret environment files. Do not commit real database credentials.
