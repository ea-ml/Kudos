// Global function to show add modal
window.showEmployeeModal = function() {
    window.dispatchEvent(new CustomEvent('show-employee-modal'));
};

// Global function to show edit modal
window.showEditEmployeeModal = function() {
    window.dispatchEvent(new CustomEvent('show-edit-employee-modal'));
};

// Global function to show team modal
window.showTeamModal = function() {
    window.dispatchEvent(new CustomEvent('show-team-modal'));
};

// Global function to show edit team modal
window.showEditTeamModal = function() {
    window.dispatchEvent(new CustomEvent('show-edit-team-modal'));
};

// Global function to hide modals
window.hideEmployeeModal = function() {
    window.dispatchEvent(new CustomEvent('hide-employee-modal'));
};

window.hideEditEmployeeModal = function() {
    window.dispatchEvent(new CustomEvent('hide-edit-employee-modal'));
};

window.hideTeamModal = function() {
    window.dispatchEvent(new CustomEvent('hide-team-modal'));
};

window.hideEditTeamModal = function() {
    window.dispatchEvent(new CustomEvent('hide-edit-team-modal'));
};

// Helper for confirm dialog using toastr
function toastrConfirm(message, onConfirm) {
    toastr.info(
        `<div>${message}</div><br><button type='button' id='toastr-confirm-btn' class='toastr-confirm-btn'>Yes</button> <button type='button' id='toastr-cancel-btn' class='toastr-cancel-btn'>No</button>`,
        '',
        {
            timeOut: 0,
            extendedTimeOut: 0,
            closeButton: false,
            tapToDismiss: false,
            allowHtml: true,
            onShown: function() {
                document.getElementById('toastr-confirm-btn').onclick = function() {
                    toastr.clear();
                    onConfirm();
                };
                document.getElementById('toastr-cancel-btn').onclick = function() {
                    toastr.clear();
                };
            }
        }
    );
}

function editEmployee(event) {
    const employeeId = event.target.dataset.employeeId;
    fetch(`/admin/employees/${employeeId}`)
        .then(response => response.json())
        .then(employee => {
            document.getElementById('editEmployeeIdHidden').value = employee.id;
            document.getElementById('editEmployeeId').value = employee.employeeId;
            document.getElementById('editName').value = employee.name;
            document.getElementById('editEmail').value = employee.email;
            document.getElementById('editDepartment').value = employee.department?.id || '';
            window.showEditEmployeeModal();
        })
        .catch(error => {
            console.error('Error:', error);
            toastr.error('Failed to load employee data');
        });
}

function deleteEmployee(event) {
    const employeeId = event.target.dataset.employeeId;
    toastrConfirm('Are you sure you want to delete this employee?', () => {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = `/admin/employees/${employeeId}/delete`;
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfInput = document.createElement('input');
        csrfInput.type = 'hidden';
        csrfInput.name = '_csrf';
        csrfInput.value = csrfToken;
        form.appendChild(csrfInput);
        document.body.appendChild(form);
        form.submit();
    });
}

window.resetKudos = function(event) {
    const employeeId = event.target.dataset.employeeId;
    const employeeName = event.target.dataset.employeeName;
    toastrConfirm(`Are you sure you want to reset all active kudos for ${employeeName}?`, () => {
        fetch(`/admin/employees/${employeeId}/reset-kudos`, {
            method: 'PUT',
            headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        })
        .then(async res => {
            if (!res.ok) {
                const text = await res.text();
                throw new Error(text);
            }
            toastr.success(`All active kudos for ${employeeName} have been reset!`);
        })
        .catch(err => {
            toastr.error('Failed to reset kudos: ' + err.message);
        });
    });
}

// Team Management Functions
function editTeam(event) {
    const button = event.target.closest('button');
    const teamId = button.dataset.teamId;
    if (!teamId) {
        console.error('Team ID not found');
        toastr.error('Failed to get team ID');
        return;
    }
    fetch(`/admin/teams/${teamId}`)
        .then(response => response.json())
        .then(team => {
            document.getElementById('editTeamIdHidden').value = team.id;
            document.getElementById('editTeamName').value = team.name;
            window.editSelectedMembers = team.members || [];
            updateEditSelectedMembersDisplay();
            updateEditMemberInputs();
            window.showEditTeamModal();
        })
        .catch(error => {
            console.error('Error:', error);
            toastr.error('Failed to load team data');
        });
}

