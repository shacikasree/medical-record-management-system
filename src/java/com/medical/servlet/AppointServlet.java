package com.medical.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;


@WebServlet("/AppointServlet")
public class AppointServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = Integer.parseInt(request.getParameter("userId"));
        String doctor = request.getParameter("doctor_name");
        String department = request.getParameter("department");
        String date = request.getParameter("appointment_date");
        String time = request.getParameter("appointment_time");
        String symptoms = request.getParameter("symptoms");

        try {
            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO appointments "
                       + "(user_id, doctor_name, department, appointment_date, appointment_time, symptoms, status) "
                       + "VALUES (?, ?, ?, ?, ?, ?, 'Upcoming')";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, doctor);
            ps.setString(3, department);
            ps.setString(4, date);
            ps.setString(5, time);
            ps.setString(6, symptoms);

            ps.executeUpdate();

            response.sendRedirect("patientDash.jsp?msg=success");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("PatientAppointment.jsp?error=true");
        }
    }
}
