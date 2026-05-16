/* =========================================================
   validators.js — Validaciones reutilizables de formularios
   ========================================================= */

const Validators = (() => {

  const required   = (value)         => value !== null && value !== undefined && String(value).trim().length > 0;
  const minLength  = (value, n)      => required(value) && String(value).trim().length >= n;
  const maxLength  = (value, n)      => !value || String(value).trim().length <= n;
  const isEmail    = (value)         => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(value || '').trim());
  const isPhone    = (value)         => /^[+\d][\d\s().-]{6,}$/.test(String(value || '').trim());
  const isNumber   = (value)         => !isNaN(parseFloat(value)) && isFinite(value);
  const sameAs     = (a, b)          => a === b;

  /**
   * Aplica reglas a un formulario y muestra errores debajo de cada campo.
   * @param {HTMLFormElement} form
   * @param {Record<string, Array<{ test: (v: string, all: object) => boolean, message: string }>>} rules
   * @returns {{ valid: boolean, values: object, errors: object }}
   */
  const validateForm = (form, rules) => {
    const values = {};
    Array.from(form.elements).forEach((field) => {
      if (field.name) values[field.name] = field.type === 'checkbox' ? field.checked : field.value;
    });

    const errors = {};
    Object.entries(rules).forEach(([name, fieldRules]) => {
      for (const rule of fieldRules) {
        if (!rule.test(values[name], values)) {
          errors[name] = rule.message;
          break;
        }
      }
    });

    Array.from(form.elements).forEach((field) => {
      if (!field.name) return;
      const wrapper  = field.closest('.field');
      const errorEl  = wrapper ? wrapper.querySelector('.error') : null;
      if (errors[field.name]) {
        field.classList.add('is-invalid');
        if (errorEl) errorEl.textContent = errors[field.name];
      } else {
        field.classList.remove('is-invalid');
        if (errorEl) errorEl.textContent = '';
      }
    });

    return { valid: Object.keys(errors).length === 0, values, errors };
  };

  return { required, minLength, maxLength, isEmail, isPhone, isNumber, sameAs, validateForm };
})();

window.Validators = Validators;
