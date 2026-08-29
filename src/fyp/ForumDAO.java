package fyp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ForumDAO {

    public boolean createThread(int authorId, String category, String title, String content) {
        String query = "INSERT INTO forum_threads (author_id, category, title, content) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, authorId);
            stmt.setString(2, category);
            stmt.setString(3, title);
            stmt.setString(4, content);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ForumThread> getThreadsByCategory(String category) {
        List<ForumThread> threads = new ArrayList<>();
        String query = "SELECT t.thread_id, u.username, t.category, t.title, t.content, t.created_at " +
                       "FROM forum_threads t JOIN users u ON t.author_id = u.id " +
                       "WHERE t.category = ? ORDER BY t.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                threads.add(new ForumThread(
                    rs.getInt("thread_id"), rs.getString("username"), rs.getString("category"),
                    rs.getString("title"), rs.getString("content"), rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return threads;
    }

    public boolean addReply(int threadId, int authorId, String content) {
        String query = "INSERT INTO forum_replies (thread_id, author_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, threadId);
            stmt.setInt(2, authorId);
            stmt.setString(3, content);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ForumReply> getRepliesForThread(int threadId) {
        List<ForumReply> replies = new ArrayList<>();
        String query = "SELECT u.username, r.content, r.created_at " +
                       "FROM forum_replies r JOIN users u ON r.author_id = u.id " +
                       "WHERE r.thread_id = ? ORDER BY r.created_at ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, threadId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                replies.add(new ForumReply(rs.getString("username"), rs.getString("content"), rs.getTimestamp("created_at")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return replies;
    }
}