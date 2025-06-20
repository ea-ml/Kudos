// Entity Kudos List JS
const entityType = document.body.getAttribute('data-entity-type') || (window.entityType || null);
const entityId = document.body.getAttribute('data-entity-id') || (window.entityId || null);

function populateKudosTable(tbodyId, kudosList) {
    const tbody = document.getElementById(tbodyId);
    tbody.innerHTML = '';
    if (!kudosList || kudosList.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" class="px-6 py-4 text-center text-gray-500">No kudos found.</td></tr>';
        return;
    }
    kudosList.forEach(kudos => {
        const row = document.createElement('tr');
        const senderDisplay = kudos.isAnonymous ? 'Anonymous' : kudos.senderName;
        row.innerHTML = `
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${kudos.message}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${senderDisplay}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${formatDate(kudos.createdAt)}</td>
        `;
        tbody.appendChild(row);
    });
}

function loadEntityKudos() {
    if (!entityType || !entityId) return;
    fetch(`/kudos?type=${entityType}&id=${entityId}`)
        .then(res => res.json())
        .then(data => {
            populateKudosTable('entityKudosTableBody', data.directKudos);

            if (entityType === 'employee' && data.teamKudos && data.teamKudos.length > 0) {
                const teamKudosSection = document.getElementById('team-kudos-section');
                teamKudosSection.style.display = 'block';
                populateKudosTable('teamKudosTableBody', data.teamKudos);
            }
        });
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: '2-digit' });
}

document.addEventListener('DOMContentLoaded', loadEntityKudos); 