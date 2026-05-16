/* =========================================================
   page-admin-dashboard.js — Resumen ejecutivo del admin
   ========================================================= */

(() => {
  const init = async () => {
    await Storage.ready;
    if (!Auth.requireAdmin('../login.html')) return;
    renderKPIs();
    renderEstadosBreakdown();
    renderRecientes();
    renderUsuarios();
  };

  const renderKPIs = () => {
    const slot = document.getElementById('admin-kpis');
    const all  = Postulantes.list();
    const usuarios = Usuarios.list();
    const aceptados = all.filter((p) => p.estado === 'ACEPTADO').length;
    const enProceso = all.filter((p) => !['ACEPTADO', 'RECHAZADO'].includes(p.estado)).length;

    UI.clear(slot);
    [
      { label: 'Postulantes totales', value: all.length },
      { label: 'En proceso',          value: enProceso },
      { label: 'Aceptados',           value: aceptados },
      { label: 'Usuarios registrados', value: usuarios.length },
      { label: 'Administradores', value: usuarios.filter((u) => u.rol === 'admin').length },
      { label: 'Ofertas abiertas',    value: Ofertas.list().length }
    ].forEach((kpi, i) => {
      slot.appendChild(UI.el('div', { class: `kpi anim-fade-up delay-${i + 1}` }, [
        UI.el('div', { class: 'kpi__label', text: kpi.label }),
        UI.el('div', { class: 'kpi__value', text: kpi.value })
      ]));
    });
  };

  const renderUsuarios = () => {
    const tbody = document.getElementById('admin-usuarios-resumen');
    if (!tbody) return;
    UI.clear(tbody);
    const usuarios = [...Usuarios.list()].sort((a, b) =>
      (UI.dateValue(b.creadoEn)?.getTime() || 0) - (UI.dateValue(a.creadoEn)?.getTime() || 0));
    if (usuarios.length === 0) {
      tbody.appendChild(UI.el('tr', {}, [
        UI.el('td', { colspan: 4, class: 'soft', style: 'text-align:center;padding:32px', text: 'No hay usuarios registrados.' })
      ]));
      return;
    }
    usuarios.forEach((u) => {
      tbody.appendChild(UI.el('tr', {}, [
        UI.el('td', {}, [
          UI.el('div', { class: 'flex gap-2', style: 'align-items: center;' }, [
            UI.el('div', { class: 'avatar', text: `${u.nombre?.[0] || ''}${u.apellido?.[0] || ''}` }),
            UI.el('div', {}, [
              UI.el('div', { text: `${u.nombre || ''} ${u.apellido || ''}`.trim(), style: 'font-weight: 500;' }),
              UI.el('div', { class: 'soft', style: 'font-size: 0.8rem;', text: u.email })
            ])
          ])
        ]),
        UI.el('td', {}, [UI.el('span', { class: `badge ${u.rol === 'admin' ? 'badge--entrevista' : 'badge--evaluacion'}`, text: u.rol || '-' })]),
        UI.el('td', {}, [UI.el('span', { class: `badge ${u.activo ? 'badge--aceptado' : 'badge--rechazado'}`, text: u.activo ? 'Activo' : 'Inactivo' })]),
        UI.el('td', { text: UI.formatDate(u.creadoEn) })
      ]));
    });
  };

  const renderEstadosBreakdown = () => {
    const wrap = document.getElementById('admin-estados');
    if (!wrap) return;
    const all = Postulantes.list();
    UI.clear(wrap);
    Object.keys(UI.ESTADOS).forEach((est) => {
      const count = all.filter((p) => p.estado === est).length;
      wrap.appendChild(UI.el('div', { class: 'card' }, [
        UI.renderEstadoBadge(est),
        UI.el('div', { class: 'kpi__value', style: 'font-size: 1.6rem; margin-top: 8px;', text: count })
      ]));
    });
  };

  const renderRecientes = () => {
    const tbody = document.getElementById('admin-recientes');
    if (!tbody) return;
    UI.clear(tbody);
    const recientes = [...Postulantes.list()].sort((a, b) => (UI.dateValue(b.creadoEn)?.getTime() || 0) - (UI.dateValue(a.creadoEn)?.getTime() || 0));
    recientes.forEach((p) => {
      const oferta = Ofertas.get(p.ofertaId);
      const tr = UI.el('tr', {}, [
        UI.el('td', {}, [
          UI.el('div', { class: 'flex gap-2', style: 'align-items: center;' }, [
            UI.el('div', { class: 'avatar', text: p.nombre.split(' ').map((n) => n[0]).slice(0, 2).join('') }),
            UI.el('div', {}, [
              UI.el('div', { text: p.nombre, style: 'font-weight: 500;' }),
              UI.el('div', { class: 'soft', style: 'font-size: 0.8rem;', text: p.email })
            ])
          ])
        ]),
        UI.el('td', { text: oferta?.titulo || '—' }),
        UI.el('td', {}, [UI.renderEstadoBadge(p.estado)]),
        UI.el('td', { text: `${p.puntajeFinal ?? p.puntaje ?? 0} pts` }),
        UI.el('td', { text: UI.formatDate(p.creadoEn) })
      ]);
      tbody.appendChild(tr);
    });
  };

  document.addEventListener('DOMContentLoaded', init);
})();
