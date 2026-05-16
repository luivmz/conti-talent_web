/* =========================================================
   page-registro.js — Controlador de la página de registro
   ========================================================= */

(() => {
  const init = async () => {
    await Storage.ready;
    const form = document.getElementById('registro-form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const result = Validators.validateForm(form, {
        nombre:          [{ test: Validators.required,           message: 'Ingresa tu nombre' }],
        apellido:        [{ test: Validators.required,           message: 'Ingresa tu apellido' }],
        email:           [{ test: Validators.required,           message: 'Ingresa tu correo' },
                          { test: Validators.isEmail,            message: 'Correo no válido' }],
        password:        [{ test: Validators.required,           message: 'Crea una contraseña' },
                          { test: (v) => Validators.minLength(v, 6), message: 'Mínimo 6 caracteres' }],
        passwordConfirm: [{ test: Validators.required,           message: 'Confirma tu contraseña' },
                          { test: (v, all) => v === all.password, message: 'Las contraseñas no coinciden' }],
        terms:           [{ test: (v) => v === true,             message: 'Debes aceptar los términos' }]
      });
      if (!result.valid) return;

      const out = await Auth.register(result.values);
      if (!out.ok) {
        UI.showToast(out.error, 'error');
        return;
      }

      UI.showToast('Cuenta creada con éxito', 'success');
      setTimeout(() => { window.location.href = 'index.html'; }, 700);
    });
  };

  document.addEventListener('DOMContentLoaded', init);
})();
