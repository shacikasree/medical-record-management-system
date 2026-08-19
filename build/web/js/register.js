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

        function validateForm() {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            const errorAlert = document.getElementById('errorAlert');
            
            if (password !== confirmPassword) {
                errorAlert.textContent = '❌ Passwords do not match';
                errorAlert.style.display = 'block';
                return false;
            }

            if (password.length < 6) {
                errorAlert.textContent = '❌ Password must be at least 6 characters';
                errorAlert.style.display = 'block';
                return false;
            }

            return true;
        }

        window.onload = function() {
            const params = new URLSearchParams(window.location.search);
            const error = params.get('error');
            const msg = params.get('msg');
            
            const errorMessages = {
                'userExists': '❌ Username already exists',
                'emailExists': '❌ Email already registered',
                'passwordMismatch': '❌ Passwords do not match',
                'server': '❌ Server Error! Try again.',
                'invalidData': '❌ Invalid data provided'
            };
            
            if (error && errorMessages[error]) {
                const errorAlert = document.getElementById('errorAlert');
                errorAlert.textContent = errorMessages[error];
                errorAlert.style.display = 'block';
            }

            if (msg === 'success') {
                const successAlert = document.getElementById('successAlert');
                successAlert.textContent = '✅ Registration Successful! Redirecting to login...';
                successAlert.style.display = 'block';
                
                setTimeout(() => {
                    window.location.href = 'Login.jsp?msg=registered';
                }, 2000);
            }
        };