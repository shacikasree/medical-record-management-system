<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MedLife Hospital - Register</title>
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

        .register-container {
            display: flex;
            background: rgba(255, 255, 255, 0.98);
            backdrop-filter: blur(10px);
            border-radius: 30px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
            max-width: 1100px;
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

        .register-panel {
            flex: 1.3;
            padding: 50px 45px;
            background: white;
            overflow-y: auto;
            max-height: 95vh;
        }

        .register-header {
            margin-bottom: 30px;
        }

        .register-header h2 {
            color: #2d3748;
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 8px;
        }

        .register-header p {
            color: #718096;
            font-size: 14px;
        }

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
            margin-bottom: 20px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group.full-width {
            grid-column: 1 / -1;
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
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 14px 16px 14px 45px;
            border: 2px solid #e2e8f0;
            border-radius: 12px;
            font-size: 14px;
            color: #2d3748;
            transition: all 0.3s ease;
            background: #f7fafc;
            font-family: 'Inter', sans-serif;
        }

        .form-group textarea {
            resize: vertical;
            min-height: 80px;
            padding-top: 14px;
            padding-left: 16px;
        }

        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #4db8d8;
            background: white;
            box-shadow: 0 0 0 3px rgba(77, 184, 216, 0.1);
        }

        .form-group input::placeholder,
        .form-group textarea::placeholder {
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
        }

        .password-toggle:hover {
            color: #4a5568;
        }

        .error-message,
        .success-message {
            padding: 12px 16px;
            border-radius: 10px;
            margin-bottom: 20px;
            font-size: 13px;
            display: none;
            animation: slideDown 0.3s ease;
        }

        .error-message {
            background: #fff5f5;
            color: #c53030;
            border: 1px solid #feb2b2;
        }

        .success-message {
            background: #f0fff4;
            color: #2f855a;
            border: 1px solid #9ae6b4;
        }

        .error-message.show,
        .success-message.show {
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

        .register-button {
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

        .register-button:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(77, 184, 216, 0.4);
        }

        .register-button:active {
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

        .login-section {
            text-align: center;
        }

        .login-section p {
            color: #718096;
            font-size: 14px;
        }

        .login-section a {
            color: #4db8d8;
            text-decoration: none;
            font-weight: 600;
            transition: 0.3s;
        }

        .login-section a:hover {
            color: #3a9bb8;
        }

        @media (max-width: 968px) {
            .register-container {
                flex-direction: column;
                border-radius: 20px;
            }

            .welcome-panel {
                padding: 40px 30px;
            }

            .welcome-content h1 {
                font-size: 28px;
            }

            .register-panel {
                padding: 40px 30px;
                max-height: none;
            }

            .form-row {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="register-container">
        <div class="welcome-panel">
            <div class="welcome-content">
                <div class="hospital-icon">🏥</div>
                <h1>Join MedLife Hospital</h1>
                <p>Create your account and get access to world-class healthcare services.</p>
                
                <ul class="feature-list">
                    <li class="feature-item">24/7 Healthcare Access</li>
                    <li class="feature-item">Secure Medical Records</li>
                    <li class="feature-item">Expert Medical Team</li>
                    <li class="feature-item">Easy Appointment Booking</li>
                    <li class="feature-item">Online Consultations</li>
                    <li class="feature-item">Prescription Management</li>
                </ul>
            </div>
        </div>

        <div class="register-panel">
            <div class="register-header">
                <h2>Create Account</h2>
                <p>Fill in the details to register your account</p>
            </div>

            <div class="error-message" id="errorMessage"></div>
            <div class="success-message" id="successMessage"></div>

            <form action="RegisterServlet" method="POST" id="registerForm">
                <div class="form-row">
                    <div class="form-group">
                        <label>Full Name</label>
                        <div class="input-wrapper">
                            <span class="input-icon">👤</span>
                            <input type="text" name="fullname" placeholder="Enter your full name" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Email Address</label>
                        <div class="input-wrapper">
                            <span class="input-icon">✉️</span>
                            <input type="email" name="email" placeholder="Enter your email" required>
                        </div>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Phone Number</label>
                        <div class="input-wrapper">
                            <span class="input-icon">📱</span>
                            <input type="tel" name="phone" placeholder="Enter phone number" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Date of Birth</label>
                        <div class="input-wrapper">
                            <span class="input-icon">📅</span>
                            <input type="date" name="dob" required>
                        </div>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Password</label>
                        <div class="input-wrapper">
                            <span class="input-icon">🔒</span>
                            <input type="password" name="password" id="password" placeholder="Create a password" required>
                            <span class="password-toggle" onclick="togglePassword('password', 'eyeIcon1')">
                                <svg id="eyeIcon1" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                    <circle cx="12" cy="12" r="3"></circle>
                                </svg>
                            </span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Confirm Password</label>
                        <div class="input-wrapper">
                            <span class="input-icon">🔒</span>
                            <input type="password" id="confirmPassword" placeholder="Confirm password" required>
                            <span class="password-toggle" onclick="togglePassword('confirmPassword', 'eyeIcon2')">
                                <svg id="eyeIcon2" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                    <circle cx="12" cy="12" r="3"></circle>
                                </svg>
                            </span>
                        </div>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Gender</label>
                        <div class="input-wrapper">
                            <span class="input-icon">⚧️</span>
                            <select name="gender" required>
                                <option value="">Select gender</option>
                                <option value="male">Male</option>
                                <option value="female">Female</option>
                                <option value="other">Other</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Blood Group</label>
                        <div class="input-wrapper">
                            <span class="input-icon">🩸</span>
                            <select name="bloodGroup" required>
                                <option value="">Select blood group</option>
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
                    </div>
                </div>

                <div class="form-group full-width">
                    <label>Select Your Role</label>
                    <div class="input-wrapper">
                        <span class="input-icon">👤</span>
                        <select name="role" id="roleSelect" required onchange="handleRoleChange()">
                            <option value="">Choose your role</option>
                            <option value="patient">Patient</option>
                            <option value="doctor">Doctor</option>
                        </select>
                    </div>
                </div>

                <div id="doctorFields" style="display: none;">
                    <div class="form-row">
                        <div class="form-group">
                            <label>Specialty</label>
                            <div class="input-wrapper">
                                <span class="input-icon">🩺</span>
                                <select name="specialty" id="specialty">
                                    <option value="">Select specialty</option>
                                    <option value="Cardiology">Cardiology</option>
                                    <option value="Neurology">Neurology</option>
                                    <option value="Orthopedics">Orthopedics</option>
                                    <option value="Pediatrics">Pediatrics</option>
                                    <option value="General Medicine">General Medicine</option>
                                    <option value="Dermatology">Dermatology</option>
                                    <option value="Psychiatry">Psychiatry</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label>License Number</label>
                            <div class="input-wrapper">
                                <span class="input-icon">🆔</span>
                                <input type="text" name="licenseNumber" id="licenseNumber" placeholder="Medical license number">
                            </div>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Qualification</label>
                            <div class="input-wrapper">
                                <span class="input-icon">🎓</span>
                                <input type="text" name="qualification" id="qualification" placeholder="MBBS, MD, etc.">
                            </div>
                        </div>
                        <div class="form-group">
                            <label>Experience (Years)</label>
                            <div class="input-wrapper">
                                <span class="input-icon">💼</span>
                                <input type="number" name="experience" id="experience" placeholder="Years of experience" min="0">
                            </div>
                        </div>
                    </div>

                    <div class="form-group full-width">
                        <label>Department</label>
                        <div class="input-wrapper">
                            <span class="input-icon">🏢</span>
                            <select name="department" id="department">
                                <option value="">Select department</option>
                                <option value="Cardiology">Cardiology</option>
                                <option value="Neurology">Neurology</option>
                                <option value="Orthopedics">Orthopedics</option>
                                <option value="Pediatrics">Pediatrics</option>
                                <option value="General Medicine">General Medicine</option>
                            </select>
                        </div>
                    </div>
                </div>

                <div id="patientFields" style="display: none;">
                    <div class="form-row">
                        <div class="form-group">
                            <label>Emergency Contact Name</label>
                            <div class="input-wrapper">
                                <span class="input-icon">👥</span>
                                <input type="text" name="emergencyContactName" id="emergencyContactName" placeholder="Contact person name">
                            </div>
                        </div>
                        <div class="form-group">
                            <label>Emergency Contact Number</label>
                            <div class="input-wrapper">
                                <span class="input-icon">🚨</span>
                                <input type="tel" name="emergencyContact" id="emergencyContact" placeholder="Emergency contact">
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-group full-width">
                    <label>Address</label>
                    <textarea name="address" placeholder="Enter your address" required></textarea>
                </div>

                <button type="submit" class="register-button">Create Account</button>
            </form>

            <div class="divider">
                <span>OR</span>
            </div>

            <div class="login-section">
                <p>Already have an account? <a href="Login.jsp">Sign In</a></p>
            </div>
        </div>
    </div>

    <script>
        function togglePassword(fieldId, iconId) {
            const field = document.getElementById(fieldId);
            const eyeIcon = document.getElementById(iconId);
            
            if (field.type === 'password') {
                field.type = 'text';
                eyeIcon.innerHTML = '<path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line>';
            } else {
                field.type = 'password';
                eyeIcon.innerHTML = '<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle>';
            }
        }

        function handleRoleChange() {
            const role = document.getElementById('roleSelect').value;
            const doctorFields = document.getElementById('doctorFields');
            const patientFields = document.getElementById('patientFields');
            
            doctorFields.style.display = 'none';
            patientFields.style.display = 'none';
            
            if (role === 'doctor') {
                doctorFields.style.display = 'block';
                document.getElementById('specialty').required = true;
                document.getElementById('licenseNumber').required = true;
                document.getElementById('qualification').required = true;
                document.getElementById('experience').required = true;
                document.getElementById('department').required = true;
                document.getElementById('emergencyContactName').required = false;
                document.getElementById('emergencyContact').required = false;
            } else if (role === 'patient') {
                patientFields.style.display = 'block';
                document.getElementById('emergencyContactName').required = true;
                document.getElementById('emergencyContact').required = true;
                document.getElementById('specialty').required = false;
                document.getElementById('licenseNumber').required = false;
                document.getElementById('qualification').required = false;
                document.getElementById('experience').required = false;
                document.getElementById('department').required = false;
            }
        }

        document.getElementById('registerForm').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            const errorMessage = document.getElementById('errorMessage');
            
            if (password !== confirmPassword) {
                e.preventDefault();
                errorMessage.textContent = '❌ Passwords do not match!';
                errorMessage.classList.add('show');
                setTimeout(() => {
                    errorMessage.classList.remove('show');
                }, 3000);
                return false;
            }

            if (password.length < 6) {
                e.preventDefault();
                errorMessage.textContent = '❌ Password must be at least 6 characters long!';
                errorMessage.classList.add('show');
                setTimeout(() => {
                    errorMessage.classList.remove('show');
                }, 3000);
                return false;
            }

            const role = document.getElementById('roleSelect').value;
            if (!role) {
                e.preventDefault();
                errorMessage.textContent = '❌ Please select your role!';
                errorMessage.classList.add('show');
                setTimeout(() => {
                    errorMessage.classList.remove('show');
                }, 3000);
                return false;
            }
        });

        window.addEventListener('DOMContentLoaded', function() {
            const urlParams = new URLSearchParams(window.location.search);
            const error = urlParams.get('error');
            const success = urlParams.get('success');
            
            if (error) {
                const errorMessage = document.getElementById('errorMessage');
                const messages = {
                    'emailExists': '❌ Email already registered!',
                    'registrationFailed': '❌ Registration failed. Please try again.',
                    'invalidData': '❌ Invalid data provided.'
                };
                errorMessage.textContent = messages[error] || '❌ An error occurred.';
                errorMessage.classList.add('show');
            }
            
            if (success) {
                const successMessage = document.getElementById('successMessage');
                successMessage.textContent = '✅ Registration successful! Redirecting to login...';
                successMessage.classList.add('show');
                setTimeout(() => {
                    window.location.href = 'Login.jsp';
                }, 2000);
            }
        });
    </script>
</body>
</html>