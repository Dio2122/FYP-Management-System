package fyp;

import java.sql.Timestamp;

public class LogbookEntry {
    private int entryId;
    private int studentId;
    private String entryDate;
    private String activity;
    private Timestamp createdAt;

    public LogbookEntry(int entryId, int studentId, String entryDate, String activity, Timestamp createdAt) {
        this.entryId = entryId;
        this.studentId = studentId;
        this.entryDate = entryDate;
        this.activity = activity;
        this.createdAt = createdAt;
    }

    public int getEntryId(){
    	return entryId;
    }
    public int getStudentId(){
    	return studentId;
    }
    public String getEntryDate(){
    	return entryDate;
    }
    public String getActivity(){
    	return activity;
    }
    public Timestamp getCreatedAt(){
    	return createdAt;
    }
}