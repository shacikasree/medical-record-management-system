// doctor.js - Works with JSP dynamic data

let medicineCounter = 0;
let unavailableDates = [];

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('Doctor Dashboard Loaded');
    setupEventListeners();
});

function setupEventListeners() {
    // Navigation items
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const page = this.getAttribute('data-page');
            navigateTo(page);
        });
    });

    // Menu toggle for mobile
    const menuToggle = document.getElementById('menuToggle');
    if (menuToggle) {
        menuToggle.addEventListener('click', function() {
            document.getElementById('sidebar').classList.toggle('active');
        });
    }

    // Emergency toggle
    const emergencyToggle = document.getElementById('emergencyAvailable');
    if (emergencyToggle) {
        emergencyToggle.addEventListener('change', function() {
            const status = document.getElementById('emergencyStatus');
            if (status) {
                status.textContent = this.checked ? 
                    'Available for emergencies' : 
                    'Not available for emergencies';
            }
        });
    }

    // Close sidebar when clicking outside on mobile
    document.addEventListener('click', function(e) {
        const sidebar = document.getElementById('sidebar');
        const menuToggle = document.getElementById('menuToggle');
        
        if (window.innerWidth <= 768) {
            if (!sidebar.contains(e.target) && !menuToggle.contains(e.target)) {
                sidebar.classList.remove('active');
            }
        }
    });
}

// Navigation function
function navigateTo(page) {
    // Update active nav item
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
    });
    const activeNav = document.querySelector(`[data-page="${page}"]`);
    if (activeNav) {
        activeNav.classList.add('active');
    }

    // Update page content
    document.querySelectorAll('.page-content').forEach(content => {
        content.classList.remove('active');
    });
    const activePage = document.getElementById(page);
    if (activePage) {
        activePage.classList.add('active');
    }

    // Update page title
    const titles = {
        'dashboard': 'Dashboard',
        'today-appointments': "Today's Appointments",
        'all-appointments': 'All Appointments',
        'patients': 'My Patients',
        'write-prescription': 'Write Prescription',
        'prescription-history': 'Prescription History',
        'availability': 'Manage Availability',
        'patient-records': 'Patient Records',
        'statistics': 'Statistics & Analytics',
        'profile': 'My Profile'
    };
    
    const pageTitle = document.getElementById('pageTitle');
    if (pageTitle && titles[page]) {
        pageTitle.textContent = titles[page];
    }

    // Close sidebar on mobile
    if (window.innerWidth <= 768) {
        document.getElementById('sidebar').classList.remove('active');
    }
}

