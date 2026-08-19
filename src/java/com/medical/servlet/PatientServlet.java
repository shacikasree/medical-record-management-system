package com.medical.servlet;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/PatientServlet")
public class PatientServlet extends HttpServlet {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/medical_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "shacika@@2006";
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ [PatientServlet] MySQL Driver loaded");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("userId") == null) {
            System.out.println("❌ [PatientServlet] No session or userId");
            response.sendRedirect("Login.jsp?error=sessionExpired");
            return;
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        String fullname = (String) session.getAttribute("fullname");
        String email = (String) session.getAttribute("email");
        
        System.out.println("========================================");
        System.out.println("📋 [PatientServlet] Loading patient dashboard");
        System.out.println("   User ID: " + userId);
        System.out.println("   Fullname: " + fullname);
        System.out.println("   Email: " + email);
        
        // Handle success/error messages
        String success = request.getParameter("success");
        String error = request.getParameter("error");
        
        if ("booked".equals(success)) {
            request.setAttribute("successMessage", "✅ Appointment booked successfully!");
            System.out.println("✅ Appointment booked message");
        } else if ("cancelled".equals(success)) {
            request.setAttribute("successMessage", "✅ Appointment cancelled successfully!");
            System.out.println("✅ Appointment cancelled message");
        } else if ("completed".equals(success)) {
            request.setAttribute("successMessage", "✅ Appointment marked as completed!");
            System.out.println("✅ Appointment completed message");
        } else if ("profileUpdated".equals(success)) {
            request.setAttribute("successMessage", "✅ Profile updated successfully!");
            System.out.println("✅ Profile updated message");
        }
        
        if (error != null) {
            if ("updateFailed".equals(error)) {
                request.setAttribute("errorMessage", "❌ Failed to update profile. Please try again.");
            } else {
                request.setAttribute("errorMessage", "❌ An error occurred. Please try again.");
            }
            System.out.println("❌ Error: " + error);
        }
        
        // Fetch user profile details
        Map<String, String> userProfile = getUserProfile(userId);
        request.setAttribute("userProfile", userProfile);
        
        // Fetch upcoming appointments (pending/scheduled)
        List<Map<String, String>> upcomingApts = getUpcomingAppointments(userId);
        request.setAttribute("upcomingAppointments", upcomingApts);
        System.out.println("✅ Upcoming appointments: " + upcomingApts.size());
        
        // Fetch completed appointments
        List<Map<String, String>> completedApts = getCompletedAppointments(userId);
        request.setAttribute("completedAppointments", completedApts);
        System.out.println("✅ Completed appointments: " + completedApts.size());
        
        // Fetch cancelled appointments
        List<Map<String, String>> cancelledApts = getCancelledAppointments(userId);
        request.setAttribute("cancelledAppointments", cancelledApts);
        System.out.println("✅ Cancelled appointments: " + cancelledApts.size());
        
        // Fetch active prescriptions
        List<Map<String, String>> activePrescriptions = getActivePrescriptions(userId);
        request.setAttribute("activePrescriptions", activePrescriptions);
        System.out.println("✅ Active prescriptions: " + activePrescriptions.size());
        
        // Fetch completed/expired prescriptions
        List<Map<String, String>> completedPrescriptions = getCompletedPrescriptions(userId);
        request.setAttribute("completedPrescriptions", completedPrescriptions);
        System.out.println("✅ Completed prescriptions: " + completedPrescriptions.size());
        
        System.out.println("✅ Forwarding to patientDash.jsp");
        System.out.println("========================================");
        
        // Forward to JSP
        request.getRequestDispatcher("patientDash.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("Login.jsp?error=sessionExpired");
            return;
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        
        // Get form parameters
        String phone = request.getParameter("phone");
        String dateOfBirth = request.getParameter("dateOfBirth");
        String gender = request.getParameter("gender");
        String bloodGroup = request.getParameter("bloodGroup");
        String address = request.getParameter("address");
        
        System.out.println("========================================");
        System.out.println("📝 [PatientServlet] Updating profile for user: " + userId);
        System.out.println("   Phone: " + phone);
        System.out.println("   Date of Birth: " + dateOfBirth);
        System.out.println("   Gender: " + gender);
        System.out.println("   Blood Group: " + bloodGroup);
        System.out.println("   Address: " + address);
        
        // Update user profile
        boolean success = updateUserProfile(userId, phone, dateOfBirth, gender, bloodGroup, address);
        
        if (success) {
            System.out.println("✅ Profile updated successfully");
            response.sendRedirect("PatientServlet?success=profileUpdated");
        } else {
            System.out.println("❌ Profile update failed");
            response.sendRedirect("PatientServlet?error=updateFailed");
        }
        
        System.out.println("========================================");
    }
    
