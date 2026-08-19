<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%
    // Retrieve all data from servlet
    Map<String, Integer> stats = (Map<String, Integer>) request.getAttribute("stats");
    List<Map<String, String>> doctors = (List<Map<String, String>>) request.getAttribute("doctors");
    List<Map<String, String>> patients = (List<Map<String, String>>) request.getAttribute("patients");
    List<Map<String, String>> appointments = (List<Map<String, String>>) request.getAttribute("appointments");
    List<Map<String, String>> todayAppointments = (List<Map<String, String>>) request.getAttribute("todayAppointments");
    List<Map<String, String>> prescriptions = (List<Map<String, String>>) request.getAttribute("prescriptions");
    List<Map<String, String>> activities = (List<Map<String, String>>) request.getAttribute("activities");
    List<Map<String, String>> departmentStats = (List<Map<String, String>>) request.getAttribute("departmentStats");
    List<Map<String, String>> doctorSchedules = (List<Map<String, String>>) request.getAttribute("doctorSchedules");
    
    String adminName = (String) session.getAttribute("fullname");
    
    // Initialize collections if null
    if (stats == null) stats = new HashMap<>();
    if (doctors == null) doctors = new ArrayList<>();
    if (patients == null) patients = new ArrayList<>();
    if (appointments == null) appointments = new ArrayList<>();
    if (todayAppointments == null) todayAppointments = new ArrayList<>();
    if (prescriptions == null) prescriptions = new ArrayList<>();
    if (activities == null) activities = new ArrayList<>();
    if (departmentStats == null) departmentStats = new ArrayList<>();
    if (doctorSchedules == null) doctorSchedules = new ArrayList<>();
    
    // Extract unique specialties for filter dropdown
    Set<String> specialties = new LinkedHashSet<>();
    for (Map<String, String> doc : doctors) {
        if (doc.get("specialty") != null) specialties.add(doc.get("specialty"));
    }
    
    // Extract unique departments for filter dropdown
    Set<String> deptNames = new LinkedHashSet<>();
    for (Map<String, String> dept : departmentStats) {
        if (dept.get("name") != null) deptNames.add(dept.get("name"));
    }
%>

<%
// Get data from servlet
Map<String, String> hospitalSettings = (Map<String, String>) request.getAttribute("hospitalSettings");
List<Map<String, String>> holidays = (List<Map<String, String>>) request.getAttribute("holidays");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Hospital Management System</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <link rel="stylesheet" href="admin-dashboard.css">
    
</head>
<style>


    </style>
<body>
    <!-- Sidebar -->
    <div class="sidebar" id="sidebar">
        <div class="sidebar-header">
            <i class="fas fa-hospital"></i>
            <h2>HMS Admin</h2>
        </div>
        <ul class="sidebar-menu">
            <li class="menu-item active" data-section="dashboard">
                <i class="fas fa-chart-line"></i>
                <span>Dashboard</span>
            </li>
            <li class="menu-item" data-section="doctors">
                <i class="fas fa-user-md"></i>
                <span>Manage Doctors</span>
            </li>
            <li class="menu-item" data-section="patients">
                <i class="fas fa-users"></i>
                <span>Manage Patients</span>
            </li>
            <li class="menu-item" data-section="appointments">
                <i class="fas fa-calendar-check"></i>
                <span>Appointments</span>
            </li>
            <li class="menu-item" data-section="departments">
                <i class="fas fa-building"></i>
                <span>Departments</span>
            </li>
            <li class="menu-item" data-section="prescriptions">
                <i class="fas fa-prescription"></i>
                <span>Prescriptions</span>
            </li>
            <li class="menu-item" data-section="reports">
                <i class="fas fa-file-alt"></i>
                <span>Reports</span>
            </li>
            <li class="menu-item" data-section="users">
                <i class="fas fa-user-cog"></i>
                <span>User Management</span>
            </li>
            <li class="menu-item" data-section="settings">
                <i class="fas fa-cog"></i>
                <span>Settings</span>
            </li>
        </ul>
    </div>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Top Navigation -->
        <nav class="top-nav">
            <div class="nav-left">
                <button class="menu-toggle" id="menuToggle">
                    <i class="fas fa-bars"></i>
                </button>
                <div class="search-box">
                    <i class="fas fa-search"></i>
                    <input type="text" placeholder="Search...">
                </div>
            </div>
            <div class="nav-right">
                <div class="notifications">
                    <i class="fas fa-bell"></i>
                    <span class="badge">5</span>
                </div>
                <div class="user-profile">
                    <img src="https://ui-avatars.com/api/?name=<%= adminName != null ? adminName.replace(" ", "+") : "Admin" %>&background=4e73df&color=fff" alt="Admin">
                    <span><%= adminName != null ? adminName : "Admin" %></span>
                    <i class="fas fa-chevron-down"></i>
                </div>
            </div>
        </nav>

        <!-- Dashboard Section -->
        <section id="dashboard-section" class="content-section active">
            <div class="page-header">
                <div>
                    <h1>Dashboard Overview</h1>
                    <p>Welcome to Hospital Management System</p>
                </div>
            </div>

            <!-- Stats Cards -->
            <div class="stats-grid">
                <div class="stat-card blue">
                    <div class="stat-icon">
                        <i class="fas fa-user-md"></i>
                    </div>
                    <div class="stat-details">
                        <h3 id="totalDoctors"><%= stats.getOrDefault("totalDoctors", 0) %></h3>
                        <p>Total Doctors</p>
                    </div>
                </div>
                <div class="stat-card green">
                    <div class="stat-icon">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="stat-details">
                        <h3 id="totalPatients"><%= stats.getOrDefault("totalPatients", 0) %></h3>
                        <p>Total Patients</p>
                    </div>
                </div>
                <div class="stat-card orange">
                    <div class="stat-icon">
                        <i class="fas fa-calendar-check"></i>
                    </div>
                    <div class="stat-details">
                        <h3 id="totalAppointments"><%= stats.getOrDefault("totalAppointments", 0) %></h3>
                        <p>Total Appointments</p>
                    </div>
                </div>
                <div class="stat-card purple">
                    <div class="stat-icon">
                        <i class="fas fa-clock"></i>
                    </div>
                    <div class="stat-details">
                        <h3 id="todayAppointments"><%= stats.getOrDefault("todayAppointments", 0) %></h3>
                        <p>Today's Appointments</p>
                    </div>
                </div>
            </div>

           <!-- Charts Row - COMMENTED OUT (NO SAMPLE DATA) -->
