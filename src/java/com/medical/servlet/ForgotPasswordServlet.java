package com.medical.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;
import java.nio.file.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

@WebServlet("/ForgotPasswordServlet")
public class ForgotPasswordServlet extends HttpServlet {
    
    // ========== DATABASE CONFIGURATION ==========
    private static final String DB_URL = "jdbc:mysql://localhost:3306/medical_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "shacika@@2006";
    
    // ========== EMAIL CONFIGURATION ==========
    private static final String EMAIL_HOST = "smtp.gmail.com";
    private static final String EMAIL_PORT = "587";
    private static final String EMAIL_USERNAME = ""; // Your Gmail here
    private static final String EMAIL_PASSWORD = ""; // Your App Password here
    private static final String EMAIL_FROM = "noreply@medlifehospital.com";
    
    // OTP validity in milliseconds (10 minutes)
    private static final long OTP_VALIDITY = 10 * 60 * 1000;
    
    // TEST MODE - Set to true to skip email sending
    private static final boolean TEST_MODE = true;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        out.write("{\"success\": true, \"message\": \"ForgotPasswordServlet is working!\", \"testMode\": " + TEST_MODE + "}");
        out.flush();
        out.close();
        
        System.out.println("✅ Test GET request received");
    }
   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = null;
        
        try {
            out = response.getWriter();
            
            System.out.println("========== REQUEST DEBUG ==========");
            Enumeration<String> paramNames = request.getParameterNames();
            while(paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String paramValue = request.getParameter(paramName);
                System.out.println("Parameter: " + paramName + " = " + paramValue);
            }
            System.out.println("==================================");
            
            String action = request.getParameter("action");
            System.out.println("Action parameter: " + action);
            
            if (action == null || action.trim().isEmpty()) {
                System.out.println("❌ Action is null or empty");
                out.write("{\"success\": false, \"message\": \"Action parameter is missing\"}");
                out.flush();
                return;
            }
            
            if ("sendOTP".equals(action)) {
                handleSendOTP(request, response, out);
            } else if ("verifyOTP".equals(action)) {
                handleVerifyOTP(request, response, out);
            } else if ("resetPassword".equals(action)) {
                handleResetPassword(request, response, out);
            } else {
                System.out.println("❌ Invalid action: " + action);
                out.write("{\"success\": false, \"message\": \"Invalid action: " + action + "\"}");
            }
            
            out.flush();
            
        } catch (Exception e) {
            System.out.println("❌ Exception in doPost: " + e.getMessage());
            e.printStackTrace();
            
            if (out != null) {
                try {
                    out.write("{\"success\": false, \"message\": \"Server error: " + e.getMessage().replace("\"", "'") + "\"}");
                    out.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    private void handleSendOTP(HttpServletRequest request, HttpServletResponse response, 
                               PrintWriter out) throws Exception {
        
        String email = request.getParameter("email");
        
        System.out.println("📧 Send OTP request for: " + email);
        
        try {
            if (email == null || email.trim().isEmpty()) {
                System.out.println("❌ Email parameter is null or empty");
                out.write("{\"success\": false, \"message\": \"Email is required\"}");
                return;
            }
            
            System.out.println("🔍 Checking if email exists in database...");
            boolean emailExists = isEmailRegistered(email);
            System.out.println("Email exists in DB: " + emailExists);
            
            if (!emailExists) {
                System.out.println("❌ Email not found: " + email);
                out.write("{\"success\": false, \"message\": \"Email not found in our system\"}");
                return;
            }
            
            String otp = generateOTP();
            System.out.println("🔑 Generated OTP: " + otp);
            
            HttpSession session = request.getSession();
            session.setAttribute("resetOTP", otp);
            session.setAttribute("resetEmail", email);
            session.setAttribute("otpTimestamp", System.currentTimeMillis());
            
            System.out.println("💾 OTP stored in session");
            
            if (TEST_MODE) {
                System.out.println("⚠️ ===== TEST MODE ENABLED =====");
                System.out.println("🔑 YOUR OTP IS: " + otp);
                System.out.println("📧 Would send to: " + email);
                System.out.println("================================");
                out.write("{\"success\": true, \"message\": \"OTP generated! Check console for OTP: " + otp + "\"}");
                return;
            }
            
            boolean emailSent = sendPasswordResetEmail(email, otp);
            
            if (emailSent) {
                System.out.println("✅ Email sent successfully to: " + email);
                out.write("{\"success\": true, \"message\": \"OTP sent to your email\"}");
            } else {
                System.out.println("❌ Failed to send email");
                System.out.println("🔑 OTP was: " + otp + " (use this to continue)");
                out.write("{\"success\": false, \"message\": \"Failed to send email. OTP in console: " + otp + "\"}");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Exception in handleSendOTP: " + e.getMessage());
            e.printStackTrace();
            out.write("{\"success\": false, \"message\": \"Error: " + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
    
    private void handleVerifyOTP(HttpServletRequest request, HttpServletResponse response, 
                                 PrintWriter out) throws Exception {
        
        HttpSession session = request.getSession();
        
        String sessionOTP = (String) session.getAttribute("resetOTP");
        Long otpTimestamp = (Long) session.getAttribute("otpTimestamp");
        
        System.out.println("🔍 Verifying OTP...");
        System.out.println("Session OTP: " + sessionOTP);
        
        if (sessionOTP == null || otpTimestamp == null) {
            System.out.println("❌ OTP not found in session");
            out.write("{\"success\": false, \"message\": \"OTP expired or not found. Please request new OTP.\"}");
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - otpTimestamp > OTP_VALIDITY) {
            System.out.println("❌ OTP expired");
            session.removeAttribute("resetOTP");
            session.removeAttribute("otpTimestamp");
            out.write("{\"success\": false, \"message\": \"OTP expired. Please request new OTP.\"}");
            return;
        }
        
        String otp1 = request.getParameter("otp1");
        String otp2 = request.getParameter("otp2");
        String otp3 = request.getParameter("otp3");
        String otp4 = request.getParameter("otp4");
        String otp5 = request.getParameter("otp5");
        String otp6 = request.getParameter("otp6");
        
        String enteredOTP = (otp1 != null ? otp1 : "") + 
                           (otp2 != null ? otp2 : "") + 
                           (otp3 != null ? otp3 : "") + 
                           (otp4 != null ? otp4 : "") + 
                           (otp5 != null ? otp5 : "") + 
                           (otp6 != null ? otp6 : "");
        
        System.out.println("Entered OTP: " + enteredOTP);
        
        if (sessionOTP.equals(enteredOTP)) {
            System.out.println("✅ OTP verified successfully");
            session.setAttribute("otpVerified", true);
            out.write("{\"success\": true, \"message\": \"OTP verified successfully\"}");
        } else {
            System.out.println("❌ Invalid OTP");
            out.write("{\"success\": false, \"message\": \"Invalid OTP. Please try again.\"}");
        }
    }
    
    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response, 
                                     PrintWriter out) throws Exception {
        
        HttpSession session = request.getSession();
        
        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
        String email = (String) session.getAttribute("resetEmail");
        
        System.out.println("🔐 Reset password request");
        System.out.println("OTP Verified: " + otpVerified);
        System.out.println("Email: " + email);
        
        if (otpVerified == null || !otpVerified) {
            System.out.println("❌ OTP not verified");
            out.write("{\"success\": false, \"message\": \"Please verify OTP first\"}");
            return;
        }
        
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        if (newPassword == null || confirmPassword == null) {
            out.write("{\"success\": false, \"message\": \"Password fields are required\"}");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("❌ Passwords do not match");
            out.write("{\"success\": false, \"message\": \"Passwords do not match\"}");
            return;
        }
        
        if (newPassword.length() < 6) {
            System.out.println("❌ Password too short");
            out.write("{\"success\": false, \"message\": \"Password must be at least 6 characters\"}");
            return;
        }
        
        // ========== HASH THE PASSWORD BEFORE STORING ==========
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        boolean updated = updatePassword(email, hashedPassword);
        
        if (updated) {
            System.out.println("✅ Password updated successfully (hashed)");
            
            session.removeAttribute("resetOTP");
            session.removeAttribute("resetEmail");
            session.removeAttribute("otpTimestamp");
            session.removeAttribute("otpVerified");
            
            out.write("{\"success\": true, \"message\": \"Password reset successful\"}");
        } else {
            System.out.println("❌ Failed to update password");
            out.write("{\"success\": false, \"message\": \"Failed to update password\"}");
        }
    }
    
    private boolean isEmailRegistered(String email) {
        String query = "SELECT email FROM users WHERE email = ?";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(query);
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            boolean exists = rs.next();
            
            rs.close();
            stmt.close();
            conn.close();
            
            return exists;
            
        } catch (Exception e) {
            System.out.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean updatePassword(String email, String hashedPassword) {
        String query = "UPDATE users SET password = ? WHERE email = ?";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(query);
            
            stmt.setString(1, hashedPassword);
            stmt.setString(2, email);
            
            int rowsAffected = stmt.executeUpdate();
            
            System.out.println("✅ Password updated in database for: " + email);
            System.out.println("   Hashed password stored successfully");
            
            stmt.close();
            conn.close();
            
            return rowsAffected > 0;
            
        } catch (Exception e) {
            System.out.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    
    private boolean sendPasswordResetEmail(String toEmail, String otpCode) {
        try {
            System.out.println("📤 Preparing to send email to: " + toEmail);
            
            String emailTemplate = readEmailTemplate();
            
            String resetLink = "http://localhost:8080/Medicalrecordsystem/forgot.jsp";
            emailTemplate = emailTemplate.replace("{{OTP_CODE}}", otpCode);
            emailTemplate = emailTemplate.replace("{{RESET_LINK}}", resetLink);
            
            Properties props = new Properties();
            props.put("mail.smtp.host", EMAIL_HOST);
            props.put("mail.smtp.port", EMAIL_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.ssl.trust", EMAIL_HOST);
            
            javax.mail.Session mailSession = javax.mail.Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });
            
            mailSession.setDebug(true);
            
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(EMAIL_FROM, "MedLife Hospital"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Password Reset Request - MedLife Hospital");
            message.setContent(emailTemplate, "text/html; charset=utf-8");
            
            System.out.println("📨 Sending email...");
            Transport.send(message);
            
            System.out.println("✅ Password reset email sent to: " + toEmail);
            return true;
            
        } catch (Exception e) {
            System.out.println("❌ Email sending failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private String readEmailTemplate() {
        try {
            String path = getServletContext().getRealPath(
                "/WEB-INF/email-templates/password-reset-email.html"
            );
            
            System.out.println("📄 Reading template from: " + path);
            
            return new String(Files.readAllBytes(Paths.get(path)));
            
        } catch (IOException e) {
            System.out.println("⚠️ Template file not found, using fallback");
            
            return "<!DOCTYPE html>" +
                   "<html><body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                   "<div style='max-width: 600px; margin: 0 auto; background: #f7fafc; padding: 30px; border-radius: 10px;'>" +
                   "<div style='background: linear-gradient(135deg, #81c784 0%, #66bb6a 100%); padding: 30px; text-align: center; border-radius: 10px;'>" +
                   "<h1 style='color: white; margin: 0;'>🏥 MedLife Hospital</h1>" +
                   "</div>" +
                   "<div style='background: white; padding: 30px; margin-top: 20px; border-radius: 10px;'>" +
                   "<h2 style='color: #2d3748;'>Password Reset Request</h2>" +
                   "<p style='color: #4a5568; font-size: 16px;'>Your verification code is:</p>" +
                   "<div style='background: #f0fff4; border: 2px dashed #81c784; padding: 20px; text-align: center; border-radius: 10px; margin: 20px 0;'>" +
                   "<h1 style='color: #2d5f3f; font-size: 36px; letter-spacing: 5px; margin: 0;'>{{OTP_CODE}}</h1>" +
                   "<p style='color: #718096; font-size: 12px; margin-top: 10px;'>Valid for 10 minutes</p>" +
                   "</div>" +
                   "<p style='color: #4a5568;'>If you didn't request this, please ignore this email.</p>" +
                   "</div>" +
                   "<p style='text-align: center; color: #a0aec0; font-size: 12px; margin-top: 20px;'>© 2025 MedLife Hospital</p>" +
                   "</div>" +
                   "</body></html>";
        }
    }
}