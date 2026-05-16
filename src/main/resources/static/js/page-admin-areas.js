/* =========================================================
   page-admin-areas.js — CRUD de áreas
   ========================================================= */

(() => {
  const ICONOS = ['💻','🎨','📣','🤝','👥','📊','⚙️','🏥','📚','🌐','🔧','🚀'];

  const init = async () => {
    await Storage.ready;
    if (!Auth.requireAdmin('../login.html')) return;
    document.getElementById('btn-nueva-area').addEventListener('click', () => openForm());
    document.getElementById('search-area').addEventListener('input', renderTable);
    renderTable();
  };

  const renderTable = () => {
    const tbody = document.getElementById('areas-tbody');
    const search = document.getElementById('search-area').value.toLowerCase();
    const items = Areas.list().filter((a) => a.nombre.toLowerCase().includes(search));
    UI.clear(tbody);
    if (items.length === 0) {
      tbody.appendChild(UI.el('tr', {}, [UI.el('td', { colspan: 4, class: 'soft', style: 'text-align:center; padding: 32px;', text: 'No hay áreas registradas.' })]));
      return;
    }
    items.forEach((a) => {
      const tr = UI.el('tr', {}, [
        UI.el('td', {}, [
          UI.el('div', { class: 'flex gap-2', style: 'align-items: center;' }, [
            UI.el('div', { class: 'avatar', text: a.icono, style: `background: linear-gradient(135deg, ${a.color}, #8b5cf6);` }),
            UI.el('div', { text: a.nombre, style: 'font-weight: 500;' })
          ])
        ]),
        UI.el('td', { class: 'muted', text: a.descripcion || '—' }),
        UI.el('td', { text: Areas.countOfertasByArea(a.id) }),
        UI.el('td', {}, [
          UI.el('div', { class: 'row-actions' }, [
            UI.el('button', { class: 'btn btn--sm btn--ghost', text: 'Editar', onClick: () => openForm(a) }),
            UI.el('button', { class: 'btn btn--sm btn--danger', text: 'Eliminar', onClick: () => onDelete(a) })
          ])
        ])
      ]);
      tbody.appendChild(tr);
    });
  };

  const openForm = (area = null) => {
    const isEdit = !!area;
    const iconChooser = UI.el('div', { class: 'flex gap-2', style: 'flex-wrap: wrap;' });
    let chosen = area?.icono || ICONOS[0];
    ICONOS.forEach((ic) => {
      const btn = UI.el('button', { type: 'button', class: 'icon-btn', text: ic, style: ic === chosen ? 'background: rgba(99,102,241,0.2); border-color: var(--brand-500);' : '' });
      btn.addEventListener('click', () => {
        chosen = ic;
        Array.from(iconChooser.children).forEach((c) => c.style = '');
        btn.style = 'background: rgba(99,102,241,0.2); border-color: var(--brand-500);';
      });
      iconChooser.appendChild(btn);
    });

    const form = UI.el('form', { class: 'form-grid' }, [
      UI.el('div', { class: 'field' }, [
        UI.el('label', { text: 'Icono' }),
        iconChooser
      ]),
      UI.el('div', { class: 'field' }, [
        UI.el('label', { text: 'Nombre del área' }),
        UI.el('input', { class: 'input', name: 'nombre', value: area?.nombre || '', placeholder: 'Ej. Tecnología' }),
        UI.el('div', { class: 'error' })
      ]),
      UI.el('div', { class: 'field' }, [
        UI.el('label', { text: 'Descripción' }),
        UI.el('textarea', { class: 'textarea', name: 'descripcion', text: area?.descripcion || '', placeholder: 'Breve descripción…' }),
        UI.el('div', { class: 'error' })
      ]),
      UI.el('div', { class: 'field' }, [
        UI.el('label', { text: 'Color (HEX)' }),
        UI.el('input', { class: 'input', name: 'color', value: area?.color || '#6366f1' }),
        UI.el('div', { class: 'error' })
      ])
    ]);

    const submit = UI.el('button', { class: 'btn btn--primary', text: isEdit ? 'Guardar cambios' : 'Crear área' });
    const footer = UI.el('footer', { style: 'display:flex; justify-content:flex-end; gap:8px; margin-top:16px;' }, [submit]);

    const m = UI.openModal({
      title: isEdit ? 'Editar área' : 'Nueva área',
      content: form,
      footer
    });

    submit.addEventListener('click', (e) => {
      e.preventDefault();
      const r = Validators.validateForm(form, {
        nombre: [{ test: Validators.required, message: 'Nombre obligatorio' }]
      });
      if (!r.valid) return;
      const data = { ...r.values, icono: chosen };
      if (isEdit) Areas.update(area.id, data);
      else Areas.create(data);
      UI.showToast(isEdit ? 'Área actualizada' : 'Área creada', 'success');
      m.close();
      renderTable();
    });
  };

  const onDelete = async (area) => {
    if (Areas.countOfertasByArea(area.id) > 0) {
      UI.showToast('No puedes eliminar un área con ofertas activas', 'error');
      return;
    }
    const ok = await UI.confirm({ title: 'Eliminar área', message: `¿Eliminar "${area.nombre}"? Esta acción no se puede deshacer.`, okLabel: 'Eliminar' });
    if (!ok) return;
    Areas.remove(area.id);
    UI.showToast('Área eliminada', 'success');
    renderTable();
  };

  document.addEventListener('DOMContentLoaded', init);
})();
