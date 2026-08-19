package com.medical.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get common form parameters
        String fullname = request.getParameter("fullname");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String dobStr = request.getParameter("dob");
        String gender = request.getParameter("gender");
        String bloodGroup = request.getParameter("bloodGroup");
        String role = request.getParameter("role");
        String address = request.getParameter("address");

        // Validate required fields
        if (fullname == null || fullname.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            phone == null || phone.trim().isEmpty() ||
            dobStr == null || dobStr.trim().isEmpty() ||
            gender == null || gender.trim().isEmpty() ||
            bloodGroup == null || bloodGroup.trim().isEmpty() ||
            role == null || role.trim().isEmpty()) {
            
            response.sendRedirect("Register.jsp?error=emptyFields");
            return;
        }

        // Validate password strength
        if (password.length() < 6) {
            response.sendRedirect("Register.jsp?error=weakPassword");
            return;
        }

        // Get role-specific parameters
        String specialty = request.getParameter("specialty");
        String qualification = request.getParameter("qualification");
        String experienceStr = request.getParameter("experience");
        String licenseNumber = request.getParameter("licenseNumber");
        String department = request.getParameter("department");
        String emergencyContact = request.getParameter("emergencyContact");
        String emergencyContactName = request.getParameter("emergencyContactName");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Get database connection
            conn = DBConnection.getConnection();
            
            if (conn == null) {
                response.sendRedirect("Register.jsp?error=databaseError");
                return;
            }

            // Check if email already exists
            String checkEmailSql = "SELECT COUNT(*) as count FROM users WHERE email = ?";
            pstmt = conn.prepareStatement(checkEmailSql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            
            if (rs.next() && rs.getInt("count") > 0) {
                response.sendRedirect("Register.jsp?error=emailExists");
                return;
            }
            
            // Close the previous statement
            rs.close();
            pstmt.close();

            // ========== ENCRYPT PASSWORD ==========
            String hashedPassword = PasswordUtil.hashPassword(password);
            System.out.println("Password encrypted successfully");
            
            // Calculate age from date of birth
            LocalDate birthDate = LocalDate.parse(dobStr);
            LocalDate currentDate = LocalDate.now();
            int age = Period.between(birthDate, currentDate).getYears();

            // Parse experience for doctors
            Integer experience = null;
            if ("doctor".equals(role) && experienceStr != null && !experienceStr.trim().isEmpty()) {
                try {
                    experience = Integer.parseInt(experienceStr);
                } catch (NumberFormatException e) {
                    experience = 0;
                }
            }

            // SQL Insert statement
            String insertSql = "INSERT INTO users " +
                "(fullname, email, password, phone, date_of_birth, age, gender, address, " +
                "blood_group, specialty, qualification, experience, license_number, department, " +
                "emergency_contact, emergency_contact_name, role, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'active', NOW())";

            pstmt = conn.prepareStatement(insertSql);
            
            // Set parameters
            pstmt.setString(1, fullname);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword); // ✅ Using encrypted password
            pstmt.setString(4, phone);
            pstmt.setDate(5, Date.valueOf(birthDate));
            pstmt.setInt(6, age);
            pstmt.setString(7, gender);
            pstmt.setString(8, address);
            pstmt.setString(9, bloodGroup);
            
            // Role-specific fields
            if ("doctor".equals(role)) {
                pstmt.setString(10, specialty);
                pstmt.setString(11, qualification);
                pstmt.setObject(12, experience);
                pstmt.setString(13, licenseNumber);
                pstmt.setString(14, department);
                pstmt.setNull(15, java.sql.Types.VARCHAR);
                pstmt.setNull(16, java.sql.Types.VARCHAR);
            } else if ("patient".equals(role)) {
                pstmt.setNull(10, java.sql.Types.VARCHAR);
                pstmt.setNull(11, java.sql.Types.VARCHAR);
                pstmt.setNull(12, java.sql.Types.INTEGER);
                pstmt.setNull(13, java.sql.Types.VARCHAR);
                pstmt.setNull(14, java.sql.Types.VARCHAR);
                pstmt.setString(15, emergencyContact);
                pstmt.setString(16, emergencyContactName);
            } else {
                pstmt.setNull(10, java.sql.Types.VARCHAR);
                pstmt.setNull(11, java.sql.Types.VARCHAR);
                pstmt.setNull(12, java.sql.Types.INTEGER);
                pstmt.setNull(13, java.sql.Types.VARCHAR);
                pstmt.setNull(14, java.sql.Types.VARCHAR);
                pstmt.setNull(15, java.sql.Types.VARCHAR);
                pstmt.setNull(16, java.sql.Types.VARCHAR);
            }
            
            pstmt.setString(17, role);

            // Execute insert
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Registration successful for: " + email);
                response.sendRedirect("Login.jsp?success=registered");
            } else {
                response.sendRedirect("Register.jsp?error=registrationFailed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Registration Error: " + e.getMessage());
            response.sendRedirect("Register.jsp?error=systemError");
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("Register.jsp");
    }
}