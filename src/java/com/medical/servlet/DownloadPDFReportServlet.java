package com.medical.servlet;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.IOException;
import java.io.OutputStream;
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

@WebServlet("/DownloadPDFReportServlet")
public class DownloadPDFReportServlet extends HttpServlet {
    
    private static final BaseColor HEADER_COLOR = new BaseColor(78, 115, 223);
    private static final BaseColor ALT_ROW_COLOR = new BaseColor(248, 249, 252);
    
    private Font createTitleFont() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, HEADER_COLOR);
    }
    
    private Font createDateFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
    }
    
    private Font createHeaderFont() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
    }
    
    private Font createCellFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String reportType = request.getParameter("type");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        
        System.out.println("========================================");
        System.out.println("📊 [PDF Report] Request received");
        System.out.println("   Report Type: " + reportType);
        
        try {
            response.setContentType("application/pdf");
            String fileName = reportType + "_Report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            
            Document document = new Document(PageSize.A4, 36, 36, 54, 36);
            OutputStream out = response.getOutputStream();
            PdfWriter.getInstance(document, out);
            
            document.open();
            
            switch (reportType.toLowerCase()) {
                case "system":
                    generateSystemReportPDF(document);
                    break;
                case "doctors":
                    generateDoctorsReportPDF(document, startDate, endDate);
                    break;
                case "patients":
                    generatePatientsReportPDF(document, startDate, endDate);
                    break;
                case "appointments":
                    generateAppointmentsReportPDF(document, startDate, endDate);
                    break;
                case "departments":
                    generateDepartmentsReportPDF(document);
                    break;
                default:
                    addTitle(document, "Unknown Report");
            }
            
            document.close();
            out.flush();
            out.close();
            
            System.out.println("✅ PDF Report generated: " + fileName);
            System.out.println("========================================");
            
        } catch (Exception e) {
            System.err.println("❌ Error generating PDF:");
            e.printStackTrace();
        }
    }
    
    private void generateSystemReportPDF(Document document) throws Exception {
        addTitle(document, "System Overview Report");
        addGeneratedDate(document);
        document.add(new Paragraph("\n"));
        
        Connection conn = DBConnection.getConnection();
        
        PdfPTable statsTable = new PdfPTable(2);
        statsTable.setWidthPercentage(100);
        statsTable.setSpacingBefore(10);
        statsTable.setSpacingAfter(20);
        
        addTableHeader(statsTable, new String[]{"Metric", "Count"});
        
        // Total Doctors
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) as count FROM users WHERE role = 'doctor'");
        ResultSet rs = ps.executeQuery();
        rs.next();
        addTableRow(statsTable, new String[]{"Total Doctors", String.valueOf(rs.getInt("count"))}, false);
        rs.close();
        ps.close();
        
        // Total Patients
        ps = conn.prepareStatement("SELECT COUNT(*) as count FROM users WHERE role = 'patient'");
        rs = ps.executeQuery();
        rs.next();
        addTableRow(statsTable, new String[]{"Total Patients", String.valueOf(rs.getInt("count"))}, true);
        rs.close();
        ps.close();
        
        // Total Appointments
        ps = conn.prepareStatement("SELECT COUNT(*) as count FROM appointments");
        rs = ps.executeQuery();
        rs.next();
        addTableRow(statsTable, new String[]{"Total Appointments", String.valueOf(rs.getInt("count"))}, false);
        rs.close();
        ps.close();
        
        // Total Departments
        ps = conn.prepareStatement("SELECT COUNT(*) as count FROM departments");
        rs = ps.executeQuery();
        rs.next();
        addTableRow(statsTable, new String[]{"Total Departments", String.valueOf(rs.getInt("count"))}, true);
        rs.close();
        ps.close();
        
        // Scheduled Appointments
        ps = conn.prepareStatement("SELECT COUNT(*) as count FROM appointments WHERE status = 'Scheduled'");
        rs = ps.executeQuery();
        rs.next();
        addTableRow(statsTable, new String[]{"Scheduled Appointments", String.valueOf(rs.getInt("count"))}, false);
        rs.close();
        ps.close();
        
        // Completed Appointments
        ps = conn.prepareStatement("SELECT COUNT(*) as count FROM appointments WHERE status = 'Completed'");
        rs = ps.executeQuery();
        rs.next();
        addTableRow(statsTable, new String[]{"Completed Appointments", String.valueOf(rs.getInt("count"))}, true);
        rs.close();
        ps.close();
        
        document.add(statsTable);
        conn.close();
    }
    
    private void generateDoctorsReportPDF(Document document, String startDate, String endDate) throws Exception {
        addTitle(document, "Doctors Report");
        addGeneratedDate(document);
        document.add(new Paragraph("\n"));
        
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT id, fullname, email, phone, specialty, status FROM users WHERE role = 'doctor' ORDER BY fullname";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{1, 2.5f, 2.5f, 2, 2, 1.5f});
        
        addTableHeader(table, new String[]{"ID", "Name", "Email", "Phone", "Specialty", "Status"});
        
        int count = 0;
        while (rs.next()) {
            addTableRow(table, new String[]{
                String.valueOf(rs.getInt("id")),
                rs.getString("fullname"),
                rs.getString("email"),
                rs.getString("phone") != null ? rs.getString("phone") : "N/A",
                rs.getString("specialty") != null ? rs.getString("specialty") : "General",
                rs.getString("status")
            }, count % 2 == 1);
            count++;
        }
        
        document.add(table);
        rs.close();
        ps.close();
        conn.close();
    }
    
    private void generatePatientsReportPDF(Document document, String startDate, String endDate) throws Exception {
        addTitle(document, "Patients Report");
        addGeneratedDate(document);
        document.add(new Paragraph("\n"));
        
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT id, fullname, email, phone, gender, status FROM users WHERE role = 'patient' ORDER BY fullname";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{1, 2.5f, 2.5f, 2, 1.5f, 1.5f});
        
        addTableHeader(table, new String[]{"ID", "Name", "Email", "Phone", "Gender", "Status"});
        
        int count = 0;
        while (rs.next()) {
            addTableRow(table, new String[]{
                String.valueOf(rs.getInt("id")),
                rs.getString("fullname"),
                rs.getString("email"),
                rs.getString("phone") != null ? rs.getString("phone") : "N/A",
                rs.getString("gender") != null ? rs.getString("gender") : "N/A",
                rs.getString("status")
            }, count % 2 == 1);
            count++;
        }
        
        document.add(table);
        rs.close();
        ps.close();
        conn.close();
    }
    
    private void generateAppointmentsReportPDF(Document document, String startDate, String endDate) throws Exception {
        addTitle(document, "Appointments Report");
        addGeneratedDate(document);
        document.add(new Paragraph("\n"));
        
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT a.id, a.patient_name, a.doctor_name, " +
                    "a.department, a.appointment_date, a.appointment_time, a.status " +
                    "FROM appointments a ";
        
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            sql += "WHERE a.appointment_date BETWEEN ? AND ? ";
        }
        
        sql += "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            ps.setString(1, startDate);
            ps.setString(2, endDate);
        }
        
        ResultSet rs = ps.executeQuery();
        
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{1, 2, 2, 2, 1.5f, 1.5f, 1.5f});
        
        addTableHeader(table, new String[]{"ID", "Patient", "Doctor", "Department", "Date", "Time", "Status"});
        
        int count = 0;
        while (rs.next()) {
            String department = rs.getString("department");
            System.out.println("🔍 [DEBUG] Appointment ID: " + rs.getInt("id") + ", Department from DB: '" + department + "'");
            
            // Check for both null and empty string
            if (department == null || department.trim().isEmpty()) {
                System.out.println("⚠️ [DEBUG] Department is null or empty, setting to N/A");
                department = "N/A";
            } else {
                System.out.println("✅ [DEBUG] Department has value: " + department);
            }
            
            addTableRow(table, new String[]{
                String.valueOf(rs.getInt("id")),
                rs.getString("patient_name") != null ? rs.getString("patient_name") : "N/A",
                rs.getString("doctor_name") != null ? rs.getString("doctor_name") : "N/A",
                department,
                rs.getString("appointment_date"),
                rs.getString("appointment_time"),
                rs.getString("status")
            }, count % 2 == 1);
            count++;
        }
        
        document.add(table);
        rs.close();
        ps.close();
        conn.close();
    }
    
    private void generateDepartmentsReportPDF(Document document) throws Exception {
        addTitle(document, "Departments Report");
        addGeneratedDate(document);
        document.add(new Paragraph("\n"));
        
        Connection conn = DBConnection.getConnection();
        
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{1, 2, 2.5f, 2, 1.5f, 1.5f});
        
        addTableHeader(table, new String[]{"ID", "Department", "Description", "Head", "Doctors", "Patients"});
        
        // Get departments only
        String deptSql = "SELECT id, name, description, head_id FROM departments ORDER BY name";
        
        PreparedStatement ps = conn.prepareStatement(deptSql);
        ResultSet rs = ps.executeQuery();
        
        int count = 0;
        while (rs.next()) {
            String deptName = rs.getString("name");
            Integer deptId = rs.getInt("id");
            Integer headId = null;
            
            // Check if head_id exists and is not null
            Object headIdObj = rs.getObject("head_id");
            if (headIdObj != null) {
                headId = rs.getInt("head_id");
            }
            
            // Get head name separately
            String headName = "Not Assigned";
            if (headId != null) {
                String headSql = "SELECT fullname FROM users WHERE id = ?";
                PreparedStatement headPs = conn.prepareStatement(headSql);
                headPs.setInt(1, headId);
                ResultSet headRs = headPs.executeQuery();
                if (headRs.next()) {
                    headName = headRs.getString("fullname");
                }
                headRs.close();
                headPs.close();
            }
            
            // Count doctors for this department by specialty
            String countSql = "SELECT COUNT(*) as count FROM users WHERE role = 'doctor' AND specialty = ?";
            PreparedStatement countPs = conn.prepareStatement(countSql);
            countPs.setString(1, deptName);
            ResultSet countRs = countPs.executeQuery();
            countRs.next();
            int doctorCount = countRs.getInt("count");
            countRs.close();
            countPs.close();
            
            // Count patients through appointments in this department
            String patientCountSql = "SELECT COUNT(DISTINCT a.patient_id) as count " +
                                    "FROM appointments a WHERE a.department = ?";
            PreparedStatement patientPs = conn.prepareStatement(patientCountSql);
            patientPs.setString(1, deptName);
            ResultSet patientRs = patientPs.executeQuery();
            patientRs.next();
            int patientCount = patientRs.getInt("count");
            patientRs.close();
            patientPs.close();
            
            addTableRow(table, new String[]{
                String.valueOf(rs.getInt("id")),
                deptName,
                rs.getString("description") != null ? rs.getString("description") : "N/A",
                headName,
                String.valueOf(doctorCount),
                String.valueOf(patientCount)
            }, count % 2 == 1);
            count++;
        }
        
        document.add(table);
        rs.close();
        ps.close();
        conn.close();
    }
    
    // Helper Methods
    private void addTitle(Document document, String title) throws DocumentException {
        Paragraph titlePara = new Paragraph(cleanString(title), createTitleFont());
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(10);
        document.add(titlePara);
    }
    
    private void addGeneratedDate(Document document) throws DocumentException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
        Paragraph datePara = new Paragraph("Generated: " + sdf.format(new Date()), createDateFont());
        datePara.setAlignment(Element.ALIGN_RIGHT);
        document.add(datePara);
    }
    
    private void addTableHeader(PdfPTable table, String[] headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(cleanString(header), createHeaderFont()));
            cell.setBackgroundColor(HEADER_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }
    
    private void addTableRow(PdfPTable table, String[] data, boolean alternate) {
        for (String value : data) {
            PdfPCell cell = new PdfPCell(new Phrase(cleanString(value != null ? value : ""), createCellFont()));
            if (alternate) {
                cell.setBackgroundColor(ALT_ROW_COLOR);
            }
            cell.setPadding(6);
            table.addCell(cell);
        }
    }
    
    private String cleanString(String input) {
        if (input == null || input.isEmpty()) return "";
        return input.replaceAll("[^\\x20-\\x7E]", "")
                   .replaceAll("\u2018", "'")
                   .replaceAll("\u2019", "'")
                   .replaceAll("\u201C", "\"")
                   .replaceAll("\u201D", "\"")
                   .trim();
    }
}