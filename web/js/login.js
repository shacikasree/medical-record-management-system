// Password Toggle Function
function togglePassword() {
    const field = document.getElementById('password');
    const eyeIcon = document.getElementById('eyeIcon');
    
    if (field.type === 'password') {
        field.type = 'text';
        eyeIcon.innerHTML = '<path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line>';
    } else {
        field.type = 'password';
        eyeIcon.innerHTML = '<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle>';
    }
}

// Error Message Handler
window.onload = function() {
    const params = new URLSearchParams(window.location.search);
    const error = params.get('error');
    const msg = params.get('msg');
    
    // All Error Messages
    const errorMessages = {
        'invalidUser': '❌ Incorrect Username',
        'invalidPass': '❌ Incorrect Password',
        'roleMismatch': '❌ Selected Role Incorrect',
        'emptyfields': '❌ All fields are required!',
        'inactive': '❌ Account is not active. Contact admin!',
        'invalidrole': '❌ Invalid role assigned. Contact admin!',
        'database': '❌ Database error. Please try again later!',
        'server': '❌ Server Error! Try again.',
        'invalid': '❌ Invalid credentials. Please try again!'
    };
    
    // Success Messages
    const successMessages = {
        'registered': '✅ Registration Successful! Please login',
        'logout': '✅ Logged out successfully!'
    };
    
    // Display Error Message
    if (error && errorMessages[error]) {
        const errorAlert = document.getElementById('errorMessage');
        if (errorAlert) {
            errorAlert.textContent = errorMessages[error];
            errorAlert.classList.add('show');
            console.log('Error displayed:', error);
        }
    }
    
    // Display Success Message
    if (msg && successMessages[msg]) {
        const successAlert = document.getElementById('successMessage');
        if (successAlert) {
            successAlert.textContent = successMessages[msg];
            successAlert.classList.add('show');
            
            // Auto-hide after 5 seconds
            setTimeout(() => {
                successAlert.classList.remove('show');
            }, 5000);
            
            console.log('Success message displayed:', msg);
        }
    }
};