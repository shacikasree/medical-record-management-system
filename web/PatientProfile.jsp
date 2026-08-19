<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
    String fullname = (String) request.getAttribute("fullname");
    String email = (String) request.getAttribute("email");
    String phone = (String) request.getAttribute("phone");
    String gender = (String) request.getAttribute("gender");
    String address = (String) request.getAttribute("address");
    String bloodGroup = (String) request.getAttribute("bloodGroup");
    Object ageObj = request.getAttribute("age");
    Object dobObj = request.getAttribute("dob");

    int age = 0;
    if (ageObj != null) {
        age = (Integer) ageObj;
    }
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Patient Profile</title>

<style>
body {
    font-family: Arial, sans-serif;
    background: #f4f6f8;
}

.container {
    width: 60%;
    margin: 40px auto;
    background: white;
    padding: 30px;
    border-radius: 8px;
}

h2 {
    text-align: center;
    color: #2c3e50;
}

.form-group {
    margin-bottom: 15px;
}

label {
    font-weight: bold;
}

input, textarea {
    width: 100%;
    padding: 8px;
    margin-top: 5px;
}

input[disabled], textarea[disabled] {
    background: #eee;
    border: 1px solid #ccc;
}

.btn-group {
    text-align: center;
    margin-top: 20px;
}

button {
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

.edit {
    background: #3498db;
    color: white;
}

.save {
    background: #2ecc71;
    color: white;
    display: none;
}

.cancel {
    background: #e74c3c;
    color: white;
    display: none;
}
</style>

<script>
function enableEdit() {
    document.querySelectorAll("input, textarea").forEach(el => {
        el.disabled = false;
    });

    document.getElementById("editBtn").style.display = "none";
    document.getElementById("saveBtn").style.display = "inline-block";
    document.getElementById("cancelBtn").style.display = "inline-block";
}

function cancelEdit() {
    window.location.reload();
}
</script>

</head>
<body>

<div class="container">
    <h2>Patient Profile</h2>

    <form action="UpdateProfileServlet" method="post">

        <div class="form-group">
            <label>Full Name</label>
            <input type="text" name="fullname" value="<%= fullname %>" disabled>
        </div>

        <div class="form-group">
            <label>Email</label>
            <input type="email" name="email" value="<%= email %>" disabled>
        </div>

        <div class="form-group">
            <label>Phone</label>
            <input type="text" name="phone" value="<%= phone %>" disabled>
        </div>

        <div class="form-group">
            <label>Date of Birth</label>
            <input type="date" name="dob" value="<%= dobObj %>" disabled>
        </div>

        <div class="form-group">
            <label>Age</label>
            <input type="text" value="<%= age %>" disabled>
        </div>

        <div class="form-group">
            <label>Gender</label>
            <input type="text" name="gender" value="<%= gender %>" disabled>
        </div>

        <div class="form-group">
            <label>Blood Group</label>
            <input type="text" name="bloodGroup" value="<%= bloodGroup %>" disabled>
        </div>

        <div class="form-group">
            <label>Address</label>
            <textarea name="address" disabled><%= address %></textarea>
        </div>

        <div class="btn-group">
            <button type="button" class="edit" id="editBtn" onclick="enableEdit()">Edit</button>
            <button type="submit" class="save" id="saveBtn">Save</button>
            <button type="button" class="cancel" id="cancelBtn" onclick="cancelEdit()">Cancel</button>
        </div>

    </form>
</div>

</body>
</html>
