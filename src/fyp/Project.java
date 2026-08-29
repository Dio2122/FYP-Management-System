package fyp;

public class Project {
    private int projectId;
    private int studentId;
    private int supervisorId;
    private String title;
    private String status;
    private String grade;

    public Project(int projectId, int studentId, int supervisorId, String title, String status, String grade) {
        this.projectId = projectId;
        this.studentId = studentId;
        this.supervisorId = supervisorId;
        this.title = title;
        this.status = status;
        this.grade = grade;
    }

    public int getProjectId(){
    	return projectId;
    }
    public int getStudentId(){
    	return studentId;
    }
    public int getSupervisorId(){
    	return supervisorId;
    }
    public String getTitle(){
    	return title;
    }
    public String getStatus(){
    	return status;
    }
    public String getGrade(){
    	return grade;
    }
}