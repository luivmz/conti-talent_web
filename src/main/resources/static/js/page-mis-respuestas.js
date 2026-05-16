/* =========================================================
   page-mis-respuestas.js — Vista de SOLO LECTURA
   Muestra puntaje y las preguntas con la respuesta marcada
   por el postulante (más la correcta para feedback).
   No permite editar ni reenviar la evaluación.
   ========================================================= */

(() => {
  const params       = new URLSearchParams(window.location.search);
  const postulanteId = params.get('postulante');

  const init = async () => {
    await Storage.ready;
    if (!Auth.requireAuth('login.html')) return;

    const postulante = Postulantes.get(postulanteId);
    if (!postulante) {
      UI.showToast('No se encontró la postulación', 'error');
      setTimeout(() => window.location.href = 'mis-postulaciones.html', 800);
      return;
    }

    const session = Auth.getSession();
    if (postulante.usuarioId && postulante.usuarioId !== session.id && session.rol !== 'admin') {
      UI.showToast('No tienes acceso a estas respuestas', 'error');
      setTimeout(() => window.location.href = 'mis-postulaciones.html', 800);
      return;
    }

    const oferta    = Ofertas.get(postulante.ofertaId);
    const preguntas = Evaluacion.byOferta(postulante.ofertaId);
    const respuestas = postulante.respuestas || {};

    renderHeader(postulante, oferta);
    renderResumen(postulante, preguntas, respuestas);
    renderPreguntas(preguntas, respuestas);
  };

  const renderHeader = (p, oferta) => {
    document.getElementById('respuestas-title').textContent = oferta?.titulo || '—';
    document.getElementById('respuestas-area').textContent  = Areas.get(oferta?.areaId)?.nombre || '—';
    const badge = UI.renderEstadoBadge(p.estado);
    document.getElementById('respuestas-estado').appendChild(badge);
    document.getElementById('respuestas-fecha').textContent = `Evaluado · ${UI.formatDate(p.creadoEn)}`;
  };

  const renderResumen = (p, preguntas, respuestas) => {
    const correctas = preguntas.filter((q) => respuestas[q.id] === q.correcta).length;
    const total     = preguntas.length;
    const wrap      = document.getElementById('respuestas-summary');
    UI.clear(wrap);
    [
      { label: 'Puntaje',   value: `${p.puntaje}` },
      { label: 'Correctas', value: total > 0 ? `${correctas} / ${total}` : '—' },
      { label: 'Estado',    value: UI.ESTADOS[p.estado]?.label || p.estado }
    ].forEach((kpi) => {
      wrap.appendChild(UI.el('div', { class: 'kpi' }, [
        UI.el('div', { class: 'kpi__label', text: kpi.label }),
        UI.el('div', { class: 'kpi__value', text: kpi.value })
      ]));
    });
  };

  const renderPreguntas = (preguntas, respuestas) => {
    const wrap = document.getElementById('respuestas-list');
    UI.clear(wrap);

    if (preguntas.length === 0) {
      wrap.appendChild(UI.el('div', { class: 'empty-state' }, [
        UI.el('div', { class: 'empty-state__icon', text: '📋' }),
        UI.el('h3', { text: 'Esta oferta no tiene cuestionario' }),
        UI.el('p',  { class: 'muted', text: 'Tu evaluación fue registrada manualmente por el equipo de selección.' })
      ]));
      return;
    }

    if (Object.keys(respuestas).length === 0) {
      wrap.appendChild(UI.el('div', { class: 'empty-state' }, [
        UI.el('div', { class: 'empty-state__icon', text: '📝' }),
        UI.el('h3', { text: 'Sin respuestas registradas' }),
        UI.el('p',  { class: 'muted', text: 'Todavía no has rendido la evaluación.' })
      ]));
      return;
    }

    preguntas.forEach((q, idx) => {
      const elegida   = respuestas[q.id];
      const acerto    = elegida === q.correcta;
      const card      = UI.el('article', { class: 'question-card anim-fade-up' }, [
        UI.el('div', { class: 'flex gap-2', style: 'align-items:center' }, [
          UI.el('span', { class: 'badge', text: `Pregunta ${idx + 1}` }),
          UI.el('span', { class: `badge ${acerto ? 'badge--aceptado' : 'badge--rechazado'}`, text: acerto ? 'Correcta' : 'Incorrecta' })
        ]),
        UI.el('h3', { text: q.pregunta, style: 'margin-top: 12px;' }),
        UI.el('div', { class: 'options' },
          q.opciones.map((opcion, i) => {
            const tuRespuesta = i === elegida;
            const esCorrecta  = i === q.correcta;
            let cls = 'option';
            if (tuRespuesta && esCorrecta) cls += ' is-selected';
            const styleCorrecta = esCorrecta && !tuRespuesta
              ? 'border-color:rgba(16,185,129,0.45);background:rgba(16,185,129,0.08);'
              : '';
            const styleIncorrecta = tuRespuesta && !esCorrecta
              ? 'border-color:rgba(239,68,68,0.45);background:rgba(239,68,68,0.08);'
              : '';
            const tag = tuRespuesta
              ? UI.el('span', { class: 'badge', style: 'margin-left:auto;', text: 'Tu respuesta' })
              : esCorrecta
                ? UI.el('span', { class: 'badge badge--aceptado', style: 'margin-left:auto;', text: 'Respuesta correcta' })
                : null;
            const children = [
              UI.el('span', { class: 'option__bullet', style: tuRespuesta ? 'border-color:#6366f1;' : (esCorrecta ? 'border-color:#10b981;' : '') }),
              UI.el('span', { text: opcion })
            ];
            if (tag) children.push(tag);
            return UI.el('div', { class: cls, style: `${styleCorrecta}${styleIncorrecta} pointer-events:none;`, role: 'group' }, children);
          })
        )
      ]);
      wrap.appendChild(card);
    });
  };

  document.addEventListener('DOMContentLoaded', init);
})();
