/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.medical.servlet;


import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/GetPrescriptionDetailsServlet")
public class GetPrescriptionDetailsServlet extends HttpServlet {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/medical_db?";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "shacika@@2006";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("doctorId") == null) {
            sendErrorResponse(response, "Not authenticated");
            return;
        }
        
        String prescriptionId = request.getParameter("id");
        
        if (prescriptionId == null || prescriptionId.isEmpty()) {
            sendErrorResponse(response, "Prescription ID is required");
            return;
        }
        
        Connection conn = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            String sql = "SELECT p.*, u.fullname as patient_name, " +
                        "a.appointment_date, a.symptoms as diagnosis " +
                        "FROM prescriptions p " +
                        "JOIN users u ON p.user_id = u.id " +
                        "LEFT JOIN appointments a ON p.appointment_id = a.id " +
                        "WHERE p.id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, prescriptionId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    StringBuilder jsonResponse = new StringBuilder();
                    jsonResponse.append("{\"success\": true, \"prescription\": {");
                    jsonResponse.append("\"id\": \"").append(escapeJson(rs.getString("id"))).append("\",");
                    jsonResponse.append("\"patientId\": \"").append(escapeJson(rs.getString("user_id"))).append("\",");
                    jsonResponse.append("\"patientName\": \"").append(escapeJson(rs.getString("patient_name"))).append("\",");
                    jsonResponse.append("\"doctorName\": \"").append(escapeJson(rs.getString("doctor_name"))).append("\",");
                    jsonResponse.append("\"medicineName\": \"").append(escapeJson(rs.getString("medicine_name"))).append("\",");
                    jsonResponse.append("\"dosage\": \"").append(escapeJson(rs.getString("dosage"))).append("\",");
                    jsonResponse.append("\"frequency\": \"").append(escapeJson(rs.getString("frequency"))).append("\",");
                    jsonResponse.append("\"duration\": \"").append(escapeJson(rs.getString("duration"))).append("\",");
                    jsonResponse.append("\"instructions\": \"").append(escapeJson(rs.getString("instructions"))).append("\",");
                    jsonResponse.append("\"prescribedDate\": \"").append(escapeJson(rs.getString("prescribed_date"))).append("\",");
                    jsonResponse.append("\"status\": \"").append(escapeJson(rs.getString("status"))).append("\",");
                    jsonResponse.append("\"refillsRemaining\": ").append(rs.getInt("refills_remaining")).append(",");
                    
                    String diagnosis = rs.getString("diagnosis");
                    if (diagnosis == null || diagnosis.isEmpty()) {
                        diagnosis = "General consultation";
                    }
                    jsonResponse.append("\"diagnosis\": \"").append(escapeJson(diagnosis)).append("\",");
                    
                    // Get all medicines for this appointment
                    String appointmentId = rs.getString("appointment_id");
                    String medicinesJson = "[]";
                    if (appointmentId != null && !appointmentId.isEmpty()) {
                        medicinesJson = getAllMedicinesForAppointment(conn, appointmentId);
                    } else {
                        medicinesJson = "[{\"name\": \"" + escapeJson(rs.getString("medicine_name")) + 
                                       "\", \"dosage\": \"" + escapeJson(rs.getString("dosage")) + 
                                       "\", \"duration\": \"" + escapeJson(rs.getString("duration")) + "\"}]";
                    }
                    jsonResponse.append("\"medicines\": \"").append(medicinesJson.replace("\"", "\\\"")).append("\"");
                    
                    jsonResponse.append("}}");
                    
                    response.setContentType("application/json");
                    response.getWriter().write(jsonResponse.toString());
                    
                } else {
                    sendErrorResponse(response, "Prescription not found");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, "Error loading prescription: " + e.getMessage());
            
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    private String getAllMedicinesForAppointment(Connection conn, String appointmentId) throws SQLException {
        StringBuilder medicines = new StringBuilder("[");
        
        String sql = "SELECT medicine_name, dosage, duration FROM prescriptions " +
                    "WHERE appointment_id = ? ORDER BY id ASC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            
            boolean first = true;
            while (rs.next()) {
                if (!first) medicines.append(",");
                first = false;
                
                medicines.append("{");
                medicines.append("\"name\": \"").append(escapeJson(rs.getString("medicine_name"))).append("\",");
                medicines.append("\"dosage\": \"").append(escapeJson(rs.getString("dosage"))).append("\",");
                medicines.append("\"duration\": \"").append(escapeJson(rs.getString("duration"))).append("\"");
                medicines.append("}");
            }
        }
        
        medicines.append("]");
        return medicines.toString();
    }
    
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        String jsonResponse = "{\"success\": false, \"message\": \"" + escapeJson(message) + "\"}";
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
    }
}