
// ==================== NAVIGATION ====================
function showSection(sectionName) {
    const pages = document.querySelectorAll('.page');
    const navLinks = document.querySelectorAll('.nav-link');
    const pageTitle = document.getElementById('pageTitle');
    
    // Remove active class from all pages and nav links
    pages.forEach(page => page.classList.remove('active'));
    navLinks.forEach(link => link.classList.remove('active'));
    
    // Show target page
    const targetPage = document.getElementById(sectionName + 'Page');
    if(targetPage) {
        targetPage.classList.add('active');
    }
    
    // Update active nav link
    navLinks.forEach(link => {
        const onclick = link.getAttribute('onclick');
        if(onclick && onclick.includes(sectionName)) {
            link.classList.add('active');
        }
    });
    
    // Update page title
    const titles = {
        'dashboard': 'Dashboard',
        'appointments': 'My Appointments',
        'prescriptions': 'My Prescriptions',
        'medical-records': 'Medical Records',
        'profile': 'My Profile'
    };
    
    pageTitle.textContent = titles[sectionName] || 'Dashboard';
    
    // Close sidebar on mobile
    if(window.innerWidth <= 768) {
        document.getElementById('sidebar').classList.remove('active');
    }
}

// ==================== SIDEBAR TOGGLE ====================
function toggleSidebar() {
    document.getElementById('sidebar').classList.toggle('active');
}

// ==================== LOGOUT ====================
function logout() {
    if(confirm('Are you sure you want to logout?')) {
        window.location.href = 'Login.jsp';
    }
}

// ==================== FILTER APPOINTMENTS ====================
function filterAppointments(filter) {
    const cards = document.querySelectorAll('#appointmentsContainer .appointment-card');
    const tabs = document.querySelectorAll('#appointmentsPage .tab-btn');
    
    // Update active tab
    tabs.forEach(tab => tab.classList.remove('active'));
    event.target.classList.add('active');
    
    // Show/hide cards based on filter
    cards.forEach(card => {
        if(filter === 'all') {
            card.style.display = 'flex';
        } else {
            const status = card.getAttribute('data-status');
            card.style.display = status === filter ? 'flex' : 'none';
        }
    });
}

// ==================== FILTER PRESCRIPTIONS ====================
function filterPrescriptions(filter) {
    const cards = document.querySelectorAll('#prescriptionsContainer .prescription-card');
    const tabs = document.querySelectorAll('#prescriptionsPage .tab-btn');
    
    // Update active tab
    tabs.forEach(tab => tab.classList.remove('active'));
    event.target.classList.add('active');
    
    // Show/hide cards based on filter
    cards.forEach(card => {
        const status = card.getAttribute('data-status');
        card.style.display = status === filter ? 'block' : 'none';
    });
}

// ==================== CANCEL APPOINTMENT ====================
function cancelAppointment(id) {
    if(confirm('Are you sure you want to cancel this appointment?')) {
        // Show loading indicator
        showLoadingMessage('Cancelling appointment...');
        
        // Redirect to cancel servlet
        window.location.href = 'CancelAppointmentServlet?id=' + id;
    }
}

// ==================== MARK APPOINTMENT AS COMPLETED ====================
function markCompleted(id) {
    if(confirm('Mark this appointment as completed?')) {
        // Show loading indicator
        showLoadingMessage('Updating appointment status...');
        
        // Redirect to complete servlet
        window.location.href = 'CompleteAppointmentServlet?id=' + id;
    }
}

// ==================== DOWNLOAD PRESCRIPTION ====================
function downloadPrescription(id, medicineName) {
    console.log('===========================================');
    console.log('DOWNLOAD PRESCRIPTION');
    console.log('ID:', id);
    console.log('Medicine:', medicineName);
    console.log('===========================================');
    
    // Get the button that was clicked
    const btn = event.target.closest('button');
    const originalText = btn.innerHTML;
    
    // Show loading state
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Downloading...';
    btn.disabled = true;
    btn.style.opacity = '0.6';
    btn.style.cursor = 'not-allowed';
    
    // Create download URL
    const url = 'DownloadPrescriptionServlet?id=' + id;
    console.log('Download URL:', url);
    
    // Try to open in new window
    const downloadWindow = window.open(url, '_blank');
    
    // Check if popup was blocked
    if (!downloadWindow || downloadWindow.closed || typeof downloadWindow.closed === 'undefined') {
        console.log('Popup blocked, using direct download');
        // If popup blocked, use direct download
        window.location.href = url;
    } else {
        console.log('Download opened in new window');
    }
    
    // Reset button after 2 seconds
    setTimeout(function() {
        btn.innerHTML = originalText;
        btn.disabled = false;
        btn.style.opacity = '1';
        btn.style.cursor = 'pointer';
        console.log('Button reset');
    }, 2000);
}

