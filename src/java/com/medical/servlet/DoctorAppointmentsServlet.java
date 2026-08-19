/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.medical.servlet;



import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/DoctorAppointmentsServlet")
public class DoctorAppointmentsServlet extends HttpServlet {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/medical_db?";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "shacika@@2006";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("doctorId") == null) {
            response.sendRedirect("Login.jsp?error=loginRequired");
            return;
        }
        
        String doctorId = (String) session.getAttribute("doctorId");
        String action = request.getParameter("action");
        
        try {
            if ("getTodayAppointments".equals(action)) {
                getTodayAppointments(request, response, doctorId);
            } else if ("getUpcomingAppointments".equals(action)) {
                getUpcomingAppointments(request, response, doctorId);
            } else if ("getAllAppointments".equals(action)) {
                getAllAppointments(request, response, doctorId);
            } else if ("getAppointmentDetails".equals(action)) {
                getAppointmentDetails(request, response);
            } else {
                // Default: Load all appointments for the dashboard
                loadDoctorDashboard(request, response, doctorId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error loading appointments: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("doctorId") == null) {
            response.sendRedirect("Login.jsp?error=loginRequired");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            if ("updateStatus".equals(action)) {
                updateAppointmentStatus(request, response);
            } else if ("complete".equals(action)) {
                completeAppointment(request, response);
            } else if ("cancel".equals(action)) {
                cancelAppointment(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error processing request: " + e.getMessage());
        }
    }
    
    private void loadDoctorDashboard(HttpServletRequest request, HttpServletResponse response, 
            String doctorId) throws Exception {
        
        Connection conn = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // Get today's appointments
            List<Map<String, String>> todayAppointments = getTodayAppointmentsData(conn, doctorId);
            
            // Get upcoming appointments
            List<Map<String, String>> upcomingAppointments = getUpcomingAppointmentsData(conn, doctorId);
            
            // Get completed appointments
            List<Map<String, String>> completedAppointments = getCompletedAppointmentsData(conn, doctorId);
            
            // Get appointment statistics
            Map<String, Integer> stats = getAppointmentStats(conn, doctorId);
            
            // Set attributes
            request.setAttribute("todayAppointments", todayAppointments);
            request.setAttribute("upcomingAppointments", upcomingAppointments);
            request.setAttribute("completedAppointments", completedAppointments);
            request.setAttribute("stats", stats);
            
            // Forward to JSP
            request.getRequestDispatcher("doctor-dashboard.jsp").forward(request, response);
            
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    private List<Map<String, String>> getTodayAppointmentsData(Connection conn, String doctorId) 
            throws SQLException {
        
        List<Map<String, String>> appointments = new ArrayList<>();
        
        String sql = "SELECT a.id, a.user_id, a.appointment_date, a.appointment_time, " +
                    "a.symptoms, a.status, a.department, " +
                    "u.fullname as patient_name, u.phone, u.dob, u.gender " +
                    "FROM appointments a " +
                    "JOIN users u ON a.user_id = u.id " +
                    "WHERE a.doctor_name = ? " +
                    "AND DATE(a.appointment_date) = CURDATE() " +
                    "AND a.status IN ('Pending', 'Confirmed') " +
                    "ORDER BY a.appointment_time ASC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, getDoctorName(conn, doctorId));
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, String> apt = new HashMap<>();
                apt.put("id", rs.getString("id"));
                apt.put("user_id", rs.getString("user_id"));
                apt.put("patient_name", rs.getString("patient_name"));
                apt.put("phone", rs.getString("phone"));
                apt.put("dob", rs.getString("dob"));
                apt.put("gender", rs.getString("gender"));
                apt.put("appointment_date", rs.getString("appointment_date"));
                apt.put("appointment_time", rs.getString("appointment_time"));
                apt.put("symptoms", rs.getString("symptoms"));
                apt.put("status", rs.getString("status"));
                apt.put("department", rs.getString("department"));
                
                appointments.add(apt);
            }
        }
        
        return appointments;
    }
    
    private List<Map<String, String>> getUpcomingAppointmentsData(Connection conn, String doctorId) 
            throws SQLException {
        
        List<Map<String, String>> appointments = new ArrayList<>();
        
        String sql = "SELECT a.id, a.user_id, a.appointment_date, a.appointment_time, " +
                    "a.symptoms, a.status, a.department, " +
                    "u.fullname as patient_name, u.phone " +
                    "FROM appointments a " +
                    "JOIN users u ON a.user_id = u.id " +
                    "WHERE a.doctor_name = ? " +
                    "AND DATE(a.appointment_date) > CURDATE() " +
                    "AND a.status IN ('Pending', 'Confirmed') " +
                    "ORDER BY a.appointment_date ASC, a.appointment_time ASC " +
                    "LIMIT 10";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, getDoctorName(conn, doctorId));
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, String> apt = new HashMap<>();
                apt.put("id", rs.getString("id"));
                apt.put("user_id", rs.getString("user_id"));
                apt.put("patient_name", rs.getString("patient_name"));
                apt.put("phone", rs.getString("phone"));
                apt.put("appointment_date", rs.getString("appointment_date"));
                apt.put("appointment_time", rs.getString("appointment_time"));
                apt.put("symptoms", rs.getString("symptoms"));
                apt.put("status", rs.getString("status"));
                apt.put("department", rs.getString("department"));
                
                appointments.add(apt);
            }
        }
        
        return appointments;
    }
    
    private List<Map<String, String>> getCompletedAppointmentsData(Connection conn, String doctorId) 
            throws SQLException {
        
        List<Map<String, String>> appointments = new ArrayList<>();
        
        String sql = "SELECT a.id, a.user_id, a.appointment_date, a.appointment_time, " +
                    "a.symptoms, a.status, a.department, " +
                    "u.fullname as patient_name " +
                    "FROM appointments a " +
                    "JOIN users u ON a.user_id = u.id " +
                    "WHERE a.doctor_name = ? " +
                    "AND a.status = 'Completed' " +
                    "ORDER BY a.appointment_date DESC, a.appointment_time DESC " +
                    "LIMIT 20";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, getDoctorName(conn, doctorId));
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, String> apt = new HashMap<>();
                apt.put("id", rs.getString("id"));
                apt.put("user_id", rs.getString("user_id"));
                apt.put("patient_name", rs.getString("patient_name"));
                apt.put("appointment_date", rs.getString("appointment_date"));
                apt.put("appointment_time", rs.getString("appointment_time"));
                apt.put("symptoms", rs.getString("symptoms"));
                apt.put("status", rs.getString("status"));
                apt.put("department", rs.getString("department"));
                
                appointments.add(apt);
            }
        }
        
        return appointments;
    }
    
    private Map<String, Integer> getAppointmentStats(Connection conn, String doctorId) 
            throws SQLException {
        
        Map<String, Integer> stats = new HashMap<>();
        String doctorName = getDoctorName(conn, doctorId);
        
        // Today's appointments
        String todaySql = "SELECT COUNT(*) as count FROM appointments " +
                         "WHERE doctor_name = ? AND DATE(appointment_date) = CURDATE() " +
                         "AND status IN ('Pending', 'Confirmed')";
        
        try (PreparedStatement stmt = conn.prepareStatement(todaySql)) {
            stmt.setString(1, doctorName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stats.put("today", rs.getInt("count"));
            }
        }
        
        // Upcoming appointments
        String upcomingSql = "SELECT COUNT(*) as count FROM appointments " +
                           "WHERE doctor_name = ? AND DATE(appointment_date) > CURDATE() " +
                           "AND status IN ('Pending', 'Confirmed')";
        
        try (PreparedStatement stmt = conn.prepareStatement(upcomingSql)) {
            stmt.setString(1, doctorName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stats.put("upcoming", rs.getInt("count"));
            }
        }
        
        // Completed appointments
        String completedSql = "SELECT COUNT(*) as count FROM appointments " +
                            "WHERE doctor_name = ? AND status = 'Completed'";
        
        try (PreparedStatement stmt = conn.prepareStatement(completedSql)) {
            stmt.setString(1, doctorName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stats.put("completed", rs.getInt("count"));
            }
        }
        
        return stats;
    }
    
    private void updateAppointmentStatus(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        String appointmentId = request.getParameter("appointmentId");
        String status = request.getParameter("status");
        
        Connection conn = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            String sql = "UPDATE appointments SET status = ?, updated_at = NOW() WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, status);
                stmt.setString(2, appointmentId);
                
                int updated = stmt.executeUpdate();
                
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": " + (updated > 0) + "}");
            }
            
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    private void completeAppointment(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        String appointmentId = request.getParameter("appointmentId");
        
        Connection conn = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            String sql = "UPDATE appointments SET status = 'Completed', updated_at = NOW() WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, appointmentId);
                
                int updated = stmt.executeUpdate();
                
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": " + (updated > 0) + "}");
            }
            
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    private void cancelAppointment(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        String appointmentId = request.getParameter("appointmentId");
        
        Connection conn = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            String sql = "UPDATE appointments SET status = 'Cancelled', updated_at = NOW() WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, appointmentId);
                
                int updated = stmt.executeUpdate();
                
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": " + (updated > 0) + "}");
            }
            
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    private String getDoctorName(Connection conn, String doctorId) throws SQLException {
        String sql = "SELECT fullname FROM users WHERE id = ? AND role = 'doctor'";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("fullname");
            }
        }
        
        return "";
    }
    
    private void getTodayAppointments(HttpServletRequest request, HttpServletResponse response, 
            String doctorId) throws Exception {
        
        Connection conn = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            List<Map<String, String>> appointments = getTodayAppointmentsData(conn, doctorId);
            
            response.setContentType("application/json");
            response.getWriter().write(convertToJSON(appointments));
            
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    private void getUpcomingAppointments(HttpServletRequest request, HttpServletResponse response, 
            String doctorId) throws Exception {
        
        Connection conn = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            List<Map<String, String>> appointments = getUpcomingAppointmentsData(conn, doctorId);
            
            response.setContentType("application/json");
            response.getWriter().write(convertToJSON(appointments));
            
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    private void getAllAppointments(HttpServletRequest request, HttpServletResponse response, 
            String doctorId) throws Exception {
        
        Connection conn = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            List<Map<String, String>> today = getTodayAppointmentsData(conn, doctorId);
            List<Map<String, String>> upcoming = getUpcomingAppointmentsData(conn, doctorId);
            List<Map<String, String>> completed = getCompletedAppointmentsData(conn, doctorId);
            
            response.setContentType("application/json");
            response.getWriter().write("{" +
                "\"today\": " + convertToJSON(today) + "," +
                "\"upcoming\": " + convertToJSON(upcoming) + "," +
                "\"completed\": " + convertToJSON(completed) +
                "}");
            
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    private void getAppointmentDetails(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        String appointmentId = request.getParameter("id");
        
        Connection conn = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            String sql = "SELECT a.*, u.fullname as patient_name, u.phone, u.email, " +
                        "u.dob, u.gender, u.address " +
                        "FROM appointments a " +
                        "JOIN users u ON a.user_id = u.id " +
                        "WHERE a.id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, appointmentId);
                
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    Map<String, String> apt = new HashMap<>();
                    apt.put("id", rs.getString("id"));
                    apt.put("user_id", rs.getString("user_id"));
                    apt.put("patient_name", rs.getString("patient_name"));
                    apt.put("phone", rs.getString("phone"));
                    apt.put("email", rs.getString("email"));
                    apt.put("dob", rs.getString("dob"));
                    apt.put("gender", rs.getString("gender"));
                    apt.put("address", rs.getString("address"));
                    apt.put("appointment_date", rs.getString("appointment_date"));
                    apt.put("appointment_time", rs.getString("appointment_time"));
                    apt.put("symptoms", rs.getString("symptoms"));
                    apt.put("status", rs.getString("status"));
                    apt.put("department", rs.getString("department"));
                    apt.put("doctor_name", rs.getString("doctor_name"));
                    
                    response.setContentType("application/json");
                    response.getWriter().write(convertMapToJSON(apt));
                }
            }
            
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    private String convertToJSON(List<Map<String, String>> list) {
        StringBuilder json = new StringBuilder("[");
        
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) json.append(",");
            json.append(convertMapToJSON(list.get(i)));
        }
        
        json.append("]");
        return json.toString();
    }
    
    private String convertMapToJSON(Map<String, String> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!first) json.append(",");
            first = false;
            
            json.append("\"").append(entry.getKey()).append("\":\"")
                .append(escapeJSON(entry.getValue())).append("\"");
        }
        
        json.append("}");
        return json.toString();
    }
    
    private String escapeJSON(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }
}