    private Map<String, String> getUserProfile(int userId) {
        Map<String, String> profile = new HashMap<>();
        
        System.out.println("🔍 [getUserProfile] Fetching profile for userId: " + userId);
        
        // CORRECTED COLUMN NAMES TO MATCH YOUR DATABASE
        String query = "SELECT fullname, email, phone, date_of_birth, gender, blood_group, address " +
                      "FROM users WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                profile.put("fullname", rs.getString("fullname") != null ? rs.getString("fullname") : "");
                profile.put("email", rs.getString("email") != null ? rs.getString("email") : "");
                profile.put("phone", rs.getString("phone") != null ? rs.getString("phone") : "");
                profile.put("date_of_birth", rs.getString("date_of_birth") != null ? rs.getString("date_of_birth") : "");
                profile.put("gender", rs.getString("gender") != null ? rs.getString("gender") : "");
                profile.put("blood_group", rs.getString("blood_group") != null ? rs.getString("blood_group") : "");
                profile.put("address", rs.getString("address") != null ? rs.getString("address") : "");
                
                System.out.println("✅ User profile loaded:");
                System.out.println("   Phone: " + profile.get("phone"));
                System.out.println("   DOB: " + profile.get("date_of_birth"));
                System.out.println("   Gender: " + profile.get("gender"));
                System.out.println("   Blood Group: " + profile.get("blood_group"));
                System.out.println("   Address: " + profile.get("address"));
            } else {
                System.out.println("❌ User not found in database");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error fetching user profile: " + e.getMessage());
            e.printStackTrace();
        }
        