<!-- 
<div class="charts-row">
    <div class="chart-card">
        <h3>Appointments Overview</h3>
        <canvas id="appointmentsChart"></canvas>
    </div>
    <div class="chart-card">
        <h3>Department Distribution</h3>
        <canvas id="departmentChart"></canvas>
    </div>
</div>
-->

            <!-- Recent Activities -->
            <div class="recent-activities">
                <h3>Recent Activities</h3>
                <div class="activity-list" id="activityList">
                    <% if (activities != null && !activities.isEmpty()) { 
                        for (Map<String, String> activity : activities) { %>
                            <div class="activity-item">
                                <p><%= activity.get("text") %></p>
                                <span class="activity-time"><%= activity.get("time") %></span>
                            </div>
                    <% } 
                    } else { %>
                        <div class="activity-item">
                            <p>No recent activities</p>
                        </div>
                    <% } %>
                </div>
            </div>
        </section>


<section id="doctors-section" class="content-section">
    <div class="page-header">
        <div>
            <h1>Manage Doctors</h1>
            <p>Add, edit, and manage doctor accounts</p>
        </div>
        <button class="btn btn-primary" onclick="openAddDoctorModal()">
            <i class="fas fa-plus"></i> Add Doctor
        </button>
    </div>
    
    <div class="filters-bar">
        <input type="text" id="doctorSearch" placeholder="Search doctors..." class="search-input" onkeyup="filterDoctors()">
        <select id="specialtyFilter" class="filter-select" onchange="filterDoctors()">
            <option value="">All Specialties</option>
            <% 
            List<Map<String, String>> doctorsList = (List<Map<String, String>>) request.getAttribute("doctors");
            Map<Integer, Map<String, Object>> availabilityMap = 
                (Map<Integer, Map<String, Object>>) request.getAttribute("doctorAvailability");
            
            Set<String> specialtySet = new HashSet<>();
            if (doctorsList != null) {
                for (Map<String, String> doc : doctorsList) {
                    String spec = doc.get("specialty");
                    if (spec != null) {
                        specialtySet.add(spec);
                    }
                }
            }
            for (String spec : specialtySet) { 
            %>
                <option value="<%= spec %>"><%= spec %></option>
            <% } %>
        </select>
    </div>
    
    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>NAME</th>
                    <th>SPECIALTY</th>
                    <th>EMAIL</th>
                    <th>PHONE</th>
                    <th>AVAILABILITY</th>
                    <th>PATIENTS</th>
                    <th>STATUS</th>
                    <th>ACTIONS</th>
                </tr>
            </thead>
            <tbody id="doctorsTableBody">
                <% 
                if (doctorsList != null && !doctorsList.isEmpty()) {
                    for (Map<String, String> doc : doctorsList) { 
                        int docId = Integer.parseInt(doc.get("id"));
                        
                        Map<String, Object> availData = availabilityMap != null ? 
                            availabilityMap.get(docId) : null;
                        
                        List<Map<String, String>> schedule = null;
                        List<Map<String, String>> leaves = null;
                        boolean isEmergency = false;
                        
                        if (availData != null) {
                            schedule = (List<Map<String, String>>) availData.get("weeklySchedule");
                            leaves = (List<Map<String, String>>) availData.get("unavailableDates");
                            Boolean emergencyObj = (Boolean) availData.get("emergencyAvailable");
                            isEmergency = emergencyObj != null ? emergencyObj.booleanValue() : false;
                        }
                %>
                    <tr class="doctor-row" 
                        data-name="<%= doc.get("name") != null ? doc.get("name").toLowerCase() : "" %>" 
                        data-specialty="<%= doc.get("specialty") != null ? doc.get("specialty") : "" %>">
                        <td><%= doc.get("id") %></td>
                        <td><%= doc.get("name") %></td>
                        <td><%= doc.get("specialty") %></td>
                        <td><%= doc.get("email") %></td>
                        <td><%= doc.get("phone") %></td>
                        <td>
                            <div class="availability-cell">
                                <!-- Daily Schedule -->
                                <div class="avail-section">
                                    <strong class="avail-heading">📅 Daily Schedule:</strong>
                                    <% 
                                    if (schedule != null && !schedule.isEmpty()) {
                                        boolean hasActive = false;
                                        for (Map<String, String> day : schedule) {
                                            if ("true".equals(day.get("isActive"))) {
                                                hasActive = true;
                                                String dayName = day.get("dayName");
                                                String startTime = day.get("startTime");
                                                String endTime = day.get("endTime");
                                    %>
                                        <div class="day-schedule">
                                            <span class="day-name"><%= dayName.substring(0, 3) %></span>
                                            <span class="day-time"><%= startTime %> - <%= endTime %></span>
                                        </div>
                                    <% 
                                            }
                                        }
                                        if (!hasActive) {
                                    %>
                                        <span class="no-schedule">Not Set</span>
                                    <% 
                                        }
                                    } else { 
                                    %>
                                        <span class="no-schedule">Not Set</span>
                                    <% } %>
                                </div>
                                
                                <!-- Emergency Availability -->
                                <div class="avail-section">
                                    <strong class="avail-heading">🚑 Emergency:</strong>
                                    <% if (isEmergency) { %>
                                        <span class="emergency-yes">✓ Available</span>
                                    <% } else { %>
                                        <span class="emergency-no">✗ Not Available</span>
                                    <% } %>
                                </div>
                                
                                <!-- Unavailable Dates -->
                                <div class="avail-section">
                                    <strong class="avail-heading">🗓 Unavailable:</strong>
                                    <% 
                                    if (leaves != null && !leaves.isEmpty()) {
                                        for (Map<String, String> leave : leaves) {
                                            String leaveDate = leave.get("date");
                                            String reason = leave.get("reason");
                                    %>
                                        <div class="leave-item">
                                            <span class="leave-date"><%= leaveDate %></span>
                                            <% if (reason != null && !reason.isEmpty()) { %>
                                                <span class="leave-reason">(<%= reason %>)</span>
                                            <% } %>
                                        </div>
                                    <% 
                                        }
                                    } else { 
                                    %>
                                        <span class="no-leaves">None</span>
                                    <% } %>
                                </div>
                            </div>
                        </td>
                        <td><%= doc.get("patientCount") %></td>
                        <td>
                            <% 
                            String docStatus = doc.get("status");
                            String statusClass = "active".equals(docStatus) ? "status-active" : "status-inactive"; 
                            %>
                            <span class="status-badge <%= statusClass %>"><%= docStatus %></span>
                        </td>
                        <td>
                            <div class="action-btns">
                                <button class="action-btn edit" onclick="editDoctor(<%= doc.get("id") %>)" title="Edit">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="action-btn view" onclick="viewDoctor(<%= doc.get("id") %>)" title="View">
                                    <i class="fas fa-eye"></i>
                                </button>
                                <button class="action-btn delete" onclick="deleteDoctor(<%= doc.get("id") %>)" title="Delete">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                <% 
                    }
                } else { 
                %>
                    <tr>
                        <td colspan="9" style="text-align: center; padding: 40px; color: #94a3b8;">
                            <i class="fas fa-user-md" style="font-size: 48px; display: block; margin-bottom: 10px; opacity: 0.3;"></i>
                            No doctors found
                        </td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</section>
        <!-- Patients Section -->
     
<section id="patients-section" class="content-section">
    <div class="page-header">
        <div>
            <h1>Manage Patients</h1>
            <p>View and manage patient records</p>
        </div>
        <button class="btn btn-primary" onclick="openAddPatientModal()">
    <i class="fas fa-plus"></i> Add Patient
</button>
    </div>
    
    <div class="filters-bar">
        <input type="text" id="patientSearch" placeholder="Search patients..." class="search-input" onkeyup="filterPatients()">
        <select id="patientStatusFilter" class="filter-select" onchange="filterPatients()">
            <option value="">All Status</option>
            <option value="active">Active</option>
            <option value="blocked">Blocked</option>
        </select>
    </div>
    
    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Age</th>
                    <th>Gender</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody id="patientsTableBody">
                <% if (patients != null && !patients.isEmpty()) {
                    for (Map<String, String> patient : patients) { %>
                        <tr class="patient-row" data-name="<%= patient.get("name").toLowerCase() %>" data-status="<%= patient.get("status") %>">
                            <td><%= patient.get("id") %></td>
                            <td><%= patient.get("name") %></td>
                            <td><%= patient.get("age") %></td>
                            <td><%= patient.get("gender") %></td>
                            <td><%= patient.get("email") %></td>
                            <td><%= patient.get("phone") %></td>
                            <td>
                                <% String status = patient.get("status");
                                   String badgeClass = "status-" + status; %>
                                <span class="status-badge <%= badgeClass %>"><%= status %></span>
                            </td>
                            <td>
                                <div class="action-btns">
                                    <button class="action-btn view" onclick="viewPatient(<%= patient.get("id") %>)" title="View">
                                        <i class="fas fa-eye"></i>
                                    </button>
                                    <button class="action-btn edit" onclick="editPatient(<%= patient.get("id") %>)" title="Edit">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <button class="action-btn delete" onclick="togglePatientStatus(<%= patient.get("id") %>)" title="Block/Unblock">
                                        <i class="fas fa-ban"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                <% }
                } else { %>
                    <tr>
                        <td colspan="8" style="text-align: center; padding: 20px;">No patients found</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</section>
       <!-- Appointments Section -->
<section id="appointments-section" class="content-section">
    <div class="page-header">
        <div>
            <h1>Manage Appointments</h1>
            <p>View and manage all appointments</p>
        </div>
    </div>

    <div class="filters-bar">
        <input type="date" id="appointmentDate" class="filter-input" onchange="filterAppointments()">
        <select id="appointmentStatus" class="filter-select" onchange="filterAppointments()">
            <option value="">All Status</option>
            <option value="scheduled">Scheduled</option>
            <option value="completed">Completed</option>
            <option value="cancelled">Cancelled</option>
        </select>
        <select id="appointmentDept" class="filter-select" onchange="filterAppointments()">
            <option value="">All Departments</option>
            <% for (String dept : deptNames) { %>
                <option value="<%= dept %>"><%= dept %></option>
            <% } %>
        </select>
    </div>

    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>PATIENT</th>
                    <th>DOCTOR</th>
                    <th>DEPARTMENT</th>
                    <th>DATE</th>
                    <th>TIME</th>
                    <th>STATUS</th>
                    <th>ACTIONS</th>
                </tr>
            </thead>
            <tbody id="appointmentsTableBody">
                <% if (appointments != null && !appointments.isEmpty()) {
                    for (Map<String, String> apt : appointments) { %>
                        <tr class="appointment-row" 
                            data-date="<%= apt.get("date") %>" 
                            data-status="<%= apt.get("status") %>" 
                            data-dept="<%= apt.get("department") %>">
                            <td><%= apt.get("id") %></td>
                            <td><%= apt.get("patientName") %></td>
                            <td><%= apt.get("doctorName") %></td>
                            <td><%= apt.get("department") %></td>
                            <td><%= apt.get("date") %></td>
                            <td><%= apt.get("time") %></td>
                            <td>
                                <% String status = apt.get("status");
                                   String badgeClass = "scheduled".equals(status) ? "status-scheduled" : 
                                                      "completed".equals(status) ? "status-completed" : 
                                                      "cancelled".equals(status) ? "status-cancelled" : "status-scheduled"; %>
                                <span class="status-badge <%= badgeClass %>"><%= status %></span>
                            </td>
                            <td>
                                <div class="action-btns">
                                    <button class="action-btn view" onclick="viewAppointment(<%= apt.get("id") %>)" title="View Details">
                                        <i class="fas fa-eye"></i>
                                    </button>
                                    <% if (!"cancelled".equals(status) && !"completed".equals(status)) { %>
                                        <button class="action-btn delete" onclick="cancelAppointment(<%= apt.get("id") %>)" title="Cancel">
                                            <i class="fas fa-times"></i>
                                        </button>
                                    <% } %>
                                </div>
                            </td>
                        </tr>
                <% }
                } else { %>
                    <tr>
                        <td colspan="8" style="text-align: center; padding: 20px;">No appointments found</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</section>

   
<!-- Departments Section -->
<section id="departments-section" class="content-section">
    <div class="page-header">
        <div>
            <h1>Manage Departments</h1>
            <p>Add and manage hospital departments</p>
        </div>
        <button class="btn btn-primary" onclick="openDepartmentModal()">
            <i class="fas fa-plus"></i> Add Department
        </button>
    </div>

    <div class="departments-grid" id="departmentsGrid">
        <% if (departmentStats != null && !departmentStats.isEmpty()) {
            for (Map<String, String> dept : departmentStats) { 
                // Get values with null checks
                String deptName = dept.get("name") != null ? dept.get("name") : "Unnamed Department";
                String deptDesc = dept.get("description") != null ? dept.get("description") : "";
                String headName = dept.get("headName") != null && !dept.get("headName").isEmpty() ? dept.get("headName") : "Not Assigned";
                String doctorCount = dept.get("doctorCount") != null ? dept.get("doctorCount") : "0";
                String patientCount = dept.get("patientCount") != null ? dept.get("patientCount") : "0";
                String deptId = dept.get("id") != null ? dept.get("id") : "0";
        %>
                <div class="department-card">
                    <h3 class="dept-title"><%= deptName %></h3>
                    <% if (!deptDesc.isEmpty()) { %>
                        <p class="dept-description"><%= deptDesc %></p>
                    <% } %>
                    
                    <p class="dept-head">
                        <strong>Head:</strong> <%= headName %>
                    </p>
                    
                    <div class="dept-stats-row">
                        <div class="dept-stat-item">
                            <h2 class="stat-number"><%= doctorCount %></h2>
                            <p class="stat-label">Doctors</p>
                        </div>
                        <div class="dept-stat-item">
                            <h2 class="stat-number"><%= patientCount %></h2>
                            <p class="stat-label">Patients</p>
                        </div>
                    </div>
                    
                    <div class="dept-actions">
                        <button class="btn-edit" onclick="editDepartment(<%= deptId %>)">Edit</button>
                        <button class="btn-delete" onclick="deleteDepartment(<%= deptId %>)">Delete</button>
                    </div>
                </div>
        <% }
        } else { %>
            <div class="no-data-message">
                <i class="fas fa-building"></i>
                <p>No departments found</p>
            </div>
        <% } %>
    </div>
</section>

       <!-- Prescriptions Section -->
<section id="prescriptions-section" class="content-section">
    <div class="page-header">
        <div>
            <h1>Prescription Management</h1>
            <p>View and manage all prescriptions</p>
        </div>
    </div>

    <div class="filters-bar">
        <input type="text" id="prescriptionSearch" placeholder="Search by patient or doctor..." class="search-input">
        <input type="date" id="prescriptionDate" class="filter-input">
    </div>

    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>PATIENT</th>
                    <th>DOCTOR</th>
                    <th>DATE</th>
                    <th>MEDICINES</th>
                    <th>ACTIONS</th>
                </tr>
            </thead>
            <tbody id="prescriptionsTableBody">
                <% if (prescriptions != null && !prescriptions.isEmpty()) {
                    for (Map<String, String> rx : prescriptions) { 
                        String patientName = rx.get("patientName") != null ? rx.get("patientName") : "";
                        String doctorName = rx.get("doctorName") != null ? rx.get("doctorName") : "";
                        String date = rx.get("date") != null ? rx.get("date") : "";
                        String medicine = rx.get("medicine") != null ? rx.get("medicine") : "";
                        String id = rx.get("id") != null ? rx.get("id") : "0";
                %>
                    <tr class="prescription-row" 
                        data-patient="<%= patientName.toLowerCase() %>" 
                        data-doctor="<%= doctorName.toLowerCase() %>" 
                        data-date="<%= date %>">
                        <td><%= id %></td>
                        <td><%= patientName %></td>
                        <td><%= doctorName %></td>
                        <td><%= date %></td>
                        <td><%= medicine %></td>
                        <td>
                            <div class="action-btns">
                                <button class="action-btn view" onclick="viewPrescription(<%= id %>)" title="View Details">
                                    <i class="fas fa-eye"></i>
                                </button>
                                <button class="action-btn edit" onclick="downloadPrescription(<%= id %>)" title="Download">
                                    <i class="fas fa-download"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                <% }
                } else { %>
                    <tr>
                        <td colspan="6" style="text-align: center; padding: 20px; color: #6b7280;">No prescriptions found</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</section>

        <!-- Reports Section -->
     <!-- Reports Section -->
<section id="reports-section" class="content-section">
    <div class="page-header">
        <div>
            <h1>Generate Reports</h1>
            <p>Download various system reports</p>
        </div>
    </div>

    <div class="reports-grid">
        <div class="report-card" onclick="generateReport('system')">
            <i class="fas fa-chart-bar"></i>
            <h3>System Summary</h3>
            <p>Overall statistics report</p>
        </div>
        <div class="report-card" onclick="generateReport('doctors')">
            <i class="fas fa-user-md"></i>
            <h3>Doctors Report</h3>
            <p>Complete doctors list</p>
        </div>
        <div class="report-card" onclick="generateReport('patients')">
            <i class="fas fa-users"></i>
            <h3>Patients Report</h3>
            <p>Patient demographics</p>
        </div>
        <div class="report-card" onclick="generateReport('appointments')">
            <i class="fas fa-calendar"></i>
            <h3>Appointments Report</h3>
            <p>Appointment statistics</p>
        </div>
        <div class="report-card" onclick="generateReport('departments')">
            <i class="fas fa-building"></i>
            <h3>Department Report</h3>
            <p>Department-wise analysis</p>
        </div>
    </div>

    <div class="custom-report">
        <h3>Custom Report</h3>
        <div class="report-filters">
            <input type="date" id="reportStartDate" class="filter-input" placeholder="Start Date">
            <input type="date" id="reportEndDate" class="filter-input" placeholder="End Date">
            <select id="reportType" class="filter-select">
                <option value="appointments">Appointments</option>
                <option value="patients">Patients</option>
                <option value="doctors">Doctors</option>
                <option value="departments">Departments</option>
            </select>
            <button class="btn btn-primary" onclick="generateCustomReport()">Generate</button>
        </div>
    </div>
</section>
        <!-- Users Section -->
<section id="users-section" class="content-section">
    <div class="page-header">
        <div>
            <h1>User Management</h1>
            <p>Manage system users and permissions</p>
        </div>
        <button class="btn btn-primary" onclick="openUserModal()">
            <i class="fas fa-plus"></i> Add User
        </button>
    </div>
    
    <div class="filters-bar">
        <input type="text" id="userSearch" placeholder="Search users..." class="search-input" onkeyup="filterUsers()">
        <select id="userRoleFilter" class="filter-select" onchange="filterUsers()">
            <option value="">All Roles</option>
            <option value="admin">Admin</option>
            <option value="doctor">Doctor</option>
            <option value="patient">Patient</option>
            <option value="staff">Staff</option>
        </select>
    </div>
    
    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>NAME</th>
                    <th>EMAIL</th>
                    <th>ROLE</th>
                    <th>STATUS</th>
                    <th>LAST LOGIN</th>
                    <th>ACTIONS</th>
                </tr>
            </thead>
            <tbody id="usersTableBody">
                <% 
                List<Map<String, String>> users = (List<Map<String, String>>) request.getAttribute("users");
                if (users != null && !users.isEmpty()) {
                    for (Map<String, String> user : users) { 
                %>
                    <tr class="user-row" 
                        data-role="<%= user.get("role") %>" 
                        data-name="<%= user.get("name") %>"
                        data-email="<%= user.get("email") %>">
                        <td><%= user.get("id") %></td>
                        <td><%= user.get("name") %></td>
                        <td><%= user.get("email") %></td>
                        <td><span class="status-badge status-<%= user.get("role") %>"><%= user.get("role") %></span></td>
                        <td><span class="status-badge status-<%= user.get("status") %>"><%= user.get("status") %></span></td>
                        <td><%= user.get("lastLogin") %></td>
                        <td>
                            <div class="action-btns">
                                <button class="action-btn edit" onclick="editUser(<%= user.get("id") %>)" title="Edit">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="action-btn view" onclick="resetPassword(<%= user.get("id") %>)" title="Reset Password">
                                    <i class="fas fa-key"></i>
                                </button>
                                <button class="action-btn delete" onclick="deleteUser(<%= user.get("id") %>)" title="Delete">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                <% 
                    }
                } else { 
                %>
                    <tr>
                        <td colspan="7" style="text-align: center; padding: 20px;">No users found</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</section>

     
<!-- Settings Section -->
<section id="settings-section" class="content-section">
    <div class="page-header">
        <div>
            <h1>System Settings</h1>
            <p>Configure hospital settings</p>
        </div>
    </div>
    
    <div class="settings-container">
        <!-- Hospital Information Card -->
        <div class="settings-card">
            <h3><i class="fas fa-hospital"></i> Hospital Information</h3>
            <form id="hospitalInfoForm" action="SettingsServlet?action=updateHospitalInfo" method="post">
                <div class="form-group">
                    <label>Hospital Name</label>
                    <input type="text" name="hospitalName" class="form-control" 
                           value="<%= hospitalSettings != null ? hospitalSettings.get("hospitalName") : "City Hospital" %>" required>
                </div>
                <div class="form-group">
                    <label>Address</label>
                    <textarea name="hospitalAddress" class="form-control" rows="3" required><%= hospitalSettings != null ? hospitalSettings.get("address") : "123 Medical Street, Health City" %></textarea>
                </div>
                <div class="form-group">
                    <label>Phone</label>
                    <input type="tel" name="hospitalPhone" class="form-control" 
                           value="<%= hospitalSettings != null ? hospitalSettings.get("phone") : "+1234567890" %>" required>
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="hospitalEmail" class="form-control" 
                           value="<%= hospitalSettings != null ? hospitalSettings.get("email") : "info@cityhospital.com" %>" required>
                </div>
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> Save Changes
                </button>
            </form>
        </div>

        <!-- Working Hours Card -->
        <div class="settings-card">
            <h3><i class="fas fa-clock"></i> Working Hours</h3>
            <form id="workingHoursForm" action="SettingsServlet?action=updateWorkingHours" method="post">
                <div class="form-group">
                    <label>Opening Time</label>
                    <input type="time" name="openingTime" class="form-control" 
                           value="<%= hospitalSettings != null ? hospitalSettings.get("openingTime") : "08:00" %>" required>
                </div>
                <div class="form-group">
                    <label>Closing Time</label>
                    <input type="time" name="closingTime" class="form-control" 
                           value="<%= hospitalSettings != null ? hospitalSettings.get("closingTime") : "20:00" %>" required>
                </div>
                <div class="form-group">
                    <label>Appointment Duration (minutes)</label>
                    <input type="number" name="appointmentDuration" class="form-control" 
                           value="<%= hospitalSettings != null ? hospitalSettings.get("appointmentDuration") : "30" %>" 
                           min="15" max="120" required>
                </div>
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> Save Changes
                </button>
            </form>
        </div>

        <!-- Holidays Management Card -->
        <div class="settings-card">
            <h3><i class="fas fa-calendar-alt"></i> Holidays Management</h3>
            <div class="holidays-list" id="holidaysList">
                <% if (holidays != null && !holidays.isEmpty()) {
                    for (Map<String, String> holiday : holidays) { %>
                        <div class="holiday-item">
                            <div>
                                <strong><%= holiday.get("name") %></strong>
                                <p style="margin: 0; color: #858796; font-size: 0.9rem;"><%= holiday.get("date") %></p>
                            </div>
                            <button class="btn-sm btn-danger" onclick="deleteHoliday(<%= holiday.get("id") %>)">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                <% }
                } else { %>
                    <p style="text-align: center; color: #999; padding: 20px;">No holidays found</p>
                <% } %>
            </div>
            <button class="btn btn-secondary" onclick="addHoliday()">
                <i class="fas fa-plus"></i> Add Holiday
            </button>
        </div>
    </div>
</section>
 <!-- Doctor Modal -->
<div id="doctorModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h2>Add/Edit Doctor</h2>
            <span class="close" onclick="closeDoctorModal()">&times;</span>
        </div>
        <form id="doctorForm" onsubmit="saveDoctorForm(event)">
            <div class="form-row">
                <div class="form-group">
                    <label for="doctorName">Full Name</label>
                    <input type="text" id="doctorName" name="fullname" required>
                </div>
                <div class="form-group">
                    <label for="doctorEmail">Email</label>
                    <input type="email" id="doctorEmail" name="email" required>
                </div>
            </div>
            
            <div class="form-row">
                <div class="form-group">
                    <label for="doctorPhone">Phone</label>
                    <input type="tel" id="doctorPhone" name="phone" required>
                </div>
                <div class="form-group">
                    <label for="doctorSpecialty">Specialty</label>
                    <select id="doctorSpecialty" name="specialty" required>
                        <option value="">Select Specialty</option>
                        <option value="Cardiology">Cardiology</option>
                        <option value="Neurology">Neurology</option>
                        <option value="Orthopedics">Orthopedics</option>
                        <option value="Pediatrics">Pediatrics</option>
                        <option value="Dermatology">Dermatology</option>
                        <option value="Psychiatry">Psychiatry</option>
                        <option value="General Medicine">General Medicine</option>
                        <option value="Surgery">Surgery</option>
                    </select>
                </div>
            </div>
            
            <div class="form-row">
                <div class="form-group">
                    <label for="doctorQualification">Qualification</label>
                    <input type="text" id="doctorQualification" name="qualification" required>
                </div>
                <div class="form-group">
                    <label for="doctorExperience">Experience (Years)</label>
                    <input type="number" id="doctorExperience" name="experience" required min="0" value="0">
                </div>
            </div>
            
            <!-- Hidden fields -->
            <input type="hidden" id="doctorPassword" name="password" value="doctor123">
            <input type="hidden" id="doctorDepartment" name="department" value="">
            
            <div class="modal-footer">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> Save Doctor
                </button>
            </div>
        </form>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<div id="deleteConfirmModal" class="modal">
    <div class="modal-content modal-small">
        <div class="modal-header">
            <h2>Confirm Delete</h2>
            <span class="close" onclick="closeDeleteModal()">&times;</span>
        </div>
        <div class="modal-body">
            <p>Are you sure you want to delete this doctor? This action cannot be undone.</p>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary" onclick="closeDeleteModal()">Cancel</button>
            <button type="button" class="btn btn-danger" onclick="confirmDeleteDoctor()">
                <i class="fas fa-trash"></i> Delete
            </button>
        </div>
    </div>
</div>
<!-- Doctor Modal -->
<div id="doctorModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h2>Add/Edit Doctor</h2>
            <span class="close" onclick="closeDoctorModal()">&times;</span>
        </div>
        <form id="doctorForm" onsubmit="saveDoctorForm(event)">
            <div class="form-row">
                <div class="form-group">
                    <label for="doctorName">Full Name</label>
                    <input type="text" id="doctorName" name="fullname" required>
                </div>
                <div class="form-group">
                    <label for="doctorEmail">Email</label>
                    <input type="email" id="doctorEmail" name="email" required>
                </div>
            </div>
            
            <div class="form-row">
                <div class="form-group">
                    <label for="doctorPhone">Phone</label>
                    <input type="tel" id="doctorPhone" name="phone" required>
                </div>
                <div class="form-group">
                    <label for="doctorSpecialty">Specialty</label>
                    <select id="doctorSpecialty" name="specialty" required>
                        <option value="">Select Specialty</option>
                        <option value="Cardiology">Cardiology</option>
                        <option value="Neurology">Neurology</option>
                        <option value="Orthopedics">Orthopedics</option>
                        <option value="Pediatrics">Pediatrics</option>
                        <option value="Dermatology">Dermatology</option>
                        <option value="Psychiatry">Psychiatry</option>
                        <option value="General Medicine">General Medicine</option>
                        <option value="Surgery">Surgery</option>
                    </select>
                </div>
            </div>
            
            <div class="form-row">
                <div class="form-group">
                    <label for="doctorQualification">Qualification</label>
                    <input type="text" id="doctorQualification" name="qualification" required>
                </div>
                <div class="form-group">
                    <label for="doctorExperience">Experience (Years)</label>
                    <input type="number" id="doctorExperience" name="experience" required min="0" value="0">
                </div>
            </div>
            
            <!-- Hidden fields -->
            <input type="hidden" id="doctorPassword" name="password" value="doctor123">
            <input type="hidden" id="doctorDepartment" name="department" value="">
            
            <div class="modal-footer">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> Save Doctor
                </button>
            </div>
        </form>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<div id="deleteConfirmModal" class="modal">
    <div class="modal-content modal-small">
        <div class="modal-header">
            <h2>Confirm Delete</h2>
            <span class="close" onclick="closeDeleteModal()">&times;</span>
        </div>
        <div class="modal-body">
            <p>Are you sure you want to delete this doctor? This action cannot be undone.</p>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary" onclick="closeDeleteModal()">Cancel</button>
            <button type="button" class="btn btn-danger" onclick="confirmDeleteDoctor()">
                <i class="fas fa-trash"></i> Delete
            </button>
        </div>
    </div>
</div>

<!-- Patient Modal -->

<div id="patientModal" class="modal" style="display:none;">
    <div class="modal-content">
        <div class="modal-header">
            <h2 id="patientModalTitle">Add New Patient</h2>
            <span class="close" onclick="closePatientModal()">&times;</span>
        </div>
        <form id="patientForm">
            <input type="hidden" id="patientId" name="patientId" value="">
            
            <div class="form-row">
                <div class="form-group">
                    <label>Full Name *</label>
                    <input type="text" id="patientName" name="name" required>
                </div>
                <div class="form-group">
                    <label>Age *</label>
                    <input type="number" id="patientAge" name="age" min="1" max="120" required>
                </div>
            </div>
            
            <div class="form-row">
                <div class="form-group">
                    <label>Gender *</label>
                    <select id="patientGender" name="gender" required>
                        <option value="">Select Gender</option>
                        <option value="Male">Male</option>
                        <option value="Female">Female</option>
                        <option value="Other">Other</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Phone *</label>
                    <input type="tel" id="patientPhone" name="phone" required>
                </div>
            </div>
            
            <div class="form-group">
                <label>Email *</label>
                <input type="email" id="patientEmail" name="email" required>
            </div>
            
            <div class="form-group">
                <label>Address</label>
                <textarea id="patientAddress" name="address" rows="2"></textarea>
            </div>
            
            <div class="form-group">
                <label>Blood Group</label>
                <select id="patientBloodGroup" name="bloodGroup">
                    <option value="">Select Blood Group</option>
                    <option value="A+">A+</option>
                    <option value="A-">A-</option>
                    <option value="B+">B+</option>
                    <option value="B-">B-</option>
                    <option value="AB+">AB+</option>
                    <option value="AB-">AB-</option>
                    <option value="O+">O+</option>
                    <option value="O-">O-</option>
                </select>
            </div>
            
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closePatientModal()">Cancel</button>
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> Add Patient
                </button>
            </div>
        </form>
    </div>
</div>
   <!-- Department Modal -->
<div id="departmentModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeDepartmentModal()">&times;</span>
        <h2 id="departmentModalTitle">Add New Department</h2>
        <form id="departmentForm" method="post" action="saveDepartment">
            <input type="hidden" id="departmentId" name="departmentId">
            
            <div class="form-group">
                <label>Department Name *</label>
                <input type="text" id="departmentName" name="departmentName" required placeholder="e.g., Cardiology">
            </div>
            
            <div class="form-group">
                <label>Description</label>
                <textarea id="departmentDescription" name="departmentDescription" rows="3" placeholder="e.g., Heart and cardiovascular care"></textarea>
            </div>
            
            <div class="form-group">
                <label>Department Head</label>
                <select id="departmentHead" name="departmentHead">
                    <option value="">Select Doctor (Optional)</option>
                    <% if (doctors != null && !doctors.isEmpty()) {
                        for (Map<String, String> doctor : doctors) { %>
                            <option value="<%= doctor.get("id") %>"><%= doctor.get("name") %></option>
                    <% }
                    } %>
                </select>
            </div>
            
            <button type="submit" class="btn btn-primary">Save Department</button>
        </form>
    </div>
</div>
              <!-- Prescription Details Modal -->
<div id="prescriptionModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closePrescriptionModal()">&times;</span>
        <h2>Prescription Details</h2>
        <div id="prescriptionDetails">
            <div class="detail-row">
                <label>Prescription ID:</label>
                <span id="rxDetailId"></span>
            </div>
            <div class="detail-row">
                <label>Patient Name:</label>
                <span id="rxDetailPatient"></span>
            </div>
            <div class="detail-row">
                <label>Doctor Name:</label>
                <span id="rxDetailDoctor"></span>
            </div>
            <div class="detail-row">
                <label>Date:</label>
                <span id="rxDetailDate"></span>
            </div>
            <div class="detail-row">
                <label>Medicines:</label>
                <span id="rxDetailMedicines"></span>
            </div>
        </div>
        <div style="margin-top: 20px; display: flex; gap: 10px;">
            <button class="btn btn-primary" onclick="downloadPrescriptionFromModal()">
                <i class="fas fa-download"></i> Download
            </button>
            <button class="btn btn-secondary" onclick="closePrescriptionModal()">Close</button>
        </div>
    </div>
</div>

 
<!-- User Modal -->
<div id="userModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeUserModal()">&times;</span>
        <h2 id="modalTitle">Add New User</h2>
        <form id="userForm" action="UserServlet?action=add" method="post">
            <input type="hidden" id="userId" name="id">
            
            <div class="form-row">
                <div class="form-group">
                    <label>Full Name *</label>
                    <input type="text" id="userName" name="fullname" required>
                </div>
                <div class="form-group">
                    <label>Email *</label>
                    <input type="email" id="userEmail" name="email" required>
                </div>
            </div>
            
            <div class="form-row">
                <div class="form-group">
                    <label>Phone</label>
                    <input type="text" id="userPhone" name="phone">
                </div>
                <div class="form-group">
                    <label>Password *</label>
                    <input type="password" id="userPassword" name="password" required>
                </div>
            </div>
            
            <div class="form-row">
                <div class="form-group">
                    <label>Role *</label>
                    <select id="userRole" name="role" required>
                        <option value="">Select Role</option>
                        <option value="admin">Admin</option>
                        <option value="doctor">Doctor</option>
                        <option value="patient">Patient</option>
                        <option value="staff">Staff</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Status</label>
                    <select id="userStatus" name="status">
                        <option value="active">Active</option>
                        <option value="inactive">Inactive</option>
                    </select>
                </div>
            </div>
            
            <div style="margin-top: 20px; display: flex; gap: 10px; justify-content: flex-end;">
                <button type="button" class="btn btn-secondary" onclick="closeUserModal()">Cancel</button>
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> Save User
                </button>
            </div>
        </form>
    </div>
</div>
<!-- Add Holiday Modal -->
<div id="holidayModal" class="modal">
    <div class="modal-content" style="max-width: 400px;">
        <span class="close" onclick="closeHolidayModal()">&times;</span>
        <h2>Add Holiday</h2>
        <form id="holidayForm" action="SettingsServlet?action=addHoliday" method="post">
            <div class="form-group">
                <label>Holiday Name *</label>
                <input type="text" name="holidayName" class="form-control" required placeholder="e.g., Christmas">
            </div>
            <div class="form-group">
                <label>Date *</label>
                <input type="date" name="holidayDate" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Description</label>
                <textarea name="holidayDescription" class="form-control" rows="2" placeholder="Optional description"></textarea>
            </div>
            <div style="margin-top: 20px; display: flex; gap: 10px; justify-content: flex-end;">
                <button type="button" class="btn btn-secondary" onclick="closeHolidayModal()">Cancel</button>
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> Add Holiday
                </button>
            </div>
        </form>
    </div>
</div>

    <script src="js/admin.js"></script>
</body>
</html>