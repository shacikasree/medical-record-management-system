package com.medical.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.google.gson.Gson;

@WebServlet("/SettingsServlet")
public class SettingsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Gson gson = new Gson();
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // Check authorization
        if (!isAuthorized(request, response)) return;
        
        if ("deleteHoliday".equals(action)) {
            deleteHoliday(request, response);
            return;
        }
        
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check authorization
        if (!isAuthorized(request, response)) return;
        
        String action = request.getParameter("action");
        
        System.out.println("========== SETTINGS POST REQUEST ==========");
        System.out.println("Action: " + action);
        
        if ("updateHospitalInfo".equals(action)) {
            updateHospitalInfo(request, response);
        } else if ("updateWorkingHours".equals(action)) {
            updateWorkingHours(request, response);
        } else if ("addHoliday".equals(action)) {
            addHoliday(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }
    
    // ========================================================================
    // AUTHORIZATION
    // ========================================================================
    
    private boolean isAuthorized(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("Login.jsp");
            return false;
        }
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            response.sendRedirect("Login.jsp?error=unauthorized");
            return false;
        }
        return true;
    }
    
    // ========================================================================
    // HOSPITAL INFORMATION
    // ========================================================================
    
    private void updateHospitalInfo(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement updateStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;
        
        System.out.println("========== UPDATE HOSPITAL INFO ==========");
        
        try {
            String hospitalName = request.getParameter("hospitalName");
            String hospitalAddress = request.getParameter("hospitalAddress");
            String hospitalPhone = request.getParameter("hospitalPhone");
            String hospitalEmail = request.getParameter("hospitalEmail");
            
            System.out.println("Hospital Name: " + hospitalName);
            System.out.println("Address: " + hospitalAddress);
            System.out.println("Phone: " + hospitalPhone);
            System.out.println("Email: " + hospitalEmail);
            
            // Validation
            if (hospitalName == null || hospitalName.trim().isEmpty()) {
                response.sendRedirect("AdminServlet?error=hospital_name_required");
                return;
            }
            if (hospitalAddress == null || hospitalAddress.trim().isEmpty()) {
                response.sendRedirect("AdminServlet?error=address_required");
                return;
            }
            if (hospitalPhone == null || hospitalPhone.trim().isEmpty()) {
                response.sendRedirect("AdminServlet?error=phone_required");
                return;
            }
            if (hospitalEmail == null || hospitalEmail.trim().isEmpty()) {
                response.sendRedirect("AdminServlet?error=email_required");
                return;
            }
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if settings exist
            String checkSql = "SELECT id FROM hospital_settings LIMIT 1";
            checkStmt = conn.prepareStatement(checkSql);
            rs = checkStmt.executeQuery();
            
            boolean settingsExist = rs.next();
            rs.close();
            checkStmt.close();
            
            if (settingsExist) {
                // Update existing settings
                String updateSql = "UPDATE hospital_settings SET " +
                                  "hospital_name = ?, address = ?, phone = ?, email = ?, " +
                                  "updated_at = CURRENT_TIMESTAMP " +
                                  "WHERE id = (SELECT id FROM (SELECT id FROM hospital_settings LIMIT 1) AS tmp)";
                
                updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, hospitalName.trim());
                updateStmt.setString(2, hospitalAddress.trim());
                updateStmt.setString(3, hospitalPhone.trim());
                updateStmt.setString(4, hospitalEmail.trim());
                
                int rows = updateStmt.executeUpdate();
                System.out.println("Rows updated: " + rows);
                
                if (rows > 0) {
                    conn.commit();
                    System.out.println("Hospital info updated successfully");
                    response.sendRedirect("AdminServlet?success=hospital_info_updated#settings");
                } else {
                    conn.rollback();
                    response.sendRedirect("AdminServlet?error=update_failed#settings");
                }
            } else {
                // Insert new settings
                String insertSql = "INSERT INTO hospital_settings " +
                                  "(hospital_name, address, phone, email) VALUES (?, ?, ?, ?)";
                
                insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, hospitalName.trim());
                insertStmt.setString(2, hospitalAddress.trim());
                insertStmt.setString(3, hospitalPhone.trim());
                insertStmt.setString(4, hospitalEmail.trim());
                
                int rows = insertStmt.executeUpdate();
                System.out.println("Rows inserted: " + rows);
                
                if (rows > 0) {
                    conn.commit();
                    System.out.println("Hospital info created successfully");
                    response.sendRedirect("AdminServlet?success=hospital_info_updated#settings");
                } else {
                    conn.rollback();
                    response.sendRedirect("AdminServlet?error=insert_failed#settings");
                }
            }
            
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=database_error#settings");
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=unexpected_error#settings");
        } finally {
            closeResources(conn, checkStmt, rs);
            if (updateStmt != null) {
                try { updateStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (insertStmt != null) {
                try { insertStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    // ========================================================================
    // WORKING HOURS
    // ========================================================================
    
    private void updateWorkingHours(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement updateStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;
        
        System.out.println("========== UPDATE WORKING HOURS ==========");
        
        try {
            String openingTime = request.getParameter("openingTime");
            String closingTime = request.getParameter("closingTime");
            String appointmentDuration = request.getParameter("appointmentDuration");
            
            System.out.println("Opening Time: " + openingTime);
            System.out.println("Closing Time: " + closingTime);
            System.out.println("Appointment Duration: " + appointmentDuration);
            
            // Validation
            if (openingTime == null || openingTime.trim().isEmpty()) {
                response.sendRedirect("AdminServlet?error=opening_time_required#settings");
                return;
            }
            if (closingTime == null || closingTime.trim().isEmpty()) {
                response.sendRedirect("AdminServlet?error=closing_time_required#settings");
                return;
            }
            if (appointmentDuration == null || appointmentDuration.trim().isEmpty()) {
                response.sendRedirect("AdminServlet?error=duration_required#settings");
                return;
            }
            
            int duration = 0;
            try {
                duration = Integer.parseInt(appointmentDuration.trim());
                if (duration < 15 || duration > 120) {
                    response.sendRedirect("AdminServlet?error=invalid_duration#settings");
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendRedirect("AdminServlet?error=invalid_duration_format#settings");
                return;
            }
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if settings exist
            String checkSql = "SELECT id FROM hospital_settings LIMIT 1";
            checkStmt = conn.prepareStatement(checkSql);
            rs = checkStmt.executeQuery();
            
            boolean settingsExist = rs.next();
            rs.close();
            checkStmt.close();
            
            if (settingsExist) {
                // Update existing settings
                String updateSql = "UPDATE hospital_settings SET " +
                                  "opening_time = ?, closing_time = ?, appointment_duration = ?, " +
                                  "updated_at = CURRENT_TIMESTAMP " +
                                  "WHERE id = (SELECT id FROM (SELECT id FROM hospital_settings LIMIT 1) AS tmp)";
                
                updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, openingTime.trim() + ":00");
                updateStmt.setString(2, closingTime.trim() + ":00");
                updateStmt.setInt(3, duration);
                
                int rows = updateStmt.executeUpdate();
                System.out.println("Rows updated: " + rows);
                
                if (rows > 0) {
                    conn.commit();
                    System.out.println("Working hours updated successfully");
                    response.sendRedirect("AdminServlet?success=working_hours_updated#settings");
                } else {
                    conn.rollback();
                    response.sendRedirect("AdminServlet?error=update_failed#settings");
                }
            } else {
                // Insert new settings with default values
                String insertSql = "INSERT INTO hospital_settings " +
                                  "(hospital_name, address, phone, email, opening_time, closing_time, appointment_duration) " +
                                  "VALUES ('City Hospital', '123 Medical Street', '+1234567890', 'info@hospital.com', ?, ?, ?)";
                
                insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, openingTime.trim() + ":00");
                insertStmt.setString(2, closingTime.trim() + ":00");
                insertStmt.setInt(3, duration);
                
                int rows = insertStmt.executeUpdate();
                System.out.println("Rows inserted: " + rows);
                
                if (rows > 0) {
                    conn.commit();
                    System.out.println("Working hours created successfully");
                    response.sendRedirect("AdminServlet?success=working_hours_updated#settings");
                } else {
                    conn.rollback();
                    response.sendRedirect("AdminServlet?error=insert_failed#settings");
                }
            }
            
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=database_error#settings");
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=unexpected_error#settings");
        } finally {
            closeResources(conn, checkStmt, rs);
            if (updateStmt != null) {
                try { updateStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (insertStmt != null) {
                try { insertStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    // ========================================================================
    // HOLIDAYS MANAGEMENT
    // ========================================================================
    
    private void addHoliday(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;
        
        System.out.println("========== ADD HOLIDAY ==========");
        
        try {
            String holidayName = request.getParameter("holidayName");
            String holidayDate = request.getParameter("holidayDate");
            String holidayDescription = request.getParameter("holidayDescription");
            
            System.out.println("Holiday Name: " + holidayName);
            System.out.println("Holiday Date: " + holidayDate);
            System.out.println("Description: " + holidayDescription);
            
            // Validation
            if (holidayName == null || holidayName.trim().isEmpty()) {
                response.sendRedirect("AdminServlet?error=holiday_name_required#settings");
                return;
            }
            if (holidayDate == null || holidayDate.trim().isEmpty()) {
                response.sendRedirect("AdminServlet?error=holiday_date_required#settings");
                return;
            }
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if holiday already exists on this date
            String checkSql = "SELECT id FROM holidays WHERE date = ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, holidayDate.trim());
            rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                conn.rollback();
                response.sendRedirect("AdminServlet?error=holiday_already_exists#settings");
                return;
            }
            rs.close();
            checkStmt.close();
            
            // Insert holiday
            String insertSql = "INSERT INTO holidays (name, date, description, is_active) VALUES (?, ?, ?, 'yes')";
            insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, holidayName.trim());
            insertStmt.setString(2, holidayDate.trim());
            insertStmt.setString(3, holidayDescription != null ? holidayDescription.trim() : "");
            
            int rows = insertStmt.executeUpdate();
            System.out.println("Rows inserted: " + rows);
            
            if (rows > 0) {
                conn.commit();
                System.out.println("Holiday added successfully");
                response.sendRedirect("AdminServlet?success=holiday_added#settings");
            } else {
                conn.rollback();
                response.sendRedirect("AdminServlet?error=insert_failed#settings");
            }
            
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=database_error#settings");
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=unexpected_error#settings");
        } finally {
            closeResources(conn, checkStmt, rs);
            if (insertStmt != null) {
                try { insertStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    private void deleteHoliday(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Connection conn = null;
        PreparedStatement deleteStmt = null;
        
        System.out.println("========== DELETE HOLIDAY ==========");
        
        try {
            String idParam = request.getParameter("id");
            
            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect("AdminServlet?error=holiday_id_required#settings");
                return;
            }
            
            int id = Integer.parseInt(idParam);
            System.out.println("Deleting holiday ID: " + id);
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            String deleteSql = "DELETE FROM holidays WHERE id = ?";
            deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, id);
            
            int rows = deleteStmt.executeUpdate();
            System.out.println("Rows deleted: " + rows);
            
            if (rows > 0) {
                conn.commit();
                System.out.println("Holiday deleted successfully");
                response.sendRedirect("AdminServlet?success=holiday_deleted#settings");
            } else {
                conn.rollback();
                response.sendRedirect("AdminServlet?error=holiday_not_found#settings");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect("AdminServlet?error=invalid_holiday_id#settings");
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=database_error#settings");
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=unexpected_error#settings");
        } finally {
            if (deleteStmt != null) {
                try { deleteStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    // ========================================================================
    // HELPER METHODS
    // ========================================================================
    
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try {
            if (conn != null) { conn.setAutoCommit(true); conn.close(); }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}