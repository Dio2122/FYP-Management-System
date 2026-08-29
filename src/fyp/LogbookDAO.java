package fyp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LogbookDAO {

    public boolean addEntry(int studentId, String date, String activity) {
        String query = "INSERT INTO logbook_entries (student_id, entry_date, activity) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            stmt.setString(2, date);
            stmt.setString(3, activity);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<LogbookEntry> getEntriesByStudentId(int studentId) {
        List<LogbookEntry> entries = new ArrayList<>();
        String query = "SELECT * FROM logbook_entries WHERE student_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                entries.add(new LogbookEntry(rs.getInt("entry_id"), rs.getInt("student_id"), rs.getString("entry_date"), rs.getString("activity"), rs.getTimestamp("created_at")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return entries;
    }

    public List<LogbookEntry> getEntriesByStudentName(String username) {
        List<LogbookEntry> entries = new ArrayList<>();
        String query = "SELECT l.* FROM logbook_entries l JOIN users u ON l.student_id = u.id WHERE u.username = ? ORDER BY l.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                entries.add(new LogbookEntry(rs.getInt("entry_id"), rs.getInt("student_id"), rs.getString("entry_date"), rs.getString("activity"), rs.getTimestamp("created_at")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return entries;
    }
}