<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>

<%
    // Session check
    HttpSession session1 = request.getSession(false);
    if (session1 == null || session1.getAttribute("email") == null) {
        response.sendRedirect("Login.jsp?error=loginRequired");
        return;
    }

    // Get doctor details from session
    String doctorId = String.valueOf(session1.getAttribute("doctorId"));
    String fullname = (String) session1.getAttribute("fullname");
    String email = (String) session1.getAttribute("email");
    String phone = (String) session1.getAttribute("phone");
    String department = (String) session1.getAttribute("department");
    String specialty = (String) session1.getAttribute("specialty");
    String licenseNumber = (String) session1.getAttribute("licenseNumber");
    
    // Handle experience - could be Integer or String
    String experience = "";
    Object expObj = session1.getAttribute("experience");
    if (expObj != null) {
        experience = String.valueOf(expObj);
    }
    
    String qualifications = (String) session1.getAttribute("qualifications");
    String address = (String) session1.getAttribute("address");
    
    // Get data from request attributes (loaded by servlet)
    List<Map<String, String>> todayAppointments = 
        (List<Map<String, String>>) request.getAttribute("todayAppointments");
    List<Map<String, String>> allAppointments = 
        (List<Map<String, String>>) request.getAttribute("allAppointments");
    List<Map<String, String>> myPatients = 
        (List<Map<String, String>>) request.getAttribute("myPatients");
    List<Map<String, String>> myPrescriptions = 
        (List<Map<String, String>>) request.getAttribute("myPrescriptions");
    
    // Calculate counts
    int todayCount = (todayAppointments != null) ? todayAppointments.size() : 0;
    int totalPatients = (myPatients != null) ? myPatients.size() : 0;
    int totalPrescriptions = (myPrescriptions != null) ? myPrescriptions.size() : 0;
    
    int completedToday = 0;
    int pendingToday = 0;
    
    if (todayAppointments != null) {
        for (Map<String, String> apt : todayAppointments) {
            String status = apt.get("status");
            if ("completed".equalsIgnoreCase(status)) {
                completedToday++;
            } else if ("pending".equalsIgnoreCase(status)) {
                pendingToday++;
            }
        }
    }
    
    // Avatar initials
    String initials = "DR";
    if (fullname != null && fullname.length() > 1) {
        String[] parts = fullname.split(" ");
        initials = parts[0].substring(0,1).toUpperCase();
        if (parts.length > 1) {
            initials += parts[1].substring(0,1).toUpperCase();
        }
    }
    
    // Current date
    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
    String currentDate = sdf.format(new Date());
    
    // Today's date for comparison
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String today = dateFormat.format(new Date());
%>
<%
    // Get availability data from request attributes
    List<Map<String, String>> weeklySchedule = 
        (List<Map<String, String>>) request.getAttribute("weeklySchedule");
    List<Map<String, String>> unavailableDates = 
        (List<Map<String, String>>) request.getAttribute("unavailableDates");
    Boolean emergencyAvailable = (Boolean) request.getAttribute("emergencyAvailable");
    
    if (weeklySchedule == null) weeklySchedule = new ArrayList<>();
    if (unavailableDates == null) unavailableDates = new ArrayList<>();
    if (emergencyAvailable == null) emergencyAvailable = true;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Doctor Dashboard - <%= fullname %></title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- Font Awesome Icons -->
    
    
    <link rel="stylesheet" href="doctor-dashboard.css">
