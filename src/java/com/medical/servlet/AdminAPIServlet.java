package com.medical.servlet;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.google.gson.Gson;

/**
 * REST API Servlet for Admin Dashboard Operations
 * Handles CRUD operations for doctors, patients, appointments, etc.
 */
@WebServlet("/api/admin/*")
public class AdminAPIServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check admin session
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            sendError(response, "Unauthorized access");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            sendError(response, "Invalid endpoint");
            return;
        }

        try {
            switch (pathInfo) {
                case "/doctors":
                    getDoctors(request, response);
                    break;
                case "/patients":
                    getPatients(request, response);
                    break;
                case "/appointments":
                    getAppointments(request, response);
                    break;
                case "/prescriptions":
                    getPrescriptions(request, response);
                    break;
                case "/statistics":
                    getStatistics(request, response);
                    break;
                default:
                    sendError(response, "Unknown endpoint");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Server error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            sendError(response, "Unauthorized access");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            sendError(response, "Invalid endpoint");
            return;
        }

        try {
            switch (pathInfo) {
                case "/doctors":
                    addDoctor(request, response);
                    break;
                case "/patients":
                    addPatient(request, response);
                    break;
                default:
                    sendError(response, "Unknown endpoint");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Server error: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            sendError(response, "Unauthorized access");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            sendError(response, "Invalid endpoint");
            return;
        }

        try {
            switch (pathInfo) {
                case "/doctors":
                    updateDoctor(request, response);
                    break;
                case "/patients":
                    updatePatientStatus(request, response);
                    break;
                case "/appointments":
                    updateAppointmentStatus(request, response);
                    break;
                default:
                    sendError(response, "Unknown endpoint");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Server error: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            sendError(response, "Unauthorized access");
            return;
        }

        String pathInfo = request.getPathInfo();
        String idParam = request.getParameter("id");

        if (pathInfo == null || idParam == null) {
            sendError(response, "Invalid parameters");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            
            switch (pathInfo) {
                case "/doctors":
                    deleteDoctor(id, response);
                    break;
                case "/appointments":
                    deleteAppointment(id, response);
                    break;
                default:
                    sendError(response, "Unknown endpoint");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Server error: " + e.getMessage());
        }
    }

    // ========== GET METHODS ==========
    
    private void getDoctors(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        String specialty = request.getParameter("specialty");
        
        List<Map<String, Object>> doctors = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT id, fullname, email, phone, specialty, qualification, " +
                        "experience, department, status, license_number " +
                        "FROM users WHERE role='doctor'";
            
            if (specialty != null && !specialty.isEmpty()) {
                sql += " AND specialty = ?";
            }
            
            sql += " ORDER BY fullname";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            if (specialty != null && !specialty.isEmpty()) {
                pstmt.setString(1, specialty);
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> doctor = new HashMap<>();
                doctor.put("id", rs.getInt("id"));
                doctor.put("name", rs.getString("fullname"));
                doctor.put("email", rs.getString("email"));
                doctor.put("phone", rs.getString("phone"));
                doctor.put("specialty", rs.getString("specialty"));
                doctor.put("qualification", rs.getString("qualification"));
                doctor.put("experience", rs.getInt("experience"));
                doctor.put("department", rs.getString("department"));
                doctor.put("status", rs.getString("status"));
                doctor.put("license", rs.getString("license_number"));
                
                // Get patient count
                int patientCount = getDoctorPatientCount(conn, rs.getInt("id"));
                doctor.put("patients", patientCount);
                
                doctors.add(doctor);
            }
            
            rs.close();
            pstmt.close();
            
        } finally {
            if (conn != null) conn.close();
        }
        
        sendSuccess(response, doctors);
    }

    private void getPatients(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        String status = request.getParameter("status");
        
        List<Map<String, Object>> patients = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT id, fullname, email, phone, age, gender, blood_group, status " +
                        "FROM users WHERE role='patient'";
            
            if (status != null && !status.isEmpty()) {
                sql += " AND status = ?";
            }
            
            sql += " ORDER BY fullname";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            if (status != null && !status.isEmpty()) {
                pstmt.setString(1, status);
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> patient = new HashMap<>();
                patient.put("id", rs.getInt("id"));
                patient.put("name", rs.getString("fullname"));
                patient.put("email", rs.getString("email"));
                patient.put("phone", rs.getString("phone"));
                patient.put("age", rs.getInt("age"));
                patient.put("gender", rs.getString("gender"));
                patient.put("blood_group", rs.getString("blood_group"));
                patient.put("status", rs.getString("status"));
                patients.add(patient);
            }
            
            rs.close();
            pstmt.close();
            
        } finally {
            if (conn != null) conn.close();
        }
        
        sendSuccess(response, patients);
    }

    private void getAppointments(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        String status = request.getParameter("status");
        String date = request.getParameter("date");
        
        List<Map<String, Object>> appointments = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT id, patient_name, doctor_name, department, " +
                        "appointment_date, appointment_time, status, symptoms " +
                        "FROM appointments WHERE 1=1";
            
            List<String> params = new ArrayList<>();
            
            if (status != null && !status.isEmpty()) {
                sql += " AND status = ?";
                params.add(status);
            }
            
            if (date != null && !date.isEmpty()) {
                sql += " AND appointment_date = ?";
                params.add(date);
            }
            
            sql += " ORDER BY appointment_date DESC, appointment_time DESC LIMIT 100";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setString(i + 1, params.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> apt = new HashMap<>();
                apt.put("id", rs.getInt("id"));
                apt.put("patient", rs.getString("patient_name"));
                apt.put("doctor", rs.getString("doctor_name"));
                apt.put("department", rs.getString("department"));
                apt.put("date", rs.getString("appointment_date"));
                apt.put("time", rs.getString("appointment_time"));
                apt.put("status", rs.getString("status"));
                apt.put("symptoms", rs.getString("symptoms"));
                appointments.add(apt);
            }
            
            rs.close();
            pstmt.close();
            
        } finally {
            if (conn != null) conn.close();
        }
        
        sendSuccess(response, appointments);
    }

    private void getPrescriptions(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        List<Map<String, Object>> prescriptions = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT id, prescription_number, patient_name, doctor_name, " +
                        "medicine_name, dosage, frequency, duration, prescribed_date, status " +
                        "FROM prescriptions ORDER BY prescribed_date DESC LIMIT 100";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> rx = new HashMap<>();
                rx.put("id", rs.getInt("id"));
                rx.put("prescription_number", rs.getString("prescription_number"));
                rx.put("patient", rs.getString("patient_name"));
                rx.put("doctor", rs.getString("doctor_name"));
                rx.put("medicine", rs.getString("medicine_name"));
                rx.put("dosage", rs.getString("dosage"));
                rx.put("frequency", rs.getString("frequency"));
                rx.put("duration", rs.getString("duration"));
                rx.put("date", rs.getString("prescribed_date"));
                rx.put("status", rs.getString("status"));
                prescriptions.add(rx);
            }
            
            rs.close();
            pstmt.close();
            
        } finally {
            if (conn != null) conn.close();
        }
        
        sendSuccess(response, prescriptions);
    }

    private void getStatistics(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        Connection conn = null;
        Map<String, Integer> stats = new HashMap<>();
        
        try {
            conn = DBConnection.getConnection();
            
            // Get today's date
            String today = LocalDate.now().toString();
            
            stats.put("totalDoctors", getCount(conn, 
                "SELECT COUNT(*) FROM users WHERE role='doctor' AND status='active'"));
            stats.put("totalPatients", getCount(conn, 
                "SELECT COUNT(*) FROM users WHERE role='patient' AND status='active'"));
            stats.put("totalAppointments", getCount(conn, 
                "SELECT COUNT(*) FROM appointments"));
            stats.put("todayAppointments", getCount(conn, 
                "SELECT COUNT(*) FROM appointments WHERE appointment_date = '" + today + "'"));
            
        } finally {
            if (conn != null) conn.close();
        }
        
        sendSuccess(response, stats);
    }

    // ========== POST METHODS ==========
    
    private void addDoctor(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        BufferedReader reader = request.getReader();
        Map<String, Object> data = gson.fromJson(reader, Map.class);
        
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "INSERT INTO users (fullname, email, phone, password, role, " +
                        "specialty, qualification, experience, department, license_number, status) " +
                        "VALUES (?, ?, ?, ?, 'doctor', ?, ?, ?, ?, ?, 'active')";
            
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, (String) data.get("name"));
            pstmt.setString(2, (String) data.get("email"));
            pstmt.setString(3, (String) data.get("phone"));
            
            // Hash default password
            String defaultPassword = "Doctor@123";
            String hashedPassword = PasswordUtil.hashPassword(defaultPassword);
            pstmt.setString(4, hashedPassword);
            
            pstmt.setString(5, (String) data.get("specialty"));
            pstmt.setString(6, (String) data.get("qualification"));
            pstmt.setInt(7, ((Double) data.get("experience")).intValue());
            pstmt.setString(8, (String) data.get("department"));
            pstmt.setString(9, (String) data.get("license"));
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", newId);
                    result.put("message", "Doctor added successfully");
                    sendSuccess(response, result);
                }
            }
            
            pstmt.close();
            
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    private void addPatient(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        BufferedReader reader = request.getReader();
        Map<String, Object> data = gson.fromJson(reader, Map.class);
        
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "INSERT INTO users (fullname, email, phone, password, role, " +
                        "age, gender, blood_group, status) " +
                        "VALUES (?, ?, ?, ?, 'patient', ?, ?, ?, 'active')";
            
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, (String) data.get("name"));
            pstmt.setString(2, (String) data.get("email"));
            pstmt.setString(3, (String) data.get("phone"));
            
            // Hash default password
            String defaultPassword = "Patient@123";
            String hashedPassword = PasswordUtil.hashPassword(defaultPassword);
            pstmt.setString(4, hashedPassword);
            
            pstmt.setInt(5, ((Double) data.get("age")).intValue());
            pstmt.setString(6, (String) data.get("gender"));
            pstmt.setString(7, (String) data.get("blood_group"));
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", newId);
                    result.put("message", "Patient added successfully");
                    sendSuccess(response, result);
                }
            }
            
            pstmt.close();
            
        } finally {
            if (conn != null) conn.close();
        }
    }

    // ========== PUT METHODS ==========
    
    private void updateDoctor(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        BufferedReader reader = request.getReader();
        Map<String, Object> data = gson.fromJson(reader, Map.class);
        
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "UPDATE users SET fullname=?, email=?, phone=?, specialty=?, " +
                        "qualification=?, experience=?, department=?, status=? " +
                        "WHERE id=? AND role='doctor'";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, (String) data.get("name"));
            pstmt.setString(2, (String) data.get("email"));
            pstmt.setString(3, (String) data.get("phone"));
            pstmt.setString(4, (String) data.get("specialty"));
            pstmt.setString(5, (String) data.get("qualification"));
            pstmt.setInt(6, ((Double) data.get("experience")).intValue());
            pstmt.setString(7, (String) data.get("department"));
            pstmt.setString(8, (String) data.get("status"));
            pstmt.setInt(9, ((Double) data.get("id")).intValue());
            
            int rows = pstmt.executeUpdate();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", rows > 0);
            result.put("message", rows > 0 ? "Doctor updated" : "Doctor not found");
            
            sendSuccess(response, result);
            
            pstmt.close();
            
        } finally {
            if (conn != null) conn.close();
        }
    }

    private void updatePatientStatus(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        BufferedReader reader = request.getReader();
        Map<String, Object> data = gson.fromJson(reader, Map.class);
        
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "UPDATE users SET status=? WHERE id=? AND role='patient'";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, (String) data.get("status"));
            pstmt.setInt(2, ((Double) data.get("id")).intValue());
            
            int rows = pstmt.executeUpdate();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", rows > 0);
            result.put("message", rows > 0 ? "Patient updated" : "Patient not found");
            
            sendSuccess(response, result);
            
            pstmt.close();
            
        } finally {
            if (conn != null) conn.close();
        }
    }

    private void updateAppointmentStatus(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        BufferedReader reader = request.getReader();
        Map<String, Object> data = gson.fromJson(reader, Map.class);
        
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "UPDATE appointments SET status=? WHERE id=?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, (String) data.get("status"));
            pstmt.setInt(2, ((Double) data.get("id")).intValue());
            
            int rows = pstmt.executeUpdate();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", rows > 0);
            result.put("message", rows > 0 ? "Appointment updated" : "Appointment not found");
            
            sendSuccess(response, result);
            
            pstmt.close();
            
        } finally {
            if (conn != null) conn.close();
        }
    }

    // ========== DELETE METHODS ==========
    
    private void deleteDoctor(int id, HttpServletResponse response) 
            throws SQLException, IOException {
        
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // Set status to inactive instead of deleting
            String sql = "UPDATE users SET status='inactive' WHERE id=? AND role='doctor'";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            int rows = pstmt.executeUpdate();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", rows > 0);
            result.put("message", rows > 0 ? "Doctor deactivated" : "Doctor not found");
            
            sendSuccess(response, result);
            
            pstmt.close();
            
        } finally {
            if (conn != null) conn.close();
        }
    }

    private void deleteAppointment(int id, HttpServletResponse response) 
            throws SQLException, IOException {
        
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "UPDATE appointments SET status='cancelled' WHERE id=?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            int rows = pstmt.executeUpdate();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", rows > 0);
            result.put("message", rows > 0 ? "Appointment cancelled" : "Appointment not found");
            
            sendSuccess(response, result);
            
            pstmt.close();
            
        } finally {
            if (conn != null) conn.close();
        }
    }

    // ========== HELPER METHODS ==========
    
    private int getCount(Connection conn, String query) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getDoctorPatientCount(Connection conn, int doctorId) {
        String sql = "SELECT COUNT(DISTINCT patient_id) FROM appointments WHERE doctor_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void sendSuccess(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        
        response.getWriter().write(gson.toJson(result));
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        
        response.getWriter().write(gson.toJson(result));
    }
}