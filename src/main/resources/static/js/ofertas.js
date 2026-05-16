/* =========================================================
   ofertas.js — CRUD de ofertas contra API REST
   ========================================================= */

const Ofertas = (() => {
  const ENTITY = 'ofertas';
  const TIPOS = ['Practica', 'Trabajo'];

  const list = () => Storage.read(ENTITY, []);
  const get = (id) => list().find((o) => String(o.id) === String(id)) || null;
  const filterByArea = (areaId) => list().filter((o) => String(o.areaId) === String(areaId));
  const featured = () => list().filter((o) => !!o.destacada);

  const payload = (data) => ({
    titulo: data.titulo?.trim(),
    tipo: data.tipo || 'Trabajo',
    areaId: Storage.toNumber(data.areaId),
    modalidad: data.modalidad || 'Presencial',
    ubicacion: data.ubicacion || '',
    horario: data.horario || '',
    vacantes: parseInt(data.vacantes, 10) || 1,
    destacada: !!data.destacada,
    descripcion: (data.descripcion || '').trim(),
    requisitos: data.requisitos || [],
    beneficios: data.beneficios || [],
    creadaEn: data.creadaEn || new Date().toISOString()
  });

  const create = (data) => {
    const temp = { id: Storage.generateId(), ...payload(data), areaId: String(data.areaId), creadaEn: new Date().toISOString() };
    Storage.upsert(ENTITY, temp);
    ContiAPI.crearOferta(payload(data))
      .then((created) => Storage.upsert(ENTITY, created))
      .catch((err) => { Storage.removeCached(ENTITY, temp.id); UI.showToast(err.message, 'error'); });
    return temp;
  };

  const update = (id, data) => {
    const current = get(id);
    const updated = Storage.upsert(ENTITY, { ...current, ...data, id, areaId: String(data.areaId ?? current?.areaId) });
    ContiAPI.actualizarOferta(id, payload(updated))
      .then((saved) => Storage.upsert(ENTITY, saved))
      .catch((err) => { if (current) Storage.upsert(ENTITY, current); UI.showToast(err.message, 'error'); });
    return updated;
  };

  const remove = (id) => {
    const current = get(id);
    Storage.removeCached(ENTITY, id);
    Storage.write('preguntas', Storage.read('preguntas', []).filter((q) => String(q.ofertaId) !== String(id)));
    ContiAPI.eliminarOferta(id)
      .catch((err) => { if (current) Storage.upsert(ENTITY, current); UI.showToast(err.message, 'error'); });
  };

  return { list, get, filterByArea, featured, create, update, remove, TIPOS };
})();

window.Ofertas = Ofertas;
