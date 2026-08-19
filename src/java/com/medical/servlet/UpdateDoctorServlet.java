package com.medical.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/UpdateDoctorServlet")
public class UpdateDoctorServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        // Get parameters from request
        String doctorId = request.getParameter("doctorId");
        String fullname = request.getParameter("fullname");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String specialty = request.getParameter("specialty");
        String qualification = request.getParameter("qualification");
        String experience = request.getParameter("experience");
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            // Get database connection
            conn = DBConnection.getConnection();
            
            // SQL update query
            String sql = "UPDATE users SET fullname = ?, email = ?, phone = ?, specialty = ?, " +
                        "qualification = ?, experience = ? WHERE id = ? AND role = 'doctor'";
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, fullname);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, specialty);
            stmt.setString(5, qualification);
            stmt.setInt(6, Integer.parseInt(experience));
            stmt.setInt(7, Integer.parseInt(doctorId));
            
            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                out.print("{\"success\": true, \"message\": \"Doctor updated successfully\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to update doctor. Doctor not found.\"}");
            }
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"Invalid number format: " + e.getMessage() + "\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"Error: " + e.getMessage() + "\"}");
        } finally {
            // Close resources
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        out.flush();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("AdminServlet");
    }
}