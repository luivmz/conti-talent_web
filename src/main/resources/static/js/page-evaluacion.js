/* =========================================================
   page-evaluacion.js — Cuestionario asociado a oferta
   La evaluación se rinde UNA sola vez. Si ya se rindió,
   se redirige a la vista de respuestas (solo lectura).
   ========================================================= */

(() => {
  const params       = new URLSearchParams(window.location.search);
  const postulanteId = params.get('postulante');

  let postulante = null;
  let oferta     = null;
  let preguntas  = [];
  const respuestas = {};

  const init = async () => {
    await Storage.ready;
    postulante = Postulantes.get(postulanteId);
    if (!postulante) {
      UI.showToast('No se encontró tu postulación', 'error');
      setTimeout(() => window.location.href = 'index.html', 800);
      return;
    }

    // Si ya rindió, no se permite volver a hacerlo: vamos a la vista de respuestas.
    if (Postulantes.hasRespuestas(postulante)) {
      window.location.replace(`mis-respuestas.html?postulante=${postulante.id}`);
      return;
    }

    oferta    = Ofertas.get(postulante.ofertaId);
    preguntas = Evaluacion.byOferta(oferta.id);

    UI.$('#quiz-title').textContent = `Evaluación técnica · ${oferta.titulo}`;
    UI.$('#quiz-area').textContent  = Areas.get(oferta.areaId)?.nombre || '—';

    if (preguntas.length === 0) {
      UI.$('#quiz-form').innerHTML = '<div class="empty-state"><div class="empty-state__icon">📝</div><h3>Aún no hay cuestionario</h3><p>Pronto recibirás una notificación con la evaluación.</p></div>';
      return;
    }

    renderPreguntas();
    document.getElementById('quiz-form').addEventListener('submit', onSubmit);
    updateProgress();
  };

  const renderPreguntas = () => {
    const wrap = document.getElementById('quiz-questions');
    UI.clear(wrap);
    preguntas.forEach((q, idx) => {
      const card = UI.el('article', { class: 'question-card anim-fade-up', 'data-id': q.id }, [
        UI.el('span', { class: 'badge', text: `Pregunta ${idx + 1} de ${preguntas.length}` }),
        UI.el('h3', { text: q.pregunta, style: 'margin-top: 12px;' }),
        UI.el('div', { class: 'options' },
          q.opciones.map((opcion, oIdx) =>
            UI.el('label', { class: 'option', 'data-q': q.id, 'data-i': oIdx, onClick: onSelect }, [
              UI.el('span', { class: 'option__bullet' }),
              UI.el('span', { text: opcion })
            ])
          )
        )
      ]);
      wrap.appendChild(card);
    });
  };

  const onSelect = (e) => {
    const opt = e.currentTarget;
    const qId = opt.dataset.q;
    const i   = parseInt(opt.dataset.i, 10);

    UI.$$(`.option[data-q="${qId}"]`).forEach((o) => o.classList.remove('is-selected'));
    opt.classList.add('is-selected');
    respuestas[qId] = i;
    updateProgress();
  };

  const updateProgress = () => {
    const answered = Object.keys(respuestas).length;
    UI.$('#quiz-progress-text').textContent = `${answered} / ${preguntas.length} respondidas`;
    UI.$('#quiz-progress-bar').style.width  = `${(answered / preguntas.length) * 100}%`;
  };

  const onSubmit = (e) => {
    e.preventDefault();
    if (Object.keys(respuestas).length < preguntas.length) {
      UI.showToast('Responde todas las preguntas', 'error');
      return;
    }
    const result = Evaluacion.calificar(oferta.id, respuestas);
    Postulantes.saveEvaluation(postulante.id, result.puntaje, { ...respuestas });

    UI.openModal({
      title: 'Resultado de tu evaluación',
      content: UI.el('div', {}, [
        UI.el('p', { class: 'muted', text: 'Tu evaluación fue calificada automáticamente. Estos son tus resultados:' }),
        UI.el('div', { class: 'kpi-grid', style: 'margin-top: 16px;' }, [
          UI.el('div', { class: 'kpi' }, [
            UI.el('div', { class: 'kpi__label', text: 'Puntaje' }),
            UI.el('div', { class: 'kpi__value', text: `${result.puntaje}` })
          ]),
          UI.el('div', { class: 'kpi' }, [
            UI.el('div', { class: 'kpi__label', text: 'Correctas' }),
            UI.el('div', { class: 'kpi__value', text: `${result.correctas}/${result.total}` })
          ])
        ]),
        UI.el('p', { style: 'margin-top: 16px;', text: result.puntaje >= 70 ? '¡Has aprobado la evaluación técnica! Tu estado se actualizó.' : 'Has terminado la evaluación. Tu puntaje será revisado.' })
      ]),
      footer: UI.el('footer', { style: 'display:flex;justify-content:flex-end;gap:8px;margin-top:16px;' }, [
        UI.el('a', { href: `mis-respuestas.html?postulante=${postulante.id}`, class: 'btn btn--ghost', text: 'Ver mis respuestas' }),
        UI.el('a', { href: `mi-proceso.html?postulante=${postulante.id}`, class: 'btn btn--primary', text: 'Ver mi proceso' })
      ])
    });
  };

  document.addEventListener('DOMContentLoaded', init);
})();
