/* =========================================================
   page-home.js — Controlador del Home (renderiza áreas y ofertas destacadas)
   ========================================================= */

(() => {
  const init = async () => {
    await Storage.ready;
    renderAreas();
    renderFeatured();
    renderStats();
  };

  const renderAreas = () => {
    const grid = document.getElementById('home-areas');
    if (!grid) return;
    UI.clear(grid);
    Areas.list().slice(0, 6).forEach((a) => {
      const card = UI.el('a', { href: `ofertas.html?area=${a.id}`, class: 'area-card' }, [
        UI.el('div', { class: 'area-card__icon', text: a.icono, style: `background: linear-gradient(135deg, ${a.color}, #8b5cf6);` }),
        UI.el('h3',  { text: a.nombre }),
        UI.el('p',   { class: 'muted', text: a.descripcion }),
        UI.el('div', { class: 'area-card__count' }, [
          UI.el('span', { text: `${Areas.countOfertasByArea(a.id)} ofertas activas` })
        ])
      ]);
      grid.appendChild(card);
    });
  };

  const renderFeatured = () => {
    const wrap = document.getElementById('home-featured');
    if (!wrap) return;
    UI.clear(wrap);
    Ofertas.featured().slice(0, 3).forEach((o) => {
      const area = Areas.get(o.areaId);
      const card = UI.el('a', { href: `detalle-oferta.html?id=${o.id}`, class: 'featured-card' }, [
        UI.el('div', { class: 'featured-card__tag', text: 'Destacada' }),
        UI.el('span', { class: 'badge', text: area?.nombre || '—' }),
        UI.el('h3',  { class: 'card__title', style: 'margin-top: 12px;', text: o.titulo }),
        UI.el('p',   { class: 'muted', text: `${o.modalidad} · ${o.ubicacion}` }),
        UI.el('div', { class: 'oferta-card__meta' }, [
          UI.el('span', { text: `${o.vacantes} vacante(s)` }),
          UI.el('span', { text: UI.formatDate(o.creadaEn) })
        ])
      ]);
      wrap.appendChild(card);
    });
  };

  const renderStats = () => {
    const slot = document.getElementById('home-stats');
    if (!slot) return;
    const ofertasCount     = Ofertas.list().length;
    const areasCount       = Areas.list().length;
    const postulantesCount = Postulantes.list().length;
    const aceptadosCount   = Postulantes.list().filter((p) => p.estado === 'ACEPTADO').length;
    UI.clear(slot);
    [
      { label: 'Áreas',       value: areasCount },
      { label: 'Ofertas',     value: ofertasCount },
      { label: 'Postulantes', value: postulantesCount },
      { label: 'Aceptados',   value: aceptadosCount }
    ].forEach((kpi, i) => {
      slot.appendChild(UI.el('div', { class: `kpi anim-fade-up delay-${i + 1}` }, [
        UI.el('div', { class: 'kpi__label', text: kpi.label }),
        UI.el('div', { class: 'kpi__value', text: kpi.value })
      ]));
    });
  };

  document.addEventListener('DOMContentLoaded', init);
})();
