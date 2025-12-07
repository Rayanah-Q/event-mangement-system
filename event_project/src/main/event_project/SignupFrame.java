/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.event_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SignupFrame extends JFrame {

    private JTextField fullNameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> roleBox;
    private JButton registerButton, backButton;

    public SignupFrame() {

        setTitle("Event System - Sign Up");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 1));

        JPanel p1 = new JPanel(new FlowLayout());
        p1.add(new JLabel("Full Name:"));
        fullNameField = new JTextField(20);
        p1.add(fullNameField);

        JPanel p2 = new JPanel(new FlowLayout());
        p2.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        p2.add(emailField);

        JPanel p3 = new JPanel(new FlowLayout());
        p3.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        p3.add(passwordField);

        JPanel p4 = new JPanel(new FlowLayout());
        p4.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField(20);
        p4.add(confirmPasswordField);

        JPanel p5 = new JPanel(new FlowLayout());
        p5.add(new JLabel("Role:"));
        roleBox = new JComboBox<>(new String[]{"ATTENDEE"});
        // only attendees register from app
        p5.add(roleBox);

        JPanel p6 = new JPanel(new FlowLayout());
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");
        p6.add(registerButton);
        p6.add(backButton);

        add(p1);
        add(p2);
        add(p3);
        add(p4);
        add(p5);
        add(p6);

        // Actions
        registerButton.addActionListener(new RegisterAction());
        backButton.addActionListener(e -> {
            new Log_sginup();
            dispose();
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class RegisterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            String name = fullNameField.getText();
String email = emailField.getText();
String pass = new String(passwordField.getPassword());
String confirm = new String(confirmPasswordField.getPassword());
String role = roleBox.getSelectedItem().toString();

// Validation
if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
    JOptionPane.showMessageDialog(null, "Please fill all fields!");
    return;
}

// Email format validation (xxxxx@xxx.xxx)
if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
    JOptionPane.showMessageDialog(null, "Invalid email format! Use example@domain.com");
    return;
}

if (!pass.equals(confirm)) {
    JOptionPane.showMessageDialog(null, "Passwords do not match!");
    return;
}

            // Insert user
            try (Connection con = DBConnection.getConnection()) {

                String sql = "INSERT INTO users (full_name, email, password, role) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);

                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, pass);
                ps.setString(4, role);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(null, "Registered Successfully!");
                new Log_sginup();
                dispose();

            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(null, "Email already exists!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        }
    }
}
