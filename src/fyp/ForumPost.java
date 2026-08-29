package fyp;

import java.sql.Timestamp;

public class ForumPost {
    private String authorName;
    private String category;
    private String content;
    private Timestamp postDate;

    public ForumPost(String authorName, String category, String content, Timestamp postDate) {
        this.authorName = authorName;
        this.category = category;
        this.content = content;
        this.postDate = postDate;
    }

    public String getAuthorName(){ 
    	return authorName; 
    }
    public String getCategory(){ 
    	return category; 
    }
    public String getContent(){ 
    	return content; 
    }
    public Timestamp getPostDate(){ 
    	return postDate; 
    }
}