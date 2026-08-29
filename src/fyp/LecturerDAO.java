package fyp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LecturerDAO {

    public List<LecturerProfile> getAllLecturerProfiles() {
        List<LecturerProfile> profiles = new ArrayList<>();
        
        
        String query = "SELECT u.username, p.email, p.academic_position, p.major, p.research_interest, p.current_project " +
                       "FROM users u " +
                       "LEFT JOIN lecturer_profiles p ON u.id = p.user_id " +
                       "WHERE u.role IN ('Lecturer', 'Supervisor') AND u.is_active = 1 " +
                       "ORDER BY u.username ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // displays "Pending Setup" if a supervisor doesn't have a profile yet
                profiles.add(new LecturerProfile(
                    rs.getString("username"),
                    rs.getString("email") != null ? rs.getString("email") : "Pending Setup",
                    rs.getString("academic_position") != null ? rs.getString("academic_position") : "Pending Setup",
                    rs.getString("major") != null ? rs.getString("major") : "Pending Setup",
                    rs.getString("research_interest") != null ? rs.getString("research_interest") : "Pending Setup",
                    rs.getString("current_project") != null ? rs.getString("current_project") : "Pending Setup"
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profiles;
    }
}