function deleteTeam(event) {
    const teamId = event.target.dataset.teamId;
    const teamName = event.target.dataset.teamName;
    toastrConfirm(`Are you sure you want to delete the team '${teamName}'?`, () => {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = `/admin/teams/${teamId}/delete`;
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfInput = document.createElement('input');
        csrfInput.type = 'hidden';
        csrfInput.name = '_csrf';
        csrfInput.value = csrfToken;
        form.appendChild(csrfInput);
        document.body.appendChild(form);
        form.submit();
    });
}

// Member Search Functions
function searchMembers(query) {
    const searchResults = document.getElementById('searchResults');
    const noResults = document.getElementById('noResults');
    
    // Ensure selectedMembers is initialized
    if (!window.selectedMembers || !Array.isArray(window.selectedMembers)) {
        window.selectedMembers = [];
    }
    
    if (query.length < 2) {
        searchResults.style.display = 'none';
        noResults.style.display = 'none';
        return;
    }

    // Show loading state
    searchResults.style.display = 'block';
    noResults.style.display = 'none';

    fetch(`/admin/employees/search?query=${encodeURIComponent(query)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Search failed');
            }
            return response.json();
        })
        .then(employees => {
            // Filter out already selected employees
            const filteredEmployees = employees.filter(emp => 
                !window.selectedMembers.some(selected => selected.id === emp.id)
            );
            
            // Update the search results display
            updateSearchResultsDisplay(filteredEmployees);
        })
        .catch(error => {
            console.error('Error searching employees:', error);
            searchResults.style.display = 'none';
            noResults.style.display = 'block';
        });
}

function searchEditMembers(query) {
    const searchResults = document.getElementById('editSearchResults');
    const noResults = document.getElementById('editNoResults');
    
    // Ensure editSelectedMembers is initialized
    if (!window.editSelectedMembers || !Array.isArray(window.editSelectedMembers)) {
        window.editSelectedMembers = [];
    }
    
    if (query.length < 2) {
        searchResults.style.display = 'none';
        noResults.style.display = 'none';
        return;
    }

    // Show loading state
    searchResults.style.display = 'block';
    noResults.style.display = 'none';

    fetch(`/admin/employees/search?query=${encodeURIComponent(query)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Search failed');
            }
            return response.json();
        })
        .then(employees => {
            // Filter out already selected employees
            const filteredEmployees = employees.filter(emp => 
                !window.editSelectedMembers.some(selected => selected.id === emp.id)
            );
            
            // Update the search results display
            updateEditSearchResultsDisplay(filteredEmployees);
        })
        .catch(error => {
            console.error('Error searching employees:', error);
            searchResults.style.display = 'none';
            noResults.style.display = 'block';
        });
}

function updateSearchResultsDisplay(employees) {
    const searchResults = document.getElementById('searchResults');
    const noResults = document.getElementById('noResults');
    const resultsContainer = searchResults.querySelector('.py-1');
    
    if (employees.length === 0) {
        searchResults.style.display = 'none';
        noResults.style.display = 'block';
        return;
    }
    
    searchResults.style.display = 'block';
    noResults.style.display = 'none';
    
    // Clear previous results
    resultsContainer.innerHTML = '';
    
    // Add new results
    employees.forEach(employee => {
        const button = document.createElement('button');
        button.type = 'button';
        button.className = 'w-full text-left px-4 py-3 text-sm text-gray-700 hover:bg-indigo-50 focus:bg-indigo-50 focus:outline-none border-b border-gray-100 last:border-b-0';
        button.onclick = () => addMember(employee);
        
        button.innerHTML = `
            <div class="flex justify-between items-center">
                <div class="flex items-center">
                    <span class="material-icons text-indigo-600 mr-2 text-sm">person</span>
                    <span class="font-medium">${employee.name}</span>
                </div>
                <span class="text-gray-500 text-xs bg-gray-100 px-2 py-1 rounded">${employee.employeeId}</span>
            </div>
        `;
        
        resultsContainer.appendChild(button);
    });
}

