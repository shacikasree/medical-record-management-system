package com.medical.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GetPatientRecords")
public class GetPatientRecords extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        String patientId = request.getParameter("patientId");
        
        System.out.println("========================================");
        System.out.println("📁 [GetPatientRecords] Fetching records for patient ID: " + patientId);
        
        if (patientId == null || patientId.trim().isEmpty()) {
            System.out.println("❌ No patient ID provided");
            out.print("{\"error\": \"Patient ID is required\"}");
            return;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // Get patient info
            String patientSql = "SELECT fullname, age, gender, blood_group FROM users WHERE id = ?";
            pstmt = conn.prepareStatement(patientSql);
            pstmt.setInt(1, Integer.parseInt(patientId));
            rs = pstmt.executeQuery();
            
            StringBuilder json = new StringBuilder();
            json.append("{");
            
            if (rs.next()) {
                String fullname = rs.getString("fullname");
                int age = rs.getInt("age");
                String gender = rs.getString("gender");
                String bloodGroup = rs.getString("blood_group");
                
                json.append("\"patientName\":\"").append(escapeJson(fullname)).append("\",");
                json.append("\"age\":").append(age).append(",");
                json.append("\"gender\":\"").append(escapeJson(gender)).append("\",");
                json.append("\"bloodGroup\":\"").append(escapeJson(bloodGroup)).append("\",");
                
                System.out.println("✅ Patient found: " + fullname);
            } else {
                System.out.println("❌ Patient not found");
                out.print("{\"error\": \"Patient not found\"}");
                return;
            }
            
            rs.close();
            pstmt.close();
            
            // Get appointment history
            String aptSql = "SELECT appointment_date, appointment_time, symptoms, " +
                          "status, diagnosis, notes, chief_complaint " +
                          "FROM appointments " +
                          "WHERE patient_id = ? " +
                          "ORDER BY appointment_date DESC, appointment_time DESC";
            
            pstmt = conn.prepareStatement(aptSql);
            pstmt.setInt(1, Integer.parseInt(patientId));
            rs = pstmt.executeQuery();
            
            json.append("\"appointments\":[");
            
            int count = 0;
            while (rs.next()) {
                if (count > 0) {
                    json.append(",");
                }
                
                String appointmentDate = rs.getString("appointment_date");
                String appointmentTime = rs.getString("appointment_time");
                String symptoms = rs.getString("symptoms");
                String status = rs.getString("status");
                String diagnosis = rs.getString("diagnosis");
                String notes = rs.getString("notes");
                String chiefComplaint = rs.getString("chief_complaint");
                
                json.append("{");
                json.append("\"appointmentDate\":\"").append(escapeJson(appointmentDate)).append("\",");
                json.append("\"appointmentTime\":\"").append(escapeJson(appointmentTime)).append("\",");
                json.append("\"symptoms\":\"").append(escapeJson(symptoms)).append("\",");
                json.append("\"status\":\"").append(escapeJson(status)).append("\",");
                json.append("\"diagnosis\":\"").append(escapeJson(diagnosis)).append("\",");
                json.append("\"notes\":\"").append(escapeJson(notes)).append("\",");
                json.append("\"chiefComplaint\":\"").append(escapeJson(chiefComplaint)).append("\"");
                json.append("}");
                
                count++;
            }
            
            json.append("]");
            json.append("}");
            
            System.out.println("✅ Found " + count + " appointments");
            System.out.println("📤 Sending JSON response");
            System.out.println("========================================");
            
            out.print(json.toString());
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            out.print("{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
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
    
    /**
     * Escape special characters for JSON
     */
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < ' ') {
                        sb.append("\\u");
                        String hex = Integer.toHexString(c);
                        for (int j = hex.length(); j < 4; j++) {
                            sb.append('0');
                        }
                        sb.append(hex);
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}