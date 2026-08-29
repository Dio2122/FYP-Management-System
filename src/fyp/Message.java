package fyp;

import java.sql.Timestamp;

public class Message {
    private String senderName;
    private String subject;
    private String body;
    private Timestamp sentDate;

    public Message(String senderName, String subject, String body, Timestamp sentDate) {
        this.senderName = senderName;
        this.subject = subject;
        this.body = body;
        this.sentDate = sentDate;
    }

    public String getSenderName(){
    	return senderName;
    }
    public String getSubject(){
    	return subject;
    }
    public String getBody(){
    	return body;
    }
    public Timestamp getSentDate(){
    	return sentDate;
    }
}