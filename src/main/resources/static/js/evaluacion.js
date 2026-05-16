/* =========================================================
   evaluacion.js — Preguntas y calificacion contra API REST
   ========================================================= */

const Evaluacion = (() => {
  const ENTITY = 'preguntas';

  const list = () => Storage.read(ENTITY, []);
  const byOferta = (ofertaId) => list().filter((q) => String(q.ofertaId) === String(ofertaId));
  const get = (id) => list().find((q) => String(q.id) === String(id)) || null;

  const payload = (data) => ({
    ofertaId: Storage.toNumber(data.ofertaId),
    pregunta: data.pregunta?.trim(),
    opciones: data.opciones || [],
    correcta: parseInt(data.correcta, 10)
  });

  const create = (data) => {
    const temp = { id: Storage.generateId(), ...data, ofertaId: String(data.ofertaId) };
    Storage.upsert(ENTITY, temp);
    ContiAPI.crearPregunta(payload(data))
      .then((created) => Storage.upsert(ENTITY, created))
      .catch((err) => { Storage.removeCached(ENTITY, temp.id); UI.showToast(err.message, 'error'); });
    return temp;
  };

  const update = (id, data) => {
    const current = get(id);
    const updated = Storage.upsert(ENTITY, { ...current, ...data, id, ofertaId: String(data.ofertaId ?? current?.ofertaId) });
    ContiAPI.actualizarPregunta(id, payload(updated))
      .then((saved) => Storage.upsert(ENTITY, saved))
      .catch((err) => { if (current) Storage.upsert(ENTITY, current); UI.showToast(err.message, 'error'); });
    return updated;
  };

  const remove = (id) => {
    const current = get(id);
    Storage.removeCached(ENTITY, id);
    ContiAPI.eliminarPregunta(id)
      .catch((err) => { if (current) Storage.upsert(ENTITY, current); UI.showToast(err.message, 'error'); });
  };

  const calificar = (ofertaId, respuestas) => {
    const preguntas = byOferta(ofertaId);
    if (preguntas.length === 0) return { puntaje: 0, correctas: 0, total: 0 };
    const correctas = preguntas.reduce((acc, q) => acc + (Number(respuestas[q.id]) === Number(q.correcta) ? 1 : 0), 0);
    const puntaje = Math.round((correctas / preguntas.length) * 100);
    return { puntaje, correctas, total: preguntas.length };
  };

  return { list, byOferta, get, create, update, remove, calificar };
})();

window.Evaluacion = Evaluacion;
