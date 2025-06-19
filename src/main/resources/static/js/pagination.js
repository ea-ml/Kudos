// Pagination and Search functionality for Admin Dashboard
// Global variables for pagination and search
let employeeCurrentPage = 0;
let employeePageSize = 10;
let employeeSortBy = 'name';
let employeeSortDir = 'asc';
let employeeSearchQuery = '';

let teamCurrentPage = 0;
let teamPageSize = 10;
let teamSortBy = 'name';
let teamSortDir = 'asc';
let teamSearchQuery = '';

// Declare functions as variables first
let loadEmployees, updateEmployeeTable, updateEmployeePagination, searchEmployees, clearEmployeeSearch;
let sortEmployees, previousEmployeePage, nextEmployeePage, changeEmployeePageSize;
let loadTeams, updateTeamTable, updateTeamPagination, searchTeams, clearTeamSearch;
let sortTeams, previousTeamPage, nextTeamPage, changeTeamPageSize;

// Employee pagination and search functions
loadEmployees = function() {
    const params = new URLSearchParams({
        page: employeeCurrentPage,
        size: employeePageSize,
        sortBy: employeeSortBy,
        sortDir: employeeSortDir
    });
    
    if (employeeSearchQuery) {
        params.append('search', employeeSearchQuery);
    }
    
    fetch(`/admin/employees/paginated?${params}`)
        .then(response => response.json())
        .then(data => {
            updateEmployeeTable(data.content);
            updateEmployeePagination(data);
        })
        .catch(error => {
            console.error('Error loading employees:', error);
        });
};

