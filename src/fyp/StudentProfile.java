package fyp;

public class StudentProfile {
    private String studentName;
    private String programme;
    private double cgpa;
    private String phoneNumber;
    private String projectTitle;
    private String supervisorName;

    public StudentProfile(String studentName, String programme, double cgpa, String phoneNumber, String projectTitle, String supervisorName) {
        this.studentName = studentName;
        this.programme = programme;
        this.cgpa = cgpa;
        this.phoneNumber = phoneNumber;
        this.projectTitle = projectTitle;
        this.supervisorName = supervisorName;
    }

    public String getStudentName(){
    	return studentName;
    }
    public String getProgramme(){
    	return programme;
    }
    public double getCgpa(){
    	return cgpa;
    }
    public String getPhoneNumber(){
    	return phoneNumber;
    }
    public String getProjectTitle(){
    	return projectTitle;
    }
    public String getSupervisorName(){
    	return supervisorName;
    }
}