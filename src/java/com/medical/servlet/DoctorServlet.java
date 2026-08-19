package com.medical.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/DoctorServlet")
public class DoctorServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("========================================");
        System.out.println("👨‍⚕️ [DoctorServlet] Loading Doctor Dashboard");
        
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

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            
            // Get doctor ID from session - try both userId and doctorId
            Integer doctorId = (Integer) session.getAttribute("doctorId");
            if (doctorId == null) {
                doctorId = (Integer) session.getAttribute("userId");
            }
            
            String department = (String) session.getAttribute("department");
            
            System.out.println("✅ Session loaded:");
            System.out.println("   Doctor ID: " + doctorId);
            System.out.println("   Department: " + department);
            System.out.println("   Role: " + role);

            // Get today's date
            String today = LocalDate.now().toString();
            System.out.println("📅 Today's date: " + today);

            // ========== 1. Load Today's Appointments ==========
            System.out.println("\n🔍 Loading Today's Appointments...");
            
            // Appointments table already has all patient data stored
            String sql1 = "SELECT id, patient_id, patient_name, patient_email, " +
                         "patient_phone, patient_age, patient_gender, " +
                         "appointment_date, appointment_time, symptoms, status, " +
                         "consultation_type, chief_complaint, diagnosis, notes, " +
                         "created_at " +
                         "FROM appointments " +
                         "WHERE doctor_id = ? AND appointment_date = ? " +
                         "ORDER BY appointment_time ASC";
            
            pstmt = conn.prepareStatement(sql1);
            pstmt.setInt(1, doctorId);
            pstmt.setString(2, today);
            
            System.out.println("   Query: " + sql1);
            System.out.println("   Parameters: doctorId=" + doctorId + ", date=" + today);
            
            rs = pstmt.executeQuery();
            
            List<Map<String, String>> todayAppointments = new ArrayList<>();
            int todayCount = 0;
            while (rs.next()) {
                todayCount++;
                Map<String, String> apt = new HashMap<>();
                apt.put("id", String.valueOf(rs.getInt("id")));
                apt.put("patient_id", String.valueOf(rs.getInt("patient_id")));
                apt.put("patient_name", rs.getString("patient_name"));
                apt.put("patient_email", rs.getString("patient_email"));
                apt.put("patient_phone", rs.getString("patient_phone"));
                apt.put("patient_age", String.valueOf(rs.getInt("patient_age")));
                apt.put("patient_gender", rs.getString("patient_gender"));
                apt.put("appointment_date", rs.getString("appointment_date"));
                apt.put("appointment_time", rs.getString("appointment_time"));
                apt.put("symptoms", rs.getString("symptoms"));
                apt.put("status", rs.getString("status"));
                apt.put("type", rs.getString("consultation_type"));
                apt.put("chief_complaint", rs.getString("chief_complaint"));
                apt.put("diagnosis", rs.getString("diagnosis"));
                apt.put("notes", rs.getString("notes"));
                apt.put("created_at", rs.getString("created_at"));
                
                todayAppointments.add(apt);
                
                System.out.println("   📋 Appointment #" + todayCount + ":");
                System.out.println("      Patient: " + apt.get("patient_name"));
                System.out.println("      Time: " + apt.get("appointment_time"));
                System.out.println("      Status: " + apt.get("status"));
                System.out.println("      Symptoms: " + apt.get("symptoms"));
            }
            rs.close();
            pstmt.close();
            
            System.out.println("✅ Today's appointments loaded: " + todayAppointments.size());

            // ========== 2. Load All Appointments ==========
            System.out.println("\n🔍 Loading All Appointments...");
            
            String sql2 = "SELECT id, patient_id, patient_name, patient_email, " +
                         "patient_phone, patient_age, patient_gender, " +
                         "appointment_date, appointment_time, symptoms, status, " +
                         "consultation_type, chief_complaint, created_at " +
                         "FROM appointments " +
                         "WHERE doctor_id = ? " +
                         "ORDER BY appointment_date DESC, appointment_time DESC " +
                         "LIMIT 50";
            
            pstmt = conn.prepareStatement(sql2);
            pstmt.setInt(1, doctorId);
            rs = pstmt.executeQuery();
            
            List<Map<String, String>> allAppointments = new ArrayList<>();
            while (rs.next()) {
                Map<String, String> apt = new HashMap<>();
                apt.put("id", String.valueOf(rs.getInt("id")));
                apt.put("patient_id", String.valueOf(rs.getInt("patient_id")));
                apt.put("patient_name", rs.getString("patient_name"));
                apt.put("patient_email", rs.getString("patient_email"));
                apt.put("patient_phone", rs.getString("patient_phone"));
                apt.put("patient_age", String.valueOf(rs.getInt("patient_age")));
                apt.put("patient_gender", rs.getString("patient_gender"));
                apt.put("appointment_date", rs.getString("appointment_date"));
                apt.put("appointment_time", rs.getString("appointment_time"));
                apt.put("symptoms", rs.getString("symptoms"));
                apt.put("status", rs.getString("status"));
                apt.put("type", rs.getString("consultation_type"));
                apt.put("chief_complaint", rs.getString("chief_complaint"));
                apt.put("created_at", rs.getString("created_at"));
                
                allAppointments.add(apt);
            }
            rs.close();
            pstmt.close();
            
            System.out.println("✅ All appointments loaded: " + allAppointments.size());

            // ========== 3. Load My Patients ==========
            System.out.println("\n🔍 Loading My Patients...");
            
            // Get unique patients who have appointments with this doctor
            String sql3 = "SELECT DISTINCT u.id, u.fullname, u.age, u.gender, u.phone, " +
                         "u.email, u.blood_group, " +
                         "(SELECT MAX(a.appointment_date) FROM appointments a " +
                         "WHERE a.patient_id = u.id AND a.doctor_id = ?) as last_visit, " +
                         "(SELECT COUNT(*) FROM appointments a " +
                         "WHERE a.patient_id = u.id AND a.doctor_id = ?) as visit_count " +
                         "FROM users u " +
                         "INNER JOIN appointments a ON u.id = a.patient_id " +
                         "WHERE a.doctor_id = ? AND u.role = 'patient' " +
                         "ORDER BY u.fullname";
            
            pstmt = conn.prepareStatement(sql3);
            pstmt.setInt(1, doctorId);
            pstmt.setInt(2, doctorId);
            pstmt.setInt(3, doctorId);
            rs = pstmt.executeQuery();
            
            List<Map<String, String>> myPatients = new ArrayList<>();
            while (rs.next()) {
                Map<String, String> patient = new HashMap<>();
                patient.put("id", String.valueOf(rs.getInt("id")));
                patient.put("fullname", rs.getString("fullname"));
                patient.put("age", String.valueOf(rs.getInt("age")));
                patient.put("gender", rs.getString("gender"));
                patient.put("phone", rs.getString("phone"));
                patient.put("email", rs.getString("email"));
                patient.put("blood_group", rs.getString("blood_group"));
                patient.put("last_visit", rs.getString("last_visit"));
                patient.put("visit_count", String.valueOf(rs.getInt("visit_count")));
                myPatients.add(patient);
            }
            rs.close();
            pstmt.close();
            
            System.out.println("✅ Patients loaded: " + myPatients.size());

            // ========== 4. Load My Prescriptions ==========
            System.out.println("\n🔍 Loading My Prescriptions...");
            
            // Check if prescriptions table exists, if not skip this
            List<Map<String, String>> myPrescriptions = new ArrayList<>();
            
            try {
                String sql4 = "SELECT p.id, u.fullname as patient_name, " +
                             "p.diagnosis, p.medicine_name, p.dosage, p.frequency, p.duration, " +
                             "p.instructions, p.prescribed_date " +
                             "FROM prescriptions p " +
                             "INNER JOIN users u ON p.patient_id = u.id " +
                             "WHERE p.doctor_id = ? " +
                             "ORDER BY p.prescribed_date DESC " +
                             "LIMIT 30";
                
                pstmt = conn.prepareStatement(sql4);
                pstmt.setInt(1, doctorId);
                rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Map<String, String> rx = new HashMap<>();
                    rx.put("id", String.valueOf(rs.getInt("id")));
                    rx.put("patient_name", rs.getString("patient_name"));
                    rx.put("diagnosis", rs.getString("diagnosis"));
                    rx.put("medicine_name", rs.getString("medicine_name") != null ? rs.getString("medicine_name") : "N/A");
                    rx.put("dosage", rs.getString("dosage") != null ? rs.getString("dosage") : "N/A");
                    rx.put("frequency", rs.getString("frequency") != null ? rs.getString("frequency") : "N/A");
                    rx.put("duration", rs.getString("duration") != null ? rs.getString("duration") : "N/A");
                    rx.put("instructions", rs.getString("instructions"));
                    rx.put("prescribed_date", rs.getString("prescribed_date"));
                    myPrescriptions.add(rx);
                }
                rs.close();
                pstmt.close();
            } catch (Exception e) {
                System.out.println("⚠️ Prescriptions table not found or error loading: " + e.getMessage());
            }
            
            System.out.println("✅ Prescriptions loaded: " + myPrescriptions.size());

            // ========== 5. Load Availability Data ==========
            System.out.println("\n🔍 Loading Availability Data...");
            
            List<Map<String, String>> weeklySchedule = loadWeeklySchedule(conn, doctorId);
            List<Map<String, String>> unavailableDates = loadUnavailableDates(conn, doctorId);
            boolean emergencyAvailable = getEmergencyStatus(conn, doctorId);
            
            request.setAttribute("weeklySchedule", weeklySchedule);
            request.setAttribute("unavailableDates", unavailableDates);
            request.setAttribute("emergencyAvailable", emergencyAvailable);
            
            System.out.println("✅ Availability data loaded:");
            System.out.println("   Weekly schedule: " + weeklySchedule.size() + " days");
            System.out.println("   Unavailable dates: " + unavailableDates.size());
            System.out.println("   Emergency available: " + emergencyAvailable);

            // ========== Set Request Attributes ==========
            request.setAttribute("todayAppointments", todayAppointments);
            request.setAttribute("allAppointments", allAppointments);
            request.setAttribute("myPatients", myPatients);
            request.setAttribute("myPrescriptions", myPrescriptions);

            System.out.println("\n📊 SUMMARY:");
            System.out.println("   ✅ Today's Appointments: " + todayAppointments.size());
            System.out.println("   ✅ All Appointments: " + allAppointments.size());
            System.out.println("   ✅ My Patients: " + myPatients.size());
            System.out.println("   ✅ My Prescriptions: " + myPrescriptions.size());
            System.out.println("   ✅ Weekly Schedule: " + weeklySchedule.size());
            System.out.println("   ✅ Unavailable Dates: " + unavailableDates.size());
            System.out.println("========================================");

            // Forward to JSP
            request.getRequestDispatcher("doctor.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌❌❌ Error loading doctor dashboard: " + e.getMessage());
            System.out.println("   Stack trace printed above");
            System.out.println("========================================");
            response.sendRedirect("Login.jsp?error=systemError");
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
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    // ========================================================================
    // AVAILABILITY MANAGEMENT METHODS
    // ========================================================================
    
    /**
     * Load weekly schedule from database
     */
    private List<Map<String, String>> loadWeeklySchedule(Connection conn, int doctorId) 
            throws Exception {
        
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
    
    /**
     * Load unavailable dates from database
     */
    private List<Map<String, String>> loadUnavailableDates(Connection conn, int doctorId) 
            throws Exception {
        
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
    
    /**
     * Get emergency availability status
     */
    private boolean getEmergencyStatus(Connection conn, int doctorId) throws Exception {
        
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
}