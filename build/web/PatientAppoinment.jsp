<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    HttpSession session1 = request.getSession(false);
    Integer userId = null;
    
    if (session1 == null || session1.getAttribute("userId") == null) {
        response.sendRedirect("Login.jsp?error=loginRequired");
        return;
    }
    
    userId = (Integer) session1.getAttribute("userId");
    String fullname = (String) session1.getAttribute("fullname");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Book Appointment | MediCare+</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; font-family: 'Inter', sans-serif; }
    body { background: #f8fafc; min-height: 100vh; }
    
    .top-nav { background: white; box-shadow: 0 2px 8px rgba(0,0,0,0.08); padding: 20px 0; position: sticky; top: 0; z-index: 100; }
    .nav-content { max-width: 1200px; margin: 0 auto; padding: 0 30px; display: flex; justify-content: space-between; align-items: center; }
    .logo { display: flex; align-items: center; gap: 12px; font-size: 24px; font-weight: 700; color: #1e293b; }
    .logo-icon { background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); width: 40px; height: 40px; border-radius: 10px; display: flex; align-items: center; justify-content: center; color: white; font-size: 20px; }
    .back-link { color: #64748b; text-decoration: none; font-weight: 500; display: flex; align-items: center; gap: 8px; transition: color 0.3s; }
    .back-link:hover { color: #3b82f6; }
    
    .main-container { max-width: 1200px; margin: 40px auto; padding: 0 30px; display: grid; grid-template-columns: 1fr 1.5fr; gap: 30px; }
    
    .info-panel { background: white; border-radius: 20px; padding: 35px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); height: fit-content; position: sticky; top: 100px; }
    .info-panel h2 { font-size: 28px; color: #1e293b; margin-bottom: 15px; }
    .info-panel p { color: #64748b; line-height: 1.7; margin-bottom: 30px; }
    .feature-list { list-style: none; }
    .feature-item { display: flex; align-items: flex-start; gap: 15px; margin-bottom: 20px; }
    .feature-icon { background: linear-gradient(135deg, #dbeafe 0%, #ede9fe 100%); width: 45px; height: 45px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 20px; flex-shrink: 0; }
    .feature-text h3 { font-size: 16px; color: #1e293b; margin-bottom: 5px; }
    .feature-text p { font-size: 14px; color: #64748b; margin: 0; }
    
    .form-panel { background: white; border-radius: 20px; padding: 40px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
    .form-header { margin-bottom: 35px; }
    .form-header h1 { font-size: 32px; color: #1e293b; margin-bottom: 10px; }
    .form-header .subtitle { color: #64748b; font-size: 16px; }
    .progress-bar { height: 6px; background: #e2e8f0; border-radius: 10px; margin-top: 20px; overflow: hidden; }
    .progress-fill { height: 100%; background: linear-gradient(90deg, #3b82f6 0%, #8b5cf6 100%); width: 33%; transition: width 0.3s ease; border-radius: 10px; }
    
    .form-step { display: none; }
    .form-step.active { display: block; animation: fadeSlide 0.4s ease; }
    @keyframes fadeSlide { from { opacity: 0; transform: translateX(20px); } to { opacity: 1; transform: translateX(0); } }
    
    .step-title { font-size: 20px; color: #1e293b; font-weight: 600; margin-bottom: 25px; padding-bottom: 15px; border-bottom: 2px solid #e2e8f0; }
    .field-group { margin-bottom: 25px; }
    .field-label { display: block; font-weight: 600; color: #334155; margin-bottom: 10px; font-size: 14px; }
    .required { color: #ef4444; }
    input, select, textarea { width: 100%; padding: 14px 16px; border: 2px solid #e2e8f0; border-radius: 12px; font-size: 15px; transition: all 0.3s; background: white; }
    input:focus, select:focus, textarea:focus { outline: none; border-color: #3b82f6; box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1); }
    select { cursor: pointer; appearance: none; padding-right: 45px; background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3E%3Cpath fill='%233b82f6' d='M8 11L3 6h10z'/%3E%3C/svg%3E"); background-repeat: no-repeat; background-position: right 16px center; }
    textarea { min-height: 120px; resize: vertical; font-family: inherit; }
    
    .button-group { display: flex; gap: 15px; margin-top: 35px; }
    .btn { flex: 1; padding: 16px; border: none; border-radius: 12px; font-size: 16px; font-weight: 600; cursor: pointer; transition: all 0.3s; }
    .btn-secondary { background: #f1f5f9; color: #64748b; }
    .btn-secondary:hover { background: #e2e8f0; }
    .btn-primary { background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); color: white; box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3); }
    .btn-primary:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4); }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; transform: none !important; }
    .loading-text { color: #64748b; font-size: 14px; font-style: italic; margin-top: 10px; }
    .error-text { color: #ef4444; font-size: 14px; margin-top: 10px; display: none; }
    
    @media (max-width: 968px) {
      .main-container { grid-template-columns: 1fr; }
      .info-panel { position: relative; top: 0; }
    }
  </style>
</head>
<body>
  <div class="top-nav">
    <div class="nav-content">
      <div class="logo"><div class="logo-icon">🏥</div>MediCare+</div>
      <a href="PatientServlet" class="back-link">← Back to Dashboard</a>
    </div>
  </div>

  <div class="main-container">
    <div class="info-panel">
      <h2>Book Your Appointment</h2>
      <p>Schedule a consultation with our expert healthcare professionals in just a few simple steps.</p>
      <ul class="feature-list">
        <li class="feature-item"><div class="feature-icon">⚡</div><div class="feature-text"><h3>Quick Booking</h3><p>Simple 3-step process</p></div></li>
        <li class="feature-item"><div class="feature-icon">✅</div><div class="feature-text"><h3>Instant Confirmation</h3><p>Get notified within 24 hours</p></div></li>
        <li class="feature-item"><div class="feature-icon">📅</div><div class="feature-text"><h3>Flexible Scheduling</h3><p>Choose your preferred time</p></div></li>
        <li class="feature-item"><div class="feature-icon">🔒</div><div class="feature-text"><h3>Secure & Private</h3><p>Your data is protected</p></div></li>
      </ul>
    </div>

    <div class="form-panel">
      <div class="form-header">
        <h1>Appointment Details</h1>
        <p class="subtitle">Please fill in all the required information</p>
        <div class="progress-bar"><div class="progress-fill" id="progressBar"></div></div>
      </div>

      <form action="AppointmentsServlet" method="post" id="appointmentForm">
        <div class="form-step active" data-step="1">
          <div class="step-title">👨‍⚕️ Select Department & Doctor</div>
          <div class="field-group">
            <label class="field-label">Department <span class="required">*</span></label>
            <select name="department" id="departmentSelect" required>
              <option value="">-- Choose Department --</option>
              <option value="Cardiology">❤️ Cardiology</option>
              <option value="Neurology">🧠 Neurology</option>
              <option value="Orthopedics">🦴 Orthopedics</option>
              <option value="Pediatrics">👶 Pediatrics</option>
              <option value="General Medicine">🏥 General Medicine</option>
            </select>
          </div>
          <div class="field-group" id="doctorSelectGroup" style="display:none;">
            <label class="field-label">Select Doctor <span class="required">*</span></label>
            <select name="doctor_id" id="doctorSelect" required>
              <option value="">-- Choose Doctor --</option>
            </select>
            <div class="loading-text" id="loadingDoctors">Loading doctors...</div>
            <div class="error-text" id="errorDoctors"></div>
          </div>
          <input type="hidden" name="doctor_name" id="doctorNameHidden">
          <div class="button-group">
            <button type="button" class="btn btn-secondary" onclick="window.location.href='PatientServlet'">Cancel</button>
            <button type="button" class="btn btn-primary" id="nextBtn1" disabled>Next →</button>
          </div>
        </div>

        <div class="form-step" data-step="2">
          <div class="step-title">📅 Choose Date & Time</div>
          <div class="field-group">
            <label class="field-label">Appointment Date <span class="required">*</span></label>
            <input type="date" name="appointment_date" id="appointmentDate" required>
          </div>
          <div class="field-group">
            <label class="field-label">Preferred Time <span class="required">*</span></label>
            <input type="time" name="appointment_time" required>
          </div>
          <div class="button-group">
            <button type="button" class="btn btn-secondary" id="backBtn2">← Back</button>
            <button type="button" class="btn btn-primary" id="nextBtn2">Next →</button>
          </div>
        </div>

        <div class="form-step" data-step="3">
          <div class="step-title">📝 Additional Information</div>
          <div class="field-group">
            <label class="field-label">Symptoms / Purpose of Visit</label>
            <textarea name="symptoms" placeholder="Describe your symptoms or reason for appointment..."></textarea>
          </div>
          <div class="button-group">
            <button type="button" class="btn btn-secondary" id="backBtn3">← Back</button>
            <button type="submit" class="btn btn-primary">Confirm Booking ✓</button>
          </div>
        </div>
      </form>
    </div>
  </div>

  <script src="js/appoint.js"></script>
</body>
</html>