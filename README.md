# E-Supervision: FYP Management System (Java Swing + MS SQL Server)

A Java desktop application built to streamline Final Year Project (FYP) management, academic supervision workflows, progress logging, meeting schedules, threaded discussions, and user administration across academic institutions.

---

## Role-Based Feature Modules

### 1. Coordinator Dashboard
- **User Administration:** Add, manage, change roles, and deactivate system accounts (`Student`, `Supervisor`, `Lecturer`).
- **Supervisor Assignment:** Assign supervisors to pending student proposals and transition project states to `In Progress`.
- **Central Project Repository:** System-wide table tracking all submitted titles, evaluation statuses, assigned supervisors, and final grades.
- **Student Search & Directory:** Filter student profiles by programme, CGPA, and assigned supervisor.

### 2. Supervisor Dashboard
- **Student Oversight & Logbook Inspection:** Review student profiles and view detailed weekly activity submissions from assigned supervisees.
- **Project Evaluation & Grading:** Grade completed projects (`A`, `B+`, etc.) and archive finished proposals.
- **Meeting Scheduling & Status Tracking:** Manage consultation slots and approve student appointment requests (`Scheduled`, `Completed`, `Cancelled`).
- **Direct Messaging:** Direct messaging interface across registered system contacts.

### 3. Student Dashboard
- **Profile & Progress Tracking:** View academic profile details, current CGPA, project status, and assigned supervisor details.
- **Weekly Logbook System:** Record weekly project milestones, activities, and progress notes.
- **Meeting Appointment Requests:** Request consultation slots with date, time, and custom agenda descriptions.
- **Proposal Submission:** Submit project proposals for coordinator review and supervisor allocation.
- **Threaded Discussion Forum:** Category-based discussion board (`FYP1` / `FYP2`) with multi-user threaded replies.

### 4. Lecturer Dashboard
- **Student Search & Directory:** Search student records and academic standing.
- **Communication Hub:** Internal inbox and composer for staff-student messaging.
- **Lecturer Directory:** Academic directory showing research interests, positions, and current active projects.

---

## Database Architecture & DAOs

The backend uses a standard DAO design pattern connecting to Microsoft SQL Server (`fyp_management`):

- **`users` / `UserDAO`:** System authentication, status flags, and role assignments (`Coordinator`, `Supervisor`, `Student`, `Lecturer`).
- **`student_profiles` & `lecturer_profiles`:** Academic credentials, contacts, research areas, and program details.
- **`projects` / `ProjectDAO`:** Project titles, student-supervisor pairings, status workflows, and grade archiving.
- **`logbook_entries` / `LogbookDAO`:** Time-stamped weekly activity submissions and progress entries.
- **`meetings` / `MeetingDAO`:** Appointment times, agendas, and approval lifecycles.
- **`forum_threads` & `forum_replies` / `ForumDAO`:** Threaded community discussions and replies.
- **`messages` / `MessageDAO`:** Internal messaging and inbox histories.

---

## Getting Started

### Prerequisites

- **Java Development Kit (JDK):** Version 11 or higher.
- **Database:** Microsoft SQL Server running a database named `fyp_management`.
- **Driver:** Microsoft JDBC Driver for SQL Server (`mssql-jdbc.jar`).

### Database Configuration

Update the connection string in `src/fyp/DBConnection.java` to match your SQL Server instance:

```java
private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=fyp_management;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
