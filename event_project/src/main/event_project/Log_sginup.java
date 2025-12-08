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
        getContentPane().setBackground(Color.decode("#debee6"));

        JPanel p1 = new JPanel(new FlowLayout());
        p1.setBackground(Color.decode("#debee6"));
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p1.add(emailLabel);
        emailField = new JTextField(20);
        emailField.setBackground(Color.decode("#f1ebf2"));
        emailField.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p1.add(emailField);

        JPanel p2 = new JPanel(new FlowLayout());
        p2.setBackground(Color.decode("#debee6"));
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p2.add(passwordLabel);
        passwordField = new JPasswordField(20);
        passwordField.setBackground(Color.decode("#f1ebf2"));
        passwordField.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p2.add(passwordField);

        JPanel p3 = new JPanel(new FlowLayout());
        p3.setBackground(Color.decode("#debee6"));
        loginButton = new JButton("Login");
        loginButton.setBackground(Color.decode("#f1ebf2"));
        loginButton.setFont(new Font("Tahoma", Font.ITALIC, 12));
        signupButton = new JButton("Sign Up");
        signupButton.setBackground(Color.decode("#f1ebf2"));
        signupButton.setFont(new Font("Tahoma", Font.ITALIC, 12));
        p3.add(loginButton);
        p3.add(signupButton);

        add(p1);
        add(p2);
        add(p3);

        // Enter key navigation
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
                    loginButton.doClick();
                }
            }
        });

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