function updateEditSearchResultsDisplay(employees) {
    const searchResults = document.getElementById('editSearchResults');
    const noResults = document.getElementById('editNoResults');
    const resultsContainer = searchResults.querySelector('.py-1');
    
    if (employees.length === 0) {
        searchResults.style.display = 'none';
        noResults.style.display = 'block';
        return;
    }
    
    searchResults.style.display = 'block';
    noResults.style.display = 'none';
    
    // Clear previous results
    resultsContainer.innerHTML = '';
    
    // Add new results
    employees.forEach(employee => {
        const button = document.createElement('button');
        button.type = 'button';
        button.className = 'w-full text-left px-4 py-3 text-sm text-gray-700 hover:bg-indigo-50 focus:bg-indigo-50 focus:outline-none border-b border-gray-100 last:border-b-0';
        button.onclick = () => addEditMember(employee);
        
        button.innerHTML = `
            <div class="flex justify-between items-center">
                <div class="flex items-center">
                    <span class="material-icons text-indigo-600 mr-2 text-sm">person</span>
                    <span class="font-medium">${employee.name}</span>
                </div>
                <span class="text-gray-500 text-xs bg-gray-100 px-2 py-1 rounded">${employee.employeeId}</span>
            </div>
        `;
        
        resultsContainer.appendChild(button);
    });
}

function addMember(employee) {
    // Ensure selectedMembers is initialized
    if (!window.selectedMembers || !Array.isArray(window.selectedMembers)) {
        window.selectedMembers = [];
    }
    
    if (!window.selectedMembers.some(member => member.id === employee.id)) {
        window.selectedMembers.push(employee);
        updateSelectedMembersDisplay();
        updateMemberInputs();
        
        // Show visual feedback
        const searchInput = document.getElementById('memberSearch');
        if (searchInput) {
            searchInput.value = '';
            searchInput.placeholder = `${employee.name} added to team`;
            setTimeout(() => {
                searchInput.placeholder = 'Search employees by name or ID...';
            }, 2000);
        }
    }
    
    // Hide search results
    const searchResults = document.getElementById('searchResults');
    const noResults = document.getElementById('noResults');
    if (searchResults) searchResults.style.display = 'none';
    if (noResults) noResults.style.display = 'none';
}

function addEditMember(employee) {
    // Ensure editSelectedMembers is initialized
    if (!window.editSelectedMembers || !Array.isArray(window.editSelectedMembers)) {
        window.editSelectedMembers = [];
    }
    
    if (!window.editSelectedMembers.some(member => member.id === employee.id)) {
        window.editSelectedMembers.push(employee);
        updateEditSelectedMembersDisplay();
        updateEditMemberInputs();
        
        // Show visual feedback
        const searchInput = document.getElementById('editMemberSearch');
        if (searchInput) {
            searchInput.value = '';
            searchInput.placeholder = `${employee.name} added to team`;
            setTimeout(() => {
                searchInput.placeholder = 'Search employees by name or ID...';
            }, 2000);
        }
    }
    
    // Hide search results
    const searchResults = document.getElementById('editSearchResults');
    const noResults = document.getElementById('editNoResults');
    if (searchResults) searchResults.style.display = 'none';
    if (noResults) noResults.style.display = 'none';
}

function removeMember(employeeId) {
    // Ensure selectedMembers is initialized
    if (!window.selectedMembers || !Array.isArray(window.selectedMembers)) {
        window.selectedMembers = [];
        return;
    }
    
    const removedMember = window.selectedMembers.find(member => member.id === employeeId);
    window.selectedMembers = window.selectedMembers.filter(member => member.id !== employeeId);
    updateSelectedMembersDisplay();
    updateMemberInputs();
    
    // Show visual feedback
    if (removedMember) {
        const searchInput = document.getElementById('memberSearch');
        if (searchInput) {
            searchInput.placeholder = `${removedMember.name} removed from team`;
            setTimeout(() => {
                searchInput.placeholder = 'Search employees by name or ID...';
            }, 2000);
        }
    }
}

