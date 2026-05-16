/* =========================================================
   page-mis-postulaciones.js - Resumen unico de postulaciones
   ========================================================= */

(() => {
  const init = async () => {
    await Storage.ready;
    if (!Auth.requireAuth('login.html?next=' + encodeURIComponent(window.location.href))) return;
    const session = Auth.getSession();
    const wrap = document.getElementById('postulaciones-list');
    const postulaciones = Postulantes.byUsuario(session.id)
      .sort((a, b) => (UI.dateValue(b.fechaPostulacion || b.creadoEn)?.getTime() || 0) - (UI.dateValue(a.fechaPostulacion || a.creadoEn)?.getTime() || 0));
    UI.clear(wrap);

    if (postulaciones.length === 0) {
      wrap.appendChild(UI.el('div', { class: 'empty-state' }, [
        UI.el('div', { class: 'empty-state__icon', text: '📋' }),
        UI.el('h3', { text: 'Aun no tienes postulaciones' }),
        UI.el('p', { class: 'muted', text: 'Postula a una oferta para iniciar tu proceso de seleccion.' }),
        UI.el('a', { href: 'ofertas.html', class: 'btn btn--primary', style: 'margin-top:12px', text: 'Ver ofertas' })
      ]));
      return;
    }

    postulaciones.forEach((p) => wrap.appendChild(renderCard(p)));
  };

  const renderCard = (p) => {
    const oferta = Ofertas.get(p.ofertaId);
    const area = oferta ? Areas.get(oferta.areaId) : null;
    const final = p.puntajeFinal ?? p.puntaje ?? 0;
    const yaRindio = Postulantes.hasRespuestas(p);
    const fechaPostulacion = p.fechaPostulacion || p.creadoEn;

    const primaryAction = yaRindio
      ? UI.el('a', { href: `mi-proceso.html?postulante=${p.id}`, class: 'btn btn--primary', text: 'Ver proceso' })
      : UI.el('a', { href: `evaluacion.html?postulante=${p.id}`, class: 'btn btn--primary', text: 'Rendir evaluacion' });
    const secondaryAction = yaRindio
      ? UI.el('a', { href: `mis-respuestas.html?postulante=${p.id}`, class: 'btn btn--ghost', text: 'Respuestas' })
      : UI.el('a', { href: `mi-proceso.html?postulante=${p.id}`, class: 'btn btn--ghost', text: 'Proceso' });

    return UI.el('article', { class: 'postulacion-card anim-fade-up' }, [
      UI.el('header', { class: 'flex between gap-3' }, [
        UI.el('div', {}, [
          UI.el('h3', { text: oferta?.titulo || 'Oferta no disponible' }),
          UI.el('p', { class: 'muted', text: `${area?.nombre || 'Area no definida'} · ${oferta?.modalidad || '-'} · ${oferta?.ubicacion || '-'}` })
        ]),
        UI.renderEstadoBadge(p.estado)
      ]),
      UI.el('div', { class: 'postulacion-summary' }, [
        summaryItem('Fecha postulacion', UI.formatDate(fechaPostulacion)),
        summaryItem('Puntaje final', `${final} pts`),
        summaryItem('Evaluacion', p.fechaEvaluacion ? UI.formatDate(p.fechaEvaluacion) : 'Pendiente')
      ]),
      UI.el('div', { class: 'progress', style: 'margin: 14px 0;' }, [
        UI.el('div', { class: 'progress__bar', style: `width:${Math.min(100, final)}%` })
      ]),
      UI.el('footer', { class: 'flex between gap-3' }, [
        UI.el('span', { class: 'soft', text: `Tec ${p.puntajeCuestionario ?? p.puntaje ?? 0} · Exp ${p.puntajeExperiencia ?? 0} · Hab ${p.puntajeHabilidades ?? 0}` }),
        UI.el('div', { class: 'flex gap-2' }, [secondaryAction, primaryAction])
      ])
    ]);
  };

  const summaryItem = (label, value) => UI.el('div', {}, [
    UI.el('span', { text: label }),
    UI.el('strong', { text: value })
  ]);

  document.addEventListener('DOMContentLoaded', init);
})();
