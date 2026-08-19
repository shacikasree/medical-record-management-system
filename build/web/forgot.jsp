<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MedLife Hospital - Forgot Password</title>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', sans-serif;
            background: linear-gradient(135deg, #d4f1d4 0%, #e8f4f8 50%, #fce4ec 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .forgot-password-container {
            position: relative;
            z-index: 1;
            display: flex;
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            border-radius: 30px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
            max-width: 900px;
            width: 100%;
            overflow: hidden;
        }

        .info-panel {
            flex: 1;
            background: linear-gradient(135deg, #b8e6b8 0%, #d4f1d4 100%);
            padding: 60px 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .lock-icon {
            font-size: 80px;
            margin-bottom: 30px;
            text-align: center;
        }

        .info-panel h1 {
            color: #2d5f3f;
            font-size: 36px;
            font-weight: 700;
            margin-bottom: 15px;
        }

        .info-panel p {
            color: #4a7c59;
            font-size: 16px;
            line-height: 1.6;
            margin-bottom: 30px;
        }

        .info-steps {
            list-style: none;
        }

        .info-step {
            display: flex;
            align-items: flex-start;
            gap: 12px;
            margin-bottom: 15px;
            color: #3d6e4e;
            font-size: 14px;
        }

        .step-number {
            width: 24px;
            height: 24px;
            background: rgba(255, 255, 255, 0.7);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            color: #2d5f3f;
            flex-shrink: 0;
            font-size: 12px;
        }

        .form-panel {
            flex: 1;
            padding: 60px 50px;
            background: white;
        }

        .form-header {
            margin-bottom: 40px;
        }

        .form-header h2 {
            color: #2d3748;
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 8px;
        }

        .form-header p {
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

        .form-group input {
            width: 100%;
            padding: 14px 16px 14px 45px;
            border: 2px solid #e2e8f0;
            border-radius: 12px;
            font-size: 14px;
            color: #2d3748;
            transition: all 0.3s ease;
            background: #f7fafc;
        }

        .form-group input:focus {
            outline: none;
            border-color: #81c784;
            background: white;
            box-shadow: 0 0 0 3px rgba(129, 199, 132, 0.1);
        }

        .error-message, .success-message {
            padding: 12px 16px;
            border-radius: 10px;
            margin-bottom: 20px;
            font-size: 13px;
            display: none;
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

        .error-message.show, .success-message.show {
            display: block;
        }

        .submit-button {
            width: 100%;
            padding: 16px;
            background: linear-gradient(135deg, #81c784 0%, #66bb6a 100%);
            border: none;
            border-radius: 12px;
            color: white;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(129, 199, 132, 0.3);
            margin-bottom: 20px;
        }

        .submit-button:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(129, 199, 132, 0.4);
        }

        .submit-button:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
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

        .back-to-login {
            text-align: center;
        }

        .back-to-login p {
            color: #718096;
            font-size: 14px;
        }

        .back-to-login a {
            color: #81c784;
            text-decoration: none;
            font-weight: 600;
        }

        .verification-section {
            display: none;
        }

        .verification-section.active {
            display: block;
        }

        .password-toggle {
            position: absolute;
            right: 16px;
            top: 50%;
            transform: translateY(-50%);
            cursor: pointer;
            color: #a0aec0;
        }

        .otp-inputs {
            display: flex;
            gap: 10px;
            justify-content: center;
            margin: 20px 0;
        }

        .otp-input {
            width: 50px;
            height: 50px;
            text-align: center;
            font-size: 20px;
            font-weight: 600;
            border: 2px solid #e2e8f0;
            border-radius: 12px;
            background: #f7fafc;
            padding: 0;
        }

        .otp-input:focus {
            outline: none;
            border-color: #81c784;
            background: white;
        }

        .resend-code {
            text-align: center;
            margin-top: 15px;
            color: #718096;
            font-size: 13px;
        }

        .resend-code a {
            color: #81c784;
            cursor: pointer;
            font-weight: 600;
        }

        @media (max-width: 968px) {
            .forgot-password-container {
                flex-direction: column;
            }
            .info-panel, .form-panel {
                padding: 40px 30px;
            }
        }
    </style>
</head>
<body>
    <div class="forgot-password-container">
        <div class="info-panel">
            <div class="lock-icon">🔐</div>
            <h1>Reset Password</h1>
            <p>Follow these simple steps to reset your password.</p>
            
            <ul class="info-steps">
                <li class="info-step">
                    <span class="step-number">1</span>
                    <span>Enter your registered email address</span>
                </li>
                <li class="info-step">
                    <span class="step-number">2</span>
                    <span>Verify the OTP sent to your email</span>
                </li>
                <li class="info-step">
                    <span class="step-number">3</span>
                    <span>Create a new secure password</span>
                </li>
                <li class="info-step">
                    <span class="step-number">4</span>
                    <span>Login with your new password</span>
                </li>
            </ul>
        </div>

        <div class="form-panel">
            <div class="form-header">
                <h2>Forgot Password?</h2>
                <p>Enter your email to receive a reset code</p>
            </div>

            <div class="error-message" id="errorMessage"></div>
            <div class="success-message" id="successMessage"></div>

            <!-- Step 1: Email Verification -->
            <div id="emailSection">
                <form id="emailForm">
                    <div class="form-group">
                        <label>Email Address</label>
                        <div class="input-wrapper">
                            <span class="input-icon">✉️</span>
                            <input type="email" id="email" placeholder="Enter your registered email" required>
                        </div>
                    </div>
                    <button type="submit" class="submit-button" id="sendOTPBtn">
                        Send Verification Code
                    </button>
                </form>
            </div>

            <!-- Step 2: OTP Verification -->
            <div id="otpSection" class="verification-section">
                <form id="otpForm">
                    <div class="form-group">
                        <label>Enter Verification Code</label>
                        <div class="otp-inputs">
    <input type="tel" class="otp-input" maxlength="1" id="otp1" inputmode="numeric" pattern="[0-9]" required>
    <input type="tel" class="otp-input" maxlength="1" id="otp2" inputmode="numeric" pattern="[0-9]" required>
    <input type="tel" class="otp-input" maxlength="1" id="otp3" inputmode="numeric" pattern="[0-9]" required>
    <input type="tel" class="otp-input" maxlength="1" id="otp4" inputmode="numeric" pattern="[0-9]" required>
    <input type="tel" class="otp-input" maxlength="1" id="otp5" inputmode="numeric" pattern="[0-9]" required>
    <input type="tel" class="otp-input" maxlength="1" id="otp6" inputmode="numeric" pattern="[0-9]" required>
</div>
                        <div class="resend-code">
                            Didn't receive code? <a onclick="resendOTP()">Resend</a>
                        </div>
                    </div>
                    <button type="submit" class="submit-button">Verify Code</button>
                </form>
            </div>

            <!-- Step 3: Reset Password -->
            <div id="resetSection" class="verification-section">
                <form id="resetForm">
                    <div class="form-group">
                        <label>New Password</label>
                        <div class="input-wrapper">
                            <span class="input-icon">🔒</span>
                            <input type="password" id="newPassword" placeholder="Enter new password" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Confirm Password</label>
                        <div class="input-wrapper">
                            <span class="input-icon">🔒</span>
                            <input type="password" id="confirmPassword" placeholder="Confirm new password" required>
                        </div>
                    </div>
                    <button type="submit" class="submit-button">Reset Password</button>
                </form>
            </div>

            <div class="divider"><span>OR</span></div>
            <div class="back-to-login">
                <p>Remember your password? <a href="Login.jsp">Back to Login</a></p>
            </div>
        </div>
    </div>

    <script>
       // OTP Input Auto-focus
const otpInputs = document.querySelectorAll('.otp-input');
otpInputs.forEach((input, index) => {
    input.addEventListener('input', function() {
        if (this.value.length === 1 && index < otpInputs.length - 1) {
            otpInputs[index + 1].focus();
        }
        this.value = this.value.replace(/[^0-9]/g, '');
    });

    input.addEventListener('keydown', function(e) {
        if (e.key === 'Backspace' && this.value === '' && index > 0) {
            otpInputs[index - 1].focus();
        }
    });
});
    // Email Form Submission - IMPROVED WITH BETTER ERROR HANDLING
document.getElementById('emailForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const email = document.getElementById('email').value;
    const btn = document.getElementById('sendOTPBtn');
    
    console.log('📧 Attempting to send OTP for:', email);
    
    btn.disabled = true;
    btn.textContent = 'Sending...';
    
    const params = new URLSearchParams();
    params.append('action', 'sendOTP');
    params.append('email', email);
    
    console.log('Sending:', params.toString());

    fetch('ForgotPasswordServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params.toString()
    })
    .then(response => {
        console.log('Response status:', response.status);
        console.log('Response ok:', response.ok);
        console.log('Response type:', response.type);
        console.log('Content-Type:', response.headers.get('content-type'));
        
        // Clone response to read it twice
        return response.clone().text().then(text => {
            console.log('=== RAW RESPONSE START ===');
            console.log(text);
            console.log('=== RAW RESPONSE END ===');
            console.log('Response length:', text.length);
            
            // If empty response
            if (!text || text.trim() === '') {
                throw new Error('Empty response from server');
            }
            
            // Try to parse JSON
            try {
                const json = JSON.parse(text);
                console.log('✅ Parsed JSON:', json);
                return json;
            } catch (e) {
                console.error('❌ JSON Parse Error:', e);
                console.error('Failed to parse:', text.substring(0, 200));
                throw new Error('Server returned invalid JSON. Check NetBeans console.');
            }
        });
    })
    .then(data => {
        btn.disabled = false;
        btn.textContent = 'Send Verification Code';
        
        if (data.success) {
            document.getElementById('successMessage').textContent = '✅ ' + data.message;
            document.getElementById('successMessage').classList.add('show');
            
            setTimeout(() => {
                document.getElementById('emailSection').style.display = 'none';
                document.getElementById('otpSection').classList.add('active');
                document.getElementById('successMessage').classList.remove('show');
            }, 2000);
        } else {
            document.getElementById('errorMessage').textContent = '❌ ' + data.message;
            document.getElementById('errorMessage').classList.add('show');
            setTimeout(() => {
                document.getElementById('errorMessage').classList.remove('show');
            }, 3000);
        }
    })
    .catch(error => {
        btn.disabled = false;
        btn.textContent = 'Send Verification Code';
        console.error('❌ Full Error:', error);
        document.getElementById('errorMessage').textContent = '❌ Error: ' + error.message;
        document.getElementById('errorMessage').classList.add('show');
        setTimeout(() => {
            document.getElementById('errorMessage').classList.remove('show');
        }, 5000);
    });
});
        // OTP Form Submission
        document.getElementById('otpForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            const params = new URLSearchParams();
            params.append('action', 'verifyOTP');
            params.append('otp1', document.getElementById('otp1').value);
            params.append('otp2', document.getElementById('otp2').value);
            params.append('otp3', document.getElementById('otp3').value);
            params.append('otp4', document.getElementById('otp4').value);
            params.append('otp5', document.getElementById('otp5').value);
            params.append('otp6', document.getElementById('otp6').value);

            fetch('ForgotPasswordServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: params.toString()
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    document.getElementById('successMessage').textContent = '✅ ' + data.message;
                    document.getElementById('successMessage').classList.add('show');
                    
                    setTimeout(() => {
                        document.getElementById('otpSection').classList.remove('active');
                        document.getElementById('resetSection').classList.add('active');
                        document.getElementById('successMessage').classList.remove('show');
                    }, 2000);
                } else {
                    document.getElementById('errorMessage').textContent = '❌ ' + data.message;
                    document.getElementById('errorMessage').classList.add('show');
                    setTimeout(() => {
                        document.getElementById('errorMessage').classList.remove('show');
                    }, 3000);
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
        });

        // Reset Password Form Submission
        document.getElementById('resetForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (newPassword !== confirmPassword) {
                document.getElementById('errorMessage').textContent = '❌ Passwords do not match!';
                document.getElementById('errorMessage').classList.add('show');
                setTimeout(() => document.getElementById('errorMessage').classList.remove('show'), 3000);
                return;
            }
            
            const params = new URLSearchParams();
            params.append('action', 'resetPassword');
            params.append('newPassword', newPassword);
            params.append('confirmPassword', confirmPassword);

            fetch('ForgotPasswordServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: params.toString()
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    document.getElementById('successMessage').textContent = '✅ ' + data.message + ' Redirecting...';
                    document.getElementById('successMessage').classList.add('show');
                    
                    setTimeout(() => {
                        window.location.href = 'Login.jsp?msg=passwordReset';
                    }, 2000);
                } else {
                    document.getElementById('errorMessage').textContent = '❌ ' + data.message;
                    document.getElementById('errorMessage').classList.add('show');
                    setTimeout(() => {
                        document.getElementById('errorMessage').classList.remove('show');
                    }, 3000);
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
        });

        function resendOTP() {
            const email = document.getElementById('email').value;
            
            const params = new URLSearchParams();
            params.append('action', 'sendOTP');
            params.append('email', email);

            fetch('ForgotPasswordServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: params.toString()
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    document.getElementById('successMessage').textContent = '✅ New code sent!';
                    document.getElementById('successMessage').classList.add('show');
                    setTimeout(() => document.getElementById('successMessage').classList.remove('show'), 3000);
                }
            });
        }
    </script>
</body>
</html>