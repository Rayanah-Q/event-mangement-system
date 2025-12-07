package com.mycompany.event_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class OrganizerDashboardFrame extends JFrame {

    private JTable eventTable, attendeeTable;
    private JButton refreshEventsBtn, addEventBtn, editEventBtn, deleteEventBtn, loadAttendeesBtn;

    private int organizerId;  // نجيبه من LoginFrame

    public OrganizerDashboardFrame(int organizerId) {

        this.organizerId = organizerId;

        setTitle("Organizer Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        // -------------------------
        // EVENTS TAB
        // -------------------------
        JPanel eventsPanel = new JPanel(new BorderLayout());

        eventTable = new JTable();
        JScrollPane eventScroll = new JScrollPane(eventTable);

        JPanel eventBtns = new JPanel();
        refreshEventsBtn = new JButton("Refresh Events");
        addEventBtn = new JButton("Add Event");
        editEventBtn = new JButton("Edit Event");
        deleteEventBtn = new JButton("Delete Event");

        eventBtns.add(refreshEventsBtn);
        eventBtns.add(addEventBtn);
        eventBtns.add(editEventBtn);
        eventBtns.add(deleteEventBtn);

        eventsPanel.add(eventScroll, BorderLayout.CENTER);
        eventsPanel.add(eventBtns, BorderLayout.SOUTH);

        tabs.add("My Events", eventsPanel);

        // -------------------------
        // ATTENDEES TAB
        // -------------------------
        JPanel attendeesPanel = new JPanel(new BorderLayout());

        attendeeTable = new JTable();
        JScrollPane attendeeScroll = new JScrollPane(attendeeTable);

        JPanel attendeeBtns = new JPanel();
        loadAttendeesBtn = new JButton("Load Attendees");
        attendeeBtns.add(loadAttendeesBtn);

        attendeesPanel.add(attendeeScroll, BorderLayout.CENTER);
        attendeesPanel.add(attendeeBtns, BorderLayout.SOUTH);

        tabs.add("Event Attendees", attendeesPanel);

        add(tabs);

        // ACTIONS
        refreshEventsBtn.addActionListener(e -> loadEvents());
        addEventBtn.addActionListener(e -> addEvent());
        editEventBtn.addActionListener(e -> editEvent());
        deleteEventBtn.addActionListener(e -> deleteEvent());
        loadAttendeesBtn.addActionListener(e -> loadAttendees());

        loadEvents();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // helper
    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean isValidDateFormat(String date) {
        // فحص بسيط لصيغة YYYY-MM-DD HH:MM:SS
        // ما هو perfect بس يكفي للمشروع
        return date.matches("\\d{4}-\\d{2}-\\d{2}.*");
    }

    // ---------------------
    // LOAD EVENTS
    // ---------------------
    private void loadEvents() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Title", "Category", "Location", "Date", "Capacity"}, 0
        );

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM events WHERE organizer_id = ?")) {

            ps.setInt(1, organizerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("event_id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getString("location"),
                        rs.getString("event_date"),
                        rs.getInt("seat_capacity")
                });
            }

            eventTable.setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading events:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------------
    // ADD EVENT
    // ---------------------
    private void addEvent() {

        String title = JOptionPane.showInputDialog(this, "Event Title:");
        String category = JOptionPane.showInputDialog(this, "Event Category:");
        String location = JOptionPane.showInputDialog(this, "Location:");
        String date = JOptionPane.showInputDialog(this, "Event Date (YYYY-MM-DD HH:MM:SS):");
        String capacity = JOptionPane.showInputDialog(this, "Capacity:");

        // لو المستخدم ضغط Cancel في أي وحدة
        if (title == null || category == null || location == null || date == null || capacity == null) {
            JOptionPane.showMessageDialog(this, "Operation cancelled.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // إزالة المسافات
        title = title.trim();
        category = category.trim();
        location = location.trim();
        date = date.trim();
        capacity = capacity.trim();

        // فحص الحقول الفارغة
        if (title.isEmpty() || category.isEmpty() || location.isEmpty() || date.isEmpty() || capacity.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields are required (no empty values).",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // طول النصوص (بس فحص بسيط)
        if (title.length() > 100 || category.length() > 100 || location.length() > 100) {
            JOptionPane.showMessageDialog(this,
                    "Title, Category, and Location must be less than 100 characters.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // فحص صيغة التاريخ بشكل مبدئي
        if (!isValidDateFormat(date)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter date in format: YYYY-MM-DD HH:MM:SS",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cap;
        try {
            cap = Integer.parseInt(capacity);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                    "Capacity must be an integer number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cap <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Capacity must be a positive number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // إدخال في الداتابيس
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO events (title, category, location, event_date, seat_capacity, organizer_id) VALUES (?, ?, ?, ?, ?, ?)"
             )) {

            ps.setString(1, title);
            ps.setString(2, category);
            ps.setString(3, location);
            ps.setString(4, date);
            ps.setInt(5, cap);
            ps.setInt(6, organizerId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Event added successfully!");
            loadEvents();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding event:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------------
    // EDIT EVENT
    // ---------------------
    private void editEvent() {

        int row = eventTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select an event to edit!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) eventTable.getValueAt(row, 0);

        String title = JOptionPane.showInputDialog(this, "New Title:", eventTable.getValueAt(row, 1));
        String category = JOptionPane.showInputDialog(this, "New Category:", eventTable.getValueAt(row, 2));
        String location = JOptionPane.showInputDialog(this, "New Location:", eventTable.getValueAt(row, 3));
        String date = JOptionPane.showInputDialog(this, "New Date (YYYY-MM-DD HH:MM:SS):", eventTable.getValueAt(row, 4));
        String capacity = JOptionPane.showInputDialog(this, "New Capacity:", eventTable.getValueAt(row, 5));

        // لو المستخدم لغى أي وحدة
        if (title == null || category == null || location == null || date == null || capacity == null) {
            JOptionPane.showMessageDialog(this, "Operation cancelled.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        title = title.trim();
        category = category.trim();
        location = location.trim();
        date = date.trim();
        capacity = capacity.trim();

        if (title.isEmpty() || category.isEmpty() || location.isEmpty() || date.isEmpty() || capacity.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields are required (no empty values).",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (title.length() > 100 || category.length() > 100 || location.length() > 100) {
            JOptionPane.showMessageDialog(this,
                    "Title, Category, and Location must be less than 100 characters.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!isValidDateFormat(date)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter date in format: YYYY-MM-DD HH:MM:SS",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cap;
        try {
            cap = Integer.parseInt(capacity);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                    "Capacity must be an integer number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cap <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Capacity must be a positive number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE events SET title=?, category=?, location=?, event_date=?, seat_capacity=? WHERE event_id=?"
             )) {

            ps.setString(1, title);
            ps.setString(2, category);
            ps.setString(3, location);
            ps.setString(4, date);
            ps.setInt(5, cap);
            ps.setInt(6, id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Event updated successfully!");
            loadEvents();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating event:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------------
    // DELETE EVENT
    // ---------------------
    private void deleteEvent() {
        int row = eventTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select an event!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) eventTable.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this event and its registrations?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            // أولاً نحذف التسجيلات المرتبطة
            try (PreparedStatement psReg = con.prepareStatement(
                    "DELETE FROM registrations WHERE event_id = ?")) {
                psReg.setInt(1, id);
                psReg.executeUpdate();
            }

            // بعدين نحذف الفعالية نفسها
            try (PreparedStatement psEvt = con.prepareStatement(
                    "DELETE FROM events WHERE event_id = ?")) {
                psEvt.setInt(1, id);
                psEvt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this,
                    "Event and its registrations deleted successfully!");
            loadEvents();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting event:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------------
    // LOAD ATTENDEES
    // ---------------------
    private void loadAttendees() {

        int row = eventTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select an event!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = (int) eventTable.getValueAt(row, 0);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Reg ID", "Name", "Email", "Status"}, 0
        );

        String sql =
                "SELECT r.registration_id, u.full_name, u.email, r.status " +
                "FROM registrations r JOIN users u ON r.user_id = u.user_id " +
                "WHERE r.event_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("registration_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("status")
                });
            }

            attendeeTable.setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading attendees:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
