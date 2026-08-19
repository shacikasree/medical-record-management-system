<%-- 
    Document   : appointments
    Created on : 1 Jan 2026, 6:37:07 pm
    Author     : Lenovo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>

 



<%
    // Get values from session (recommended)
    String fullname = (String) session.getAttribute("fullname");
    String email = (String) session.getAttribute("email");

    if (fullname == null) {
        fullname = "User";
    }
    if (email == null) {
        email = "user@email.com";
    }

    // Initials logic
    String initials = "U";
    if (fullname != null && fullname.trim().length() > 1) {
        String[] parts = fullname.trim().split(" ");
        initials = parts[0].substring(0, 1).toUpperCase();
        if (parts.length > 1) {
            initials += parts[1].substring(0, 1).toUpperCase();
        }
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Patient Dashboard</title>
</head>

<style>
/* ===== YOUR FULL CSS (UNCHANGED) ===== */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}
body {
    font-family: Arial, sans-serif;
    background: #f0f2f5;
}
.dashboard-wrapper {
    display: flex;
    min-height: 100vh;
}
.sidebar {
    width: 260px;
    background: white;
    box-shadow: 2px 0 5px rgba(0,0,0,0.1);
}
.logo {
    padding: 25px 20px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
}
.menu-item {
    padding: 15px 20px;
    cursor: pointer;
}
.main-content {
    flex: 1;
    padding: 30px;
}
.header {
    background: white;
    padding: 20px;
    border-radius: 10px;
    display: flex;
    justify-content: space-between;
}
.user-avatar {
    width: 45px;
    height: 45px;
    border-radius: 50%;
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
}
.section {
    background: white;
    padding: 25px;
    border-radius: 10px;
    margin-top: 20px;
}
</style>

<body>

<div class="dashboard-wrapper">

    <!-- Sidebar -->
    <div class="sidebar">
        <div class="logo">
            <h2>🏥 MediCare</h2>
            <p>Patient Portal</p>
        </div>

        <div class="menu">
            <div class="menu-item">👤 View Profile</div>
            <div class="menu-item">
                📅 <a href="AppointServlet">Appointments</a>
            </div>
            <div class="menu-item">💊 Prescriptions</div>
        </div>

        <div style="padding:20px;">
            <button onclick="location.href='PatientAppointment.jsp'">➕ Book Appointment</button><br><br>
            <button onclick="logout()">🚪 Logout</button>
        </div>
    </div>

    <!-- Main Content -->
    <div class="main-content">

        <div class="header">
            <h1>View Profile</h1>

            <div style="display:flex;align-items:center;gap:10px;">
                <div class="user-avatar"><%= initials %></div>
                <div>
                    <h3><%= fullname %></h3>
                    <p><%= email %></p>
                </div>
            </div>
        </div>

        <!-- Appointments -->
        <div class="section">
            <h2>My Appointments</h2>

            <%
                List<Map<String,String>> upcoming =
                    (List<Map<String,String>>) request.getAttribute("upcomingAppointments");

                if (upcoming != null && !upcoming.isEmpty()) {
                    for (Map<String,String> a : upcoming) {
            %>
                <p>
                    <strong>Date:</strong> <%= a.get("appointment_date") %><br>
                    <strong>Doctor:</strong> <%= a.get("doctor_name") %>
                </p>
                <hr>
            <%
                    }
                } else {
            %>
                <p>No upcoming appointments</p>
            <%
                }
            %>

        </div>

    </div>
</div>

<script>


</body>
</html>
