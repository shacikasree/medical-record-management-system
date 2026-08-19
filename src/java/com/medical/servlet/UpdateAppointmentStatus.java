package com.medical.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/UpdateAppointmentStatus")
public class UpdateAppointmentStatus extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("========================================");
        System.out.println("🔄 [UpdateAppointmentStatus] POST Request");
        
        // Session check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            System.out.println("❌ No session found - redirecting to login");
            response.sendRedirect("Login.jsp?error=loginRequired");
            return;
        }

        // Check if user is a doctor
        String role = (String) session.getAttribute("role");
        if (!"doctor".equals(role)) {
            System.out.println("❌ User is not a doctor - role: " + role);
            response.sendRedirect("Login.jsp?error=unauthorized");
            return;
        }

        // Get parameters
        String appointmentIdStr = request.getParameter("appointmentId");
        String newStatus = request.getParameter("status");
        String doctorIdStr = request.getParameter("doctorId");
        
        System.out.println("📋 Parameters received:");
        System.out.println("   Appointment ID: " + appointmentIdStr);
        System.out.println("   New Status: " + newStatus);
        System.out.println("   Doctor ID: " + doctorIdStr);

        // Validate parameters
        if (appointmentIdStr == null || appointmentIdStr.trim().isEmpty()) {
            System.out.println("❌ Missing appointment ID");
            response.sendRedirect("DoctorServlet?error=missingAppointmentId");
            return;
        }

        if (newStatus == null || newStatus.trim().isEmpty()) {
            System.out.println("❌ Missing status");
            response.sendRedirect("DoctorServlet?error=missingStatus");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            int appointmentId = Integer.parseInt(appointmentIdStr);
            
            conn = DBConnection.getConnection();
            System.out.println("✅ Database connection established");

            // Update appointment status
            String sql;
            if ("completed".equalsIgnoreCase(newStatus)) {
                // If marking as completed, also set completed_at timestamp
                sql = "UPDATE appointments SET status = ?, completed_at = ?, updated_at = ? WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newStatus);
                pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                pstmt.setInt(4, appointmentId);
            } else if ("cancelled".equalsIgnoreCase(newStatus)) {
                // If cancelling, set cancelled_at timestamp and cancelled_by
                String doctorName = (String) session.getAttribute("fullname");
                sql = "UPDATE appointments SET status = ?, cancelled_at = ?, cancelled_by = ?, updated_at = ? WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newStatus);
                pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                pstmt.setString(3, doctorName);
                pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                pstmt.setInt(5, appointmentId);
            } else {
                // For other status changes
                sql = "UPDATE appointments SET status = ?, updated_at = ? WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newStatus);
                pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                pstmt.setInt(3, appointmentId);
            }

            System.out.println("📝 Executing SQL: " + sql);
            System.out.println("   Parameters: status=" + newStatus + ", appointmentId=" + appointmentId);

            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅✅✅ Appointment status updated successfully!");
                System.out.println("   Rows affected: " + rowsAffected);
                System.out.println("   New status: " + newStatus);
                System.out.println("========================================");
                
                // Redirect back to doctor dashboard with success message
                response.sendRedirect("DoctorServlet?success=statusUpdated");
            } else {
                System.out.println("❌ No rows updated - appointment not found?");
                System.out.println("========================================");
                response.sendRedirect("DoctorServlet?error=appointmentNotFound");
            }

        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid appointment ID format: " + appointmentIdStr);
            System.out.println("========================================");
            response.sendRedirect("DoctorServlet?error=invalidAppointmentId");
        } catch (Exception e) {
            System.err.println("❌❌❌ Error updating appointment status:");
            e.printStackTrace();
            System.out.println("========================================");
            response.sendRedirect("DoctorServlet?error=updateFailed");
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
                System.out.println("🔒 Database connection closed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to POST
        doPost(request, response);
    }
}