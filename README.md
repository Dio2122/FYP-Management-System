# E-Supervision: FYP Management System (Java Swing + MS SQL Server)

A Java desktop application built to streamline Final Year Project (FYP) management, academic supervision workflows, progress logging, meeting schedules, threaded discussions, and user administration across academic institutions[cite: 34, 45, 53, 55].

---

## Role-Based Feature Modules

### 1. Coordinator Dashboard
- **User Administration:** Add, manage, change roles, and deactivate system accounts (`Student`, `Supervisor`, `Lecturer`)[cite: 34].
- **Supervisor Assignment:** Assign supervisors to pending student proposals and transition project states to `In Progress`[cite: 34, 51].
- **Central Project Repository:** System-wide table tracking all submitted titles, evaluation statuses, assigned supervisors, and final grades[cite: 34].
- **Student Search & Directory:** Filter student profiles by programme, CGPA, and assigned supervisor[cite: 34, 52].

### 2. Supervisor Dashboard
- **Student Oversight & Logbook Inspection:** Review student profiles and view detailed weekly activity submissions from assigned supervisees[cite: 55].
- **Project Evaluation & Grading:** Grade completed projects (`A`, `B+`, etc.) and archive finished proposals[cite: 51, 55].
- **Meeting Scheduling & Status Tracking:** Manage consultation slots and approve student appointment requests (`Scheduled`, `Completed`, `Cancelled`)[cite: 47, 55].
- **Direct Messaging:** Direct messaging interface across registered system contacts[cite: 49, 55].

### 3. Student Dashboard
- **Profile & Progress Tracking:** View academic profile details, current CGPA, project status, and assigned supervisor details[cite: 53, 54].
- **Weekly Logbook System:** Record weekly project milestones, activities, and progress notes[cite: 43, 53].
- **Meeting Appointment Requests:** Request consultation slots with date, time, and custom agenda descriptions[cite: 47, 53].
- **Proposal Submission:** Submit project proposals for coordinator review and supervisor allocation[cite: 51, 53].
- **Threaded Discussion Forum:** Category-based discussion board (`FYP1` / `FYP2`) with multi-user threaded replies[cite: 36, 53].

### 4. Lecturer Dashboard
- **Student Search & Directory:** Search student records and academic standing[cite: 41, 52].
- **Communication Hub:** Internal inbox and composer for staff-student messaging[cite: 41, 49].
- **Lecturer Directory:** Academic directory showing research interests, positions, and current active projects[cite: 40, 41].

---

## Database Architecture & DAOs

The backend uses a standard DAO design pattern connecting to Microsoft SQL Server (`fyp_management`)[cite: 35]:

- **`users` / `UserDAO`:** System authentication, status flags, and role assignments (`Coordinator`, `Supervisor`, `Student`, `Lecturer`)[cite: 45, 56, 57].
- **`student_profiles` & `lecturer_profiles`:** Academic credentials, contacts, research areas, and program details[cite: 40, 42, 52, 54].
- **`projects` / `ProjectDAO`:** Project titles, student-supervisor pairings, status workflows, and grade archiving[cite: 50, 51].
- **`logbook_entries` / `LogbookDAO`:** Time-stamped weekly activity submissions and progress entries[cite: 43, 44].
- **`meetings` / `MeetingDAO`:** Appointment times, agendas, and approval lifecycles[cite: 46, 47].
- **`forum_threads` & `forum_replies` / `ForumDAO`:** Threaded community discussions and replies[cite: 36, 38, 39].
- **`messages` / `MessageDAO`:** Internal messaging and inbox histories[cite: 48, 49].

---

## Getting Started

### Prerequisites

- **Java Development Kit (JDK):** Version 11 or higher.
- **Database:** Microsoft SQL Server running a database named `fyp_management`[cite: 35].
- **Driver:** Microsoft JDBC Driver for SQL Server (`mssql-jdbc.jar`).

### Database Configuration

Update the connection string in `src/fyp/DBConnection.java` to match your SQL Server instance[cite: 35]:

```java
private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=fyp_management;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";