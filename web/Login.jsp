text

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MedLife Hospital - Login</title>
</head>
   <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', sans-serif;
            background-image: url('https://img.freepik.com/free-photo/frame-medical-equipment-desk_23-2148519742.jpg?semt=ais_user_personalization&w=740&q=80');
            background-size: cover;
            background-position: center;
            background-repeat: no-repeat;
            background-attachment: fixed;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .login-container {
            display: flex;
            background: rgba(255, 255, 255, 0.98);
            backdrop-filter: blur(10px);
            border-radius: 30px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
            max-width: 900px;
            width: 100%;
            overflow: hidden;
        }

        .welcome-panel {
            flex: 1;
            background: linear-gradient(135deg, rgba(176, 224, 230, 0.85) 0%, rgba(173, 216, 230, 0.9) 100%);
            padding: 60px 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .welcome-content {
            position: relative;
            z-index: 1;
        }

        .hospital-icon {
            font-size: 80px;
            margin-bottom: 30px;
        }

        .welcome-content h1 {
            color: #1e3a5f;
            font-size: 36px;
            font-weight: 700;
            margin-bottom: 15px;
            line-height: 1.2;
        }

        .welcome-content p {
            color: #2c5f7a;
            font-size: 16px;
            line-height: 1.6;
            margin-bottom: 30px;
        }

        .feature-list {
            list-style: none;
        }

        .feature-item {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 15px;
            color: #2c5f7a;
            font-size: 14px;
        }

        .feature-item::before {
            content: '✓';
            width: 24px;
            height: 24px;
            background: rgba(255, 255, 255, 0.8);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            color: #1e3a5f;
            flex-shrink: 0;
        }

        .login-panel {
            flex: 1;
            padding: 60px 50px;
            background: white;
        }

        .login-header {
            margin-bottom: 40px;
        }

        .login-header h2 {
            color: #2d3748;
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 8px;
        }

        .login-header p {
            color: #718096;
            font-size: 14px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            color: #4a5568;
            font-size: 13px;
            font-weight: 600;
            margin-bottom: 8px;
        }

        .input-wrapper {
            position: relative;
        }

        .input-icon {
            position: absolute;
            left: 16px;
            top: 50%;
            transform: translateY(-50%);
            font-size: 18px;
            color: #a0aec0;
        }

        .form-group input,
        .form-group select {
            width: 100%;
            padding: 14px 16px 14px 45px;
            border: 2px solid #e2e8f0;
            border-radius: 12px;
            font-size: 14px;
            color: #2d3748;
            transition: all 0.3s ease;
            background: #f7fafc;
        }

        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: #4db8d8;
            background: white;
            box-shadow: 0 0 0 3px rgba(77, 184, 216, 0.1);
        }

        .form-group input::placeholder {
            color: #cbd5e0;
        }

        .password-toggle {
            position: absolute;
            right: 16px;
            top: 50%;
            transform: translateY(-50%);
            cursor: pointer;
            color: #a0aec0;
            transition: 0.3s;
            user-select: none;
        }

        .password-toggle:hover {
            color: #4a5568;
        }

        .forgot-password {
            text-align: right;
            margin-top: -10px;
            margin-bottom: 25px;
        }

        .forgot-password a {
            color: #4db8d8;
            text-decoration: none;
            font-size: 13px;
            font-weight: 500;
            transition: 0.3s;
        }

        .forgot-password a:hover {
            color: #3a9bb8;
        }

        .error-message {
            padding: 12px 16px;
            border-radius: 10px;
            margin-bottom: 20px;
            font-size: 13px;
            display: none;
            animation: slideDown 0.3s ease;
            background: #fff5f5;
            color: #c53030;
            border: 1px solid #feb2b2;
        }

        .error-message.show {
            display: block;
        }

        @keyframes slideDown {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .login-button {
            width: 100%;
            padding: 16px;
            background: linear-gradient(135deg, #4db8d8 0%, #3a9bb8 100%);
            border: none;
            border-radius: 12px;
            color: white;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(77, 184, 216, 0.3);
            margin-bottom: 20px;
        }

        .login-button:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(77, 184, 216, 0.4);
        }

        .login-button:active {
            transform: translateY(0);
        }

        .divider {
            text-align: center;
            margin: 25px 0;
            position: relative;
        }

        .divider::before {
            content: '';
            position: absolute;
            left: 0;
            right: 0;
            top: 50%;
            height: 1px;
            background: #e2e8f0;
        }

        .divider span {
            position: relative;
            background: white;
            padding: 0 15px;
            color: #a0aec0;
            font-size: 12px;
        }

        .register-section {
            text-align: center;
        }

        .register-section p {
            color: #718096;
            font-size: 14px;
        }

        .register-section a {
            color: #4db8d8;
            text-decoration: none;
            font-weight: 600;
            transition: 0.3s;
        }

        .register-section a:hover {
            color: #3a9bb8;
        }

        @media (max-width: 968px) {
            .login-container {
                flex-direction: column;
                border-radius: 20px;
            }

            .welcome-panel {
                padding: 40px 30px;
            }

            .welcome-content h1 {
                font-size: 28px;
            }

            .login-panel {
                padding: 40px 30px;
            }
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="welcome-panel">
            <div class="welcome-content">
                <div class="hospital-icon">🏥</div>
                <h1>Welcome Back!</h1>
                <p>Login to access your medical records, appointments, and healthcare services.</p>
                
                <ul class="feature-list">
                    <li class="feature-item">24/7 Healthcare Access</li>
                    <li class="feature-item">Secure Medical Records</li>
                    <li class="feature-item">Expert Medical Team</li>
                    <li class="feature-item">Easy Appointment Booking</li>
                </ul>
            </div>
        </div>

        <div class="login-panel">
            <div class="login-header">
                <h2>Sign In</h2>
                <p>Enter your credentials to continue</p>
            </div>

            <div class="error-message" id="errorMessage"></div>

            <form action="LoginServlet" method="POST" id="loginForm">
                <div class="form-group">
                    <label>Select Role</label>
                    <div class="input-wrapper">
                        <span class="input-icon">👤</span>
                        <select name="role" required>
                            <option value="">Choose your role</option>
                            <option value="admin">Admin</option>
                            <option value="doctor">Doctor</option>
                            <option value="patient">Patient</option>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label>Email Address</label>
                    <div class="input-wrapper">
                        <span class="input-icon">✉️</span>
                        <input type="email" name="email" placeholder="Enter your email" required>
                    </div>
                </div>

                <div class="form-group">
                    <label>Password</label>
                    <div class="input-wrapper">
                        <span class="input-icon">🔒</span>
                        <input type="password" id="password" name="password" placeholder="Enter your password" required>
                        <span class="password-toggle" onclick="togglePassword()">
                            <svg id="eyeIcon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                <circle cx="12" cy="12" r="3"></circle>
                            </svg>
                        </span>
                    </div>
                </div>

                <div class="forgot-password">
                    <a href="forgot.jsp">Forgot Password?</a>
                </div>

                <button type="submit" class="login-button">Sign In</button>
            </form>

            <div class="divider">
                <span>OR</span>
            </div>

            <div class="register-section">
                <p>Don't have an account? <a href="Register.jsp">Create Account</a></p>
            </div>
        </div>
    </div>

    <script>
        // ==================== PASSWORD TOGGLE ====================
        function togglePassword() {
            const passwordField = document.getElementById('password');
            const eyeIcon = document.getElementById('eyeIcon');
            
            if (passwordField.type === 'password') {
                // Show password
                passwordField.type = 'text';
                eyeIcon.innerHTML = '<path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line>';
            } else {
                // Hide password
                passwordField.type = 'password';
                eyeIcon.innerHTML = '<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle>';
            }
        }

        // ==================== ERROR MESSAGE HANDLING ====================
        window.addEventListener('DOMContentLoaded', function() {
            const urlParams = new URLSearchParams(window.location.search);
            const error = urlParams.get('error');
            const errorMessage = document.getElementById('errorMessage');
            
            if (error) {
                let message = '';
                
                switch(error) {
                    case 'emptyfields':
                        message = '⚠️ Please fill in all fields';
                        break;
                    case 'invalidUser':
                        message = '❌ User not found. Please check your email.';
                        break;
                    case 'invalidPass':
                        message = '❌ Incorrect password. Please try again.';
                        break;
                    case 'roleMismatch':
                        message = '❌ Selected role does not match your account.';
                        break;
                    case 'inactive':
                        message = '❌ Your account is inactive. Contact support.';
                        break;
                    case 'database':
                        message = '❌ Database error. Please try again later.';
                        break;
                    case 'server':
                        message = '❌ Server error. Please try again.';
                        break;
                    case 'loginRequired':
                        message = '⚠️ Please login to continue';
                        break;
                    case 'sessionExpired':
                        message = '⚠️ Your session has expired. Please login again.';
                        break;
                    default:
                        message = '❌ An error occurred. Please try again.';
                }
                
                errorMessage.textContent = message;
                errorMessage.classList.add('show');
                
                // Auto-hide after 5 seconds
                setTimeout(function() {
                    errorMessage.classList.remove('show');
                }, 5000);
            }
        });

        // ==================== FORM VALIDATION ====================
        document.getElementById('loginForm').addEventListener('submit', function(e) {
            const role = document.querySelector('[name="role"]').value;
            const email = document.querySelector('[name="email"]').value;
            const password = document.querySelector('[name="password"]').value;
            
            if (!role || !email || !password) {
                e.preventDefault();
                const errorMessage = document.getElementById('errorMessage');
                errorMessage.textContent = '⚠️ Please fill in all fields';
                errorMessage.classList.add('show');
                
                setTimeout(function() {
                    errorMessage.classList.remove('show');
                }, 3000);
            }
        });

        console.log('✅ Login page loaded successfully');
    </script>
</body>
</html>