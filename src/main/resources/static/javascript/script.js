(function () {
    const table = document.getElementById('buildingTable');
    if (!table) return;

    const rows = Array.from(table.querySelectorAll('tbody tr'));

    // Rendi tutte le righe focusabili
    rows.forEach(tr => {
        if (!tr.hasAttribute('tabindex')) {
            tr.setAttribute('tabindex', '0');
        }
    });

    function focusRow(idx) {
        if (idx >= 0 && idx < rows.length) {
            rows[idx].focus();
        }
    }

    function rowIndex(el) {
        return rows.indexOf(el.closest('tr'));
    }

    table.addEventListener('keydown', function (e) {
        const targetRow = e.target.closest('tr[tabindex]');
        if (!targetRow) return;

        let idx = rowIndex(targetRow);
        let handled = true;

        switch (e.key) {
            case 'ArrowDown':
                focusRow(Math.min(idx + 1, rows.length - 1));
                break;
            case 'ArrowUp':
                focusRow(Math.max(idx - 1, 0));
                break;
            case 'Enter':
            case ' ':
                const link = targetRow.querySelector('a[href]');
                const href = targetRow.getAttribute('data-default-href');
                if (link) {
                    link.click();
                } else if (href) {
                    window.location.href = href;
                }
                break;
            default:
                handled = false;
        }

        if (handled) {
            e.preventDefault();
            e.stopPropagation();
        }
    });

    // Clic sulla riga porta focus
    rows.forEach(tr => {
        tr.addEventListener('mousedown', () => tr.focus());
    });
})();

document.addEventListener("DOMContentLoaded", function () {
    const toggleBtn = document.getElementById("togglePassword");
    const input = document.getElementById("password");

    if (!toggleBtn || !input) {
        console.warn("togglePassword o input password non trovati");
        return;
    }

    toggleBtn.addEventListener("click", function () {
        const hidden = input.type === "password";
        input.type = hidden ? "text" : "password";
        this.textContent = hidden ? "🙈" : "👁️";
        this.setAttribute('aria-pressed', String(hidden));
    });
});
