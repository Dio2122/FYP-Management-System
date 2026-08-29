package fyp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SupervisorDashboard extends JFrame {
    private User loggedInUser;
    private ProjectDAO projectDAO;
    private MessageDAO messageDAO;
    private MeetingDAO meetingDAO;
    private LecturerDAO lecturerDAO;
    private StudentDAO studentDAO; 
    private LogbookDAO logbookDAO;

    private JTable projectTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusComboBox;
    private JComboBox<String> gradeComboBox;

    private JTable studentSearchTable;
    private DefaultTableModel searchTableModel;
    private JTextField txtSearch;

    public SupervisorDashboard(User user) {
        this.loggedInUser = user;
        this.projectDAO = new ProjectDAO();
        this.messageDAO = new MessageDAO();
        this.meetingDAO = new MeetingDAO();
        this.lecturerDAO = new LecturerDAO();
        this.studentDAO = new StudentDAO(); 
        this.logbookDAO = new LogbookDAO();
        
        setTitle("E-Supervision - Supervisor Dashboard: " + user.getUsername());
        setSize(950, 650); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Student Search & Logbooks", createStudentSearchPanel()); 
        tabbedPane.addTab("Evaluate & Archive Projects", createEvaluationPanel());
        tabbedPane.addTab("Schedule Meetings", createSchedulePanel());
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
        
        JButton btnViewLogbook = new JButton("View Selected Student's Logbook");
        searchPanel.add(btnViewLogbook);
        
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

        btnViewLogbook.addActionListener(e -> {
            int row = studentSearchTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a student from the table first.");
                return;
            }
            
            String studentName = (String) searchTableModel.getValueAt(row, 0);
            List<LogbookEntry> entries = logbookDAO.getEntriesByStudentName(studentName);
            
            JDialog dialog = new JDialog(this, "Logbook: " + studentName, true);
            dialog.setSize(750, 500); // Made slightly larger
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout(10, 10));

            String[] logCols = {"Date/Week", "Progress Summary", "Submitted On"};
            DefaultTableModel logModel = new DefaultTableModel(logCols, 0) {
                @Override
                public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable logTable = new JTable(logModel);
            
            JTextArea entryViewer = new JTextArea(8, 40);
            entryViewer.setEditable(false);
            entryViewer.setLineWrap(true);
            entryViewer.setWrapStyleWord(true);
            entryViewer.setBorder(BorderFactory.createTitledBorder("Full Entry Details"));

            logTable.getSelectionModel().addListSelectionListener(event -> {
                if (!event.getValueIsAdjusting() && logTable.getSelectedRow() != -1) {
                    String fullText = (String) logModel.getValueAt(logTable.getSelectedRow(), 1);
                    entryViewer.setText(fullText);
                    entryViewer.setCaretPosition(0); 
                }
            });
            
            if (entries.isEmpty()) {
                logModel.addRow(new Object[]{"N/A", "This student has no logbook entries yet.", "N/A"});
            } else {
                for (LogbookEntry entry : entries) {
                    logModel.addRow(new Object[]{entry.getEntryDate(), entry.getActivity(), entry.getCreatedAt()});
                }
            }
            
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(logTable), new JScrollPane(entryViewer));
            splitPane.setDividerLocation(200);
            dialog.add(splitPane, BorderLayout.CENTER);
            
            dialog.setVisible(true);
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


    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); formPanel.setBorder(BorderFactory.createTitledBorder("Schedule New Meeting / Approve Request"));
        formPanel.add(new JLabel("Student:")); JComboBox<String> studentCombo = new JComboBox<>();
        List<Project> myStudents = projectDAO.getProjectsBySupervisor(loggedInUser.getId());
        for (Project p : myStudents) { studentCombo.addItem("Student ID: " + p.getStudentId()); } formPanel.add(studentCombo);
        
        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        String[] days = new String[31]; for (int i = 0; i < 31; i++) days[i] = String.format("%02d", i + 1); JComboBox<String> cbDay = new JComboBox<>(days);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}; JComboBox<String> cbMonth = new JComboBox<>(months);
        String[] years = {"2026", "2027", "2028"}; JComboBox<String> cbYear = new JComboBox<>(years);
        dateTimePanel.add(cbDay); dateTimePanel.add(cbMonth); dateTimePanel.add(cbYear); dateTimePanel.add(Box.createHorizontalStrut(15)); dateTimePanel.add(new JLabel("Time: "));
        String[] hours = new String[12]; for (int i = 0; i < 12; i++) hours[i] = String.format("%02d", i + 1); JComboBox<String> cbHour = new JComboBox<>(hours);
        String[] mins = {"00", "15", "30", "45"}; JComboBox<String> cbMin = new JComboBox<>(mins); String[] ampm = {"AM", "PM"}; JComboBox<String> cbAmPm = new JComboBox<>(ampm);
        dateTimePanel.add(cbHour); dateTimePanel.add(new JLabel(":")); dateTimePanel.add(cbMin); dateTimePanel.add(Box.createHorizontalStrut(5)); dateTimePanel.add(cbAmPm);

        formPanel.add(new JLabel("  Date:")); formPanel.add(dateTimePanel);
        formPanel.add(new JLabel("  Agenda:")); JTextField txtAgenda = new JTextField(12); formPanel.add(txtAgenda);
        JButton btnSchedule = new JButton("Schedule"); formPanel.add(btnSchedule); panel.add(formPanel, BorderLayout.NORTH);

        String[] columns = {"Meeting ID", "Student", "Date & Time", "Agenda", "Status"};
        DefaultTableModel meetingModel = new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        JTable meetingTable = new JTable(meetingModel); panel.add(new JScrollPane(meetingTable), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); String[] statuses = {"Scheduled", "Completed", "Cancelled"}; JComboBox<String> statusCombo = new JComboBox<>(statuses);
        controlPanel.add(new JLabel("Change Status of Selected Meeting:")); controlPanel.add(statusCombo);
        JButton btnUpdateStatus = new JButton("Update Status"); controlPanel.add(btnUpdateStatus); panel.add(controlPanel, BorderLayout.SOUTH);

        Runnable refreshMeetings = () -> {
            meetingModel.setRowCount(0);
            for (Meeting m : meetingDAO.getUserMeetings(loggedInUser.getId(), "supervisor_id")) {
                meetingModel.addRow(new Object[]{m.getMeetingId(), m.getStudentName(), m.getMeetingTime(), m.getAgenda(), m.getStatus()});
            }
        };

        btnSchedule.addActionListener(e -> {
            if (studentCombo.getSelectedIndex() == -1) return;
            int studentId = myStudents.get(studentCombo.getSelectedIndex()).getStudentId();
            if (txtAgenda.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Please fill in the Agenda."); return; }
            String formattedDateTime = cbDay.getSelectedItem() + " " + cbMonth.getSelectedItem() + " " + cbYear.getSelectedItem() + " at " + cbHour.getSelectedItem() + ":" + cbMin.getSelectedItem() + " " + cbAmPm.getSelectedItem();
            if (meetingDAO.createMeeting(loggedInUser.getId(), studentId, formattedDateTime, txtAgenda.getText(), "Scheduled")) {
                JOptionPane.showMessageDialog(this, "Meeting Scheduled for " + formattedDateTime + "!");
                txtAgenda.setText(""); refreshMeetings.run();
            }
        });

        btnUpdateStatus.addActionListener(e -> {
            int row = meetingTable.getSelectedRow();
            if (row != -1) {
                if (meetingDAO.updateMeetingStatus((int) meetingModel.getValueAt(row, 0), (String) statusCombo.getSelectedItem())) { refreshMeetings.run(); }
            } else { JOptionPane.showMessageDialog(this, "Select a meeting from the table first."); }
        });

        refreshMeetings.run(); return panel;
    }

    private JPanel createEvaluationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Select a student's project below to update their status or assign a grade."), BorderLayout.NORTH);
        String[] columnNames = {"Project ID", "Student ID", "Title", "Status", "Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        projectTable = new JTable(tableModel); panel.add(new JScrollPane(projectTable), BorderLayout.CENTER);
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.add(new JLabel("Update Status: ")); String[] statuses = {"In Progress", "Completed", "Archived"}; statusComboBox = new JComboBox<>(statuses); controlPanel.add(statusComboBox);
        controlPanel.add(new JLabel("Assign Grade: ")); String[] grades = {"N/A", "A", "A-", "B+", "B", "C", "F"}; gradeComboBox = new JComboBox<>(grades); controlPanel.add(gradeComboBox);
        JButton btnUpdate = new JButton("Save Evaluation"); btnUpdate.addActionListener(e -> updateSelectedProject()); controlPanel.add(btnUpdate);
        panel.add(controlPanel, BorderLayout.SOUTH); refreshTable(); return panel;
    }

    private void refreshTable() {
        tableModel.setRowCount(0); 
        for (Project p : projectDAO.getProjectsBySupervisor(loggedInUser.getId())) { tableModel.addRow(new Object[]{p.getProjectId(), p.getStudentId(), p.getTitle(), p.getStatus(), p.getGrade()}); }
    }

    private void updateSelectedProject() {
        int selectedRow = projectTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this, "Please select a project from the table first."); return; }
        if (projectDAO.evaluateProject((int) tableModel.getValueAt(selectedRow, 0), (String) statusComboBox.getSelectedItem(), (String) gradeComboBox.getSelectedItem())) {
            JOptionPane.showMessageDialog(this, "Project updated successfully!"); refreshTable(); 
        } else { JOptionPane.showMessageDialog(this, "Failed to update project."); }
    }

    private JPanel createMessagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel composePanel = new JPanel(new BorderLayout(5, 5)); composePanel.setBorder(BorderFactory.createTitledBorder("Compose Message"));
        JPanel headerPanel = new JPanel(new GridLayout(2, 2, 5, 5)); headerPanel.add(new JLabel("To:"));
        JComboBox<String> contactCombo = new JComboBox<>(); List<User> contacts = messageDAO.getMessageContacts(loggedInUser.getId());
        for (User u : contacts) { contactCombo.addItem(u.getUsername() + " (" + u.getRole() + ") - ID:" + u.getId()); }
        headerPanel.add(contactCombo); headerPanel.add(new JLabel("Subject:")); JTextField txtSubject = new JTextField(); headerPanel.add(txtSubject); composePanel.add(headerPanel, BorderLayout.NORTH);
        JTextArea txtBody = new JTextArea(4, 20); composePanel.add(new JScrollPane(txtBody), BorderLayout.CENTER);
        JButton btnSend = new JButton("Send Message"); composePanel.add(btnSend, BorderLayout.SOUTH);
        JPanel inboxPanel = new JPanel(new BorderLayout()); inboxPanel.setBorder(BorderFactory.createTitledBorder("My Inbox"));
        JTextArea txtInbox = new JTextArea(); txtInbox.setEditable(false); inboxPanel.add(new JScrollPane(txtInbox), BorderLayout.CENTER);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, composePanel, inboxPanel); splitPane.setDividerLocation(200); panel.add(splitPane, BorderLayout.CENTER);

        Runnable refreshInbox = () -> {
            txtInbox.setText("");
            for (Message m : messageDAO.getInbox(loggedInUser.getId())) { txtInbox.append("From: " + m.getSenderName() + " | Date: " + m.getSentDate() + "\nSubject: " + m.getSubject() + "\n" + m.getBody() + "\n---------------------------------------------------\n"); }
        };

        btnSend.addActionListener(e -> {
            if (contactCombo.getSelectedIndex() != -1) {
                if (messageDAO.sendMessage(loggedInUser.getId(), contacts.get(contactCombo.getSelectedIndex()).getId(), txtSubject.getText(), txtBody.getText())) {
                    JOptionPane.showMessageDialog(this, "Message Sent!"); txtSubject.setText(""); txtBody.setText(""); refreshInbox.run();
                }
            }
        });
        refreshInbox.run(); JButton btnRefresh = new JButton("Refresh Inbox"); btnRefresh.addActionListener(e -> refreshInbox.run()); inboxPanel.add(btnRefresh, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createDirectoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Lecturer Information Directory", SwingConstants.CENTER); titleLabel.setFont(new Font("Arial", Font.BOLD, 16)); panel.add(titleLabel, BorderLayout.NORTH);
        String[] columnNames = {"Name", "Email", "Academic Position", "Major", "Area of Interest", "Current Project"};
        DefaultTableModel dirTableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        JTable directoryTable = new JTable(dirTableModel); directoryTable.setRowHeight(25); panel.add(new JScrollPane(directoryTable), BorderLayout.CENTER);
        for (LecturerProfile p : lecturerDAO.getAllLecturerProfiles()) { dirTableModel.addRow(new Object[]{p.getName(), p.getEmail(), p.getAcademicPosition(), p.getMajor(), p.getResearchInterest(), p.getCurrentProject()}); }
        return panel;
    }
}