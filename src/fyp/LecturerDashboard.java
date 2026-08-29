package fyp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LecturerDashboard extends JFrame {
    private User loggedInUser;
    private StudentDAO studentDAO;
    private MessageDAO messageDAO;
    private LecturerDAO lecturerDAO;

    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    public LecturerDashboard(User user) {
        this.loggedInUser = user;
        this.studentDAO = new StudentDAO();
        this.messageDAO = new MessageDAO();
        this.lecturerDAO = new LecturerDAO();

        setTitle("E-Supervision - Lecturer Dashboard: " + user.getUsername());
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Student Information Search", createStudentSearchPanel());
        tabbedPane.addTab("Messages", createMessagePanel());
        tabbedPane.addTab("Lecturer Directory", createDirectoryPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

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
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(25);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        btnSearch.addActionListener(e -> loadStudentData(txtSearch.getText().trim()));
        
        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            loadStudentData(""); 
        });

        loadStudentData("");

        return panel;
    }

    private void loadStudentData(String keyword) {
        tableModel.setRowCount(0);
        // Passes the user ID to trigger the sorting logic
        List<StudentProfile> profiles = studentDAO.searchStudents(keyword, loggedInUser.getId());
        
        if (profiles.isEmpty() && !keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students found matching: " + keyword);
        }

        for (StudentProfile sp : profiles) {
            Object[] row = {
                sp.getStudentName(), sp.getProgramme(), sp.getCgpa(),
                sp.getPhoneNumber(), sp.getProjectTitle(), sp.getSupervisorName()
            };
            tableModel.addRow(row);
        }
    }

    private JPanel createMessagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel composePanel = new JPanel(new BorderLayout(5, 5));
        composePanel.setBorder(BorderFactory.createTitledBorder("Compose Message"));
        
        JPanel headerPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        headerPanel.add(new JLabel("To:"));
        
        JComboBox<String> contactCombo = new JComboBox<>();
        List<User> contacts = messageDAO.getMessageContacts(loggedInUser.getId());
        for (User u : contacts) {
            contactCombo.addItem(u.getUsername() + " (" + u.getRole() + ") - ID:" + u.getId());
        }
        headerPanel.add(contactCombo);
        
        headerPanel.add(new JLabel("Subject:"));
        JTextField txtSubject = new JTextField();
        headerPanel.add(txtSubject);
        composePanel.add(headerPanel, BorderLayout.NORTH);
        
        JTextArea txtBody = new JTextArea(4, 20);
        composePanel.add(new JScrollPane(txtBody), BorderLayout.CENTER);
        
        JButton btnSend = new JButton("Send Message");
        composePanel.add(btnSend, BorderLayout.SOUTH);

        JPanel inboxPanel = new JPanel(new BorderLayout());
        inboxPanel.setBorder(BorderFactory.createTitledBorder("My Inbox"));
        JTextArea txtInbox = new JTextArea();
        txtInbox.setEditable(false);
        inboxPanel.add(new JScrollPane(txtInbox), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, composePanel, inboxPanel);
        splitPane.setDividerLocation(200);
        panel.add(splitPane, BorderLayout.CENTER);

        Runnable refreshInbox = () -> {
            txtInbox.setText("");
            List<Message> messages = messageDAO.getInbox(loggedInUser.getId());
            if (messages.isEmpty()) {
                txtInbox.append("Your inbox is empty.");
            } else {
                for (Message m : messages) {
                    txtInbox.append("From: " + m.getSenderName() + " | Date: " + m.getSentDate() + "\n");
                    txtInbox.append("Subject: " + m.getSubject() + "\n");
                    txtInbox.append(m.getBody() + "\n");
                    txtInbox.append("---------------------------------------------------\n");
                }
            }
        };

        btnSend.addActionListener(e -> {
            int selectedIndex = contactCombo.getSelectedIndex();
            if (selectedIndex != -1) {
                User receiver = contacts.get(selectedIndex);
                if (messageDAO.sendMessage(loggedInUser.getId(), receiver.getId(), txtSubject.getText(), txtBody.getText())) {
                    JOptionPane.showMessageDialog(this, "Message Sent!");
                    txtSubject.setText(""); txtBody.setText("");
                    refreshInbox.run();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to send message.");
                }
            }
        });

        refreshInbox.run();
        JButton btnRefresh = new JButton("Refresh Inbox");
        btnRefresh.addActionListener(e -> refreshInbox.run());
        inboxPanel.add(btnRefresh, BorderLayout.SOUTH);

        return panel;
    }

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

        List<LecturerProfile> profiles = lecturerDAO.getAllLecturerProfiles();
        for (LecturerProfile p : profiles) {
            Object[] row = {
                p.getName(), p.getEmail(), p.getAcademicPosition(),
                p.getMajor(), p.getResearchInterest(), p.getCurrentProject()
            };
            dirTableModel.addRow(row);
        }
        return panel;
    }
}