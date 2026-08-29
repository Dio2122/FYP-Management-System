package fyp;
import java.sql.Timestamp;

public class ForumReply {
    private String authorName;
    private String content;
    private Timestamp createdAt;

    public ForumReply(String authorName, String content, Timestamp createdAt) {
        this.authorName = authorName;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getAuthorName(){
    	return authorName;
    }
    public String getContent(){
    	return content;
    }
    public Timestamp getCreatedAt(){
    	return createdAt;
    }
}