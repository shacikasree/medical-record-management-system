package com.medical.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Gson gson = new Gson();
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("getDoctorData".equals(action)) {
            if (!isAuthorized(request, response)) return;
            getDoctorData(request, response);
            return;
        }
        
        if ("getPatientData".equals(action)) {
            if (!isAuthorized(request, response)) return;
            getPatientData(request, response);
            return;
        }

        if ("getAppointmentData".equals(action)) {
            if (!isAuthorized(request, response)) return;
            getAppointmentData(request, response);
            return;
        }
        
        // NEW: Department data endpoint
        if ("getDepartmentData".equals(action)) {
            if (!isAuthorized(request, response)) return;
            getDepartmentData(request, response);
            return;
        }
        
        // Session validation for main page load
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("Login.jsp");
            return;
        }
        
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            response.sendRedirect("Login.jsp?error=unauthorized");
            return;
        }
        
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            
            Map<String, Integer> stats = getStatistics(conn);
            List<Map<String, String>> doctors = getDoctors(conn);
     Map<Integer, Map<String, Object>> doctorAvailability = new HashMap<>();

if (doctors != null && !doctors.isEmpty()) {
    for (Map<String, String> doctor : doctors) {
        try {
            int doctorId = Integer.parseInt(doctor.get("id"));
            
            // 1. Get weekly schedule
            List<Map<String, String>> weeklySchedule = new ArrayList<>();
            String scheduleQuery = "SELECT day_name, is_active, " +
                "TIME_FORMAT(start_time, '%H:%i') as start_time, " +
                "TIME_FORMAT(end_time, '%H:%i') as end_time " +
                "FROM doctor_schedule " +
                "WHERE doctor_id = ? " +
                "ORDER BY FIELD(day_name, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday')";
            
            PreparedStatement scheduleStmt = conn.prepareStatement(scheduleQuery);
            scheduleStmt.setInt(1, doctorId);
            ResultSet scheduleRs = scheduleStmt.executeQuery();
            
            while (scheduleRs.next()) {
                Map<String, String> daySchedule = new HashMap<>();
                daySchedule.put("dayName", scheduleRs.getString("day_name"));
                daySchedule.put("isActive", String.valueOf(scheduleRs.getBoolean("is_active")));
                daySchedule.put("startTime", scheduleRs.getString("start_time"));
                daySchedule.put("endTime", scheduleRs.getString("end_time"));
                weeklySchedule.add(daySchedule);
            }
            scheduleRs.close();
            scheduleStmt.close();
            
            // 2. Get unavailable dates (future only)
            List<Map<String, String>> unavailableDates = new ArrayList<>();
            String datesQuery = "SELECT id, DATE_FORMAT(unavailable_date, '%Y-%m-%d') as date, " +
                "unavailable_date, reason " +
                "FROM doctor_unavailable_dates " +
                "WHERE doctor_id = ? AND unavailable_date >= CURDATE() " +
                "ORDER BY unavailable_date";
            
            PreparedStatement datesStmt = conn.prepareStatement(datesQuery);
            datesStmt.setInt(1, doctorId);
            ResultSet datesRs = datesStmt.executeQuery();
            
            while (datesRs.next()) {
                Map<String, String> unavailDate = new HashMap<>();
                unavailDate.put("id", String.valueOf(datesRs.getInt("id")));
                unavailDate.put("date", datesRs.getString("date"));
                unavailDate.put("reason", datesRs.getString("reason"));
                unavailableDates.add(unavailDate);
            }
            datesRs.close();
            datesStmt.close();
            
            // 3. Get emergency availability
            boolean emergencyAvailable = false;
            String emergencyQuery = "SELECT emergency_available FROM doctor_settings WHERE doctor_id = ?";
            
            PreparedStatement emergencyStmt = conn.prepareStatement(emergencyQuery);
            emergencyStmt.setInt(1, doctorId);
            ResultSet emergencyRs = emergencyStmt.executeQuery();
            
            if (emergencyRs.next()) {
                emergencyAvailable = emergencyRs.getBoolean("emergency_available");
            }
            emergencyRs.close();
            emergencyStmt.close();
            
            // Store all availability data
            Map<String, Object> availData = new HashMap<>();
            availData.put("weeklySchedule", weeklySchedule);
            availData.put("unavailableDates", unavailableDates);
            availData.put("emergencyAvailable", emergencyAvailable);
            
            doctorAvailability.put(doctorId, availData);
            
        } catch (Exception e) {
            System.err.println("Error loading availability for doctor ID: " + doctor.get("id"));
            e.printStackTrace();
        }
    }
}

            List<Map<String, String>> patients = getPatients(conn);
            List<Map<String, String>> appointments = getAppointments(conn);
            List<Map<String, String>> todayAppointments = getTodayAppointments(conn);
            List<Map<String, String>> prescriptions = getPrescriptions(conn);
            List<Map<String, String>> activities = getRecentActivities(conn);
            List<Map<String, String>> departmentStats = getDepartmentStats(conn);
            List<Map<String, String>> doctorSchedules = getDoctorSchedules(conn);
            List<Map<String, String>> users = getUsers(conn);
            Map<String, String> hospitalSettings = getHospitalSettings(conn);
            List<Map<String, String>> holidays = getHolidays(conn);
            
            request.setAttribute("stats", stats);
            request.setAttribute("doctors", doctors);
            request.setAttribute("patients", patients);
            request.setAttribute("appointments", appointments);
            request.setAttribute("todayAppointments", todayAppointments);
            request.setAttribute("prescriptions", prescriptions);
            request.setAttribute("activities", activities);
            request.setAttribute("departmentStats", departmentStats);
            request.setAttribute("doctorSchedules", doctorSchedules);
            request.setAttribute("users", users);
            request.setAttribute("hospitalSettings", hospitalSettings);
            request.setAttribute("holidays", holidays);
            request.setAttribute("doctorAvailability", doctorAvailability);
            request.getRequestDispatcher("admin.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "An error occurred while loading the dashboard. Please try again later.");
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!isAuthorizedForJson(request, response)) return;
        
        String action = request.getParameter("action");
        
        System.out.println("========== POST REQUEST ==========");
        System.out.println("Action: " + action);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        if ("addDoctor".equals(action)) {
            addDoctor(request, response);
        } else if ("updateDoctor".equals(action)) {
            updateDoctor(request, response);
        } else if ("deleteDoctor".equals(action)) {
            deleteDoctor(request, response);
        } else if ("addPatient".equals(action)) {
            addPatient(request, response);
        } else if ("updatePatient".equals(action)) {
            updatePatient(request, response);
        } else if ("togglePatientStatus".equals(action)) {
            togglePatientStatus(request, response);
        } else if ("cancelAppointment".equals(action)) {
            updateAppointmentStatus(request, response, "cancelled");
        } else if ("completeAppointment".equals(action)) {
            updateAppointmentStatus(request, response, "completed");
        // NEW: Department actions
        } else if ("addDepartment".equals(action)) {
            addDepartment(request, response);
        } else if ("updateDepartment".equals(action)) {
            updateDepartment(request, response);
        } else if ("deleteDepartment".equals(action)) {
            deleteDepartment(request, response);
        } else {
            PrintWriter out = response.getWriter();
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Invalid action");
            out.print(gson.toJson(result));
            out.flush();
        }
    }
    
    // ========================================================================
    // AUTHORIZATION HELPERS
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
    
    private boolean isAuthorizedForJson(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || 
            !"admin".equals(session.getAttribute("role"))) {
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Unauthorized access");
            out.print(gson.toJson(result));
            out.flush();
            return false;
        }
        return true;
    }
    
    // ========================================================================
    // DOCTOR METHODS
    // ========================================================================
    
    private void getDoctorData(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Doctor ID is required");
                out.print(gson.toJson(result));
                out.flush();
                return;
            }
            
            int id = Integer.parseInt(idParam);
            conn = DBConnection.getConnection();
            
            String sql = "SELECT u.id, u.fullname, u.email, u.phone, u.specialty, " +
                        "u.qualification, u.experience, u.department, " +
                        "(SELECT COUNT(*) FROM appointments WHERE doctor_id = u.id) as patient_count " +
                        "FROM users u WHERE u.id = ? AND u.role = 'doctor'";
            
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                result.put("success", true);
                result.put("id", rs.getInt("id"));
                result.put("fullname", rs.getString("fullname"));
                result.put("email", rs.getString("email"));
                result.put("phone", rs.getString("phone"));
                result.put("specialty", rs.getString("specialty"));
                result.put("qualification", rs.getString("qualification"));
                result.put("experience", rs.getInt("experience"));
                result.put("department", rs.getString("department"));
                result.put("patientCount", rs.getInt("patient_count"));
            } else {
                result.put("success", false);
                result.put("message", "Doctor not found");
            }
            
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid doctor ID format");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "An error occurred while fetching doctor data");
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        out.print(gson.toJson(result));
        out.flush();
    }
    
    private void addDoctor(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet checkRs = null;
        
        System.out.println("========== ADD DOCTOR ==========");
        
        try {
            String fullname = request.getParameter("fullname");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String password = request.getParameter("password");
            String specialty = request.getParameter("specialty");
            String qualification = request.getParameter("qualification");
            String experienceStr = request.getParameter("experience");
            String department = request.getParameter("department");
            
            System.out.println("Fullname: " + fullname);
            System.out.println("Email: " + email);
            System.out.println("Experience string: " + experienceStr);
            
            if (fullname == null || fullname.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Full name is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            if (fullname.trim().length() < 3) {
                result.put("success", false);
                result.put("message", "Full name must be at least 3 characters");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            if (email == null || email.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Email is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
                result.put("success", false);
                result.put("message", "Invalid email format");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            if (specialty == null || specialty.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Specialty is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            if (qualification == null || qualification.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Qualification is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            if (phone != null && !phone.trim().isEmpty()) {
                if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
                    result.put("success", false);
                    result.put("message", "Phone number must be 10 digits");
                    out.print(gson.toJson(result)); out.flush(); return;
                }
            }
            if (password == null || password.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Password is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            if (password.length() < 8) {
                result.put("success", false);
                result.put("message", "Password must be at least 8 characters");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            
            int experience = 0;
            if (experienceStr != null && !experienceStr.trim().isEmpty()) {
                try {
                    experience = Integer.parseInt(experienceStr.trim());
                    if (experience < 0 || experience > 50) {
                        result.put("success", false);
                        result.put("message", "Experience must be between 0 and 50 years");
                        out.print(gson.toJson(result)); out.flush(); return;
                    }
                } catch (NumberFormatException e) {
                    result.put("success", false);
                    result.put("message", "Invalid experience value");
                    out.print(gson.toJson(result)); out.flush(); return;
                }
            }
            
            System.out.println("Parsed experience: " + experience);
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            String checkSql = "SELECT id FROM users WHERE email = ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email.trim().toLowerCase());
            checkRs = checkStmt.executeQuery();
            
            if (checkRs.next()) {
                result.put("success", false);
                result.put("message", "Email already exists");
                out.print(gson.toJson(result)); out.flush();
                conn.rollback(); return;
            }
            checkRs.close();
            checkStmt.close();
            
            String hashedPassword;
            try {
                hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
            } catch (Exception e) {
                e.printStackTrace();
                result.put("success", false);
                result.put("message", "Error processing password");
                out.print(gson.toJson(result)); out.flush();
                conn.rollback(); return;
            }
            
            String sql = "INSERT INTO users (email, password, fullname, phone, specialty, " +
                        "qualification, experience, department, role, status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'doctor', 'active')";
            
            insertStmt = conn.prepareStatement(sql);
            insertStmt.setString(1, email.trim().toLowerCase());
            insertStmt.setString(2, hashedPassword);
            insertStmt.setString(3, fullname.trim());
            insertStmt.setString(4, phone != null ? phone.trim() : "");
            insertStmt.setString(5, specialty.trim());
            insertStmt.setString(6, qualification.trim());
            insertStmt.setInt(7, experience);
            insertStmt.setString(8, department != null ? department.trim() : "");
            
            int rows = insertStmt.executeUpdate();
            System.out.println("Rows inserted: " + rows);
            
            if (rows > 0) {
                conn.commit();
                result.put("success", true);
                result.put("message", "Doctor added successfully");
            } else {
                conn.rollback();
                result.put("success", false);
                result.put("message", "Failed to add doctor");
            }
            
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Database error occurred");
        } catch (Exception e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "An error occurred");
        } finally {
            closeResources(conn, checkStmt, checkRs);
            if (insertStmt != null) { try { insertStmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
        
        out.print(gson.toJson(result));
        out.flush();
    }
    
    private void updateDoctor(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet checkRs = null;
        
        System.out.println("========== UPDATE DOCTOR ==========");
        
        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Doctor ID is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            
            int id = Integer.parseInt(idStr);
            String fullname = request.getParameter("fullname");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String password = request.getParameter("password");
            String specialty = request.getParameter("specialty");
            String qualification = request.getParameter("qualification");
            String experienceStr = request.getParameter("experience");
            String department = request.getParameter("department");
            
            System.out.println("Updating doctor ID: " + id);
            
            if (fullname == null || fullname.trim().isEmpty() || fullname.trim().length() < 3) {
                result.put("success", false);
                result.put("message", "Valid full name is required (min 3 characters)");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
                result.put("success", false);
                result.put("message", "Valid email is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            if (phone != null && !phone.trim().isEmpty()) {
                if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
                    result.put("success", false);
                    result.put("message", "Phone number must be 10 digits");
                    out.print(gson.toJson(result)); out.flush(); return;
                }
            }
            
            int experience = 0;
            if (experienceStr != null && !experienceStr.trim().isEmpty()) {
                try {
                    experience = Integer.parseInt(experienceStr.trim());
                    if (experience < 0 || experience > 50) {
                        result.put("success", false);
                        result.put("message", "Experience must be between 0 and 50 years");
                        out.print(gson.toJson(result)); out.flush(); return;
                    }
                } catch (NumberFormatException e) {
                    result.put("success", false);
                    result.put("message", "Invalid experience value");
                    out.print(gson.toJson(result)); out.flush(); return;
                }
            }
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            String checkSql = "SELECT id FROM users WHERE email = ? AND id != ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email.trim().toLowerCase());
            checkStmt.setInt(2, id);
            checkRs = checkStmt.executeQuery();
            
            if (checkRs.next()) {
                result.put("success", false);
                result.put("message", "Email already exists");
                out.print(gson.toJson(result)); out.flush();
                conn.rollback(); return;
            }
            checkRs.close();
            checkStmt.close();
            
            String sql;
            if (password != null && !password.trim().isEmpty() && !password.equals("doctor123")) {
                if (password.length() < 8) {
                    result.put("success", false);
                    result.put("message", "Password must be at least 8 characters");
                    out.print(gson.toJson(result)); out.flush();
                    conn.rollback(); return;
                }
                String hashedPassword;
                try {
                    hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
                } catch (Exception e) {
                    e.printStackTrace();
                    result.put("success", false);
                    result.put("message", "Error processing password");
                    out.print(gson.toJson(result)); out.flush();
                    conn.rollback(); return;
                }
                sql = "UPDATE users SET fullname = ?, email = ?, phone = ?, password = ?, " +
                      "specialty = ?, qualification = ?, experience = ?, department = ? " +
                      "WHERE id = ? AND role = 'doctor'";
                updateStmt = conn.prepareStatement(sql);
                updateStmt.setString(1, fullname.trim());
                updateStmt.setString(2, email.trim().toLowerCase());
                updateStmt.setString(3, phone != null ? phone.trim() : "");
                updateStmt.setString(4, hashedPassword);
                updateStmt.setString(5, specialty != null ? specialty.trim() : "");
                updateStmt.setString(6, qualification != null ? qualification.trim() : "");
                updateStmt.setInt(7, experience);
                updateStmt.setString(8, department != null ? department.trim() : "");
                updateStmt.setInt(9, id);
            } else {
                sql = "UPDATE users SET fullname = ?, email = ?, phone = ?, " +
                      "specialty = ?, qualification = ?, experience = ?, department = ? " +
                      "WHERE id = ? AND role = 'doctor'";
                updateStmt = conn.prepareStatement(sql);
                updateStmt.setString(1, fullname.trim());
                updateStmt.setString(2, email.trim().toLowerCase());
                updateStmt.setString(3, phone != null ? phone.trim() : "");
                updateStmt.setString(4, specialty != null ? specialty.trim() : "");
                updateStmt.setString(5, qualification != null ? qualification.trim() : "");
                updateStmt.setInt(6, experience);
                updateStmt.setString(7, department != null ? department.trim() : "");
                updateStmt.setInt(8, id);
            }
            
            int rows = updateStmt.executeUpdate();
            System.out.println("Rows updated: " + rows);
            
            if (rows > 0) {
                conn.commit();
                result.put("success", true);
                result.put("message", "Doctor updated successfully");
            } else {
                conn.rollback();
                result.put("success", false);
                result.put("message", "Failed to update doctor or doctor not found");
            }
            
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid doctor ID format");
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Database error occurred");
        } catch (Exception e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "An error occurred");
        } finally {
            closeResources(null, checkStmt, checkRs);
            if (updateStmt != null) { try { updateStmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
        
        out.print(gson.toJson(result));
        out.flush();
    }
    
    private void deleteDoctor(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement deleteStmt = null;
        ResultSet checkRs = null;
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Doctor ID is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            
            int id = Integer.parseInt(idParam);
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            String checkSql = "SELECT COUNT(*) as count FROM appointments WHERE doctor_id = ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, id);
            checkRs = checkStmt.executeQuery();
            
            if (checkRs.next() && checkRs.getInt("count") > 0) {
                result.put("success", false);
                result.put("message", "Cannot delete doctor with existing appointments");
                out.print(gson.toJson(result)); out.flush();
                conn.rollback(); return;
            }
            checkRs.close();
            checkStmt.close();
            
            String sql = "DELETE FROM users WHERE id = ? AND role = 'doctor'";
            deleteStmt = conn.prepareStatement(sql);
            deleteStmt.setInt(1, id);
            
            int rows = deleteStmt.executeUpdate();
            
            if (rows > 0) {
                conn.commit();
                result.put("success", true);
                result.put("message", "Doctor deleted successfully");
            } else {
                conn.rollback();
                result.put("success", false);
                result.put("message", "Failed to delete doctor or doctor not found");
            }
            
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid doctor ID format");
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Database error occurred");
        } catch (Exception e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "An error occurred");
        } finally {
            closeResources(null, checkStmt, checkRs);
            if (deleteStmt != null) { try { deleteStmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
        
        out.print(gson.toJson(result));
        out.flush();
    }
    
    // ========================================================================
    // PATIENT METHODS
    // ========================================================================
    
    private void getPatientData(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Patient ID is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            
            int id = Integer.parseInt(idParam);
            conn = DBConnection.getConnection();
            
            String sql = "SELECT u.id, u.fullname, u.email, u.phone, u.age, u.gender, " +
                        "u.address, u.blood_group, u.status, " +
                        "(SELECT COUNT(*) FROM appointments WHERE patient_id = u.id) as appointment_count " +
                        "FROM users u WHERE u.id = ? AND u.role = 'patient'";
            
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                result.put("success", true);
                result.put("id", rs.getInt("id"));
                result.put("name", rs.getString("fullname"));
                result.put("email", rs.getString("email"));
                result.put("phone", rs.getString("phone"));
                result.put("age", rs.getInt("age"));
                result.put("gender", rs.getString("gender"));
                result.put("address", rs.getString("address"));
                result.put("bloodGroup", rs.getString("blood_group"));
                result.put("status", rs.getString("status"));
                result.put("appointmentCount", rs.getInt("appointment_count"));
            } else {
                result.put("success", false);
                result.put("message", "Patient not found");
            }
            
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid patient ID");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Error fetching patient data");
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        out.print(gson.toJson(result));
        out.flush();
    }
    
    private void addPatient(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet checkRs = null;
        
        System.out.println("========== ADD PATIENT ==========");
        
        try {
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String password = request.getParameter("password");
            String ageStr = request.getParameter("age");
            String gender = request.getParameter("gender");
            String address = request.getParameter("address");
            String bloodGroup = request.getParameter("bloodGroup");
            
            System.out.println("Name: " + name);
            System.out.println("Email: " + email);
            
            if (name == null || name.trim().length() < 2) {
                result.put("success", false);
                result.put("message", "Valid name is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
                result.put("success", false);
                result.put("message", "Valid email is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            if (phone != null && !phone.trim().isEmpty() && !PHONE_PATTERN.matcher(phone.trim()).matches()) {
                result.put("success", false);
                result.put("message", "Phone must be 10 digits");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            
            int age = 0;
            if (ageStr != null && !ageStr.trim().isEmpty()) {
                try {
                    age = Integer.parseInt(ageStr.trim());
                    if (age < 1 || age > 120) {
                        result.put("success", false);
                        result.put("message", "Age must be between 1 and 120");
                        out.print(gson.toJson(result)); out.flush(); return;
                    }
                } catch (NumberFormatException e) {
                    result.put("success", false);
                    result.put("message", "Invalid age");
                    out.print(gson.toJson(result)); out.flush(); return;
                }
            }
            
            if (password == null || password.trim().isEmpty()) {
                password = "Patient@123";
            }
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            String checkSql = "SELECT id FROM users WHERE email = ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email.trim().toLowerCase());
            checkRs = checkStmt.executeQuery();
            
            if (checkRs.next()) {
                result.put("success", false);
                result.put("message", "Email already exists");
                out.print(gson.toJson(result)); out.flush();
                conn.rollback(); return;
            }
            checkRs.close();
            checkStmt.close();
            
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
            
            String sql = "INSERT INTO users (email, password, fullname, phone, age, gender, " +
                        "address, blood_group, role, status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'patient', 'active')";
            
            insertStmt = conn.prepareStatement(sql);
            insertStmt.setString(1, email.trim().toLowerCase());
            insertStmt.setString(2, hashedPassword);
            insertStmt.setString(3, name.trim());
            insertStmt.setString(4, phone != null ? phone.trim() : "");
            insertStmt.setInt(5, age);
            insertStmt.setString(6, gender != null ? gender.trim() : "");
            insertStmt.setString(7, address != null ? address.trim() : "");
            insertStmt.setString(8, bloodGroup != null ? bloodGroup.trim() : "");
            
            int rows = insertStmt.executeUpdate();
            System.out.println("Rows inserted: " + rows);
            
            if (rows > 0) {
                conn.commit();
                result.put("success", true);
                result.put("message", "Patient added successfully");
            } else {
                conn.rollback();
                result.put("success", false);
                result.put("message", "Failed to add patient");
            }
            
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Database error");
        } catch (Exception e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "An error occurred");
        } finally {
            closeResources(conn, checkStmt, checkRs);
            if (insertStmt != null) { try { insertStmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
        
        out.print(gson.toJson(result));
        out.flush();
    }
    
    private void updatePatient(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet checkRs = null;
        
        System.out.println("========== UPDATE PATIENT ==========");
        
        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Patient ID is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            
            int id = Integer.parseInt(idStr);
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String ageStr = request.getParameter("age");
            String gender = request.getParameter("gender");
            String address = request.getParameter("address");
            String bloodGroup = request.getParameter("bloodGroup");
            
            System.out.println("Updating patient ID: " + id);
            
            if (name == null || name.trim().length() < 2) {
                result.put("success", false);
                result.put("message", "Valid name is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
                result.put("success", false);
                result.put("message", "Valid email is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            
            int age = 0;
            if (ageStr != null && !ageStr.trim().isEmpty()) {
                try {
                    age = Integer.parseInt(ageStr.trim());
                } catch (NumberFormatException e) {
                    result.put("success", false);
                    result.put("message", "Invalid age");
                    out.print(gson.toJson(result)); out.flush(); return;
                }
            }
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            String checkSql = "SELECT id FROM users WHERE email = ? AND id != ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email.trim().toLowerCase());
            checkStmt.setInt(2, id);
            checkRs = checkStmt.executeQuery();
            
            if (checkRs.next()) {
                result.put("success", false);
                result.put("message", "Email already exists");
                out.print(gson.toJson(result)); out.flush();
                conn.rollback(); return;
            }
            checkRs.close();
            checkStmt.close();
            
            String sql = "UPDATE users SET fullname = ?, email = ?, phone = ?, " +
                        "age = ?, gender = ?, address = ?, blood_group = ? " +
                        "WHERE id = ? AND role = 'patient'";
            
            updateStmt = conn.prepareStatement(sql);
            updateStmt.setString(1, name.trim());
            updateStmt.setString(2, email.trim().toLowerCase());
            updateStmt.setString(3, phone != null ? phone.trim() : "");
            updateStmt.setInt(4, age);
            updateStmt.setString(5, gender != null ? gender.trim() : "");
            updateStmt.setString(6, address != null ? address.trim() : "");
            updateStmt.setString(7, bloodGroup != null ? bloodGroup.trim() : "");
            updateStmt.setInt(8, id);
            
            int rows = updateStmt.executeUpdate();
            System.out.println("Rows updated: " + rows);
            
            if (rows > 0) {
                conn.commit();
                result.put("success", true);
                result.put("message", "Patient updated successfully");
            } else {
                conn.rollback();
                result.put("success", false);
                result.put("message", "Failed to update patient");
            }
            
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Database error");
        } catch (Exception e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "An error occurred");
        } finally {
            closeResources(null, checkStmt, checkRs);
            if (updateStmt != null) { try { updateStmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
        
        out.print(gson.toJson(result));
        out.flush();
    }
    
    private void togglePatientStatus(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Patient ID is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            
            int id = Integer.parseInt(idParam);
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            String sql = "UPDATE users SET status = CASE " +
                        "WHEN status = 'active' THEN 'blocked' " +
                        "WHEN status = 'blocked' THEN 'active' " +
                        "ELSE 'active' END " +
                        "WHERE id = ? AND role = 'patient'";
            
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                conn.commit();
                result.put("success", true);
                result.put("message", "Patient status updated successfully");
            } else {
                conn.rollback();
                result.put("success", false);
                result.put("message", "Patient not found");
            }
            
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Database error");
        } catch (Exception e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "An error occurred");
        } finally {
            if (stmt != null) { try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
        
        out.print(gson.toJson(result));
        out.flush();
    }
    
    // ========================================================================
    // APPOINTMENT METHODS
    // ========================================================================

    private void getAppointmentData(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Appointment ID is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            
            int id = Integer.parseInt(idParam);
            conn = DBConnection.getConnection();
            
            String sql = "SELECT a.id, a.patient_name, a.doctor_name, a.department, " +
                        "a.appointment_date, a.appointment_time, a.status, " +
                        "a.patient_id, a.doctor_id, a.notes " +
                        "FROM appointments a WHERE a.id = ?";
            
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                result.put("success", true);
                result.put("id", rs.getInt("id"));
                result.put("patientName", rs.getString("patient_name"));
                result.put("doctorName", rs.getString("doctor_name"));
                result.put("department", rs.getString("department"));
                result.put("date", rs.getString("appointment_date"));
                result.put("time", rs.getString("appointment_time"));
                result.put("status", rs.getString("status"));
                result.put("patientId", rs.getInt("patient_id"));
                result.put("doctorId", rs.getInt("doctor_id"));
                result.put("notes", rs.getString("notes"));
            } else {
                result.put("success", false);
                result.put("message", "Appointment not found");
            }
            
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid appointment ID");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Error fetching appointment data");
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        out.print(gson.toJson(result));
        out.flush();
    }

    private void updateAppointmentStatus(HttpServletRequest request, HttpServletResponse response,
                                         String newStatus) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Appointment ID is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            
            int id = Integer.parseInt(idParam);
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            String sql = "UPDATE appointments SET status = ? " +
                        "WHERE id = ? AND status = 'scheduled'";
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newStatus);
            stmt.setInt(2, id);
            
            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                conn.commit();
                result.put("success", true);
                result.put("message", "Appointment " + newStatus + " successfully");
                result.put("newStatus", newStatus);
            } else {
                conn.rollback();
                result.put("success", false);
                result.put("message", "Cannot update appointment. It may already be completed or cancelled.");
            }
            
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid appointment ID");
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Database error");
        } catch (Exception e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "An error occurred");
        } finally {
            if (stmt != null) { try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
        
        out.print(gson.toJson(result));
        out.flush();
    }
    
    // ========================================================================
    // DEPARTMENT METHODS — NEW
    // ========================================================================
    
    private void getDepartmentData(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Department ID is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            
            int id = Integer.parseInt(idParam);
            conn = DBConnection.getConnection();
            
            String sql = "SELECT d.id, d.name, d.description, d.head_id, u.fullname as head_name " +
                        "FROM departments d " +
                        "LEFT JOIN users u ON d.head_id = u.id " +
                        "WHERE d.id = ?";
            
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                result.put("success", true);
                result.put("id", rs.getInt("id"));
                result.put("name", rs.getString("name"));
                result.put("description", rs.getString("description") != null ? rs.getString("description") : "");
                result.put("headId", rs.getInt("head_id"));
                result.put("headName", rs.getString("head_name"));
            } else {
                result.put("success", false);
                result.put("message", "Department not found");
            }
            
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid department ID");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Error fetching department data");
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        out.print(gson.toJson(result));
        out.flush();
    }
    
    private void addDepartment(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet checkRs = null;
        
        System.out.println("========== ADD DEPARTMENT ==========");
        
        try {
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String headIdStr = request.getParameter("headId");
            
            System.out.println("Department Name: " + name);
            System.out.println("Head ID: " + headIdStr);
            
            if (name == null || name.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Department name is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            
            if (name.trim().length() < 2) {
                result.put("success", false);
                result.put("message", "Department name must be at least 2 characters");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            
            Integer headId = null;
            if (headIdStr != null && !headIdStr.trim().isEmpty() && !headIdStr.equals("0")) {
                try {
                    headId = Integer.parseInt(headIdStr.trim());
                } catch (NumberFormatException e) {
                    result.put("success", false);
                    result.put("message", "Invalid head ID");
                    out.print(gson.toJson(result)); out.flush(); return;
                }
            }
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if department name already exists
            String checkSql = "SELECT id FROM departments WHERE name = ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, name.trim());
            checkRs = checkStmt.executeQuery();
            
            if (checkRs.next()) {
                result.put("success", false);
                result.put("message", "Department name already exists");
                out.print(gson.toJson(result)); out.flush();
                conn.rollback(); return;
            }
            checkRs.close();
            checkStmt.close();
            
            // Insert department
            String sql = "INSERT INTO departments (name, description, head_id) VALUES (?, ?, ?)";
            insertStmt = conn.prepareStatement(sql);
            insertStmt.setString(1, name.trim());
            insertStmt.setString(2, description != null ? description.trim() : "");
            if (headId != null) {
                insertStmt.setInt(3, headId);
            } else {
                insertStmt.setNull(3, java.sql.Types.INTEGER);
            }
            
            int rows = insertStmt.executeUpdate();
            System.out.println("Rows inserted: " + rows);
            
            if (rows > 0) {
                conn.commit();
                result.put("success", true);
                result.put("message", "Department added successfully");
            } else {
                conn.rollback();
                result.put("success", false);
                result.put("message", "Failed to add department");
            }
            
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Database error: " + e.getMessage());
        } catch (Exception e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "An error occurred: " + e.getMessage());
        } finally {
            closeResources(conn, checkStmt, checkRs);
            if (insertStmt != null) { try { insertStmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
        
        out.print(gson.toJson(result));
        out.flush();
    }
    
    private void updateDepartment(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet checkRs = null;
        
        System.out.println("========== UPDATE DEPARTMENT ==========");
        
        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Department ID is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            
            int id = Integer.parseInt(idStr);
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String headIdStr = request.getParameter("headId");
            
            System.out.println("Updating department ID: " + id);
            System.out.println("New name: " + name);
            
            if (name == null || name.trim().length() < 2) {
                result.put("success", false);
                result.put("message", "Valid department name is required (min 2 characters)");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            
            Integer headId = null;
            if (headIdStr != null && !headIdStr.trim().isEmpty() && !headIdStr.equals("0")) {
                try {
                    headId = Integer.parseInt(headIdStr.trim());
                } catch (NumberFormatException e) {
                    result.put("success", false);
                    result.put("message", "Invalid head ID");
                    out.print(gson.toJson(result)); out.flush(); return;
                }
            }
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if another department has the same name
            String checkSql = "SELECT id FROM departments WHERE name = ? AND id != ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, name.trim());
            checkStmt.setInt(2, id);
            checkRs = checkStmt.executeQuery();
            
            if (checkRs.next()) {
                result.put("success", false);
                result.put("message", "Department name already exists");
                out.print(gson.toJson(result)); out.flush();
                conn.rollback(); return;
            }
            checkRs.close();
            checkStmt.close();
            
            // Update department
            String sql = "UPDATE departments SET name = ?, description = ?, head_id = ? WHERE id = ?";
            updateStmt = conn.prepareStatement(sql);
            updateStmt.setString(1, name.trim());
            updateStmt.setString(2, description != null ? description.trim() : "");
            if (headId != null) {
                updateStmt.setInt(3, headId);
            } else {
                updateStmt.setNull(3, java.sql.Types.INTEGER);
            }
            updateStmt.setInt(4, id);
            
            int rows = updateStmt.executeUpdate();
            System.out.println("Rows updated: " + rows);
            
            if (rows > 0) {
                conn.commit();
                result.put("success", true);
                result.put("message", "Department updated successfully");
            } else {
                conn.rollback();
                result.put("success", false);
                result.put("message", "Failed to update department or department not found");
            }
            
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid department ID format");
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Database error: " + e.getMessage());
        } catch (Exception e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "An error occurred: " + e.getMessage());
        } finally {
            closeResources(null, checkStmt, checkRs);
            if (updateStmt != null) { try { updateStmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
        
        out.print(gson.toJson(result));
        out.flush();
    }
    
    private void deleteDepartment(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement deleteStmt = null;
        ResultSet checkRs = null;
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Department ID is required");
                out.print(gson.toJson(result)); out.flush(); return;
            }
            
            int id = Integer.parseInt(idParam);
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if any doctors are assigned to this department
            String checkSql = "SELECT COUNT(*) as count FROM users WHERE department IN " +
                             "(SELECT name FROM departments WHERE id = ?) AND role = 'doctor'";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, id);
            checkRs = checkStmt.executeQuery();
            
            if (checkRs.next() && checkRs.getInt("count") > 0) {
                result.put("success", false);
                result.put("message", "Cannot delete department with assigned doctors");
                out.print(gson.toJson(result)); out.flush();
                conn.rollback(); return;
            }
            checkRs.close();
            checkStmt.close();
            
            // Delete department
            String sql = "DELETE FROM departments WHERE id = ?";
            deleteStmt = conn.prepareStatement(sql);
            deleteStmt.setInt(1, id);
            
            int rows = deleteStmt.executeUpdate();
            
            if (rows > 0) {
                conn.commit();
                result.put("success", true);
                result.put("message", "Department deleted successfully");
            } else {
                conn.rollback();
                result.put("success", false);
                result.put("message", "Failed to delete department or department not found");
            }
            
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid department ID format");
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Database error: " + e.getMessage());
        } catch (Exception e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "An error occurred: " + e.getMessage());
        } finally {
            closeResources(null, checkStmt, checkRs);
            if (deleteStmt != null) { try { deleteStmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
        
        out.print(gson.toJson(result));
        out.flush();
    }
    
    // ========================================================================
    // RESOURCE CLEANUP & HELPER METHODS
    // ========================================================================
    
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try {
            if (conn != null) { conn.setAutoCommit(true); conn.close(); }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    private Map<String, Integer> getStatistics(Connection conn) throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        
        String sql = "SELECT COUNT(*) as count FROM users WHERE role = 'doctor' AND status = 'active'";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) stats.put("totalDoctors", rs.getInt("count"));
        }
        sql = "SELECT COUNT(*) as count FROM users WHERE role = 'patient' AND status = 'active'";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) stats.put("totalPatients", rs.getInt("count"));
        }
        sql = "SELECT COUNT(*) as count FROM appointments";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) stats.put("totalAppointments", rs.getInt("count"));
        }
        sql = "SELECT COUNT(*) as count FROM appointments WHERE DATE(appointment_date) = CURDATE()";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) stats.put("todayAppointments", rs.getInt("count"));
        }
        return stats;
    }
    
    private List<Map<String, String>> getDoctors(Connection conn) throws SQLException {
        List<Map<String, String>> doctors = new ArrayList<>();
        String sql = "SELECT u.id, u.fullname as name, u.email, u.phone, u.specialty, " +
                    "u.qualification, u.experience, u.department, u.status, " +
                    "(SELECT COUNT(*) FROM appointments WHERE doctor_id = u.id) as patient_count " +
                    "FROM users u WHERE u.role = 'doctor' ORDER BY u.id DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, String> doctor = new HashMap<>();
                doctor.put("id", String.valueOf(rs.getInt("id")));
                doctor.put("name", rs.getString("name"));
                doctor.put("email", rs.getString("email"));
                doctor.put("phone", rs.getString("phone"));
                doctor.put("specialty", rs.getString("specialty"));
                doctor.put("qualification", rs.getString("qualification"));
                doctor.put("experience", String.valueOf(rs.getInt("experience")));
                doctor.put("department", rs.getString("department"));
                doctor.put("status", rs.getString("status"));
                doctor.put("patientCount", String.valueOf(rs.getInt("patient_count")));
                doctors.add(doctor);
            }
        }
        return doctors;
    }
    
    private List<Map<String, String>> getPatients(Connection conn) throws SQLException {
        List<Map<String, String>> patients = new ArrayList<>();
        String sql = "SELECT id, fullname as name, email, phone, age, gender, address, status " +
                    "FROM users WHERE role = 'patient' ORDER BY id DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, String> patient = new HashMap<>();
                patient.put("id", String.valueOf(rs.getInt("id")));
                patient.put("name", rs.getString("name"));
                patient.put("email", rs.getString("email"));
                patient.put("phone", rs.getString("phone"));
                patient.put("age", String.valueOf(rs.getInt("age")));
                patient.put("gender", rs.getString("gender"));
                patient.put("address", rs.getString("address"));
                patient.put("status", rs.getString("status"));
                patients.add(patient);
            }
        }
        return patients;
    }
    
    private List<Map<String, String>> getAppointments(Connection conn) throws SQLException {
        List<Map<String, String>> appointments = new ArrayList<>();
        String sql = "SELECT a.id, a.appointment_date, a.appointment_time, a.status, " +
                    "a.patient_name, a.doctor_name, a.department " +
                    "FROM appointments a " +
                    "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, String> apt = new HashMap<>();
                apt.put("id", String.valueOf(rs.getInt("id")));
                apt.put("patientName", rs.getString("patient_name"));
                apt.put("doctorName", rs.getString("doctor_name"));
                apt.put("department", rs.getString("department"));
                apt.put("date", rs.getString("appointment_date"));
                apt.put("time", rs.getString("appointment_time"));
                apt.put("status", rs.getString("status"));
                appointments.add(apt);
            }
        }
        return appointments;
    }
    
    private List<Map<String, String>> getTodayAppointments(Connection conn) throws SQLException {
        List<Map<String, String>> appointments = new ArrayList<>();
        String sql = "SELECT a.id, a.appointment_time, a.status, a.patient_name, a.patient_age, " +
                    "a.patient_gender, a.doctor_name, a.department " +
                    "FROM appointments a WHERE DATE(a.appointment_date) = CURDATE() " +
                    "ORDER BY a.appointment_time";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, String> apt = new HashMap<>();
                apt.put("id", String.valueOf(rs.getInt("id")));
                apt.put("patientName", rs.getString("patient_name"));
                apt.put("age", String.valueOf(rs.getInt("patient_age")));
                apt.put("gender", rs.getString("patient_gender"));
                apt.put("doctorName", rs.getString("doctor_name"));
                apt.put("department", rs.getString("department"));
                apt.put("time", rs.getString("appointment_time"));
                apt.put("status", rs.getString("status"));
                appointments.add(apt);
            }
        }
        return appointments;
    }
    
    private List<Map<String, String>> getPrescriptions(Connection conn) throws SQLException {
        List<Map<String, String>> prescriptions = new ArrayList<>();
        String sql = "SELECT pr.id, pr.medicine_name, pr.dosage, pr.duration, pr.prescribed_date, " +
                    "pr.patient_name, pr.doctor_name " +
                    "FROM prescriptions pr ORDER BY pr.prescribed_date DESC LIMIT 50";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, String> rx = new HashMap<>();
                rx.put("id", String.valueOf(rs.getInt("id")));
                rx.put("patientName", rs.getString("patient_name"));
                rx.put("doctorName", rs.getString("doctor_name"));
                rx.put("medicine", rs.getString("medicine_name"));
                rx.put("dosage", rs.getString("dosage"));
                rx.put("duration", rs.getString("duration"));
                rx.put("date", rs.getString("prescribed_date"));
                prescriptions.add(rx);
            }
        }
        return prescriptions;
    }
    
    private List<Map<String, String>> getRecentActivities(Connection conn) throws SQLException {
        List<Map<String, String>> activities = new ArrayList<>();
        String sql = "SELECT CONCAT('New appointment: ', a.patient_name, ' with Dr. ', a.doctor_name) as activity_text, " +
                    "a.created_at as activity_time " +
                    "FROM appointments a ORDER BY a.created_at DESC LIMIT 10";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, String> activity = new HashMap<>();
                activity.put("text", rs.getString("activity_text"));
                activity.put("time", getRelativeTime(rs.getTimestamp("activity_time")));
                activities.add(activity);
            }
        } catch (SQLException e) {
            System.out.println("Activities query error: " + e.getMessage());
        }
        return activities;
    }
    
    private List<Map<String, String>> getDepartmentStats(Connection conn) throws SQLException {
        List<Map<String, String>> departments = new ArrayList<>();
        String deptSql = "SELECT d.id, d.name, d.description, d.head_id, " +
                        "u.fullname as head_name, " +
                        "(SELECT COUNT(*) FROM users WHERE department = d.name AND role = 'doctor' AND status = 'active') as doctor_count, " +
                        "(SELECT COUNT(DISTINCT a.patient_id) FROM appointments a " +
                        " JOIN users doc ON a.doctor_id = doc.id " +
                        " WHERE doc.department = d.name) as patient_count " +
                        "FROM departments d " +
                        "LEFT JOIN users u ON d.head_id = u.id " +
                        "ORDER BY d.name";
        try (PreparedStatement stmt = conn.prepareStatement(deptSql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, String> dept = new HashMap<>();
                dept.put("id", String.valueOf(rs.getInt("id")));
                dept.put("name", rs.getString("name"));
                dept.put("description", rs.getString("description") != null ? rs.getString("description") : "");
                dept.put("headId", rs.getInt("head_id") > 0 ? String.valueOf(rs.getInt("head_id")) : "");
                dept.put("headName", rs.getString("head_name") != null ? rs.getString("head_name") : "Not Assigned");
                dept.put("doctorCount", String.valueOf(rs.getInt("doctor_count")));
                dept.put("patientCount", String.valueOf(rs.getInt("patient_count")));
                departments.add(dept);
            }
        }
        return departments;
    }
    
    private List<Map<String, String>> getDoctorSchedules(Connection conn) throws SQLException {
        return new ArrayList<>();
    }
    
    private List<Map<String, String>> getUsers(Connection conn) throws SQLException {
        List<Map<String, String>> users = new ArrayList<>();
        String sql = "SELECT id, fullname, email, phone, role, status, last_login " +
                    "FROM users ORDER BY id DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, String> user = new HashMap<>();
                user.put("id", String.valueOf(rs.getInt("id")));
                user.put("name", rs.getString("fullname"));
                user.put("email", rs.getString("email"));
                user.put("phone", rs.getString("phone"));
                user.put("role", rs.getString("role"));
                user.put("status", rs.getString("status"));
                Timestamp lastLogin = rs.getTimestamp("last_login");
                user.put("lastLogin", lastLogin != null ? formatLastLogin(lastLogin) : "Never");
                users.add(user);
            }
        }
        return users;
    }
    
    private String formatLastLogin(Timestamp timestamp) {
        if (timestamp == null) return "Never";
        long diff = System.currentTimeMillis() - timestamp.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        if (seconds < 60) return "Just now";
        if (minutes < 60) return minutes + " min ago";
        if (hours < 24) return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        if (days < 7) return days + " day" + (days > 1 ? "s" : "") + " ago";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy");
        return sdf.format(new java.util.Date(timestamp.getTime()));
    }
    
   private Map<String, String> getHospitalSettings(Connection conn) throws SQLException {
    Map<String, String> settings = new HashMap<>();
    
    // Default values
    settings.put("hospitalName", "City Hospital");
    settings.put("address", "123 Medical Street, Health City");
    settings.put("phone", "+1234567890");
    settings.put("email", "info@cityhospital.com");
    settings.put("openingTime", "08:00");
    settings.put("closingTime", "20:00");
    settings.put("appointmentDuration", "30");
    settings.put("website", "");
    
    try {
        String sql = "SELECT hospital_name, address, phone, email, " +
                    "TIME_FORMAT(opening_time, '%H:%i') as opening_time, " +
                    "TIME_FORMAT(closing_time, '%H:%i') as closing_time, " +
                    "appointment_duration, website " +
                    "FROM hospital_settings LIMIT 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                settings.put("hospitalName", rs.getString("hospital_name"));
                settings.put("address", rs.getString("address"));
                settings.put("phone", rs.getString("phone"));
                settings.put("email", rs.getString("email"));
                settings.put("openingTime", rs.getString("opening_time"));
                settings.put("closingTime", rs.getString("closing_time"));
                settings.put("appointmentDuration", String.valueOf(rs.getInt("appointment_duration")));
                String website = rs.getString("website");
                settings.put("website", website != null ? website : "");
            }
        }
    } catch (SQLException e) {
        System.err.println("Error loading hospital settings: " + e.getMessage());
        e.printStackTrace();
        // Return default values if table doesn't exist or error occurs
    }
    
    return settings;
}

private List<Map<String, String>> getHolidays(Connection conn) throws SQLException {
    List<Map<String, String>> holidays = new ArrayList<>();
    
    try {
        String sql = "SELECT id, name, DATE_FORMAT(date, '%Y-%m-%d') as holiday_date, " +
                    "description, is_active " +
                    "FROM holidays " +
                    "WHERE is_active = 'yes' " +
                    "ORDER BY date";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, String> holiday = new HashMap<>();
                holiday.put("id", String.valueOf(rs.getInt("id")));
                holiday.put("name", rs.getString("name"));
                holiday.put("date", rs.getString("holiday_date"));
                String description = rs.getString("description");
                holiday.put("description", description != null ? description : "");
                holidays.add(holiday);
            }
        }
    } catch (SQLException e) {
        System.err.println("Error loading holidays: " + e.getMessage());
        e.printStackTrace();
        // Return empty list if table doesn't exist or error occurs
    }
    
    return holidays;
}
    
    private String getRelativeTime(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        long diff = System.currentTimeMillis() - timestamp.getTime();
        long minutes = diff / (60 * 1000);
        if (minutes < 60) return minutes + " minutes ago";
        return (diff / (60 * 60 * 1000)) + " hours ago";
    }
}