function removeEditMember(employeeId) {
    // Ensure editSelectedMembers is initialized
    if (!window.editSelectedMembers || !Array.isArray(window.editSelectedMembers)) {
        window.editSelectedMembers = [];
        return;
    }
    
    const removedMember = window.editSelectedMembers.find(member => member.id === employeeId);
    window.editSelectedMembers = window.editSelectedMembers.filter(member => member.id !== employeeId);
    updateEditSelectedMembersDisplay();
    updateEditMemberInputs();
    
    // Show visual feedback
    if (removedMember) {
        const searchInput = document.getElementById('editMemberSearch');
        if (searchInput) {
            searchInput.placeholder = `${removedMember.name} removed from team`;
            setTimeout(() => {
                searchInput.placeholder = 'Search employees by name or ID...';
            }, 2000);
        }
    }
}

function updateSelectedMembersDisplay() {
    const container = document.getElementById('selectedMembers');
    if (!container) return;
    
    // Ensure selectedMembers is initialized
    if (!window.selectedMembers || !Array.isArray(window.selectedMembers)) {
        window.selectedMembers = [];
    }
    
    container.innerHTML = '';
    
    if (window.selectedMembers.length === 0) {
        container.innerHTML = '<p class="text-gray-500 text-sm italic">No members selected yet. Search above to add team members.</p>';
        return;
    }
    
    window.selectedMembers.forEach(member => {
        const memberDiv = document.createElement('div');
        memberDiv.className = 'flex justify-between items-center p-3 bg-white rounded border shadow-sm hover:shadow-md transition-shadow';
        memberDiv.innerHTML = `
            <div class="flex items-center">
                <span class="material-icons text-indigo-600 mr-2 text-sm">person</span>
                <div>
                    <span class="font-medium text-sm text-gray-900">${member.name}</span>
                    <span class="text-gray-500 text-xs ml-2">${member.employeeId}</span>
                </div>
            </div>
            <button type="button" onclick="removeMember(${member.id})" 
                    class="text-red-600 hover:text-red-800 p-1 rounded hover:bg-red-50 transition-colors"
                    title="Remove from team">
                <span class="material-icons text-sm">close</span>
            </button>
        `;
        container.appendChild(memberDiv);
    });
}

function updateEditSelectedMembersDisplay() {
    const container = document.getElementById('editSelectedMembers');
    if (!container) return;
    
    // Ensure editSelectedMembers is initialized
    if (!window.editSelectedMembers || !Array.isArray(window.editSelectedMembers)) {
        window.editSelectedMembers = [];
    }
    
    container.innerHTML = '';
    
    if (window.editSelectedMembers.length === 0) {
        container.innerHTML = '<p class="text-gray-500 text-sm italic">No members selected yet. Search above to add team members.</p>';
        return;
    }
    
    window.editSelectedMembers.forEach(member => {
        const memberDiv = document.createElement('div');
        memberDiv.className = 'flex justify-between items-center p-3 bg-white rounded border shadow-sm hover:shadow-md transition-shadow';
        memberDiv.innerHTML = `
            <div class="flex items-center">
                <span class="material-icons text-indigo-600 mr-2 text-sm">person</span>
                <div>
                    <span class="font-medium text-sm text-gray-900">${member.name}</span>
                    <span class="text-gray-500 text-xs ml-2">${member.employeeId}</span>
                </div>
            </div>
            <button type="button" onclick="removeEditMember(${member.id})" 
                    class="text-red-600 hover:text-red-800 p-1 rounded hover:bg-red-50 transition-colors"
                    title="Remove from team">
                <span class="material-icons text-sm">close</span>
            </button>
        `;
        container.appendChild(memberDiv);
    });
}

