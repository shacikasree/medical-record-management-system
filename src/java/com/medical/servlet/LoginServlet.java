package com.medical.servlet;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("Login.jsp");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String selectedRole = request.getParameter("role");
        
        System.out.println("========== LOGIN ATTEMPT ==========");
        System.out.println("Email: " + email);
        System.out.println("Selected Role: " + selectedRole);
        
        // Validation - Check for empty fields
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty() ||
            selectedRole == null || selectedRole.trim().isEmpty()) {
            System.out.println("❌ Empty fields");
            response.sendRedirect("Login.jsp?error=emptyfields");
            return;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // Query user by email
            String sql = "SELECT id, email, password, fullname, role, status FROM users WHERE email = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email.trim().toLowerCase());
            rs = stmt.executeQuery();
            
            if (!rs.next()) {
                System.out.println("❌ User not found: " + email);
                response.sendRedirect("Login.jsp?error=invalidUser");
                return;
            }
            
            // User found - Get details
            int userId = rs.getInt("id");
            String hashedPassword = rs.getString("password");
            String fullname = rs.getString("fullname");
            String actualRole = rs.getString("role");
            String status = rs.getString("status");
            
            System.out.println("✅ User found: " + email);
            System.out.println("Actual Role: " + actualRole);
            System.out.println("Selected Role: " + selectedRole);
            System.out.println("Status: " + status);
            
            // Check if account is active
            if (!"active".equals(status)) {
                System.out.println("❌ Account is not active");
                response.sendRedirect("Login.jsp?error=inactive");
                return;
            }
            
            // CHECK ROLE MATCH
            if (!selectedRole.equalsIgnoreCase(actualRole)) {
                System.out.println("❌ ROLE MISMATCH!");
                response.sendRedirect("Login.jsp?error=roleMismatch");
                return;
            }
            
            System.out.println("✅ Role matched");
            
            // Verify password
            boolean passwordMatch = false;
            try {
                passwordMatch = BCrypt.checkpw(password, hashedPassword);
                System.out.println("BCrypt verification: " + (passwordMatch ? "✅ SUCCESS" : "❌ FAILED"));
            } catch (Exception e) {
                passwordMatch = password.equals(hashedPassword);
                System.out.println("Plain text verification: " + (passwordMatch ? "✅ SUCCESS" : "❌ FAILED"));
            }
            
            if (!passwordMatch) {
                System.out.println("❌ Password verification failed");
                response.sendRedirect("Login.jsp?error=invalidPass");
                return;
            }
            
            System.out.println("✅ Login successful for: " + email);
            
            // UPDATE LAST LOGIN
            try {
                String updateLoginSql = "UPDATE users SET last_login = NOW() WHERE id = ?";
                updateStmt = conn.prepareStatement(updateLoginSql);
                updateStmt.setInt(1, userId);
                updateStmt.executeUpdate();
                System.out.println("✅ Last login updated");
            } catch (SQLException e) {
                System.out.println("⚠️ Last login update failed: " + e.getMessage());
            }
            
            // Close first ResultSet
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            
            // Create session
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", userId);
            session.setAttribute("email", email);
            session.setAttribute("role", actualRole);
            session.setAttribute("fullname", fullname != null ? fullname : "");
            session.setMaxInactiveInterval(30 * 60);
            
            System.out.println("✅ Session created");
            
            // Redirect based on role
            if ("admin".equalsIgnoreCase(actualRole)) {
                System.out.println("✅ Admin login");
                response.sendRedirect("AdminServlet");
                
            } else if ("doctor".equalsIgnoreCase(actualRole)) {
                // ========== DOCTOR LOGIN - LOAD ALL PROFILE DATA ==========
                System.out.println("✅ Doctor login - loading ALL profile data...");
                
                try {
                    // Fetch ALL profile data from users table
                    String doctorSql = "SELECT phone, address, department, specialty, " +
                                      "qualification, experience, license_number " +
                                      "FROM users WHERE id = ? AND role = 'doctor'";
                    
                    stmt = conn.prepareStatement(doctorSql);
                    stmt.setInt(1, userId);
                    rs = stmt.executeQuery();
                    
                    if (rs.next()) {
                        // Get data from database
                        String phone = rs.getString("phone");
                        String address = rs.getString("address");
                        String department = rs.getString("department");
                        String specialty = rs.getString("specialty");
                        String qualification = rs.getString("qualification");
                        Object expObj = rs.getObject("experience");
                        String experience = (expObj != null) ? String.valueOf(expObj) : "";
                        String licenseNumber = rs.getString("license_number");
                        
                        // Calculate initials
                        String initials = "DR";
                        if (fullname != null && !fullname.trim().isEmpty()) {
                            String[] names = fullname.trim().split("\\s+");
                            StringBuilder initialsBuilder = new StringBuilder();
                            for (String name : names) {
                                if (!name.isEmpty()) {
                                    initialsBuilder.append(name.charAt(0));
                                }
                            }
                            initials = initialsBuilder.toString().toUpperCase();
                            if (initials.length() > 2) {
                                initials = initials.substring(0, 2);
                            }
                        }
                        
                        // Set ALL session attributes with null checks
                        session.setAttribute("doctorId", userId);
                        session.setAttribute("initials", initials);
                        session.setAttribute("phone", phone != null ? phone : "");
                        session.setAttribute("address", address != null ? address : "");
                        session.setAttribute("department", department != null ? department : "");
                        session.setAttribute("specialty", specialty != null ? specialty : "");
                        session.setAttribute("qualification", qualification != null ? qualification : "");
                        session.setAttribute("qualifications", qualification != null ? qualification : "");
                        session.setAttribute("experience", experience);
                        session.setAttribute("license_number", licenseNumber != null ? licenseNumber : "");
                        session.setAttribute("licenseNumber", licenseNumber != null ? licenseNumber : "");
                        
                        // Detailed debug output
                        System.out.println("╔════════════════════════════════════════════════════════════╗");
                        System.out.println("║          DOCTOR PROFILE DATA LOADED                        ║");
                        System.out.println("╠════════════════════════════════════════════════════════════╣");
                        System.out.println("║ Doctor ID:      " + userId);
                        System.out.println("║ Name:           " + fullname);
                        System.out.println("║ Email:          " + email);
                        System.out.println("║ Phone:          " + (phone != null ? phone : "NULL"));
                        System.out.println("║ Address:        " + (address != null ? address : "NULL"));
                        System.out.println("║ Department:     " + (department != null ? department : "NULL"));
                        System.out.println("║ Specialty:      " + (specialty != null ? specialty : "NULL"));
                        System.out.println("║ Qualification:  " + (qualification != null ? qualification : "NULL"));
                        System.out.println("║ Experience:     " + experience);
                        System.out.println("║ License:        " + (licenseNumber != null ? licenseNumber : "NULL"));
                        System.out.println("║ Initials:       " + initials);
                        System.out.println("╚════════════════════════════════════════════════════════════╝");
                        
                    } else {
                        System.out.println("⚠️ No profile data found for doctor ID: " + userId);
                        // Set empty defaults
                        session.setAttribute("doctorId", userId);
                        session.setAttribute("initials", "DR");
                        session.setAttribute("phone", "");
                        session.setAttribute("address", "");
                        session.setAttribute("department", "");
                        session.setAttribute("specialty", "");
                        session.setAttribute("qualification", "");
                        session.setAttribute("qualifications", "");
                        session.setAttribute("experience", "");
                        session.setAttribute("license_number", "");
                        session.setAttribute("licenseNumber", "");
                    }
                    
                } catch (SQLException e) {
                    System.out.println("❌ ERROR loading profile data:");
                    System.out.println("   Message: " + e.getMessage());
                    System.out.println("   SQL State: " + e.getSQLState());
                    System.out.println("   Error Code: " + e.getErrorCode());
                    e.printStackTrace();
                    
                    // Set empty defaults on error
                    session.setAttribute("doctorId", userId);
                    session.setAttribute("initials", "DR");
                    session.setAttribute("phone", "");
                    session.setAttribute("address", "");
                    session.setAttribute("department", "");
                    session.setAttribute("specialty", "");
                    session.setAttribute("qualification", "");
                    session.setAttribute("qualifications", "");
                    session.setAttribute("experience", "");
                    session.setAttribute("license_number", "");
                    session.setAttribute("licenseNumber", "");
                }
                
                System.out.println("✅ Redirecting to DoctorServlet");
                response.sendRedirect("DoctorServlet");
                
            } else if ("patient".equalsIgnoreCase(actualRole)) {
                // ========== PATIENT LOGIN - LOAD ALL PROFILE DATA ==========
                System.out.println("✅ Patient login - loading ALL profile data...");
                
                try {
                    // Fetch ALL profile data from users table
                    String patientSql = "SELECT phone, address, date_of_birth, age, gender, " +
                                      "blood_group, emergency_contact, emergency_contact_name " +
                                      "FROM users WHERE id = ? AND role = 'patient'";
                    
                    stmt = conn.prepareStatement(patientSql);
                    stmt.setInt(1, userId);
                    rs = stmt.executeQuery();
                    
                    if (rs.next()) {
                        // Get data from database
                        String phone = rs.getString("phone");
                        String address = rs.getString("address");
                        String dateOfBirth = rs.getString("date_of_birth");
                        Object ageObj = rs.getObject("age");
                        String age = (ageObj != null) ? String.valueOf(ageObj) : "";
                        String gender = rs.getString("gender");
                        String bloodGroup = rs.getString("blood_group");
                        String emergencyContact = rs.getString("emergency_contact");
                        String emergencyContactName = rs.getString("emergency_contact_name");
                        
                        // Calculate initials
                        String initials = "P";
                        if (fullname != null && !fullname.trim().isEmpty()) {
                            String[] names = fullname.trim().split("\\s+");
                            StringBuilder initialsBuilder = new StringBuilder();
                            for (String name : names) {
                                if (!name.isEmpty()) {
                                    initialsBuilder.append(name.charAt(0));
                                }
                            }
                            initials = initialsBuilder.toString().toUpperCase();
                            if (initials.length() > 2) {
                                initials = initials.substring(0, 2);
                            }
                        }
                        
                        // Set ALL session attributes with null checks
                        session.setAttribute("patientId", userId);
                        session.setAttribute("initials", initials);
                        session.setAttribute("phone", phone != null ? phone : "");
                        session.setAttribute("address", address != null ? address : "");
                        session.setAttribute("dob", dateOfBirth != null ? dateOfBirth : "");
                        session.setAttribute("date_of_birth", dateOfBirth != null ? dateOfBirth : "");
                        session.setAttribute("age", age);
                        session.setAttribute("gender", gender != null ? gender : "");
                        session.setAttribute("blood", bloodGroup != null ? bloodGroup : "");
                        session.setAttribute("blood_group", bloodGroup != null ? bloodGroup : "");
                        session.setAttribute("emergency_contact", emergencyContact != null ? emergencyContact : "");
                        session.setAttribute("emergency_contact_name", emergencyContactName != null ? emergencyContactName : "");
                        
                        // Detailed debug output
                        System.out.println("╔════════════════════════════════════════════════════════════╗");
                        System.out.println("║          PATIENT PROFILE DATA LOADED                       ║");
                        System.out.println("╠════════════════════════════════════════════════════════════╣");
                        System.out.println("║ Patient ID:     " + userId);
                        System.out.println("║ Name:           " + fullname);
                        System.out.println("║ Email:          " + email);
                        System.out.println("║ Phone:          " + (phone != null ? phone : "NULL"));
                        System.out.println("║ Address:        " + (address != null ? address : "NULL"));
                        System.out.println("║ DOB:            " + (dateOfBirth != null ? dateOfBirth : "NULL"));
                        System.out.println("║ Age:            " + age);
                        System.out.println("║ Gender:         " + (gender != null ? gender : "NULL"));
                        System.out.println("║ Blood Group:    " + (bloodGroup != null ? bloodGroup : "NULL"));
                        System.out.println("║ Emergency:      " + (emergencyContact != null ? emergencyContact : "NULL"));
                        System.out.println("║ Initials:       " + initials);
                        System.out.println("╚════════════════════════════════════════════════════════════╝");
                        
                    } else {
                        System.out.println("⚠️ No profile data found for patient ID: " + userId);
                        // Set empty defaults
                        session.setAttribute("patientId", userId);
                        session.setAttribute("initials", "P");
                        session.setAttribute("phone", "");
                        session.setAttribute("address", "");
                        session.setAttribute("dob", "");
                        session.setAttribute("date_of_birth", "");
                        session.setAttribute("age", "");
                        session.setAttribute("gender", "");
                        session.setAttribute("blood", "");
                        session.setAttribute("blood_group", "");
                        session.setAttribute("emergency_contact", "");
                        session.setAttribute("emergency_contact_name", "");
                    }
                    
                } catch (SQLException e) {
                    System.out.println("❌ ERROR loading patient profile data:");
                    System.out.println("   Message: " + e.getMessage());
                    System.out.println("   SQL State: " + e.getSQLState());
                    System.out.println("   Error Code: " + e.getErrorCode());
                    e.printStackTrace();
                    
                    // Set empty defaults on error
                    session.setAttribute("patientId", userId);
                    session.setAttribute("initials", "P");
                    session.setAttribute("phone", "");
                    session.setAttribute("address", "");
                    session.setAttribute("dob", "");
                    session.setAttribute("date_of_birth", "");
                    session.setAttribute("age", "");
                    session.setAttribute("gender", "");
                    session.setAttribute("blood", "");
                    session.setAttribute("blood_group", "");
                    session.setAttribute("emergency_contact", "");
                    session.setAttribute("emergency_contact_name", "");
                }
                
                System.out.println("✅ Redirecting to PatientServlet");
                response.sendRedirect("PatientServlet");
                
            } else {
                System.out.println("❌ Unknown role: " + actualRole);
                response.sendRedirect("Login.jsp?error=invalidrole");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Database error: " + e.getMessage());
            response.sendRedirect("Login.jsp?error=database");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Unexpected error: " + e.getMessage());
            response.sendRedirect("Login.jsp?error=server");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (updateStmt != null) updateStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}