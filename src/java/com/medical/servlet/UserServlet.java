package com.medical.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Gson gson = new Gson();
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check authorization
        if (!isAuthorized(request, response)) return;
        
        String action = request.getParameter("action");
        
        if ("get".equals(action)) {
            getUserData(request, response);
        } else if ("getData".equals(action)) {
            getUserDataJSON(request, response);
        } else if ("delete".equals(action)) {
            deleteUser(request, response);
        } else if ("resetPassword".equals(action)) {
            resetPassword(request, response);
        } else {
            response.sendRedirect("AdminServlet?error=invalid_action");
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check authorization
        if (!isAuthorized(request, response)) return;
        
        String action = request.getParameter("action");
        
        System.out.println("========== USER POST REQUEST ==========");
        System.out.println("Action: " + action);
        
        if ("add".equals(action)) {
            addUser(request, response);
        } else if ("update".equals(action)) {
            updateUser(request, response);
        } else {
            response.sendRedirect("AdminServlet?error=invalid_action");
        }
    }
    
    // ========================================================================
    // AUTHORIZATION
    // ========================================================================
    
    private boolean isAuthorized(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("Login.jsp");
            return false;
        }
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            response.sendRedirect("Login.jsp?error=unauthorized");
            return false;
        }
        return true;
    }
    
    // ========================================================================
    // GET USER DATA (FOR EDIT)
    // ========================================================================
    
    private void getUserData(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect("AdminServlet?error=user_id_required#users");
                return;
            }
            
            int id = Integer.parseInt(idParam);
            conn = DBConnection.getConnection();
            
            String sql = "SELECT id, fullname, email, phone, role, status FROM users WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Set user data as request attributes for the JSP
                request.setAttribute("editUserId", rs.getInt("id"));
                request.setAttribute("editUserName", rs.getString("fullname"));
                request.setAttribute("editUserEmail", rs.getString("email"));
                request.setAttribute("editUserPhone", rs.getString("phone"));
                request.setAttribute("editUserRole", rs.getString("role"));
                request.setAttribute("editUserStatus", rs.getString("status"));
                request.setAttribute("editMode", true);
                
                // Forward back to admin page
                request.getRequestDispatcher("AdminServlet").forward(request, response);
            } else {
                response.sendRedirect("AdminServlet?error=user_not_found#users");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect("AdminServlet?error=invalid_user_id#users");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=database_error#users");
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    // ========================================================================
    // GET USER DATA JSON (FOR AJAX EDIT)
    // ========================================================================
    
    private void getUserDataJSON(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "User ID is required");
                out.print(gson.toJson(result));
                out.flush();
                return;
            }
            
            int id = Integer.parseInt(idParam);
            conn = DBConnection.getConnection();
            
            String sql = "SELECT id, fullname, email, phone, role, status FROM users WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                result.put("success", true);
                result.put("id", rs.getInt("id"));
                result.put("fullname", rs.getString("fullname"));
                result.put("email", rs.getString("email"));
                result.put("phone", rs.getString("phone"));
                result.put("role", rs.getString("role"));
                result.put("status", rs.getString("status"));
            } else {
                result.put("success", false);
                result.put("message", "User not found");
            }
            
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid user ID");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Error fetching user data");
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        out.print(gson.toJson(result));
        out.flush();
    }
    
    // ========================================================================
    // ADD USER
    // ========================================================================
    
    private void addUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet checkRs = null;
        
        System.out.println("========== ADD USER ==========");
        
        try {
            String fullname = request.getParameter("fullname");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String password = request.getParameter("password");
            String role = request.getParameter("role");
            String status = request.getParameter("status");
            
            System.out.println("Fullname: " + fullname);
            System.out.println("Email: " + email);
            System.out.println("Role: " + role);
            
            // Validation
            if (fullname == null || fullname.trim().length() < 2) {
                response.sendRedirect("AdminServlet?error=invalid_name#users");
                return;
            }
            if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
                response.sendRedirect("AdminServlet?error=invalid_email#users");
                return;
            }
            if (phone != null && !phone.trim().isEmpty()) {
                if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
                    response.sendRedirect("AdminServlet?error=invalid_phone#users");
                    return;
                }
            }
            if (password == null || password.trim().length() < 8) {
                response.sendRedirect("AdminServlet?error=password_too_short#users");
                return;
            }
            if (role == null || role.trim().isEmpty()) {
                response.sendRedirect("AdminServlet?error=role_required#users");
                return;
            }
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if email already exists
            String checkSql = "SELECT id FROM users WHERE email = ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email.trim().toLowerCase());
            checkRs = checkStmt.executeQuery();
            
            if (checkRs.next()) {
                response.sendRedirect("AdminServlet?error=email_exists#users");
                conn.rollback();
                return;
            }
            checkRs.close();
            checkStmt.close();
            
            // Hash password
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
            
            // Insert user
            String insertSql = "INSERT INTO users (fullname, email, phone, password, role, status) " +
                              "VALUES (?, ?, ?, ?, ?, ?)";
            
            insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, fullname.trim());
            insertStmt.setString(2, email.trim().toLowerCase());
            insertStmt.setString(3, phone != null ? phone.trim() : "");
            insertStmt.setString(4, hashedPassword);
            insertStmt.setString(5, role.trim().toLowerCase());
            insertStmt.setString(6, status != null ? status.trim() : "active");
            
            int rows = insertStmt.executeUpdate();
            System.out.println("Rows inserted: " + rows);
            
            if (rows > 0) {
                conn.commit();
                System.out.println("User added successfully");
                response.sendRedirect("AdminServlet?success=added#users");
            } else {
                conn.rollback();
                response.sendRedirect("AdminServlet?error=insert_failed#users");
            }
            
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=database_error#users");
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=unexpected_error#users");
        } finally {
            closeResources(conn, checkStmt, checkRs);
            if (insertStmt != null) {
                try { insertStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    // ========================================================================
    // UPDATE USER
    // ========================================================================
    
    private void updateUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet checkRs = null;
        
        System.out.println("========== UPDATE USER ==========");
        
        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendRedirect("AdminServlet?error=user_id_required#users");
                return;
            }
            
            int id = Integer.parseInt(idStr);
            String fullname = request.getParameter("fullname");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String password = request.getParameter("password");
            String role = request.getParameter("role");
            String status = request.getParameter("status");
            
            System.out.println("Updating user ID: " + id);
            System.out.println("New email: " + email);
            
            // Validation
            if (fullname == null || fullname.trim().length() < 2) {
                response.sendRedirect("AdminServlet?error=invalid_name#users");
                return;
            }
            if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
                response.sendRedirect("AdminServlet?error=invalid_email#users");
                return;
            }
            if (phone != null && !phone.trim().isEmpty()) {
                if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
                    response.sendRedirect("AdminServlet?error=invalid_phone#users");
                    return;
                }
            }
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if email exists for another user
            String checkSql = "SELECT id FROM users WHERE email = ? AND id != ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email.trim().toLowerCase());
            checkStmt.setInt(2, id);
            checkRs = checkStmt.executeQuery();
            
            if (checkRs.next()) {
                response.sendRedirect("AdminServlet?error=email_exists#users");
                conn.rollback();
                return;
            }
            checkRs.close();
            checkStmt.close();
            
            // Update user (with or without password)
            String updateSql;
            if (password != null && !password.trim().isEmpty()) {
                if (password.length() < 8) {
                    response.sendRedirect("AdminServlet?error=password_too_short#users");
                    conn.rollback();
                    return;
                }
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
                updateSql = "UPDATE users SET fullname = ?, email = ?, phone = ?, " +
                           "password = ?, role = ?, status = ? WHERE id = ?";
                updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, fullname.trim());
                updateStmt.setString(2, email.trim().toLowerCase());
                updateStmt.setString(3, phone != null ? phone.trim() : "");
                updateStmt.setString(4, hashedPassword);
                updateStmt.setString(5, role != null ? role.trim().toLowerCase() : "patient");
                updateStmt.setString(6, status != null ? status.trim() : "active");
                updateStmt.setInt(7, id);
            } else {
                updateSql = "UPDATE users SET fullname = ?, email = ?, phone = ?, " +
                           "role = ?, status = ? WHERE id = ?";
                updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, fullname.trim());
                updateStmt.setString(2, email.trim().toLowerCase());
                updateStmt.setString(3, phone != null ? phone.trim() : "");
                updateStmt.setString(4, role != null ? role.trim().toLowerCase() : "patient");
                updateStmt.setString(5, status != null ? status.trim() : "active");
                updateStmt.setInt(6, id);
            }
            
            int rows = updateStmt.executeUpdate();
            System.out.println("Rows updated: " + rows);
            
            if (rows > 0) {
                conn.commit();
                System.out.println("User updated successfully");
                response.sendRedirect("AdminServlet?success=updated#users");
            } else {
                conn.rollback();
                response.sendRedirect("AdminServlet?error=update_failed#users");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect("AdminServlet?error=invalid_user_id#users");
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=database_error#users");
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=unexpected_error#users");
        } finally {
            closeResources(null, checkStmt, checkRs);
            if (updateStmt != null) {
                try { updateStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    // ========================================================================
    // DELETE USER
    // ========================================================================
    
    private void deleteUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        System.out.println("========== DELETE USER ==========");
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect("AdminServlet?error=user_id_required#users");
                return;
            }
            
            int id = Integer.parseInt(idParam);
            System.out.println("Deleting user ID: " + id);
            
            // Prevent deleting current admin user
            HttpSession session = request.getSession(false);
            Integer currentUserId = (Integer) session.getAttribute("userId");
            if (currentUserId != null && currentUserId == id) {
                response.sendRedirect("AdminServlet?error=cannot_delete_self#users");
                return;
            }
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            String deleteSql = "DELETE FROM users WHERE id = ?";
            stmt = conn.prepareStatement(deleteSql);
            stmt.setInt(1, id);
            
            int rows = stmt.executeUpdate();
            System.out.println("Rows deleted: " + rows);
            
            if (rows > 0) {
                conn.commit();
                System.out.println("User deleted successfully");
                response.sendRedirect("AdminServlet?success=deleted#users");
            } else {
                conn.rollback();
                response.sendRedirect("AdminServlet?error=user_not_found#users");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect("AdminServlet?error=invalid_user_id#users");
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=database_error#users");
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=unexpected_error#users");
        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    // ========================================================================
    // RESET PASSWORD
    // ========================================================================
    
    private void resetPassword(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        System.out.println("========== RESET PASSWORD ==========");
        
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect("AdminServlet?error=user_id_required#users");
                return;
            }
            
            int id = Integer.parseInt(idParam);
            System.out.println("Resetting password for user ID: " + id);
            
            // Generate default password
            String defaultPassword = "Welcome@123";
            String hashedPassword = BCrypt.hashpw(defaultPassword, BCrypt.gensalt(12));
            
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            String updateSql = "UPDATE users SET password = ? WHERE id = ?";
            stmt = conn.prepareStatement(updateSql);
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, id);
            
            int rows = stmt.executeUpdate();
            System.out.println("Rows updated: " + rows);
            
            if (rows > 0) {
                conn.commit();
                System.out.println("Password reset successfully");
                response.sendRedirect("AdminServlet?success=password_reset#users");
            } else {
                conn.rollback();
                response.sendRedirect("AdminServlet?error=user_not_found#users");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect("AdminServlet?error=invalid_user_id#users");
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=database_error#users");
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendRedirect("AdminServlet?error=unexpected_error#users");
        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    // ========================================================================
    // HELPER METHODS
    // ========================================================================
    
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try {
            if (conn != null) { conn.setAutoCommit(true); conn.close(); }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}