// Filter appointments by status and search
function filterAppointments() {
    const filter = document.getElementById('appointmentFilter').value;
    const search = document.getElementById('searchAppointment').value.toLowerCase();
    const cards = document.querySelectorAll('#allAppointmentsList .appointment-card');
    
    cards.forEach(card => {
        const status = card.getAttribute('data-status');
        const patientName = card.getAttribute('data-patient');
        
        let showStatus = filter === 'all' || status === filter;
        let showSearch = !search || (patientName && patientName.includes(search));
        
        if (showStatus && showSearch) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
}

// Search patients by name or phone
function searchPatients() {
    const search = document.getElementById('searchPatient').value.toLowerCase();
    const cards = document.querySelectorAll('#patientsList .patient-card');
    
    cards.forEach(card => {
        const name = card.getAttribute('data-name');
        
        if (!search || (name && name.includes(search))) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
}

// Search prescriptions by patient name
function searchPrescriptions() {
    const search = document.getElementById('searchPrescription').value.toLowerCase();
    const cards = document.querySelectorAll('#prescriptionHistoryList .prescription-card');
    
    cards.forEach(card => {
        const patientName = card.getAttribute('data-patient');
        
        if (!search || (patientName && patientName.includes(search))) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
}

// Add medicine to prescription form
function addMedicine() {
    medicineCounter++;
    const html = `
        <div class="medicine-item" id="medicine-${medicineCounter}">
            <button type="button" class="remove-medicine" onclick="removeMedicine(${medicineCounter})">
                <i class="fas fa-times"></i>
            </button>
            <div class="form-row">
                <div class="form-group">
                    <label>Medicine Name *</label>
                    <input type="text" name="medicineName[]" required>
                </div>
                <div class="form-group">
                    <label>Dosage *</label>
                    <input type="text" name="dosage[]" placeholder="e.g., 500mg" required>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Frequency *</label>
                    <select name="frequency[]" required>
                        <option value="">Select frequency</option>
                        <option value="Once daily">Once daily</option>
                        <option value="Twice daily">Twice daily</option>
                        <option value="Three times daily">Three times daily</option>
                        <option value="Four times daily">Four times daily</option>
                        <option value="As needed">As needed</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Duration *</label>
                    <input type="text" name="duration[]" placeholder="e.g., 7 days" required>
                </div>
            </div>
        </div>
    `;
    document.getElementById('medicinesList').insertAdjacentHTML('beforeend', html);
}

// Remove medicine from prescription form
function removeMedicine(id) {
    const medicineItem = document.getElementById(`medicine-${id}`);
    if (medicineItem) {
        medicineItem.remove();
    }
}

// Clear prescription form
function clearPrescriptionForm() {
    const form = document.getElementById('prescriptionForm');
    if (form) {
        form.reset();
    }
    const medicinesList = document.getElementById('medicinesList');
    if (medicinesList) {
        medicinesList.innerHTML = '';
    }
    medicineCounter = 0;
    
    // Clear appointment ID
    const appointmentField = document.getElementById('appointmentId');
    if (appointmentField) {
        appointmentField.value = '';
    }
}

/**
 * FIXED: Navigate to prescription form with patient and appointment preselected
 * @param {string} patientId - The patient's ID
 * @param {string} patientName - The patient's name  
 * @param {string} appointmentId - Optional appointment ID to link prescription to
 */
function writePrescriptionFor(patientId, patientName, appointmentId) {
    console.log('✍️ Writing prescription for:', {patientId, patientName, appointmentId});
    
    // Navigate to Write Prescription page
    navigateTo('write-prescription');
    
    // Set values after a short delay to ensure page is loaded
    setTimeout(function() {
        // Set the patient in the dropdown
        const patientSelect = document.getElementById('prescriptionPatient');
        if (patientSelect) {
            patientSelect.value = patientId;
            console.log('✅ Patient selected:', patientId);
        } else {
            console.error('❌ Patient select not found');
        }
        
        // Set appointment ID if provided
        if (appointmentId && appointmentId !== 'null') {
            const appointmentField = document.getElementById('appointmentId');
            if (appointmentField) {
                appointmentField.value = appointmentId;
                console.log('✅ Appointment ID set:', appointmentId);
            } else {
                console.error('❌ Appointment ID field not found');
            }
        }
    }, 100);
}

// Availability functions
function addUnavailableDate() {
    const dateInput = document.getElementById('unavailableDate');
    const date = dateInput.value;
    
    if (!date) {
        alert('Please select a date');
        return;
    }
    
    if (unavailableDates.includes(date)) {
        alert('This date is already marked as unavailable');
        return;
    }
    
    unavailableDates.push(date);
    
    const datesList = document.getElementById('unavailableDatesList');
    const dateItem = document.createElement('div');
    dateItem.className = 'unavailable-date-item';
    dateItem.style.cssText = 'display: flex; justify-content: space-between; padding: 10px; margin: 8px 0; background: #fef2f2; border-radius: 8px; border-left: 3px solid #ef4444;';
    dateItem.innerHTML = `
        <span style="color: #991b1b; font-weight: 500;">${formatDate(date)}</span>
        <button onclick="removeUnavailableDate('${date}')" style="background: #ef4444; color: white; border: none; padding: 5px 10px; border-radius: 5px; cursor: pointer; font-size: 12px;">Remove</button>
    `;
    datesList.appendChild(dateItem);
    
    dateInput.value = '';
}

function removeUnavailableDate(date) {
    unavailableDates = unavailableDates.filter(d => d !== date);
    const datesList = document.getElementById('unavailableDatesList');
    if (datesList) {
        datesList.innerHTML = '';
        unavailableDates.forEach(d => {
            const dateItem = document.createElement('div');
            dateItem.className = 'unavailable-date-item';
            dateItem.style.cssText = 'display: flex; justify-content: space-between; padding: 10px; margin: 8px 0; background: #fef2f2; border-radius: 8px; border-left: 3px solid #ef4444;';
            dateItem.innerHTML = `
                <span style="color: #991b1b; font-weight: 500;">${formatDate(d)}</span>
                <button onclick="removeUnavailableDate('${d}')" style="background: #ef4444; color: white; border: none; padding: 5px 10px; border-radius: 5px; cursor: pointer; font-size: 12px;">Remove</button>
            `;
            datesList.appendChild(dateItem);
        });
    }
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    return date.toLocaleDateString('en-US', options);
}

// Patient Records
function loadPatientRecords() {
    const patientId = document.getElementById('recordsPatientSelect').value;
    const container = document.getElementById('patientRecordsContainer');
    
    if (!patientId) {
        container.innerHTML = `
            <div class="no-selection" style="text-align: center; padding: 60px 20px; color: #64748b;">
                <i class="fas fa-folder-open" style="font-size: 64px; margin-bottom: 20px; opacity: 0.3;"></i>
                <p>Select a patient to view records</p>
            </div>
        `;
        return;
    }
    
    // This would normally load from database via AJAX
    container.innerHTML = `
        <div style="padding: 20px;">
            <h3>Patient Records</h3>
            <p style="color: #64748b; margin-top: 10px;">Records for patient ID: ${patientId}</p>
            <p style="color: #64748b; margin-top: 10px;">Loading patient records from database...</p>
        </div>
    `;
}

// Close modal
function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none';
    }
}

// Close modals when clicking outside
window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.style.display = 'none';
    }
}

function filterMyPrescriptions(status) {
    // Update tab buttons
    document.querySelectorAll('.filter-tabs .tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');
    
    // Show/hide prescriptions
    document.querySelectorAll('#myPrescriptionsList .prescription-card').forEach(card => {
        if (status === 'active') {
            card.style.display = card.dataset.status === 'active' ? 'block' : 'none';
        } else {
            card.style.display = card.dataset.status === 'history' ? 'block' : 'none';
        }
    });
}

// Prescription filtering
function filterPrescriptions(status) {
    console.log('🔍 Filtering prescriptions:', status);
    
    // Update tab buttons
    document.querySelectorAll('#prescriptionsPage .tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');
    
    // Show/hide cards based on status
    const cards = document.querySelectorAll('#prescriptionsContainer .prescription-card:not(.empty-state)');
    let visibleCount = 0;
    
    cards.forEach(card => {
        const cardStatus = card.getAttribute('data-status');
        
        if (status === 'active' && cardStatus === 'active') {
            card.style.display = 'block';
            visibleCount++;
        } else if (status === 'history' && cardStatus === 'history') {
            card.style.display = 'block';
            visibleCount++;
        } else {
            card.style.display = 'none';
        }
    });
    
    console.log('✅ Visible cards:', visibleCount);
    
    // Handle empty state
    const emptyState = document.querySelector('#prescriptionsContainer .empty-state');
    if (visibleCount === 0 && !emptyState) {
        // Create empty state if it doesn't exist
        const container = document.getElementById('prescriptionsContainer');
        const empty = document.createElement('div');
        empty.className = 'empty-state';
        empty.innerHTML = `
            <i class="fas fa-prescription"></i>
            <p>No ${status} prescriptions found</p>
        `;
        container.appendChild(empty);
    } else if (emptyState) {
        emptyState.style.display = visibleCount === 0 ? 'block' : 'none';
    }
}

function downloadPrescription(rxId, medicineName) {
    console.log('📥 Downloading prescription:', rxId, medicineName);
    
    // Trigger download directly
    window.location.href = 'DownloadPrescriptionServlet?id=' + rxId;
}

// Print function
function printPrescription() {
    window.print();
}

function toggleDayInputs(checkbox, dayName) {
    const startInput = document.getElementById(dayName + '_start');
    const endInput = document.getElementById(dayName + '_end');
    
    if (checkbox.checked) {
        startInput.disabled = false;
        endInput.disabled = false;
        
        // Set default times if empty
        if (!startInput.value) {
            if (dayName === 'Saturday') {
                startInput.value = '09:00';
                endInput.value = '13:00';
            } else {
                startInput.value = '09:00';
                endInput.value = '17:00';
            }
        }
    } else {
        startInput.disabled = true;
        endInput.disabled = true;
        startInput.value = '';
        endInput.value = '';
    }
}

/**
 * Update emergency availability status
 */
function updateEmergencyStatus(isAvailable) {
    const statusSpan = document.getElementById('emergencyStatus');
    
    // Update UI immediately
    if (isAvailable) {
        statusSpan.textContent = 'Available for emergencies';
        statusSpan.style.color = '#10b981';
    } else {
        statusSpan.textContent = 'Not available for emergencies';
        statusSpan.style.color = '#ef4444';
    }
    
    // Send to server
    const form = new FormData();
    form.append('action', 'toggleEmergency');
    form.append('emergencyAvailable', isAvailable);
    
    fetch('SaveSchedule', {
        method: 'POST',
        body: form
    })
    .then(response => {
        if (response.ok) {
            showNotification('Emergency status updated successfully', 'success');
        } else {
            showNotification('Failed to update emergency status', 'error');
            // Revert checkbox
            document.getElementById('emergencyAvailable').checked = !isAvailable;
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Error updating emergency status', 'error');
        // Revert checkbox
        document.getElementById('emergencyAvailable').checked = !isAvailable;
    });
}

/**
 * Validate schedule form before submission
 */
document.addEventListener('DOMContentLoaded', function() {
    const scheduleForm = document.getElementById('scheduleForm');
    
    if (scheduleForm) {
        scheduleForm.addEventListener('submit', function(e) {
            // Validate that at least one day is active
            const checkboxes = scheduleForm.querySelectorAll('input[type="checkbox"]');
            const anyChecked = Array.from(checkboxes).some(cb => cb.checked);
            
            if (!anyChecked) {
                e.preventDefault();
                showNotification('Please select at least one active day', 'error');
                return false;
            }
            
            // Validate time ranges
            const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
            let isValid = true;
            
            days.forEach(day => {
                const checkbox = scheduleForm.querySelector(`input[name="${day}_active"]`);
                if (checkbox && checkbox.checked) {
                    const startTime = scheduleForm.querySelector(`input[name="${day}_start"]`).value;
                    const endTime = scheduleForm.querySelector(`input[name="${day}_end"]`).value;
                    
                    if (!startTime || !endTime) {
                        showNotification(`Please set times for ${day}`, 'error');
                        isValid = false;
                        return;
                    }
                    
                    if (startTime >= endTime) {
                        showNotification(`End time must be after start time for ${day}`, 'error');
                        isValid = false;
                        return;
                    }
                }
            });
            
            if (!isValid) {
                e.preventDefault();
                return false;
            }
            
            // Show loading state
            const submitBtn = scheduleForm.querySelector('button[type="submit"]');
            const originalText = submitBtn.innerHTML;
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
            
            // Note: Form will submit normally, button state will reset on page reload
        });
    }
    
    // Validate unavailable date form
    const unavailableDateForm = document.getElementById('unavailableDateForm');
    
    if (unavailableDateForm) {
        unavailableDateForm.addEventListener('submit', function(e) {
            const dateInput = document.getElementById('unavailableDate');
            const selectedDate = new Date(dateInput.value);
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            
            if (selectedDate < today) {
                e.preventDefault();
                showNotification('Please select a future date', 'error');
                return false;
            }
        });
    }
});

/**
 * Show notification message
 */
function showNotification(message, type) {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i>
        <span>${message}</span>
    `;
    
    // Add styles
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        gap: 10px;
        z-index: 10000;
        animation: slideInRight 0.3s ease;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        font-weight: 500;
    `;
    
    if (type === 'success') {
        notification.style.background = '#d1fae5';
        notification.style.color = '#065f46';
        notification.style.borderLeft = '4px solid #10b981';
    } else {
        notification.style.background = '#fee2e2';
        notification.style.color = '#991b1b';
        notification.style.borderLeft = '4px solid #ef4444';
    }
    
    document.body.appendChild(notification);
    
    // Remove after 3 seconds
    setTimeout(() => {
        notification.style.animation = 'slideOutRight 0.3s ease';
        setTimeout(() => {
            notification.remove();
        }, 300);
    }, 3000);
}

/**
 * Add CSS animations
 */
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from {
            transform: translateX(400px);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOutRight {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(400px);
            opacity: 0;
        }
    }
    
    .schedule-day input[type="time"]:disabled {
        background: #f3f4f6;
        cursor: not-allowed;
    }
    
    .checkbox-label {
        display: flex;
        align-items: center;
        gap: 8px;
        cursor: pointer;
        user-select: none;
    }
    
    .checkbox-label input[type="checkbox"] {
        cursor: pointer;
        width: 18px;
        height: 18px;
    }
`;

document.head.appendChild(style);

console.log('✅ Availability management JavaScript loaded');
console.log('✅ Doctor.js loaded successfully');