        return profile;
    }
    
    private boolean updateUserProfile(int userId, String phone, String dateOfBirth, String gender, 
                                     String bloodGroup, String address) {
        
        // CORRECTED COLUMN NAMES TO MATCH YOUR DATABASE
        String query = "UPDATE users SET phone = ?, date_of_birth = ?, gender = ?, " +
                      "blood_group = ?, address = ?, updated_at = NOW() WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, phone);
            stmt.setString(2, dateOfBirth);
            stmt.setString(3, gender);
            stmt.setString(4, bloodGroup);
            stmt.setString(5, address);
            stmt.setInt(6, userId);
            
            System.out.println("📝 Executing UPDATE query for userId: " + userId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ " + rowsAffected + " row(s) updated successfully");
                return true;
            } else {
                System.out.println("❌ No rows were updated");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating user profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private List<Map<String, String>> getUpcomingAppointments(int userId) {
        List<Map<String, String>> appointments = new ArrayList<>();
        
        String query = "SELECT id, doctor_name, department, appointment_date, " +
                      "appointment_time, symptoms, status FROM appointments " +
                      "WHERE patient_id = ? AND status IN ('pending', 'scheduled', 'upcoming') " +
                      "ORDER BY appointment_date ASC, appointment_time ASC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, String> apt = new HashMap<>();
                apt.put("id", String.valueOf(rs.getInt("id")));
                apt.put("doctor_name", rs.getString("doctor_name"));
                apt.put("department", rs.getString("department"));
                apt.put("appointment_date", rs.getString("appointment_date"));
                apt.put("appointment_time", rs.getString("appointment_time"));
                apt.put("symptoms", rs.getString("symptoms"));
                apt.put("status", rs.getString("status"));
                appointments.add(apt);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error fetching upcoming appointments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    private List<Map<String, String>> getCompletedAppointments(int userId) {
        List<Map<String, String>> appointments = new ArrayList<>();
        
        String query = "SELECT id, doctor_name, department, appointment_date, " +
                      "appointment_time, symptoms, status FROM appointments " +
                      "WHERE patient_id = ? AND status = 'completed' " +
                      "ORDER BY appointment_date DESC, appointment_time DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, String> apt = new HashMap<>();
                apt.put("id", String.valueOf(rs.getInt("id")));
                apt.put("doctor_name", rs.getString("doctor_name"));
                apt.put("department", rs.getString("department"));
                apt.put("appointment_date", rs.getString("appointment_date"));
                apt.put("appointment_time", rs.getString("appointment_time"));
                apt.put("symptoms", rs.getString("symptoms"));
                apt.put("status", rs.getString("status"));
                appointments.add(apt);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error fetching completed appointments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    private List<Map<String, String>> getCancelledAppointments(int userId) {
        List<Map<String, String>> appointments = new ArrayList<>();
        
        String query = "SELECT id, doctor_name, department, appointment_date, " +
                      "appointment_time, symptoms, status FROM appointments " +
                      "WHERE patient_id = ? AND status = 'cancelled' " +
                      "ORDER BY appointment_date DESC, appointment_time DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, String> apt = new HashMap<>();
                apt.put("id", String.valueOf(rs.getInt("id")));
                apt.put("doctor_name", rs.getString("doctor_name"));
                apt.put("department", rs.getString("department"));
                apt.put("appointment_date", rs.getString("appointment_date"));
                apt.put("appointment_time", rs.getString("appointment_time"));
                apt.put("symptoms", rs.getString("symptoms"));
                apt.put("status", rs.getString("status"));
                appointments.add(apt);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error fetching cancelled appointments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    private List<Map<String, String>> getActivePrescriptions(int userId) {
        List<Map<String, String>> prescriptions = new ArrayList<>();
        
        System.out.println("\n🔍 [getActivePrescriptions] Searching for patient_id: " + userId);
        
        String query = "SELECT id, prescription_number, doctor_name, medicine_name, " +
                      "dosage, frequency, duration, diagnosis, instructions, " +
                      "prescribed_date, start_date, end_date, followup_date, status " +
                      "FROM prescriptions " +
                      "WHERE patient_id = ? AND status = 'active' " +
                      "ORDER BY prescribed_date DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                Map<String, String> rx = new HashMap<>();
                rx.put("id", String.valueOf(rs.getInt("id")));
                rx.put("prescription_number", rs.getString("prescription_number"));
                rx.put("doctor_name", rs.getString("doctor_name"));
                rx.put("medicine_name", rs.getString("medicine_name"));
                rx.put("dosage", rs.getString("dosage"));
                rx.put("frequency", rs.getString("frequency"));
                rx.put("duration", rs.getString("duration"));
                rx.put("diagnosis", rs.getString("diagnosis"));
                rx.put("instructions", rs.getString("instructions"));
                rx.put("prescribed_date", rs.getString("prescribed_date"));
                rx.put("start_date", rs.getString("start_date"));
                rx.put("end_date", rs.getString("end_date"));
                rx.put("followup_date", rs.getString("followup_date"));
                rx.put("status", rs.getString("status"));
                
                prescriptions.add(rx);
            }
            
            System.out.println("   ✅ Found " + count + " active prescriptions");
            
        } catch (SQLException e) {
            System.err.println("❌ Error fetching active prescriptions: " + e.getMessage());
            e.printStackTrace();
        }
        
        return prescriptions;
    }
    
    private List<Map<String, String>> getCompletedPrescriptions(int userId) {
        List<Map<String, String>> prescriptions = new ArrayList<>();
        
        String query = "SELECT id, prescription_number, doctor_name, medicine_name, " +
                      "dosage, frequency, duration, diagnosis, instructions, " +
                      "prescribed_date, status " +
                      "FROM prescriptions " +
                      "WHERE patient_id = ? AND status IN ('completed', 'expired', 'cancelled') " +
                      "ORDER BY prescribed_date DESC " +
                      "LIMIT 20";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, String> rx = new HashMap<>();
                rx.put("id", String.valueOf(rs.getInt("id")));
                rx.put("prescription_number", rs.getString("prescription_number"));
                rx.put("doctor_name", rs.getString("doctor_name"));
                rx.put("medicine_name", rs.getString("medicine_name"));
                rx.put("dosage", rs.getString("dosage"));
                rx.put("frequency", rs.getString("frequency"));
                rx.put("duration", rs.getString("duration"));
                rx.put("diagnosis", rs.getString("diagnosis"));
                rx.put("instructions", rs.getString("instructions"));
                rx.put("prescribed_date", rs.getString("prescribed_date"));
                rx.put("status", rs.getString("status"));
                
                prescriptions.add(rx);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error fetching completed prescriptions: " + e.getMessage());
            e.printStackTrace();
        }
        
        return prescriptions;
    }
}