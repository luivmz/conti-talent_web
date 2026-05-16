/* =========================================================
   page-contacto.js — Formulario de contacto
   ========================================================= */

(() => {
  const init = async () => {
    await Storage.ready;
    const form = document.getElementById('contacto-form');
    if (!form) return;

    form.addEventListener('submit', (e) => {
      e.preventDefault();
      const result = Validators.validateForm(form, {
        nombre:   [{ test: Validators.required, message: 'Tu nombre es requerido' }],
        email:    [{ test: Validators.required, message: 'Tu correo es requerido' },
                   { test: Validators.isEmail,  message: 'Correo no válido' }],
        telefono: [{ test: (v) => !v || Validators.isPhone(v), message: 'Teléfono no válido' }],
        asunto:   [{ test: Validators.required, message: 'Indica un asunto' }],
        mensaje:  [{ test: Validators.required, message: 'Escribe tu mensaje' },
                   { test: (v) => Validators.minLength(v, 10), message: 'Mínimo 10 caracteres' }]
      });
      if (!result.valid) return;

      const mensajes = Storage.read('mensajes_contacto', []);
      mensajes.push({ id: Storage.generateId(), ...result.values, recibidoEn: new Date().toISOString() });
      Storage.write('mensajes_contacto', mensajes);

      UI.showToast('Tu mensaje fue enviado. Te contactaremos pronto.', 'success');
      form.reset();
    });
  };

  document.addEventListener('DOMContentLoaded', init);
})();
