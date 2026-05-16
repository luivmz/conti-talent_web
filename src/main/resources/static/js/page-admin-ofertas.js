/* =========================================================
   page-admin-ofertas.js — CRUD de ofertas laborales
   ========================================================= */

(() => {
  const init = async () => {
    await Storage.ready;
    if (!Auth.requireAdmin('../login.html')) return;
    document.getElementById('btn-nueva-oferta').addEventListener('click', () => openForm());
    document.getElementById('search-oferta').addEventListener('input', renderTable);
    document.getElementById('filter-area-admin').addEventListener('change', renderTable);
    populateAreaFilter();
    renderTable();
  };

  const populateAreaFilter = () => {
    const sel = document.getElementById('filter-area-admin');
    Areas.list().forEach((a) => sel.appendChild(UI.el('option', { value: a.id, text: a.nombre })));
  };

  const renderTable = () => {
    const tbody = document.getElementById('ofertas-tbody');
    const search = document.getElementById('search-oferta').value.toLowerCase();
    const areaId = document.getElementById('filter-area-admin').value;
    let items = Ofertas.list();
    if (areaId) items = items.filter((o) => o.areaId === areaId);
    if (search) items = items.filter((o) => o.titulo.toLowerCase().includes(search));
    UI.clear(tbody);
    if (items.length === 0) {
      tbody.appendChild(UI.el('tr', {}, [UI.el('td', { colspan: 7, class: 'soft', style: 'text-align:center; padding: 32px;', text: 'Sin ofertas.' })]));
      return;
    }
    items.forEach((o) => {
      const a = Areas.get(o.areaId);
      const tipoCls = o.tipo === 'Práctica' ? 'badge--evaluacion' : 'badge--aprobado-tecnico';
      const tr = UI.el('tr', {}, [
        UI.el('td', {}, [
          UI.el('div', { class: 'font-medium', text: o.titulo }),
          UI.el('div', { class: 'soft text-xs', text: o.descripcion?.slice(0, 100) || '' }),
          UI.el('div', { class: 'offer-table-meta' }, [
            UI.el('span', { text: `Creada: ${UI.formatDate(o.creadaEn)}` }),
            UI.el('span', { text: `${o.vacantes || 1} vacante(s)` })
          ])
        ]),
        UI.el('td', {}, [UI.el('span', { class: `badge ${tipoCls}`, text: o.tipo || '—' })]),
        UI.el('td', {}, [UI.el('span', { class: 'badge', text: a?.nombre || '—' })]),
        UI.el('td', {}, [
          UI.el('div', { class: 'offer-schedule-cell' }, [
            UI.el('strong', { text: o.modalidad || '-' }),
            UI.el('span', { text: o.ubicacion || '-' }),
            UI.el('small', { text: o.horario || 'Horario por definir' })
          ])
        ]),
        UI.el('td', { text: o.vacantes }),
        UI.el('td', { text: o.destacada ? '⭐ Sí' : '—' }),
        UI.el('td', {}, [
          UI.el('div', { class: 'row-actions' }, [
            UI.el('a', { href: `admin-preguntas.html?oferta=${o.id}`, class: 'btn btn--sm btn--ghost', text: 'Preguntas' }),
            UI.el('button', { class: 'btn btn--sm btn--ghost',  text: 'Editar', onClick: () => openForm(o) }),
            UI.el('button', { class: 'btn btn--sm btn--danger', text: 'Eliminar', onClick: () => onDelete(o) })
          ])
        ])
      ]);
      tbody.appendChild(tr);
    });
  };

  const openForm = (oferta = null) => {
    const isEdit = !!oferta;
    const areaSelect = UI.el('select', { class: 'select', name: 'areaId' });
    Areas.list().forEach((a) => {
      const opt = UI.el('option', { value: a.id, text: a.nombre });
      if (oferta && oferta.areaId === a.id) opt.selected = true;
      areaSelect.appendChild(opt);
    });

    const modSelect = UI.el('select', { class: 'select', name: 'modalidad' });
    ['Presencial', 'Remoto', 'Híbrido'].forEach((m) => {
      const opt = UI.el('option', { value: m, text: m });
      if (oferta && oferta.modalidad === m) opt.selected = true;
      modSelect.appendChild(opt);
    });

    const tipoSelect = UI.el('select', { class: 'select', name: 'tipo' });
    Ofertas.TIPOS.forEach((t) => {
      const opt = UI.el('option', { value: t, text: t });
      if ((oferta && oferta.tipo === t) || (!oferta && t === 'Trabajo')) opt.selected = true;
      tipoSelect.appendChild(opt);
    });

    const reqInput = UI.el('textarea', { class: 'textarea', name: 'requisitos', placeholder: 'Un requisito por línea', text: (oferta?.requisitos || []).join('\n') });
    const benInput = UI.el('textarea', { class: 'textarea', name: 'beneficios', placeholder: 'Un beneficio por línea',  text: (oferta?.beneficios || []).join('\n') });

    const form = UI.el('form', { class: 'form-grid' }, [
      UI.el('div', { class: 'field' }, [
        UI.el('label', { text: 'Título de la oferta' }),
        UI.el('input', { class: 'input', name: 'titulo', value: oferta?.titulo || '' }),
        UI.el('div', { class: 'error' })
      ]),
      UI.el('div', { class: 'form-grid form-grid--2' }, [
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Tipo de oferta' }), tipoSelect]),
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Modalidad' }), modSelect])
      ]),
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Área' }), areaSelect]),
      UI.el('div', { class: 'form-grid form-grid--2' }, [
        UI.el('div', { class: 'field' }, [
          UI.el('label', { text: 'Ubicación' }),
          UI.el('input', { class: 'input', name: 'ubicacion', value: oferta?.ubicacion || '' })
        ]),
        UI.el('div', { class: 'field' }, [
          UI.el('label', { text: 'Vacantes' }),
          UI.el('input', { class: 'input', type: 'number', min: 1, name: 'vacantes', value: oferta?.vacantes || 1 })
        ])
      ]),
      UI.el('div', { class: 'field' }, [
        UI.el('label', { text: 'Horario / jornada' }),
        UI.el('input', { class: 'input', name: 'horario', value: oferta?.horario || '', placeholder: 'Ej. Lunes a viernes 08:30 - 17:30' })
      ]),
      UI.el('div', { class: 'field' }, [
        UI.el('label', { text: 'Descripción' }),
        UI.el('textarea', { class: 'textarea', name: 'descripcion', text: oferta?.descripcion || '' }),
        UI.el('div', { class: 'error' })
      ]),
      UI.el('div', { class: 'form-grid form-grid--2' }, [
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Requisitos' }), reqInput]),
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Beneficios' }), benInput])
      ]),
      UI.el('label', { class: 'checkbox' }, [
        UI.el('input', { type: 'checkbox', name: 'destacada', checked: oferta?.destacada ? 'checked' : null }),
        UI.el('span', { text: 'Marcar como oferta destacada' })
      ])
    ]);

    const submit = UI.el('button', { class: 'btn btn--primary', text: isEdit ? 'Guardar cambios' : 'Crear oferta' });
    const footer = UI.el('footer', { style: 'display:flex; justify-content:flex-end; gap:8px; margin-top:16px;' }, [submit]);
    const m = UI.openModal({ title: isEdit ? 'Editar oferta' : 'Nueva oferta', content: form, footer });

    submit.addEventListener('click', (e) => {
      e.preventDefault();
      const r = Validators.validateForm(form, {
        titulo:      [{ test: Validators.required, message: 'Título obligatorio' }],
        descripcion: [{ test: Validators.required, message: 'Descripción obligatoria' }]
      });
      if (!r.valid) return;
      const data = {
        ...r.values,
        requisitos: reqInput.value.split('\n').map((s) => s.trim()).filter(Boolean),
        beneficios: benInput.value.split('\n').map((s) => s.trim()).filter(Boolean)
      };
      if (isEdit) Ofertas.update(oferta.id, data);
      else Ofertas.create(data);
      UI.showToast(isEdit ? 'Oferta actualizada' : 'Oferta creada', 'success');
      m.close();
      renderTable();
    });
  };

  const onDelete = async (oferta) => {
    const ok = await UI.confirm({ title: 'Eliminar oferta', message: `¿Eliminar "${oferta.titulo}"? Se eliminarán también sus preguntas asociadas.` });
    if (!ok) return;
    Ofertas.remove(oferta.id);
    UI.showToast('Oferta eliminada', 'success');
    renderTable();
  };

  document.addEventListener('DOMContentLoaded', init);
})();
