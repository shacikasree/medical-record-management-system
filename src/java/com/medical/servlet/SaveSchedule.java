package com.medical.servlet;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/SaveSchedule")
public class SaveSchedule extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("========================================");
        System.out.println("💾 [SaveSchedule] Processing request");
        
        // Session check
        HttpSession session = request.getSession(false);
        if (session == null || !"doctor".equals(session.getAttribute("role"))) {
            System.out.println("❌ Unauthorized access");
            response.sendRedirect("Login.jsp?error=unauthorized");
            return;
        }

        Integer doctorId = (Integer) session.getAttribute("doctorId");
        if (doctorId == null) {
            doctorId = (Integer) session.getAttribute("userId");
        }
        
        String action = request.getParameter("action");
        
        System.out.println("   Doctor ID: " + doctorId);
        System.out.println("   Action: " + action);

        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            
            if ("saveSchedule".equals(action)) {
                saveWeeklySchedule(request, conn, doctorId);
                System.out.println("✅ Schedule saved successfully");
                response.sendRedirect("DoctorServlet#availability?success=scheduleUpdated");
                
            } else if ("addUnavailableDate".equals(action)) {
                addUnavailableDate(request, conn, doctorId);
                System.out.println("✅ Unavailable date added");
                response.sendRedirect("DoctorServlet#availability?success=dateAdded");
                
            } else if ("removeUnavailableDate".equals(action)) {
                removeUnavailableDate(request, conn, doctorId);
                System.out.println("✅ Unavailable date removed");
                response.sendRedirect("DoctorServlet#availability?success=dateRemoved");
                
            } else if ("toggleEmergency".equals(action)) {
                toggleEmergencyStatus(request, conn, doctorId);
                System.out.println("✅ Emergency status updated");
                
                // Return JSON response for AJAX
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\": true}");
                return;
                
            } else {
                System.out.println("❌ Invalid action: " + action);
                response.sendRedirect("DoctorServlet?error=invalidAction");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error: " + e.getMessage());
            response.sendRedirect("DoctorServlet#availability?error=saveFailed");
        } finally {
            try {
                if (conn != null) conn.close();
                System.out.println("========================================");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveWeeklySchedule(HttpServletRequest request, Connection conn, int doctorId) 
            throws SQLException {
        
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        
        // Create table if not exists
        String createTable = "CREATE TABLE IF NOT EXISTS doctor_schedule (" +
                           "id INT PRIMARY KEY AUTO_INCREMENT, " +
                           "doctor_id INT NOT NULL, " +
                           "day_name VARCHAR(20) NOT NULL, " +
                           "is_active BOOLEAN DEFAULT true, " +
                           "start_time TIME, " +
                           "end_time TIME, " +
                           "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                           "UNIQUE KEY unique_doctor_day (doctor_id, day_name))";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTable);
        }
        
        String sql = "INSERT INTO doctor_schedule (doctor_id, day_name, is_active, start_time, end_time) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE is_active=?, start_time=?, end_time=?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String day : days) {
                boolean isActive = "on".equals(request.getParameter(day + "_active"));
                String startTime = request.getParameter(day + "_start");
                String endTime = request.getParameter(day + "_end");
                
                System.out.println("   " + day + ": active=" + isActive + 
                                 ", start=" + startTime + ", end=" + endTime);
                
                pstmt.setInt(1, doctorId);
                pstmt.setString(2, day);
                pstmt.setBoolean(3, isActive);
                pstmt.setString(4, isActive && startTime != null && !startTime.isEmpty() ? startTime : null);
                pstmt.setString(5, isActive && endTime != null && !endTime.isEmpty() ? endTime : null);
                pstmt.setBoolean(6, isActive);
                pstmt.setString(7, isActive && startTime != null && !startTime.isEmpty() ? startTime : null);
                pstmt.setString(8, isActive && endTime != null && !endTime.isEmpty() ? endTime : null);
                
                pstmt.executeUpdate();
            }
        }
    }

    private void addUnavailableDate(HttpServletRequest request, Connection conn, int doctorId) 
            throws SQLException {
        
        String date = request.getParameter("unavailableDate");
        String reason = request.getParameter("reason");
        
        System.out.println("   Adding unavailable date: " + date);
        System.out.println("   Reason: " + (reason != null ? reason : "Not specified"));
        
        // Create table if not exists
        String createTable = "CREATE TABLE IF NOT EXISTS doctor_unavailable_dates (" +
                           "id INT PRIMARY KEY AUTO_INCREMENT, " +
                           "doctor_id INT NOT NULL, " +
                           "unavailable_date DATE NOT NULL, " +
                           "reason VARCHAR(255), " +
                           "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                           "UNIQUE KEY unique_doctor_date (doctor_id, unavailable_date))";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTable);
        }
        
        String sql = "INSERT INTO doctor_unavailable_dates (doctor_id, unavailable_date, reason) " +
                    "VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setString(2, date);
            pstmt.setString(3, reason != null && !reason.isEmpty() ? reason : "Not available");
            pstmt.executeUpdate();
        }
    }

    private void removeUnavailableDate(HttpServletRequest request, Connection conn, int doctorId) 
            throws SQLException {
        
        int dateId = Integer.parseInt(request.getParameter("dateId"));
        
        System.out.println("   Removing unavailable date ID: " + dateId);
        
        String sql = "DELETE FROM doctor_unavailable_dates WHERE id = ? AND doctor_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dateId);
            pstmt.setInt(2, doctorId);
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("   ✅ Date removed successfully");
            } else {
                System.out.println("   ⚠️ No date found with ID: " + dateId);
            }
        }
    }

    private void toggleEmergencyStatus(HttpServletRequest request, Connection conn, int doctorId) 
            throws SQLException {
        
        boolean emergencyAvailable = "true".equals(request.getParameter("emergencyAvailable"));
        
        System.out.println("   Setting emergency status: " + emergencyAvailable);
        
        // Create table if not exists
        String createTable = "CREATE TABLE IF NOT EXISTS doctor_settings (" +
                           "doctor_id INT PRIMARY KEY, " +
                           "emergency_available BOOLEAN DEFAULT true, " +
                           "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTable);
        }
        
        String sql = "INSERT INTO doctor_settings (doctor_id, emergency_available) " +
                    "VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE emergency_available = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setBoolean(2, emergencyAvailable);
            pstmt.setBoolean(3, emergencyAvailable);
            pstmt.executeUpdate();
        }
    }
}