# Student Report Submission & Review System (Group 13)

## Project Identity

**Course:** ET4430E – Lập trình Nâng cao (Advanced Programming), HUST  
**Group:** 13  
**Members:**
- Trần Thế Ninh — 20233873
- Đoàn Ngọc Linh — 20233862
- Đào Hữu Mão — 20233865

**System name:** Student Report Submission and Review System  
**Reference workflow:** Plagiarism-checking process of Can Tho University Publishing House (NXB ĐHCT)

---

## Course Assessment Context

The project (A1.2 – Bài tập lớn) carries 30% of the final grade. It maps to CLO1, CLO2, CLO3, and CLO5.  
Deliverables are tracked through weekly progress reports submitted to the instructor.  
The final product is a full web application with DB connectivity and a group presentation.

Key course CLOs relevant to this project:
- CLO3: Deploy networked applications, connect and manage databases via JDBC, ensure security.
- CLO5: Demonstrate teamwork, project management, and ability to present results.

---

## System Overview

The system replicates the multi-role document submission and review workflow of NXB ĐHCT, adapted for university report submission. It supports four user roles:

1. **Student** – submits reports, views feedback, resubmits revised versions
2. **Instructor** – reviews submissions, gives feedback, approves or rejects
3. **Admin** – manages users, monitors system-wide activity
4. *(Optional)* **Reviewer** – secondary review role for extensibility

### Core Workflow (Activity Diagram Summary)
1. Student registers/logs in
2. Student submits a report (title, file, references)
3. System notifies instructor
4. Instructor reviews, writes feedback, approves or rejects
5. System notifies student of decision
6. If rejected: student revises and resubmits (linked to original via `parent_report_id`)
7. Instructor views resubmission and makes final decision

---

## Technical Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| View | Thymeleaf |
| Data access | Spring JDBC (no ORM/Hibernate) |
| Database | MySQL 8 |
| Build | Maven |
| IDE | VS Code |
| Diagrams | draw.io (raw XML delivery) |

**Important constraint:** The team uses Spring JDBC directly — no JPA, no Hibernate, no ORM auto-generation. SQL schema is managed manually.

---

## Database Schema (Finalized, 5 tables)

### users
```
user_id        INT AUTO_INCREMENT PRIMARY KEY
username       VARCHAR(50) NOT NULL UNIQUE
password_hash  VARCHAR(255) NOT NULL          -- SHA-256 hashed
email          VARCHAR(100) NOT NULL UNIQUE
full_name      VARCHAR(100) NOT NULL
role           ENUM('student','instructor','admin') NOT NULL
created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

### reports
```
report_id        INT AUTO_INCREMENT PRIMARY KEY
student_id       INT NOT NULL                  -- FK → users(user_id)
instructor_id    INT                           -- FK → users(user_id), assigned reviewer
title            VARCHAR(200) NOT NULL
file_path        VARCHAR(500) NOT NULL
status           ENUM('pending','under_review','approved','rejected') DEFAULT 'pending'
parent_report_id INT                           -- FK → reports(report_id), self-ref for resubmissions
submitted_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
```

### references_list
```
reference_id  INT AUTO_INCREMENT PRIMARY KEY
report_id     INT NOT NULL                    -- FK → reports(report_id)
citation_text TEXT NOT NULL
url           VARCHAR(500)
added_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

### feedbacks
```
feedback_id    INT AUTO_INCREMENT PRIMARY KEY
report_id      INT NOT NULL                   -- FK → reports(report_id)
instructor_id  INT NOT NULL                   -- FK → users(user_id), explicit (not inferred)
content        TEXT NOT NULL
created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

### notifications
```
notification_id  INT AUTO_INCREMENT PRIMARY KEY
user_id          INT NOT NULL                 -- FK → users(user_id), recipient
report_id        INT                          -- FK → reports(report_id), optional context
message          TEXT NOT NULL
is_read          BOOLEAN DEFAULT FALSE
created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

**Key design decisions:**
- `parent_report_id` is a self-referencing FK in `reports` — tracks resubmission chains without duplicating data
- `instructor_id` is explicit in `feedbacks` — not inferred from `reports.instructor_id` — for audit integrity
- No ORM; all DDL is written by hand and run directly in MySQL

---

## Java Model Classes (Baseline)

The following model classes have been produced. Each maps 1:1 to a database table using plain Java fields with getters/setters (no annotations beyond basic Java).

- `User.java` — fields: userId, username, passwordHash, email, fullName, role, createdAt
- `Report.java` — fields: reportId, studentId, instructorId, title, filePath, status, parentReportId, submittedAt, updatedAt
- `Reference.java` (or `ReferencesItem.java`) — maps references_list
- `Feedback.java` — maps feedbacks
- `Notification.java` — maps notifications

---

## Project Structure (Spring Boot / Maven)

Standard Spring Boot layout under `src/main/`:
- `java/com/group13/reportsystem/` — root package
  - `controller/` — Spring MVC controllers (one per feature area)
  - `model/` — Java model classes
  - `repository/` — Spring JDBC DAO classes
  - `service/` — business logic layer
  - `config/` — Spring security or app config beans
- `resources/`
  - `templates/` — Thymeleaf HTML templates
  - `static/` — CSS, JS, images
  - `application.properties` — DB connection, server config

---

## Differentiating Features (Scoped)

Two features distinguish this system beyond the baseline CRUD:

