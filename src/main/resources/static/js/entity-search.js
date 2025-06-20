// Entity Search Bar Logic
const input = document.getElementById('entitySearchInput');
const dropdown = document.getElementById('entitySearchDropdown');
let timeout = null;

input.addEventListener('input', function() {
    clearTimeout(timeout);
    const query = input.value.trim();
    if (!query) {
        dropdown.classList.add('hidden');
        dropdown.innerHTML = '';
        return;
    }
    timeout = setTimeout(() => {
        fetch(`/search/entities?query=${encodeURIComponent(query)}`)
            .then(res => res.json())
            .then(data => {
                renderDropdown(data);
            });
    }, 250);
});

document.addEventListener('click', function(e) {
    if (!dropdown.contains(e.target) && e.target !== input) {
        dropdown.classList.add('hidden');
    }
});

function renderDropdown(data) {
    let html = '';
    if (data.employees.length > 0) {
        html += '<div class="px-3 py-1 text-xs text-gray-500">Employees</div>';
        data.employees.forEach(emp => {
            html += `<div class="px-4 py-2 cursor-pointer hover:bg-indigo-50" data-type="employee" data-id="${emp.id}">${emp.name}</div>`;
        });
    }
    if (data.teams.length > 0) {
        html += '<div class="px-3 py-1 text-xs text-gray-500">Teams</div>';
        data.teams.forEach(team => {
            html += `<div class="px-4 py-2 cursor-pointer hover:bg-indigo-50" data-type="team" data-id="${team.id}">${team.name}</div>`;
        });
    }
    if (!html) {
        html = '<div class="px-4 py-2 text-gray-500">No results found</div>';
    }
    dropdown.innerHTML = html;
    dropdown.classList.remove('hidden');
    Array.from(dropdown.querySelectorAll('[data-type]')).forEach(item => {
        item.addEventListener('click', function() {
            const type = this.getAttribute('data-type');
            const id = this.getAttribute('data-id');
            if (type === 'employee') {
                window.location.href = `/employees/${id}/kudos`;
            } else {
                window.location.href = `/teams/${id}/kudos`;
            }
        });
    });
} 