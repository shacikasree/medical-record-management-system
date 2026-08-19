<%-- 
    Document   : prescription
    Created on : 2 Jan 2026, 7:40:59 pm
    Author     : Lenovo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Prescription Database Test</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            padding: 30px;
            background: #f5f5f5;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .success {
            background: #d4edda;
            color: #155724;
            padding: 15px;
            margin: 10px 0;
            border-left: 4px solid #28a745;
            border-radius: 4px;
        }
        .error {
            background: #f8d7da;
            color: #721c24;
            padding: 15px;
            margin: 10px 0;
            border-left: 4px solid #dc3545;
            border-radius: 4px;
        }
        .info {
            background: #d1ecf1;
            color: #0c5460;
            padding: 15px;
            margin: 10px 0;
            border-left: 4px solid #17a2b8;
            border-radius: 4px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
        }
        th {
            background: #007bff;
            color: white;
        }
        pre {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            overflow-x: auto;
        }
        h2 {
            color: #333;
            border-bottom: 2px solid #007bff;
            padding-bottom: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🔍 Prescription Database Test</h1>
        <p>Testing database connection and prescription data...</p>
        <hr>
        
        <%
        String DB_URL = "jdbc:mysql://localhost:3306/medical_db";
        String DB_USER = "root";
        String DB_PASSWORD = "shacika@@2006";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            // TEST 1: Load Driver
            out.println("<h2>Test 1: MySQL Driver</h2>");
            Class.forName("com.mysql.cj.jdbc.Driver");
            out.println("<div class='success'>✅ MySQL Driver loaded successfully</div>");
            
            // TEST 2: Connect to Database
            out.println("<h2>Test 2: Database Connection</h2>");
            out.println("<div class='info'>");
            out.println("<strong>Connection Details:</strong><br>");
            out.println("URL: " + DB_URL + "<br>");
            out.println("User: " + DB_USER + "<br>");
            out.println("Password: ***" + "<br>");
            out.println("</div>");
            
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            out.println("<div class='success'>✅ Connected to database successfully</div>");
            
            // TEST 3: Check Tables
            out.println("<h2>Test 3: Tables Check</h2>");
            DatabaseMetaData meta = conn.getMetaData();
            
            String[] requiredTables = {"users", "doctors", "prescriptions"};
            boolean allTablesExist = true;
            
            for (String table : requiredTables) {
                rs = meta.getTables(null, null, table, new String[]{"TABLE"});
                if (rs.next()) {
                    out.println("<div class='success'>✅ Table '" + table + "' exists</div>");
                } else {
                    out.println("<div class='error'>❌ Table '" + table + "' NOT FOUND</div>");
                    allTablesExist = false;
                }
                rs.close();
            }
            
            if (!allTablesExist) {
                out.println("<div class='error'>");
                out.println("<strong>SOLUTION:</strong> Create missing tables using this SQL:");
                out.println("<pre>");
                out.println("CREATE TABLE prescriptions (");
                out.println("  id INT PRIMARY KEY AUTO_INCREMENT,");
                out.println("  user_id INT NOT NULL,");
                out.println("  doctor_id INT,");
                out.println("  medicine_name VARCHAR(200) NOT NULL,");
                out.println("  dosage VARCHAR(100),");
                out.println("  frequency VARCHAR(100),");
                out.println("  duration VARCHAR(100),");
                out.println("  instructions TEXT,");
                out.println("  prescribed_date DATE NOT NULL,");
                out.println("  refills_remaining INT DEFAULT 0,");
                out.println("  status VARCHAR(50) DEFAULT 'Active'");
                out.println(");");
                out.println("</pre>");
                out.println("</div>");
            }
            
            // TEST 4: Count Records
            out.println("<h2>Test 4: Data Check</h2>");
            stmt = conn.createStatement();
            
            // Count users
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
            rs.next();
            int userCount = rs.getInt("count");
            out.println("<div class='" + (userCount > 0 ? "success" : "error") + "'>");
            out.println((userCount > 0 ? "✅" : "❌") + " Users table: " + userCount + " records");
            out.println("</div>");
            rs.close();
            
            // Count doctors
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM doctors");
            rs.next();
            int doctorCount = rs.getInt("count");
            out.println("<div class='" + (doctorCount > 0 ? "success" : "error") + "'>");
            out.println((doctorCount > 0 ? "✅" : "❌") + " Doctors table: " + doctorCount + " records");
            out.println("</div>");
            rs.close();
            
            // Count prescriptions
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM prescriptions");
            rs.next();
            int prescriptionCount = rs.getInt("count");
            out.println("<div class='" + (prescriptionCount > 0 ? "success" : "error") + "'>");
            out.println((prescriptionCount > 0 ? "✅" : "❌") + " Prescriptions table: " + prescriptionCount + " records");
            out.println("</div>");
            rs.close();
            
            // TEST 5: Show Prescriptions
            if (prescriptionCount > 0) {
                out.println("<h2>Test 5: Prescription Data</h2>");
                
                String sql = "SELECT " +
                            "p.id, p.medicine_name, p.dosage, p.prescribed_date, p.status, " +
                            "u.fullname as patient_name, " +
                            "COALESCE(d.fullname, 'No Doctor') as doctor_name " +
                            "FROM prescriptions p " +
                            "JOIN users u ON p.user_id = u.id " +
                            "LEFT JOIN doctors d ON p.doctor_id = d.id " +
                            "LIMIT 10";
                
                rs = stmt.executeQuery(sql);
                
                out.println("<table>");
                out.println("<tr>");
                out.println("<th>ID</th>");
                out.println("<th>Medicine</th>");
                out.println("<th>Patient</th>");
                out.println("<th>Doctor</th>");
                out.println("<th>Date</th>");
                out.println("<th>Status</th>");
                out.println("</tr>");
                
                while (rs.next()) {
                    out.println("<tr>");
                    out.println("<td>" + rs.getInt("id") + "</td>");
                    out.println("<td>" + rs.getString("medicine_name") + "</td>");
                    out.println("<td>" + rs.getString("patient_name") + "</td>");
                    out.println("<td>" + rs.getString("doctor_name") + "</td>");
                    out.println("<td>" + rs.getString("prescribed_date") + "</td>");
                    out.println("<td>" + rs.getString("status") + "</td>");
                    out.println("</tr>");
                }
                
                out.println("</table>");
                rs.close();
                
                out.println("<div class='success'>");
                out.println("<h3>✅ ALL TESTS PASSED!</h3>");
                out.println("<p>Database is working correctly. You can try downloading prescriptions now.</p>");
                out.println("<p><strong>Test download URL:</strong><br>");
                out.println("<a href='DownloadPrescriptionServlet?id=1' target='_blank'>");
                out.println("Click here to test download prescription ID 1</a></p>");
                out.println("</div>");
                
            } else {
                out.println("<div class='error'>");
                out.println("<h3>❌ No Prescriptions Found</h3>");
                out.println("<p>Insert sample data using this SQL:</p>");
                out.println("<pre>");
                out.println("INSERT INTO prescriptions ");
                out.println("(user_id, doctor_id, medicine_name, dosage, frequency, duration, instructions, prescribed_date, refills_remaining, status)");
                out.println("VALUES ");
                out.println("(1, 1, 'Aspirin 81mg', '1 tablet', 'Once daily', '30 days', 'Take with food', CURDATE(), 3, 'Active');");
                out.println("</pre>");
                out.println("</div>");
            }
            
        } catch (ClassNotFoundException e) {
            out.println("<div class='error'>");
            out.println("<h3>❌ MySQL Driver Not Found</h3>");
            out.println("<p><strong>Error:</strong> " + e.getMessage() + "</p>");
            out.println("<p><strong>Solution:</strong></p>");
            out.println("<ol>");
            out.println("<li>Download mysql-connector-java-8.0.33.jar</li>");
            out.println("<li>Copy to: WebContent/WEB-INF/lib/</li>");
            out.println("<li>Restart server</li>");
            out.println("</ol>");
            out.println("</div>");
            
        } catch (SQLException e) {
            out.println("<div class='error'>");
            out.println("<h3>❌ Database Error</h3>");
            out.println("<p><strong>Error Message:</strong> " + e.getMessage() + "</p>");
            out.println("<p><strong>SQL State:</strong> " + e.getSQLState() + "</p>");
            out.println("<p><strong>Error Code:</strong> " + e.getErrorCode() + "</p>");
            out.println("<pre>");
            e.printStackTrace(new java.io.PrintWriter(out));
            out.println("</pre>");
            out.println("</div>");
            
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        %>
        
        <hr>
        <p style="text-align: center; color: #666;">
            <a href="patientDash.jsp">← Back to Dashboard</a>
        </p>
    </div>
</body>
</html>