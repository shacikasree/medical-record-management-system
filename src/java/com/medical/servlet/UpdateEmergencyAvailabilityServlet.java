package com.medical.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * UpdateEmergencyAvailabilityServlet
 * 
 * Uses existing doctor_unavailable_dates table to store emergency availability status
 * Logic:
 * - Emergency OFF: Insert a marker record with reason='EMERGENCY_OFF' and date='9999-12-31'
 * - Emergency ON: Delete the marker record
 * 
 * This approach reuses the existing table without creating new columns or tables
 * This version does NOT require org.json library - uses manual JSON string building
 */
@WebServlet("/UpdateEmergencyAvailabilityServlet")
public class UpdateEmergencyAvailabilityServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Special marker values to identify emergency status records
    private static final String EMERGENCY_MARKER_REASON = "EMERGENCY_OFF";
    private static final String EMERGENCY_MARKER_DATE = "9999-12-31";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        System.out.println("========================================");
        System.out.println("🚨 [UpdateEmergencyAvailability] Processing request");
        
        // Session check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            System.out.println("❌ No session found");
            String jsonResponse = "{\"success\":false,\"message\":\"Session expired. Please login again.\"}";
            out.print(jsonResponse);
            out.flush();
            return;
        }

        // Check if user is a doctor
        String role = (String) session.getAttribute("role");
        if (!"doctor".equals(role)) {
            System.out.println("❌ User is not a doctor - role: " + role);
            String jsonResponse = "{\"success\":false,\"message\":\"Unauthorized access.\"}";
            out.print(jsonResponse);
            out.flush();
            return;
        }

        // Get doctor ID from session
        Integer doctorId = (Integer) session.getAttribute("doctorId");
        if (doctorId == null) {
            doctorId = (Integer) session.getAttribute("userId");
        }

        // Get emergency status from request
        String emergencyStatusStr = request.getParameter("emergencyAvailable");
        
        System.out.println("📋 Request Details:");
        System.out.println("   Doctor ID: " + doctorId);
        System.out.println("   Emergency Status: " + emergencyStatusStr);

        if (emergencyStatusStr == null) {
            System.out.println("❌ Emergency status parameter missing");
            String jsonResponse = "{\"success\":false,\"message\":\"Emergency status is required.\"}";
            out.print(jsonResponse);
            out.flush();
            return;
        }

        boolean emergencyAvailable = Boolean.parseBoolean(emergencyStatusStr);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            System.out.println("✅ Database connection established");

            // Create doctor_unavailable_dates table if not exists
            String createTableSQL = "CREATE TABLE IF NOT EXISTS doctor_unavailable_dates (" +
                                  "id INT PRIMARY KEY AUTO_INCREMENT, " +
                                  "doctor_id INT NOT NULL, " +
                                  "unavailable_date DATE NOT NULL, " +
                                  "reason VARCHAR(255), " +
                                  "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                  "UNIQUE KEY unique_doctor_date (doctor_id, unavailable_date))";
            
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createTableSQL);
                System.out.println("✅ Ensured doctor_unavailable_dates table exists");
            }

            if (emergencyAvailable) {
                // Emergency ON: Delete the marker record
                System.out.println("📝 Setting emergency availability to ON (Available)");
                System.out.println("   Deleting marker record...");
                
                String deleteSQL = "DELETE FROM doctor_unavailable_dates " +
                                 "WHERE doctor_id = ? AND reason = ? AND unavailable_date = ?";
                
                pstmt = conn.prepareStatement(deleteSQL);
                pstmt.setInt(1, doctorId);
                pstmt.setString(2, EMERGENCY_MARKER_REASON);
                pstmt.setString(3, EMERGENCY_MARKER_DATE);
                
                int rowsDeleted = pstmt.executeUpdate();
                System.out.println("   Rows deleted: " + rowsDeleted);
                
            } else {
                // Emergency OFF: Insert/Update the marker record
                System.out.println("📝 Setting emergency availability to OFF (Not Available)");
                System.out.println("   Inserting marker record...");
                
                // Try to insert, if exists then it's already OFF
                String insertSQL = "INSERT INTO doctor_unavailable_dates " +
                                 "(doctor_id, unavailable_date, reason) " +
                                 "VALUES (?, ?, ?) " +
                                 "ON DUPLICATE KEY UPDATE reason = ?";
                
                pstmt = conn.prepareStatement(insertSQL);
                pstmt.setInt(1, doctorId);
                pstmt.setString(2, EMERGENCY_MARKER_DATE);
                pstmt.setString(3, EMERGENCY_MARKER_REASON);
                pstmt.setString(4, EMERGENCY_MARKER_REASON);
                
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("   Rows affected: " + rowsAffected);
            }

            // Verify the update
            pstmt.close();
            String verifySQL = "SELECT COUNT(*) as count FROM doctor_unavailable_dates " +
                             "WHERE doctor_id = ? AND reason = ? AND unavailable_date = ?";
            
            pstmt = conn.prepareStatement(verifySQL);
            pstmt.setInt(1, doctorId);
            pstmt.setString(2, EMERGENCY_MARKER_REASON);
            pstmt.setString(3, EMERGENCY_MARKER_DATE);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt("count");
                // If count is 0, emergency is available (ON)
                // If count is 1, emergency is not available (OFF)
                boolean actualStatus = (count == 0);
                
                System.out.println("✅ Verification:");
                System.out.println("   Marker record exists: " + (count > 0));
                System.out.println("   Emergency available: " + actualStatus);
                
                if (actualStatus == emergencyAvailable) {
                    String statusText = actualStatus ? 
                        "Available for emergencies" : "Not available for emergencies";
                    
                    // Build JSON response manually
                    String jsonResponse = "{" +
                        "\"success\":true," +
                        "\"message\":\"Emergency availability updated successfully.\"," +
                        "\"emergencyAvailable\":" + actualStatus + "," +
                        "\"statusText\":\"" + statusText + "\"" +
                        "}";
                    
                    out.print(jsonResponse);
                    System.out.println("✅ Emergency status updated successfully");
                } else {
                    System.out.println("⚠️ Status mismatch after update");
                    String jsonResponse = "{\"success\":false,\"message\":\"Status verification failed. Please try again.\"}";
                    out.print(jsonResponse);
                }
            } else {
                System.out.println("❌ Verification query failed");
                String jsonResponse = "{\"success\":false,\"message\":\"Failed to verify update.\"}";
                out.print(jsonResponse);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌❌❌ Error updating emergency status: " + e.getMessage());
            
            // Escape error message for JSON
            String errorMsg = e.getMessage().replace("\"", "\\\"").replace("\n", " ");
            String jsonResponse = "{\"success\":false,\"message\":\"Error updating emergency status: " + errorMsg + "\"}";
            out.print(jsonResponse);
            
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
                System.out.println("🔒 Database connection closed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("========================================");
        out.flush();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}