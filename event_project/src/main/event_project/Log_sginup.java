/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.event_project;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Log_sginup extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, signupButton;

    public Log_sginup() {

        setTitle("Event System - Login");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        JPanel p1 = new JPanel(new FlowLayout());
        p1.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        p1.add(emailField);

        JPanel p2 = new JPanel(new FlowLayout());
        p2.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        p2.add(passwordField);

        JPanel p3 = new JPanel(new FlowLayout());
        loginButton = new JButton("Login");
        signupButton = new JButton("Sign Up");
        p3.add(loginButton);
        p3.add(signupButton);

        add(p1);
        add(p2);
        add(p3);

        // Event Handler
        loginButton.addActionListener(new LoginAction());
        signupButton.addActionListener(e -> {
            new SignupFrame();
            dispose();
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText();
            String pass = new String(passwordField.getPassword());

            if (email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all fields!");
                return;
            }

            try (Connection con = DBConnection.getConnection()) {

                String sql = "SELECT * FROM users WHERE email=? AND password=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, email);
                ps.setString(2, pass);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

    int userId = rs.getInt("user_id");
    String role = rs.getString("role");

    JOptionPane.showMessageDialog(null, "Login Successful!");

    switch (role) {
        case "ADMIN":
            new AdminDashboardFrame();
            break;
        case "ORGANIZER":
            new OrganizerDashboardFrame(userId);
            break;
        case "ATTENDEE":
            new AttendeeDashboardFrame(userId);
            break;
    }

    dispose();
} else {
                    JOptionPane.showMessageDialog(null, "Invalid email or password!");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        }
    }
}
