package com.medical.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DownloadPrescriptionServlet")
public class DownloadPrescriptionServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String prescriptionId = request.getParameter("id");
        
        System.out.println("========================================");
        System.out.println("📥 [DownloadPrescription] Request received");
        System.out.println("   Prescription ID: " + prescriptionId);
        
        if (prescriptionId == null || prescriptionId.trim().isEmpty()) {
            System.out.println("❌ Missing prescription ID");
            response.sendRedirect("PatientServlet?error=missingId");
            return;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            int rxId = Integer.parseInt(prescriptionId);
            
            conn = DBConnection.getConnection();
            
            // Fetch prescription details
            String query = "SELECT p.*, u.fullname as patient_fullname, u.phone as patient_phone " +
                          "FROM prescriptions p " +
                          "JOIN users u ON p.patient_id = u.id " +
                          "WHERE p.id = ?";
            
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, rxId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("✅ Prescription found");
                
                // Get prescription details
                String prescriptionNumber = rs.getString("prescription_number");
                String fileName = "Prescription_" + prescriptionNumber.replace("/", "-") + ".html";
                
                // Generate HTML content for PDF
                String html = generatePrescriptionHTML(rs);
                byte[] htmlBytes = html.getBytes("UTF-8");
                
                // Set response headers to force download
                response.reset();
                response.setContentType("application/octet-stream");
                response.setContentLength(htmlBytes.length);
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "0");
                
                // Write HTML to response
                OutputStream out = response.getOutputStream();
                out.write(htmlBytes);
                out.flush();
                out.close();
                
                System.out.println("✅ Prescription downloaded to Downloads folder: " + fileName);
                System.out.println("========================================");
                
            } else {
                System.out.println("❌ Prescription not found");
                response.sendRedirect("PatientServlet?error=notFound");
            }
            
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid prescription ID format");
            response.sendRedirect("PatientServlet?error=invalidId");
        } catch (Exception e) {
            System.err.println("❌ Error downloading prescription:");
            e.printStackTrace();
            response.sendRedirect("PatientServlet?error=downloadFailed");
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
    
    private String generatePrescriptionHTML(ResultSet rs) throws Exception {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<title>Medical Prescription</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; padding: 40px; background: white; }\n");
        html.append(".header { text-align: center; border-bottom: 3px solid #3498db; padding-bottom: 20px; margin-bottom: 30px; }\n");
        html.append(".header h1 { color: #2c3e50; margin: 0; font-size: 28px; }\n");
        html.append(".header p { color: #7f8c8d; margin: 5px 0; }\n");
        html.append(".section { margin: 20px 0; }\n");
        html.append(".section-title { font-size: 18px; font-weight: bold; color: #2c3e50; margin-bottom: 10px; border-bottom: 2px solid #ecf0f1; padding-bottom: 5px; }\n");
        html.append(".info-row { display: flex; margin: 8px 0; }\n");
        html.append(".info-label { font-weight: bold; min-width: 150px; color: #555; }\n");
        html.append(".info-value { color: #2c3e50; }\n");
        html.append(".medicine-box { background: #f8f9fa; border-left: 4px solid #3498db; padding: 15px; margin: 10px 0; border-radius: 4px; }\n");
        html.append(".medicine-name { font-size: 18px; font-weight: bold; color: #2c3e50; margin-bottom: 10px; }\n");
        html.append(".instructions { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 15px 0; border-radius: 4px; }\n");
        html.append(".footer { margin-top: 50px; padding-top: 20px; border-top: 2px solid #ecf0f1; text-align: center; color: #7f8c8d; font-size: 12px; }\n");
        html.append(".signature { margin-top: 40px; text-align: right; }\n");
        html.append(".signature-line { border-top: 2px solid #000; width: 200px; margin: 10px 0 5px auto; }\n");
        html.append("@media print { body { padding: 20px; } }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");
        
        // Header
        html.append("<div class='header'>\n");
        html.append("<h1>🏥 MEDICAL PRESCRIPTION</h1>\n");
        html.append("<p>Medical Record System</p>\n");
        html.append("<p>Prescription No: ").append(rs.getString("prescription_number")).append("</p>\n");
        html.append("</div>\n");
        
        // Doctor Information
        html.append("<div class='section'>\n");
        html.append("<div class='section-title'>Doctor Information</div>\n");
        html.append("<div class='info-row'><div class='info-label'>Doctor Name:</div><div class='info-value'>")
            .append(rs.getString("doctor_name")).append("</div></div>\n");
        html.append("</div>\n");
        
        // Patient Information
        html.append("<div class='section'>\n");
        html.append("<div class='section-title'>Patient Information</div>\n");
        html.append("<div class='info-row'><div class='info-label'>Patient Name:</div><div class='info-value'>")
            .append(rs.getString("patient_name")).append("</div></div>\n");
        html.append("<div class='info-row'><div class='info-label'>Phone:</div><div class='info-value'>")
            .append(rs.getString("patient_phone")).append("</div></div>\n");
        html.append("<div class='info-row'><div class='info-label'>Date:</div><div class='info-value'>")
            .append(rs.getString("prescribed_date")).append("</div></div>\n");
        html.append("</div>\n");
        
        // Diagnosis
        String diagnosis = rs.getString("diagnosis");
        if (diagnosis != null && !diagnosis.isEmpty()) {
            html.append("<div class='section'>\n");
            html.append("<div class='section-title'>Diagnosis</div>\n");
            html.append("<p>").append(diagnosis).append("</p>\n");
            html.append("</div>\n");
        }
        
        // Medicine Details
        html.append("<div class='section'>\n");
        html.append("<div class='section-title'>Prescribed Medicine</div>\n");
        html.append("<div class='medicine-box'>\n");
        html.append("<div class='medicine-name'>📋 ").append(rs.getString("medicine_name")).append("</div>\n");
        
        String dosage = rs.getString("dosage");
        if (dosage != null && !dosage.isEmpty()) {
            html.append("<div class='info-row'><div class='info-label'>Dosage:</div><div class='info-value'>")
                .append(dosage).append("</div></div>\n");
        }
        
        String frequency = rs.getString("frequency");
        if (frequency != null && !frequency.isEmpty()) {
            html.append("<div class='info-row'><div class='info-label'>Frequency:</div><div class='info-value'>")
                .append(frequency).append("</div></div>\n");
        }
        
        String duration = rs.getString("duration");
        if (duration != null && !duration.isEmpty()) {
            html.append("<div class='info-row'><div class='info-label'>Duration:</div><div class='info-value'>")
                .append(duration).append("</div></div>\n");
        }
        
        html.append("</div>\n");
        html.append("</div>\n");
        
        // Instructions
        String instructions = rs.getString("instructions");
        if (instructions != null && !instructions.isEmpty()) {
            html.append("<div class='instructions'>\n");
            html.append("<div style='font-weight: bold; margin-bottom: 10px;'>⚠️ Instructions:</div>\n");
            html.append("<p>").append(instructions).append("</p>\n");
            html.append("</div>\n");
        }
        
        // Follow-up
        String followupDate = rs.getString("followup_date");
        if (followupDate != null && !followupDate.isEmpty()) {
            html.append("<div class='section'>\n");
            html.append("<div class='info-row'><div class='info-label'>Follow-up Date:</div><div class='info-value'>")
                .append(followupDate).append("</div></div>\n");
            html.append("</div>\n");
        }
        
        // Signature
        html.append("<div class='signature'>\n");
        html.append("<div class='signature-line'></div>\n");
        html.append("<p style='margin: 5px 0;'><strong>").append(rs.getString("doctor_name")).append("</strong></p>\n");
        html.append("<p style='font-size: 12px; color: #7f8c8d;'>Medical Practitioner</p>\n");
        html.append("</div>\n");
        
        // Footer
        html.append("<div class='footer'>\n");
        html.append("<p>This is a computer-generated prescription.</p>\n");
        html.append("<p>For queries, please contact your healthcare provider.</p>\n");
        html.append("<p style='margin-top: 10px;'>© 2026 Medical Record System. All rights reserved.</p>\n");
        html.append("</div>\n");
        
        html.append("</body>\n</html>");
        
        return html.toString();
    }
}