<%@ page session="true" %>
<%
    String userName = (String) session.getAttribute("name");
    String userEmail = (String) session.getAttribute("email");

    if (userName == null || userEmail == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
%>


    String firstLetter = headerName.substring(0,1).toUpperCase();
%>

<style>
.top-header{
    display:flex;
    justify-content:space-between;
    align-items:center;
    background:#fff;
    padding:20px 30px;
    border-radius:12px;
    margin-bottom:20px;
}
.profile-box{
    display:flex;
    align-items:center;
    gap:12px;
}
.avatar{
    width:45px;
    height:45px;
    border-radius:50%;
    background:#6a5acd;
    color:#fff;
    font-weight:bold;
    display:flex;
    align-items:center;
    justify-content:center;
    cursor:pointer;
}
</style>

<div class="top-header">
    <h2>Patient Dashboard</h2>

    <div class="profile-box">
        <div class="avatar"><%= firstLetter %></div>
        <div>
            <b><%= headerName %></b><br>
            <small><%= headerEmail %></small>
        </div>
    </div>
</div>
