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
    
    String name = "", email = "", phone = "", specialty = "", qualification = "", experience = "";
    
    try {
        conn = DBConnection.getConnection();
        String sql = "SELECT * FROM users WHERE id = ? AND role = 'doctor'";
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
    <title>Edit Doctor - Hospital Management System</title>
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
        }

        .modal-header h2 {
            font-size: 28px;
            color: #1e293b;
            font-weight: 600;
        }

        .close-btn {
            background: none;
            border: none;
            font-size: 28px;
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

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 20px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #334155;
            font-weight: 500;
            font-size: 14px;
        }

        .form-group input,
        .form-group select {
            width: 100%;
            padding: 12px 16px;
            border: 2px solid #e2e8f0;
            border-radius: 10px;
            font-size: 15px;
            transition: all 0.3s ease;
            outline: none;
        }

        .form-group input:focus,
        .form-group select:focus {
            border-color: #4f46e5;
            box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
        }

        .btn-save {
            background: #4f46e5;
            color: white;
            border: none;
            padding: 14px 32px;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 10px;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
        }

        .btn-save:hover {
            background: #4338ca;
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(79, 70, 229, 0.4);
        }

        .btn-save:active {
            transform: translateY(0);
        }

        .btn-cancel {
            background: #94a3b8;
            color: white;
            border: none;
            padding: 14px 32px;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            margin-left: 10px;
        }

        .btn-cancel:hover {
            background: #64748b;
        }

        .button-group {
            display: flex;
            gap: 10px;
            margin-top: 30px;
        }

        @media (max-width: 600px) {
            .modal-content {
                padding: 30px 20px;
            }

            .form-row {
                grid-template-columns: 1fr;
            }

            .button-group {
                flex-direction: column;
            }

            .btn-save,
            .btn-cancel {
                width: 100%;
                margin-left: 0;
            }
        }
    </style>
</head>
<body>
    <div class="modal-overlay"></div>
    
    <div class="modal-content">
        <div class="modal-header">
            <h2>Edit Doctor</h2>
            <button class="close-btn" onclick="goBack()">
                <i class="fas fa-times"></i>
            </button>
        </div>

        <form id="editDoctorForm" method="POST" action="UpdateDoctorServlet">
            <input type="hidden" name="doctorId" value="<%= doctorId %>">
            
            <div class="form-row">
                <div class="form-group">
                    <label>Full Name</label>
                    <input type="text" name="fullname" value="<%= name %>" required>
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" value="<%= email %>" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Phone</label>
                    <input type="tel" name="phone" value="<%= phone %>" required>
                </div>
                <div class="form-group">
                    <label>Specialty</label>
                    <select name="specialty" required>
                        <option value="">Select Specialty</option>
                        <option value="Cardiology" <%= "Cardiology".equals(specialty) ? "selected" : "" %>>Cardiology</option>
                        <option value="Neurology" <%= "Neurology".equals(specialty) ? "selected" : "" %>>Neurology</option>
                        <option value="Orthopedics" <%= "Orthopedics".equals(specialty) ? "selected" : "" %>>Orthopedics</option>
                        <option value="Pediatrics" <%= "Pediatrics".equals(specialty) ? "selected" : "" %>>Pediatrics</option>
                        <option value="Dermatology" <%= "Dermatology".equals(specialty) ? "selected" : "" %>>Dermatology</option>
                        <option value="Gynecology" <%= "Gynecology".equals(specialty) ? "selected" : "" %>>Gynecology</option>
                        <option value="General Medicine" <%= "General Medicine".equals(specialty) ? "selected" : "" %>>General Medicine</option>
                    </select>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Qualification</label>
                    <input type="text" name="qualification" value="<%= qualification %>" required>
                </div>
                <div class="form-group">
                    <label>Experience (Years)</label>
                    <input type="number" name="experience" value="<%= experience %>" min="0" required>
                </div>
            </div>

            <div class="button-group">
                <button type="submit" class="btn-save">
                    <i class="fas fa-save"></i>
                    Update Doctor
                </button>
                <button type="button" class="btn-cancel" onclick="goBack()">
                    Cancel
                </button>
            </div>
        </form>
    </div>

    <script>
        function goBack() {
            window.location.href = 'AdminServlet';
        }

        document.getElementById('editDoctorForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            
            fetch('UpdateDoctorServlet', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Doctor updated successfully!');
                    window.location.href = 'AdminServlet';
                } else {
                    alert('Error: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred while updating the doctor.');
            });
        });
    </script>
</body>
</html>