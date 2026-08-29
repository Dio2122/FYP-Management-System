package fyp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    // Fetch all users except the person currently logged in
    public List<User> getMessageContacts(int currentUserId) {
        List<User> contacts = new ArrayList<>();
        String query = "SELECT id, username, role, is_active FROM users WHERE id != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                contacts.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("role"), rs.getBoolean("is_active")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    public boolean sendMessage(int senderId, int receiverId, String subject, String body) {
        String query = "INSERT INTO messages (sender_id, receiver_id, subject, body) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, subject);
            stmt.setString(4, body);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Message> getInbox(int receiverId) {
        List<Message> inbox = new ArrayList<>();
        String query = "SELECT u.username AS sender, m.subject, m.body, m.sent_date " +
                       "FROM messages m JOIN users u ON m.sender_id = u.id " +
                       "WHERE m.receiver_id = ? ORDER BY m.sent_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, receiverId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                inbox.add(new Message(rs.getString("sender"), rs.getString("subject"), rs.getString("body"), rs.getTimestamp("sent_date")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inbox;
    }
}