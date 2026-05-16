/* =========================================================
   page-areas.js — Listado público de áreas
   ========================================================= */

(() => {
  const init = async () => {
    await Storage.ready;
    const grid = document.getElementById('areas-grid');
    if (!grid) return;
    UI.clear(grid);
    Areas.list().forEach((a, i) => {
      const card = UI.el('a', { href: `ofertas.html?area=${a.id}`, class: `area-card anim-fade-up delay-${(i % 5) + 1}` }, [
        UI.el('div', { class: 'area-card__icon', text: a.icono, style: `background: linear-gradient(135deg, ${a.color}, #8b5cf6);` }),
        UI.el('h3',  { text: a.nombre }),
        UI.el('p',   { class: 'muted', text: a.descripcion }),
        UI.el('div', { class: 'area-card__count' }, [
          UI.el('span', { text: `${Areas.countOfertasByArea(a.id)} ofertas activas →` })
        ])
      ]);
      grid.appendChild(card);
    });
  };

  document.addEventListener('DOMContentLoaded', init);
})();
