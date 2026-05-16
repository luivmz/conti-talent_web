/* =========================================================
   page-ofertas.js — Listado de ofertas
   Filtros: búsqueda, área, modalidad y tipo (Práctica / Trabajo).
   ========================================================= */

(() => {
  const params = new URLSearchParams(window.location.search);

  const init = async () => {
    await Storage.ready;
    populateAreaFilter();
    populateTipoFilter();

    document.getElementById('filter-area').addEventListener('change', renderList);
    document.getElementById('filter-search').addEventListener('input', renderList);
    document.getElementById('filter-modalidad').addEventListener('change', renderList);
    document.getElementById('filter-tipo').addEventListener('change', renderList);

    const initialArea = params.get('area');
    const initialTipo = params.get('tipo');
    if (initialArea) document.getElementById('filter-area').value = initialArea;
    if (initialTipo) document.getElementById('filter-tipo').value = initialTipo;

    renderList();
  };

  const populateAreaFilter = () => {
    const select = document.getElementById('filter-area');
    if (!select) return;
    Areas.list().forEach((a) => select.appendChild(UI.el('option', { value: a.id, text: a.nombre })));
  };

  const populateTipoFilter = () => {
    const select = document.getElementById('filter-tipo');
    if (!select) return;
    Ofertas.TIPOS.forEach((t) => select.appendChild(UI.el('option', { value: t, text: t })));
  };

  const renderList = () => {
    const wrap = document.getElementById('ofertas-list');
    if (!wrap) return;

    const area      = document.getElementById('filter-area').value;
    const search    = document.getElementById('filter-search').value.toLowerCase().trim();
    const modalidad = document.getElementById('filter-modalidad').value;
    const tipo      = document.getElementById('filter-tipo').value;

    let items = Ofertas.list();
    if (area)      items = items.filter((o) => o.areaId === area);
    if (modalidad) items = items.filter((o) => o.modalidad === modalidad);
    if (tipo)      items = items.filter((o) => o.tipo === tipo);
    if (search)    items = items.filter((o) => o.titulo.toLowerCase().includes(search));

    UI.clear(wrap);
    if (items.length === 0) {
      wrap.appendChild(UI.el('div', { class: 'empty-state' }, [
        UI.el('div', { class: 'empty-state__icon', text: '🔎' }),
        UI.el('p',   { text: 'No encontramos ofertas con esos filtros.' })
      ]));
      return;
    }

    items.forEach((o) => {
      const a = Areas.get(o.areaId);
      const tipoBadgeMod = o.tipo === 'Práctica' ? 'badge--evaluacion' : 'badge--aprobado-tecnico';
      const card = UI.el('a', { href: `detalle-oferta.html?id=${o.id}`, class: 'oferta-card' }, [
        UI.el('div', {}, [
          UI.el('div', { class: 'flex gap-2 flex-wrap' }, [
            UI.el('span', { class: `badge ${tipoBadgeMod}`, text: o.tipo }),
            UI.el('span', { class: 'badge', text: a?.nombre || '—' })
          ]),
          UI.el('h4',   { class: 'card__title mt-2', text: o.titulo }),
          UI.el('p',    { class: 'muted', text: o.descripcion.slice(0, 140) + (o.descripcion.length > 140 ? '…' : '') }),
          UI.el('div',  { class: 'oferta-card__meta' }, [
            UI.el('span', { text: `📍 ${o.ubicacion}` }),
            UI.el('span', { text: `🌐 ${o.modalidad}` }),
            UI.el('span', { text: `👥 ${o.vacantes} vacantes` }),
            UI.el('span', { text: `📅 ${UI.formatDate(o.creadaEn)}` })
          ])
        ]),
        UI.el('div', { class: 'oferta-card__cta' }, [
          UI.el('span', { class: 'btn btn--primary btn--sm', text: 'Ver oferta' })
        ])
      ]);
      wrap.appendChild(card);
    });
  };

  document.addEventListener('DOMContentLoaded', init);
})();
