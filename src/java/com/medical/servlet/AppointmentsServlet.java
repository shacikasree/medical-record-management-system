package com.medical.servlet;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AppointmentsServlet")
public class AppointmentsServlet extends HttpServlet {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/medical_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "shacika@@2006";
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ [AppointmentsServlet] MySQL Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ [AppointmentsServlet] MySQL Driver not found!");
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("userId") == null) {
            System.out.println("❌ [GET] Session expired or user not logged in");
            response.sendRedirect("Login.jsp?error=sessionExpired");
            return;
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");
        
        System.out.println("========================================");
        System.out.println("📋 [Appointments] GET Request");
        System.out.println("   User ID: " + userId);
        System.out.println("   Role: " + role);
        
        // Check for success message
        String success = request.getParameter("success");
        if (success != null) {
            request.setAttribute("successMessage", "Appointment booked successfully!");
            System.out.println("✅ Success message set");
        }
        
        // Forward to appropriate dashboard based on role
        if ("patient".equalsIgnoreCase(role)) {
            List<Appointment> appointments = getPatientAppointments(userId);
            request.setAttribute("appointments", appointments);
            System.out.println("✅ Forwarding to Patient Dashboard with " + appointments.size() + " appointments");
            System.out.println("========================================");
            request.getRequestDispatcher("patientDash.jsp").forward(request, response);
        } else if ("doctor".equalsIgnoreCase(role)) {
            List<Appointment> appointments = getDoctorAppointments(userId);
            request.setAttribute("appointments", appointments);
            System.out.println("✅ Forwarding to Doctor Dashboard with " + appointments.size() + " appointments");
            System.out.println("========================================");
            request.getRequestDispatcher("DoctorDashboard.jsp").forward(request, response);
        } else {
            System.out.println("❌ Invalid role: " + role);
            System.out.println("========================================");
            response.sendRedirect("Login.jsp?error=invalidRole");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("========================================");
        System.out.println("📝 [Appointments] POST Request - START");
        System.out.println("========================================");
        
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("userId") == null) {
            System.out.println("❌ [POST] Session expired or user not logged in");
            response.sendRedirect("Login.jsp?error=sessionExpired");
            return;
        }
        
        try {
            // Get patient ID from session
            Integer patientId = (Integer) session.getAttribute("userId");
            System.out.println("👤 Patient ID from session: " + patientId);
            
            // Get ALL form parameters and log them
            System.out.println("\n📋 FORM PARAMETERS RECEIVED:");
            java.util.Enumeration<String> paramNames = request.getParameterNames();
            while(paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String paramValue = request.getParameter(paramName);
                System.out.println("   " + paramName + " = '" + paramValue + "'");
            }
            
            // Get form parameters
            String doctorIdStr = request.getParameter("doctor_id");
            String doctorName = request.getParameter("doctor_name");
            String department = request.getParameter("department");
            String appointmentDate = request.getParameter("appointment_date");
            String appointmentTime = request.getParameter("appointment_time");
            String symptoms = request.getParameter("symptoms");
            
            System.out.println("\n📝 EXTRACTED VALUES:");
            System.out.println("   Patient ID: " + patientId);
            System.out.println("   Doctor ID String: '" + doctorIdStr + "'");
            System.out.println("   Doctor Name: '" + doctorName + "'");
            System.out.println("   Department: '" + department + "'");
            System.out.println("   Date: '" + appointmentDate + "'");
            System.out.println("   Time: '" + appointmentTime + "'");
            System.out.println("   Symptoms: '" + symptoms + "'");
            
            // Validate required fields
            if (doctorIdStr == null || doctorIdStr.trim().isEmpty()) {
                System.out.println("❌ VALIDATION FAILED: doctor_id is null or empty");
                response.sendRedirect("PatientAppoinment.jsp?error=missingDoctorId");
                return;
            }
            
            if (doctorName == null || doctorName.trim().isEmpty()) {
                System.out.println("❌ VALIDATION FAILED: doctor_name is null or empty");
                response.sendRedirect("PatientAppoinment.jsp?error=missingDoctorName");
                return;
            }
            
            if (appointmentDate == null || appointmentDate.trim().isEmpty()) {
                System.out.println("❌ VALIDATION FAILED: appointment_date is null or empty");
                response.sendRedirect("PatientAppoinment.jsp?error=missingDate");
                return;
            }
            
            if (appointmentTime == null || appointmentTime.trim().isEmpty()) {
                System.out.println("❌ VALIDATION FAILED: appointment_time is null or empty");
                response.sendRedirect("PatientAppoinment.jsp?error=missingTime");
                return;
            }
            
            System.out.println("✅ All required fields present");
            
            // Parse doctor ID
            int doctorId;
            try {
                doctorId = Integer.parseInt(doctorIdStr.trim());
                System.out.println("✅ Doctor ID parsed: " + doctorId);
            } catch(NumberFormatException e) {
                System.out.println("❌ PARSE ERROR: Cannot parse doctor_id '" + doctorIdStr + "' to integer");
                e.printStackTrace();
                response.sendRedirect("PatientAppoinment.jsp?error=invalidDoctorId");
                return;
            }
            
            // Validate date is not in the past
            LocalDate selectedDate = LocalDate.parse(appointmentDate);
            LocalDate today = LocalDate.now();
            System.out.println("📅 Selected date: " + selectedDate + ", Today: " + today);
            
            if (selectedDate.isBefore(today)) {
                System.out.println("❌ VALIDATION FAILED: Date " + selectedDate + " is before today " + today);
                response.sendRedirect("PatientAppoinment.jsp?error=pastDate");
                return;
            }
            
            System.out.println("✅ Date validation passed");
            
            // Check if appointment slot is available
            System.out.println("\n🔍 Checking if slot is available...");
            if (isSlotTaken(doctorId, appointmentDate, appointmentTime)) {
                System.out.println("❌ SLOT TAKEN: Time slot already booked");
                response.sendRedirect("PatientAppoinment.jsp?error=slotTaken");
                return;
            }
            
            System.out.println("✅ Slot is available");
            
            // Get patient details
            System.out.println("\n👤 Fetching patient information...");
            PatientInfo patientInfo = getPatientInfo(patientId);
            if (patientInfo == null) {
                System.out.println("❌ ERROR: Could not fetch patient info for ID " + patientId);
                response.sendRedirect("PatientAppoinment.jsp?error=patientNotFound");
                return;
            }
            
            System.out.println("✅ Patient info retrieved:");
            System.out.println("   Name: " + patientInfo.name);
            System.out.println("   Email: " + patientInfo.email);
            System.out.println("   Phone: " + patientInfo.phone);
            System.out.println("   Age: " + patientInfo.age);
            System.out.println("   Gender: " + patientInfo.gender);
            
            // Book the appointment
            System.out.println("\n💾 Attempting to book appointment...");
            boolean success = bookAppointment(patientId, patientInfo, doctorId, doctorName, 
                                             department, appointmentDate, appointmentTime, symptoms);
            
            if (success) {
                System.out.println("✅✅✅ APPOINTMENT BOOKED SUCCESSFULLY! ✅✅✅");
                System.out.println("========================================");
                response.sendRedirect("PatientServlet?success=booked");
            } else {
                System.out.println("❌❌❌ BOOKING FAILED! ❌❌❌");
                System.out.println("========================================");
                response.sendRedirect("PatientAppoinment.jsp?error=bookingFailed");
            }
            
        } catch (Exception e) {
            System.err.println("❌❌❌ EXCEPTION OCCURRED! ❌❌❌");
            System.err.println("Exception Type: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            System.err.println("Stack Trace:");
            e.printStackTrace();
            System.out.println("========================================");
            response.sendRedirect("PatientAppoinment.jsp?error=systemError");
        }
    }
    
    // Get patient info from database
    private PatientInfo getPatientInfo(int patientId) {
        // First detect the correct ID column
        String idColumn = detectIdColumn();
        
        String query = "SELECT fullname, email, phone, age, gender FROM users WHERE " + idColumn + " = ?";
        
        System.out.println("   SQL: " + query);
        System.out.println("   Parameter: " + idColumn + " = " + patientId);
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                PatientInfo info = new PatientInfo();
                info.name = rs.getString("fullname");
                info.email = rs.getString("email");
                info.phone = rs.getString("phone");
                info.age = rs.getInt("age");
                info.gender = rs.getString("gender");
                System.out.println("   ✅ Patient found in database");
                System.out.println("      Name: " + info.name);
                System.out.println("      Email: " + info.email);
                System.out.println("      Phone: " + info.phone);
                return info;
            } else {
                System.out.println("   ❌ No patient found with " + idColumn + " = " + patientId);
            }
            
        } catch (SQLException e) {
            System.err.println("   ❌ SQL Error in getPatientInfo:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Detect whether to use 'id' or 'user_id'
    private String detectIdColumn() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, "users", null);
            
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                if ("user_id".equalsIgnoreCase(columnName)) {
                    System.out.println("   🔍 Using column: user_id");
                    return "user_id";
                }
            }
            
            System.out.println("   🔍 Using column: id");
            return "id";
            
        } catch (SQLException e) {
            System.out.println("   ⚠️ Column detection failed, defaulting to user_id");
            return "user_id";
        }
    }
    
    // Check if time slot is already taken
    private boolean isSlotTaken(int doctorId, String date, String time) {
        String query = "SELECT COUNT(*) FROM appointments " +
                      "WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? " +
                      "AND status NOT IN ('cancelled')";
        
        System.out.println("   SQL: " + query);
        System.out.println("   Parameters: doctor_id=" + doctorId + ", date=" + date + ", time=" + time);
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, doctorId);
            stmt.setString(2, date);
            stmt.setString(3, time);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("   Existing appointments found: " + count);
                return count > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("   ❌ SQL Error in isSlotTaken:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Book a new appointment
    private boolean bookAppointment(int patientId, PatientInfo patientInfo, int doctorId, 
                                   String doctorName, String department, String date, 
                                   String time, String symptoms) {
        
        String query = "INSERT INTO appointments " +
                      "(patient_id, patient_name, patient_email, patient_phone, patient_age, " +
                      "patient_gender, doctor_id, doctor_name, department, appointment_date, " +
                      "appointment_time, symptoms, status, created_at) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'pending', NOW())";
        
        System.out.println("   SQL Query: " + query);
        System.out.println("   Parameters:");
        System.out.println("      1. patient_id = " + patientId);
        System.out.println("      2. patient_name = '" + patientInfo.name + "'");
        System.out.println("      3. patient_email = '" + patientInfo.email + "'");
        System.out.println("      4. patient_phone = '" + patientInfo.phone + "'");
        System.out.println("      5. patient_age = " + patientInfo.age);
        System.out.println("      6. patient_gender = '" + patientInfo.gender + "'");
        System.out.println("      7. doctor_id = " + doctorId);
        System.out.println("      8. doctor_name = '" + doctorName + "'");
        System.out.println("      9. department = '" + department + "'");
        System.out.println("      10. appointment_date = '" + date + "'");
        System.out.println("      11. appointment_time = '" + time + "'");
        System.out.println("      12. symptoms = '" + (symptoms != null ? symptoms : "No symptoms") + "'");
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("   ✅ Database connection established");
            
            stmt = conn.prepareStatement(query);
            
            stmt.setInt(1, patientId);
            stmt.setString(2, patientInfo.name);
            stmt.setString(3, patientInfo.email);
            stmt.setString(4, patientInfo.phone);
            stmt.setInt(5, patientInfo.age);
            stmt.setString(6, patientInfo.gender);
            stmt.setInt(7, doctorId);
            stmt.setString(8, doctorName);
            stmt.setString(9, department);
            stmt.setString(10, date);
            stmt.setString(11, time);
            stmt.setString(12, symptoms != null && !symptoms.trim().isEmpty() ? symptoms : "No symptoms provided");
            
            System.out.println("   ⏳ Executing INSERT query...");
            int rows = stmt.executeUpdate();
            
            System.out.println("   📊 Rows affected: " + rows);
            
            if (rows > 0) {
                System.out.println("   ✅✅✅ INSERT SUCCESSFUL! ✅✅✅");
                
                // Verify the insert
                String verifyQuery = "SELECT * FROM appointments WHERE patient_id = ? AND doctor_id = ? " +
                                   "AND appointment_date = ? AND appointment_time = ? ORDER BY id DESC LIMIT 1";
                PreparedStatement verifyStmt = conn.prepareStatement(verifyQuery);
                verifyStmt.setInt(1, patientId);
                verifyStmt.setInt(2, doctorId);
                verifyStmt.setString(3, date);
                verifyStmt.setString(4, time);
                ResultSet verifyRs = verifyStmt.executeQuery();
                
                if(verifyRs.next()) {
                    System.out.println("   ✅ VERIFICATION: Appointment found in database!");
                    System.out.println("      Appointment ID: " + verifyRs.getInt("id"));
                    System.out.println("      Patient: " + verifyRs.getString("patient_name"));
                    System.out.println("      Doctor: " + verifyRs.getString("doctor_name"));
                    System.out.println("      Status: " + verifyRs.getString("status"));
                } else {
                    System.out.println("   ⚠️ WARNING: Insert reported success but cannot find appointment!");
                }
                
                verifyRs.close();
                verifyStmt.close();
                
                return true;
            } else {
                System.out.println("   ❌ INSERT FAILED: No rows affected");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("   ❌❌❌ SQL EXCEPTION! ❌❌❌");
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("   Error Code: " + e.getErrorCode());
            System.err.println("   Message: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(stmt != null) stmt.close();
                if(conn != null) conn.close();
                System.out.println("   ✅ Database connection closed");
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Get appointments for a patient
    private List<Appointment> getPatientAppointments(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        
        String query = "SELECT id, doctor_name, department, appointment_date, " +
                      "appointment_time, symptoms, status, created_at " +
                      "FROM appointments " +
                      "WHERE patient_id = ? " +
                      "ORDER BY appointment_date DESC, appointment_time DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Appointment apt = new Appointment();
                apt.appointmentId = rs.getInt("id");
                apt.doctorName = rs.getString("doctor_name");
                apt.department = rs.getString("department");
                apt.appointmentDate = rs.getString("appointment_date");
                apt.appointmentTime = rs.getString("appointment_time");
                apt.symptoms = rs.getString("symptoms");
                apt.status = rs.getString("status");
                apt.createdAt = rs.getTimestamp("created_at");
                appointments.add(apt);
            }
            
            System.out.println("✅ [getPatientAppointments] Found " + appointments.size() + " appointments for patient " + patientId);
            
        } catch (SQLException e) {
            System.err.println("❌ [getPatientAppointments] Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    // Get appointments for a doctor
    private List<Appointment> getDoctorAppointments(int doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        
        String query = "SELECT id, patient_name, patient_email, patient_phone, patient_age, " +
                      "patient_gender, department, appointment_date, appointment_time, " +
                      "symptoms, status, created_at " +
                      "FROM appointments " +
                      "WHERE doctor_id = ? " +
                      "ORDER BY appointment_date DESC, appointment_time DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Appointment apt = new Appointment();
                apt.appointmentId = rs.getInt("id");
                apt.patientName = rs.getString("patient_name");
                apt.patientEmail = rs.getString("patient_email");
                apt.patientPhone = rs.getString("patient_phone");
                apt.patientAge = rs.getInt("patient_age");
                apt.patientGender = rs.getString("patient_gender");
                apt.department = rs.getString("department");
                apt.appointmentDate = rs.getString("appointment_date");
                apt.appointmentTime = rs.getString("appointment_time");
                apt.symptoms = rs.getString("symptoms");
                apt.status = rs.getString("status");
                apt.createdAt = rs.getTimestamp("created_at");
                appointments.add(apt);
            }
            
            System.out.println("✅ [getDoctorAppointments] Found " + appointments.size() + " appointments for doctor " + doctorId);
            
        } catch (SQLException e) {
            System.err.println("❌ [getDoctorAppointments] Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    // Inner class for Patient Info
    private static class PatientInfo {
        String name;
        String email;
        String phone;
        int age;
        String gender;
    }
    
    // Inner class for Appointment data
    public static class Appointment {
        public int appointmentId;
        public String patientName;
        public String patientEmail;
        public String patientPhone;
        public int patientAge;
        public String patientGender;
        public String doctorName;
        public String department;
        public String appointmentDate;
        public String appointmentTime;
        public String symptoms;
        public String status;
        public Timestamp createdAt;
    }
}