/* =========================================================
   areas.js — CRUD de areas contra API REST
   ========================================================= */

const Areas = (() => {
  const ENTITY = 'areas';

  const list = () => Storage.read(ENTITY, []);
  const get = (id) => list().find((a) => String(a.id) === String(id)) || null;

  const payload = (data) => ({
    nombre: data.nombre?.trim(),
    descripcion: (data.descripcion || '').trim(),
    icono: data.icono || 'building',
    color: data.color || '#6366f1'
  });

  const create = (data) => {
    const temp = { id: Storage.generateId(), ...payload(data) };
    Storage.upsert(ENTITY, temp);
    ContiAPI.crearArea(payload(data))
      .then((created) => Storage.upsert(ENTITY, created))
      .catch((err) => { Storage.removeCached(ENTITY, temp.id); UI.showToast(err.message, 'error'); });
    return temp;
  };

  const update = (id, data) => {
    const current = get(id);
    const updated = Storage.upsert(ENTITY, { ...current, ...payload(data), id });
    ContiAPI.actualizarArea(id, updated)
      .then((saved) => Storage.upsert(ENTITY, saved))
      .catch((err) => { if (current) Storage.upsert(ENTITY, current); UI.showToast(err.message, 'error'); });
    return updated;
  };

  const remove = (id) => {
    const current = get(id);
    Storage.removeCached(ENTITY, id);
    ContiAPI.eliminarArea(id)
      .catch((err) => { if (current) Storage.upsert(ENTITY, current); UI.showToast(err.message, 'error'); });
  };

  const countOfertasByArea = (areaId) =>
    Storage.read('ofertas', []).filter((o) => String(o.areaId) === String(areaId)).length;

  return { list, get, create, update, remove, countOfertasByArea };
})();

window.Areas = Areas;
