package fyp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {

    public boolean submitProject(int studentId, String title) {
        String query = "INSERT INTO projects (student_id, title, status) VALUES (?, ?, 'Pending Approval')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, studentId);
            stmt.setString(2, title);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Submission failed. Student may already have a project.");
            return false;
        }
    }

    // Retrieves a student's project
    public Project getStudentProject(int studentId) {
        String query = "SELECT * FROM projects WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Project(
                    rs.getInt("project_id"),
                    rs.getInt("student_id"),
                    rs.getInt("supervisor_id"), 
                    rs.getString("title"),
                    rs.getString("status"),
                    rs.getString("grade")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
 // Fetches all projects for the Coordinator
    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT * FROM projects";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                projects.add(new Project(
                    rs.getInt("project_id"),
                    rs.getInt("student_id"),
                    rs.getInt("supervisor_id"), 
                    rs.getString("title"),
                    rs.getString("status"),
                    rs.getString("grade")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }

    // Updates the supervisor and status
    public boolean assignSupervisor(int projectId, int supervisorId) {
        String query = "UPDATE projects SET supervisor_id = ?, status = 'In Progress' WHERE project_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, supervisorId);
            stmt.setInt(2, projectId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
 // Gets projects assigned to the logged-in supervisor
    public List<Project> getProjectsBySupervisor(int supervisorId) {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT * FROM projects WHERE supervisor_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, supervisorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                projects.add(new Project(
                    rs.getInt("project_id"),
                    rs.getInt("student_id"),
                    rs.getInt("supervisor_id"), 
                    rs.getString("title"),
                    rs.getString("status"),
                    rs.getString("grade") 
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }

    // Allows the supervisor to grade and archive the project
    public boolean evaluateProject(int projectId, String status, String grade) {
        String query = "UPDATE projects SET status = ?, grade = ? WHERE project_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            stmt.setString(2, grade);
            stmt.setInt(3, projectId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProject(int projectId) {
        String query = "DELETE FROM projects WHERE project_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, projectId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}