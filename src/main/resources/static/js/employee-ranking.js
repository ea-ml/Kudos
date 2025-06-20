// Employee Ranking Pagination JS
let employeeRankingCurrentPage = 0;
let employeeRankingPageSize = 10;

function loadEmployeeRanking() {
    const params = new URLSearchParams({
        page: employeeRankingCurrentPage,
        size: employeeRankingPageSize
    });
    fetch(`/employees/ranking/paginated?${params}`)
        .then(res => res.json())
        .then(data => {
            updateEmployeeRankingTable(data.content, data.number, data.size);
            updateEmployeeRankingPagination(data);
        });
}

function updateEmployeeRankingTable(employees, page, size) {
    const tbody = document.getElementById('employeeRankingTableBody');
    tbody.innerHTML = '';
    employees.forEach((employee, idx) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${page * size + idx + 1}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${employee.name}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${employee.department || ''}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${employee.kudosCount}</td>
        `;
        tbody.appendChild(row);
    });
}

function updateEmployeeRankingPagination(data) {
    document.getElementById('employeeRankingPageInfo').textContent = `Page ${data.number + 1} of ${data.totalPages}`;
    document.getElementById('employeeRankingPrevPage').disabled = data.first;
    document.getElementById('employeeRankingNextPage').disabled = data.last;
}

document.getElementById('employeeRankingPrevPage').addEventListener('click', function() {
    if (employeeRankingCurrentPage > 0) {
        employeeRankingCurrentPage--;
        loadEmployeeRanking();
    }
});
document.getElementById('employeeRankingNextPage').addEventListener('click', function() {
    employeeRankingCurrentPage++;
    loadEmployeeRanking();
});
document.getElementById('employeeRankingPageSize').addEventListener('change', function() {
    employeeRankingPageSize = parseInt(this.value);
    employeeRankingCurrentPage = 0;
    loadEmployeeRanking();
});

document.addEventListener('DOMContentLoaded', loadEmployeeRanking); 