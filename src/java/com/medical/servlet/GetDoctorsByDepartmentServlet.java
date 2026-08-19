package com.medical.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

@WebServlet("/getDoctorsByDepartment")
public class GetDoctorsByDepartmentServlet extends HttpServlet {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/medical_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "shacika@@2006";
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ [GetDoctors] MySQL Driver loaded");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ [GetDoctors] MySQL Driver not found!");
            e.printStackTrace();
        }
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String department = request.getParameter("department");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        
        PrintWriter out = response.getWriter();
        
        System.out.println("========================================");
        System.out.println("🔍 [GetDoctors] Request received");
        System.out.println("   Department parameter: '" + department + "'");
        
        if(department == null || department.trim().isEmpty()) {
            System.out.println("❌ [GetDoctors] Department is null or empty");
            out.write("[]");
            return;
        }
        
        List<Doctor> doctors = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✅ [GetDoctors] Database connected");
            
            // First, let's check what columns exist
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "users", null);
            System.out.println("📋 [GetDoctors] Available columns in 'users' table:");
            while(columns.next()) {
                System.out.println("   - " + columns.getString("COLUMN_NAME"));
            }
            columns.close();
            
            // Try to detect the correct ID column
            String idColumn = "user_id"; // default
            String checkColumnQuery = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                                     "WHERE TABLE_SCHEMA = 'medical_db' AND TABLE_NAME = 'users' " +
                                     "AND COLUMN_NAME IN ('user_id', 'id', 'userId')";
            
            PreparedStatement checkStmt = conn.prepareStatement(checkColumnQuery);
            ResultSet checkRs = checkStmt.executeQuery();
            if(checkRs.next()) {
                idColumn = checkRs.getString("COLUMN_NAME");
                System.out.println("✅ [GetDoctors] Using ID column: " + idColumn);
            }
            checkRs.close();
            checkStmt.close();
            
            // Build query with detected column name
            String query = "SELECT " + idColumn + ", fullname, email, department, specialty, qualification, status " +
                          "FROM users " +
                          "WHERE role = 'doctor' AND department = ? " +
                          "ORDER BY fullname";
            
            System.out.println("📝 [GetDoctors] Query: " + query);
            
            stmt = conn.prepareStatement(query);
            stmt.setString(1, department);
            rs = stmt.executeQuery();
            
            System.out.println("🔍 [GetDoctors] Executing query...");
            
            int count = 0;
            while(rs.next()) {
                count++;
                Doctor doctor = new Doctor();
                doctor.doctorId = rs.getInt(idColumn);
                doctor.name = rs.getString("fullname");
                doctor.email = rs.getString("email");
                doctor.specialty = rs.getString("specialty");
                doctor.qualification = rs.getString("qualification");
                doctor.status = rs.getString("status");
                
                System.out.println("   Doctor #" + count + ":");
                System.out.println("      ID: " + doctor.doctorId);
                System.out.println("      Name: " + doctor.name);
                System.out.println("      Email: " + doctor.email);
                System.out.println("      Department: " + rs.getString("department"));
                System.out.println("      Status: " + doctor.status);
                
                doctors.add(doctor);
            }
            
            System.out.println("✅ [GetDoctors] Total doctors found: " + doctors.size());
            
            // If no doctors found, let's debug
            if(doctors.isEmpty()) {
                System.out.println("⚠️ [GetDoctors] No doctors found! Debugging...");
                
                // Check all doctors in database
                String debugQuery = "SELECT " + idColumn + ", fullname, department, role, status FROM users WHERE role = 'doctor'";
                PreparedStatement debugStmt = conn.prepareStatement(debugQuery);
                ResultSet debugRs = debugStmt.executeQuery();
                
                System.out.println("📋 [GetDoctors] All doctors in database:");
                int debugCount = 0;
                while(debugRs.next()) {
                    debugCount++;
                    System.out.println("   " + debugCount + ". " + debugRs.getString("fullname") + 
                                     " - Dept: '" + debugRs.getString("department") + 
                                     "' - Status: '" + debugRs.getString("status") + "'");
                }
                
                if(debugCount == 0) {
                    System.out.println("❌ [GetDoctors] NO DOCTORS IN DATABASE AT ALL!");
                } else {
                    System.out.println("⚠️ [GetDoctors] Found " + debugCount + " doctors in DB but none match department: '" + department + "'");
                    System.out.println("💡 [GetDoctors] Check for:");
                    System.out.println("   1. Exact spelling match");
                    System.out.println("   2. Extra spaces in department name");
                    System.out.println("   3. Status is 'active'");
                }
                
                debugRs.close();
                debugStmt.close();
            }
            
            Gson gson = new Gson();
            String json = gson.toJson(doctors);
            out.write(json);
            System.out.println("📤 [GetDoctors] Response sent: " + json);
            System.out.println("========================================");
            
        } catch(SQLException e) {
            System.err.println("❌ [GetDoctors] SQL Error:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("   Error Code: " + e.getErrorCode());
            e.printStackTrace();
            out.write("{\"error\": \"Database error: " + e.getMessage() + "\"}");
        } catch(Exception e) {
            System.err.println("❌ [GetDoctors] Error: " + e.getMessage());
            e.printStackTrace();
            out.write("{\"error\": \"" + e.getMessage() + "\"}");
        } finally {
            try {
                if(rs != null) rs.close();
                if(stmt != null) stmt.close();
                if(conn != null) conn.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    class Doctor {
        int doctorId;
        String name;
        String email;
        String specialty;
        String qualification;
        String status;
    }
}