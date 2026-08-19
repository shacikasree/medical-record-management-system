package com.medical.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/SavePrescription")
public class SavePrescriptionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("========================================");
        System.out.println("💊 [SavePrescription] POST Request");
        
        // Session check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            System.out.println("❌ No session found");
            response.sendRedirect("Login.jsp?error=loginRequired");
            return;
        }

        // Check if user is a doctor
        String role = (String) session.getAttribute("role");
        if (!"doctor".equals(role)) {
            System.out.println("❌ User is not a doctor");
            response.sendRedirect("Login.jsp?error=unauthorized");
            return;
        }

        // Get form parameters
        String doctorIdStr = request.getParameter("doctorId");
        String patientIdStr = request.getParameter("patientId");
        String appointmentIdStr = request.getParameter("appointmentId");
        String prescriptionDate = request.getParameter("prescriptionDate");
        String diagnosis = request.getParameter("diagnosis");
        String instructions = request.getParameter("instructions");
        String followupDateStr = request.getParameter("followupDate");
        
        // Get multiple medicines (if you implement dynamic medicine fields)
        String[] medicineNames = request.getParameterValues("medicineName[]");
        String[] dosages = request.getParameterValues("dosage[]");
        String[] frequencies = request.getParameterValues("frequency[]");
        String[] durations = request.getParameterValues("duration[]");
        
        System.out.println("📋 Form Parameters:");
        System.out.println("   Doctor ID (from form): " + doctorIdStr);
        System.out.println("   Patient ID: " + patientIdStr);
        System.out.println("   Appointment ID: " + appointmentIdStr);
        System.out.println("   Date: " + prescriptionDate);
        System.out.println("   Diagnosis: " + diagnosis);
        System.out.println("   Instructions: " + instructions);
        System.out.println("   Follow-up: " + followupDateStr);

        // FIX: Get doctorId from session if not in form or if null/empty
        int doctorId;
        try {
            if (doctorIdStr == null || doctorIdStr.trim().isEmpty() || "null".equals(doctorIdStr)) {
                System.out.println("⚠️ Doctor ID not in form, getting from session...");
                
                // Try doctorId attribute first
                Object doctorIdObj = session.getAttribute("doctorId");
                if (doctorIdObj == null) {
                    // Fall back to userId
                    doctorIdObj = session.getAttribute("userId");
                    System.out.println("   Using userId from session");
                }
                
                if (doctorIdObj == null) {
                    System.out.println("❌ No doctor ID found in session");
                    response.sendRedirect("DoctorServlet?error=noDoctorId");
                    return;
                }
                
                doctorId = Integer.parseInt(String.valueOf(doctorIdObj));
                System.out.println("✅ Doctor ID from session: " + doctorId);
            } else {
                doctorId = Integer.parseInt(doctorIdStr);
                System.out.println("✅ Doctor ID from form: " + doctorId);
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid doctor ID format: " + doctorIdStr);
            e.printStackTrace();
            response.sendRedirect("DoctorServlet?error=invalidDoctorId");
            return;
        }

        // Validate required fields
        if (patientIdStr == null || patientIdStr.trim().isEmpty()) {
            System.out.println("❌ Missing patient ID");
            response.sendRedirect("DoctorServlet?error=missingPatient");
            return;
        }

        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            System.out.println("❌ Missing diagnosis");
            response.sendRedirect("DoctorServlet?error=missingDiagnosis");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            int patientId = Integer.parseInt(patientIdStr);
            Integer appointmentId = null;
            if (appointmentIdStr != null && !appointmentIdStr.trim().isEmpty() && !"null".equals(appointmentIdStr)) {
                try {
                    appointmentId = Integer.parseInt(appointmentIdStr);
                    System.out.println("✅ Appointment ID parsed: " + appointmentId);
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ Could not parse appointment ID: " + appointmentIdStr);
                }
            }
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            System.out.println("✅ Database connection established");

            // Get doctor and patient names
            String doctorName = (String) session.getAttribute("fullname");
            String patientName = getPatientName(conn, patientId);
            
            System.out.println("👨‍⚕️ Doctor: " + doctorName + " (ID: " + doctorId + ")");
            System.out.println("👤 Patient: " + patientName + " (ID: " + patientId + ")");

            // Handle multiple medicines or single medicine
            if (medicineNames != null && medicineNames.length > 0) {
                // Multiple medicines - loop through each
                System.out.println("💊 Saving " + medicineNames.length + " medicines...");
                
                for (int i = 0; i < medicineNames.length; i++) {
                    if (medicineNames[i] != null && !medicineNames[i].trim().isEmpty()) {
                        savePrescription(conn, appointmentId, patientId, patientName, doctorId, doctorName,
                                       medicineNames[i], 
                                       dosages != null && i < dosages.length ? dosages[i] : "",
                                       frequencies != null && i < frequencies.length ? frequencies[i] : "",
                                       durations != null && i < durations.length ? durations[i] : "",
                                       diagnosis, instructions, prescriptionDate, followupDateStr);
                        
                        System.out.println("   ✅ Medicine #" + (i+1) + " saved: " + medicineNames[i]);
                    }
                }
            } else {
                // Single medicine - check for single field values
                String medicineName = request.getParameter("medicineName");
                String dosage = request.getParameter("dosage");
                String frequency = request.getParameter("frequency");
                String duration = request.getParameter("duration");
                
                if (medicineName != null && !medicineName.trim().isEmpty()) {
                    savePrescription(conn, appointmentId, patientId, patientName, doctorId, doctorName,
                                   medicineName, dosage, frequency, duration,
                                   diagnosis, instructions, prescriptionDate, followupDateStr);
                    
                    System.out.println("   ✅ Single medicine saved: " + medicineName);
                } else {
                    // No medicine specified - save prescription without medicine
                    savePrescription(conn, appointmentId, patientId, patientName, doctorId, doctorName,
                                   "Not specified", "", "", "",
                                   diagnosis, instructions, prescriptionDate, followupDateStr);
                    
                    System.out.println("   ⚠️ Prescription saved without medicine details");
                }
            }

            conn.commit(); // Commit transaction
            System.out.println("✅✅✅ Prescription saved successfully!");
            System.out.println("========================================");
            
            response.sendRedirect("DoctorServlet?success=prescriptionSaved");

        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid ID format");
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) {}
            }
            response.sendRedirect("DoctorServlet?error=invalidId");
        } catch (Exception e) {
            System.err.println("❌❌❌ Error saving prescription:");
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) {}
            }
            System.out.println("========================================");
            response.sendRedirect("DoctorServlet?error=saveFailed");
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
                System.out.println("🔒 Database connection closed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getPatientName(Connection conn, int patientId) throws Exception {
        String query = "SELECT fullname FROM users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("fullname");
            }
            return "Unknown Patient";
        }
    }

    private void savePrescription(Connection conn, Integer appointmentId, 
                                 int patientId, String patientName,
                                 int doctorId, String doctorName, String medicineName,
                                 String dosage, String frequency, String duration,
                                 String diagnosis, String instructions, 
                                 String prescriptionDate, String followupDate) throws Exception {
        
        // Generate prescription number
        String prescriptionNumber = "RX-" + System.currentTimeMillis();
        
        // Calculate end date based on duration
        LocalDate startDate = prescriptionDate != null && !prescriptionDate.isEmpty() 
                            ? LocalDate.parse(prescriptionDate) 
                            : LocalDate.now();
        
        LocalDate endDate = calculateEndDate(startDate, duration);
        
        // Determine status
        String status = "active";
        if (endDate != null && endDate.isBefore(LocalDate.now())) {
            status = "expired";
        }

        String sql = "INSERT INTO prescriptions " +
                    "(appointment_id, prescription_number, patient_id, patient_name, doctor_id, doctor_name, " +
                    "medicine_name, dosage, frequency, duration, diagnosis, instructions, " +
                    "prescribed_date, start_date, end_date, followup_date, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, appointmentId); // Use setObject to handle NULL
            pstmt.setString(2, prescriptionNumber);
            pstmt.setInt(3, patientId);
            pstmt.setString(4, patientName);
            pstmt.setInt(5, doctorId);
            pstmt.setString(6, doctorName);
            pstmt.setString(7, medicineName);
            pstmt.setString(8, dosage != null ? dosage : "");
            pstmt.setString(9, frequency != null ? frequency : "");
            pstmt.setString(10, duration != null ? duration : "");
            pstmt.setString(11, diagnosis);
            pstmt.setString(12, instructions != null ? instructions : "");
            pstmt.setString(13, prescriptionDate != null ? prescriptionDate : startDate.toString());
            pstmt.setString(14, startDate.toString());
            pstmt.setString(15, endDate != null ? endDate.toString() : null);
            pstmt.setString(16, followupDate != null && !followupDate.isEmpty() ? followupDate : null);
            pstmt.setString(17, status);
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("      ✅ Prescription record inserted" + 
                                 (appointmentId != null ? " (linked to appointment #" + appointmentId + ")" : ""));
            } else {
                System.out.println("      ❌ Failed to insert prescription");
            }
        }
    }

    private LocalDate calculateEndDate(LocalDate startDate, String duration) {
        if (duration == null || duration.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Parse duration like "7 days", "2 weeks", "1 month"
            String[] parts = duration.trim().toLowerCase().split(" ");
            if (parts.length >= 2) {
                int value = Integer.parseInt(parts[0]);
                String unit = parts[1];
                
                if (unit.startsWith("day")) {
                    return startDate.plusDays(value);
                } else if (unit.startsWith("week")) {
                    return startDate.plusWeeks(value);
                } else if (unit.startsWith("month")) {
                    return startDate.plusMonths(value);
                } else if (unit.startsWith("year")) {
                    return startDate.plusYears(value);
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not parse duration: " + duration);
        }
        
        return null;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}