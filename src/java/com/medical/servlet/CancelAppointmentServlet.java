package com.medical.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/CancelAppointmentServlet")
public class CancelAppointmentServlet extends HttpServlet {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/medical_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "shacika@@2006";
    private Gson gson = new Gson();
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    // ORIGINAL GET METHOD - For redirects from other pages
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
        System.out.println("❌ [CancelAppointment GET] Request received");
        System.out.println("   Appointment ID: " + appointmentId);
        
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            System.out.println("❌ Invalid appointment ID");
            response.sendRedirect("AdminServlet?error=invalidId");
            return;
        }
        
        String query = "UPDATE appointments SET status = 'cancelled', " +
                      "cancelled_at = NOW() WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, Integer.parseInt(appointmentId));
            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("✅ Appointment cancelled successfully");
                System.out.println("========================================");
                response.sendRedirect("AdminServlet?success=cancelled");
            } else {
                System.out.println("❌ No appointment found with ID: " + appointmentId);
                System.out.println("========================================");
                response.sendRedirect("AdminServlet?error=notFound");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ SQL Error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            response.sendRedirect("AdminServlet?error=dbError");
        }
    }
    
    // NEW POST METHOD - For AJAX requests with JSON response
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            result.put("success", false);
            result.put("message", "Session expired. Please login again.");
            out.print(gson.toJson(result));
            out.flush();
            return;
        }
        
        String appointmentId = request.getParameter("id");
        
        System.out.println("========================================");
        System.out.println("❌ [CancelAppointment POST] Request received");
        System.out.println("   Appointment ID: " + appointmentId);
        
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            System.out.println("❌ Invalid appointment ID");
            result.put("success", false);
            result.put("message", "Appointment ID is required");
            out.print(gson.toJson(result));
            out.flush();
            return;
        }
        
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        
        try {
            int id = Integer.parseInt(appointmentId);
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false);
            
            // Check current status
            String checkSql = "SELECT status FROM appointments WHERE id = ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, id);
            rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                System.out.println("❌ Appointment not found");
                result.put("success", false);
                result.put("message", "Appointment not found");
                conn.rollback();
                out.print(gson.toJson(result));
                out.flush();
                return;
            }
            
            String currentStatus = rs.getString("status");
            System.out.println("   Current status: " + currentStatus);
            rs.close();
            checkStmt.close();
            
            // Check if already cancelled
            if ("cancelled".equals(currentStatus)) {
                System.out.println("❌ Already cancelled");
                result.put("success", false);
                result.put("message", "Appointment is already cancelled");
                conn.rollback();
                out.print(gson.toJson(result));
                out.flush();
                return;
            }
            
            // Update to cancelled
            String updateSql = "UPDATE appointments SET status = 'cancelled', " +
                              "cancelled_at = NOW() WHERE id = ?";
            updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, id);
            
            int rows = updateStmt.executeUpdate();
            System.out.println("   Rows updated: " + rows);
            
            if (rows > 0) {
                conn.commit();
                System.out.println("✅ Appointment cancelled successfully");
                System.out.println("========================================");
                result.put("success", true);
                result.put("message", "Appointment cancelled successfully");
                result.put("newStatus", "cancelled");
            } else {
                conn.rollback();
                System.out.println("❌ Update failed");
                System.out.println("========================================");
                result.put("success", false);
                result.put("message", "Failed to cancel appointment");
            }
            
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid ID format: " + e.getMessage());
            result.put("success", false);
            result.put("message", "Invalid appointment ID format");
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            System.err.println("❌ SQL Error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            result.put("success", false);
            result.put("message", "Database error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (checkStmt != null) checkStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (updateStmt != null) updateStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
        
        out.print(gson.toJson(result));
        out.flush();
    }
}