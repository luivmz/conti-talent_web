/* =========================================================
   auth.js — Sesion, login, registro y control de roles
   ========================================================= */

const Auth = (() => {
  const SESSION_KEY = 'session';

  const getSession = () => Storage.read(SESSION_KEY, null);
  const setSession = (session) => Storage.write(SESSION_KEY, Storage.normalizeSession(session));
  const clear = () => Storage.clear(SESSION_KEY);

  const isAuthenticated = () => !!getSession();
  const isAdmin = () => getSession()?.rol === 'admin';

  const login = async (email, password) => {
    try {
      const response = await ContiAPI.login(email, password);
      const user = Storage.normalizeSession(response.data || response);
      setSession(user);
      await Storage.refresh('usuarios').catch(() => {});
      return { ok: true, user };
    } catch (err) {
      return { ok: false, error: err.message || 'Credenciales invalidas o cuenta desactivada' };
    }
  };

  const register = async ({ nombre, apellido, email, password }) => {
    try {
      const response = await ContiAPI.registro({ nombre, apellido, email, password });
      const user = Storage.normalizeSession(response.data || response);
      setSession(user);
      await Storage.refresh('usuarios').catch(() => {});
      return { ok: true, user };
    } catch (err) {
      return { ok: false, error: err.message || 'No se pudo crear la cuenta' };
    }
  };

  const logout = async () => {
    clear();
    try { await ContiAPI.logout(); } catch (_err) {}
  };

  const requireAuth = (redirect = 'login.html') => {
    if (!isAuthenticated()) {
      window.location.href = redirect;
      return false;
    }
    return true;
  };

  const requireAdmin = (redirect = 'login.html') => {
    if (!isAdmin()) {
      window.location.href = redirect;
      return false;
    }
    return true;
  };

  return { getSession, isAuthenticated, isAdmin, login, register, logout, requireAuth, requireAdmin };
})();

window.Auth = Auth;