</head>
<body>
    <!-- Sidebar Navigation -->
    <div class="sidebar" id="sidebar">
        <div class="sidebar-header">
            <i class="fas fa-user-md"></i>
            <h2>Doctor Portal</h2>
        </div>
        <nav class="sidebar-nav">
            <a href="#" class="nav-item active" data-page="dashboard">
                <i class="fas fa-home"></i>
                <span>Dashboard</span>
            </a>
            <a href="#" class="nav-item" data-page="today-appointments">
                <i class="fas fa-calendar-day"></i>
                <span>Today's Appointments</span>
            </a>
            <a href="#" class="nav-item" data-page="all-appointments">
                <i class="fas fa-calendar-alt"></i>
                <span>All Appointments</span>
            </a>
            <a href="#" class="nav-item" data-page="patients">
                <i class="fas fa-users"></i>
                <span>My Patients</span>
            </a>
            <a href="#" class="nav-item" data-page="write-prescription">
                <i class="fas fa-prescription"></i>
                <span>Write Prescription</span>
            </a>
            <a href="#" class="nav-item" data-page="prescription-history">
                <i class="fas fa-file-medical"></i>
                <span>Prescription History</span>
            </a>
            <a href="#" class="nav-item" data-page="availability">
                <i class="fas fa-clock"></i>
                <span>Availability</span>
            </a>
            <a href="#" class="nav-item" data-page="patient-records">
                <i class="fas fa-folder-open"></i>
                <span>Patient Records</span>
            </a>
            <a href="#" class="nav-item" data-page="statistics">
                <i class="fas fa-chart-bar"></i>
                <span>Statistics</span>
            </a>
            <a href="#" class="nav-item" data-page="profile">
                <i class="fas fa-user-circle"></i>
                <span>My Profile</span>
            </a>
        </nav>
        <div class="sidebar-footer">
            <a href="Login.jsp" class="logout-btn">
                <i class="fas fa-sign-out-alt"></i>
                <span>Logout</span>
            </a>
        </div>
    </div>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Header -->
        <header class="header">
            <button class="menu-toggle" id="menuToggle">
                <i class="fas fa-bars"></i>
            </button>
            <div class="header-left">
                <h1 id="pageTitle">Dashboard</h1>
            </div>
            <div class="header-right">
                <div class="notification-icon">
                    <i class="fas fa-bell"></i>
                    <span class="badge" id="notificationCount"><%= pendingToday %></span>
                </div>
                <div class="user-profile">
                    <div class="user-avatar"><%= initials %></div>
                    <span class="user-name"><%= fullname %></span>
                </div>
            </div>
        </header>

        <!-- Dashboard Content -->
        <div class="content-area" id="contentArea">
            
            <!-- Dashboard Overview -->
            <div class="page-content active" id="dashboard">
                <div class="welcome-card">
                    <h2>Welcome back, <%= fullname %>! 👋</h2>
                    <p>Here's your overview for today - <%= currentDate %></p>
                </div>

                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-icon blue">
                            <i class="fas fa-calendar-check"></i>
                        </div>
                        <div class="stat-details">
                            <h3 id="todayAppointments"><%= todayCount %></h3>
                            <p>Today's Appointments</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon green">
                            <i class="fas fa-users"></i>
                        </div>
                        <div class="stat-details">
                            <h3 id="totalPatients"><%= totalPatients %></h3>
                            <p>Total Patients</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon orange">
                            <i class="fas fa-check-circle"></i>
                        </div>
                        <div class="stat-details">
                            <h3 id="completedAppointments"><%= completedToday %></h3>
                            <p>Completed Today</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon red">
                            <i class="fas fa-clock"></i>
                        </div>
                        <div class="stat-details">
                            <h3 id="pendingAppointments"><%= pendingToday %></h3>
                            <p>Pending Today</p>
                        </div>
                    </div>
                </div>

                <div class="dashboard-grid">
                    <div class="dashboard-section">
                        <h2>Upcoming Appointments</h2>
                        <div class="appointment-list" id="dashboardAppointments">
                            <% 
                            if (todayAppointments != null && !todayAppointments.isEmpty()) {
                                int displayCount = 0;
                                for (Map<String, String> apt : todayAppointments) {
                                    if ("pending".equalsIgnoreCase(apt.get("status")) && displayCount < 5) {
                                        displayCount++;
                            %>
                            <div class="appointment-item">
                                <div class="appointment-icon">
                                    <i class="fas fa-user"></i>
                                </div>
                                <div class="appointment-info">
                                    <h4><%= apt.get("patient_name") %></h4>
                                    <p><%= apt.get("symptoms") != null && !apt.get("symptoms").isEmpty() ? apt.get("symptoms") : "Regular checkup" %></p>
                                </div>
                                <div class="appointment-time"><%= apt.get("appointment_time") %></div>
                            </div>
                            <% 
                                    }
                                }
                                if (displayCount == 0) {
                            %>
                            <p style="text-align: center; padding: 20px; color: #64748b;">No pending appointments today</p>
                            <% 
                                }
                            } else {
                            %>
                            <p style="text-align: center; padding: 20px; color: #64748b;">No appointments today</p>
                            <% } %>
                        </div>
                    </div>
                    <div class="dashboard-section">
                        <h2>Quick Actions</h2>
                        <div class="quick-actions">
                            <button class="action-btn" onclick="navigateTo('write-prescription')">
                                <i class="fas fa-prescription"></i>
                                Write Prescription
                            </button>
                            <button class="action-btn" onclick="navigateTo('patients')">
                                <i class="fas fa-users"></i>
                                View Patients
                            </button>
                            <button class="action-btn" onclick="navigateTo('today-appointments')">
                                <i class="fas fa-calendar-day"></i>
                                Today's Schedule
                            </button>
                            <button class="action-btn" onclick="navigateTo('prescription-history')">
                                <i class="fas fa-file-medical"></i>
                                Prescriptions
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Today's Appointments -->
            <div class="page-content" id="today-appointments">
                <div class="section-header">
                    <h2>Today's Appointments - <%= currentDate %></h2>
                    <button class="btn-primary" onclick="window.location.href='DoctorServlet'">
                        <i class="fas fa-sync"></i> Refresh
                    </button>
                </div>
                <div class="appointments-container" id="todayAppointmentsList">
                    <% 
                    if (todayAppointments != null && !todayAppointments.isEmpty()) {
                        for (Map<String, String> apt : todayAppointments) {
                    %>
                    <div class="appointment-card">
                        <div class="appointment-header">
                            <div class="patient-info">
                                <h3><%= apt.get("patient_name") %></h3>
                                <p><%= apt.get("patient_age") %> years, <%= apt.get("patient_gender") %></p>
                            </div>
                            <span class="status-badge <%= apt.get("status") %>"><%= apt.get("status").toUpperCase() %></span>
                        </div>
                        <div class="appointment-details">
                            <div class="detail-item">
                                <i class="fas fa-calendar"></i>
                                <span><%= apt.get("appointment_date") %></span>
                            </div>
                            <div class="detail-item">
                                <i class="fas fa-clock"></i>
                                <span><%= apt.get("appointment_time") %></span>
                            </div>
                            <div class="detail-item">
                                <i class="fas fa-phone"></i>
                                <span><%= apt.get("patient_phone") %></span>
                            </div>
                            <div class="detail-item">
                                <i class="fas fa-stethoscope"></i>
                                <span><%= apt.get("symptoms") != null && !apt.get("symptoms").isEmpty() ? apt.get("symptoms") : "Regular checkup" %></span>
                            </div>
                        </div>
                        <div class="appointment-actions" style="margin-top: 15px;">
                            <% if ("pending".equalsIgnoreCase(apt.get("status"))) { %>
                            <form action="UpdateAppointmentStatus" method="post" style="display: inline;">
                                <input type="hidden" name="appointmentId" value="<%= apt.get("id") %>">
                                <input type="hidden" name="status" value="completed">
                                <input type="hidden" name="doctorId" value="<%= doctorId %>">
                                <button type="submit" class="btn-primary btn-sm">
                                    <i class="fas fa-check"></i> Mark Completed
                                </button>
                            </form>
                            <button class="btn btn-sm btn-primary" onclick="writePrescriptionFor('<%= apt.get("patient_id") %>', '<%= apt.get("patient_name") %>')">
                                <i class="fas fa-prescription"></i> Prescribe
                            </button>
                            <% } %>
                        </div>
                    </div>
                    <% 
                        }
                    } else {
                    %>
                    <div class="empty-state">
                        <i class="fas fa-calendar"></i>
                        <p>No appointments for today</p>
                    </div>
                    <% } %>
                </div>
            </div>

            <!-- All Appointments -->
            <div class="page-content" id="all-appointments">
                <div class="section-header">
                    <h2>All Appointments</h2>
                    <div class="filter-controls">
                        <select id="appointmentFilter" onchange="filterAppointments()">
                            <option value="all">All Appointments</option>
                            <option value="pending">Pending</option>
                            <option value="completed">Completed</option>
                            <option value="cancelled">Cancelled</option>
                        </select>
                        <input type="text" id="searchAppointment" placeholder="Search patient..." onkeyup="filterAppointments()">
                    </div>
                </div>
                <div class="appointments-container" id="allAppointmentsList">
                    <% 
                    if (allAppointments != null && !allAppointments.isEmpty()) {
                        for (Map<String, String> apt : allAppointments) {
                    %>
                    <div class="appointment-card" data-status="<%= apt.get("status") %>" data-patient="<%= apt.get("patient_name").toLowerCase() %>">
                        <div class="appointment-header">
                            <div class="patient-info">
                                <h3><%= apt.get("patient_name") %></h3>
                                <p><%= apt.get("patient_age") %> years, <%= apt.get("patient_gender") %></p>
                            </div>
                            <span class="status-badge <%= apt.get("status") %>"><%= apt.get("status").toUpperCase() %></span>
                        </div>
                        <div class="appointment-details">
                            <div class="detail-item">
                                <i class="fas fa-calendar"></i>
                                <span><%= apt.get("appointment_date") %></span>
                            </div>
                            <div class="detail-item">
                                <i class="fas fa-clock"></i>
                                <span><%= apt.get("appointment_time") %></span>
                            </div>
                            <div class="detail-item">
                                <i class="fas fa-stethoscope"></i>
                                <span><%= apt.get("symptoms") != null && !apt.get("symptoms").isEmpty() ? apt.get("symptoms") : "Regular checkup" %></span>
                            </div>
                        </div>
                    </div>
                    <% 
                        }
                    } else {
                    %>
                    <div class="empty-state">
                        <i class="fas fa-calendar"></i>
                        <p>No appointments found</p>
                    </div>
                    <% } %>
                </div>
            </div>

            <!-- My Patients -->
            <div class="page-content" id="patients">
                <div class="section-header">
                    <h2>My Patients (<%= totalPatients %>)</h2>
                    <input type="text" id="searchPatient" placeholder="Search patients..." onkeyup="searchPatients()">
                </div>
                <div class="patients-grid" id="patientsList">
                    <% 
                    if (myPatients != null && !myPatients.isEmpty()) {
                        for (Map<String, String> patient : myPatients) {
                            String patInitials = patient.get("fullname").substring(0, 1).toUpperCase();
                    %>
                    <div class="patient-card" data-name="<%= patient.get("fullname").toLowerCase() %>">
                        <div class="patient-header">
                            <div class="patient-avatar"><%= patInitials %></div>
                            <div class="patient-name">
                                <h3><%= patient.get("fullname") %></h3>
                                <p><%= patient.get("age") %> years, <%= patient.get("gender") %></p>
                            </div>
                        </div>
                        <div class="patient-details">
                            <div class="patient-detail">
                                <i class="fas fa-phone"></i>
                                <span><%= patient.get("phone") %></span>
                            </div>
                            <div class="patient-detail">
                                <i class="fas fa-tint"></i>
                                <span>Blood: <%= patient.get("blood_group") != null ? patient.get("blood_group") : "N/A" %></span>
                            </div>
                            <div class="patient-detail">
                                <i class="fas fa-calendar"></i>
                                <span>Last Visit: <%= patient.get("last_visit") != null ? patient.get("last_visit") : "N/A" %></span>
                            </div>
                        </div>
                        <div class="patient-actions">
                            <button class="btn-primary btn-sm" onclick="writePrescriptionFor('<%= patient.get("id") %>', '<%= patient.get("fullname") %>')">
                                <i class="fas fa-prescription"></i> Prescribe
                            </button>
                        </div>
                    </div>
                    <% 
                        }
                    } else {
                    %>
                    <div class="empty-state" style="grid-column: 1/-1;">
                        <i class="fas fa-users"></i>
                        <p>No patients found</p>
                    </div>
                    <% } %>
                </div>
            </div>

            <!-- Write Prescription -->
            <div class="page-content" id="write-prescription">
                <div class="prescription-form">
                    <h2>Write Prescription</h2>
                    <form id="prescriptionForm" action="SavePrescription" method="post">
                        <input type="hidden" name="doctorId" value="<%= doctorId %>">
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label>Select Patient *</label>
                                <select id="prescriptionPatient" name="patientId" required>
                                    <option value="">Choose Patient</option>
                                    <% 
                                    if (myPatients != null) {
                                        for (Map<String, String> patient : myPatients) {
                                    %>
                                    <option value="<%= patient.get("id") %>"><%= patient.get("fullname") %> (<%= patient.get("age") %> years)</option>
                                    <% 
                                        }
                                    }
                                    %>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>Date *</label>
                                <input type="date" id="prescriptionDate" name="prescriptionDate" value="<%= today %>" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label>Chief Complaint / Diagnosis *</label>
                            <textarea id="diagnosis" name="diagnosis" rows="3" required></textarea>
                        </div>

                        <div class="medicines-section">
                            <div class="section-header">
                                <h3>Medicines</h3>
                                <button type="button" class="btn-secondary" onclick="addMedicine()">
                                    <i class="fas fa-plus"></i> Add Medicine
                                </button>
                            </div>
                            <div id="medicinesList"></div>
                        </div>

                        <div class="form-group">
                            <label>Special Instructions</label>
                            <textarea id="specialInstructions" name="instructions" rows="3"></textarea>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label>Follow-up Date</label>
                                <input type="date" id="followupDate" name="followupDate">
                            </div>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn-primary">
                                <i class="fas fa-save"></i> Save Prescription
                            </button>
                            <button type="button" class="btn-secondary" onclick="clearPrescriptionForm()">
                                <i class="fas fa-times"></i> Clear
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Prescription History -->
            <div class="page-content" id="prescription-history">
                <div class="section-header">
                    <h2>Prescription History (<%= totalPrescriptions %>)</h2>
                    <input type="text" id="searchPrescription" placeholder="Search by patient name..." onkeyup="searchPrescriptions()">
                </div>
                <div class="prescriptions-list" id="prescriptionHistoryList">
                    <% 
                    if (myPrescriptions != null && !myPrescriptions.isEmpty()) {
                        for (Map<String, String> rx : myPrescriptions) {
                    %>
                    <div class="prescription-card" data-patient="<%= rx.get("patient_name").toLowerCase() %>">
                        <div class="prescription-header">
                            <div>
                                <h3><%= rx.get("patient_name") %></h3>
                                <p><i class="fas fa-calendar"></i> <%= rx.get("prescribed_date") %></p>
                            </div>
                        </div>
                        <p style="margin: 15px 0;"><strong>Diagnosis:</strong> <%= rx.get("diagnosis") %></p>
                        <div class="prescription-medicines">
                            <h4>Medicines:</h4>
                            <div class="medicine-list-item">
                                <strong><%= rx.get("medicine_name") %></strong> - <%= rx.get("dosage") %><br>
                                <small><%= rx.get("frequency") %> for <%= rx.get("duration") %></small>
                            </div>
                        </div>
                        <% if (rx.get("instructions") != null && !rx.get("instructions").isEmpty()) { %>
                        <p style="margin-top: 10px;"><strong>Instructions:</strong> <%= rx.get("instructions") %></p>
                        <% } %>
                    </div>
                    <% 
                        }
                    } else {
                    %>
                    <div class="empty-state">
                        <i class="fas fa-prescription"></i>
                        <p>No prescriptions found</p>
                    </div>
                    <% } %>
                </div>
            </div>

        
           <!-- Availability Management -->
