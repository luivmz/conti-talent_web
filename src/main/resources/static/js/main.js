/* =========================================================
   main.js — Entry point
   - Ejecuta la semilla
   - Renderiza la zona de sesión del header
   - Marca la nav activa
   - Inyecta el menú móvil (hamburguesa + drawer)
   - Maneja sidebar admin colapsable en móvil
   Se ejecuta en TODAS las páginas.
   ========================================================= */

(() => {
  const ICONS = {
    burger: '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M4 6h16M4 12h16M4 18h16"/></svg>',
    close:  '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M6 6l12 12M18 6l-12 12"/></svg>'
  };

  const init = async () => {
    await Storage.ready;
    renderHeaderSession();
    highlightActiveNav();
    bindLogoutLinks();
    setupMobileNav();
  };

  const renderHeaderSession = () => {
    const slot = document.getElementById('session-area');
    if (!slot) return;

    const session = Auth.getSession();
    UI.clear(slot);

    if (!session) {
      slot.appendChild(UI.el('a', { href: 'login.html',    class: 'btn btn--ghost btn--sm', text: 'Iniciar sesión' }));
      slot.appendChild(UI.el('a', { href: 'registro.html', class: 'btn btn--primary btn--sm', text: 'Crear cuenta' }));
      return;
    }

    const initials = (session.nombre?.[0] || '') + (session.apellido?.[0] || '');
    const avatar = UI.el('div',  { class: 'avatar', text: initials.toUpperCase() });
    const name   = UI.el('span', { class: 'muted', text: `${session.nombre} ${session.apellido}` });

    if (session.rol === 'admin') {
      slot.appendChild(UI.el('a', { href: 'admin/dashboard.html', class: 'btn btn--ghost btn--sm', text: 'Panel admin' }));
    } else {
      slot.appendChild(UI.el('a', { href: 'mis-postulaciones.html', class: 'btn btn--ghost btn--sm', text: 'Mis postulaciones' }));
    }
    slot.appendChild(avatar);
    slot.appendChild(name);
    slot.appendChild(UI.el('button', {
      class: 'icon-btn',
      title: 'Cerrar sesión',
      'aria-label': 'Cerrar sesión',
      html: '⎋',
      onClick: async () => { await Auth.logout(); window.location.href = resolveHomeUrl(); }
    }));
  };

  const highlightActiveNav = () => {
    const path = window.location.pathname.split('/').pop() || 'index.html';
    UI.$$('[data-nav]').forEach((node) => {
      if (node.dataset.nav === path) node.classList.add('is-active');
    });
  };

  const bindLogoutLinks = () => {
    UI.$$('[data-action="logout"]').forEach((node) => {
      node.addEventListener('click', async (e) => {
        e.preventDefault();
        await Auth.logout();
        window.location.href = resolveHomeUrl();
      });
    });
  };

  /* ------------------------------------------------------------------
     Mobile navigation: hamburger button + drawer
     - En páginas admin: el drawer ofrece los enlaces del sidebar
     - En páginas públicas: el drawer ofrece los enlaces del nav
  ------------------------------------------------------------------ */
  const setupMobileNav = () => {
    const headerInner = document.querySelector('.site-header__inner');
    if (!headerInner) return;

    // Botón hamburguesa
    const toggleBtn = UI.el('button', {
      class: 'mobile-toggle',
      'aria-label': 'Abrir menú',
      'aria-expanded': 'false',
      html: ICONS.burger
    });
    headerInner.appendChild(toggleBtn);

    // Drawer
    const closeBtn  = UI.el('button', { class: 'mobile-drawer__close', 'aria-label': 'Cerrar menú', html: ICONS.close });
    const brandRef  = headerInner.querySelector('.brand')?.cloneNode(true);
    const drawerHead = UI.el('header', { class: 'mobile-drawer__head' }, [brandRef, closeBtn].filter(Boolean));
    const navWrap    = UI.el('nav', { 'aria-label': 'Menú móvil' });
    const sessWrap   = UI.el('div', { class: 'mobile-drawer__session' });
    const drawer     = UI.el('aside', { class: 'mobile-drawer', 'aria-hidden': 'true' }, [drawerHead, navWrap, sessWrap]);

    fillDrawerNav(navWrap);
    fillDrawerSession(sessWrap);

    document.body.appendChild(drawer);

    const open  = () => {
      drawer.classList.add('is-open');
      toggleBtn.setAttribute('aria-expanded', 'true');
      drawer.setAttribute('aria-hidden', 'false');
      document.body.style.overflow = 'hidden';
    };
    const close = () => {
      drawer.classList.remove('is-open');
      toggleBtn.setAttribute('aria-expanded', 'false');
      drawer.setAttribute('aria-hidden', 'true');
      document.body.style.overflow = '';
    };

    toggleBtn.addEventListener('click', open);
    closeBtn.addEventListener('click',  close);

    // Cierra al hacer click en cualquier link
    drawer.addEventListener('click', (e) => {
      const link = e.target.closest('a, button[data-mobile-action]');
      if (link && !link.classList.contains('mobile-drawer__close')) close();
    });

    // Cierra con Escape
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && drawer.classList.contains('is-open')) close();
    });

    // Si la ventana crece de móvil a desktop, cerrar el drawer y resetear scroll lock
    const mediaQuery = window.matchMedia('(min-width: 769px)');
    const onChange = (mq) => { if (mq.matches) close(); };
    if (mediaQuery.addEventListener) mediaQuery.addEventListener('change', onChange);
    else mediaQuery.addListener(onChange);

    // Sidebar admin colapsable: si hay sidebar, agregamos un FAB para abrir/cerrar
    setupAdminSidebarToggle();
  };

  const fillDrawerNav = (navWrap) => {
    UI.clear(navWrap);
    const adminSidebar = document.querySelector('.app-sidebar');

    if (adminSidebar) {
      // Clona estructura del sidebar admin (secciones + links)
      adminSidebar.querySelectorAll('.sidebar-section').forEach((section) => {
        const label = section.querySelector('.sidebar-section__label')?.textContent;
        if (label) navWrap.appendChild(UI.el('div', { class: 'mobile-drawer__group-label', text: label }));
        section.querySelectorAll('.sidebar-link').forEach((link) => {
          navWrap.appendChild(link.cloneNode(true));
        });
      });
      // Volver al sitio público
      navWrap.appendChild(UI.el('div', { class: 'mobile-drawer__group-label', text: 'Sitio' }));
      navWrap.appendChild(UI.el('a', { href: '../index.html', text: '↗ Ir al sitio público' }));
      return;
    }

    // Nav pública
    document.querySelectorAll('.primary-nav > a').forEach((a) => {
      const clone = a.cloneNode(true);
      clone.classList.remove('btn', 'btn--ghost', 'btn--primary', 'btn--sm');
      navWrap.appendChild(clone);
    });
  };

  const fillDrawerSession = (wrap) => {
    UI.clear(wrap);
    const session = Auth.getSession();
    const isAdmin = !!document.querySelector('.app-sidebar');

    if (!session) {
      wrap.appendChild(UI.el('a', { href: prefixed('login.html'),    class: 'btn btn--ghost',   text: 'Iniciar sesión' }));
      wrap.appendChild(UI.el('a', { href: prefixed('registro.html'), class: 'btn btn--primary', text: 'Crear cuenta' }));
      return;
    }

    const initials = ((session.nombre?.[0] || '') + (session.apellido?.[0] || '')).toUpperCase();
    wrap.appendChild(UI.el('div', { class: 'flex gap-3', style: 'align-items:center;padding:12px 0;' }, [
      UI.el('div', { class: 'avatar', text: initials }),
      UI.el('div', {}, [
        UI.el('div', { style: 'font-weight:500', text: `${session.nombre} ${session.apellido}` }),
        UI.el('div', { class: 'soft', style: 'font-size:0.8rem', text: session.email })
      ])
    ]));

    if (session.rol === 'admin' && !isAdmin) {
      wrap.appendChild(UI.el('a', { href: 'admin/dashboard.html', class: 'btn btn--ghost', text: 'Panel admin' }));
    } else if (session.rol !== 'admin') {
      wrap.appendChild(UI.el('a', { href: prefixed('mis-postulaciones.html'), class: 'btn btn--ghost', text: 'Mis postulaciones' }));
    }

    const logoutBtn = UI.el('button', { class: 'btn btn--danger', 'data-mobile-action': 'logout', text: 'Cerrar sesión' });
    logoutBtn.addEventListener('click', async () => { await Auth.logout(); window.location.href = resolveHomeUrl(); });
    wrap.appendChild(logoutBtn);
  };

  const setupAdminSidebarToggle = () => {
    const sidebar = document.querySelector('.app-sidebar');
    const main    = document.querySelector('.app-main');
    if (!sidebar || !main) return;

    // Botón flotante para tablet (que abre el sidebar real, no el drawer)
    const fab = UI.el('button', {
      class: 'btn btn--ghost btn--sm sidebar-fab',
      style: 'display:none;margin-bottom:16px;',
      html: '☰ Menú admin'
    });
    fab.addEventListener('click', () => sidebar.classList.toggle('is-open'));

    // Cerrar al click fuera
    document.addEventListener('click', (e) => {
      if (!sidebar.classList.contains('is-open')) return;
      if (!sidebar.contains(e.target) && e.target !== fab) sidebar.classList.remove('is-open');
    });
  };

  /* Ruta home según contexto admin / público */
  const resolveHomeUrl = () => {
    const isAdminPath = window.location.pathname.includes('/admin/');
    return isAdminPath ? '../index.html' : 'index.html';
  };

  const prefixed = (path) => {
    const isAdminPath = window.location.pathname.includes('/admin/');
    return isAdminPath ? `../${path}` : path;
  };

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
