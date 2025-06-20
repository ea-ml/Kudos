// Team Ranking Pagination JS
let teamRankingCurrentPage = 0;
let teamRankingPageSize = 10;

function loadTeamRanking() {
    const params = new URLSearchParams({
        page: teamRankingCurrentPage,
        size: teamRankingPageSize
    });
    fetch(`/teams/ranking/paginated?${params}`)
        .then(res => res.json())
        .then(data => {
            updateTeamRankingTable(data.content, data.number, data.size);
            updateTeamRankingPagination(data);
        });
}

function updateTeamRankingTable(teams, page, size) {
    const tbody = document.getElementById('teamRankingTableBody');
    tbody.innerHTML = '';
    teams.forEach((team, idx) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${page * size + idx + 1}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900"><a href="/teams/${team.id}/members/leaderboard-page" class="text-indigo-600 hover:underline">${team.name}</a></td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${team.memberCount}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${team.kudosCount}</td>
        `;
        tbody.appendChild(row);
    });
}

function updateTeamRankingPagination(data) {
    document.getElementById('teamRankingPageInfo').textContent = `Page ${data.number + 1} of ${data.totalPages}`;
    document.getElementById('teamRankingPrevPage').disabled = data.first;
    document.getElementById('teamRankingNextPage').disabled = data.last;
}

document.getElementById('teamRankingPrevPage').addEventListener('click', function() {
    if (teamRankingCurrentPage > 0) {
        teamRankingCurrentPage--;
        loadTeamRanking();
    }
});
document.getElementById('teamRankingNextPage').addEventListener('click', function() {
    teamRankingCurrentPage++;
    loadTeamRanking();
});
document.getElementById('teamRankingPageSize').addEventListener('change', function() {
    teamRankingPageSize = parseInt(this.value);
    teamRankingCurrentPage = 0;
    loadTeamRanking();
});

document.addEventListener('DOMContentLoaded', loadTeamRanking); 