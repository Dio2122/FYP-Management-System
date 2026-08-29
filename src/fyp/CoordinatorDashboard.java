package fyp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CoordinatorDashboard extends JFrame {
    private User loggedInUser;
    private ProjectDAO projectDAO;
    private UserDAO userDAO;
    private LecturerDAO lecturerDAO;
    private StudentDAO studentDAO; 

    private JTable studentSearchTable;
    private DefaultTableModel searchTableModel;
    private JTextField txtSearch;

    private JTable assignTable;
    private DefaultTableModel assignTableModel;
    private JComboBox<String> supervisorComboBox;
    private List<User> supervisorList;
    private JTable usersTable;
    private DefaultTableModel usersTableModel;
    private JTable repoTable;
    private DefaultTableModel repoTableModel;

    
    
    public CoordinatorDashboard(User user) {
        this.loggedInUser = user;
        this.projectDAO = new ProjectDAO();
        this.userDAO = new UserDAO();
        this.lecturerDAO = new LecturerDAO();
        this.studentDAO = new StudentDAO(); 
        
        setTitle("E-Supervision - Coordinator Dashboard: " + user.getUsername());
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Student Search", createStudentSearchPanel()); 
        tabbedPane.addTab("Manage Users", createManageUsersPanel());
        tabbedPane.addTab("Assign Supervisors", createAssignmentPanel());
        tabbedPane.addTab("Manage Repositories", createRepositoryPanel());
        tabbedPane.addTab("Lecturer Directory", createDirectoryPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    // Student Search Panel
    private JPanel createStudentSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Student Username:"));
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);
        
        JButton btnSearch = new JButton("Search");
        searchPanel.add(btnSearch);
        
        JButton btnClear = new JButton("View All");
        searchPanel.add(btnClear);
        
        panel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"Student Name", "Programme", "CGPA", "Phone Number", "Project Title", "Supervisor"};
        searchTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        studentSearchTable = new JTable(searchTableModel);
        studentSearchTable.setRowHeight(25);
        panel.add(new JScrollPane(studentSearchTable), BorderLayout.CENTER);

        btnSearch.addActionListener(e -> loadStudentData(txtSearch.getText().trim()));
        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            loadStudentData(""); 
        });

        loadStudentData(""); 
        return panel;
    }

    private void loadStudentData(String keyword) {
        searchTableModel.setRowCount(0);
        List<StudentProfile> profiles = studentDAO.searchStudents(keyword, loggedInUser.getId());
        for (StudentProfile sp : profiles) {
            searchTableModel.addRow(new Object[]{
                sp.getStudentName(), sp.getProgramme(), sp.getCgpa(),
                sp.getPhoneNumber(), sp.getProjectTitle(), sp.getSupervisorName()
            });
        }
    }

    // Manage Users
    private JPanel createManageUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add / Update User"));

        formPanel.add(new JLabel("Username: ")); JTextField txtUser = new JTextField(10); formPanel.add(txtUser);
        formPanel.add(new JLabel("Password: ")); JPasswordField txtPass = new JPasswordField(10); formPanel.add(txtPass);
        formPanel.add(new JLabel("Role: ")); String[] roles = {"Student", "Supervisor", "Lecturer"}; 
        JComboBox<String> roleCombo = new JComboBox<>(roles); formPanel.add(roleCombo);

        JButton btnAdd = new JButton("Add New User"); formPanel.add(btnAdd);
        panel.add(formPanel, BorderLayout.NORTH);

        String[] columns = {"User ID", "Username", "Role", "Active Status"};
        usersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        usersTable = new JTable(usersTableModel);
        panel.add(new JScrollPane(usersTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnDelete = new JButton("Delete Selected User");
        JButton btnUpdateRole = new JButton("Change Role of Selected");
        actionPanel.add(btnDelete); actionPanel.add(btnUpdateRole);
        panel.add(actionPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            if(txtUser.getText().trim().isEmpty() || new String(txtPass.getPassword()).isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and Password cannot be empty."); return;
            }
            if (userDAO.addUser(txtUser.getText().trim(), new String(txtPass.getPassword()), (String) roleCombo.getSelectedItem())) {
                JOptionPane.showMessageDialog(this, "User added successfully!");
                txtUser.setText(""); txtPass.setText(""); refreshUsersTable(); loadSupervisors(); 
            } else { JOptionPane.showMessageDialog(this, "Failed to add user. Username might already exist."); }
        });

        btnDelete.addActionListener(e -> {
            int row = usersTable.getSelectedRow();
            if (row != -1) {
                if (userDAO.deleteUser((int) usersTableModel.getValueAt(row, 0))) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully."); refreshUsersTable(); loadSupervisors(); 
                } else { JOptionPane.showMessageDialog(this, "Cannot delete user: Active records exist."); }
            } else { JOptionPane.showMessageDialog(this, "Please select a user."); }
        });

        btnUpdateRole.addActionListener(e -> {
            int row = usersTable.getSelectedRow();
            if (row != -1) {
                String[] roleOptions = {"Student", "Supervisor", "Lecturer"};
                String newRole = (String) JOptionPane.showInputDialog(this, "Select new role:", "Change Role", JOptionPane.QUESTION_MESSAGE, null, roleOptions, roleOptions[0]);
                if (newRole != null && userDAO.updateUserRole((int) usersTableModel.getValueAt(row, 0), newRole)) {
                    JOptionPane.showMessageDialog(this, "Role updated."); refreshUsersTable(); loadSupervisors(); 
                }
            } else { JOptionPane.showMessageDialog(this, "Please select a user."); }
        });

        refreshUsersTable(); 
        return panel;
    }

    private void refreshUsersTable() {
        usersTableModel.setRowCount(0);
        for (User u : userDAO.getAllManageableUsers()) {
            usersTableModel.addRow(new Object[]{u.getId(), u.getUsername(), u.getRole(), u.isActive() ? "Active" : "Inactive"});
        }
    }

    // Assign Supervisor
    private JPanel createAssignmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Select a project from the table, choose a supervisor, and click Assign."), BorderLayout.NORTH);

        String[] columnNames = {"Project ID", "Student ID", "Title", "Status", "Supervisor ID"};
        assignTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        assignTable = new JTable(assignTableModel);
        panel.add(new JScrollPane(assignTable), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Assign to Supervisor: "));
        supervisorComboBox = new JComboBox<>(); loadSupervisors(); 
        controlPanel.add(supervisorComboBox);
        JButton btnAssign = new JButton("Assign Supervisor");
        btnAssign.addActionListener(e -> assignSelectedProject());
        controlPanel.add(btnAssign);
        panel.add(controlPanel, BorderLayout.SOUTH);

        refreshAssignTable(); 
        return panel;
    }

    private void loadSupervisors() {
        if (supervisorComboBox != null) {
            supervisorComboBox.removeAllItems();
            supervisorList = userDAO.getUsersByRole("Supervisor");
            for (User sup : supervisorList) { supervisorComboBox.addItem(sup.getUsername() + " (ID: " + sup.getId() + ")"); }
        }
    }

    private void refreshAssignTable() {
        if (assignTableModel != null) {
            assignTableModel.setRowCount(0); 
            for (Project p : projectDAO.getAllProjects()) {
                assignTableModel.addRow(new Object[]{p.getProjectId(), p.getStudentId(), p.getTitle(), p.getStatus(), p.getSupervisorId() == 0 ? "Unassigned" : p.getSupervisorId()});
            }
        }
    }

    private void assignSelectedProject() {
        int selectedRow = assignTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this, "Select a project first."); return; }
        int selectedSupervisorIndex = supervisorComboBox.getSelectedIndex();
        if (selectedSupervisorIndex == -1) { JOptionPane.showMessageDialog(this, "Select a supervisor."); return; }

        if (projectDAO.assignSupervisor((int) assignTableModel.getValueAt(selectedRow, 0), supervisorList.get(selectedSupervisorIndex).getId())) {
            JOptionPane.showMessageDialog(this, "Supervisor assigned!");
            refreshAssignTable(); refreshRepoTable(); 
        } else { JOptionPane.showMessageDialog(this, "Failed to assign supervisor."); }
    }

    // View Repository
    private JPanel createRepositoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("FYP Project Repository (View All System Projects)"), BorderLayout.NORTH);

        String[] columnNames = {"Project ID", "Student ID", "Title", "Status", "Grade", "Supervisor ID"};
        repoTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        repoTable = new JTable(repoTableModel);
        panel.add(new JScrollPane(repoTable), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Refresh Repository Data");
        btnRefresh.addActionListener(e -> refreshRepoTable());
        controlPanel.add(btnRefresh);
        panel.add(controlPanel, BorderLayout.SOUTH);

        refreshRepoTable();
        return panel;
    }

    private void refreshRepoTable() {
        if (repoTableModel != null) {
            repoTableModel.setRowCount(0);
            for (Project p : projectDAO.getAllProjects()) {
                repoTableModel.addRow(new Object[]{p.getProjectId(), p.getStudentId(), p.getTitle(), p.getStatus(), p.getGrade(), p.getSupervisorId() == 0 ? "Unassigned" : p.getSupervisorId()});
            }
        }
    }

    // View Lecturers
    private JPanel createDirectoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Lecturer Information Directory", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Name", "Email", "Academic Position", "Major", "Area of Interest", "Current Project"};
        DefaultTableModel dirTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } 
        };
        JTable directoryTable = new JTable(dirTableModel);
        directoryTable.setRowHeight(25);
        panel.add(new JScrollPane(directoryTable), BorderLayout.CENTER);

        for (LecturerProfile p : lecturerDAO.getAllLecturerProfiles()) {
            dirTableModel.addRow(new Object[]{p.getName(), p.getEmail(), p.getAcademicPosition(), p.getMajor(), p.getResearchInterest(), p.getCurrentProject()});
        }
        return panel;
    }
}