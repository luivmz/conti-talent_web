/* =========================================================
   storage-api.js — Capa de datos REST sin localStorage
   ========================================================= */

const API = '';

const Storage = (() => {
  const cache = {
    usuarios: [],
    areas: [],
    ofertas: [],
    preguntas: [],
    postulantes: [],
    metricas: { series: {}, estadoActual: {} },
    session: null
  };

  const endpoints = {
    usuarios: '/api/usuarios',
    areas: '/api/areas',
    ofertas: '/api/ofertas',
    preguntas: '/api/preguntas',
    postulantes: '/api/postulantes',
    metricas: '/api/metricas'
  };

  const normalizeId = (value) => value === null || value === undefined ? null : String(value);
  const toNumber = (value) => value === null || value === undefined || value === '' ? null : Number(value);
  const rolCode = (u) => String(u.rol?.codigo || u.rolCodigo || u.rol || '').toLowerCase();

  const iconMap = {
    engineering: '💻',
    chart: '📊',
    scale: '⚖️',
    books: '📚',
    stethoscope: '🏥',
    microscope: '🔬',
    computer: '🖥️',
    handshake: '🤝',
    building: '🏢'
  };

  const displayIcon = (icono) => iconMap[icono] || icono || iconMap.building;

  const normalize = {
    usuarios: (items) => (items || []).map((u) => ({
      ...u,
      id: normalizeId(u.id),
      rolId: normalizeId(u.rolId || u.rol?.id),
      rol: rolCode(u),
      activo: u.activo !== false
    })),
    areas: (items) => (items || []).map((a) => ({ ...a, id: normalizeId(a.id), icono: displayIcon(a.icono) })),
    ofertas: (items) => (items || []).map((o) => ({
      ...o,
      id: normalizeId(o.id),
      areaId: normalizeId(o.areaId),
      horario: o.horario || '',
      requisitos: o.requisitos || [],
      beneficios: o.beneficios || []
    })),
    preguntas: (items) => (items || []).map((q) => ({
      ...q,
      id: normalizeId(q.id),
      ofertaId: normalizeId(q.ofertaId),
      opciones: q.opciones || []
    })),
    postulantes: (items) => (items || []).map((p) => ({
      ...p,
      id: normalizeId(p.id),
      usuarioId: normalizeId(p.usuarioId),
      ofertaId: normalizeId(p.ofertaId),
      estadoId: normalizeId(p.estadoId),
      entrevistas: (p.entrevistas || []).map((e) => ({ ...e, id: normalizeId(e.id), postulanteId: normalizeId(e.postulanteId) })),
      respuestas: p.respuestas || {}
    })),
    metricas: (data) => data || { series: {}, estadoActual: {} }
  };

  const normalizeSession = (s) => !s ? null : ({
    ...s,
    id: normalizeId(s.id),
    rol: String(s.rol || s.rolCodigo || '').toLowerCase()
  });

  const request = (url, opts = {}) =>
    fetch(API + url, {
      credentials: 'include',
      headers: { 'Content-Type': 'application/json', ...(opts.headers || {}) },
      ...opts
    }).then(async (response) => {
      if (response.status === 204) return null;
      const contentType = response.headers.get('content-type') || '';
      const body = contentType.includes('application/json') ? await response.json() : await response.text();
      if (!response.ok) {
        const message = body?.message || body?.error || body || `Error HTTP ${response.status}`;
        throw new Error(message);
      }
      return body;
    });

  const refresh = async (entity) => {
    const endpoint = endpoints[entity];
    if (!endpoint) return read(entity, entity === 'metricas' ? { series: {}, estadoActual: {} } : []);
    const data = await request(endpoint);
    cache[entity] = normalize[entity] ? normalize[entity](data) : data;
    return cache[entity];
  };

  const refreshSession = async () => {
    try {
      cache.session = normalizeSession(await request('/api/auth/me'));
    } catch (_err) {
      cache.session = null;
    }
    return cache.session;
  };

  const ready = Promise.all([
    refresh('usuarios'),
    refresh('areas'),
    refresh('ofertas'),
    refresh('preguntas'),
    refresh('postulantes'),
    refresh('metricas').catch(() => cache.metricas),
    refreshSession()
  ]).then(() => cache);

  const read = (entity, fallback = []) => {
    if (Object.prototype.hasOwnProperty.call(cache, entity)) return cache[entity] ?? fallback;
    return fallback;
  };

  const write = (entity, value) => {
    cache[entity] = normalize[entity] ? normalize[entity](value) : value;
  };

  const clear = (entity) => {
    if (entity === 'session') cache.session = null;
    else if (Array.isArray(cache[entity])) cache[entity] = [];
    else cache[entity] = null;
  };

  const upsert = (entity, item) => {
    const normalized = normalize[entity] ? normalize[entity]([item])[0] : item;
    const items = Array.isArray(cache[entity]) ? cache[entity] : [];
    const index = items.findIndex((x) => String(x.id) === String(normalized.id));
    if (index >= 0) items[index] = normalized;
    else items.push(normalized);
    cache[entity] = items;
    return normalized;
  };

  const removeCached = (entity, id) => {
    cache[entity] = (cache[entity] || []).filter((item) => String(item.id) !== String(id));
  };

  const generateId = () => 'tmp_' + Math.random().toString(36).slice(2, 10);

  return { ready, request, refresh, read, write, clear, upsert, removeCached, generateId, normalizeSession, toNumber };
})();

