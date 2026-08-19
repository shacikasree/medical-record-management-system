// ========================================================================================
// HOSPITAL MANAGEMENT SYSTEM - ADMIN DASHBOARD - DATABASE ONLY VERSION
// ========================================================================================

console.log('🔄 JavaScript file loaded!');

// Chart instances
let appointmentsChartInstance = null;
let departmentChartInstance = null;

// Wait for DOM to be fully loaded
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initDashboard);
} else {
    initDashboard();
}

function initDashboard() {
    console.log('✅ Dashboard initializing...');
    
    // Initialize navigation
    initializeNavigation();
    
    // Initialize menu toggle
    const menuToggle = document.getElementById('menuToggle');
    if (menuToggle) {
        menuToggle.addEventListener('click', function() {
            document.getElementById('sidebar').classList.toggle('collapsed');
        });
    }
    
    // Load dashboard
    loadDashboard();
    
    // Setup event listeners
    setupEventListeners();
    
    console.log('✅ Dashboard loaded successfully!');
}

// Navigation
function initializeNavigation() {
    const menuItems = document.querySelectorAll('.menu-item');
    console.log('Found menu items:', menuItems.length);
    
    menuItems.forEach(function(item) {
        item.addEventListener('click', function() {
            const sectionName = this.getAttribute('data-section');
            console.log('Menu clicked:', sectionName);
            
            // Remove active from all
            menuItems.forEach(function(mi) {
                mi.classList.remove('active');
            });
            
            // Add active to clicked
            this.classList.add('active');
            
            // Hide all sections
            const allSections = document.querySelectorAll('.content-section');
            allSections.forEach(function(section) {
                section.classList.remove('active');
            });
            
            // Show selected section
            const sectionId = sectionName + '-section';
            const targetSection = document.getElementById(sectionId);
            
            console.log('Looking for:', sectionId, 'Found:', targetSection ? 'YES' : 'NO');
            
            if (targetSection) {
                targetSection.classList.add('active');
                console.log('✅ Showing section:', sectionId);
                loadSectionData(sectionName);
            } else {
                console.error('❌ Section not found:', sectionId);
            }
        });
    });
}

// Load section data
function loadSectionData(section) {
    console.log('Loading data for:', section);
    
    switch(section) {
        case 'dashboard':
            loadDashboard();
            break;
        case 'doctors':
            console.log('Doctors section - data already loaded from database via JSP');
            break;
        case 'patients':
            console.log('Patients section - data already loaded from database via JSP');
            break;
        case 'appointments':
            console.log('Appointments section - data already loaded from database via JSP');
            break;
        case 'departments':
            console.log('Departments section - data already loaded from database via JSP');
            break;
        case 'prescriptions':
            console.log('Prescriptions section - data already loaded from database via JSP');
            break;
        case 'users':
            console.log('Users section - data already loaded from database via JSP');
            break;
        case 'settings':
            console.log('Settings section - data already loaded from database via JSP');
            break;
    }
}

// Load Dashboard
function loadDashboard() {
    console.log('Dashboard loaded - using database data only');
}

// ========================================================================================
// DOCTOR MANAGEMENT FUNCTIONS
// ========================================================================================
// ============================================================================
// DOCTOR MANAGEMENT JAVASCRIPT - FINAL COMPLETE CODE
// YOUR EXISTING JS IS PERFECT - NO CHANGES NEEDED!
// ============================================================================

let deleteDoctorId = null;

function openAddDoctorModal() {
    const form = document.getElementById('doctorForm');
    if (form) form.reset();
    
    const hiddenId = document.querySelector('#doctorForm input[name="doctorId"]');
    if (hiddenId) hiddenId.remove();
    
    const passwordField = document.getElementById('doctorPassword');
    if (passwordField) passwordField.value = 'Doctor@123';
    
    const modalTitle = document.querySelector('#doctorModal h2');
    if (modalTitle) modalTitle.textContent = 'Add New Doctor';
    
    const submitBtn = document.querySelector('#doctorForm button[type="submit"]');
    if (submitBtn) {
        submitBtn.innerHTML = '<i class="fas fa-save"></i> Add Doctor';
    }
    
    openDoctorModal();
}

function openDoctorModal() {
    const modal = document.getElementById('doctorModal');
    if (modal) {
        modal.classList.add('active');
        modal.style.display = 'flex';
    }
}

function closeDoctorModal() {
    const modal = document.getElementById('doctorModal');
    if (modal) {
        modal.classList.remove('active');
        modal.style.display = 'none';
    }
    
    const form = document.getElementById('doctorForm');
    if (form) form.reset();
    
    const hiddenId = document.querySelector('#doctorForm input[name="doctorId"]');
    if (hiddenId) hiddenId.remove();
}

function editDoctor(id) {
    fetch('AdminServlet?action=getDoctorData&id=' + id)
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                document.getElementById('doctorName').value = data.fullname || '';
                document.getElementById('doctorEmail').value = data.email || '';
                document.getElementById('doctorPhone').value = data.phone || '';
                document.getElementById('doctorSpecialty').value = data.specialty || '';
                document.getElementById('doctorQualification').value = data.qualification || '';
                document.getElementById('doctorExperience').value = data.experience || '0';
                
                const deptField = document.getElementById('doctorDepartment');
                if (deptField) deptField.value = data.department || '';
                
                const passwordField = document.getElementById('doctorPassword');
                if (passwordField) passwordField.value = '';
                
                let hiddenId = document.querySelector('#doctorForm input[name="doctorId"]');
                if (!hiddenId) {
                    hiddenId = document.createElement('input');
                    hiddenId.type = 'hidden';
                    hiddenId.name = 'doctorId';
                    const form = document.getElementById('doctorForm');
                    form.insertBefore(hiddenId, form.firstChild);
                }
                hiddenId.value = id;
                
                const modalTitle = document.querySelector('#doctorModal h2');
                if (modalTitle) modalTitle.textContent = 'Edit Doctor';
                
                const submitBtn = document.querySelector('#doctorForm button[type="submit"]');
                if (submitBtn) {
                    submitBtn.innerHTML = '<i class="fas fa-save"></i> Update Doctor';
                }
                
                openDoctorModal();
            } else {
                showNotification(data.message || 'Error loading doctor data', 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('Error loading doctor data', 'error');
        });
}

function viewDoctor(id) {
    fetch('AdminServlet?action=getDoctorData&id=' + id)
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                const details = `Doctor Details:

Name: ${data.fullname}
Specialty: ${data.specialty}
Email: ${data.email}
Phone: ${data.phone}
Qualification: ${data.qualification}
Experience: ${data.experience} years
Department: ${data.department || 'N/A'}
Patients: ${data.patientCount || 0}`;
                alert(details);
            } else {
                showNotification('Error loading doctor details', 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('Error loading doctor details', 'error');
        });
}

function deleteDoctor(id) {
    deleteDoctorId = id;
    const modal = document.getElementById('deleteConfirmModal');
    if (modal) {
        modal.classList.add('active');
        modal.style.display = 'flex';
    }
}

function closeDeleteModal() {
    const modal = document.getElementById('deleteConfirmModal');
    if (modal) {
        modal.classList.remove('active');
        modal.style.display = 'none';
    }
    deleteDoctorId = null;
}

