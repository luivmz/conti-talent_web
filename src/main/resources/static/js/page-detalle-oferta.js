/* =========================================================
   page-detalle-oferta.js — Detalle de oferta + CTA Postular
   ========================================================= */

(() => {
  const params = new URLSearchParams(window.location.search);
  const id     = params.get('id');

  const init = async () => {
    await Storage.ready;
    const oferta = Ofertas.get(id);
    if (!oferta) {
      const main = document.getElementById('detalle-main');
      if (main) main.innerHTML = '<div class="empty-state"><h3>Oferta no encontrada</h3><p>La oferta que buscas no existe o fue retirada.</p></div>';
      return;
    }

    const area = Areas.get(oferta.areaId);

    UI.$('#detalle-title').textContent     = oferta.titulo;
    UI.$('#detalle-area').textContent      = area?.nombre || '—';
    UI.$('#detalle-modalidad').textContent = oferta.modalidad;
    UI.$('#detalle-ubicacion').textContent = oferta.ubicacion;
    UI.$('#detalle-vacantes').textContent  = `${oferta.vacantes} vacantes`;
    UI.$('#detalle-fecha').textContent     = UI.formatDate(oferta.creadaEn);
    UI.$('#detalle-descripcion').textContent = oferta.descripcion;

    const tipoEl = UI.$('#detalle-tipo');
    if (tipoEl) {
      tipoEl.textContent = oferta.tipo || 'Trabajo';
      tipoEl.className   = `badge ${oferta.tipo === 'Práctica' ? 'badge--evaluacion' : 'badge--aprobado-tecnico'}`;
    }

    const reqList = UI.$('#detalle-requisitos');
    UI.clear(reqList);
    (oferta.requisitos || []).forEach((r) => reqList.appendChild(UI.el('li', { text: r })));

    const benList = UI.$('#detalle-beneficios');
    UI.clear(benList);
    (oferta.beneficios || []).forEach((b) => benList.appendChild(UI.el('li', { text: b })));

    const cta = UI.$('#detalle-cta');
    cta.href = `postular.html?oferta=${oferta.id}`;
  };

  document.addEventListener('DOMContentLoaded', init);
})();
