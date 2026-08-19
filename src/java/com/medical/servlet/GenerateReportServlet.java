package com.medical.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GenerateReportServlet")
public class GenerateReportServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String reportType = request.getParameter("type");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String custom = request.getParameter("custom");
        
        System.out.println("========================================");
        System.out.println("📊 [GenerateReport] Request received");
        System.out.println("   Report Type: " + reportType);
        System.out.println("   Start Date: " + startDate);
        System.out.println("   End Date: " + endDate);
        System.out.println("   Custom: " + custom);
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String reportHTML = generateReportHTML(reportType, startDate, endDate, custom);
            out.println(reportHTML);
            System.out.println("✅ Report generated successfully");
        } catch (Exception e) {
            System.err.println("❌ Error generating report:");
            e.printStackTrace();
            out.println(generateErrorPage(e.getMessage()));
        } finally {
            out.close();
            System.out.println("========================================");
        }
    }
    
    private String generateReportHTML(String type, String startDate, String endDate, String custom) 
            throws Exception {
        
        StringBuilder html = new StringBuilder();
        
        // HTML Header
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<title>").append(capitalizeFirst(type)).append(" Report</title>\n");
        html.append(getReportStyles());
        html.append("</head>\n<body>\n");
        
        // Report Header
        html.append("<div class='report-header'>\n");
        html.append("<h1>").append(capitalizeFirst(type)).append(" Report</h1>\n");
        html.append("<div class='report-date'>Generated: ").append(getCurrentDateTime()).append("</div>\n");
        html.append("</div>\n");
        
        // Report Content based on type
        switch (type.toLowerCase()) {
            case "system":
                html.append(generateSystemReport());
                break;
            case "doctors":
                html.append(generateDoctorsReport(startDate, endDate));
                break;
            case "patients":
                html.append(generatePatientsReport(startDate, endDate));
                break;
            case "appointments":
                html.append(generateAppointmentsReport(startDate, endDate));
                break;
            case "departments":
                html.append(generateDepartmentsReport());
                break;
            default:
                html.append("<p>Unknown report type</p>");
        }
        
        // Action Buttons
        html.append("<div class='action-btns'>\n");
        html.append("<button class='btn' onclick='window.print()'>Print Report</button>\n");
        html.append("<button class='btn btn-secondary' onclick='window.close()'>Close</button>\n");
        html.append("</div>\n");
        
        html.append("</body>\n</html>");
        
        return html.toString();
    }
    
    private String generateSystemReport() throws Exception {
        StringBuilder report = new StringBuilder();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            report.append("<h2>System Summary</h2>\n");
            report.append("<table>\n");
            report.append("<tr><th>Metric</th><th>Count</th></tr>\n");
            
            // Total Doctors
            pstmt = conn.prepareStatement("SELECT COUNT(*) as count FROM users WHERE role = 'doctor'");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                report.append("<tr><td>Total Doctors</td><td>").append(rs.getInt("count")).append("</td></tr>\n");
            }
            rs.close();
            pstmt.close();
            
            // Total Patients
            pstmt = conn.prepareStatement("SELECT COUNT(*) as count FROM users WHERE role = 'patient'");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                report.append("<tr><td>Total Patients</td><td>").append(rs.getInt("count")).append("</td></tr>\n");
            }
            rs.close();
            pstmt.close();
            
            // Total Appointments
            pstmt = conn.prepareStatement("SELECT COUNT(*) as count FROM appointments");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                report.append("<tr><td>Total Appointments</td><td>").append(rs.getInt("count")).append("</td></tr>\n");
            }
            rs.close();
            pstmt.close();
            
            // Total Departments
            pstmt = conn.prepareStatement("SELECT COUNT(*) as count FROM departments");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                report.append("<tr><td>Total Departments</td><td>").append(rs.getInt("count")).append("</td></tr>\n");
            }
            rs.close();
            pstmt.close();
            
            // Scheduled Appointments
            pstmt = conn.prepareStatement("SELECT COUNT(*) as count FROM appointments WHERE status = 'Scheduled'");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                report.append("<tr><td>Scheduled Appointments</td><td>").append(rs.getInt("count")).append("</td></tr>\n");
            }
            rs.close();
            pstmt.close();
            
            // Completed Appointments
            pstmt = conn.prepareStatement("SELECT COUNT(*) as count FROM appointments WHERE status = 'Completed'");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                report.append("<tr><td>Completed Appointments</td><td>").append(rs.getInt("count")).append("</td></tr>\n");
            }
            
            report.append("</table>\n");
            
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
        
        return report.toString();
    }
    
    private String generateDoctorsReport(String startDate, String endDate) throws Exception {
        StringBuilder report = new StringBuilder();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            report.append("<h2>Doctors Report</h2>\n");
            report.append("<table>\n");
            report.append("<tr><th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Specialty</th><th>Status</th></tr>\n");
            
            String query = "SELECT id, fullname, email, phone, specialty, status FROM users WHERE role = 'doctor' ORDER BY fullname";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                report.append("<tr>");
                report.append("<td>").append(rs.getInt("id")).append("</td>");
                report.append("<td>").append(rs.getString("fullname")).append("</td>");
                report.append("<td>").append(rs.getString("email")).append("</td>");
                report.append("<td>").append(rs.getString("phone") != null ? rs.getString("phone") : "N/A").append("</td>");
                report.append("<td>").append(rs.getString("specialty") != null ? rs.getString("specialty") : "General").append("</td>");
                report.append("<td>").append(rs.getString("status")).append("</td>");
                report.append("</tr>\n");
            }
            
            report.append("</table>\n");
            
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
        
        return report.toString();
    }
    
    private String generatePatientsReport(String startDate, String endDate) throws Exception {
        StringBuilder report = new StringBuilder();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            report.append("<h2>Patients Report</h2>\n");
            report.append("<table>\n");
            report.append("<tr><th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Gender</th><th>Status</th></tr>\n");
            
            String query = "SELECT id, fullname, email, phone, gender, status FROM users WHERE role = 'patient' ORDER BY fullname";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                report.append("<tr>");
                report.append("<td>").append(rs.getInt("id")).append("</td>");
                report.append("<td>").append(rs.getString("fullname")).append("</td>");
                report.append("<td>").append(rs.getString("email")).append("</td>");
                report.append("<td>").append(rs.getString("phone") != null ? rs.getString("phone") : "N/A").append("</td>");
                report.append("<td>").append(rs.getString("gender") != null ? rs.getString("gender") : "N/A").append("</td>");
                report.append("<td>").append(rs.getString("status")).append("</td>");
                report.append("</tr>\n");
            }
            
            report.append("</table>\n");
            
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
        
        return report.toString();
    }
    
    private String generateAppointmentsReport(String startDate, String endDate) throws Exception {
        StringBuilder report = new StringBuilder();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            report.append("<h2>Appointments Report</h2>\n");
            report.append("<table>\n");
            report.append("<tr><th>ID</th><th>Patient</th><th>Doctor</th><th>Department</th><th>Date</th><th>Time</th><th>Status</th></tr>\n");
            
            String query = "SELECT a.id, p.fullname as patient_name, d.fullname as doctor_name, " +
                          "dept.name as department_name, a.appointment_date, a.appointment_time, a.status " +
                          "FROM appointments a " +
                          "JOIN users p ON a.patient_id = p.id " +
                          "JOIN users d ON a.doctor_id = d.id " +
                          "LEFT JOIN departments dept ON a.department_id = dept.id ";
            
            if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
                query += "WHERE a.appointment_date BETWEEN ? AND ? ";
            }
            
            query += "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
            
            pstmt = conn.prepareStatement(query);
            
            if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
                pstmt.setString(1, startDate);
                pstmt.setString(2, endDate);
            }
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                report.append("<tr>");
                report.append("<td>").append(rs.getInt("id")).append("</td>");
                report.append("<td>").append(rs.getString("patient_name")).append("</td>");
                report.append("<td>").append(rs.getString("doctor_name")).append("</td>");
                report.append("<td>").append(rs.getString("department_name") != null ? rs.getString("department_name") : "N/A").append("</td>");
                report.append("<td>").append(rs.getString("appointment_date")).append("</td>");
                report.append("<td>").append(rs.getString("appointment_time")).append("</td>");
                report.append("<td>").append(rs.getString("status")).append("</td>");
                report.append("</tr>\n");
            }
            
            report.append("</table>\n");
            
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
        
        return report.toString();
    }
    
    private String generateDepartmentsReport() throws Exception {
        StringBuilder report = new StringBuilder();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            report.append("<h2>Departments Report</h2>\n");
            report.append("<table>\n");
            report.append("<tr><th>ID</th><th>Department</th><th>Description</th><th>Head</th><th>Doctors</th></tr>\n");
            
            String query = "SELECT d.id, d.name, d.description, u.fullname as head_name, " +
                          "(SELECT COUNT(*) FROM users WHERE role = 'doctor' AND specialty = d.name) as doctor_count " +
                          "FROM departments d " +
                          "LEFT JOIN users u ON d.head_id = u.id " +
                          "ORDER BY d.name";
            
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                report.append("<tr>");
                report.append("<td>").append(rs.getInt("id")).append("</td>");
                report.append("<td>").append(rs.getString("name")).append("</td>");
                report.append("<td>").append(rs.getString("description") != null ? rs.getString("description") : "N/A").append("</td>");
                report.append("<td>").append(rs.getString("head_name") != null ? rs.getString("head_name") : "Not Assigned").append("</td>");
                report.append("<td>").append(rs.getInt("doctor_count")).append("</td>");
                report.append("</tr>\n");
            }
            
            report.append("</table>\n");
            
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
        
        return report.toString();
    }
    
    private String getReportStyles() {
        return "<style>\n" +
               "body { font-family: Arial, sans-serif; padding: 40px; max-width: 1200px; margin: 0 auto; background: #f8f9fc; }\n" +
               ".report-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; padding-bottom: 20px; border-bottom: 3px solid #4e73df; }\n" +
               "h1 { color: #4e73df; margin: 0; font-size: 2rem; }\n" +
               ".report-date { color: #858796; font-size: 14px; }\n" +
               "h2 { color: #5a5c69; margin-top: 30px; font-size: 1.5rem; }\n" +
               "table { width: 100%; border-collapse: collapse; margin: 20px 0; box-shadow: 0 2px 8px rgba(0,0,0,0.1); background: white; }\n" +
               "th, td { padding: 12px 16px; text-align: left; border: 1px solid #ddd; }\n" +
               "th { background: #4e73df; color: white; font-weight: 600; text-transform: uppercase; font-size: 0.85rem; letter-spacing: 0.5px; }\n" +
               "tr:nth-child(even) { background: #f8f9fc; }\n" +
               "tr:hover { background: #e8eaf6; }\n" +
               ".action-btns { margin: 30px 0; display: flex; gap: 10px; }\n" +
               ".btn { background: #4e73df; color: white; padding: 12px 24px; border: none; cursor: pointer; border-radius: 6px; font-size: 14px; font-weight: 600; transition: all 0.2s; }\n" +
               ".btn:hover { background: #224abe; transform: translateY(-2px); }\n" +
               ".btn-secondary { background: #858796; }\n" +
               ".btn-secondary:hover { background: #6c6e7e; }\n" +
               "@media print { .action-btns { display: none; } body { background: white; } }\n" +
               "</style>\n";
    }
    
    private String generateErrorPage(String error) {
        return "<!DOCTYPE html><html><head><title>Error</title></head><body>" +
               "<h1 style='color: red;'>Error Generating Report</h1>" +
               "<p>" + error + "</p>" +
               "<button onclick='window.close()'>Close</button>" +
               "</body></html>";
    }
    
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
        return sdf.format(new Date());
    }
    
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}