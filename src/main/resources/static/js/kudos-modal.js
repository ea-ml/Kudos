// Kudos Modal Logic

document.addEventListener('DOMContentLoaded', function() {
    // Modal open/close logic
    const openBtn = document.getElementById('openKudosModalBtn');
    const closeBtn = document.getElementById('closeKudosModalBtn');
    const cancelBtn = document.getElementById('cancelKudosBtn');
    const modal = document.getElementById('kudosModal');

    const senderSelect = document.getElementById('senderSelect');
    const employeeSelect = document.getElementById('employeeSelect');
    const teamSelect = document.getElementById('teamSelect');

    async function loadEmployees(selectElement, placeholder) {
        try {
            const res = await fetch('/employees/all');
            const employees = await res.json();
            selectElement.innerHTML = `<option value="">-- ${placeholder} --</option>`;
            employees.forEach(emp => {
                const option = document.createElement('option');
                option.value = emp.id;
                option.textContent = emp.name + (emp.department && emp.department.name ? ' (' + emp.department.name + ')' : '');
                selectElement.appendChild(option);
            });
        } catch (e) {
            selectElement.innerHTML = '<option value="">Failed to load employees</option>';
        }
    }

    async function loadTeams() {
        try {
            const res = await fetch('/teams/all');
            const teams = await res.json();
            teamSelect.innerHTML = '<option value="">-- Select Team --</option>';
            teams.forEach(team => {
                const option = document.createElement('option');
                option.value = team.id;
                option.textContent = team.name;
                teamSelect.appendChild(option);
            });
        } catch (e) {
            teamSelect.innerHTML = '<option value="">Failed to load teams</option>';
        }
    }

    if (openBtn && closeBtn && cancelBtn && modal) {
        openBtn.addEventListener('click', () => {
            modal.classList.remove('hidden');
            loadEmployees(employeeSelect, 'Select Employee');
            loadEmployees(senderSelect, 'Select Sender');
            loadTeams();
        });
        closeBtn.addEventListener('click', () => { modal.classList.add('hidden'); });
        cancelBtn.addEventListener('click', () => { modal.classList.add('hidden'); });
    }

    // Toggle dropdowns
    const recipientRadios = document.querySelectorAll('.recipient-type-radio');
    const employeeDropdown = document.getElementById('employeeDropdown');
    const teamDropdown = document.getElementById('teamDropdown');
    recipientRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            if (this.value === 'employee') {
                employeeDropdown.classList.remove('hidden');
                teamDropdown.classList.add('hidden');
            } else {
                employeeDropdown.classList.add('hidden');
                teamDropdown.classList.remove('hidden');
            }
        });
    });

    // AJAX form submission for kudos
    const kudosForm = document.getElementById('kudosForm');
    if (kudosForm) {
        kudosForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            const senderId = senderSelect.value;
            const recipientType = document.querySelector('input[name="recipientType"]:checked').value;
            let recipientId = null;

            if (recipientType === 'employee') {
                recipientId = employeeSelect.value;
            } else {
                recipientId = teamSelect.value;
            }
            const message = escapeHtml(document.getElementById('kudosMessage').value.trim());
            const isAnonymous = document.getElementById('anonymousCheckbox').checked;

            if (!senderId || !recipientId || !recipientType || !message) {
                toastr.error('Please fill in all required fields.');
                return;
            }
            try {
                const csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
                const csrfHeader = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

                const headers = {
                    'Content-Type': 'application/json'
                };
                headers[csrfHeader] = csrfToken;

                const response = await fetch('/kudos', {
                    method: 'POST',
                    headers: headers,
                    body: JSON.stringify({
                        senderId: Number(senderId),
                        recipientId: Number(recipientId),
                        recipientType: recipientType,
                        message: message,
                        anonymous: isAnonymous
                    })
                });
                if (!response.ok) {
                    const errorText = await response.text();
                    toastr.error('Failed to send kudos: ' + errorText);
                    return;
                }
                toastr.success('Kudos sent successfully!');
                modal.classList.add('hidden');
                kudosForm.reset();
            } catch (err) {
                toastr.error('Failed to send kudos: ' + err.message);
            }
        });
    }
}); 

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>"]/g, function(tag) {
        const charsToReplace = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;'
        };
        return charsToReplace[tag] || tag;
    });
} 