/* =========================================================
   page-publicidad.js — Ofertas destacadas y banner promocional
   ========================================================= */

(() => {
  const init = async () => {
    await Storage.ready;
    const grid = document.getElementById('publicidad-grid');
    if (!grid) return;
    UI.clear(grid);
    Ofertas.featured().forEach((o, i) => {
      const a = Areas.get(o.areaId);
      const card = UI.el('a', { href: `detalle-oferta.html?id=${o.id}`, class: `featured-card anim-fade-up delay-${(i % 5) + 1}` }, [
        UI.el('div', { class: 'featured-card__tag', text: 'Top oferta' }),
        UI.el('span', { class: 'badge', text: a?.nombre || '—' }),
        UI.el('h3',   { class: 'card__title', style: 'margin-top: 12px;', text: o.titulo }),
        UI.el('p',    { class: 'muted', text: o.descripcion.slice(0, 120) + '…' }),
        UI.el('div',  { class: 'oferta-card__meta' }, [
          UI.el('span', { text: `📍 ${o.ubicacion}` }),
          UI.el('span', { text: `🌐 ${o.modalidad}` }),
          UI.el('span', { text: `👥 ${o.vacantes} vacantes` })
        ])
      ]);
      grid.appendChild(card);
    });
  };

  document.addEventListener('DOMContentLoaded', init);
})();