// ==================== LOADING MESSAGE ====================
function showLoadingMessage(message) {
    // Create loading overlay
    const overlay = document.createElement('div');
    overlay.id = 'loadingOverlay';
    overlay.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 9999;
    `;
    
    overlay.innerHTML = `
        <div style="background: white; padding: 30px; border-radius: 12px; text-align: center; box-shadow: 0 10px 40px rgba(0,0,0,0.2);">
            <i class="fas fa-spinner fa-spin" style="font-size: 48px; color: #4f46e5; margin-bottom: 15px;"></i>
            <p style="font-size: 16px; color: #333; margin: 0;">${message}</p>
        </div>
    `;
    
    document.body.appendChild(overlay);
}

// ==================== CLOSE SIDEBAR ON OUTSIDE CLICK (MOBILE) ====================
document.addEventListener('click', function(e) {
    const sidebar = document.getElementById('sidebar');
    const menuToggle = document.getElementById('menuToggle');
    
    // Only on mobile screens
    if(window.innerWidth <= 768) {
        // If sidebar is open and click is outside sidebar and not on menu toggle
        if(sidebar.classList.contains('active') && 
           !sidebar.contains(e.target) && 
           e.target !== menuToggle &&
           !menuToggle.contains(e.target)) {
            sidebar.classList.remove('active');
        }
    }
});

// ==================== HIDE SUCCESS MESSAGE AFTER 5 SECONDS ====================
window.addEventListener('DOMContentLoaded', function() {
    const successMsg = document.querySelector('.success-message.show');
    if(successMsg) {
        // Auto hide after 5 seconds
        setTimeout(function() {
            successMsg.style.transition = 'opacity 0.5s ease';
            successMsg.style.opacity = '0';
            
            setTimeout(function() {
                successMsg.classList.remove('show');
                successMsg.style.opacity = '1';
            }, 500);
        }, 5000);
    }
});

// ==================== HANDLE WINDOW RESIZE ====================
window.addEventListener('resize', function() {
    const sidebar = document.getElementById('sidebar');
    
    // Close sidebar when resizing to desktop
    if(window.innerWidth > 768 && sidebar.classList.contains('active')) {
        sidebar.classList.remove('active');
    }
});

// ==================== SMOOTH SCROLL TO TOP ====================
function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

// ==================== PRINT PRESCRIPTION ====================
function printPrescription(id) {
    window.print();
}

// ==================== VIEW APPOINTMENT DETAILS ====================
function viewAppointmentDetails(id) {
    alert('View appointment details for ID: ' + id);
    // You can implement a modal or redirect to details page
}

// ==================== RESCHEDULE APPOINTMENT ====================
function rescheduleAppointment(id) {
    if(confirm('Redirect to reschedule page?')) {
        window.location.href = 'PatientAppoinment.jsp?reschedule=' + id;
    }
}

// ==================== SEARCH FUNCTIONALITY (IF NEEDED) ====================
function searchAppointments() {
    const searchInput = document.getElementById('appointmentSearch');
    if(!searchInput) return;
    
    const searchTerm = searchInput.value.toLowerCase();
    const cards = document.querySelectorAll('#appointmentsContainer .appointment-card');
    
    cards.forEach(card => {
        const text = card.textContent.toLowerCase();
        if(text.includes(searchTerm)) {
            card.style.display = 'flex';
        } else {
            card.style.display = 'none';
        }
    });
}

function searchPrescriptions() {
    const searchInput = document.getElementById('prescriptionSearch');
    if(!searchInput) return;
    
    const searchTerm = searchInput.value.toLowerCase();
    const cards = document.querySelectorAll('#prescriptionsContainer .prescription-card');
    
    cards.forEach(card => {
        const text = card.textContent.toLowerCase();
        if(text.includes(searchTerm)) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
}

// ==================== KEYBOARD SHORTCUTS ====================
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + K to focus search (if you add search)
    if((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        const searchInput = document.querySelector('input[type="search"]');
        if(searchInput) searchInput.focus();
    }
    
    // ESC to close sidebar on mobile
    if(e.key === 'Escape') {
        const sidebar = document.getElementById('sidebar');
        if(sidebar.classList.contains('active')) {
            sidebar.classList.remove('active');
        }
    }
});

// ==================== NOTIFICATION BELL CLICK ====================
document.addEventListener('DOMContentLoaded', function() {
    const notificationBell = document.querySelector('.notification');
    if(notificationBell) {
        notificationBell.addEventListener('click', function() {
            alert('You have ' + this.querySelector('.notification-badge').textContent + ' upcoming appointments');
            // You can implement a dropdown notification panel here
        });
    }
});
// ==================== NAVIGATION ====================
function showSection(sectionName) {
    const pages = document.querySelectorAll('.page');
    const navLinks = document.querySelectorAll('.nav-link');
    const pageTitle = document.getElementById('pageTitle');
    
    // Remove active class from all pages and nav links
    pages.forEach(page => page.classList.remove('active'));
    navLinks.forEach(link => link.classList.remove('active'));
    
    // Show target page
    const targetPage = document.getElementById(sectionName + 'Page');
    if(targetPage) {
        targetPage.classList.add('active');
    }
    
    // Update active nav link
    navLinks.forEach(link => {
        const onclick = link.getAttribute('onclick');
        if(onclick && onclick.includes(sectionName)) {
            link.classList.add('active');
        }
    });
    
    // Update page title
    const titles = {
        'dashboard': 'Dashboard',
        'appointments': 'My Appointments',
        'prescriptions': 'My Prescriptions',
        'medical-records': 'Medical Records',
        'profile': 'My Profile'
    };
    
    pageTitle.textContent = titles[sectionName] || 'Dashboard';
    
    // Close sidebar on mobile
    if(window.innerWidth <= 768) {
        document.getElementById('sidebar').classList.remove('active');
    }
}

// ==================== CONSOLE LOG FOR DEBUGGING ====================
console.log('Patient Dashboard JavaScript Loaded Successfully');
console.log('Version: 1.0');
console.log('All functions initialized');