const ContiAPI = (() => ({
  login: (email, password) =>
    Storage.request('/api/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }),
  registro: (data) =>
    Storage.request('/api/auth/registro', { method: 'POST', body: JSON.stringify(data) }),
  me: () => Storage.request('/api/auth/me'),
  logout: () => Storage.request('/api/auth/logout', { method: 'POST' }),

  crearUsuario: (data) => Storage.request('/api/usuarios', { method: 'POST', body: JSON.stringify(data) }),
  actualizarUsuario: (id, data) => Storage.request(`/api/usuarios/${Storage.toNumber(id)}`, { method: 'PUT', body: JSON.stringify(data) }),
  eliminarUsuario: (id) => Storage.request(`/api/usuarios/${Storage.toNumber(id)}`, { method: 'DELETE' }),

  crearArea: (data) => Storage.request('/api/areas', { method: 'POST', body: JSON.stringify(data) }),
  actualizarArea: (id, data) => Storage.request(`/api/areas/${Storage.toNumber(id)}`, { method: 'PUT', body: JSON.stringify(data) }),
  eliminarArea: (id) => Storage.request(`/api/areas/${Storage.toNumber(id)}`, { method: 'DELETE' }),

  crearOferta: (data) => Storage.request('/api/ofertas', { method: 'POST', body: JSON.stringify(data) }),
  actualizarOferta: (id, data) => Storage.request(`/api/ofertas/${Storage.toNumber(id)}`, { method: 'PUT', body: JSON.stringify(data) }),
  eliminarOferta: (id) => Storage.request(`/api/ofertas/${Storage.toNumber(id)}`, { method: 'DELETE' }),

  crearPregunta: (data) => Storage.request('/api/preguntas', { method: 'POST', body: JSON.stringify(data) }),
  actualizarPregunta: (id, data) => Storage.request(`/api/preguntas/${Storage.toNumber(id)}`, { method: 'PUT', body: JSON.stringify(data) }),
  eliminarPregunta: (id) => Storage.request(`/api/preguntas/${Storage.toNumber(id)}`, { method: 'DELETE' }),

  postular: (data) => Storage.request('/api/postulantes', { method: 'POST', body: JSON.stringify(data) }),
  actualizarPostulante: (id, data) =>
    Storage.request(`/api/postulantes/${Storage.toNumber(id)}`, { method: 'PUT', body: JSON.stringify(data) }),
  cambiarEstado: (id, estado, extra = {}) =>
    Storage.request(`/api/postulantes/${Storage.toNumber(id)}/estado`, {
      method: 'PATCH',
      body: JSON.stringify({ estado, ...extra })
    }),
  actualizarObservacionPostulante: (id, observacionAdmin) =>
    Storage.request(`/api/postulantes/${Storage.toNumber(id)}/observaciones`, {
      method: 'PATCH',
      body: JSON.stringify({ observacionAdmin })
    }),
  registrarEntrevista: (id, data) =>
    Storage.request(`/api/postulantes/${Storage.toNumber(id)}/entrevistas`, { method: 'POST', body: JSON.stringify(data) }),
  listarEntrevistas: (id) =>
    Storage.request(`/api/postulantes/${Storage.toNumber(id)}/entrevistas`),
  actualizarEntrevista: (id, data) =>
    Storage.request(`/api/entrevistas/${Storage.toNumber(id)}`, { method: 'PUT', body: JSON.stringify(data) }),
  registrarResultadoEntrevista: (id, data) =>
    Storage.request(`/api/entrevistas/${Storage.toNumber(id)}/resultado`, { method: 'PATCH', body: JSON.stringify(data) }),
  cancelarEntrevista: (id, data = {}) =>
    Storage.request(`/api/entrevistas/${Storage.toNumber(id)}/cancelar`, { method: 'PATCH', body: JSON.stringify(data) }),
  reprogramarEntrevista: (id, data) =>
    Storage.request(`/api/entrevistas/${Storage.toNumber(id)}/reprogramar`, { method: 'PATCH', body: JSON.stringify(data) }),
  obtenerProcesoPostulante: (id) =>
    Storage.request(`/api/postulantes/${Storage.toNumber(id)}/proceso`),
  registrarEvaluacionPsicologica: (id, data) =>
    Storage.request(`/api/postulantes/${Storage.toNumber(id)}/evaluaciones-psicologicas`, { method: 'POST', body: JSON.stringify(data) }),
  rechazar: (id) => Storage.request(`/api/postulantes/${Storage.toNumber(id)}/rechazar`, { method: 'POST' }),
  eliminarPostulante: (id) => Storage.request(`/api/postulantes/${Storage.toNumber(id)}`, { method: 'DELETE' }),
  subirDocumentoPostulante: (id, tipoDocumento, archivo) => {
    const formData = new FormData();
    formData.append('tipoDocumento', tipoDocumento);
    formData.append('archivo', archivo);
    return fetch(API + `/api/postulantes/${Storage.toNumber(id)}/documentos`, {
      method: 'POST',
      credentials: 'include',
      body: formData
    }).then(async (response) => {
      const body = await response.json().catch(() => null);
      if (!response.ok) throw new Error(body?.message || body?.error || `Error HTTP ${response.status}`);
      return body;
    });
  },

  calificar: (postulanteId, respuestas) =>
    Storage.request('/api/evaluaciones', { method: 'POST', body: JSON.stringify({ postulanteId: Storage.toNumber(postulanteId), respuestas }) })
}))();

window.Storage = Storage;
window.ContiAPI = ContiAPI;
