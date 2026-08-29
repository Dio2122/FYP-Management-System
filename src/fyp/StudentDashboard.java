package fyp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {
    private User loggedInUser;
    private ForumDAO forumDAO;
    private ProjectDAO projectDAO;
    private MessageDAO messageDAO;
    private MeetingDAO meetingDAO;
    private LecturerDAO lecturerDAO;
    private StudentDAO studentDAO; 
    private LogbookDAO logbookDAO; 

    private JComboBox<String> categorySelector;
    private JTable threadTable;
    private DefaultTableModel threadTableModel;
    private JTextArea threadDisplayArea;
    private JTextField replyField;
    private int selectedThreadId = -1;
    private String selectedThreadTitle = "";
    private String selectedThreadContent = "";

    public StudentDashboard(User user) {
        this.loggedInUser = user;
        this.forumDAO = new ForumDAO();
        this.projectDAO = new ProjectDAO();
        this.messageDAO = new MessageDAO();
        this.meetingDAO = new MeetingDAO();
        this.lecturerDAO = new LecturerDAO();
        this.studentDAO = new StudentDAO(); 
        this.logbookDAO = new LogbookDAO();
        
        setTitle("E-Supervision - Student Dashboard: " + user.getUsername());
        setSize(950, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("My Profile", createProfilePanel());
        tabbedPane.addTab("My Logbook", createLogbookPanel()); 
        tabbedPane.addTab("Schedule Meetings", createSchedulePanel());
        tabbedPane.addTab("Project Submission", createSubmissionPanel());
        tabbedPane.addTab("Forum", createForumPanel());
        tabbedPane.addTab("Messages", createMessagePanel());
        tabbedPane.addTab("Lecturer Directory", createDirectoryPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }


    private JPanel createLogbookPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new BorderLayout(5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Logbook Entry"));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Date / Week (e.g., 'Week 3'): "));
        JTextField txtDate = new JTextField(15);
        inputPanel.add(txtDate);
        formPanel.add(inputPanel, BorderLayout.NORTH);

        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.add(new JLabel(" Activity Details & Progress: "), BorderLayout.WEST);
        JTextArea txtActivity = new JTextArea(4, 40);
        txtActivity.setLineWrap(true);
        txtActivity.setWrapStyleWord(true);
        activityPanel.add(new JScrollPane(txtActivity), BorderLayout.CENTER);
        formPanel.add(activityPanel, BorderLayout.CENTER);

        JButton btnSubmit = new JButton("Save Entry");
        formPanel.add(btnSubmit, BorderLayout.SOUTH);
        panel.add(formPanel, BorderLayout.NORTH);

        String[] columns = {"Date/Week", "Activity Details", "Submitted On"};
        DefaultTableModel logModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable logTable = new JTable(logModel);
        
        JTextArea entryViewer = new JTextArea(6, 40);
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

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(logTable), new JScrollPane(entryViewer));
        splitPane.setDividerLocation(150);
        panel.add(splitPane, BorderLayout.CENTER);

        Runnable refreshLogbook = () -> {
            logModel.setRowCount(0);
            List<LogbookEntry> entries = logbookDAO.getEntriesByStudentId(loggedInUser.getId());
            for (LogbookEntry e : entries) {
                logModel.addRow(new Object[]{e.getEntryDate(), e.getActivity(), e.getCreatedAt()});
            }
            entryViewer.setText("Select an entry above to read the full details.");
        };

        btnSubmit.addActionListener(e -> {
            if (txtDate.getText().trim().isEmpty() || txtActivity.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in both Date and Activity fields.");
                return;
            }
            if (logbookDAO.addEntry(loggedInUser.getId(), txtDate.getText().trim(), txtActivity.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Logbook Entry Saved!");
                txtDate.setText("");
                txtActivity.setText("");
                refreshLogbook.run();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save entry.");
            }
        });

        refreshLogbook.run();
        return panel;
    }


    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("My Academic & FYP Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(titleLabel, BorderLayout.NORTH);
        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 10, 20));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));

        StudentProfile profile = studentDAO.getStudentProfileById(loggedInUser.getId());
        if (profile != null) {
            Font labelFont = new Font("Arial", Font.BOLD, 14); Font dataFont = new Font("Arial", Font.PLAIN, 14);
            infoPanel.add(createStyledLabel(" Name:", labelFont)); infoPanel.add(createStyledLabel(profile.getStudentName(), dataFont));
            infoPanel.add(createStyledLabel(" Programme:", labelFont)); infoPanel.add(createStyledLabel(profile.getProgramme(), dataFont));
            infoPanel.add(createStyledLabel(" CGPA:", labelFont)); infoPanel.add(createStyledLabel(String.valueOf(profile.getCgpa()), dataFont));
            infoPanel.add(createStyledLabel(" Phone Number:", labelFont)); infoPanel.add(createStyledLabel(profile.getPhoneNumber(), dataFont));
            infoPanel.add(createStyledLabel(" FYP Project Title:", labelFont)); infoPanel.add(createStyledLabel(profile.getProjectTitle(), dataFont));
            infoPanel.add(createStyledLabel(" Supervisor:", labelFont)); infoPanel.add(createStyledLabel(profile.getSupervisorName(), dataFont));
        } else { infoPanel.add(new JLabel(" Profile data not found. Please contact the Coordinator.")); }
        panel.add(infoPanel, BorderLayout.CENTER); return panel;
    }

    private JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text); label.setFont(font); return label;
    }

    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Request an Appointment Slot"));
        
        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        String[] days = new String[31]; for (int i = 0; i < 31; i++) days[i] = String.format("%02d", i + 1);
        JComboBox<String> cbDay = new JComboBox<>(days);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        JComboBox<String> cbMonth = new JComboBox<>(months);
        String[] years = {"2026", "2027", "2028"}; JComboBox<String> cbYear = new JComboBox<>(years);
        
        dateTimePanel.add(cbDay); dateTimePanel.add(cbMonth); dateTimePanel.add(cbYear);
        dateTimePanel.add(Box.createHorizontalStrut(15)); dateTimePanel.add(new JLabel("Time: "));
        String[] hours = new String[12]; for (int i = 0; i < 12; i++) hours[i] = String.format("%02d", i + 1);
        JComboBox<String> cbHour = new JComboBox<>(hours);
        String[] mins = {"00", "15", "30", "45"}; JComboBox<String> cbMin = new JComboBox<>(mins);
        String[] ampm = {"AM", "PM"}; JComboBox<String> cbAmPm = new JComboBox<>(ampm);
        dateTimePanel.add(cbHour); dateTimePanel.add(new JLabel(":")); dateTimePanel.add(cbMin); dateTimePanel.add(cbAmPm);

        formPanel.add(new JLabel("Date:")); formPanel.add(dateTimePanel);
        formPanel.add(new JLabel("  Agenda:")); JTextField txtAgenda = new JTextField(15); formPanel.add(txtAgenda);
        JButton btnRequest = new JButton("Request Slot"); formPanel.add(btnRequest);
        panel.add(formPanel, BorderLayout.NORTH);

        String[] columns = {"Meeting ID", "Supervisor", "Date & Time", "Agenda", "Status"};
        DefaultTableModel meetingModel = new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        JTable meetingTable = new JTable(meetingModel); panel.add(new JScrollPane(meetingTable), BorderLayout.CENTER);

        Runnable refreshMeetings = () -> {
            meetingModel.setRowCount(0);
            for (Meeting m : meetingDAO.getUserMeetings(loggedInUser.getId(), "student_id")) {
                meetingModel.addRow(new Object[]{m.getMeetingId(), m.getSupervisorName(), m.getMeetingTime(), m.getAgenda(), m.getStatus()});
            }
        };

        btnRequest.addActionListener(e -> {
            Project myProject = projectDAO.getStudentProject(loggedInUser.getId());
            if (myProject == null || myProject.getSupervisorId() == 0) { JOptionPane.showMessageDialog(this, "You must have an assigned supervisor before requesting a meeting."); return; }
            if (txtAgenda.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Please fill in the Agenda."); return; }
            String formattedDateTime = cbDay.getSelectedItem() + " " + cbMonth.getSelectedItem() + " " + cbYear.getSelectedItem() + " at " + cbHour.getSelectedItem() + ":" + cbMin.getSelectedItem() + " " + cbAmPm.getSelectedItem();
            
            if (meetingDAO.createMeeting(myProject.getSupervisorId(), loggedInUser.getId(), formattedDateTime, txtAgenda.getText(), "Requested")) {
                JOptionPane.showMessageDialog(this, "Appointment requested for " + formattedDateTime + "! Waiting for supervisor approval.");
                txtAgenda.setText(""); refreshMeetings.run();
            } else { JOptionPane.showMessageDialog(this, "Failed to request meeting."); }
        });
        refreshMeetings.run(); return panel;
    }

    private JPanel createSubmissionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JLabel lblStatus = new JLabel("Current Status: Checking..."); lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Project Title: ")); JTextField txtTitle = new JTextField(30); inputPanel.add(txtTitle);
        JButton btnSubmit = new JButton("Submit Project Proposal");
        formPanel.add(lblStatus); formPanel.add(inputPanel); formPanel.add(btnSubmit); panel.add(formPanel, BorderLayout.NORTH);

        Project existingProject = projectDAO.getStudentProject(loggedInUser.getId());
        if (existingProject != null) {
            lblStatus.setText("Current Status: " + existingProject.getStatus() + " | Title: " + existingProject.getTitle());
            txtTitle.setEnabled(false); btnSubmit.setEnabled(false); btnSubmit.setText("Project Already Submitted");
        } else { lblStatus.setText("Current Status: No Project Submitted"); }

        btnSubmit.addActionListener(e -> {
            String title = txtTitle.getText().trim();
            if (title.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter a project title."); return; }
            if (projectDAO.submitProject(loggedInUser.getId(), title)) {
                JOptionPane.showMessageDialog(this, "Project submitted successfully!");
                dispose(); new StudentDashboard(loggedInUser).setVisible(true);
            } else { JOptionPane.showMessageDialog(this, "Submission failed. Database error or project already exists."); }
        });
        return panel;
    }

    private JPanel createForumPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Category: ")); String[] categories = {"FYP1", "FYP2"};
        categorySelector = new JComboBox<>(categories); topPanel.add(categorySelector);
        JButton btnNewThread = new JButton("Create New Thread"); topPanel.add(btnNewThread); panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Title", "Author", "Date"};
        threadTableModel = new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        threadTable = new JTable(threadTableModel); threadTable.getColumnModel().getColumn(0).setMaxWidth(50);
        JScrollPane scrollTable = new JScrollPane(threadTable); scrollTable.setBorder(BorderFactory.createTitledBorder("Active Threads"));

        JPanel viewerPanel = new JPanel(new BorderLayout(5, 5)); viewerPanel.setBorder(BorderFactory.createTitledBorder("Discussion"));
        threadDisplayArea = new JTextArea(); threadDisplayArea.setEditable(false); threadDisplayArea.setLineWrap(true); threadDisplayArea.setWrapStyleWord(true);
        viewerPanel.add(new JScrollPane(threadDisplayArea), BorderLayout.CENTER);

        JPanel replyPanel = new JPanel(new BorderLayout(5, 5));
        replyField = new JTextField(); replyField.setEnabled(false); 
        JButton btnReply = new JButton("Post Reply"); btnReply.setEnabled(false);
        replyPanel.add(new JLabel(" Reply: "), BorderLayout.WEST); replyPanel.add(replyField, BorderLayout.CENTER); replyPanel.add(btnReply, BorderLayout.EAST);
        viewerPanel.add(replyPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTable, viewerPanel);
        splitPane.setDividerLocation(150); panel.add(splitPane, BorderLayout.CENTER);

        Runnable refreshThreads = () -> {
            threadTableModel.setRowCount(0);
            for (ForumThread t : forumDAO.getThreadsByCategory((String) categorySelector.getSelectedItem())) { threadTableModel.addRow(new Object[]{t.getThreadId(), t.getTitle(), t.getAuthorName(), t.getCreatedAt()}); }
            threadDisplayArea.setText("Select a thread above to view the discussion.");
            replyField.setEnabled(false); btnReply.setEnabled(false); selectedThreadId = -1;
        };

        Runnable loadDiscussion = () -> {
            if (selectedThreadId == -1) return;
            threadDisplayArea.setText("=== " + selectedThreadTitle.toUpperCase() + " ===\nOriginal Post:\n" + selectedThreadContent + "\n\n--- Replies ---\n");
            List<ForumReply> replies = forumDAO.getRepliesForThread(selectedThreadId);
            if (replies.isEmpty()) { threadDisplayArea.append("No replies yet. Be the first!\n"); } else {
                for (ForumReply r : replies) { threadDisplayArea.append("[" + r.getCreatedAt() + "] " + r.getAuthorName() + ": " + r.getContent() + "\n"); }
            }
        };

        categorySelector.addActionListener(e -> refreshThreads.run());
        threadTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && threadTable.getSelectedRow() != -1) {
                int row = threadTable.getSelectedRow();
                selectedThreadId = (int) threadTableModel.getValueAt(row, 0); selectedThreadTitle = (String) threadTableModel.getValueAt(row, 1);
                for (ForumThread t : forumDAO.getThreadsByCategory((String) categorySelector.getSelectedItem())) { if (t.getThreadId() == selectedThreadId) selectedThreadContent = t.getContent(); }
                replyField.setEnabled(true); btnReply.setEnabled(true); loadDiscussion.run();
            }
        });

        btnNewThread.addActionListener(e -> {
            JTextField titleField = new JTextField(); JTextArea contentField = new JTextArea(5, 20); contentField.setLineWrap(true); contentField.setWrapStyleWord(true);
            Object[] message = { "Thread Title:", titleField, "Your Question/Post:", new JScrollPane(contentField) };
            int option = JOptionPane.showConfirmDialog(this, message, "Create New Thread", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION && !titleField.getText().trim().isEmpty() && !contentField.getText().trim().isEmpty()) {
                if (forumDAO.createThread(loggedInUser.getId(), (String) categorySelector.getSelectedItem(), titleField.getText().trim(), contentField.getText().trim())) { refreshThreads.run(); }
            }
        });

        btnReply.addActionListener(e -> {
            if (selectedThreadId != -1 && !replyField.getText().trim().isEmpty()) {
                if (forumDAO.addReply(selectedThreadId, loggedInUser.getId(), replyField.getText().trim())) { replyField.setText(""); loadDiscussion.run(); }
            }
        });

        refreshThreads.run(); return panel;
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