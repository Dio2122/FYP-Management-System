package fyp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MeetingDAO {

    // Creates a new meeting, default status is 'Requested' if student makes it, 'Scheduled' if supervisor makes it
    public boolean createMeeting(int supervisorId, int studentId, String meetingTime, String agenda, String status) {
        String query = "INSERT INTO meetings (supervisor_id, student_id, meeting_time, agenda, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, supervisorId);
            stmt.setInt(2, studentId);
            stmt.setString(3, meetingTime);
            stmt.setString(4, agenda);
            stmt.setString(5, status);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Updates existing meeting's status
    public boolean updateMeetingStatus(int meetingId, String newStatus) {
        String query = "UPDATE meetings SET status = ? WHERE meeting_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, meetingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fetches meetings for a specific user
    public List<Meeting> getUserMeetings(int userId, String roleColumn) {
        List<Meeting> meetings = new ArrayList<>();
        String query = "SELECT m.meeting_id, sup.username AS supervisor_name, stu.username AS student_name, " +
                       "m.meeting_time, m.status, m.agenda " +
                       "FROM meetings m " +
                       "JOIN users sup ON m.supervisor_id = sup.id " +
                       "JOIN users stu ON m.student_id = stu.id " +
                       "WHERE m." + roleColumn + " = ? ORDER BY m.meeting_id DESC";
                       
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                meetings.add(new Meeting(
                    rs.getInt("meeting_id"), rs.getString("supervisor_name"),
                    rs.getString("student_name"), rs.getString("meeting_time"),
                    rs.getString("status"), rs.getString("agenda")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meetings;
    }
}