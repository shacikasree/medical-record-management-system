<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%
    // Check if admin is logged in
    if (session.getAttribute("userId") == null || !"admin".equals(session.getAttribute("role"))) {
        response.sendRedirect("Login.jsp");
        return;
    }
    
    String doctorId = request.getParameter("id");
    
    // Fetch doctor details from database
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    
    String name = "", email = "", phone = "", specialty = "", qualification = "", experience = "", patients = "";
    
    try {
        conn = DBConnection.getConnection();
        String sql = "SELECT u.*, " +
                    "(SELECT COUNT(*) FROM appointments WHERE doctor_id = u.id) as patient_count " +
                    "FROM users u WHERE u.id = ? AND u.role = 'doctor'";
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, Integer.parseInt(doctorId));
        rs = stmt.executeQuery();
        
        if (rs.next()) {
            name = rs.getString("fullname");
            email = rs.getString("email");
            phone = rs.getString("phone");
            specialty = rs.getString("specialty");
            qualification = rs.getString("qualification");
            experience = String.valueOf(rs.getInt("experience"));
            patients = String.valueOf(rs.getInt("patient_count"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (conn != null) conn.close();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Doctor - Hospital Management System</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            backdrop-filter: blur(5px);
            z-index: 999;
        }

        .modal-content {
            background: white;
            border-radius: 20px;
            padding: 40px;
            max-width: 600px;
            width: 100%;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            position: relative;
            z-index: 1000;
            animation: slideDown 0.3s ease;
        }

        @keyframes slideDown {
            from {
                opacity: 0;
                transform: translateY(-50px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 2px solid #e2e8f0;
        }

        .modal-header h2 {
            font-size: 24px;
            color: #1e293b;
            font-weight: 600;
        }

        .close-btn {
            background: none;
            border: none;
            font-size: 24px;
            color: #94a3b8;
            cursor: pointer;
            width: 40px;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            transition: all 0.3s ease;
        }

        .close-btn:hover {
            background: #f1f5f9;
            color: #ef4444;
            transform: rotate(90deg);
        }

        .doctor-details {
            padding: 20px 0;
        }

        .detail-title {
            font-size: 18px;
            font-weight: 600;
            color: #1e293b;
            margin-bottom: 20px;
        }

        .detail-row {
            display: flex;
            justify-content: space-between;
            padding: 15px 0;
            border-bottom: 1px solid #f1f5f9;
        }

        .detail-row:last-child {
            border-bottom: none;
        }

        .detail-label {
            font-weight: 500;
            color: #64748b;
            font-size: 14px;
        }

        .detail-value {
            font-weight: 600;
            color: #1e293b;
            font-size: 15px;
            text-align: right;
        }

        .btn-ok {
            background: #4f46e5;
            color: white;
            border: none;
            padding: 14px 40px;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
            margin-top: 20px;
            width: 100%;
        }

        .btn-ok:hover {
            background: #4338ca;
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(79, 70, 229, 0.4);
        }

        .btn-ok:active {
            transform: translateY(0);
        }

        @media (max-width: 600px) {
            .modal-content {
                padding: 30px 20px;
            }

            .detail-row {
                flex-direction: column;
                gap: 5px;
            }

            .detail-value {
                text-align: left;
            }
        }
    </style>
</head>
<body>
    <div class="modal-overlay"></div>
    
    <div class="modal-content">
        <div class="modal-header">
            <h2>Doctor Details</h2>
            <button class="close-btn" onclick="goBack()">
                <i class="fas fa-times"></i>
            </button>
        </div>

        <div class="doctor-details">
            <div class="detail-row">
                <span class="detail-label">Name:</span>
                <span class="detail-value"><%= name %></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Specialty:</span>
                <span class="detail-value"><%= specialty %></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Email:</span>
                <span class="detail-value"><%= email %></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Phone:</span>
                <span class="detail-value"><%= phone %></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Qualification:</span>
                <span class="detail-value"><%= qualification %></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Experience:</span>
                <span class="detail-value"><%= experience %> years</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Patients:</span>
                <span class="detail-value"><%= patients %></span>
            </div>
        </div>

        <button class="btn-ok" onclick="goBack()">
            OK
        </button>
    </div>

    <script>
        function goBack() {
            window.location.href = 'AdminServlet';
        }
    </script>
</body>
</html>