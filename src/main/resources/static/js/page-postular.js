/* =========================================================
   page-postular.js — Formulario de postulación a oferta
   ========================================================= */

(() => {
  const params = new URLSearchParams(window.location.search);
  const ofertaId = params.get('oferta');

  const init = async () => {
    await Storage.ready;
    if (!Auth.requireAuth('login.html?next=' + encodeURIComponent(window.location.href))) return;

    const oferta = Ofertas.get(ofertaId);
    if (!oferta) {
      window.location.href = 'ofertas.html';
      return;
    }

    UI.$('#postular-title').textContent = `Postular a: ${oferta.titulo}`;
    UI.$('#postular-area').textContent  = Areas.get(oferta.areaId)?.nombre || '—';

    const session = Auth.getSession();
    if (session) {
      UI.$('input[name=nombre]').value = `${session.nombre} ${session.apellido}`;
      UI.$('input[name=email]').value  = session.email;
    }

    setupFileDrop();

    const form = document.getElementById('postular-form');
    form.addEventListener('submit', (e) => onSubmit(e, oferta));
  };

  const setupFileDrop = () => {
    const drop  = document.getElementById('cv-drop');
    const input = document.getElementById('cv-input');
    const label = document.getElementById('cv-label');

    drop.addEventListener('click', () => input.click());
    input.addEventListener('change', () => {
      if (input.files[0]) {
        label.textContent = input.files[0].name;
        drop.classList.add('is-active');
      }
    });
    drop.addEventListener('dragover', (e) => { e.preventDefault(); drop.classList.add('is-active'); });
    drop.addEventListener('dragleave', () => drop.classList.remove('is-active'));
    drop.addEventListener('drop', (e) => {
      e.preventDefault();
      if (e.dataTransfer.files[0]) {
        input.files = e.dataTransfer.files;
        label.textContent = e.dataTransfer.files[0].name;
      }
    });
  };

  const onSubmit = async (e, oferta) => {
    e.preventDefault();
    const form = e.currentTarget;
    const result = Validators.validateForm(form, {
      nombre:      [{ test: Validators.required, message: 'Nombre obligatorio' }],
      email:       [{ test: Validators.required, message: 'Correo obligatorio' },
                    { test: Validators.isEmail,  message: 'Correo no válido' }],
      telefono:    [{ test: Validators.required, message: 'Teléfono obligatorio' },
                    { test: Validators.isPhone,  message: 'Teléfono no válido' }],
      experiencia: [{ test: Validators.required, message: 'Cuéntanos sobre tu experiencia' }],
      habilidades: [{ test: Validators.required, message: 'Indica tus principales habilidades' }]
    });
    if (!result.valid) {
      UI.showToast('Revisa los campos del formulario', 'error');
      return;
    }

    const cvName = document.getElementById('cv-input').files[0]?.name || 'cv.pdf';
    const session = Auth.getSession();

    let postulante;
    try {
      postulante = await ContiAPI.postular({
        usuarioId: Storage.toNumber(session?.id),
        ofertaId: Storage.toNumber(oferta.id),
        nombre: result.values.nombre,
        email: result.values.email,
        telefono: result.values.telefono,
        experiencia: result.values.experiencia,
        habilidades: result.values.habilidades,
        cv: cvName,
        aniosExperiencia: parseInt(result.values.aniosExperiencia, 10) || 0,
        nivelEstudios: result.values.nivelEstudios,
        carrera: result.values.carrera,
        disponibilidad: result.values.disponibilidad,
        modalidadPreferida: result.values.modalidadPreferida,
        pretensionSalarial: result.values.pretensionSalarial ? Number(result.values.pretensionSalarial) : null,
        linkedin: result.values.linkedin,
        portafolio: result.values.portafolio
      });
      postulante = Storage.upsert('postulantes', postulante);
      const cvFile = document.getElementById('cv-input').files[0];
      if (cvFile) await ContiAPI.subirDocumentoPostulante(postulante.id, 'CV', cvFile);
      const extras = Array.from(document.getElementById('documentos-input')?.files || []);
      for (const archivo of extras) {
        await ContiAPI.subirDocumentoPostulante(postulante.id, 'ADICIONAL', archivo);
      }
      await Storage.refresh('postulantes');
    } catch (err) {
      UI.showToast(err.message || 'No se pudo registrar la postulacion', 'error');
      return;
    }

    UI.showToast('Postulación registrada con éxito', 'success');
    setTimeout(() => {
      window.location.href = `evaluacion.html?postulante=${postulante.id}`;
    }, 700);
  };

  document.addEventListener('DOMContentLoaded', init);
})();
