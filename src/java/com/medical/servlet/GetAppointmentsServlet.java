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
import javax.servlet.http.HttpSession;
import com.google.gson.Gson;

@WebServlet("/GetAppointmentsServlet")
public class GetAppointmentsServlet extends HttpServlet {
    
    // UPDATE THIS PASSWORD TO YOUR MYSQL ROOT PASSWORD!
    private static final String DB_URL = "jdbc:mysql://localhost:3306/medical_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "shacika@@2006"; // <-- PUT YOUR MYSQL PASSWORD HERE
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String doctorId = (String) session.getAttribute("doctorId");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        if(doctorId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"error\": \"Not logged in\"}");
            return;
        }
        
        String filter = request.getParameter("filter");
        List<Appointment> appointments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT a.id, a.patient_id, a.appointment_date, a.appointment_time, ");
            sql.append("a.status, a.symptoms, a.appointment_type, ");
            sql.append("p.name as patient_name ");
            sql.append("FROM appointments a ");
            sql.append("JOIN patients p ON a.patient_id = p.id ");
            sql.append("WHERE a.doctor_id = ? ");
            
            if("today".equals(filter)) {
                sql.append("AND DATE(a.appointment_date) = CURDATE() ");
            } else if("upcoming".equals(filter)) {
                sql.append("AND DATE(a.appointment_date) > CURDATE() ");
            } else if("completed".equals(filter)) {
                sql.append("AND a.status = 'completed' ");
            }
            
            sql.append("ORDER BY a.appointment_date DESC, a.appointment_time DESC");
            
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, doctorId);
            rs = stmt.executeQuery();
            
            while(rs.next()) {
                Appointment apt = new Appointment();
                apt.id = rs.getInt("id");
                apt.patientId = rs.getInt("patient_id");
                apt.patientName = rs.getString("patient_name");
                apt.date = rs.getDate("appointment_date").toString();
                apt.time = rs.getTime("appointment_time").toString();
                apt.status = rs.getString("status");
                apt.symptoms = rs.getString("symptoms");
                apt.type = rs.getString("appointment_type");
                appointments.add(apt);
            }
            
            Gson gson = new Gson();
            out.write(gson.toJson(appointments));
            
        } catch(Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
    
    class Appointment {
        int id;
        int patientId;
        String patientName;
        String date;
        String time;
        String status;
        String symptoms;
        String type;
    }
}