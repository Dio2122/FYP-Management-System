package fyp;

public class Meeting {
    private int meetingId;
    private String supervisorName;
    private String studentName;
    private String meetingTime;
    private String status;
    private String agenda;

    public Meeting(int meetingId, String supervisorName, String studentName, String meetingTime, String status, String agenda) {
        this.meetingId = meetingId;
        this.supervisorName = supervisorName;
        this.studentName = studentName;
        this.meetingTime = meetingTime;
        this.status = status;
        this.agenda = agenda;
    }

    public int getMeetingId(){
    	return meetingId;
    }
    public String getSupervisorName(){
    	return supervisorName;
    }
    public String getStudentName(){
    	return studentName;
    }
    public String getMeetingTime(){
    	return meetingTime;
    }
    public String getStatus(){
    	return status;
    }
    public String getAgenda(){
    	return agenda;
    }
}