// Prendi il JSON dai data-attribute e parsalo
const buildingsDataDiv = document.getElementById('buildings-data');
const buildings = JSON.parse(buildingsDataDiv.getAttribute('data-buildings'));

// Inizializza la mappa
var map = L.map('map', { fullscreenControl: true }).setView([42.35, 13.40], 6);

// Layer OpenStreetMap
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
}).addTo(map);

// Definisci un'icona rossa per il marker speciale
const redIcon = L.icon({
    iconUrl: "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png",
    shadowUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png",
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
});

if (buildings.length === 1) {
    // Pagina DETTAGLI
    const b = buildings[0];

    // Centra la mappa sull'edificio
    map.setView([b.latitude, b.longitude], 13);

    // Marker rosso
    L.marker([b.latitude, b.longitude], { icon: redIcon })
        .addTo(map)
        .bindPopup(`<b>${b.name}</b><br/>Lat: ${b.latitude}<br/>Lon: ${b.longitude}`);
} else {
    // Pagina MAPPA
    const markers = buildings.map(b =>
        L.marker([b.latitude, b.longitude])
            .bindPopup(`<b>${b.name}</b><br/>Lat: ${b.latitude}<br/>Lon: ${b.longitude}<br/>
                        <a href='/buildings/${b.id}'>Vai al dettaglio</a>`)
    );
    const group = L.featureGroup(markers).addTo(map);
    map.fitBounds(group.getBounds());
}

// Personalizzazione del pulsante fullscreen
document.addEventListener("DOMContentLoaded", function() {
    const btn = document.querySelector('.leaflet-control-zoom-fullscreen.fullscreen-icon');
    if (btn) {
        // aggiungi immagine al pulsante
        btn.innerHTML = '<img src="/images/icon.svg" style="width:20px;height:20px;display:block;margin:auto;">';
        btn.style.display = 'flex';
        btn.style.alignItems = 'center';
        btn.style.justifyContent = 'center';
    }
});

// Mantieni l’icona anche dopo il toggle fullscreen
map.on('fullscreenchange', function() {
    const btn = document.querySelector('.leaflet-control-zoom-fullscreen.fullscreen-icon');
    if (btn && btn.querySelector('img') === null) {
        btn.innerHTML = '<img src="/images/icon.svg" style="width:20px;height:20px;display:block;margin:auto;">';
    }
});


