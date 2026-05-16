/* =========================================================
   page-admin-preguntas.js — CRUD de preguntas por oferta
   ========================================================= */

(() => {
  const params   = new URLSearchParams(window.location.search);
  let ofertaId   = params.get('oferta') || '';

  const init = async () => {
    await Storage.ready;
    if (!Auth.requireAdmin('../login.html')) return;
    populateOfertaSelect();
    document.getElementById('preguntas-oferta-select').addEventListener('change', (e) => {
      ofertaId = e.target.value;
      const url = new URL(window.location.href);
      url.searchParams.set('oferta', ofertaId);
      window.history.replaceState({}, '', url);
      renderList();
    });
    document.getElementById('btn-nueva-pregunta').addEventListener('click', () => openForm());
    if (!ofertaId) ofertaId = Ofertas.list()[0]?.id || '';
    document.getElementById('preguntas-oferta-select').value = ofertaId;
    renderList();
  };

  const populateOfertaSelect = () => {
    const sel = document.getElementById('preguntas-oferta-select');
    Ofertas.list().forEach((o) => sel.appendChild(UI.el('option', { value: o.id, text: o.titulo })));
  };

  const renderList = () => {
    const wrap   = document.getElementById('preguntas-list');
    const oferta = Ofertas.get(ofertaId);
    UI.clear(wrap);
    document.getElementById('preguntas-meta').textContent = oferta ? `Preguntas para "${oferta.titulo}"` : '';
    if (!oferta) {
      wrap.appendChild(UI.el('div', { class: 'empty-state', html: '<p>Selecciona una oferta para administrar sus preguntas.</p>' }));
      return;
    }
    const items = Evaluacion.byOferta(ofertaId);
    if (items.length === 0) {
      wrap.appendChild(UI.el('div', { class: 'empty-state' }, [
        UI.el('div', { class: 'empty-state__icon', text: '📝' }),
        UI.el('p', { text: 'No hay preguntas todavía.' })
      ]));
      return;
    }
    items.forEach((q, i) => {
      const card = UI.el('article', { class: 'card', style: 'margin-bottom:12px' }, [
        UI.el('div', { class: 'flex between gap-3' }, [
          UI.el('div', {}, [
            UI.el('span', { class: 'badge', text: `Pregunta ${i + 1}` }),
            UI.el('h4', { style: 'margin-top:8px', text: q.pregunta }),
            UI.el('ul', { style: 'margin-top:8px;color:var(--color-text-muted);font-size:.9rem;display:grid;gap:4px' },
              q.opciones.map((opt, idx) => UI.el('li', { style: idx === q.correcta ? 'color:#6ee7b7' : '', text: `${idx === q.correcta ? '✓' : '·'} ${opt}` }))
            )
          ]),
          UI.el('div', { class: 'flex gap-2' }, [
            UI.el('button', { class: 'btn btn--sm btn--ghost',  text: 'Editar',   onClick: () => openForm(q) }),
            UI.el('button', { class: 'btn btn--sm btn--danger', text: 'Eliminar', onClick: () => onDelete(q) })
          ])
        ])
      ]);
      wrap.appendChild(card);
    });
  };

  const openForm = (q = null) => {
    const isEdit = !!q;
    const opciones = (q?.opciones || ['', '', '', '']).map((v, i) =>
      UI.el('div', { class: 'field' }, [
        UI.el('label', { text: `Opción ${i + 1}` }),
        UI.el('input', { class: 'input', name: `opcion_${i}`, value: v })
      ])
    );

    const correctaSel = UI.el('select', { class: 'select', name: 'correcta' });
    [0, 1, 2, 3].forEach((i) => {
      const opt = UI.el('option', { value: i, text: `Opción ${i + 1}` });
      if (q && q.correcta === i) opt.selected = true;
      correctaSel.appendChild(opt);
    });

    const form = UI.el('form', { class: 'form-grid' }, [
      UI.el('div', { class: 'field' }, [
        UI.el('label', { text: 'Enunciado' }),
        UI.el('textarea', { class: 'textarea', name: 'pregunta', text: q?.pregunta || '' }),
        UI.el('div', { class: 'error' })
      ]),
      UI.el('div', { class: 'form-grid form-grid--2' }, opciones),
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Respuesta correcta' }), correctaSel])
    ]);

    const submit = UI.el('button', { class: 'btn btn--primary', text: isEdit ? 'Guardar pregunta' : 'Crear pregunta' });
    const footer = UI.el('footer', { style: 'display:flex;justify-content:flex-end;margin-top:16px;' }, [submit]);
    const m = UI.openModal({ title: isEdit ? 'Editar pregunta' : 'Nueva pregunta', content: form, footer });

    submit.addEventListener('click', (e) => {
      e.preventDefault();
      const r = Validators.validateForm(form, {
        pregunta: [{ test: Validators.required, message: 'Enunciado obligatorio' }]
      });
      const opcionesArr = [0, 1, 2, 3].map((i) => form.querySelector(`[name=opcion_${i}]`).value.trim());
      if (opcionesArr.some((o) => !o)) {
        UI.showToast('Completa las 4 opciones', 'error');
        return;
      }
      if (!r.valid) return;
      const data = { ofertaId, pregunta: r.values.pregunta, opciones: opcionesArr, correcta: parseInt(r.values.correcta, 10) };
      if (isEdit) Evaluacion.update(q.id, data); else Evaluacion.create(data);
      UI.showToast(isEdit ? 'Pregunta actualizada' : 'Pregunta creada', 'success');
      m.close();
      renderList();
    });
  };

  const onDelete = async (q) => {
    const ok = await UI.confirm({ title: 'Eliminar pregunta', message: '¿Eliminar esta pregunta del cuestionario?' });
    if (!ok) return;
    Evaluacion.remove(q.id);
    UI.showToast('Pregunta eliminada', 'success');
    renderList();
  };

  document.addEventListener('DOMContentLoaded', init);
})();
