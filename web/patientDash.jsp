<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    // Session check
    HttpSession session1 = request.getSession(false);
    if (session1 == null || session1.getAttribute("email") == null) {
        response.sendRedirect("Login.jsp?error=loginRequired");
        return;
    }

    // Get user details from session
    String fullname = (String) session1.getAttribute("fullname");
    String email = (String) session1.getAttribute("email");
    String phone = (String) session1.getAttribute("phone");
    String dob = (String) session1.getAttribute("dob");
    String gender = (String) session1.getAttribute("gender");
    String address = (String) session1.getAttribute("address");
    String userId = String.valueOf(session1.getAttribute("userId"));
    String bloodGroup = (String) session1.getAttribute("blood");
    
    // Get data from request attributes
    List<Map<String, String>> upcomingApts = 
        (List<Map<String, String>>) request.getAttribute("upcomingAppointments");
    List<Map<String, String>> completedApts = 
        (List<Map<String, String>>) request.getAttribute("completedAppointments");
    List<Map<String, String>> activeRx = 
        (List<Map<String, String>>) request.getAttribute("activePrescriptions");
    List<Map<String, String>> completedRx = 
        (List<Map<String, String>>) request.getAttribute("completedPrescriptions");
    
    // Calculate counts
    int upcomingCount = (upcomingApts != null) ? upcomingApts.size() : 0;
    int completedCount = (completedApts != null) ? completedApts.size() : 0;
    int activeRxCount = (activeRx != null) ? activeRx.size() : 0;
    int totalAppointments = upcomingCount + completedCount;
    
    // Avatar initials
    String initials = "U";
    if (fullname != null && fullname.length() > 1) {
        String[] parts = fullname.split(" ");
        initials = parts[0].substring(0,1).toUpperCase();
        if (parts.length > 1) {
            initials += parts[1].substring(0,1).toUpperCase();
        }
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Patient Dashboard - Medical Record System</title>
      
    <!-- ✅ ADD THIS LINE - Font Awesome Icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
     <link rel="stylesheet" href="patient.css">

</head>

<body>
    <!-- Sidebar -->
    <div class="sidebar" id="sidebar">
        <div class="logo">
            <i class="fas fa-hospital"></i>
            <h2>Medical Record System</h2>
        </div>
        <nav class="nav-menu">
            <div class="nav-link active" onclick="showSection('dashboard')">
                <i class="fas fa-home"></i>
                <span>Dashboard</span>
            </div>
            <div class="nav-link" onclick="showSection('appointments')">
                <i class="fas fa-calendar-check"></i>
                <span>My Appointments</span>
            </div>
            <div class="nav-link" onclick="window.location.href='PatientAppoinment.jsp'">
                <i class="fas fa-calendar-plus"></i>
                <span>Book Appointment</span>
            </div>
            <div class="nav-link" onclick="showSection('prescriptions')">
                <i class="fas fa-prescription"></i>
                <span>My Prescriptions</span>
            </div>
           <div class="nav-link" onclick="showSection('medicalRecords')">
                <i class="fas fa-file-medical"></i>
                <span>Medical Records</span>
            </div>
            <div class="nav-link" onclick="showSection('profile')">
                <i class="fas fa-user"></i>
                <span>My Profile</span>
            </div>
        </nav>
        <div class="sidebar-footer">
            <div class="nav-link" onclick="logout()">
                <i class="fas fa-sign-out-alt"></i>
                <span>Logout</span>
            </div>
        </div>
    </div>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Header -->
        <header class="header">
            <div class="header-left">
                <button class="menu-toggle" id="menuToggle" onclick="toggleSidebar()">
                    <i class="fas fa-bars"></i>
                </button>
                <h1 id="pageTitle">Dashboard</h1>
            </div>
            <div class="header-right">
                <div class="notification">
                    <i class="fas fa-bell"></i>
                    <span class="notification-badge"><%= upcomingCount %></span>
                </div>
                <div class="user-info">
                    <div class="user-avatar"><%= initials %></div>
                    <span class="user-name"><%= fullname %></span>
                </div>
            </div>
        </header>

        <!-- Content Area -->
        <div class="content-area">
            <!-- Dashboard Page -->
            <div id="dashboardPage" class="page active">
                <div class="welcome-card">
                    <h2>Welcome back, <%= fullname %>! 👋</h2>
                    <p>Here's your health overview for today</p>
                </div>

                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-icon blue">
                            <i class="fas fa-calendar-check"></i>
                        </div>
                        <div class="stat-details">
                            <h3><%= totalAppointments %></h3>
                            <p>Total Appointments</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon green">
                            <i class="fas fa-clock"></i>
                        </div>
                        <div class="stat-details">
                            <h3><%= upcomingCount %></h3>
                            <p>Upcoming Appointments</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon purple">
                            <i class="fas fa-prescription"></i>
                        </div>
                        <div class="stat-details">
                            <h3><%= activeRxCount %></h3>
                            <p>Active Prescriptions</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon orange">
                            <i class="fas fa-file-medical"></i>
                        </div>
                        <div class="stat-details">
                            <h3><%= completedCount %></h3>
                            <p>Medical Records</p>
                        </div>
                    </div>
                </div>

                <div class="dashboard-grid">
                    <div class="card">
                        <div class="card-header">
                            <h3><i class="fas fa-calendar-alt"></i> Upcoming Appointments</h3>
                            <span class="view-all" onclick="showSection('appointments')">View All</span>
                        </div>
                        <div class="card-body">
                            <% 
                            if (upcomingApts != null && !upcomingApts.isEmpty()) {
                                int displayCount = Math.min(upcomingApts.size(), 3);
                                for (int i = 0; i < displayCount; i++) {
                                    Map<String, String> apt = upcomingApts.get(i);
                            %>
                            <div class="appointment-item">
                                <div class="appointment-icon">
                                    <i class="fas fa-user-md"></i>
                                </div>
                                <div class="appointment-details">
                                    <h4><%= apt.get("doctor_name") %></h4>
                                    <p><%= apt.get("department") %></p>
                                    <div class="appointment-time">
                                        <i class="fas fa-clock"></i>
                                        <span><%= apt.get("appointment_date") %> at <%= apt.get("appointment_time") %></span>
                                    </div>
                                </div>
                                <span class="status-badge upcoming">Upcoming</span>
                            </div>
                            <% 
                                }
                            } else {
                            %>
                            <div class="empty-state">
                                <i class="fas fa-calendar"></i>
                                <p>No upcoming appointments</p>
                            </div>
                            <% } %>
                        </div>
                    </div>

                    <div class="card">
                        <div class="card-header">
                            <h3><i class="fas fa-history"></i> Recent Activity</h3>
                        </div>
                        <div class="card-body">
                            <% if (upcomingApts != null && !upcomingApts.isEmpty()) { %>
                            <div class="activity-item">
                                <div class="activity-icon" style="background-color: #3b82f6;">
                                    <i class="fas fa-calendar-check"></i>
                                </div>
                                <div class="activity-content">
                                    <h5>Appointment Scheduled</h5>
                                    <p>New appointment with Dr. <%= upcomingApts.get(0).get("doctor_name") %></p>
                                </div>
                            </div>
                            <% } %>
                            
                            <% if (activeRx != null && !activeRx.isEmpty()) { %>
                            <div class="activity-item">
                                <div class="activity-icon" style="background-color: #8b5cf6;">
                                    <i class="fas fa-prescription"></i>
                                </div>
                                <div class="activity-content">
                                    <h5>New Prescription</h5>
                                    <p>Received prescription for <%= activeRx.get(0).get("medicine_name") %></p>
                                </div>
                            </div>
                            <% } %>
                            
                            <% if (completedApts != null && !completedApts.isEmpty()) { %>
                            <div class="activity-item">
                                <div class="activity-icon" style="background-color: #10b981;">
                                    <i class="fas fa-check-circle"></i>
                                </div>
                                <div class="activity-content">
                                    <h5>Appointment Completed</h5>
                                    <p>Visited Dr. <%= completedApts.get(0).get("doctor_name") %></p>
                                </div>
                            </div>
                            <% } %>
                            
                            <% if ((upcomingApts == null || upcomingApts.isEmpty()) && 
                                   (activeRx == null || activeRx.isEmpty()) && 
                                   (completedApts == null || completedApts.isEmpty())) { %>
                            <div class="empty-state">
                                <i class="fas fa-history"></i>
                                <p>No recent activity</p>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>
            </div>
<!-- My Appointments Page -->

<div id="appointmentsPage" class="page">
    <div class="page-header">
    
        <button class="btn btn-primary" onclick="window.location.href='PatientAppoinment.jsp'">
            <i class="fas fa-plus"></i> Book New Appointment
        </button>
    </div>

    <% 
    String successMsg = (String) request.getAttribute("successMessage");
    if (successMsg != null) { 
    %>
    <div class="success-message show">
        <%= successMsg %>
    </div>
    <% } %>

    <div class="filter-tabs">
        <button class="tab-btn active" onclick="filterAppointments('all')">All</button>
        <button class="tab-btn" onclick="filterAppointments('upcoming')">Upcoming</button>
        <button class="tab-btn" onclick="filterAppointments('completed')">Completed</button>
        <button class="tab-btn" onclick="filterAppointments('cancelled')">Cancelled</button>
    </div>

    <div class="appointments-container" id="appointmentsContainer">
        <%
        // Get cancelled appointments
        List<Map<String, String>> cancelledApts = 
            (List<Map<String, String>>) request.getAttribute("cancelledAppointments");
        int cancelledCount = (cancelledApts != null) ? cancelledApts.size() : 0;
        
        // UPCOMING APPOINTMENTS
        if (upcomingApts != null) {
            for (Map<String, String> apt : upcomingApts) {
                String doctorInitials = "DR";
                try {
                    if (apt.get("doctor_name") != null && apt.get("doctor_name").length() >= 6) {
                        doctorInitials = apt.get("doctor_name").substring(4, 6).toUpperCase();
                    }
                } catch(Exception e) {
                    doctorInitials = "DR";
                }
        %>
        <div class="appointment-card" data-status="upcoming">
            <div class="appointment-info">
                <div class="doctor-avatar"><%= doctorInitials %></div>
                <div class="appointment-meta">
                    <h4><%= apt.get("doctor_name") %></h4>
                    <p><i class="fas fa-hospital"></i> <%= apt.get("department") %></p>
                    <p><i class="fas fa-clock"></i> <%= apt.get("appointment_date") %> at <%= apt.get("appointment_time") %></p>
                    <% if (apt.get("symptoms") != null && !apt.get("symptoms").isEmpty()) { %>
                    <p><i class="fas fa-notes-medical"></i> <%= apt.get("symptoms") %></p>
                    <% } %>
                </div>
            </div>
            <div style="display: flex; flex-direction: column; align-items: flex-end; gap: 15px;">
                <span class="status-badge upcoming">Upcoming</span>
                <div class="appointment-actions">
                    <button class="btn btn-sm btn-danger" onclick="cancelAppointment('<%= apt.get("id") %>')">
                        <i class="fas fa-times"></i> Cancel
                    </button>
                    <button class="btn btn-sm btn-success" onclick="markCompleted('<%= apt.get("id") %>')">
                        <i class="fas fa-check"></i> Complete
                    </button>
                </div>
            </div>
        </div>
        <% 
            }
        }
        
        // COMPLETED APPOINTMENTS
        if (completedApts != null) {
            for (Map<String, String> apt : completedApts) {
                String doctorInitials = "DR";
                try {
                    if (apt.get("doctor_name") != null && apt.get("doctor_name").length() >= 6) {
                        doctorInitials = apt.get("doctor_name").substring(4, 6).toUpperCase();
                    }
                } catch(Exception e) {
                    doctorInitials = "DR";
                }
        %>
        <div class="appointment-card" data-status="completed">
            <div class="appointment-info">
                <div class="doctor-avatar"><%= doctorInitials %></div>
                <div class="appointment-meta">
                    <h4><%= apt.get("doctor_name") %></h4>
                    <p><i class="fas fa-hospital"></i> <%= apt.get("department") %></p>
                    <p><i class="fas fa-clock"></i> <%= apt.get("appointment_date") %> at <%= apt.get("appointment_time") %></p>
                    <% if (apt.get("symptoms") != null && !apt.get("symptoms").isEmpty()) { %>
                    <p><i class="fas fa-notes-medical"></i> <%= apt.get("symptoms") %></p>
                    <% } %>
                </div>
            </div>
            <span class="status-badge completed">Completed</span>
        </div>
        <% 
            }
        }
        
        // CANCELLED APPOINTMENTS
        if (cancelledApts != null) {
            for (Map<String, String> apt : cancelledApts) {
                String doctorInitials = "DR";
                try {
                    if (apt.get("doctor_name") != null && apt.get("doctor_name").length() >= 6) {
                        doctorInitials = apt.get("doctor_name").substring(4, 6).toUpperCase();
                    }
                } catch(Exception e) {
                    doctorInitials = "DR";
                }
        %>
        <div class="appointment-card" data-status="cancelled">
            <div class="appointment-info">
                <div class="doctor-avatar" style="background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);"><%= doctorInitials %></div>
                <div class="appointment-meta">
                    <h4><%= apt.get("doctor_name") %></h4>
                    <p><i class="fas fa-hospital"></i> <%= apt.get("department") %></p>
                    <p><i class="fas fa-clock"></i> <%= apt.get("appointment_date") %> at <%= apt.get("appointment_time") %></p>
                    <% if (apt.get("symptoms") != null && !apt.get("symptoms").isEmpty()) { %>
                    <p><i class="fas fa-notes-medical"></i> <%= apt.get("symptoms") %></p>
                    <% } %>
                </div>
            </div>
            <span class="status-badge cancelled">Cancelled</span>
        </div>
        <% 
            }
        }
        
        // EMPTY STATE
        if ((upcomingApts == null || upcomingApts.isEmpty()) && 
            (completedApts == null || completedApts.isEmpty()) &&
            (cancelledApts == null || cancelledApts.isEmpty())) {
        %>
        <div class="empty-state">
            <i class="fas fa-calendar"></i>
            <p>No appointments found</p>
        </div>
        <% } %>
    </div>
</div>

            <!-- Prescriptions Page -->
           <!-- My Prescriptions Page -->
<div id="prescriptionsPage" class="page">
    <div class="page-header">
        
    </div>

    <div class="filter-tabs">
        <button class="tab-btn active" onclick="filterPrescriptions('active')">Active</button>
        <button class="tab-btn" onclick="filterPrescriptions('history')">History</button>
    </div>

    <div class="prescriptions-container" id="prescriptionsContainer">
        <% 
        if (activeRx != null) {
            for (Map<String, String> rx : activeRx) {
        %>
        <div class="prescription-card" data-status="active">
            <div class="prescription-header">
                <div class="prescription-info">
                    <h4><i class="fas fa-pills"></i> <%= rx.get("medicine_name") %></h4>
                    <p><i class="fas fa-user-md"></i> Prescribed by Dr. <%= rx.get("doctor_name") %></p>
                    <p><i class="fas fa-calendar"></i> <%= rx.get("prescribed_date") %></p>
                </div>
                <div class="prescription-actions">
                    <button class="btn btn-sm btn-primary" onclick="downloadPrescription('<%= rx.get("id") %>', '<%= rx.get("medicine_name") %>')">
                        <i class="fas fa-download"></i> Download
                    </button>
                </div>
            </div>
            
            <div class="medicines-list">
                <div class="medicine-item">
                    <div style="width: 100%;">
                        <div class="medicine-dosage" style="margin-bottom: 8px;">
                            <strong>Dosage:</strong> <%= rx.get("dosage") %>
                        </div>
                        <% if (rx.get("frequency") != null && !rx.get("frequency").isEmpty()) { %>
                        <div class="medicine-dosage" style="margin-bottom: 8px;">
                            <strong>Frequency:</strong> <%= rx.get("frequency") %>
                        </div>
                        <% } %>
                        <% if (rx.get("duration") != null && !rx.get("duration").isEmpty()) { %>
                        <div class="medicine-dosage">
                            <strong>Duration:</strong> <%= rx.get("duration") %>
                        </div>
                        <% } %>
                    </div>
                </div>
            </div>
            
            <% if (rx.get("instructions") != null && !rx.get("instructions").isEmpty()) { %>
            <div class="prescription-notes">
                <strong><i class="fas fa-info-circle"></i> Instructions:</strong>
                <p style="margin-top: 5px;"><%= rx.get("instructions") %></p>
            </div>
            <% } %>
        </div>
        <% 
            }
        }
        
        if (completedRx != null) {
            for (Map<String, String> rx : completedRx) {
        %>
        <div class="prescription-card" data-status="history" style="display: none;">
            <div class="prescription-header">
                <div class="prescription-info">
                    <h4><i class="fas fa-pills"></i> <%= rx.get("medicine_name") %></h4>
                    <p><i class="fas fa-user-md"></i> Prescribed by Dr. <%= rx.get("doctor_name") %></p>
                    <p><i class="fas fa-calendar"></i> <%= rx.get("prescribed_date") %></p>
                    <span class="status-badge expired">Expired</span>
                </div>
                <div class="prescription-actions">
                    <button class="btn btn-sm btn-primary" onclick="downloadPrescription('<%= rx.get("id") %>', '<%= rx.get("medicine_name") %>')">
                        <i class="fas fa-download"></i> Download
                    </button>
                </div>
            </div>
            
            <div class="medicines-list">
                <div class="medicine-item">
                    <div class="medicine-dosage">
                        <strong>Dosage:</strong> <%= rx.get("dosage") %>
                    </div>
                </div>
            </div>
        </div>
        <% 
            }
        }
        
        if ((activeRx == null || activeRx.isEmpty()) && 
            (completedRx == null || completedRx.isEmpty())) {
        %>
        <div class="empty-state">
            <i class="fas fa-prescription"></i>
            <p>No prescriptions found</p>
        </div>
        <% } %>
    </div>
</div>
            <!-- Medical Records Page -->
          <!-- Medical Records Page -->
          
<div id="medicalRecordsPage" class="page">
    <div class="page-header">
        <h2>Medical Records</h2>
    </div>

    <div class="medical-info-grid">
        <div class="info-card">
            <h4><i class="fas fa-tint"></i> Blood Group</h4>
            <p class="info-value"><%= bloodGroup != null ? bloodGroup : "N/A" %></p>
        </div>
        <div class="info-card">
            <h4><i class="fas fa-birthday-cake"></i> Age</h4>
            <p class="info-value">
                <% 
                if (dob != null && !dob.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date birthDate = sdf.parse(dob);
                        java.util.Calendar birthCal = java.util.Calendar.getInstance();
                        birthCal.setTime(birthDate);
                        java.util.Calendar today = java.util.Calendar.getInstance();
                        int age = today.get(java.util.Calendar.YEAR) - birthCal.get(java.util.Calendar.YEAR);
                        if (today.get(java.util.Calendar.MONTH) < birthCal.get(java.util.Calendar.MONTH)) {
                            age--;
                        }
                        out.print(age);
                    } catch(Exception e) {
                        out.print("N/A");
                    }
                } else {
                    out.print("N/A");
                }
                %>
            </p>
        </div>
        <div class="info-card">
            <h4><i class="fas fa-venus-mars"></i> Gender</h4>
            <p class="info-value">
                <%= gender != null ? gender.substring(0,1).toUpperCase() + gender.substring(1) : "N/A" %>
            </p>
        </div>
        <div class="info-card">
            <h4><i class="fas fa-file-medical"></i> Records</h4>
            <p class="info-value"><%= totalAppointments %></p>
        </div>
    </div>

    <div class="card">
        <div class="card-header">
            <h3><i class="fas fa-history"></i> Medical History</h3>
        </div>
        <div class="card-body">
            <% if (completedApts != null && !completedApts.isEmpty()) { %>
            <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse;">
                    <thead>
                        <tr style="background: #f9fafb; text-align: left;">
                            <th style="padding: 12px; border-bottom: 2px solid #e5e7eb; font-weight: 600;">Date</th>
                            <th style="padding: 12px; border-bottom: 2px solid #e5e7eb; font-weight: 600;">Doctor</th>
                            <th style="padding: 12px; border-bottom: 2px solid #e5e7eb; font-weight: 600;">Department</th>
                            <th style="padding: 12px; border-bottom: 2px solid #e5e7eb; font-weight: 600;">Purpose</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Map<String, String> apt : completedApts) { %>
                        <tr style="transition: background 0.2s;" onmouseover="this.style.background='#f9fafb'" onmouseout="this.style.background='white'">
                            <td style="padding: 12px; border-bottom: 1px solid #e5e7eb;"><%= apt.get("appointment_date") %></td>
                            <td style="padding: 12px; border-bottom: 1px solid #e5e7eb;"><%= apt.get("doctor_name") %></td>
                            <td style="padding: 12px; border-bottom: 1px solid #e5e7eb;"><%= apt.get("department") %></td>
                            <td style="padding: 12px; border-bottom: 1px solid #e5e7eb;">
                                <%= apt.get("symptoms") != null && !apt.get("symptoms").isEmpty() ? apt.get("symptoms") : "Regular Checkup" %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% } else { %>
            <div class="empty-state">
                <i class="fas fa-history"></i>
                <p>No medical history available</p>
            </div>
            <% } %>
        </div>
    </div>
</div>

  
<%
    // Get user profile data from servlet
    Map<String, String> userProfile = (Map<String, String>) request.getAttribute("userProfile");
    
    // Extract values with proper null handling
    String profilePhone = "";
    String profileDateOfBirth = "";
    String profileGender = "";
    String profileBloodGroup = "";
    String profileAddress = "";
    
    if (userProfile != null) {
        profilePhone = userProfile.get("phone") != null && !userProfile.get("phone").isEmpty() ? userProfile.get("phone") : "";
        profileDateOfBirth = userProfile.get("date_of_birth") != null && !userProfile.get("date_of_birth").isEmpty() ? userProfile.get("date_of_birth") : "";
        profileGender = userProfile.get("gender") != null && !userProfile.get("gender").isEmpty() ? userProfile.get("gender") : "";
        profileBloodGroup = userProfile.get("blood_group") != null && !userProfile.get("blood_group").isEmpty() ? userProfile.get("blood_group") : "";
        profileAddress = userProfile.get("address") != null && !userProfile.get("address").isEmpty() ? userProfile.get("address") : "";
    }
%>

<!-- My Profile Page -->
<div id="profilePage" class="page">
    <div class="page-header">
        <h2>My Profile</h2>
    </div>

    <div class="profile-container">
        <div class="profile-avatar-section">
            <div class="profile-avatar"><%= initials %></div>
            <h3 style="margin-top: 15px; color: #111827;"><%= fullname %></h3>
            <p style="color: #6b7280; margin-top: 8px; font-size: 14px;">Patient ID: <%= userId %></p>
            <p style="color: #9ca3af; margin-top: 5px; font-size: 13px;"><%= email %></p>
        </div>

        <div class="profile-content">
            <form action="PatientServlet" method="POST">
                <div class="card">
                    <div class="card-header">
                        <h3><i class="fas fa-user"></i> Personal Information</h3>
                    </div>
                    <div class="card-body">
                        <div class="form-row">
                            <div class="form-group">
                                <label><i class="fas fa-user"></i> Full Name</label>
                                <input type="text" value="<%= fullname %>" disabled style="background-color: #f3f4f6; cursor: not-allowed;">
                            </div>
                            <div class="form-group">
                                <label><i class="fas fa-envelope"></i> Email</label>
                                <input type="email" value="<%= email %>" disabled style="background-color: #f3f4f6; cursor: not-allowed;">
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label><i class="fas fa-phone"></i> Phone Number</label>
                                <input type="tel" name="phone" value="<%= profilePhone %>" placeholder="Enter your phone number" required>
                            </div>
                            <div class="form-group">
                                <label><i class="fas fa-birthday-cake"></i> Date of Birth</label>
                                <input type="date" name="dateOfBirth" value="<%= profileDateOfBirth %>" required>
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label><i class="fas fa-venus-mars"></i> Gender</label>
                                <select name="gender" required style="padding: 12px; border: 1px solid #d1d5db; border-radius: 8px; font-size: 14px; width: 100%;">
                                    <option value="">Select Gender</option>
                                    <option value="male" <%= "male".equalsIgnoreCase(profileGender) ? "selected" : "" %>>Male</option>
                                    <option value="female" <%= "female".equalsIgnoreCase(profileGender) ? "selected" : "" %>>Female</option>
                                    <option value="other" <%= "other".equalsIgnoreCase(profileGender) ? "selected" : "" %>>Other</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label><i class="fas fa-tint"></i> Blood Group</label>
                                <select name="bloodGroup" style="padding: 12px; border: 1px solid #d1d5db; border-radius: 8px; font-size: 14px; width: 100%;">
                                    <option value="">Select Blood Group</option>
                                    <option value="A+" <%= "A+".equals(profileBloodGroup) ? "selected" : "" %>>A+</option>
                                    <option value="A-" <%= "A-".equals(profileBloodGroup) ? "selected" : "" %>>A-</option>
                                    <option value="B+" <%= "B+".equals(profileBloodGroup) ? "selected" : "" %>>B+</option>
                                    <option value="B-" <%= "B-".equals(profileBloodGroup) ? "selected" : "" %>>B-</option>
                                    <option value="O+" <%= "O+".equals(profileBloodGroup) ? "selected" : "" %>>O+</option>
                                    <option value="O-" <%= "O-".equals(profileBloodGroup) ? "selected" : "" %>>O-</option>
                                    <option value="AB+" <%= "AB+".equals(profileBloodGroup) ? "selected" : "" %>>AB+</option>
                                    <option value="AB-" <%= "AB-".equals(profileBloodGroup) ? "selected" : "" %>>AB-</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label><i class="fas fa-map-marker-alt"></i> Address</label>
                            <textarea name="address" rows="3" placeholder="Enter your complete address" style="padding: 12px; border: 1px solid #d1d5db; border-radius: 8px; font-size: 14px; width: 100%; resize: vertical;"><%= profileAddress %></textarea>
                        </div>

                        <!-- SAVE BUTTON - LEFT CORNER -->
                        <div style="margin-top: 25px; text-align: left;">
                            <button type="submit" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 14px 40px; border: none; border-radius: 10px; font-size: 15px; font-weight: 600; cursor: pointer; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4); transition: all 0.3s ease;">
                                <i class="fas fa-save"></i> Save Changes
                            </button>
                        </div>
                    </div>
                </div>
            </form>

            <div class="card">
                <div class="card-header">
                    <h3><i class="fas fa-chart-line"></i> Health Summary</h3>
                </div>
                <div class="card-body">
                    <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px;">
                        <div style="padding: 15px; background: #f0f9ff; border-radius: 8px; border-left: 4px solid #3b82f6;">
                            <p style="color: #6b7280; font-size: 13px; margin-bottom: 5px;">Total Appointments</p>
                            <h3 style="color: #3b82f6; font-size: 28px; font-weight: 700;"><%= totalAppointments %></h3>
                        </div>
                        <div style="padding: 15px; background: #f0fdf4; border-radius: 8px; border-left: 4px solid #10b981;">
                            <p style="color: #6b7280; font-size: 13px; margin-bottom: 5px;">Active Prescriptions</p>
                            <h3 style="color: #10b981; font-size: 28px; font-weight: 700;"><%= activeRxCount %></h3>
                        </div>
                        <div style="padding: 15px; background: #fef3c7; border-radius: 8px; border-left: 4px solid #f59e0b;">
                            <p style="color: #6b7280; font-size: 13px; margin-bottom: 5px;">Upcoming Visits</p>
                            <h3 style="color: #f59e0b; font-size: 28px; font-weight: 700;"><%= upcomingCount %></h3>
                        </div>
                        <div style="padding: 15px; background: #fce7f3; border-radius: 8px; border-left: 4px solid #ec4899;">
                            <p style="color: #6b7280; font-size: 13px; margin-bottom: 5px;">Completed Visits</p>
                            <h3 style="color: #ec4899; font-size: 28px; font-weight: 700;"><%= completedCount %></h3>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="js/patient.js"></script>
</body>
</html>