function updateMemberInputs() {
    const container = document.getElementById('memberInputs');
    if (!container) return;
    
    // Ensure selectedMembers is initialized
    if (!window.selectedMembers || !Array.isArray(window.selectedMembers)) {
        window.selectedMembers = [];
    }
    
    container.innerHTML = '';
    
    window.selectedMembers.forEach(member => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'memberIds';
        input.value = member.id;
        container.appendChild(input);
    });
}

function updateEditMemberInputs() {
    const container = document.getElementById('editMemberInputs');
    if (!container) return;
    
    // Ensure editSelectedMembers is initialized
    if (!window.editSelectedMembers || !Array.isArray(window.editSelectedMembers)) {
        window.editSelectedMembers = [];
    }
    
    container.innerHTML = '';
    
    window.editSelectedMembers.forEach(member => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'memberIds';
        input.value = member.id;
        container.appendChild(input);
    });
}

function resetTeamForm() {
    window.selectedMembers = [];
    
    const teamNameInput = document.getElementById('teamName');
    const memberSearchInput = document.getElementById('memberSearch');
    const searchResults = document.getElementById('searchResults');
    const noResults = document.getElementById('noResults');
    
    if (teamNameInput) teamNameInput.value = '';
    if (memberSearchInput) {
        memberSearchInput.value = '';
        memberSearchInput.placeholder = 'Search employees by name or ID...';
    }
    if (searchResults) searchResults.style.display = 'none';
    if (noResults) noResults.style.display = 'none';
    
    updateSelectedMembersDisplay();
    updateMemberInputs();
}

function resetEditTeamForm() {
    window.editSelectedMembers = [];
    
    const teamNameInput = document.getElementById('editTeamName');
    const memberSearchInput = document.getElementById('editMemberSearch');
    const searchResults = document.getElementById('editSearchResults');
    const noResults = document.getElementById('editNoResults');
    
    if (teamNameInput) teamNameInput.value = '';
    if (memberSearchInput) {
        memberSearchInput.value = '';
        memberSearchInput.placeholder = 'Search employees by name or ID...';
    }
    if (searchResults) searchResults.style.display = 'none';
    if (noResults) noResults.style.display = 'none';
    
    updateEditSelectedMembersDisplay();
    updateEditMemberInputs();
}

