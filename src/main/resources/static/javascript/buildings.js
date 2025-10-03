let currentSort = "name,asc"; // ordinamento iniziale
let currentPage = 0;
let currentSize = 5;          // elementi per pagina iniziali

// funzione principale per caricare i dati
async function loadBuildings(page = currentPage, size = currentSize, sort = currentSort) {
    const response = await fetch(`/api/buildings?page=${page}&size=${size}&sort=${sort}`);
    const data = await response.json();

    currentPage = data.number;
    currentSize = data.size;
    currentSort = sort;

    renderTable(data);
    renderPager(data);
    updatePageSizeSelect();
}

// render della tabella
function renderTable(data) {
    const container = document.getElementById("buildingTableContainer");

    // Se non ci sono edifici
    if (!data.content || data.content.length === 0) {
        container.innerHTML = `
            <p>Nessun edificio trovato.</p>
        `;
        return;
    }


    const rows = data.content.map(b => `
        <tr>
            <td>${b.name}</td>
            <td>${b.address}</td>
            <td>
                <a style="color: black" class="link btn btn-primary" href="/buildings/${b.id}">Visualizza dettagli</a>
                <button class="btn btn-danger" onclick="deleteBuilding(${b.id})">Elimina</button>
            </td>
        </tr>
    `).join("");

    container.innerHTML = `
        <table class="table">
            <thead>
                <tr>
                    <th><button class="btn btn-primary" onclick="changeSort('name')">Nome ${getSortArrow('name')}</button></th>
                    <th><button class="btn btn-primary" onclick="changeSort('address')">Indirizzo ${getSortArrow('address')}</button></th>
                    <th>Azioni</th>
                </tr>
            </thead>
            <tbody>${rows}</tbody>
        </table>
    `;
}


// render del pager
function renderPager(data) {
    const pager = document.getElementById("pagerContainer");

    const start = data.totalElements === 0 ? 0 : data.number * data.size + 1;
    const end = ((data.number + 1) * data.size) > data.totalElements ? data.totalElements : ((data.number + 1) * data.size);
    const total = data.totalElements;
    const resultText = total === 1 ? 'risultato' : 'risultati';

    pager.innerHTML = `
        ${data.number > 0
        ? `<button onclick="loadBuildings(${data.number - 1}, ${data.size}, '${currentSort}')">❮</button>`
        : `<span class="disabled">❮</span>`}
        <span>${data.number + 1} / ${data.totalPages}</span>
        ${data.number + 1 < data.totalPages
        ? `<button onclick="loadBuildings(${data.number + 1}, ${data.size}, '${currentSort}')">❯</button>`
        : `<span class="disabled">❯</span>`}
        
        <span class="pager-info">
            Stai vedendo i risultati dall' <strong>${start}</strong> al <strong>${end}</strong> di totale <strong>${total}</strong> ${resultText}
        </span>
    `;
}

// cambio ordinamento quando clicchi sull'intestazione
function changeSort(field) {
    const [currentField, direction] = currentSort.split(",");
    let nextDir = "asc";
    if (currentField === field && direction === "asc") {
        nextDir = "desc";
    }
    const newSort = `${field},${nextDir}`;
    loadBuildings(0, currentSize, newSort); // reset a pagina 0
}

// freccia per mostrare la direzione del sort
function getSortArrow(field) {
    const [currentField, direction] = currentSort.split(",");
    if (currentField !== field) return '';
    return direction === 'asc' ? '↑' : '↓';
}

// gestione cambio numero elementi per pagina
function changePageSize() {
    const select = document.getElementById("pageSizeSelect");
    const newSize = parseInt(select.value);
    currentSize = newSize;
    loadBuildings(0, currentSize, currentSort); // reset a pagina 0
}

// sincronizza il select con il valore corrente
function updatePageSizeSelect() {
    const select = document.getElementById("pageSizeSelect");
    if(select) select.value = currentSize;
}

async function deleteBuilding(buildingId) {
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    if (!confirm('Eliminare questo edificio?')) return;

    const response = await fetch(`/buildings/${buildingId}/delete`, {
        method: 'POST',
        headers: {
            [csrfHeader]: csrfToken
        },
        credentials: 'same-origin'
    });

    if (response.ok) {
        loadBuildings(currentPage, currentSize, currentSort); // ricarica la tabella
    } else {
        alert('Errore durante l\'eliminazione.');
    }
}


// carica la prima pagina al caricamento della pagina
document.addEventListener("DOMContentLoaded", () => loadBuildings());
