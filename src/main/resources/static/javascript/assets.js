
let currentAssetPage = 0;
let currentAssetSize = 5;
let currentAssetSort = "name,asc";

// Carica gli asset via REST
async function loadAssets(buildingId, page = currentAssetPage, size = currentAssetSize, sort = currentAssetSort) {
    const token = document.querySelector('meta[name="_csrf"]').content;

    const response = await fetch(`/api/buildings/${buildingId}/assets?page=${page}&size=${size}&sort=${sort}`, {
        headers: { 'X-CSRF-TOKEN': token }
    });
    const data = await response.json();

    currentAssetPage = data.number;
    currentAssetSize = data.size;
    currentAssetSort = sort;

    renderAssetTable(data, buildingId);
    renderAssetPager(data, buildingId);
    renderAssetInfo(data);
    updateAssetPageSizeSelect();
}

// Render della tabella asset
function renderAssetTable(data, buildingId) {
    const container = document.getElementById("assetTableContainer");
    if (!container) return;

    if (data.totalElements === 0) {
        container.innerHTML = "<p>Non ci sono asset presenti nell'edificio</p>";
        return;
    }

    const rows = data.content.map(a => `
        <tr>
            <td>${a.id}</td>
            <td>${a.name}</td>
            <td>${a.brand}</td>
            <td>${a.type}</td>
            <td>${a.model}</td>
            <td>${a.commProtocol}</td>
            <td>
                <form action="/buildings/${buildingId}/assets/${a.id}/delete" method="post" style="display:inline" onsubmit="return confirm('Eliminare questo asset?');">
                    <input type="hidden" name="_csrf" value="${document.querySelector('meta[name="_csrf"]').content}">
                    <button class="btn btn-danger" type="submit" style="margin-bottom: 4px">Elimina</button>
                </form>
                <button class="btn btn-primary" onclick="location.href='/buildings/${buildingId}/assets/${a.id}/edit'">Modifica</button>
            </td>
        </tr>
    `).join("");

    container.innerHTML = `
        <table class="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th><button class="link" onclick="changeAssetSort(${buildingId}, 'name')">Nome ${getAssetSortArrow('name')}</button></th>
                    <th><button class="link" onclick="changeAssetSort(${buildingId}, 'brand')">Marca ${getAssetSortArrow('brand')}</button></th>
                    <th>Tipo</th>
                    <th>Modello</th>
                    <th><button class="link" onclick="changeAssetSort(${buildingId}, 'commProtocol')">Protocollo ${getAssetSortArrow('commProtocol')}</button></th>
                    <th>Azioni</th>
                </tr>
            </thead>
            <tbody>${rows}</tbody>
        </table>
    `;
}

// Ordinamento per colonna
function changeAssetSort(buildingId, field) {
    const [currentField, direction] = currentAssetSort.split(",");
    let nextDir = "asc";
    if (currentField === field && direction === "asc") nextDir = "desc";
    currentAssetSort = `${field},${nextDir}`;
    loadAssets(buildingId, 0, currentAssetSize, currentAssetSort);
}

function getAssetSortArrow(field) {
    const [currentField, direction] = currentAssetSort.split(",");
    if (currentField !== field) return '';
    return direction === 'asc' ? '↑' : '↓';
}

// Paginazione
function renderAssetPager(data, buildingId) {
    const pager = document.getElementById("assetPager");
    if(!pager) return;

    pager.innerHTML = `
        ${data.number > 0 ? `<button onclick="loadAssets(${buildingId}, ${data.number-1}, ${data.size}, '${currentAssetSort}')">❮</button>` : `<span class="disabled">❮</span>`}
        <span>${data.number+1} / ${data.totalPages}</span>
        ${data.number+1 < data.totalPages ? `<button onclick="loadAssets(${buildingId}, ${data.number+1}, ${data.size}, '${currentAssetSort}')">❯</button>` : `<span class="disabled">❯</span>`}
    `;
}

// Messaggio "mostra X–Y di Z"
function renderAssetInfo(data) {
    const info = document.getElementById("assetInfo");
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
function changeAssetPageSize(buildingId) {
    const select = document.getElementById("assetPageSizeSelect");
    if (!select) return;
    currentAssetSize = parseInt(select.value);
    loadAssets(buildingId, 0, currentAssetSize, currentAssetSort);
}

// Sincronizza il select con il valore corrente
function updateAssetPageSizeSelect() {
    const select = document.getElementById("assetPageSizeSelect");
    if(select) select.value = currentAssetSize;
}

// Caricamento iniziale
document.addEventListener("DOMContentLoaded", () => {
    if(typeof buildingId !== 'undefined') loadAssets(buildingId);
});