<div class="page-content" id="availability">
    <h2>Manage Availability</h2>
    
    <!-- Success/Error Messages -->
    <% 
    String success = request.getParameter("success");
    String error = request.getParameter("error");
    
    if ("scheduleUpdated".equals(success)) { %>
        <div class="alert alert-success">
            <i class="fas fa-check-circle"></i> Schedule updated successfully!
        </div>
    <% } else if ("dateAdded".equals(success)) { %>
        <div class="alert alert-success">
            <i class="fas fa-check-circle"></i> Unavailable date added successfully!
        </div>
    <% } else if ("dateRemoved".equals(success)) { %>
        <div class="alert alert-success">
            <i class="fas fa-check-circle"></i> Date removed successfully!
        </div>
    <% } else if ("emergencyUpdated".equals(success)) { %>
        <div class="alert alert-success">
            <i class="fas fa-check-circle"></i> Emergency status updated!
        </div>
    <% } 
    
    if (error != null) { %>
        <div class="alert alert-error">
            <i class="fas fa-exclamation-circle"></i> An error occurred. Please try again.
        </div>
    <% } %>
    
    <div class="availability-container">
        <!-- Weekly Schedule -->
        <div class="weekly-schedule">
            <h3>Weekly Schedule</h3>
            <form action="SaveSchedule" method="post" id="scheduleForm">
                <input type="hidden" name="action" value="saveSchedule">
                
                <div id="weeklySchedule">
                    <% 
                    if (weeklySchedule != null && !weeklySchedule.isEmpty()) {
                        for (Map<String, String> day : weeklySchedule) {
                            String dayName = day.get("day_name");
                            boolean isActive = "true".equals(day.get("is_active"));
                            String startTime = day.get("start_time");
                            String endTime = day.get("end_time");
                            
                            if (startTime == null) startTime = "";
                            if (endTime == null) endTime = "";
                    %>
                    <div class="schedule-day">
                        <span class="day-name"><%= dayName %></span>
                        <div class="schedule-controls">
                            <label class="checkbox-label">
                                <input type="checkbox" 
                                       name="<%= dayName %>_active" 
                                       <%= isActive ? "checked" : "" %>
                                       onchange="toggleDayInputs(this, '<%= dayName %>')"> Active
                            </label>
                            <input type="time" 
                                   name="<%= dayName %>_start" 
                                   value="<%= startTime %>" 
                                   class="time-input"
                                   id="<%= dayName %>_start"
                                   <%= !isActive ? "disabled" : "" %>>
                            <span class="time-separator">to</span>
                            <input type="time" 
                                   name="<%= dayName %>_end" 
                                   value="<%= endTime %>" 
                                   class="time-input"
                                   id="<%= dayName %>_end"
                                   <%= !isActive ? "disabled" : "" %>>
                        </div>
                    </div>
                    <% 
                        }
                    }
                    %>
                </div>
                
                <button type="submit" class="btn-primary" style="width: 100%; margin-top: 20px;">
                    <i class="fas fa-save"></i> Save Schedule
                </button>
            </form>
        </div>
        
        <!-- Unavailable Dates -->
        <div class="unavailable-dates">
            <h3>Mark Unavailable Dates</h3>
            
            <form action="SaveSchedule" method="post" id="unavailableDateForm">
                <input type="hidden" name="action" value="addUnavailableDate">
                
                <div class="form-group">
                    <label>Select Date</label>
                    <input type="date" 
                           name="unavailableDate" 
                           id="unavailableDate" 
                           min="<%= java.time.LocalDate.now() %>"
                           required
                           style="width: 100%; padding: 10px; border: 1px solid #e5e7eb; border-radius: 8px;">
                </div>
                
                <div class="form-group">
                    <label>Reason (Optional)</label>
                    <input type="text" 
                           name="reason" 
                           placeholder="e.g., Personal leave, Conference"
                           style="width: 100%; padding: 10px; border: 1px solid #e5e7eb; border-radius: 8px;">
                </div>
                
                <button type="submit" class="btn-primary" style="width: 100%; margin-top: 10px;">
                    <i class="fas fa-plus"></i> Mark Unavailable
                </button>
            </form>
            
            <div id="unavailableDatesList" style="margin-top: 20px;">
                <% 
                if (unavailableDates != null && !unavailableDates.isEmpty()) {
                    for (Map<String, String> date : unavailableDates) {
                        String dateId = date.get("id");
                        String dateValue = date.get("date");
                        String reason = date.get("reason");
                        
                        // Format date
                        java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("MMM dd, yyyy");
                        String formattedDate = dateValue;
                        try {
                            formattedDate = outputFormat.format(inputFormat.parse(dateValue));
                        } catch (Exception e) {}
                %>
                <div style="display: flex; justify-content: space-between; align-items: center; padding: 12px; margin-bottom: 8px; background: #fef2f2; border-radius: 8px; border-left: 3px solid #ef4444;">
                    <div>
                        <span style="color: #991b1b; font-weight: 500; display: block;"><%= formattedDate %></span>
                        <% if (reason != null && !reason.isEmpty()) { %>
                        <span style="color: #dc2626; font-size: 12px;"><%= reason %></span>
                        <% } %>
                    </div>
                    <form action="SaveSchedule" method="post" style="display: inline;">
                        <input type="hidden" name="action" value="removeUnavailableDate">
                        <input type="hidden" name="dateId" value="<%= dateId %>">
                        <button type="submit" 
                                onclick="return confirm('Remove this unavailable date?')"
                                style="background: #ef4444; color: white; border: none; padding: 5px 12px; border-radius: 5px; cursor: pointer; font-size: 12px;">
                            <i class="fas fa-trash"></i>
                        </button>
                    </form>
                </div>
                <% 
                    }
                } else {
                %>
                <p style="text-align: center; color: #64748b; padding: 20px;">No unavailable dates marked</p>
                <% } %>
            </div>
        </div>
        
        <!-- Emergency Availability -->
        <div class="emergency-toggle">
            <h3>Emergency Availability</h3>
            <form id="emergencyForm">
                <div style="display: flex; align-items: center; gap: 15px; margin-top: 15px;">
                    <label class="switch">
                        <input type="checkbox" 
                               id="emergencyAvailable" 
                               <%= emergencyAvailable ? "checked" : "" %>
                               onchange="updateEmergencyStatus(this.checked)">
                        <span class="slider"></span>
                    </label>
                    <span id="emergencyStatus" 
                          style="color: <%= emergencyAvailable ? "#10b981" : "#ef4444" %>; font-weight: 500;">
                        <%= emergencyAvailable ? "Available for emergencies" : "Not available for emergencies" %>
                    </span>
                </div>
            </form>
        </div>
    </div>
