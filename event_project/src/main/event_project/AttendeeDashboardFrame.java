package com.mycompany.event_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AttendeeDashboardFrame extends JFrame {

    private int attendeeId;
    private JTable eventsTable, ticketsTable;
    private JButton registerBtn, cancelBtn, refreshEventsBtn, refreshTicketsBtn, viewTicketBtn, logoutBtn;
    
    // متغيرات الفلترة والبحث
    private JComboBox<String> categoryFilter;
    private JComboBox<String> locationFilter;
    private JTextField dateFromField, dateToField;
    private JButton applyFilterBtn;

    public AttendeeDashboardFrame(int attendeeId) {
        this.attendeeId = attendeeId;

        setTitle("Attendee Dashboard");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // --- 1. تلوين خلفية النافذة (اللون البنفسجي) ---
        getContentPane().setBackground(Color.decode("#debee6"));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(Color.decode("#debee6"));
        tabs.setFont(new Font("Tahoma", Font.ITALIC, 12));

        // ==========================================
        // TAB 1: AVAILABLE EVENTS (الأحداث + البحث)
        // ==========================================
        JPanel eventsPanel = new JPanel(new BorderLayout());
        eventsPanel.setBackground(Color.decode("#debee6"));

        // --- أ) لوحة الفلترة (في الأعلى) ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.decode("#debee6"));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));

        categoryFilter = new JComboBox<>();
        locationFilter = new JComboBox<>();
        dateFromField = new JTextField(8);
        dateToField = new JTextField(8);
        applyFilterBtn = new JButton("Apply Filter");
        styleButton(applyFilterBtn); // تنسيق الزر

        // تعبئة القوائم من قاعدة البيانات
        loadFilterValues();

        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryFilter);
        filterPanel.add(new JLabel("Location:"));
        filterPanel.add(locationFilter);
        filterPanel.add(new JLabel("From:"));
        filterPanel.add(dateFromField);
        filterPanel.add(new JLabel("To:"));
        filterPanel.add(dateToField);
        filterPanel.add(applyFilterBtn);

        eventsPanel.add(filterPanel, BorderLayout.NORTH);

        // --- ب) الجدول (في الوسط) ---
        eventsTable = new JTable();
        eventsTable.setBackground(Color.decode("#f1ebf2")); // لون الجدول فاتح
        eventsTable.setFont(new Font("Tahoma", Font.ITALIC, 12));
        eventsTable.getTableHeader().setBackground(Color.decode("#debee6")); // لون الهيدر
        eventsTable.getTableHeader().setFont(new Font("Tahoma", Font.ITALIC, 12));
        JScrollPane eventScroll = new JScrollPane(eventsTable);
        eventsPanel.add(eventScroll, BorderLayout.CENTER);

        // --- ج) الأزرار (في الأسفل) ---
        JPanel eventBtns = new JPanel();
        eventBtns.setBackground(Color.decode("#debee6"));
        registerBtn = new JButton("Register");
        styleButton(registerBtn);
        refreshEventsBtn = new JButton("Refresh");
        styleButton(refreshEventsBtn);
        
        eventBtns.add(registerBtn);
        eventBtns.add(refreshEventsBtn);
        eventsPanel.add(eventBtns, BorderLayout.SOUTH);

        tabs.add("Available Events", eventsPanel);

        // ==========================================
        // TAB 2: MY TICKETS (تذاكري)
        // ==========================================
        JPanel ticketsPanel = new JPanel(new BorderLayout());
        ticketsPanel.setBackground(Color.decode("#debee6"));

        ticketsTable = new JTable();
        ticketsTable.setBackground(Color.decode("#f1ebf2"));
        ticketsTable.setFont(new Font("Tahoma", Font.ITALIC, 12));
        ticketsTable.getTableHeader().setBackground(Color.decode("#debee6"));
        ticketsTable.getTableHeader().setFont(new Font("Tahoma", Font.ITALIC, 12));
        JScrollPane ticketsScroll = new JScrollPane(ticketsTable);

        JPanel ticketBtns = new JPanel();
        ticketBtns.setBackground(Color.decode("#debee6"));
        
        cancelBtn = new JButton("Cancel Registration");
        styleButton(cancelBtn);
        refreshTicketsBtn = new JButton("Refresh");
        styleButton(refreshTicketsBtn);
        viewTicketBtn = new JButton("View Ticket");
        styleButton(viewTicketBtn);

        ticketBtns.add(cancelBtn);
        ticketBtns.add(refreshTicketsBtn);
        ticketBtns.add(viewTicketBtn);

        ticketsPanel.add(ticketsScroll, BorderLayout.CENTER);
        ticketsPanel.add(ticketBtns, BorderLayout.SOUTH);

        tabs.add("My Tickets", ticketsPanel);

        // ==========================================
        // زر الخروج وترتيب النافذة
        // ==========================================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.decode("#debee6"));
        
        logoutBtn = new JButton("Logout");
        styleButton(logoutBtn);
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(Color.decode("#debee6"));
        logoutPanel.add(logoutBtn);
        
        topPanel.add(logoutPanel, BorderLayout.NORTH);
        topPanel.add(tabs, BorderLayout.CENTER);

        add(topPanel);

        // --- الأكشن (برمجة الأزرار) ---
        registerBtn.addActionListener(e -> registerForEvent());
        cancelBtn.addActionListener(e -> cancelRegistration());
        refreshEventsBtn.addActionListener(e -> loadEvents());
        refreshTicketsBtn.addActionListener(e -> loadTickets());
        viewTicketBtn.addActionListener(e -> showTicketDetails());
        logoutBtn.addActionListener(e -> logout());
        
        // تفعيل زر الفلترة
        applyFilterBtn.addActionListener(e -> loadEvents());

        // تحميل البيانات عند الفتح
        loadEvents();
        loadTickets();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- دالة لتنسيق الأزرار بشكل موحد ---
    private void styleButton(JButton btn) {
        btn.setBackground(Color.decode("#f1ebf2"));
        btn.setFont(new Font("Tahoma", Font.ITALIC, 12));
    }

    // --- دالة الخروج ---
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new Log_sginup(); // العودة لشاشة الدخول
            dispose();
        }
    }

    // --- تحميل خيارات الفلترة في القوائم ---
    private void loadFilterValues() {
        categoryFilter.addItem("All");
        locationFilter.addItem("All");
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            ResultSet rs1 = st.executeQuery("SELECT DISTINCT category FROM events");
            while (rs1.next()) categoryFilter.addItem(rs1.getString(1));
            ResultSet rs2 = st.executeQuery("SELECT DISTINCT location FROM events");
            while (rs2.next()) locationFilter.addItem(rs2.getString(1));
        } catch (Exception ex) { 
            // Ignored just for loading UI
        }
    }

    // --- تحميل الأحداث (مع منطق البحث) ---
    private void loadEvents() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Title", "Category", "Location", "Date", "Capacity", "Registered"}, 0
        );

        String sql = "SELECT e.event_id, e.title, e.category, e.location, e.event_date, " +
                     "       e.seat_capacity, " +
                     "       (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.event_id) AS registeredCount " +
                     "FROM events e WHERE 1=1 ";

        // تطبيق الفلترة
        if (categoryFilter.getSelectedItem() != null && !categoryFilter.getSelectedItem().toString().equals("All")) {
            sql += " AND e.category = '" + categoryFilter.getSelectedItem() + "'";
        }
        if (locationFilter.getSelectedItem() != null && !locationFilter.getSelectedItem().toString().equals("All")) {
            sql += " AND e.location = '" + locationFilter.getSelectedItem() + "'";
        }
        if (!dateFromField.getText().trim().isEmpty()) {
            sql += " AND e.event_date >= '" + dateFromField.getText().trim() + " 00:00:00'";
        }
        if (!dateToField.getText().trim().isEmpty()) {
            sql += " AND e.event_date <= '" + dateToField.getText().trim() + " 23:59:59'";
        }
        sql += " ORDER BY e.event_date ASC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("event_id"), rs.getString("title"), rs.getString("category"),
                        rs.getString("location"), rs.getString("event_date"),
                        rs.getInt("seat_capacity"), rs.getInt("registeredCount")
                });
            }
            eventsTable.setModel(model);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading events: " + ex.getMessage());
        }
    }

    // --- التسجيل في حدث ---
    private void registerForEvent() {
        int row = eventsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select an event first!", "Warning", JOptionPane.WARNING_MESSAGE); return; }

        int eventId = (int) eventsTable.getValueAt(row, 0);
        int capacity = (int) eventsTable.getValueAt(row, 5);
        int registered = (int) eventsTable.getValueAt(row, 6);

        if (registered >= capacity) { JOptionPane.showMessageDialog(this, "Sorry, this event is FULL.", "Error", JOptionPane.ERROR_MESSAGE); return; }

        try (Connection con = DBConnection.getConnection()) {
            // التحقق من التكرار
            PreparedStatement chk = con.prepareStatement("SELECT * FROM registrations WHERE user_id=? AND event_id=?");
            chk.setInt(1, attendeeId); chk.setInt(2, eventId);
            if (chk.executeQuery().next()) { JOptionPane.showMessageDialog(this, "You are already registered!", "Duplicate", JOptionPane.WARNING_MESSAGE); return; }

            int confirm = JOptionPane.showConfirmDialog(this, "Register for this event?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            // إضافة التسجيل
            PreparedStatement regPs = con.prepareStatement("INSERT INTO registrations (event_id, user_id, status) VALUES (?, ?, 'REGISTERED')", Statement.RETURN_GENERATED_KEYS);
            regPs.setInt(1, eventId); regPs.setInt(2, attendeeId);
            regPs.executeUpdate();

            ResultSet keys = regPs.getGeneratedKeys();
            keys.next();
            int regId = keys.getInt(1);

            // إنشاء تذكرة
            String ticketCode = "TCK-" + regId + "-" + System.currentTimeMillis();
            PreparedStatement tPs = con.prepareStatement("INSERT INTO tickets (registration_id, ticket_code, status) VALUES (?, ?, 'ACTIVE')");
            tPs.setInt(1, regId); tPs.setString(2, ticketCode);
            tPs.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registered Successfully!\nTicket: " + ticketCode);
            loadEvents();
            loadTickets();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // --- عرض تذاكر المستخدم ---
    private void loadTickets() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Registration ID", "Event Title", "Date", "Status"}, 0);
        String sql = "SELECT r.registration_id, e.title, e.event_date, r.status FROM registrations r JOIN events e ON r.event_id = e.event_id WHERE r.user_id = ? ORDER BY e.event_date";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, attendeeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            ticketsTable.setModel(model);
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    // --- إلغاء التسجيل ---
    private void cancelRegistration() {
        int row = ticketsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a ticket to cancel!"); return; }
        
        int regId = (int) ticketsTable.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Cancel this registration?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM registrations WHERE registration_id = ?")) {
            ps.setInt(1, regId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration Canceled.");
            loadTickets();
            loadEvents();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    // --- عرض تفاصيل التذكرة (النافذة المنبثقة) ---
    private void showTicketDetails() {
        int row = ticketsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a ticket first!"); return; }
        
        int regId = (int) ticketsTable.getValueAt(row, 0);
        String sql = "SELECT r.registration_id, e.title, e.location, e.category, e.event_date, t.ticket_code, t.status " +
                     "FROM registrations r JOIN events e ON r.event_id = e.event_id JOIN tickets t ON r.registration_id = t.registration_id WHERE r.registration_id = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, regId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, 
                    "------ EVENT TICKET ------\n\n" +
                    "Event: " + rs.getString("title") + "\n" +
                    "Category: " + rs.getString("category") + "\n" +
                    "Location: " + rs.getString("location") + "\n" +
                    "Date: " + rs.getString("event_date") + "\n\n" +
                    "Ticket ID:\n" + rs.getString("ticket_code") + "\n\n" +
                    "Status: " + rs.getString("status"), 
                    "Ticket Details", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }
}
