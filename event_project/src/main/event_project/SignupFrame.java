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
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 1));
        getContentPane().setBackground(Color.decode("#debee6"));

        JPanel p1 = new JPanel(new FlowLayout());
        p1.setBackground(Color.decode("#debee6"));
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p1.add(nameLabel);
        fullNameField = new JTextField(20);
        fullNameField.setBackground(Color.decode("#f1ebf2"));
        fullNameField.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p1.add(fullNameField);

        JPanel p2 = new JPanel(new FlowLayout());
        p2.setBackground(Color.decode("#debee6"));
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p2.add(emailLabel);
        emailField = new JTextField(20);
        emailField.setBackground(Color.decode("#f1ebf2"));
        emailField.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p2.add(emailField);

        JPanel p3 = new JPanel(new FlowLayout());
        p3.setBackground(Color.decode("#debee6"));
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p3.add(passLabel);
        passwordField = new JPasswordField(20);
        passwordField.setBackground(Color.decode("#f1ebf2"));
        passwordField.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p3.add(passwordField);

        JPanel p4 = new JPanel(new FlowLayout());
        p4.setBackground(Color.decode("#debee6"));
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p4.add(confirmLabel);
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setBackground(Color.decode("#f1ebf2"));
        confirmPasswordField.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p4.add(confirmPasswordField);

        JPanel p5 = new JPanel(new FlowLayout());
        p5.setBackground(Color.decode("#debee6"));
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p5.add(roleLabel);
        roleBox = new JComboBox<>(new String[]{"ATTENDEE"});
        roleBox.setBackground(Color.decode("#f1ebf2"));
        roleBox.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p5.add(roleBox);

        JPanel p6 = new JPanel(new FlowLayout());
        p6.setBackground(Color.decode("#debee6"));
        registerButton = new JButton("Register");
        registerButton.setBackground(Color.decode("#f1ebf2"));
        registerButton.setFont(new Font("Tahoma", Font.ITALIC, 12));
        backButton = new JButton("Back to Login");
        backButton.setBackground(Color.decode("#f1ebf2"));
        backButton.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p6.add(registerButton);
        p6.add(backButton);

        add(p1);
        add(p2);
        add(p3);
        add(p4);
        add(p5);
        add(p6);

        // Enter key navigation
        fullNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    emailField.requestFocus();
                }
            }
        });

        emailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    confirmPasswordField.requestFocus();
                }
            }
        });

        confirmPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    registerButton.doClick();
                }
            }
        });

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

            // Email format validation
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