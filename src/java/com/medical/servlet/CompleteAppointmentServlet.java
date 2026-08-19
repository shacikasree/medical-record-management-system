package com.medical.servlet;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/CompleteAppointmentServlet")
public class CompleteAppointmentServlet extends HttpServlet {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/medical_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "shacika@@2006";
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("Login.jsp?error=sessionExpired");
            return;
        }
        
        String appointmentId = request.getParameter("id");
        
        System.out.println("========================================");
        System.out.println("✅ [CompleteAppointment] Request received");
        System.out.println("   Appointment ID: " + appointmentId);
        
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            System.out.println("❌ Invalid appointment ID");
            response.sendRedirect("PatientServlet?error=invalidId");
            return;
        }
        
        String query = "UPDATE appointments SET status = 'completed', " +
                      "completed_at = NOW() WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, Integer.parseInt(appointmentId));
            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("✅ Appointment marked as completed");
                System.out.println("========================================");
                response.sendRedirect("PatientServlet?success=completed");
            } else {
                System.out.println("❌ No appointment found with ID: " + appointmentId);
                System.out.println("========================================");
                response.sendRedirect("PatientServlet?error=notFound");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ SQL Error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            response.sendRedirect("PatientServlet?error=dbError");
        }
    }
}