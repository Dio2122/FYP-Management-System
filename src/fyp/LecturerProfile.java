package fyp;

public class LecturerProfile {
    private String name;
    private String email;
    private String academicPosition;
    private String major;
    private String researchInterest;
    private String currentProject;

    public LecturerProfile(String name, String email, String academicPosition, String major, String researchInterest, String currentProject) {
        this.name = name;
        this.email = email;
        this.academicPosition = academicPosition;
        this.major = major;
        this.researchInterest = researchInterest;
        this.currentProject = currentProject;
    }

    public String getName(){
    	return name;
    }
    public String getEmail(){
    	return email;
    }
    public String getAcademicPosition(){
    	return academicPosition;
    }
    public String getMajor(){
    	return major;
    }
    public String getResearchInterest(){
    	return researchInterest;
    }
    public String getCurrentProject(){
    	return currentProject;
    }
}