// Form Handlers
document.addEventListener('DOMContentLoaded', function() {
    const addForm = document.getElementById('addEmployeeForm');
    const editForm = document.getElementById('editEmployeeForm');
    const addTeamForm = document.getElementById('addTeamForm');
    
    if (addForm) {
        addForm.addEventListener('submit', function(e) {
            e.preventDefault();
            // Clear previous errors
            document.getElementById('addEmployeeIdError').textContent = '';
            document.getElementById('addNameError').textContent = '';
            document.getElementById('addEmailError').textContent = '';
            const data = {
                employeeId: escapeHtml(document.getElementById('addEmployeeId').value.trim()),
                name: escapeHtml(document.getElementById('addName').value.trim()),
                email: escapeHtml(document.getElementById('addEmail').value.trim()),
                departmentId: document.getElementById('addDepartment').value
            };
            fetch('/admin/employees', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            })
            .then(async res => {
                if (!res.ok) {
                    const text = await res.text();
                    // Try to parse error field from backend message
                    if (text.includes('Employee ID')) {
                        document.getElementById('addEmployeeIdError').textContent = text;
                    } else if (text.includes('Email')) {
                        document.getElementById('addEmailError').textContent = text;
                    } else if (text.includes('Name')) {
                        document.getElementById('addNameError').textContent = text;
                    } else {
                        toastr.error('Failed to add employee: ' + text);
                    }
                    throw new Error(text);
                }
                return res.json();
            })
            .then(employee => {
                sessionStorage.setItem('toastrMessage', 'Employee added successfully!');
                window.location.reload();
            })
            .catch(err => {
                // Error already handled above
            });
        });
    }
    
    if (editForm) {
        editForm.addEventListener('submit', function(e) {
            e.preventDefault();
            // Clear previous errors
            document.getElementById('editEmployeeIdError').textContent = '';
            document.getElementById('editNameError').textContent = '';
            document.getElementById('editEmailError').textContent = '';
            const employeeId = document.getElementById('editEmployeeIdHidden').value;
            const data = {
                employeeId: document.getElementById('editEmployeeId').value,
                name: document.getElementById('editName').value,
                email: document.getElementById('editEmail').value,
                department: { id: document.getElementById('editDepartment').value }
            };
            fetch(`/admin/employees/${employeeId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            })
            .then(async res => {
                if (!res.ok) {
                    const text = await res.text();
                    if (text.includes('Employee ID')) {
                        document.getElementById('editEmployeeIdError').textContent = text;
                    } else if (text.includes('Email')) {
                        document.getElementById('editEmailError').textContent = text;
                    } else if (text.includes('Name')) {
                        document.getElementById('editNameError').textContent = text;
                    } else {
                        toastr.error('Failed to update employee: ' + text);
                    }
                    throw new Error(text);
                }
                return res.json();
            })
            .then(employee => {
                sessionStorage.setItem('toastrMessage', 'Employee updated successfully!');
                window.location.reload();
            })
            .catch(err => {
                // Error already handled above
            });
        });
    }
    
    if (addTeamForm) {
        addTeamForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const name = document.getElementById('teamName').value;
            // Gather memberIds from window.selectedMembers
            const memberIds = (window.selectedMembers || []).map(m => m.id);
            const data = { name, memberIds };
            fetch('/admin/teams', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            })
            .then(async res => {
                if (!res.ok) {
                    const text = await res.text();
                    throw new Error(text);
                }
                return res.json();
            })
            .then(team => {
                sessionStorage.setItem('toastrMessage', 'Team created successfully!');
                window.location.reload();
            })
            .catch(err => {
                toastr.error('Failed to create team: ' + err.message);
            });
        });
    }
    
    const editTeamForm = document.getElementById('editTeamForm');
    if (editTeamForm) {
        editTeamForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const teamId = document.getElementById('editTeamIdHidden').value;
            const name = document.getElementById('editTeamName').value;
            const memberIds = (window.editSelectedMembers || []).map(m => m.id);
            const data = { name, memberIds };
            fetch(`/admin/teams/${teamId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify(data)
            })
            .then(async res => {
                if (!res.ok) {
                    const text = await res.text();
                    throw new Error(text);
                }
                return res.json();
            })
            .then(team => {
                sessionStorage.setItem('toastrMessage', 'Team updated successfully!');
                window.location.reload();
            })
            .catch(err => {
                toastr.error('Failed to update team: ' + err.message);
            });
        });
    }
    
    // Initialize global variables
    window.selectedMembers = [];
    window.editSelectedMembers = [];
    
    // Initialize displays
    updateSelectedMembersDisplay();
    updateEditSelectedMembersDisplay();
    
    // Add click outside listener to close search results
    document.addEventListener('click', function(e) {
        const searchResults = document.getElementById('searchResults');
        const noResults = document.getElementById('noResults');
        const searchInput = document.getElementById('memberSearch');
        
        if (searchResults && searchInput && !searchResults.contains(e.target) && e.target !== searchInput) {
            searchResults.style.display = 'none';
            if (noResults) noResults.style.display = 'none';
        }
        
        const editSearchResults = document.getElementById('editSearchResults');
        const editNoResults = document.getElementById('editNoResults');
        const editSearchInput = document.getElementById('editMemberSearch');
        
        if (editSearchResults && editSearchInput && !editSearchResults.contains(e.target) && e.target !== editSearchInput) {
            editSearchResults.style.display = 'none';
            if (editNoResults) editNoResults.style.display = 'none';
        }
    });

    // Refactor deleteEmployee to use AJAX
    window.deleteEmployee = function(event) {
        const employeeId = event.target.dataset.employeeId;
        toastrConfirm('Are you sure you want to delete this employee?', () => {
            fetch(`/admin/employees/${employeeId}`, {
                method: 'DELETE',
                headers: {
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                }
            })
            .then(async res => {
                if (!res.ok) {
                    const text = await res.text();
                    throw new Error(text);
                }
                sessionStorage.setItem('toastrMessage', 'Employee deleted successfully!');
                window.location.reload();
            })
            .catch(err => {
                toastr.error('Failed to delete employee: ' + err.message);
            });
        });
    }

    // Refactor deleteTeam to use AJAX
    window.deleteTeam = function(event) {
        const button = event.target.closest('button');
        const teamId = button.dataset.teamId;
        const teamName = button.dataset.teamName;
        toastrConfirm(`Are you sure you want to delete the team '${teamName}'?`, () => {
            fetch(`/admin/teams/${teamId}`, {
                method: 'DELETE',
                headers: {
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                }
            })
            .then(async res => {
                if (!res.ok) {
                    const text = await res.text();
                    throw new Error(text);
                }
                sessionStorage.setItem('toastrMessage', 'Team deleted successfully!');
                window.location.reload();
            })
            .catch(err => {
                toastr.error('Failed to delete team: ' + err.message);
            });
        });
    }
});

