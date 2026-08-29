package fyp;

public class User {
    private int id;
    private String username;
    private String role;
    private boolean isActive;

    public User(int id, String username, String role, boolean isActive) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.isActive = isActive;
    }

    public int getId(){
    	return id;
    }
    public String getUsername(){
    	return username;
    }
    public String getRole(){
    	return role;
    }
    public boolean isActive(){
    	return isActive;
    }
}