updateEmployeeTable = function(employees) {
    const tbody = document.getElementById('employeeTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    employees.forEach(employee => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${employee.employeeId}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${employee.name}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${employee.email}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${employee.department?.name || ''}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                <div class="flex space-x-2">
                    <button onclick="editEmployee({target: {dataset: {employeeId: ${employee.id}}}})" 
                            class="text-indigo-600 hover:text-indigo-900 p-1 rounded hover:bg-indigo-50" 
                            title="Edit Employee">
                        <span class="material-icons text-lg">edit</span>
                    </button>
                    <button onclick="deleteEmployee({target: {dataset: {employeeId: ${employee.id}}}})" 
                            class="text-red-600 hover:text-red-900 p-1 rounded hover:bg-red-50" 
                            title="Delete Employee">
                        <span class="material-icons text-lg">delete</span>
                    </button>
                    <button onclick="resetKudos({target: {dataset: {employeeId: ${employee.id}, employeeName: '${employee.name}'}}})" 
                            class="text-yellow-600 hover:text-yellow-900 p-1 rounded hover:bg-yellow-50" 
                            title="Reset Kudos">
                        <span class="material-icons text-lg">refresh</span>
                    </button>
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
};

updateEmployeePagination = function(data) {
    const pageInfo = document.getElementById('employeePageInfo');
    const prevBtn = document.getElementById('employeePrevPage');
    const nextBtn = document.getElementById('employeeNextPage');
    
    if (pageInfo) {
        pageInfo.textContent = `Page ${data.number + 1} of ${data.totalPages}`;
    }
    
    if (prevBtn) {
        prevBtn.disabled = data.first;
        prevBtn.classList.toggle('opacity-50', data.first);
        prevBtn.classList.toggle('cursor-not-allowed', data.first);
    }
    
    if (nextBtn) {
        nextBtn.disabled = data.last;
        nextBtn.classList.toggle('opacity-50', data.last);
        nextBtn.classList.toggle('cursor-not-allowed', data.last);
    }
};

searchEmployees = function() {
    employeeSearchQuery = document.getElementById('employeeSearchInput').value.trim();
    employeeCurrentPage = 0;
    loadEmployees();
};

clearEmployeeSearch = function() {
    document.getElementById('employeeSearchInput').value = '';
    employeeSearchQuery = '';
    employeeCurrentPage = 0;
    loadEmployees();
};

sortEmployees = function(field) {
    if (employeeSortBy === field) {
        employeeSortDir = employeeSortDir === 'asc' ? 'desc' : 'asc';
    } else {
        employeeSortBy = field;
        employeeSortDir = 'asc';
    }
    
    // Update sort indicators
    document.querySelectorAll('[id$="Sort"]').forEach(span => {
        span.textContent = '↕';
    });
    
    const sortSpan = document.getElementById(field + 'Sort');
    if (sortSpan) {
        sortSpan.textContent = employeeSortDir === 'asc' ? '↑' : '↓';
    }
    
    loadEmployees();
};

previousEmployeePage = function() {
    if (employeeCurrentPage > 0) {
        employeeCurrentPage--;
        loadEmployees();
    }
};

nextEmployeePage = function() {
    employeeCurrentPage++;
    loadEmployees();
};

changeEmployeePageSize = function() {
    employeePageSize = parseInt(document.getElementById('employeePageSize').value);
    employeeCurrentPage = 0;
    loadEmployees();
};

// Team pagination and search functions
loadTeams = function() {
    const params = new URLSearchParams({
        page: teamCurrentPage,
        size: teamPageSize,
        sortBy: teamSortBy,
        sortDir: teamSortDir
    });
    
    if (teamSearchQuery) {
        params.append('search', teamSearchQuery);
    }
    
    fetch(`/admin/teams/paginated?${params}`)
        .then(response => response.json())
        .then(data => {
            updateTeamTable(data.content);
            updateTeamPagination(data);
        })
        .catch(error => {
            console.error('Error loading teams:', error);
        });
};

updateTeamTable = function(teams) {
    const tbody = document.getElementById('teamTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    teams.forEach(team => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${team.name}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${team.memberCount} members</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                <div class="flex space-x-2">
                    <button onclick="editTeam({target: {closest: () => ({dataset: {teamId: ${team.id}}})}})" 
                            class="text-indigo-600 hover:text-indigo-900 p-1 rounded hover:bg-indigo-50" 
                            title="Edit Team">
                        <span class="material-icons text-lg">edit</span>
                    </button>
                    <button onclick="deleteTeam({target: {dataset: {teamId: ${team.id}, teamName: '${team.name}'}}})" 
                            class="text-red-600 hover:text-red-900 p-1 rounded hover:bg-red-50" 
                            title="Delete Team">
                        <span class="material-icons text-lg">delete</span>
                    </button>
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
};

updateTeamPagination = function(data) {
    const pageInfo = document.getElementById('teamPageInfo');
    const prevBtn = document.getElementById('teamPrevPage');
    const nextBtn = document.getElementById('teamNextPage');
    
    if (pageInfo) {
        pageInfo.textContent = `Page ${data.number + 1} of ${data.totalPages}`;
    }
    
    if (prevBtn) {
        prevBtn.disabled = data.first;
        prevBtn.classList.toggle('opacity-50', data.first);
        prevBtn.classList.toggle('cursor-not-allowed', data.first);
    }
    
    if (nextBtn) {
        nextBtn.disabled = data.last;
        nextBtn.classList.toggle('opacity-50', data.last);
        nextBtn.classList.toggle('cursor-not-allowed', data.last);
    }
};

searchTeams = function() {
    teamSearchQuery = document.getElementById('teamSearchInput').value.trim();
    teamCurrentPage = 0;
    loadTeams();
};

clearTeamSearch = function() {
    document.getElementById('teamSearchInput').value = '';
    teamSearchQuery = '';
    teamCurrentPage = 0;
    loadTeams();
};

sortTeams = function(field) {
    if (teamSortBy === field) {
        teamSortDir = teamSortDir === 'asc' ? 'desc' : 'asc';
    } else {
        teamSortBy = field;
        teamSortDir = 'asc';
    }
    
    // Update sort indicators
    document.querySelectorAll('[id$="Sort"]').forEach(span => {
        span.textContent = '↕';
    });
    
    const sortSpan = document.getElementById(field + 'Sort');
    if (sortSpan) {
        sortSpan.textContent = teamSortDir === 'asc' ? '↑' : '↓';
    }
    
    loadTeams();
};

previousTeamPage = function() {
    if (teamCurrentPage > 0) {
        teamCurrentPage--;
        loadTeams();
    }
};

nextTeamPage = function() {
    teamCurrentPage++;
    loadTeams();
};

changeTeamPageSize = function() {
    teamPageSize = parseInt(document.getElementById('teamPageSize').value);
    teamCurrentPage = 0;
    loadTeams();
};

// Make functions globally accessible
window.loadEmployees = loadEmployees;
window.updateEmployeeTable = updateEmployeeTable;
window.updateEmployeePagination = updateEmployeePagination;
window.searchEmployees = searchEmployees;
window.clearEmployeeSearch = clearEmployeeSearch;
window.sortEmployees = sortEmployees;
window.previousEmployeePage = previousEmployeePage;
window.nextEmployeePage = nextEmployeePage;
window.changeEmployeePageSize = changeEmployeePageSize;

window.loadTeams = loadTeams;
window.updateTeamTable = updateTeamTable;
window.updateTeamPagination = updateTeamPagination;
window.searchTeams = searchTeams;
window.clearTeamSearch = clearTeamSearch;
window.sortTeams = sortTeams;
window.previousTeamPage = previousTeamPage;
window.nextTeamPage = nextTeamPage;
window.changeTeamPageSize = changeTeamPageSize;

// Initialize pagination when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Initialize pagination and search
    loadEmployees();
    loadTeams();
}); 