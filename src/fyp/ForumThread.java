package fyp;
import java.sql.Timestamp;

public class ForumThread {
    private int threadId;
    private String authorName;
    private String category;
    private String title;
    private String content;
    private Timestamp createdAt;

    public ForumThread(int threadId, String authorName, String category, String title, String content, Timestamp createdAt) {
        this.threadId = threadId;
        this.authorName = authorName;
        this.category = category;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public int getThreadId(){
    	return threadId;
    }
    public String getAuthorName(){
    	return authorName;
    }
    public String getCategory(){
    	return category;
    }
    public String getTitle(){
    	return title;
    }
    public String getContent(){
    	return content;
    }
    public Timestamp getCreatedAt(){
    	return createdAt;
    }
}