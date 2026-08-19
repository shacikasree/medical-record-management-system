    console.log('🚀 JavaScript Started');
    
    let currentStep = 1;
    const totalSteps = 3;

    // Initialize on page load
    document.addEventListener('DOMContentLoaded', function() {
      const today = new Date().toISOString().split('T')[0];
      document.getElementById('appointmentDate').setAttribute('min', today);
      console.log('✅ Page Ready - Date set to:', today);
    });

    // Department selection handler
    document.getElementById('departmentSelect').addEventListener('change', function() {
      const dept = this.value;
      const group = document.getElementById('doctorSelectGroup');
      const select = document.getElementById('doctorSelect');
      const loading = document.getElementById('loadingDoctors');
      const error = document.getElementById('errorDoctors');
      const btn = document.getElementById('nextBtn1');
      
      console.log('📋 Department selected:', dept);
      
      if(dept) {
        // Show doctor section
        group.style.display = 'block';
        loading.style.display = 'block';
        error.style.display = 'none';
        select.disabled = true;
        select.innerHTML = '<option value="">-- Choose Doctor --</option>';
        btn.disabled = true;
        
        const url = 'getDoctorsByDepartment?department=' + encodeURIComponent(dept);
        console.log('🔗 Fetching URL:', url);
        
        // Fetch doctors
        fetch(url)
          .then(response => {
            console.log('📥 Response Status:', response.status);
            console.log('📥 Response OK:', response.ok);
            
            if (!response.ok) {
              throw new Error('Server returned ' + response.status);
            }
            return response.text();
          })
          .then(text => {
            console.log('📄 Raw Response:', text);
            
            // Try to parse JSON
            try {
              return JSON.parse(text);
            } catch(e) {
              console.error('❌ JSON Parse Error:', e);
              throw new Error('Invalid JSON response');
            }
          })
          .then(doctors => {
            loading.style.display = 'none';
            select.disabled = false;
            
            console.log('👨‍⚕️ Doctors Array:', doctors);
            console.log('👨‍⚕️ Array Length:', doctors.length);
            
            if (!doctors || !Array.isArray(doctors) || doctors.length === 0) {
              select.innerHTML = '<option value="">No doctors available</option>';
              error.style.display = 'block';
              error.textContent = 'No doctors found in ' + dept + ' department';
              console.log('⚠️ No doctors found');
              return;
            }
            
            // Clear and populate dropdown
            select.innerHTML = '<option value="">-- Choose Doctor --</option>';
            
            doctors.forEach(function(d) {
              console.log('Adding doctor:', d.name, 'ID:', d.doctorId);
              const opt = document.createElement('option');
              opt.value = d.doctorId;
              opt.textContent = d.name + ' - ' + d.qualification;
              opt.setAttribute('data-name', d.name);
              select.appendChild(opt);
            });
            
            console.log('✅ Successfully loaded ' + doctors.length + ' doctors');
          })
          .catch(function(err) {
            loading.style.display = 'none';
            select.disabled = false;
            select.innerHTML = '<option value="">Error loading doctors</option>';
            error.style.display = 'block';
            error.textContent = 'Error: ' + err.message + '. Check browser console (F12).';
            console.error('❌ Fetch Error:', err);
            alert('Failed to load doctors!\n\nError: ' + err.message + '\n\nPlease:\n1. Check browser console (F12)\n2. Verify servlet is running\n3. Check network tab');
          });
      } else {
        group.style.display = 'none';
        select.innerHTML = '<option value="">-- Choose Doctor --</option>';
        btn.disabled = true;
        error.style.display = 'none';
      }
    });

    // Doctor selection handler
    document.getElementById('doctorSelect').addEventListener('change', function() {
      const btn = document.getElementById('nextBtn1');
      const hidden = document.getElementById('doctorNameHidden');
      
      if(this.value) {
        btn.disabled = false;
        const selectedOption = this.options[this.selectedIndex];
        hidden.value = selectedOption.getAttribute('data-name');
        console.log('✅ Doctor selected:', hidden.value, '(ID:', this.value + ')');
      } else {
        btn.disabled = true;
        hidden.value = '';
      }
    });

    // Step 1 Next button
    document.getElementById('nextBtn1').addEventListener('click', function() {
      const deptSelect = document.getElementById('departmentSelect');
      const docSelect = document.getElementById('doctorSelect');
      
      if(!deptSelect.value) {
        alert('Please select a department!');
        deptSelect.focus();
        return;
      }
      
      if(!docSelect.value) {
        alert('Please select a doctor!');
        docSelect.focus();
        return;
      }
      
      console.log('✅ Step 1 Complete');
      goToStep(2);
    });

    // Step 2 buttons
    document.getElementById('backBtn2').addEventListener('click', function() {
      goToStep(1);
    });

    document.getElementById('nextBtn2').addEventListener('click', function() {
      const dateInput = document.querySelector('[name="appointment_date"]');
      const timeInput = document.querySelector('[name="appointment_time"]');
      
      if(!dateInput.value) {
        alert('Please select appointment date!');
        dateInput.focus();
        return;
      }
      
      if(!timeInput.value) {
        alert('Please select appointment time!');
        timeInput.focus();
        return;
      }
      
      console.log('✅ Step 2 Complete');
      goToStep(3);
    });

    // Step 3 back button
    document.getElementById('backBtn3').addEventListener('click', function() {
      goToStep(2);
    });

    // Navigation function
    function goToStep(step) {
      document.querySelectorAll('.form-step').forEach(function(s) {
        s.classList.remove('active');
      });
      document.querySelector('[data-step="' + step + '"]').classList.add('active');
      currentStep = step;
      document.getElementById('progressBar').style.width = (step / totalSteps * 100) + '%';
      console.log('📍 Now on Step', step);
    }

    // Form submit validation
    document.getElementById('appointmentForm').addEventListener('submit', function(e) {
      const docSelect = document.getElementById('doctorSelect');
      
      if(!docSelect.value) {
        e.preventDefault();
        alert('Please select a doctor!');
        goToStep(1);
        return false;
      }
      
      console.log('✅ Form submitting...');
      console.log('   Doctor ID:', docSelect.value);
      console.log('   Doctor Name:', document.getElementById('doctorNameHidden').value);
      return true;
    });

    console.log('✅ All event listeners attached successfully');