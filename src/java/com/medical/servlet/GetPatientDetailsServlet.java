package com.medical.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.Gson;

@WebServlet("/GetPatientDetailsServlet")
public class GetPatientDetailsServlet extends HttpServlet {
    
    // UPDATE THIS PASSWORD TO YOUR MYSQL ROOT PASSWORD!
    private static final String DB_URL = "jdbc:mysql://localhost:3306/medical_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "shacika@@2006"; // <-- PUT YOUR MYSQL PASSWORD HERE
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String doctorId = (String) session.getAttribute("doctorId");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        if(doctorId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"error\": \"Not logged in\"}");
            return;
        }
        
        List<Patient> patients = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            String sql = "SELECT DISTINCT p.id, p.name, p.email, p.phone, " +
                        "p.blood_group, p.age, " +
                        "MAX(a.appointment_date) as last_visit " +
                        "FROM patients p " +
                        "JOIN appointments a ON p.id = a.patient_id " +
                        "WHERE a.doctor_id = ? " +
                        "GROUP BY p.id, p.name, p.email, p.phone, p.blood_group, p.age " +
                        "ORDER BY last_visit DESC";
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, doctorId);
            rs = stmt.executeQuery();
            
            while(rs.next()) {
                Patient patient = new Patient();
                patient.id = rs.getInt("id");
                patient.name = rs.getString("name");
                patient.email = rs.getString("email");
                patient.phone = rs.getString("phone");
                patient.bloodGroup = rs.getString("blood_group");
                patient.age = rs.getInt("age");
                
                Date lastVisit = rs.getDate("last_visit");
                patient.lastVisit = lastVisit != null ? lastVisit.toString() : "";
                
                patients.add(patient);
            }
            
            Gson gson = new Gson();
            out.write(gson.toJson(patients));
            
        } catch(Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"" + e.getMessage() + "\"}");
        } finally {
            try {
                if(rs != null) rs.close();
                if(stmt != null) stmt.close();
                if(conn != null) conn.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    class Patient {
        int id;
        String name;
        String email;
        String phone;
        String bloodGroup;
        int age;
        String lastVisit;
    }
}