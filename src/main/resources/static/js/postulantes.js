/* =========================================================
   postulantes.js — Gestion de postulantes contra API REST
   ========================================================= */

const Postulantes = (() => {
  const ENTITY = 'postulantes';

  const list = () => Storage.read(ENTITY, []);
  const get = (id) => list().find((p) => String(p.id) === String(id)) || null;
  const byOferta = (ofertaId) => list().filter((p) => String(p.ofertaId) === String(ofertaId));
  const byUsuario = (usuarioId) => list().filter((p) => String(p.usuarioId) === String(usuarioId));

  const payload = (data) => ({
    usuarioId: Storage.toNumber(data.usuarioId),
    ofertaId: Storage.toNumber(data.ofertaId),
    nombre: data.nombre?.trim(),
    email: data.email?.trim(),
    telefono: data.telefono || '',
    experiencia: data.experiencia || '',
    habilidades: data.habilidades || '',
    cv: data.cv || '',
    aniosExperiencia: Storage.toNumber(data.aniosExperiencia) || 0,
    nivelEstudios: data.nivelEstudios || '',
    carrera: data.carrera || '',
    disponibilidad: data.disponibilidad || '',
    modalidadPreferida: data.modalidadPreferida || '',
    pretensionSalarial: Storage.toNumber(data.pretensionSalarial),
    linkedin: data.linkedin || '',
    portafolio: data.portafolio || '',
    puntaje: Storage.toNumber(data.puntaje),
    observacionAdmin: data.observacionAdmin || ''
  });

  const create = (data) => {
    const temp = {
      id: Storage.generateId(),
      ...data,
      usuarioId: data.usuarioId ? String(data.usuarioId) : null,
      ofertaId: String(data.ofertaId),
      estado: 'POSTULADO',
      puntaje: 0,
      respuestas: {},
      creadoEn: new Date().toISOString()
    };
    Storage.upsert(ENTITY, temp);
    ContiAPI.postular(payload(data))
      .then((created) => Storage.upsert(ENTITY, created))
      .catch((err) => { Storage.removeCached(ENTITY, temp.id); UI.showToast(err.message, 'error'); });
    return temp;
  };

  const update = (id, data) => {
    const current = get(id);
    const updated = Storage.upsert(ENTITY, { ...current, ...data, id });
    ContiAPI.actualizarPostulante(id, payload(updated))
      .then((saved) => Storage.upsert(ENTITY, saved))
      .catch((err) => { if (current) Storage.upsert(ENTITY, current); UI.showToast(err.message, 'error'); });
    return updated;
  };

  const updateLocal = (id, data) => {
    const current = get(id);
    return Storage.upsert(ENTITY, { ...current, ...data, id });
  };

  const setEstado = (id, estado, extra = {}) => {
    const current = get(id);
    const updated = updateLocal(id, { estado });
    ContiAPI.cambiarEstado(id, estado, extra)
      .then((saved) => Storage.upsert(ENTITY, saved))
      .catch((err) => { if (current) Storage.upsert(ENTITY, current); UI.showToast(err.message, 'error'); });
    return updated;
  };

  const setObservacionAdmin = (id, observacionAdmin) => {
    const current = get(id);
    const updated = updateLocal(id, { observacionAdmin });
    ContiAPI.actualizarObservacionPostulante(id, observacionAdmin)
      .then((saved) => Storage.upsert(ENTITY, saved))
      .catch((err) => { if (current) Storage.upsert(ENTITY, current); UI.showToast(err.message, 'error'); });
    return updated;
  };

  const setPuntaje = (id, puntaje, autoEstado = true) => {
    const updates = { puntaje };
    if (autoEstado) updates.estado = puntaje >= 70 ? 'APROBADO_TECNICO' : 'EN_EVALUACION';
    return update(id, updates);
  };

  const saveEvaluation = (id, puntaje, respuestas) => {
    const current = get(id);
    const updated = updateLocal(id, {
      puntaje,
      respuestas,
      estado: puntaje >= 70 ? 'APROBADO_TECNICO' : 'EN_EVALUACION'
    });
    ContiAPI.calificar(id, respuestas)
      .then(() => Storage.refresh(ENTITY))
      .catch((err) => { if (current) Storage.upsert(ENTITY, current); UI.showToast(err.message, 'error'); });
    return updated;
  };

  const hasRespuestas = (postulante) =>
    postulante && postulante.respuestas && Object.keys(postulante.respuestas).length > 0;

  const remove = (id) => {
    const current = get(id);
    Storage.removeCached(ENTITY, id);
    ContiAPI.eliminarPostulante(id)
      .catch((err) => { if (current) Storage.upsert(ENTITY, current); UI.showToast(err.message, 'error'); });
  };

  const softDelete = (id) => {
    const current = get(id);
    const updated = update(id, { estado: 'RECHAZADO' });
    ContiAPI.rechazar(id)
      .then((saved) => Storage.upsert(ENTITY, saved))
      .catch((err) => { if (current) Storage.upsert(ENTITY, current); UI.showToast(err.message, 'error'); });
    return updated;
  };

  const ranking = (ofertaId = null) => {
    const data = ofertaId ? byOferta(ofertaId) : list();
    return [...data].sort((a, b) => (b.puntajeFinal ?? b.puntaje ?? 0) - (a.puntajeFinal ?? a.puntaje ?? 0));
  };

  return { list, get, byOferta, byUsuario, create, update, setEstado, setObservacionAdmin, setPuntaje, saveEvaluation, hasRespuestas, remove, softDelete, ranking };
})();

window.Postulantes = Postulantes;
