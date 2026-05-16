/* =========================================================
   page-admin-usuarios.js — CRUD de usuarios
   ========================================================= */

(() => {
  const init = async () => {
    await Storage.ready;
    if (!Auth.requireAdmin('../login.html')) return;
    document.getElementById('btn-nuevo-usuario').addEventListener('click', () => openForm());
    document.getElementById('search-usuario').addEventListener('input', renderTable);
    document.getElementById('filter-rol').addEventListener('change', renderTable);
    renderTable();
  };

  const renderTable = () => {
    const tbody  = document.getElementById('usuarios-tbody');
    const search = document.getElementById('search-usuario').value.toLowerCase();
    const rol    = document.getElementById('filter-rol').value;
    let items = Usuarios.list();
    if (rol)    items = items.filter((u) => u.rol === rol);
    if (search) items = items.filter((u) => `${u.nombre} ${u.apellido} ${u.email}`.toLowerCase().includes(search));
    UI.clear(tbody);
    if (items.length === 0) {
      tbody.appendChild(UI.el('tr', {}, [UI.el('td', { colspan: 5, class: 'soft', style: 'text-align:center;padding:32px', text: 'No hay usuarios.' })]));
      return;
    }
    items.forEach((u) => {
      const tr = UI.el('tr', {}, [
        UI.el('td', {}, [
          UI.el('div', { class: 'flex gap-2', style: 'align-items:center' }, [
            UI.el('div', { class: 'avatar', text: (u.nombre[0] || '') + (u.apellido[0] || '') }),
            UI.el('div', {}, [
              UI.el('div', { text: `${u.nombre} ${u.apellido}`, style: 'font-weight:500' }),
              UI.el('div', { class: 'soft', style: 'font-size:0.8rem', text: u.email })
            ])
          ])
        ]),
        UI.el('td', {}, [UI.el('span', { class: `badge ${u.rol === 'admin' ? 'badge--entrevista' : 'badge--evaluacion'}`, text: u.rol })]),
        UI.el('td', {}, [UI.el('span', { class: `badge ${u.activo ? 'badge--aceptado' : 'badge--rechazado'}`, text: u.activo ? 'Activo' : 'Inactivo' })]),
        UI.el('td', { text: UI.formatDate(u.creadoEn) }),
        UI.el('td', {}, [
          UI.el('div', { class: 'row-actions' }, [
            UI.el('button', { class: 'btn btn--sm btn--ghost',  text: u.activo ? 'Desactivar' : 'Activar', onClick: () => toggleActivo(u) }),
            UI.el('button', { class: 'btn btn--sm btn--ghost',  text: 'Editar',   onClick: () => openForm(u) }),
            UI.el('button', { class: 'btn btn--sm btn--danger', text: 'Eliminar', onClick: () => onDelete(u) })
          ])
        ])
      ]);
      tbody.appendChild(tr);
    });
  };

  const openForm = (u = null) => {
    const isEdit = !!u;

    const rolSelect = UI.el('select', { class: 'select', name: 'rol' });
    ['postulante', 'admin'].forEach((r) => {
      const opt = UI.el('option', { value: r, text: r });
      if (u && u.rol === r) opt.selected = true;
      rolSelect.appendChild(opt);
    });

    const form = UI.el('form', { class: 'form-grid' }, [
      UI.el('div', { class: 'form-grid form-grid--2' }, [
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Nombre' }),   UI.el('input', { class: 'input', name: 'nombre',   value: u?.nombre   || '' }), UI.el('div', { class: 'error' })]),
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Apellido' }), UI.el('input', { class: 'input', name: 'apellido', value: u?.apellido || '' }), UI.el('div', { class: 'error' })])
      ]),
      UI.el('div', { class: 'field' }, [
        UI.el('label', { text: 'Correo electrónico' }),
        UI.el('input', { class: 'input', type: 'email', name: 'email', value: u?.email || '' }),
        UI.el('div', { class: 'error' })
      ]),
      UI.el('div', { class: 'form-grid form-grid--2' }, [
        UI.el('div', { class: 'field' }, [
          UI.el('label', { text: isEdit ? 'Nueva contraseña (opcional)' : 'Contraseña' }),
          UI.el('input', { class: 'input', type: 'password', name: 'password', value: '' }),
          UI.el('div', { class: 'error' })
        ]),
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Rol' }), rolSelect])
      ])
    ]);

    const submit = UI.el('button', { class: 'btn btn--primary', text: isEdit ? 'Guardar cambios' : 'Crear usuario' });
    const footer = UI.el('footer', { style: 'display:flex;justify-content:flex-end;margin-top:16px;' }, [submit]);
    const m = UI.openModal({ title: isEdit ? 'Editar usuario' : 'Nuevo usuario', content: form, footer });

    submit.addEventListener('click', (e) => {
      e.preventDefault();
      const r = Validators.validateForm(form, {
        nombre:   [{ test: Validators.required, message: 'Nombre obligatorio' }],
        apellido: [{ test: Validators.required, message: 'Apellido obligatorio' }],
        email:    [{ test: Validators.required, message: 'Correo obligatorio' },
                   { test: Validators.isEmail,  message: 'Correo no válido' }],
        password: isEdit
          ? [{ test: (v) => !v || Validators.minLength(v, 6), message: 'Mínimo 6 caracteres' }]
          : [{ test: Validators.required, message: 'Contraseña obligatoria' },
             { test: (v) => Validators.minLength(v, 6), message: 'Mínimo 6 caracteres' }]
      });
      if (!r.valid) return;

      try {
        if (isEdit) {
          const updates = { nombre: r.values.nombre, apellido: r.values.apellido, email: r.values.email, rol: r.values.rol };
          if (r.values.password) updates.password = r.values.password;
          Usuarios.update(u.id, updates);
        } else {
          Usuarios.create(r.values);
        }
        UI.showToast(isEdit ? 'Usuario actualizado' : 'Usuario creado', 'success');
        m.close();
        renderTable();
      } catch (err) {
        UI.showToast(err.message, 'error');
      }
    });
  };

  const toggleActivo = (u) => {
    Usuarios.update(u.id, { activo: !u.activo });
    UI.showToast(u.activo ? 'Usuario desactivado' : 'Usuario activado', 'info');
    renderTable();
  };

  const onDelete = async (u) => {
    const session = Auth.getSession();
    if (session && session.id === u.id) {
      UI.showToast('No puedes eliminar tu propia cuenta', 'error');
      return;
    }
    const ok = await UI.confirm({ title: 'Eliminar usuario', message: `¿Eliminar a ${u.nombre} ${u.apellido}? Esta acción es definitiva.` });
    if (!ok) return;
    Usuarios.remove(u.id);
    UI.showToast('Usuario eliminado', 'success');
    renderTable();
  };

  document.addEventListener('DOMContentLoaded', init);
})();