1. **Statistics Dashboard (Instructor view)**
   - Shows total submissions, approval rate, rejection rate, pending count
   - Simple aggregate queries via Spring JDBC
   - Displayed as a summary card grid on the instructor home page

2. **Version History Timeline (Student view)**
   - Displays the full chain of a report's resubmissions using `parent_report_id`
   - Rendered as a vertical timeline in Thymeleaf
   - Allows student to see feedback at each stage

---

## Documentation & Reporting Rules

### Weekly Report Format (strict 3-part structure)
Every weekly report follows this structure exactly:

1. **Review of prior week** — what was planned, what was completed, any deviations and why
2. **Current week's work** — detailed description of what was done this week
3. **Plan for next week** — specific tasks, assigned members, expected outputs

### Formatting constraints (non-negotiable)
- All report content is **plain prose only** — no bullet points structured as code, no directory trees, no code blocks, no tables inside the report narrative
- No downloadable files — all report content is delivered **inline in the conversation**
- Diagrams are **never described in prose**; they are always delivered as **raw draw.io XML** for the user to paste directly into draw.io

### Instructor feedback already received (do not repeat these errors)
- Missing Name and Responsibility columns in the business process table → must be included
- Activity diagram was missing the System swimlane → must always be included
- Workflow changes must be explicitly documented when they occur
- Functional requirements must follow the sequence of the activity diagram
- Entity attributes must be expanded with full field definitions, not just entity names

---

## Prior Work Completed (as of Week 3)

The following has been done and should not be repeated or contradicted:

- Business process description written (with NXB ĐHCT reference)
- Activity diagram designed (includes Student, Instructor, System swimlanes)
- Functional requirements documented (aligned to activity diagram sequence)
- Data dictionary completed
- Five-table database schema finalized and SQL scripts produced
- Java model classes for all five tables written
- Spring Boot project initialized with Maven
- `application.properties` configured for MySQL 8 connection
- Week 4 implementation plan drafted

---

## Implementation Guidance Principles

Because all three team members are beginner-level Java developers, explanations should always:

1. Start from first principles — define any unfamiliar concept before using it
2. Explain *why* a pattern is used, not just *what* it is
3. Use concrete small examples before showing full code
4. Flag potential beginner mistakes explicitly (e.g., forgetting `@Autowired`, null FK handling, SQL injection via string concatenation)
5. Avoid assuming familiarity with Spring annotations — explain each one when first introduced

Concepts that required ground-up explanation in past sessions (do not skip over these if they arise again):
- Maven project structure and what `pom.xml` does
- SHA-256 password hashing and why plaintext is dangerous
- Session management in Spring (how login state is kept)
- Self-referencing foreign keys in SQL
- Spring JDBC vs JPA — why the team uses JDBC directly

---

## draw.io Diagram Conventions

All diagrams use traditional draw.io style:
- **Entities:** rectangles
- **Relationships:** diamond shapes with connecting lines
- **Attributes:** ellipses connected to their entity
- **Swimlanes:** for activity diagrams (Student | Instructor | System)
- Delivered as **raw XML only** — never as image embeds or prose descriptions

---

## Web Application – Final Deliverable Requirements

Based on course requirements (CLO3, CLO5) and the A1.2 assessment criteria, the final web application must demonstrate:

### Functional requirements (minimum)
1. User registration and login with role-based access (student / instructor / admin)
2. Student can submit a report (form with title, file upload, reference list)
3. System records submission and creates a notification for the assigned instructor
4. Instructor can view pending reports, read submitted content, write feedback
5. Instructor can approve or reject a report; decision triggers student notification
6. Student can view feedback and resubmit a revised report (linked to original)
7. Admin can view all users and all reports
8. Statistics dashboard for instructor (submission counts, approval/rejection rates)
9. Version history timeline for student (chain of resubmissions with feedback per version)

### Security requirements
- Passwords stored as SHA-256 hashes (minimum); bcrypt preferred if time allows
- Role-based access control: students cannot access instructor routes and vice versa
- Session-based authentication via Spring Security or manual HttpSession management

### Technology requirements (from course CLOs)
- Spring Boot backend (satisfies CLO3 – networked application with DB)
- MySQL via Spring JDBC (satisfies CLO3 – JDBC connection and SQL)
- Thymeleaf templates for all views (server-side rendering)
- Must run locally on `localhost:8080` for demonstration

### Presentation requirements (CLO5)
- Group must demo the live application
- Walk through at least: login flow, report submission, instructor review, student resubmission
- Each member explains the part they implemented
- Show the statistics dashboard and version history as differentiating features

---

## Session State & What Comes Next

The team is currently at the beginning of implementation (Week 4+). The immediate priorities in order are:

1. Set up MySQL database and run the schema creation SQL
2. Configure `application.properties` with correct DB credentials
3. Implement `UserRepository` with Spring JDBC (register, findByUsername)
4. Implement login/session — either Spring Security or manual HttpSession
5. Build the student dashboard (submit report form, view own reports)
6. Build the instructor dashboard (view pending, write feedback, approve/reject)
7. Implement notifications (insert on status change, display as badge/list)
8. Implement version history (recursive query or iterative lookup via `parent_report_id`)
9. Implement statistics dashboard (aggregate SQL queries)
10. Admin user management page
11. Final styling, error handling, testing
12. Prepare group presentation demo script
