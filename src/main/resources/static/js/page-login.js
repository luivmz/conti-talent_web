/* =========================================================
   page-login.js — Controlador de la página de login
   ========================================================= */

(() => {
  const init = async () => {
    await Storage.ready;
    const form = document.getElementById('login-form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const result = Validators.validateForm(form, {
        email:    [{ test: Validators.required, message: 'Ingresa tu correo' },
                   { test: Validators.isEmail,  message: 'Correo no válido' }],
        password: [{ test: Validators.required, message: 'Ingresa tu contraseña' }]
      });
      if (!result.valid) return;

      const out = await Auth.login(result.values.email, result.values.password);
      if (!out.ok) {
        UI.showToast(out.error, 'error');
        return;
      }

      UI.showToast(`Bienvenido, ${out.user.nombre} 👋`, 'success');
      setTimeout(() => {
        window.location.href = out.user.rol === 'admin' ? 'admin/dashboard.html' : 'index.html';
      }, 600);
    });
  };

  document.addEventListener('DOMContentLoaded', init);
})();
