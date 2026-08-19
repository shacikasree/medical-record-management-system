<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- Write Prescription Modal -->
<div id="writePrescriptionModal" class="modal">
    <div class="modal-content" style="max-width: 800px;">
        <div class="modal-header">
            <h3>📝 Write Prescription</h3>
            <button class="close-btn" onclick="closeModal('writePrescriptionModal')">&times;</button>
        </div>
        <form id="prescriptionForm">
            <div class="alert alert-info" style="margin-bottom: 20px;">
                <strong>Patient:</strong> <span id="rxPatientName"></span> (<span id="rxPatientId"></span>)<br>
                <strong>Date:</strong> <span id="rxDate"></span>
            </div>
            
            <div class="form-group">
                <label>Diagnosis / Chief Complaint *</label>
                <textarea class="form-control" id="rxDiagnosis" placeholder="e.g., Upper respiratory tract infection" required></textarea>
            </div>
            
            <h3 style="color: #2c3e50; margin: 25px 0 15px;">Medicines</h3>
            
            <div id="medicinesContainer">
                <div class="medicine-row">
                    <div style="flex: 2;">
                        <label style="font-size: 12px; color: #7f8c8d; margin-bottom: 5px;">Medicine Name</label>
                        <input type="text" class="form-control" placeholder="e.g., Paracetamol 500mg" required>
                    </div>
                    <div style="flex: 1;">
                        <label style="font-size: 12px; color: #7f8c8d; margin-bottom: 5px;">Dosage</label>
                        <input type="text" class="form-control" placeholder="e.g., 1-0-1" required>
                    </div>
                    <div style="flex: 1;">
                        <label style="font-size: 12px; color: #7f8c8d; margin-bottom: 5px;">Duration</label>
                        <input type="text" class="form-control" placeholder="e.g., 5 days" required>
                    </div>
                </div>
            </div>
            
            <button type="button" class="add-medicine-btn" onclick="addMedicineRow()">➕ Add Another Medicine</button>
            
            <div class="form-group">
                <label>Instructions / Advice</label>
                <textarea class="form-control" id="rxInstructions" placeholder="e.g., Take medicine after meals with water"></textarea>
            </div>
            
            <div class="form-group">
                <label>Follow-up Date (Optional)</label>
                <input type="date" class="form-control" id="rxFollowup">
            </div>
            
            <div style="display: flex; gap: 10px; margin-top: 25px;">
                <button type="button" class="btn btn-success" style="flex: 1;" onclick="savePrescription(false)">💾 Save Prescription</button>
                <button type="button" class="btn btn-primary" style="flex: 1;" onclick="savePrescription(true)">💾 Save & Download PDF</button>
                <button type="button" class="btn btn-danger" onclick="closeModal('writePrescriptionModal')">Cancel</button>
            </div>
        </form>
    </div>
</div>

<!-- View Prescription Modal -->
<div id="viewPrescriptionModal" class="modal">
    <div class="modal-content" style="max-width: 700px;">
        <div class="modal-header">
            <h3>📄 Prescription Details</h3>
            <button class="close-btn" onclick="closeModal('viewPrescriptionModal')">&times;</button>
        </div>
        <div id="prescriptionDetailContent">
            <!-- Content will be loaded dynamically -->
        </div>
    </div>
</div>

<!-- Patient Detail Modal -->
<div id="patientDetailModal" class="modal">
    <div class="modal-content" style="max-width: 700px;">
        <div class="modal-header">
            <h3 id="patientDetailName">👤 Patient Details</h3>
            <button class="close-btn" onclick="closeModal('patientDetailModal')">&times;</button>
        </div>
        <div id="patientDetailContent">
            <!-- Content will be loaded dynamically -->
        </div>
    </div>
</div>

<!-- Unavailable Date Modal -->
<div id="unavailableModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3>🚫 Add Unavailable Date</h3>
            <button class="close-btn" onclick="closeModal('unavailableModal')">&times;</button>
        </div>
        <form id="unavailableForm">
            <div class="form-group">
                <label>Date *</label>
                <input type="date" class="form-control" id="unavailableDate" required>
            </div>
            
            <div class="form-group">
                <label>Reason *</label>
                <input type="text" class="form-control" id="unavailableReason" placeholder="e.g., Medical Conference, Personal Leave" required>
            </div>
            
            <button type="button" class="btn btn-primary" style="width: 100%;" onclick="addUnavailableDate()">Add Unavailable Date</button>
        </form>
    </div>
</div>