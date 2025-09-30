
let currentClientPage = 0;
let currentClientSize = 5;
let currentClientSort = "name,asc";

// Carica i clienti via REST
async function loadClients(buildingId, page = currentClientPage, size = currentClientSize, sort = currentClientSort) {
    const tokenMeta = document.querySelector('meta[name="_csrf"]');
    const token = tokenMeta ? tokenMeta.content : '';

    try {
        const response = await fetch(`/api/buildings/${buildingId}/clients?page=${page}&size=${size}&sort=${sort}`, {
            headers: { 'X-CSRF-TOKEN': token }
        });

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        const data = await response.json();

        currentClientPage = data.number;
        currentClientSize = data.size;
        currentClientSort = sort;

        renderClientTable(data, buildingId);
        renderClientPager(data, buildingId);
        renderClientInfo(data);
        updateClientPageSizeSelect();
    } catch (err) {
        console.error("Errore caricando i clienti:", err);
        const container = document.getElementById("clientTableContainer");
        if (container) container.innerHTML = "<p>Errore caricando i clienti</p>";
    }
}

// Render della tabella clienti
function renderClientTable(data, buildingId) {
    const container = document.getElementById("clientTableContainer");
    if (!container) return;

    if (data.totalElements === 0) {
        container.innerHTML = "<p>L'edificio non ha inquilini</p>";
        return;
    }

    const rows = data.content.map(c => `
        <tr>
            <td>${c.id}</td>
            <td>${c.name}</td>
            <td>${c.surname}</td>
            <td>${c.email}</td>
            <td>${c.birthDate}</td>
            <td>${c.cellphone}</td>
            <td>
                <form action="/buildings/${buildingId}/clients/${c.id}/delete" method="post" style="display:inline" onsubmit="return confirm('Eliminare questo cliente?');">
                    <input type="hidden" name="_csrf" value="${document.querySelector('meta[name="_csrf"]').content}">
                    <button style="margin-bottom: 4px" class="btn btn-danger" type="submit">Elimina</button>
                </form>
                <button class="btn btn-primary" onclick="location.href='/buildings/${buildingId}/clients/${c.id}/edit'">Modifica</button>
            </td>
        </tr>
    `).join("");

    container.innerHTML = `
        <table class="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th><button class="link" onclick="changeClientSort(${buildingId}, 'name')">Nome ${getClientSortArrow('name')}</button></th>
                    <th><button class="link" onclick="changeClientSort(${buildingId}, 'surname')">Cognome ${getClientSortArrow('surname')}</button></th>
                    <th>Email</th>
                    <th>Data di Nascita</th>
                    <th>Cellulare</th>
                    <th>Azioni</th>
                </tr>
            </thead>
            <tbody>${rows}</tbody>
        </table>
    `;
}

// Ordinamento per colonna
function changeClientSort(buildingId, field) {
    const [currentField, direction] = currentClientSort.split(",");
    let nextDir = "asc";
    if (currentField === field && direction === "asc") nextDir = "desc";
    currentClientSort = `${field},${nextDir}`;
    loadClients(buildingId, 0, currentClientSize, currentClientSort);
}

function getClientSortArrow(field) {
    const [currentField, direction] = currentClientSort.split(",");
    if (currentField !== field) return '';
    return direction === 'asc' ? '↑' : '↓';
}

// Paginazione
function renderClientPager(data, buildingId) {
    const pager = document.getElementById("clientPager");
    if(!pager) return;

    pager.innerHTML = `
        ${data.number > 0 ? `<button onclick="loadClients(${buildingId}, ${data.number-1}, ${data.size}, '${currentClientSort}')">❮</button>` : `<span class="disabled">❮</span>`}
        <span>${data.number+1} / ${data.totalPages}</span>
        ${data.number+1 < data.totalPages ? `<button onclick="loadClients(${buildingId}, ${data.number+1}, ${data.size}, '${currentClientSort}')">❯</button>` : `<span class="disabled">❯</span>`}
    `;
}

// Messaggio "mostra X–Y di Z"
function renderClientInfo(data) {
    const info = document.getElementById("clientInfo");
    if(!info) return;

    if(data.totalElements === 0){
        info.innerHTML = '';
        return;
    }

    const start = data.totalElements === 0 ? 0 : data.number * data.size + 1;
    const end = Math.min((data.number + 1) * data.size, data.totalElements);
    const total = data.totalElements;
    const label = total === 1 ? 'risultato' : 'risultati';

    info.innerHTML = `Stai vedendo i risultati dall'${start} al ${end} di totale ${total} ${label}`;
}

// Cambio numero elementi per pagina
function changeClientPageSize(buildingId) {
    const select = document.getElementById("clientPageSizeSelect");
    if (!select) return;
    currentClientSize = parseInt(select.value);
    loadClients(buildingId, 0, currentClientSize, currentClientSort);
}

// Sincronizza il select con il valore corrente
function updateClientPageSizeSelect() {
    const select = document.getElementById("clientPageSizeSelect");
    if(select) select.value = currentClientSize;
}

// Caricamento iniziale
document.addEventListener("DOMContentLoaded", () => {
    if(typeof buildingId !== 'undefined') loadClients(buildingId);
});
