package com.medical.servlet;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.google.gson.Gson;

@WebServlet("/DoctorAvailabilityServlet")
public class DoctorAvailabilityServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"doctor".equals(session.getAttribute("role"))) {
            response.sendRedirect("Login.jsp?error=unauthorized");
            return;
        }

        Integer doctorId = (Integer) session.getAttribute("doctorId");
        if (doctorId == null) {
            doctorId = (Integer) session.getAttribute("userId");
        }

        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // Load weekly schedule
            List<Map<String, String>> weeklySchedule = loadWeeklySchedule(conn, doctorId);
            
            // Load unavailable dates
            List<Map<String, String>> unavailableDates = loadUnavailableDates(conn, doctorId);
            
            // Load emergency status
            boolean emergencyAvailable = getEmergencyStatus(conn, doctorId);
            
            // Set attributes
            request.setAttribute("weeklySchedule", weeklySchedule);
            request.setAttribute("unavailableDates", unavailableDates);
            request.setAttribute("emergencyAvailable", emergencyAvailable);
            
            System.out.println("✅ Availability data loaded:");
            System.out.println("   Weekly schedule: " + weeklySchedule.size() + " days");
            System.out.println("   Unavailable dates: " + unavailableDates.size());
            System.out.println("   Emergency available: " + emergencyAvailable);
            
            // Forward back to doctor servlet to reload full page
            request.getRequestDispatcher("DoctorServlet").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("DoctorServlet?error=loadFailed");
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"doctor".equals(session.getAttribute("role"))) {
            response.sendRedirect("Login.jsp?error=unauthorized");
            return;
        }

        Integer doctorId = (Integer) session.getAttribute("doctorId");
        if (doctorId == null) {
            doctorId = (Integer) session.getAttribute("userId");
        }

        String action = request.getParameter("action");
        
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            
            if ("saveSchedule".equals(action)) {
                saveWeeklySchedule(request, conn, doctorId);
                response.sendRedirect("DoctorServlet?success=scheduleUpdated");
                
            } else if ("addUnavailableDate".equals(action)) {
                addUnavailableDate(request, conn, doctorId);
                response.sendRedirect("DoctorServlet?success=dateAdded");
                
            } else if ("removeUnavailableDate".equals(action)) {
                removeUnavailableDate(request, conn, doctorId);
                response.sendRedirect("DoctorServlet?success=dateRemoved");
                
            } else if ("toggleEmergency".equals(action)) {
                toggleEmergencyStatus(request, conn, doctorId);
                response.sendRedirect("DoctorServlet?success=emergencyUpdated");
                
            } else {
                response.sendRedirect("DoctorServlet?error=invalidAction");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("DoctorServlet?error=saveFailed");
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<Map<String, String>> loadWeeklySchedule(Connection conn, int doctorId) 
            throws SQLException {
        
        List<Map<String, String>> schedule = new ArrayList<>();
        
        // Create table if not exists
        String createTable = "CREATE TABLE IF NOT EXISTS doctor_schedule (" +
                           "id INT PRIMARY KEY AUTO_INCREMENT, " +
                           "doctor_id INT NOT NULL, " +
                           "day_name VARCHAR(20) NOT NULL, " +
                           "is_active BOOLEAN DEFAULT true, " +
                           "start_time TIME, " +
                           "end_time TIME, " +
                           "UNIQUE KEY unique_doctor_day (doctor_id, day_name))";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTable);
        }
        
        // Get existing schedule
        String sql = "SELECT day_name, is_active, start_time, end_time " +
                    "FROM doctor_schedule WHERE doctor_id = ?";
        
        Map<String, Map<String, String>> scheduleMap = new HashMap<>();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, String> day = new HashMap<>();
                day.put("day_name", rs.getString("day_name"));
                day.put("is_active", String.valueOf(rs.getBoolean("is_active")));
                day.put("start_time", rs.getString("start_time"));
                day.put("end_time", rs.getString("end_time"));
                scheduleMap.put(rs.getString("day_name"), day);
            }
        }
        
        // Fill in default schedule for missing days
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        
        for (String day : days) {
            if (scheduleMap.containsKey(day)) {
                schedule.add(scheduleMap.get(day));
            } else {
                Map<String, String> defaultDay = new HashMap<>();
                defaultDay.put("day_name", day);
                
                if ("Sunday".equals(day)) {
                    defaultDay.put("is_active", "false");
                    defaultDay.put("start_time", "");
                    defaultDay.put("end_time", "");
                } else if ("Saturday".equals(day)) {
                    defaultDay.put("is_active", "true");
                    defaultDay.put("start_time", "09:00");
                    defaultDay.put("end_time", "13:00");
                } else {
                    defaultDay.put("is_active", "true");
                    defaultDay.put("start_time", "09:00");
                    defaultDay.put("end_time", "17:00");
                }
                
                schedule.add(defaultDay);
            }
        }
        
        return schedule;
    }

    private List<Map<String, String>> loadUnavailableDates(Connection conn, int doctorId) 
            throws SQLException {
        
        List<Map<String, String>> dates = new ArrayList<>();
        
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
        
        String sql = "SELECT id, unavailable_date, reason " +
                    "FROM doctor_unavailable_dates " +
                    "WHERE doctor_id = ? AND unavailable_date >= CURDATE() " +
                    "ORDER BY unavailable_date";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, String> date = new HashMap<>();
                date.put("id", String.valueOf(rs.getInt("id")));
                date.put("date", rs.getString("unavailable_date"));
                date.put("reason", rs.getString("reason"));
                dates.add(date);
            }
        }
        
        return dates;
    }

    private boolean getEmergencyStatus(Connection conn, int doctorId) throws SQLException {
        
        // Create table if not exists
        String createTable = "CREATE TABLE IF NOT EXISTS doctor_settings (" +
                           "doctor_id INT PRIMARY KEY, " +
                           "emergency_available BOOLEAN DEFAULT true, " +
                           "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTable);
        }
        
        String sql = "SELECT emergency_available FROM doctor_settings WHERE doctor_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean("emergency_available");
            }
        }
        
        // Default to true if not set
        return true;
    }

    private void saveWeeklySchedule(HttpServletRequest request, Connection conn, int doctorId) 
            throws SQLException {
        
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        
        String sql = "INSERT INTO doctor_schedule (doctor_id, day_name, is_active, start_time, end_time) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE is_active=?, start_time=?, end_time=?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String day : days) {
                boolean isActive = "on".equals(request.getParameter(day + "_active"));
                String startTime = request.getParameter(day + "_start");
                String endTime = request.getParameter(day + "_end");
                
                pstmt.setInt(1, doctorId);
                pstmt.setString(2, day);
                pstmt.setBoolean(3, isActive);
                pstmt.setString(4, isActive && startTime != null ? startTime : null);
                pstmt.setString(5, isActive && endTime != null ? endTime : null);
                pstmt.setBoolean(6, isActive);
                pstmt.setString(7, isActive && startTime != null ? startTime : null);
                pstmt.setString(8, isActive && endTime != null ? endTime : null);
                
                pstmt.executeUpdate();
            }
        }
        
        System.out.println("✅ Weekly schedule saved for doctor ID: " + doctorId);
    }

    private void addUnavailableDate(HttpServletRequest request, Connection conn, int doctorId) 
            throws SQLException {
        
        String date = request.getParameter("unavailableDate");
        String reason = request.getParameter("reason");
        
        String sql = "INSERT INTO doctor_unavailable_dates (doctor_id, unavailable_date, reason) " +
                    "VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setString(2, date);
            pstmt.setString(3, reason != null ? reason : "Not available");
            pstmt.executeUpdate();
        }
        
        System.out.println("✅ Unavailable date added: " + date);
    }

    private void removeUnavailableDate(HttpServletRequest request, Connection conn, int doctorId) 
            throws SQLException {
        
        int dateId = Integer.parseInt(request.getParameter("dateId"));
        
        String sql = "DELETE FROM doctor_unavailable_dates WHERE id = ? AND doctor_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dateId);
            pstmt.setInt(2, doctorId);
            pstmt.executeUpdate();
        }
        
        System.out.println("✅ Unavailable date removed: ID " + dateId);
    }

    private void toggleEmergencyStatus(HttpServletRequest request, Connection conn, int doctorId) 
            throws SQLException {
        
        boolean emergencyAvailable = "true".equals(request.getParameter("emergencyAvailable"));
        
        String sql = "INSERT INTO doctor_settings (doctor_id, emergency_available) " +
                    "VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE emergency_available = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setBoolean(2, emergencyAvailable);
            pstmt.setBoolean(3, emergencyAvailable);
            pstmt.executeUpdate();
        }
        
        System.out.println("✅ Emergency status updated: " + emergencyAvailable);
    }
}