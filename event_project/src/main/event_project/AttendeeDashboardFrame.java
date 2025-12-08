package com.mycompany.event_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AttendeeDashboardFrame extends JFrame {

    private int attendeeId;
    private JTable eventsTable, ticketsTable;
    private JButton registerBtn, cancelBtn, refreshEventsBtn, refreshTicketsBtn, viewTicketBtn, logoutBtn;

    public AttendeeDashboardFrame(int attendeeId) {
        this.attendeeId = attendeeId;

        setTitle("Attendee Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.decode("#debee6"));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(Color.decode("#debee6"));
        tabs.setFont(new Font("Tahoma", Font.ITALIC, 12));

        // EVENTS TAB
        JPanel eventsPanel = new JPanel(new BorderLayout());
        eventsPanel.setBackground(Color.decode("#debee6"));

        eventsTable = new JTable();
        eventsTable.setBackground(Color.decode("#f1ebf2"));
        eventsTable.setFont(new Font("Tahoma", Font.ITALIC, 12));
        eventsTable.getTableHeader().setBackground(Color.decode("#debee6"));
        eventsTable.getTableHeader().setFont(new Font("Tahoma", Font.ITALIC, 12));
        JScrollPane eventScroll = new JScrollPane(eventsTable);

        JPanel eventBtns = new JPanel();
        eventBtns.setBackground(Color.decode("#debee6"));
        registerBtn = new JButton("Register");
        styleButton(registerBtn);
        refreshEventsBtn = new JButton("Refresh");
        styleButton(refreshEventsBtn);
        eventBtns.add(registerBtn);
        eventBtns.add(refreshEventsBtn);

        eventsPanel.add(eventScroll, BorderLayout.CENTER);
        eventsPanel.add(eventBtns, BorderLayout.SOUTH);

        tabs.add("Available Events", eventsPanel);

        // TICKETS TAB
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

        // LOGOUT BUTTON
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

        // ACTIONS
        registerBtn.addActionListener(e -> registerForEvent());
        cancelBtn.addActionListener(e -> cancelRegistration());
        refreshEventsBtn.addActionListener(e -> loadEvents());
        refreshTicketsBtn.addActionListener(e -> loadTickets());
        viewTicketBtn.addActionListener(e -> showTicketDetails());
        logoutBtn.addActionListener(e -> logout());

        loadEvents();
        loadTickets();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void styleButton(JButton btn) {
        btn.setBackground(Color.decode("#f1ebf2"));
        btn.setFont(new Font("Tahoma", Font.ITALIC, 12));
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new Log_sginup();
            dispose();
        }
    }

    private void loadEvents() {

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Title", "Category", "Location", "Date", "Capacity", "Registered"}, 0
        );

        String sql =
                "SELECT e.event_id, e.title, e.category, e.location, e.event_date, " +
                "       e.seat_capacity, " +
                "       (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.event_id) AS registeredCount " +
                "FROM events e ORDER BY e.event_date ASC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("event_id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getString("location"),
                        rs.getString("event_date"),
                        rs.getInt("seat_capacity"),
                        rs.getInt("registeredCount")
                });
            }

            eventsTable.setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading events:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void registerForEvent() {

        int row = eventsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an event first!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = (int) eventsTable.getValueAt(row, 0);
        int capacity = (int) eventsTable.getValueAt(row, 5);
        int registered = (int) eventsTable.getValueAt(row, 6);

        if (registered >= capacity) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sorry, this event is FULL.",
                    "Registration Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String checkSql = "SELECT * FROM registrations WHERE user_id = ? AND event_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(checkSql)) {

            ps.setInt(1, attendeeId);
            ps.setInt(2, eventId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(
                        this,
                        "You are already registered for this event!",
                        "Duplicate Registration",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error checking registration:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Do you want to register for this event?",
                "Confirm Registration",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION)
            return;

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement regPs = con.prepareStatement(
                    "INSERT INTO registrations (event_id, user_id, status) VALUES (?, ?, 'REGISTERED')",
                    Statement.RETURN_GENERATED_KEYS
            );

            regPs.setInt(1, eventId);
            regPs.setInt(2, attendeeId);
            regPs.executeUpdate();

            ResultSet regKeys = regPs.getGeneratedKeys();
            regKeys.next();
            int registrationId = regKeys.getInt(1);

            String ticketId = "TCK-" + registrationId + "-" + System.currentTimeMillis();

            PreparedStatement ticketPs = con.prepareStatement(
                    "INSERT INTO tickets (registration_id, ticket_code, status) VALUES (?, ?, 'ACTIVE')"
            );

            ticketPs.setInt(1, registrationId);
            ticketPs.setString(2, ticketId);
            ticketPs.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Registration Successful!\nYour Ticket ID:\n" + ticketId,
                    "Ticket Generated",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadEvents();
            loadTickets();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error during registration:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTickets() {

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Registration ID", "Event Title", "Date", "Status"}, 0
        );

        String sql =
                "SELECT r.registration_id, e.title, e.event_date, r.status " +
                "FROM registrations r JOIN events e ON r.event_id = e.event_id " +
                "WHERE r.user_id = ? ORDER BY e.event_date";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, attendeeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("registration_id"),
                        rs.getString("title"),
                        rs.getString("event_date"),
                        rs.getString("status")
                });
            }

            ticketsTable.setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading tickets:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void cancelRegistration() {

        int row = ticketsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a ticket to cancel!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int regId = (int) ticketsTable.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this registration?",
                "Confirm Cancel",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION)
            return;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM registrations WHERE registration_id = ?"
             )) {

            ps.setInt(1, regId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Registration canceled!",
                    "Canceled",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadTickets();
            loadEvents();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error canceling registration:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showTicketDetails() {

        int row = ticketsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a ticket to view!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int regId = (int) ticketsTable.getValueAt(row, 0);

        String sql =
                "SELECT r.registration_id, e.title, e.location, e.category, e.event_date, " +
                "       t.ticket_code, t.status " +
                "FROM registrations r " +
                "JOIN events e ON r.event_id = e.event_id " +
                "JOIN tickets t ON r.registration_id = t.registration_id " +
                "WHERE r.registration_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, regId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String info =
                        "------ EVENT TICKET ------\n\n" +
                        "Event: " + rs.getString("title") + "\n" +
                        "Category: " + rs.getString("category") + "\n" +
                        "Location: " + rs.getString("location") + "\n" +
                        "Date: " + rs.getString("event_date") + "\n\n" +
                        "Ticket ID:\n" + rs.getString("ticket_code") + "\n\n" +
                        "Status: " + rs.getString("status") + "\n" +
                        "--------------------------";

                JOptionPane.showMessageDialog(
                        this,
                        info,
                        "Ticket Details",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading ticket details:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}