package com.medical.util;  // Change from com.medical.servlet

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static Connection con;
    
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/medical_db?zeroDateTimeBehavior=CONVERT_TO_NULL",
                "root",
                "shacika@@2006"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}