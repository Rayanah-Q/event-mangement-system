
package com.mycompany.event_project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/event_project?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private static final String USER = "root";
    private static final String PASS = "FghJ1234";

    public static Connection getConnection() {
       Connection con = null;
               
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("done");

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connection Error: " + e.getMessage());
        }

        return con;
    }
}
