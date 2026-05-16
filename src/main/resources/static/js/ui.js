/* =========================================================
   ui.js — Helpers de DOM, toasts, modales, formato
   ========================================================= */

const UI = (() => {

  /* ----- DOM helpers ----- */
  const $  = (sel, root = document) => root.querySelector(sel);
  const $$ = (sel, root = document) => Array.from(root.querySelectorAll(sel));

  const el = (tag, attrs = {}, children = []) => {
    const node = document.createElement(tag);
    Object.entries(attrs).forEach(([k, v]) => {
      if (k === 'class')      node.className = v;
      else if (k === 'html')  node.innerHTML = v;
      else if (k === 'text')  node.textContent = v;
      else if (k.startsWith('on') && typeof v === 'function') node.addEventListener(k.slice(2).toLowerCase(), v);
      else if (v !== null && v !== undefined && v !== false)  node.setAttribute(k, v);
    });
    (Array.isArray(children) ? children : [children])
      .filter(Boolean)
      .forEach((c) => node.appendChild(typeof c === 'string' ? document.createTextNode(c) : c));
    return node;
  };

  const clear = (node) => { while (node && node.firstChild) node.removeChild(node.firstChild); };

  /* ----- Toast ----- */
  const ensureToastStack = () => {
    let stack = document.getElementById('toast-stack');
    if (!stack) {
      stack = el('div', { id: 'toast-stack', class: 'toast-stack' });
      document.body.appendChild(stack);
    }
    return stack;
  };

  const showToast = (message, type = 'info', duration = 3200) => {
    const stack = ensureToastStack();
    const icons = { success: '✓', error: '!', info: 'i' };
    const toast = el('div', { class: `toast toast--${type}` }, [
      el('div', { class: 'toast__icon', text: icons[type] || 'i' }),
      el('div', { class: 'toast__msg', text: message })
    ]);
    stack.appendChild(toast);
    setTimeout(() => {
      toast.style.transition = 'opacity .3s, transform .3s';
      toast.style.opacity = '0';
      toast.style.transform = 'translateX(20px)';
      setTimeout(() => toast.remove(), 320);
    }, duration);
  };

  /* ----- Modal ----- */
  const openModal = ({ title, content, onClose, footer }) => {
    const close = () => {
      backdrop.classList.remove('is-open');
      setTimeout(() => backdrop.remove(), 280);
      if (typeof onClose === 'function') onClose();
    };

    const closeBtn = el('button', { class: 'icon-btn', 'aria-label': 'Cerrar', onClick: close, html: '&times;' });
    const head     = el('header', { class: 'modal__head' }, [el('h3', { text: title || '' }), closeBtn]);
    const body     = el('div', { class: 'modal__body' });
    if (typeof content === 'string') body.innerHTML = content;
    else if (content) body.appendChild(content);

    const modal    = el('div', { class: 'modal', role: 'dialog', 'aria-modal': 'true' }, [head, body]);
    if (footer) modal.appendChild(footer);

    const backdrop = el('div', { class: 'modal-backdrop', onClick: (e) => { if (e.target === backdrop) close(); } }, [modal]);

    document.body.appendChild(backdrop);
    requestAnimationFrame(() => backdrop.classList.add('is-open'));

    return { close, modal, body };
  };

  /* ----- Formatters ----- */
  const dateValue = (value) => {
    if (!value) return null;
    const d = new Date(value);
    return Number.isNaN(d.getTime()) ? null : d;
  };

  const formatDate = (value) => {
    const d = dateValue(value);
    if (!d) return '—';
    return d.toLocaleDateString('es-PE', { day: '2-digit', month: 'short', year: 'numeric' });
  };

  const formatDateTime = (value) => {
    const d = dateValue(value);
    if (!d) return '—';
    return d.toLocaleString('es-PE', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
  };

  /* ----- Estado postulante ----- */
  const ESTADOS = {
    POSTULADO:               { label: 'Postulado',                   modifier: 'postulado' },
    EN_EVALUACION:           { label: 'En evaluación',               modifier: 'evaluacion' },
    APROBADO_TECNICO:        { label: 'Aprobado evaluación técnica', modifier: 'aprobado-tecnico' },
    ENTREVISTA:              { label: 'Entrevista',                  modifier: 'entrevista' },
    EVALUACION_PSICOLOGICA:  { label: 'Evaluación psicológica',      modifier: 'psicologica' },
    ACEPTADO:                { label: 'Aceptado',                    modifier: 'aceptado' },
    RECHAZADO:               { label: 'Rechazado',                   modifier: 'rechazado' }
  };

  const renderEstadoBadge = (estado) => {
    const meta = ESTADOS[estado] || { label: estado, modifier: 'postulado' };
    const span = el('span', { class: `badge badge--${meta.modifier}`, text: meta.label });
    return span;
  };

  /* ----- Confirm ----- */
  const confirm = ({ title = 'Confirmar acción', message = '¿Estás seguro?', okLabel = 'Confirmar', okClass = 'btn--danger' } = {}) => {
    return new Promise((resolve) => {
      const okBtn     = el('button', { class: `btn ${okClass}`, text: okLabel });
      const cancelBtn = el('button', { class: 'btn btn--ghost', text: 'Cancelar' });
      const footer    = el('footer', { class: 'flex gap-2', style: 'justify-content: flex-end; margin-top: 16px;' }, [cancelBtn, okBtn]);
      const content   = el('p', { class: 'muted', text: message });
      const m = openModal({ title, content, footer });
      okBtn.addEventListener('click',     () => { m.close(); resolve(true);  });
      cancelBtn.addEventListener('click', () => { m.close(); resolve(false); });
    });
  };

  return { $, $$, el, clear, showToast, openModal, confirm, dateValue, formatDate, formatDateTime, ESTADOS, renderEstadoBadge };
})();

window.UI = UI;
