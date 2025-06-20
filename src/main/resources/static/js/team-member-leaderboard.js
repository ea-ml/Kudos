// Team Member Leaderboard Pagination JS
let teamMemberCurrentPage = 0;
let teamMemberPageSize = 10;

function loadTeamMemberLeaderboard() {
    const params = new URLSearchParams({
        page: teamMemberCurrentPage,
        size: teamMemberPageSize
    });
    fetch(`/teams/${teamId}/members/leaderboard?${params}`)
        .then(res => res.json())
        .then(data => {
            updateTeamMemberTable(data.content, data.number, data.size);
            updateTeamMemberPagination(data);
        });
}

function updateTeamMemberTable(members, page, size) {
    const tbody = document.getElementById('employeeRankingTableBody');
    tbody.innerHTML = '';
    members.forEach((member, idx) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${page * size + idx + 1}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${member.name}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${member.department}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${member.kudosCount}</td>
        `;
        tbody.appendChild(row);
    });
}

function updateTeamMemberPagination(data) {
    document.getElementById('employeeRankingPageInfo').textContent = `Page ${data.number + 1} of ${data.totalPages}`;
    document.getElementById('employeeRankingPrevPage').disabled = data.first;
    document.getElementById('employeeRankingNextPage').disabled = data.last;
}

document.getElementById('employeeRankingPrevPage').addEventListener('click', function() {
    if (teamMemberCurrentPage > 0) {
        teamMemberCurrentPage--;
        loadTeamMemberLeaderboard();
    }
});
document.getElementById('employeeRankingNextPage').addEventListener('click', function() {
    teamMemberCurrentPage++;
    loadTeamMemberLeaderboard();
});
document.getElementById('employeeRankingPageSize').addEventListener('change', function() {
    teamMemberPageSize = parseInt(this.value);
    teamMemberCurrentPage = 0;
    loadTeamMemberLeaderboard();
});

document.addEventListener('DOMContentLoaded', loadTeamMemberLeaderboard); 