</div>
            <!-- Patient Records -->
            <div class="page-content" id="patient-records">
                <div class="section-header">
                    <h2>Patient Records</h2>
                    <select id="recordsPatientSelect" onchange="loadPatientRecords()">
                        <option value="">Select Patient</option>
                        <% 
                        if (myPatients != null) {
                            for (Map<String, String> patient : myPatients) {
                        %>
                        <option value="<%= patient.get("id") %>"><%= patient.get("fullname") %></option>
                        <% 
                            }
                        }
                        %>
                    </select>
                </div>
                <div class="records-container" id="patientRecordsContainer">
                    <div class="no-selection" style="text-align: center; padding: 60px 20px; color: #64748b;">
                        <i class="fas fa-folder-open" style="font-size: 64px; margin-bottom: 20px; opacity: 0.3;"></i>
                        <p>Select a patient to view records</p>
                    </div>
                </div>
            </div>

            <!-- Statistics -->
            <div class="page-content" id="statistics">
                <h2>Statistics & Analytics</h2>
                <div class="stats-overview" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 30px;">
                    <div class="stat-card">
                        <h3 id="totalPatientsStats"><%= totalPatients %></h3>
                        <p>Total Patients Treated</p>
                    </div>
                    <div class="stat-card">
                        <h3 id="monthAppointments"><%= todayCount + completedToday %></h3>
                        <p>Total Appointments</p>
                    </div>
                    <div class="stat-card">
                        <h3 id="totalPrescriptionsStats"><%= totalPrescriptions %></h3>
                        <p>Total Prescriptions</p>
                    </div>
                    <div class="stat-card">
                        <h3 id="completionRate"><%= todayCount > 0 ? Math.round((completedToday * 100.0) / todayCount) : 0 %>%</h3>
                        <p>Completion Rate</p>
                    </div>
                </div>
                
                <div class="card">
                    <div class="card-header">
                        <h3><i class="fas fa-chart-line"></i> Recent Activity Overview</h3>
                    </div>
                    <div class="card-body">
                        <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px;">
                            <div style="padding: 20px; background: #f0f9ff; border-radius: 10px; border-left: 4px solid #3b82f6;">
                                <p style="color: #64748b; font-size: 14px; margin-bottom: 8px;">Today's Appointments</p>
                                <h3 style="color: #3b82f6; font-size: 32px; font-weight: 700;"><%= todayCount %></h3>
                            </div>
                            <div style="padding: 20px; background: #f0fdf4; border-radius: 10px; border-left: 4px solid #10b981;">
                                <p style="color: #64748b; font-size: 14px; margin-bottom: 8px;">Completed Today</p>
                                <h3 style="color: #10b981; font-size: 32px; font-weight: 700;"><%= completedToday %></h3>
                            </div>
                            <div style="padding: 20px; background: #fef3c7; border-radius: 10px; border-left: 4px solid #f59e0b;">
                                <p style="color: #64748b; font-size: 14px; margin-bottom: 8px;">Pending Today</p>
                                <h3 style="color: #f59e0b; font-size: 32px; font-weight: 700;"><%= pendingToday %></h3>
                            </div>
                            <div style="padding: 20px; background: #fce7f3; border-radius: 10px; border-left: 4px solid #ec4899;">
                                <p style="color: #64748b; font-size: 14px; margin-bottom: 8px;">Active Patients</p>
                                <h3 style="color: #ec4899; font-size: 32px; font-weight: 700;"><%= totalPatients %></h3>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- My Profile -->
            <div class="page-content" id="profile">
                <div class="profile-container">
                    <h2>My Profile</h2>
                    <div class="profile-content">
                        <div class="profile-photo">
                            <div class="profile-avatar-large"><%= initials %></div>
                        </div>
                        <form id="profileForm" class="profile-form">
                            <div class="form-row">
                                <div class="form-group">
                                    <label>Full Name *</label>
                                    <input type="text" id="doctorName" value="<%= fullname %>" disabled>
                                </div>
                                <div class="form-group">
                                    <label>Specialty *</label>
                                    <input type="text" id="specialty" value="<%= specialty != null ? specialty : "" %>" disabled>
                                </div>
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label>License Number *</label>
                                    <input type="text" id="licenseNumber" value="<%= licenseNumber != null ? licenseNumber : "" %>" disabled>
                                </div>
                                <div class="form-group">
                                    <label>Experience (Years)</label>
                                    <input type="text" id="experience" value="<%= experience != null ? experience : "" %>" disabled>
                                </div>
                            </div>
                            <div class="form-group">
                                <label>Qualifications</label>
                                <textarea id="qualifications" rows="3" disabled><%= qualifications != null ? qualifications : "" %></textarea>
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label>Email *</label>
                                    <input type="email" id="email" value="<%= email != null ? email : "" %>" disabled>
                                </div>
                                <div class="form-group">
                                    <label>Phone *</label>
                                    <input type="tel" id="phone" value="<%= phone != null ? phone : "" %>" disabled>
                                </div>
                            </div>
                            <div class="form-group">
                                <label>Department</label>
                                <input type="text" id="department" value="<%= department != null ? department : "" %>" disabled>
                            </div>
                            <div class="form-group">
                                <label>Address</label>
                                <textarea id="address" rows="2" disabled><%= address != null ? address : "" %></textarea>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

<script src="js/doctor.js"></script>
  
</body>
</html>