function confirmDeleteDoctor() {
    if (!deleteDoctorId) return;
    
    fetch('AdminServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'action=deleteDoctor&id=' + deleteDoctorId
    })
    .then(response => response.json())
    .then(data => {
        closeDeleteModal();
        if (data.success) {
            showNotification(data.message || 'Doctor deleted successfully!', 'success');
            setTimeout(() => window.location.reload(), 1500);
        } else {
            showNotification(data.message || 'Error deleting doctor', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        closeDeleteModal();
        showNotification('Error deleting doctor', 'error');
    });
}

function saveDoctorForm(e) {
    e.preventDefault();
    
    const doctorId = document.querySelector('#doctorForm input[name="doctorId"]')?.value || '';
    const fullname = document.getElementById('doctorName').value.trim();
    const email = document.getElementById('doctorEmail').value.trim();
    const phone = document.getElementById('doctorPhone').value.trim();
    const password = document.getElementById('doctorPassword').value.trim();
    const specialty = document.getElementById('doctorSpecialty').value.trim();
    const qualification = document.getElementById('doctorQualification').value.trim();
    const experience = document.getElementById('doctorExperience').value.trim();
    const department = document.getElementById('doctorDepartment')?.value.trim() || '';
    
    const action = doctorId ? 'updateDoctor' : 'addDoctor';
    
    if (!fullname) {
        showNotification('Full name is required', 'error');
        return;
    }
    if (!email) {
        showNotification('Email is required', 'error');
        return;
    }
    if (!action.includes('update') && !password) {
        showNotification('Password is required for new doctors', 'error');
        return;
    }
    if (!specialty) {
        showNotification('Specialty is required', 'error');
        return;
    }
    if (!qualification) {
        showNotification('Qualification is required', 'error');
        return;
    }
    if (!experience || isNaN(experience)) {
        showNotification('Please enter a valid experience value', 'error');
        return;
    }
    
    let bodyParams = 'action=' + encodeURIComponent(action) +
                     '&fullname=' + encodeURIComponent(fullname) +
                     '&email=' + encodeURIComponent(email) +
                     '&phone=' + encodeURIComponent(phone) +
                     '&specialty=' + encodeURIComponent(specialty) +
                     '&qualification=' + encodeURIComponent(qualification) +
                     '&experience=' + encodeURIComponent(experience) +
                     '&department=' + encodeURIComponent(department);
    
    if (password) {
        bodyParams += '&password=' + encodeURIComponent(password);
    }
    if (doctorId) {
        bodyParams += '&id=' + encodeURIComponent(doctorId);
    }
    
    fetch('AdminServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: bodyParams
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            const message = doctorId ? 'Doctor updated successfully!' : 'Doctor added successfully!';
            showNotification(data.message || message, 'success');
            closeDoctorModal();
            setTimeout(() => window.location.reload(), 1500);
        } else {
            showNotification(data.message || 'Error saving doctor', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('An error occurred while saving the doctor', 'error');
    });
}

function filterDoctors() {
    const searchValue = document.getElementById('doctorSearch')?.value.toLowerCase() || '';
    const specialtyValue = document.getElementById('specialtyFilter')?.value || '';
    const rows = document.querySelectorAll('#doctorsTableBody .doctor-row');
    
    let visibleCount = 0;
    
    rows.forEach(row => {
        const name = (row.getAttribute('data-name') || '').toLowerCase();
        const specialty = row.getAttribute('data-specialty') || '';
        
        const matchesSearch = name.includes(searchValue);
        const matchesSpecialty = !specialtyValue || specialty === specialtyValue;
        
        if (matchesSearch && matchesSpecialty) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    });
    
    const noResultsRow = document.querySelector('.no-results-row');
    if (visibleCount === 0 && rows.length > 0) {
        if (!noResultsRow) {
            const tbody = document.getElementById('doctorsTableBody');
            const newRow = document.createElement('tr');
            newRow.className = 'no-results-row';
            newRow.innerHTML = `
                <td colspan="9" style="text-align: center; padding: 40px; color: #94a3b8;">
                    <i class="fas fa-search" style="font-size: 48px; display: block; margin-bottom: 10px; opacity: 0.3;"></i>
                    No doctors match your search criteria
                </td>
            `;
            tbody.appendChild(newRow);
        }
    } else {
        if (noResultsRow) noResultsRow.remove();
    }
}

function showNotification(message, type) {
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(n => n.remove());
    
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    
    const icon = type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle';
    notification.innerHTML = `
        <i class="fas ${icon}"></i>
        <span>${message}</span>
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.classList.add('fade-out');
        setTimeout(() => notification.remove(), 300);
    }, 4000);
}

window.onclick = function(event) {
    const doctorModal = document.getElementById('doctorModal');
    const deleteModal = document.getElementById('deleteConfirmModal');
    
    if (event.target === doctorModal) {
        closeDoctorModal();
    }
    if (event.target === deleteModal) {
        closeDeleteModal();
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const doctorForm = document.getElementById('doctorForm');
    if (doctorForm) {
        doctorForm.addEventListener('submit', saveDoctorForm);
    }
    
    console.log('✅ Doctor management functions loaded');
});
// ========================================================================================
// PATIENT MANAGEMENT FUNCTIONS
// ========================================================================================
// ========================================================================================
// PATIENT MANAGEMENT FUNCTIONS
// ========================================================================================

// Open Add Patient Modal
function openAddPatientModal() {
    const form = document.getElementById('patientForm');
    if (form) form.reset();
    
    document.getElementById('patientId').value = '';
    
    const modalTitle = document.getElementById('patientModalTitle');
    if (modalTitle) modalTitle.textContent = 'Add New Patient';
    
    const submitBtn = document.querySelector('#patientForm button[type="submit"]');
    if (submitBtn) submitBtn.innerHTML = '<i class="fas fa-save"></i> Add Patient';
    
    const modal = document.getElementById('patientModal');
    if (modal) {
        modal.classList.add('active');
        modal.style.display = 'flex';
    }
    
    console.log('✅ Add Patient Modal opened');
}

// Close Patient Modal
function closePatientModal() {
    const modal = document.getElementById('patientModal');
    if (modal) {
        modal.classList.remove('active');
        modal.style.display = 'none';
    }
    
    const form = document.getElementById('patientForm');
    if (form) form.reset();
}

// View Patient
function viewPatient(id) {
    console.log('Viewing patient:', id);
    fetch('AdminServlet?action=getPatientData&id=' + id)
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if (data.success) {
                alert('Patient Details:\n\nName: ' + data.name +
                      '\nAge: ' + data.age + ' years' +
                      '\nGender: ' + data.gender +
                      '\nEmail: ' + data.email +
                      '\nPhone: ' + data.phone +
                      '\nAddress: ' + (data.address || 'N/A') +
                      '\nBlood Group: ' + (data.bloodGroup || 'N/A') +
                      '\nStatus: ' + data.status +
                      '\nAppointments: ' + (data.appointmentCount || 0));
            } else {
                showNotification(data.message || 'Error loading patient details', 'error');
            }
        })
        .catch(function(err) {
            console.error('Error:', err);
            showNotification('Error loading patient details', 'error');
        });
}

// Edit Patient
function editPatient(id) {
    console.log('Editing patient:', id);
    fetch('AdminServlet?action=getPatientData&id=' + id)
        .then(function(response) { return response.json(); })
        .then(function(data) {
            console.log('Patient data received:', data);
            if (data.success) {
                // Fill form fields
                document.getElementById('patientName').value = data.name || '';
                document.getElementById('patientAge').value = data.age || '';
                document.getElementById('patientGender').value = data.gender || '';
                document.getElementById('patientPhone').value = data.phone || '';
                document.getElementById('patientEmail').value = data.email || '';
                document.getElementById('patientAddress').value = data.address || '';
                document.getElementById('patientBloodGroup').value = data.bloodGroup || '';
                
                // Set hidden ID
                document.getElementById('patientId').value = id;
                
                // Update title and button
                var modalTitle = document.getElementById('patientModalTitle');
                if (modalTitle) modalTitle.textContent = 'Edit Patient';
                
                var submitBtn = document.querySelector('#patientForm button[type="submit"]');
                if (submitBtn) submitBtn.innerHTML = '<i class="fas fa-save"></i> Update Patient';
                
                // Open modal
                var modal = document.getElementById('patientModal');
                if (modal) {
                    modal.classList.add('active');
                    modal.style.display = 'flex';
                }
            } else {
                showNotification(data.message || 'Error loading patient data', 'error');
            }
        })
        .catch(function(err) {
            console.error('Error:', err);
            showNotification('Error loading patient data', 'error');
        });
}

// Block / Unblock Patient
function togglePatientStatus(id) {
    if (!confirm('Are you sure you want to change this patient status?')) {
        return;
    }
    
    fetch('AdminServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'action=togglePatientStatus&id=' + id
    })
    .then(function(response) { return response.json(); })
    .then(function(data) {
        if (data.success) {
            showNotification(data.message || 'Patient status updated!', 'success');
            setTimeout(function() { window.location.reload(); }, 1500);
        } else {
            showNotification(data.message || 'Error updating status', 'error');
        }
    })
    .catch(function(err) {
        console.error('Error:', err);
        showNotification('Error updating patient status', 'error');
    });
}

// Save Patient Form (Add / Update)
function savePatientForm(e) {
    e.preventDefault();
    console.log('Patient form submitted');
    
    var patientId = document.getElementById('patientId').value || '';
    var name = document.getElementById('patientName').value.trim();
    var age = document.getElementById('patientAge').value.trim();
    var gender = document.getElementById('patientGender').value.trim();
    var phone = document.getElementById('patientPhone').value.trim();
    var email = document.getElementById('patientEmail').value.trim();
    var address = document.getElementById('patientAddress').value.trim();
    var bloodGroup = document.getElementById('patientBloodGroup').value.trim();
    
    var action = patientId ? 'updatePatient' : 'addPatient';
    console.log('Action:', action, 'Patient ID:', patientId);
    
    // Validation
    if (!name) { showNotification('Name is required', 'error'); return; }
    if (!age || isNaN(age)) { showNotification('Valid age is required', 'error'); return; }
    if (!gender) { showNotification('Gender is required', 'error'); return; }
    if (!email) { showNotification('Email is required', 'error'); return; }
    if (!phone) { showNotification('Phone is required', 'error'); return; }
    
    // Build params
    var bodyParams = 'action=' + encodeURIComponent(action) +
                     '&name=' + encodeURIComponent(name) +
                     '&age=' + encodeURIComponent(age) +
                     '&gender=' + encodeURIComponent(gender) +
                     '&phone=' + encodeURIComponent(phone) +
                     '&email=' + encodeURIComponent(email) +
                     '&address=' + encodeURIComponent(address) +
                     '&bloodGroup=' + encodeURIComponent(bloodGroup) +
                     '&password=' + encodeURIComponent('Patient@123');
    
    if (patientId) {
        bodyParams += '&id=' + encodeURIComponent(patientId);
    }
    
    console.log('Sending:', bodyParams);
    
    fetch('AdminServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: bodyParams
    })
    .then(function(response) { return response.json(); })
    .then(function(data) {
        console.log('Response:', data);
        if (data.success) {
            showNotification(data.message || 'Patient saved successfully!', 'success');
            closePatientModal();
            setTimeout(function() { window.location.reload(); }, 1500);
        } else {
            showNotification(data.message || 'Error saving patient', 'error');
        }
    })
    .catch(function(err) {
        console.error('Error:', err);
        showNotification('Error saving patient', 'error');
    });
}

// Filter Patients
function filterPatients() {
    var searchValue = document.getElementById('patientSearch') ? document.getElementById('patientSearch').value.toLowerCase() : '';
    var statusValue = document.getElementById('patientStatusFilter') ? document.getElementById('patientStatusFilter').value : '';
    var rows = document.querySelectorAll('#patientsTableBody .patient-row');
    
    var visibleCount = 0;
    
    rows.forEach(function(row) {
        var name = (row.getAttribute('data-name') || '').toLowerCase();
        var status = row.getAttribute('data-status') || '';
        
        var matchesSearch = name.includes(searchValue);
        var matchesStatus = !statusValue || status === statusValue;
        
        if (matchesSearch && matchesStatus) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    });
}

// Initialize Patient Form
document.addEventListener('DOMContentLoaded', function() {
    var patientForm = document.getElementById('patientForm');
    if (patientForm) {
        patientForm.addEventListener('submit', savePatientForm);
        console.log('✅ Patient form submit handler attached');
    }
    
    // Close patient modal on outside click
    var patientModal = document.getElementById('patientModal');
    if (patientModal) {
        patientModal.addEventListener('click', function(e) {
            if (e.target === patientModal) {
                closePatientModal();
            }
        });
    }
    
    console.log('✅ Patient management functions loaded');
});

// ========================================================================================
// APPOINTMENT MANAGEMENT FUNCTIONS
// ========================================================================================
// ========================================================================================
// APPOINTMENT MANAGEMENT - UPDATED FOR CancelAppointmentServlet
// ========================================================================================

function showNotification(message, type) {
    var notification = document.createElement('div');
    notification.textContent = message;
    notification.style.cssText = 'position: fixed; top: 20px; right: 20px; padding: 15px 25px; ' +
                                 'background: ' + (type === 'success' ? '#10b981' : '#ef4444') + '; ' +
                                 'color: white; border-radius: 8px; z-index: 9999; font-weight: 600; ' +
                                 'box-shadow: 0 4px 12px rgba(0,0,0,0.15);';
    document.body.appendChild(notification);
    setTimeout(function() { document.body.removeChild(notification); }, 3000);
}

function viewAppointment(id) {
    console.log('Viewing appointment:', id);
    fetch('AdminServlet?action=getAppointmentData&id=' + id)
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if (data.success) {
                alert('Appointment Details:\n\n' +
                      'Appointment ID: ' + data.id + '\n' +
                      'Patient: ' + data.patientName + '\n' +
                      'Doctor: ' + data.doctorName + '\n' +
                      'Department: ' + data.department + '\n' +
                      'Date: ' + data.date + '\n' +
                      'Time: ' + data.time + '\n' +
                      'Status: ' + data.status + '\n' +
                      'Notes: ' + (data.notes || 'No notes available'));
            } else {
                showNotification(data.message || 'Error loading appointment details', 'error');
            }
        })
        .catch(function(err) {
            console.error('Error:', err);
            showNotification('Error loading appointment details', 'error');
        });
}

// UPDATED: Uses CancelAppointmentServlet with GET request (redirect)
function cancelAppointment(id) {
    if (!confirm('Are you sure you want to cancel this appointment?')) {
        return;
    }
    
    console.log('Cancelling appointment:', id);
    console.log('Redirecting to CancelAppointmentServlet...');
    
    // Direct redirect to your existing servlet
    window.location.href = 'CancelAppointmentServlet?id=' + id;
}

function completeAppointment(id) {
    if (!confirm('Mark this appointment as completed?')) {
        return;
    }
    
    console.log('Completing appointment:', id);
    
    fetch('AdminServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'action=completeAppointment&id=' + id
    })
    .then(function(response) { return response.json(); })
    .then(function(data) {
        console.log('Complete response:', data);
        if (data.success) {
            showNotification(data.message || 'Appointment completed successfully!', 'success');
            setTimeout(function() { window.location.reload(); }, 1500);
        } else {
            showNotification(data.message || 'Error completing appointment', 'error');
        }
    })
    .catch(function(err) {
        console.error('Error:', err);
        showNotification('Error completing appointment', 'error');
    });
}

function filterAppointments() {
    var dateValue = document.getElementById('appointmentDate') ? document.getElementById('appointmentDate').value : '';
    var statusValue = document.getElementById('appointmentStatus') ? document.getElementById('appointmentStatus').value : '';
    var deptValue = document.getElementById('appointmentDept') ? document.getElementById('appointmentDept').value : '';
    var rows = document.querySelectorAll('#appointmentsTableBody .appointment-row');
    
    var visibleCount = 0;
    
    rows.forEach(function(row) {
        var date = row.getAttribute('data-date') || '';
        var status = row.getAttribute('data-status') || '';
        var dept = row.getAttribute('data-dept') || '';
        
        var matchesDate = !dateValue || date === dateValue;
        var matchesStatus = !statusValue || status === statusValue;
        var matchesDept = !deptValue || dept === deptValue;
        
        if (matchesDate && matchesStatus && matchesDept) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    });
    
    console.log('Filtered appointments:', visibleCount);
}

// ========================================================================================
// INITIALIZATION
// ========================================================================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Initializing Admin Dashboard...');
    
    // ==================== MENU NAVIGATION ====================
    var menuItems = document.querySelectorAll('.menu-item');
    console.log('📋 Found menu items:', menuItems.length);
    
    menuItems.forEach(function(item) {
        item.addEventListener('click', function() {
            var sectionName = this.getAttribute('data-section');
            console.log('🖱️ Menu clicked:', sectionName);
            
            if (!sectionName) {
                console.error('❌ No data-section attribute');
                return;
            }
            
            menuItems.forEach(function(mi) {
                mi.classList.remove('active');
            });
            
            this.classList.add('active');
            
            var allSections = document.querySelectorAll('.content-section');
            allSections.forEach(function(section) {
                section.classList.remove('active');
                section.style.display = 'none';
            });
            
            var targetSection = document.getElementById(sectionName + '-section');
            if (targetSection) {
                targetSection.classList.add('active');
                targetSection.style.display = 'block';
                console.log('✅ Showing section:', sectionName);
            } else {
                console.error('❌ Section not found:', sectionName + '-section');
            }
        });
    });
    
    // ==================== MENU TOGGLE ====================
    var menuToggle = document.getElementById('menuToggle');
    var sidebar = document.getElementById('sidebar');
    if (menuToggle && sidebar) {
        menuToggle.addEventListener('click', function() {
            sidebar.classList.toggle('collapsed');
        });
    }
    
    // ==================== APPOINTMENT FILTERS ====================
    var appointmentDate = document.getElementById('appointmentDate');
    if (appointmentDate) {
        appointmentDate.addEventListener('change', filterAppointments);
    }
    
    var appointmentStatus = document.getElementById('appointmentStatus');
    if (appointmentStatus) {
        appointmentStatus.addEventListener('change', filterAppointments);
    }
    
    var appointmentDept = document.getElementById('appointmentDept');
    if (appointmentDept) {
        appointmentDept.addEventListener('change', filterAppointments);
    }
    
    // ==================== CHECK FOR SUCCESS/ERROR MESSAGES ====================
    var urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('success') === 'cancelled') {
        showNotification('Appointment cancelled successfully!', 'success');
    } else if (urlParams.get('error') === 'notFound') {
        showNotification('Appointment not found', 'error');
    } else if (urlParams.get('error') === 'dbError') {
        showNotification('Database error occurred', 'error');
    }
    
    console.log('✅ Admin Dashboard Initialized!');
});

// ========================================================================================
// DEPARTMENT MANAGEMENT FUNCTIONS - FINAL WORKING VERSION
// ========================================================================================

// Get list of all doctors for department head dropdown
let doctorsList = [];

// Load doctors list when page loads
document.addEventListener('DOMContentLoaded', function() {
    console.log('=== Department Management Initialized ===');
    loadDoctorsForDepartment();
    initializeDepartmentForm();
});

function loadDoctorsForDepartment() {
    console.log('Loading doctors for department dropdown...');
    
    // Check if doctorsList is already initialized from JSP
    if (typeof window.doctorsList !== 'undefined' && window.doctorsList.length > 0) {
        doctorsList = window.doctorsList;
        console.log('Doctors loaded from JSP variable:', doctorsList);
        return;
    }
    
    // Fallback: Try to get doctors from the page's table
    const doctorsTable = document.querySelector('#doctors-section table tbody');
    if (doctorsTable) {
        const rows = doctorsTable.querySelectorAll('tr');
        doctorsList = [];
        rows.forEach((row, index) => {
            const cells = row.cells;
            if (cells.length > 0) {
                // Get ID from the edit button's onclick attribute
                const editBtn = row.querySelector('.btn-edit, button[onclick*="editDoctor"]');
                if (editBtn) {
                    const onclickAttr = editBtn.getAttribute('onclick');
                    const idMatch = onclickAttr.match(/editDoctor\((\d+)\)/);
                    const id = idMatch ? idMatch[1] : null;
                    const name = cells[0]?.textContent?.trim();
                    if (id && name && name !== 'No doctors found') {
                        doctorsList.push({ id, name });
                        console.log(`Doctor ${index + 1}: ID=${id}, Name=${name}`);
                    }
                }
            }
        });
        console.log('Total doctors loaded from table:', doctorsList.length);
    } else {
        console.warn('Doctors table not found on page');
    }
    
    if (doctorsList.length === 0) {
        console.warn('No doctors found! The department head dropdown will be empty.');
    }
}

// Open Department Modal
function openDepartmentModal(id = null) {
    console.log('Opening department modal, ID:', id);
    
    const modal = document.getElementById('departmentModal');
    const modalTitle = document.getElementById('departmentModalTitle');
    const form = document.getElementById('departmentForm');
    
    if (!modal || !modalTitle || !form) {
        console.error('Modal elements not found!');
        alert('Error: Modal elements not found. Please check your HTML.');
        return;
    }
    
    if (id) {
        modalTitle.textContent = 'Edit Department';
        loadDepartmentData(id);
    } else {
        modalTitle.textContent = 'Add New Department';
        form.reset();
        document.getElementById('departmentId').value = '';
        
        // Populate department head dropdown
        populateDepartmentHeadDropdown();
    }
    
    modal.classList.add('active');
}

// Close Department Modal
function closeDepartmentModal() {
    const modal = document.getElementById('departmentModal');
    if (modal) {
        modal.classList.remove('active');
    }
    const form = document.getElementById('departmentForm');
    if (form) form.reset();
}

// Edit Department
function editDepartment(id) {
    console.log('Edit department called with ID:', id);
    openDepartmentModal(id);
}

// Populate Department Head Dropdown
function populateDepartmentHeadDropdown(selectedId = null) {
    console.log('Populating department head dropdown, selected ID:', selectedId);
    
    const select = document.getElementById('departmentHead');
    if (!select) {
        console.error('Department head select element not found!');
        return;
    }
    
    // Clear existing options except the first one
    select.innerHTML = '<option value="">-- Select Department Head --</option>';
    
    console.log('Available doctors:', doctorsList.length);
    
    // Add doctors as options
    doctorsList.forEach((doctor, index) => {
        const option = document.createElement('option');
        option.value = doctor.id;
        option.textContent = doctor.name;
        if (selectedId && doctor.id == selectedId) {
            option.selected = true;
            console.log('Selected doctor:', doctor.name);
        }
        select.appendChild(option);
    });
    
    console.log(`Added ${doctorsList.length} doctors to dropdown`);
}

// Load Department Data for Editing
function loadDepartmentData(id) {
    console.log('Loading department data for ID:', id);
    
    fetch('AdminServlet?action=getDepartmentData&id=' + id)
        .then(response => {
            console.log('Response status:', response.status);
            return response.json();
        })
        .then(data => {
            console.log('Department data received:', data);
            if (data.success) {
                document.getElementById('departmentId').value = data.id;
                document.getElementById('departmentName').value = data.name || '';
                document.getElementById('departmentDescription').value = data.description || '';
                
                // Populate dropdown and set selected value
                populateDepartmentHeadDropdown(data.headId);
            } else {
                showNotification(data.message || 'Error loading department data', 'danger');
            }
        })
        .catch(error => {
            console.error('Error loading department data:', error);
            showNotification('Error loading department data', 'danger');
        });
}

// Delete Department
function deleteDepartment(id) {
    console.log('Delete department called with ID:', id);
    
    if (confirm('Are you sure you want to delete this department? This action cannot be undone.')) {
        console.log('User confirmed deletion');
        
        // Create FormData with action parameter
        const formData = new FormData();
        formData.append('action', 'deleteDepartment');
        formData.append('id', id);
        
        fetch('AdminServlet', { 
            method: 'POST',
            body: formData
        })
        .then(response => {
            console.log('Delete response status:', response.status);
            return response.json();
        })
        .then(data => {
            console.log('Delete response:', data);
            if (data.success) {
                showNotification('Department deleted successfully!', 'success');
                setTimeout(() => location.reload(), 1500);
            } else {
                showNotification(data.message || 'Error deleting department', 'danger');
            }
        })
        .catch(error => {
            console.error('Error deleting department:', error);
            showNotification('Error deleting department', 'danger');
        });
    } else {
        console.log('User cancelled deletion');
    }
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('departmentModal');
    if (event.target === modal) {
        closeDepartmentModal();
    }
}

// Initialize Form Submit Handler
function initializeDepartmentForm() {
    const deptForm = document.getElementById('departmentForm');
    if (!deptForm) {
        console.warn('Department form not found!');
        return;
    }
    
    console.log('Department form found, attaching submit handler');
    
    deptForm.addEventListener('submit', function(e) {
        e.preventDefault();
        console.log('=== Department Form Submitted ===');
        
        const departmentId = document.getElementById('departmentId').value;
        const departmentName = document.getElementById('departmentName').value;
        const departmentDescription = document.getElementById('departmentDescription').value;
        const departmentHead = document.getElementById('departmentHead').value;
        
        // Determine action based on whether we have an ID
        const action = departmentId ? 'updateDepartment' : 'addDepartment';
        
        console.log('Form Data:', {
            action: action,
            id: departmentId || 'NEW',
            name: departmentName,
            description: departmentDescription,
            headId: departmentHead
        });
        
        // Validation
        if (!departmentName || departmentName.trim().length < 2) {
            showNotification('Department name must be at least 2 characters', 'danger');
            return;
        }
        
        // CRITICAL: Create FormData correctly with URLSearchParams
        const params = new URLSearchParams();
        params.append('action', action);
        params.append('name', departmentName.trim());
        params.append('description', departmentDescription.trim());
        params.append('headId', departmentHead || '');
        
        // If updating, add the ID
        if (departmentId) {
            params.append('id', departmentId);
        }
        
        // Log what we're sending
        console.log('Sending to server:');
        for (let pair of params.entries()) {
            console.log(`  ${pair[0]}: ${pair[1]}`);
        }
        
        fetch('AdminServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: params.toString()
        })
        .then(response => {
            console.log('Server response status:', response.status);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Server response data:', data);
            if (data.success) {
                showNotification(data.message || 'Department saved successfully!', 'success');
                closeDepartmentModal();
                setTimeout(() => {
                    console.log('Reloading page...');
                    location.reload();
                }, 1500);
            } else {
                showNotification(data.message || 'Error saving department', 'danger');
            }
        })
        .catch(error => {
            console.error('Fetch error:', error);
            showNotification('Error communicating with server: ' + error.message, 'danger');
        });
    });
}

// Notification System
function showNotification(message, type) {
    console.log(`Notification [${type}]: ${message}`);
    
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 25px;
        background: ${type === 'success' ? '#1cc88a' : type === 'warning' ? '#f6c23e' : type === 'danger' ? '#e74a3b' : '#36b9cc'};
        color: white;
        border-radius: 5px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 9999;
        animation: slideIn 0.3s ease;
        font-size: 14px;
        font-weight: 500;
    `;
    notification.textContent = message;
    document.body.appendChild(notification);

    setTimeout(function() {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(function() {
            notification.remove();
        }, 300);
    }, 3000);
}

// Add animation styles if not already present
(function() {
    if (!document.querySelector('style[data-animations]')) {
        const styleEl = document.createElement('style');
        styleEl.setAttribute('data-animations', 'true');
        styleEl.textContent = `
            @keyframes slideIn {
                from { transform: translateX(400px); opacity: 0; }
                to { transform: translateX(0); opacity: 1; }
            }
            @keyframes slideOut {
                from { transform: translateX(0); opacity: 1; }
                to { transform: translateX(400px); opacity: 0; }
            }
        `;
        document.head.appendChild(styleEl);
    }
})();
console.log('Department management script loaded successfully');
// ========================================================================================
// PRESCRIPTION MANAGEMENT FUNCTIONS
// ========================================================================================

// Prescription Management - Global Variables and Functions
(function() {
    'use strict';
    
    // Global variable to store current prescription ID for modal
    let currentPrescriptionId = null;

    // Setup event listeners when DOM is loaded
    function initPrescriptionListeners() {
        const searchInput = document.getElementById('prescriptionSearch');
        const dateInput = document.getElementById('prescriptionDate');
        
        if (searchInput) {
            searchInput.addEventListener('input', filterPrescriptions);
        }
        
        if (dateInput) {
            dateInput.addEventListener('change', filterPrescriptions);
        }
    }

    // Filter Prescriptions
    window.filterPrescriptions = function() {
        const searchValue = document.getElementById('prescriptionSearch')?.value.toLowerCase() || '';
        const dateValue = document.getElementById('prescriptionDate')?.value || '';
        const rows = document.querySelectorAll('.prescription-row');
        
        rows.forEach(row => {
            const patient = row.getAttribute('data-patient') || '';
            const doctor = row.getAttribute('data-doctor') || '';
            const date = row.getAttribute('data-date') || '';
            
            const matchesSearch = patient.includes(searchValue) || doctor.includes(searchValue);
            const matchesDate = !dateValue || date === dateValue;
            
            if (matchesSearch && matchesDate) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    };

    // View Prescription Details
    window.viewPrescription = function(id) {
        const rows = document.querySelectorAll('.prescription-row');
        let prescriptionData = null;
        
        rows.forEach(row => {
            if (row.cells[0].textContent == id) {
                prescriptionData = {
                    id: row.cells[0].textContent,
                    patient: row.cells[1].textContent,
                    doctor: row.cells[2].textContent,
                    date: row.cells[3].textContent,
                    medicines: row.cells[4].textContent
                };
            }
        });
        
        if (prescriptionData) {
            currentPrescriptionId = id;
            document.getElementById('rxDetailId').textContent = prescriptionData.id;
            document.getElementById('rxDetailPatient').textContent = prescriptionData.patient;
            document.getElementById('rxDetailDoctor').textContent = prescriptionData.doctor;
            document.getElementById('rxDetailDate').textContent = prescriptionData.date;
            document.getElementById('rxDetailMedicines').textContent = prescriptionData.medicines;
            
            document.getElementById('prescriptionModal').classList.add('active');
        } else {
            fetch('getPrescription?id=' + id)
                .then(response => response.json())
                .then(data => {
                    currentPrescriptionId = id;
                    document.getElementById('rxDetailId').textContent = data.id;
                    document.getElementById('rxDetailPatient').textContent = data.patientName;
                    document.getElementById('rxDetailDoctor').textContent = data.doctorName;
                    document.getElementById('rxDetailDate').textContent = data.date;
                    document.getElementById('rxDetailMedicines').textContent = data.medicine;
                    
                    document.getElementById('prescriptionModal').classList.add('active');
                })
                .catch(error => {
                    console.error('Error:', error);
                    if (typeof showNotification === 'function') {
                        showNotification('Error loading prescription details', 'danger');
                    }
                });
        }
    };

    // Close Prescription Modal
    window.closePrescriptionModal = function() {
        const modal = document.getElementById('prescriptionModal');
        if (modal) {
            modal.classList.remove('active');
        }
        currentPrescriptionId = null;
    };

    // Download Prescription
    window.downloadPrescription = function(id) {
        window.location.href = 'DownloadPrescriptionServlet?id=' + id;
        if (typeof showNotification === 'function') {
            showNotification('Downloading prescription...', 'info');
        }
    };

    // Download from Modal
    window.downloadPrescriptionFromModal = function() {
        if (currentPrescriptionId) {
            downloadPrescription(currentPrescriptionId);
        }
    };

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initPrescriptionListeners);
    } else {
        initPrescriptionListeners();
    }

    // Close modal when clicking outside
    window.addEventListener('click', function(event) {
        const modal = document.getElementById('prescriptionModal');
        if (modal && event.target === modal) {
            closePrescriptionModal();
        }
    });

})();

// ========================================================================================
// USER MANAGEMENT FUNCTIONS
// ========================================================================================
// Filter Users
function filterUsers() {
    const searchValue = document.getElementById('userSearch').value.toLowerCase();
    const roleValue = document.getElementById('userRoleFilter').value.toLowerCase();
    const rows = document.querySelectorAll('.user-row');
    
    rows.forEach(row => {
        const rowName = row.getAttribute('data-name').toLowerCase();
        const rowEmail = row.getAttribute('data-email').toLowerCase();
        const rowRole = row.getAttribute('data-role').toLowerCase();
        
        const matchesSearch = !searchValue || rowName.includes(searchValue) || rowEmail.includes(searchValue);
        const matchesRole = !roleValue || rowRole === roleValue;
        
        if (matchesSearch && matchesRole) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

// Open User Modal
function openUserModal() {
    document.getElementById('modalTitle').textContent = 'Add New User';
    document.getElementById('userForm').reset();
    document.getElementById('userForm').action = 'UserServlet?action=add';
    document.getElementById('userPassword').required = true;
    document.getElementById('userModal').classList.add('active');
}

// Close User Modal
function closeUserModal() {
    document.getElementById('userModal').classList.remove('active');
    document.getElementById('userForm').reset();
}

// Edit User
function editUser(id) {
    // Redirect to edit page or open modal with user data
    window.location.href = 'UserServlet?action=get&id=' + id;
}

// Reset Password
function resetPassword(id) {
    if (confirm('Send password reset link to this user?')) {
        window.location.href = 'UserServlet?action=resetPassword&id=' + id;
    }
}

// Delete User
function deleteUser(id) {
    if (confirm('Are you sure you want to delete this user?')) {
        window.location.href = 'UserServlet?action=delete&id=' + id;
    }
}

// Show notification
function showNotification(message, type) {
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 25px;
        background: ${type === 'success' ? '#1cc88a' : type === 'warning' ? '#f6c23e' : type === 'danger' ? '#e74a3b' : '#36b9cc'};
        color: white;
        border-radius: 5px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 10000;
        animation: slideIn 0.3s ease;
        font-size: 14px;
        font-weight: 500;
    `;
    notification.textContent = message;
    document.body.appendChild(notification);
    
    setTimeout(function() {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(function() {
            notification.remove();
        }, 300);
    }, 3000);
}

// Check for success/error messages
window.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const success = urlParams.get('success');
    const error = urlParams.get('error');
    
    if (success === 'added') {
        showNotification('User added successfully!', 'success');
    } else if (success === 'updated') {
        showNotification('User updated successfully!', 'success');
    } else if (success === 'deleted') {
        showNotification('User deleted successfully!', 'success');
    } else if (success === 'password_reset') {
        showNotification('Password reset successfully!', 'success');
    } else if (error) {
        showNotification('Error: ' + error.replace('_', ' '), 'danger');
    }
});
// ========================================================================================
// SETTINGS FUNCTIONS
// ========================================================================================
// Add Holiday
function addHoliday() {
    document.getElementById('holidayModal').classList.add('active');
}

// Close Holiday Modal
function closeHolidayModal() {
    document.getElementById('holidayModal').classList.remove('active');
    document.getElementById('holidayForm').reset();
}

// Delete Holiday
function deleteHoliday(id) {
    if (confirm('Are you sure you want to delete this holiday?')) {
        window.location.href = 'SettingsServlet?action=deleteHoliday&id=' + id;
    }
}

// Show notification
function showNotification(message, type) {
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 25px;
        background: ${type === 'success' ? '#1cc88a' : type === 'warning' ? '#f6c23e' : type === 'danger' ? '#e74a3b' : '#36b9cc'};
        color: white;
        border-radius: 5px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 10000;
        animation: slideIn 0.3s ease;
        font-size: 14px;
        font-weight: 500;
    `;
    notification.textContent = message;
    document.body.appendChild(notification);
    
    setTimeout(function() {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(function() {
            notification.remove();
        }, 300);
    }, 3000);
}

// Check for success/error messages
window.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const success = urlParams.get('success');
    const error = urlParams.get('error');
    
    if (success === 'hospital_info_updated') {
        showNotification('Hospital information updated successfully!', 'success');
    } else if (success === 'working_hours_updated') {
        showNotification('Working hours updated successfully!', 'success');
    } else if (success === 'holiday_added') {
        showNotification('Holiday added successfully!', 'success');
    } else if (success === 'holiday_deleted') {
        showNotification('Holiday deleted successfully!', 'success');
    } else if (error) {
        showNotification('Error: ' + error.replace('_', ' '), 'danger');
    }
});
// ========================================================================================
// REPORT GENERATION FUNCTIONS
// ========================================================================================
// Update report generation function
window.generateReport = function(type) {
    console.log('Generating PDF report:', type);
    
    if (typeof showNotification === 'function') {
        showNotification('Downloading ' + type + ' report as PDF...', 'info');
    }
    
    // Redirect to PDF download servlet
    window.location.href = 'DownloadPDFReportServlet?type=' + type;
};

// Update custom report generation
window.generateCustomReport = function() {
    var startDateEl = document.getElementById('reportStartDate');
    var endDateEl = document.getElementById('reportEndDate');
    var reportTypeEl = document.getElementById('reportType');
    
    if (!startDateEl || !endDateEl || !reportTypeEl) {
        return;
    }
    
    var startDate = startDateEl.value;
    var endDate = endDateEl.value;
    var reportType = reportTypeEl.value;
    
    if (!startDate || !endDate) {
        if (typeof showNotification === 'function') {
            showNotification('Please select date range', 'warning');
        } else {
            alert('Please select date range');
        }
        return;
    }

    if (typeof showNotification === 'function') {
        showNotification('Downloading custom ' + reportType + ' report as PDF...', 'info');
    }
    
    // Redirect to PDF download servlet with parameters
    window.location.href = 'DownloadPDFReportServlet?type=' + reportType + 
                          '&startDate=' + encodeURIComponent(startDate) + 
                          '&endDate=' + encodeURIComponent(endDate);
};
// ========================================
// REPORTS MANAGEMENT - FIXED VERSION
// ========================================

// Wrap everything to avoid conflicts
(function() {
    'use strict';
    
    // Helper function to capitalize first letter
    function capitalizeFirst(str) {
        if (!str) return '';
        return str.charAt(0).toUpperCase() + str.slice(1);
    }
    
    // Report Generation Function
    window.generateReport = function(type) {
        console.log('Generating report:', type);
        
        if (typeof showNotification === 'function') {
            showNotification('Generating ' + type + ' report...', 'info');
        }
        
        // Use GenerateReportServlet
        var url = 'GenerateReportServlet?type=' + type;
        
        fetch(url)
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(function(html) {
                var reportWindow = window.open('', '_blank', 'width=1200,height=800');
                if (reportWindow) {
                    reportWindow.document.write(html);
                    reportWindow.document.close();
                } else {
                    alert('Please allow popups for this site');
                }
            })
            .catch(function(error) {
                console.error('Error generating report:', error);
                if (typeof showNotification === 'function') {
                    showNotification('Error generating report', 'danger');
                } else {
                    alert('Error generating report: ' + error.message);
                }
            });
    };

    // Generate Custom Report
    window.generateCustomReport = function() {
        console.log('Generating custom report');
        
        var startDateEl = document.getElementById('reportStartDate');
        var endDateEl = document.getElementById('reportEndDate');
        var reportTypeEl = document.getElementById('reportType');
        
        if (!startDateEl || !endDateEl || !reportTypeEl) {
            console.error('Report form elements not found');
            return;
        }
        
        var startDate = startDateEl.value;
        var endDate = endDateEl.value;
        var reportType = reportTypeEl.value;
        
        if (!startDate || !endDate) {
            if (typeof showNotification === 'function') {
                showNotification('Please select date range', 'warning');
            } else {
                alert('Please select date range');
            }
            return;
        }

        if (typeof showNotification === 'function') {
            showNotification('Generating custom ' + reportType + ' report from ' + startDate + ' to ' + endDate, 'info');
        }
        
        // Use GenerateReportServlet with parameters
        var url = 'GenerateReportServlet?type=' + reportType + 
                  '&startDate=' + encodeURIComponent(startDate) + 
                  '&endDate=' + encodeURIComponent(endDate) + 
                  '&custom=true';
        
        fetch(url)
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(function(html) {
                var reportWindow = window.open('', '_blank', 'width=1200,height=800');
                if (reportWindow) {
                    reportWindow.document.write(html);
                    reportWindow.document.close();
                } else {
                    alert('Please allow popups for this site');
                }
            })
            .catch(function(error) {
                console.error('Error:', error);
                if (typeof showNotification === 'function') {
                    showNotification('Error generating custom report', 'danger');
                } else {
                    alert('Error generating custom report: ' + error.message);
                }
            });
    };
    
    console.log('✅ Reports management functions loaded');
    
})();

// ========================================================================================
// EVENT LISTENERS SETUP
// ========================================================================================

function setupEventListeners() {
    // Doctor search and filter
    const doctorSearch = document.getElementById('doctorSearch');
    if (doctorSearch) {
        doctorSearch.addEventListener('input', filterDoctors);
    }
    
    const specialtyFilter = document.getElementById('specialtyFilter');
    if (specialtyFilter) {
        specialtyFilter.addEventListener('change', filterDoctors);
    }
    
    // Patient search and filter
    const patientSearch = document.getElementById('patientSearch');
    if (patientSearch) {
        patientSearch.addEventListener('input', filterPatients);
    }
    
    const patientStatusFilter = document.getElementById('patientStatusFilter');
    if (patientStatusFilter) {
        patientStatusFilter.addEventListener('change', filterPatients);
    }
    
    // Prescription search and filter
    const prescriptionSearch = document.getElementById('prescriptionSearch');
    if (prescriptionSearch) {
        prescriptionSearch.addEventListener('input', filterPrescriptions);
    }
    
    const prescriptionDate = document.getElementById('prescriptionDate');
    if (prescriptionDate) {
        prescriptionDate.addEventListener('change', filterPrescriptions);
    }
    
    // Appointment filters
    const appointmentDate = document.getElementById('appointmentDate');
    if (appointmentDate) {
        appointmentDate.addEventListener('change', filterAppointments);
    }
    
    const appointmentStatus = document.getElementById('appointmentStatus');
    if (appointmentStatus) {
        appointmentStatus.addEventListener('change', filterAppointments);
    }
    
    const appointmentDept = document.getElementById('appointmentDept');
    if (appointmentDept) {
        appointmentDept.addEventListener('change', filterAppointments);
    }
    
    // Form submit handlers
    const doctorForm = document.getElementById('doctorForm');
    if (doctorForm) {
        doctorForm.addEventListener('submit', saveDoctorForm);
    }
    
    const hospitalInfoForm = document.getElementById('hospitalInfoForm');
    if (hospitalInfoForm) {
        hospitalInfoForm.addEventListener('submit', saveHospitalInfo);
    }
    
    const workingHoursForm = document.getElementById('workingHoursForm');
    if (workingHoursForm) {
        workingHoursForm.addEventListener('submit', saveWorkingHours);
    }
}

// ========================================================================================
// NOTIFICATION SYSTEM
// ========================================================================================

function showNotification(message, type) {
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 16px 24px;
        background: ${type === 'success' ? '#10b981' : type === 'error' ? '#ef4444' : type === 'warning' ? '#f59e0b' : '#3b82f6'};
        color: white;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 9999;
        animation: slideIn 0.3s ease;
        font-size: 14px;
        font-weight: 500;
        max-width: 400px;
    `;
    notification.textContent = message;
    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Add animation styles
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(400px); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    @keyframes slideOut {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(400px); opacity: 0; }
    }
`;
document.head.appendChild(style);
// ========================================================================================
// DOCTOR AVAILABILITY MANAGEMENT FUNCTIONS
// ========================================================================================

let currentDoctorId = null;

/**
 * View Doctor Availability
 */
function viewDoctorAvailability(doctorId) {
    console.log('Viewing availability for doctor:', doctorId);
    currentDoctorId = doctorId;
    
    fetch('AdminServlet?action=getDoctorAvailability&doctorId=' + doctorId)
        .then(response => {
            if (!response.ok) throw new Error('Network error');
            return response.json();
        })
        .then(data => {
            console.log('Availability data:', data);
            if (data.success) {
                openAvailabilityModal(data);
            } else {
                showNotification('Error: ' + data.message, 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('Error loading availability data', 'error');
        });
}

/**
 * Open Availability Modal with Data
 */
function openAvailabilityModal(data) {
    const modal = document.getElementById('availabilityModal');
    if (!modal) {
        console.error('Availability modal not found');
        return;
    }
    
    // Populate Weekly Schedule
    populateWeeklySchedule(data.weeklySchedule || []);
    
    // Populate Unavailable Dates
    populateUnavailableDates(data.unavailableDates || []);
    
    // Set Emergency Availability
    const emergencyCheckbox = document.getElementById('emergencyAvailableCheckbox');
    const emergencyStatus = document.getElementById('emergencyStatusText');
    if (emergencyCheckbox && emergencyStatus) {
        emergencyCheckbox.checked = data.emergencyAvailable;
        updateEmergencyStatusText(data.emergencyAvailable);
    }
    
    modal.classList.add('active');
    modal.style.display = 'flex';
}

/**
 * Populate Weekly Schedule
 */
function populateWeeklySchedule(schedule) {
    const container = document.getElementById('weeklyScheduleContainer');
    if (!container) return;
    
    const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
    
    container.innerHTML = '';
    
    days.forEach(day => {
        const dayData = schedule.find(s => s.dayName === day) || {
            dayName: day,
            isActive: false,
            startTime: '09:00',
            endTime: '17:00'
        };
        
        const dayDiv = document.createElement('div');
        dayDiv.className = 'schedule-day';
        dayDiv.innerHTML = `
            <span class="day-name">${day}</span>
            <div class="schedule-controls">
                <label class="checkbox-label">
                    <input type="checkbox" 
                           id="${day}_active" 
                           ${dayData.isActive ? 'checked' : ''} 
                           onchange="toggleDayInputs('${day}')"> 
                    <span>Active</span>
                </label>
                <input type="time" 
                       id="${day}_start" 
                       value="${dayData.startTime || '09:00'}" 
                       class="time-input"
                       ${!dayData.isActive ? 'disabled' : ''}>
                <span class="time-separator">to</span>
                <input type="time" 
                       id="${day}_end" 
                       value="${dayData.endTime || '17:00'}" 
                       class="time-input"
                       ${!dayData.isActive ? 'disabled' : ''}>
            </div>
        `;
        container.appendChild(dayDiv);
    });
}

/**
 * Toggle Day Inputs
 */
function toggleDayInputs(day) {
    const checkbox = document.getElementById(day + '_active');
    const startInput = document.getElementById(day + '_start');
    const endInput = document.getElementById(day + '_end');
    
    if (checkbox && startInput && endInput) {
        const isActive = checkbox.checked;
        startInput.disabled = !isActive;
        endInput.disabled = !isActive;
    }
}

/**
 * Populate Unavailable Dates
 */
function populateUnavailableDates(dates) {
    const container = document.getElementById('unavailableDatesContainer');
    if (!container) return;
    
    container.innerHTML = '';
    
    if (dates.length === 0) {
        container.innerHTML = `
            <p style="text-align: center; color: #64748b; padding: 20px;">
                No unavailable dates marked
            </p>
        `;
        return;
    }
    
    dates.forEach(dateObj => {
        const dateDiv = document.createElement('div');
        dateDiv.className = 'unavailable-date-item';
        dateDiv.style.cssText = 'display: flex; justify-content: space-between; align-items: center; padding: 12px; margin-bottom: 8px; background: #fef2f2; border-radius: 8px; border-left: 3px solid #ef4444;';
        
        const formattedDate = formatDate(dateObj.date);
        
        dateDiv.innerHTML = `
            <div>
                <span style="color: #991b1b; font-weight: 500; display: block;">${formattedDate}</span>
                ${dateObj.reason ? `<span style="color: #dc2626; font-size: 12px;">${dateObj.reason}</span>` : ''}
            </div>
            <button onclick="removeUnavailableDateAdmin(${dateObj.id})" 
                    style="background: #ef4444; color: white; border: none; padding: 5px 12px; border-radius: 5px; cursor: pointer; font-size: 12px;">
                <i class="fas fa-trash"></i> Remove
            </button>
        `;
        
        container.appendChild(dateDiv);
    });
}

/**
 * Format Date
 */
function formatDate(dateStr) {
    const date = new Date(dateStr);
    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    return date.toLocaleDateString('en-US', options);
}

/**
 * Save Doctor Schedule
 */
function saveDoctorSchedule() {
    if (!currentDoctorId) {
        showNotification('No doctor selected', 'error');
        return;
    }
    
    const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
    
    // Validate at least one day is active
    const anyActive = days.some(day => {
        const checkbox = document.getElementById(day + '_active');
        return checkbox && checkbox.checked;
    });
    
    if (!anyActive) {
        showNotification('Please select at least one active day', 'error');
        return;
    }
    
    // Build form data
    let formData = 'action=updateDoctorSchedule&doctorId=' + currentDoctorId;
    
    days.forEach(day => {
        const checkbox = document.getElementById(day + '_active');
        const startInput = document.getElementById(day + '_start');
        const endInput = document.getElementById(day + '_end');
        
        if (checkbox && startInput && endInput) {
            formData += '&' + day + '_active=' + (checkbox.checked ? 'true' : 'false');
            formData += '&' + day + '_start=' + encodeURIComponent(startInput.value || '');
            formData += '&' + day + '_end=' + encodeURIComponent(endInput.value || '');
        }
    });
    
    console.log('Saving schedule:', formData);
    
    fetch('AdminServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showNotification(data.message || 'Schedule updated successfully!', 'success');
        } else {
            showNotification(data.message || 'Error updating schedule', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Error updating schedule', 'error');
    });
}

/**
 * Add Unavailable Date
 */
function addUnavailableDateAdmin() {
    if (!currentDoctorId) {
        showNotification('No doctor selected', 'error');
        return;
    }
    
    const dateInput = document.getElementById('unavailableDateInput');
    const reasonInput = document.getElementById('unavailableReasonInput');
    
    if (!dateInput || !dateInput.value) {
        showNotification('Please select a date', 'error');
        return;
    }
    
    const selectedDate = new Date(dateInput.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    if (selectedDate < today) {
        showNotification('Please select a future date', 'error');
        return;
    }
    
    const formData = 'action=addUnavailableDate' +
                    '&doctorId=' + currentDoctorId +
                    '&unavailableDate=' + encodeURIComponent(dateInput.value) +
                    '&reason=' + encodeURIComponent(reasonInput ? reasonInput.value : '');
    
    fetch('AdminServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showNotification(data.message || 'Date added successfully!', 'success');
            dateInput.value = '';
            if (reasonInput) reasonInput.value = '';
            // Refresh availability data
            viewDoctorAvailability(currentDoctorId);
        } else {
            showNotification(data.message || 'Error adding date', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Error adding date', 'error');
    });
}

/**
 * Remove Unavailable Date
 */
function removeUnavailableDateAdmin(dateId) {
    if (!confirm('Remove this unavailable date?')) {
        return;
    }
    
    const formData = 'action=removeUnavailableDate&dateId=' + dateId;
    
    fetch('AdminServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showNotification(data.message || 'Date removed successfully!', 'success');
            // Refresh availability data
            if (currentDoctorId) {
                viewDoctorAvailability(currentDoctorId);
            }
        } else {
            showNotification(data.message || 'Error removing date', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Error removing date', 'error');
    });
}

/**
 * Toggle Emergency Availability
 */
function toggleEmergencyAvailabilityAdmin() {
    if (!currentDoctorId) {
        showNotification('No doctor selected', 'error');
        return;
    }
    
    const checkbox = document.getElementById('emergencyAvailableCheckbox');
    if (!checkbox) return;
    
    const isAvailable = checkbox.checked;
    updateEmergencyStatusText(isAvailable);
    
    const formData = 'action=toggleEmergencyAvailability' +
                    '&doctorId=' + currentDoctorId +
                    '&emergencyAvailable=' + isAvailable;
    
    fetch('AdminServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showNotification(data.message || 'Emergency status updated!', 'success');
        } else {
            showNotification(data.message || 'Error updating status', 'error');
            // Revert checkbox
            checkbox.checked = !isAvailable;
            updateEmergencyStatusText(!isAvailable);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Error updating emergency status', 'error');
        // Revert checkbox
        checkbox.checked = !isAvailable;
        updateEmergencyStatusText(!isAvailable);
    });
}

/**
 * Update Emergency Status Text
 */
function updateEmergencyStatusText(isAvailable) {
    const statusText = document.getElementById('emergencyStatusText');
    if (statusText) {
        statusText.textContent = isAvailable ? 'Available for emergencies' : 'Not available for emergencies';
        statusText.style.color = isAvailable ? '#10b981' : '#ef4444';
    }
}

/**
 * Close Availability Modal
 */
function closeAvailabilityModal() {
    const modal = document.getElementById('availabilityModal');
    if (modal) {
        modal.classList.remove('active');
        modal.style.display = 'none';
    }
    currentDoctorId = null;
}

// Close modal when clicking outside
window.addEventListener('click', function(event) {
    const availabilityModal = document.getElementById('availabilityModal');
    if (event.target === availabilityModal) {
        closeAvailabilityModal();
    }
});

console.log('✅ Doctor availability management functions loaded');
console.log('%c🏥 Hospital Management System', 'color: #4e73df; font-size: 18px; font-weight: bold;');
console.log('%c✅ Admin Dashboard Ready!', 'color: #1cc88a; font-size: 14px;');