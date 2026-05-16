/* =========================================================
   page-admin-metricas.js — 5 métricas en series de tiempo + 5 puntuales
   Estilo idéntico al ejemplo "Ventas (S/.) durante los últimos 8 meses"
   ========================================================= */

(() => {
  const COLORS = ['#8b95ff', '#6ee7b7', '#fcd34d', '#7dd3fc', '#fda4af'];

  const init = async () => {
    await Storage.ready;
    if (!Auth.requireAdmin('../login.html')) return;
    renderTimeSeriesCharts();
    renderPunctualKPIs();
    renderRanking();
    setupRankingFilter();
    bindResize();
  };

  const renderTimeSeriesCharts = () => {
    const data = Storage.read('metricas', { series: {} });
    const wrap = document.getElementById('charts-grid');
    UI.clear(wrap);
    const entries = Object.entries(data.series || {});
    entries.forEach(([key, dataset], i) => {
      const card    = UI.el('section', { class: 'chart-card', 'data-chart-key': key });
      const head    = UI.el('header', { class: 'chart-card__head' }, [
        UI.el('div', {}, [
          UI.el('h4', { text: dataset.label }),
          UI.el('p', { class: 'soft', style: 'font-size:.82rem;margin:0', text: `Serie de tiempo · últimos ${dataset.puntos.length} meses` })
        ]),
        UI.el('span', { class: 'badge badge--evaluacion', text: dataset.unidad })
      ]);
      const canvas  = UI.el('div', { class: 'chart-canvas' });
      card.appendChild(head);
      card.appendChild(canvas);
      wrap.appendChild(card);
      requestAnimationFrame(() => Chart.lineChart(canvas, { ...dataset, color: COLORS[i % COLORS.length] }));
    });
  };

  const renderPunctualKPIs = () => {
    const data = Storage.read('metricas', { estadoActual: {} });
    const wrap = document.getElementById('punctual-kpis');
    UI.clear(wrap);

    const punctualSeries = [
      { label: 'Postulantes activos hoy',         value: data.estadoActual.postulantesActivos, unit: '' },
      { label: 'Ofertas abiertas',                value: data.estadoActual.ofertasAbiertas,    unit: '' },
      { label: 'Entrevistas activas',             value: data.estadoActual.entrevistasHoy,     unit: '' },
      { label: 'Postulantes aceptados',           value: data.estadoActual.ofertasEsteMes,     unit: '' },
      { label: 'Tiempo promedio de cierre',       value: data.estadoActual.tiempoPromedio,     unit: '' }
    ];

    punctualSeries.forEach((kpi, i) => {
      wrap.appendChild(UI.el('div', { class: `kpi anim-fade-up delay-${i + 1}` }, [
        UI.el('div', { class: 'kpi__label', text: kpi.label }),
        UI.el('div', { class: 'kpi__value', text: kpi.value }),
        UI.el('div', { class: 'kpi__delta up', text: '↑ tendencia positiva' })
      ]));
    });
  };

  const setupRankingFilter = () => {
    const sel = document.getElementById('ranking-oferta-filter');
    Ofertas.list().forEach((o) => sel.appendChild(UI.el('option', { value: o.id, text: o.titulo })));
    const estadoSel = document.getElementById('ranking-estado-filter');
    Object.entries(UI.ESTADOS).forEach(([k, v]) => estadoSel.appendChild(UI.el('option', { value: k, text: v.label })));
    const areaSel = document.getElementById('ranking-area-filter');
    Areas.list().forEach((a) => areaSel.appendChild(UI.el('option', { value: a.id, text: a.nombre })));
    [sel, estadoSel, areaSel].forEach((control) => control.addEventListener('change', renderRanking));
  };

  const renderRanking = () => {
    const ofertaId = document.getElementById('ranking-oferta-filter').value || null;
    const estado = document.getElementById('ranking-estado-filter').value || null;
    const area = document.getElementById('ranking-area-filter').value || null;
    const ranking  = Postulantes.ranking(ofertaId)
      .filter((p) => !estado || p.estado === estado)
      .filter((p) => {
        if (!area) return true;
        const oferta = Ofertas.get(p.ofertaId);
        return oferta && String(oferta.areaId) === String(area);
      })
      .filter((p) => p.estado !== 'RECHAZADO')
      .slice(0, 10);
    const wrap     = document.getElementById('ranking-list');
    UI.clear(wrap);
    if (ranking.length === 0) {
      wrap.appendChild(UI.el('div', { class: 'empty-state', html: '<p>Sin postulantes calificados todavía.</p>' }));
      return;
    }
    ranking.forEach((p, i) => {
      const oferta = Ofertas.get(p.ofertaId);
      const row = UI.el('div', { class: 'ranking-row anim-fade-up' }, [
        UI.el('div', { class: 'ranking-row__pos', text: i + 1 }),
        UI.el('div', {}, [
          UI.el('div', { class: 'ranking-row__name', text: p.nombre }),
          UI.el('div', { class: 'ranking-row__sub',  text: oferta?.titulo || '—' })
        ]),
        UI.renderEstadoBadge(p.estado),
        UI.el('div', { class: 'ranking-row__score', text: `${p.puntajeFinal ?? p.puntaje}` }),
        UI.el('div', { class: 'ranking-row__sub', text: `Tec ${p.puntajeCuestionario ?? p.puntaje} · Exp ${p.puntajeExperiencia ?? 0} · Hab ${p.puntajeHabilidades ?? 0}` })
      ]);
      wrap.appendChild(row);
    });
  };

  const bindResize = () => {
    let timer;
    window.addEventListener('resize', () => {
      clearTimeout(timer);
      timer = setTimeout(renderTimeSeriesCharts, 200);
    });
  };

  document.addEventListener('DOMContentLoaded', init);
})();
