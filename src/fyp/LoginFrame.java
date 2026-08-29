package fyp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        
        setTitle("E-Supervision Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        // Components
        add(new JLabel(" Username:", SwingConstants.CENTER));
        txtUsername = new JTextField();
        add(txtUsername);

        add(new JLabel(" Password:", SwingConstants.CENTER));
        txtPassword = new JPasswordField();
        add(txtPassword);

        add(new JLabel(""));
        btnLogin = new JButton("Login");
        add(btnLogin);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        User loggedInUser = userDAO.authenticateUser(username, password);

        if (loggedInUser != null) {
            if (!loggedInUser.isActive()) {
                JOptionPane.showMessageDialog(this, "Account pending activation by Coordinator.");
                return;
            }
            
            JOptionPane.showMessageDialog(this, "Login Successful!");
            this.dispose(); // Close login window
            
            switch(loggedInUser.getRole()) {
	            case "Coordinator":
	                new CoordinatorDashboard(loggedInUser).setVisible(true);
	                break;
	            case "Supervisor":
	                new SupervisorDashboard(loggedInUser).setVisible(true);
	                break;
	            case "Student":
	                new StudentDashboard(loggedInUser).setVisible(true);
	                break;
	            case "Lecturer": 
	                new LecturerDashboard(loggedInUser).setVisible(true);
	                break;
	            default:
	                JOptionPane.showMessageDialog(null, "Unknown role.");
            }
            
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}