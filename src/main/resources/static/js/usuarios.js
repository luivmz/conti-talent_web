/* =========================================================
   usuarios.js — CRUD de usuarios contra API REST
   ========================================================= */

const Usuarios = (() => {
  const ENTITY = 'usuarios';

  const list = () => Storage.read(ENTITY, []);
  const get = (id) => list().find((u) => String(u.id) === String(id)) || null;

  const toRequest = (data) => ({
    nombre: data.nombre?.trim(),
    apellido: data.apellido?.trim(),
    email: data.email?.trim(),
    password: data.password || undefined,
    rolCodigo: (data.rol || 'postulante').toUpperCase(),
    activo: data.activo !== false
  });

  const create = (data) => {
    const temp = {
      id: Storage.generateId(),
      nombre: data.nombre.trim(),
      apellido: data.apellido.trim(),
      email: data.email.trim(),
      rol: data.rol || 'postulante',
      activo: data.activo !== false,
      creadoEn: new Date().toISOString()
    };
    Storage.upsert(ENTITY, temp);
    ContiAPI.crearUsuario(toRequest(data))
      .then((created) => Storage.upsert(ENTITY, created))
      .catch((err) => { Storage.removeCached(ENTITY, temp.id); UI.showToast(err.message, 'error'); });
    return temp;
  };

  const update = (id, data) => {
    const current = get(id);
    const updated = Storage.upsert(ENTITY, { ...current, ...data, id });
    ContiAPI.actualizarUsuario(id, toRequest(updated))
      .then((saved) => Storage.upsert(ENTITY, saved))
      .catch((err) => { if (current) Storage.upsert(ENTITY, current); UI.showToast(err.message, 'error'); });
    return updated;
  };

  const remove = (id) => {
    const current = get(id);
    Storage.removeCached(ENTITY, id);
    ContiAPI.eliminarUsuario(id)
      .catch((err) => { if (current) Storage.upsert(ENTITY, current); UI.showToast(err.message, 'error'); });
  };

  return { list, get, create, update, remove };
})();

window.Usuarios = Usuarios;
