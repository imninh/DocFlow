# DocFlow

DocFlow is a Spring Boot 3 and Thymeleaf web application for a student report submission and review workflow. It follows the stack direction with Java 17, Spring Boot, Thymeleaf, and Spring JDBC.

## Current Features

- Login with `class code + username + password`
- Role-based workspace for `student`, `instructor`, and `admin`
- Student report submission with:
  - title
  - instructor selection
  - file upload
  - reference list
- Instructor review flow with:
  - pending queue
  - file access for submitted reports
  - approve/reject decision
  - feedback entry
  - recent processed reports section
- Admin overview of users and reports
- JDBC-backed persistence
- Local file storage for uploaded reports

## Tech Stack

- Java 17
- Spring Boot 3
- Thymeleaf
- Spring JDBC
- H2 file database for local development
- MySQL driver included for future migration

## Project Structure

```text
src/main/java/com/group13/reportsystem
  controller/
  model/
  repository/
  service/

src/main/resources
  static/css/
  templates/
  application.properties
  schema.sql
  data.sql
```

## Run Locally

From the project root:

```powershell
.\run-local.cmd
```

The helper script will:

1. use Maven from your machine if available
2. otherwise download a portable Maven copy into `.tools`
3. run the Spring Boot app

Open:

[http://localhost:8080](http://localhost:8080)

## Build

```powershell
.\build-local.cmd
```

## Local Database

The app currently uses an H2 file database:

- URL: `jdbc:h2:file:./data/docflow;MODE=MySQL;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE`
- Schema: `src/main/resources/schema.sql`
- Seed data: `src/main/resources/data.sql`

Uploaded files are stored under:

- `./uploads`

## Demo Accounts

These are available for local testing:

- Student
  - Class code: `ET4430E-K68`
  - Username: `ninh`
  - Password: `ninh123`
- Instructor
  - Class code: `ET4430E-K68`
  - Username: `linh`
  - Password: `linh123`
- Admin
  - Class code: `SYSTEM`
  - Username: `mao`
  - Password: `mao123`

## Notes

- This version is already JDBC-based, but still optimized for local demonstration with H2.
- To move to MySQL, update `application.properties` and point Spring Boot to your MySQL schema.
- Passwords are plain demo values right now for convenience. Before presenting as a more realistic system, replace them with hashed passwords and a proper auth flow.

## Suggested Next Improvements

- migrate local auth passwords to SHA-256 or bcrypt
- add user registration or admin account management
- support report status filters and search
- add downloadable feedback history per report
- switch from H2 to MySQL for the final course submission
