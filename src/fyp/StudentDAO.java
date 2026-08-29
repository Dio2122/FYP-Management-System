package fyp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public List<StudentProfile> searchStudents(String keyword, int loggedInUserId) {
        List<StudentProfile> profiles = new ArrayList<>();
        
        String query = "SELECT stu.username AS student_name, sp.programme, sp.cgpa, sp.phone_number, " +
                       "ISNULL(p.title, 'No Project Submitted') AS project_title, " +
                       "ISNULL(sup.username, 'Unassigned') AS supervisor_name " +
                       "FROM users stu " +
                       "LEFT JOIN student_profiles sp ON stu.id = sp.user_id " +
                       "LEFT JOIN projects p ON stu.id = p.student_id " +
                       "LEFT JOIN users sup ON p.supervisor_id = sup.id " +
                       "WHERE stu.role = 'Student' AND stu.username LIKE ? " +
                       "ORDER BY CASE WHEN p.supervisor_id = ? THEN 0 ELSE 1 END ASC, stu.username ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, "%" + keyword + "%"); 
            stmt.setInt(2, loggedInUserId); 
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                profiles.add(new StudentProfile(
                    rs.getString("student_name"),
                    rs.getString("programme") != null ? rs.getString("programme") : "Pending Setup",
                    rs.getDouble("cgpa"),
                    rs.getString("phone_number") != null ? rs.getString("phone_number") : "Pending",
                    rs.getString("project_title"),
                    rs.getString("supervisor_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profiles;
    }

    public StudentProfile getStudentProfileById(int userId) {
        String query = "SELECT stu.username AS student_name, sp.programme, sp.cgpa, sp.phone_number, " +
                       "ISNULL(p.title, 'No Project Submitted') AS project_title, " +
                       "ISNULL(sup.username, 'Unassigned') AS supervisor_name " +
                       "FROM users stu " +
                       "LEFT JOIN student_profiles sp ON stu.id = sp.user_id " +
                       "LEFT JOIN projects p ON stu.id = p.student_id " +
                       "LEFT JOIN users sup ON p.supervisor_id = sup.id " +
                       "WHERE stu.id = ?";
                       
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new StudentProfile(
                    rs.getString("student_name"),
                    rs.getString("programme") != null ? rs.getString("programme") : "Pending Registration",
                    rs.getDouble("cgpa"),
                    rs.getString("phone_number") != null ? rs.getString("phone_number") : "Pending",
                    rs.getString("project_title"),
                    rs.getString("supervisor_name")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}