// Show toastr after reload if message exists in sessionStorage
if (sessionStorage.getItem('toastrMessage')) {
    toastr.success(sessionStorage.getItem('toastrMessage'));
    sessionStorage.removeItem('toastrMessage');
}

function formatKudosDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
}

function renderKudosTable(kudosPage) {
    const tbody = document.getElementById('kudosManagementTableBody');
    tbody.innerHTML = '';
    if (!kudosPage || !kudosPage.content || kudosPage.content.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="px-6 py-4 text-center text-gray-500">No kudos found.</td></tr>';
        return;
    }
    kudosPage.content.forEach(kudos => {
        const senderName = kudos.senderName || '';
        const recipientName = kudos.recipientName || '';
        const row = document.createElement('tr');
        row.innerHTML = `
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${senderName}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${recipientName}</td>
            <td class="px-6 py-4 text-sm text-gray-500">${kudos.message}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">
                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${kudos.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}">${kudos.active ? 'Active' : 'Inactive'}</span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${formatKudosDate(kudos.createdAt)}</td>
        `;
        tbody.appendChild(row);
    });
}

function renderKudosPagination(kudosPage, currentPage, pageSize, search = '') {
    const container = document.getElementById('kudosManagementPagination');
    if (!kudosPage || kudosPage.totalPages <= 1) {
        container.innerHTML = '';
        return;
    }
    let html = '';
    html += `<button ${kudosPage.first ? 'disabled' : ''} class="px-4 py-2 bg-indigo-600 text-white rounded-md flex items-center justify-center disabled:bg-gray-400 hover:bg-indigo-700 transition duration-300" onclick="loadKudosPage(${currentPage - 1}, ${pageSize}, '${search.replace(/'/g, "\\'")}')">
        <span class="material-icons">chevron_left</span>
    </button>`;
    html += `<span class="text-gray-700">Page ${currentPage + 1} of ${kudosPage.totalPages}</span>`;
    html += `<button ${kudosPage.last ? 'disabled' : ''} class="px-4 py-2 bg-indigo-600 text-white rounded-md flex items-center justify-center disabled:bg-gray-400 hover:bg-indigo-700 transition duration-300" onclick="loadKudosPage(${currentPage + 1}, ${pageSize}, '${search.replace(/'/g, "\\'")}')">
        <span class="material-icons">chevron_right</span>
    </button>`;
    container.innerHTML = html;
}

function loadKudosPage(page = 0, size = 10, search = '') {
    const params = new URLSearchParams({
        page,
        size,
        sortBy: 'createdAt',
        sortDir: 'desc'
    });
    if (search && search.trim() !== '') {
        params.append('search', search.trim());
    }
    fetch(`/admin/kudos/paginated?${params.toString()}`)
        .then(res => res.json())
        .then(data => {
            renderKudosTable(data);
            renderKudosPagination(data, page, size, search);
        });
}

document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('kudosManagementTableBody')) {
        let currentSearch = '';
        loadKudosPage(0, 10, currentSearch);
        document.getElementById('kudosSearchBtn').addEventListener('click', function() {
            const searchVal = document.getElementById('kudosSearchInput').value;
            currentSearch = searchVal;
            loadKudosPage(0, 10, currentSearch);
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