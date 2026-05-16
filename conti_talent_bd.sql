-- =====================================================================
-- conti_talent_bd.sql
-- Script de creacion de base de datos para Conti Talent.
-- Preparado para futura migracion a MySQL.
--
-- Cambios respecto a la version anterior:
--   * Las tablas auxiliares de oferta (requisito, beneficio, habilidad)
--     y la de respuestas del postulante ahora son entidades intermedias
--     con clave primaria propia (BIGINT AUTO_INCREMENT) en lugar de
--     claves compuestas, para reflejar la conversion de relaciones N:M
--     conceptuales en relaciones 1:N explicitas (entidades intermedias).
--   * Se agrego FK fk_respuesta_pregunta en tbl_postulante_respuesta.
--   * Se mantienen UNIQUE constraints para preservar la cardinalidad
--     logica original (una sola respuesta por par postulante-pregunta,
--     un solo orden por par oferta-orden, etc.).
-- =====================================================================

CREATE DATABASE IF NOT EXISTS conti_talent
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE conti_talent;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS tbl_postulante_respuesta;
DROP TABLE IF EXISTS tbl_documento_postulante;
DROP TABLE IF EXISTS tbl_historial_estado_postulante;
DROP TABLE IF EXISTS tbl_entrevista_postulante;
DROP TABLE IF EXISTS tbl_evaluacion_psicologica_postulante;
DROP TABLE IF EXISTS tbl_postulante;
DROP TABLE IF EXISTS tbl_pregunta_opcion;
DROP TABLE IF EXISTS tbl_pregunta;
DROP TABLE IF EXISTS tbl_oferta_beneficio;
DROP TABLE IF EXISTS tbl_oferta_habilidad;
DROP TABLE IF EXISTS tbl_oferta_requisito;
DROP TABLE IF EXISTS tbl_oferta;
DROP TABLE IF EXISTS tbl_usuario;
DROP TABLE IF EXISTS tbl_estado;
DROP TABLE IF EXISTS tbl_rol;
DROP TABLE IF EXISTS tbl_area;
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- CATALOGOS
-- =====================================================================

CREATE TABLE tbl_area (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(80) NOT NULL,
  descripcion VARCHAR(255),
  icono VARCHAR(30),
  color VARCHAR(10),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_rol (
  id BIGINT NOT NULL AUTO_INCREMENT,
  codigo VARCHAR(30) NOT NULL,
  nombre VARCHAR(80) NOT NULL,
  descripcion VARCHAR(255),
  activo BIT(1) NOT NULL,
  creado_en DATETIME NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_rol_codigo UNIQUE (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_estado (
  id BIGINT NOT NULL AUTO_INCREMENT,
  codigo VARCHAR(40) NOT NULL,
  nombre VARCHAR(80) NOT NULL,
  descripcion VARCHAR(255),
  orden INT NOT NULL,
  terminal BIT(1) NOT NULL,
  activo BIT(1) NOT NULL,
  creado_en DATETIME NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_estado_codigo UNIQUE (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- USUARIOS
-- =====================================================================

CREATE TABLE tbl_usuario (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(60) NOT NULL,
  apellido VARCHAR(60),
  email VARCHAR(120) NOT NULL,
  password VARCHAR(255) NOT NULL,
  rol_id BIGINT NOT NULL,
  activo BIT(1) NOT NULL,
  creado_en DATETIME NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_usuario_email UNIQUE (email),
  CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES tbl_rol (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- OFERTAS Y SUS ENTIDADES INTERMEDIAS
-- =====================================================================

CREATE TABLE tbl_oferta (
  id BIGINT NOT NULL AUTO_INCREMENT,
  titulo VARCHAR(120) NOT NULL,
  tipo VARCHAR(20) NOT NULL,
  area_id BIGINT NOT NULL,
  modalidad VARCHAR(20),
  ubicacion VARCHAR(80),
  horario VARCHAR(120),
  vacantes INT NOT NULL,
  destacada BIT(1) NOT NULL,
  descripcion TEXT,
  creada_en DATETIME NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_oferta_area FOREIGN KEY (area_id) REFERENCES tbl_area (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Entidad intermedia: Oferta - Requisito
CREATE TABLE tbl_oferta_requisito (
  id BIGINT NOT NULL AUTO_INCREMENT,
  oferta_id BIGINT NOT NULL,
  texto VARCHAR(255) NOT NULL,
  orden INT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_requisito_oferta FOREIGN KEY (oferta_id) REFERENCES tbl_oferta (id),
  CONSTRAINT uk_requisito_orden UNIQUE (oferta_id, orden)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Entidad intermedia: Oferta - Beneficio
CREATE TABLE tbl_oferta_beneficio (
  id BIGINT NOT NULL AUTO_INCREMENT,
  oferta_id BIGINT NOT NULL,
  texto VARCHAR(255) NOT NULL,
  orden INT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_beneficio_oferta FOREIGN KEY (oferta_id) REFERENCES tbl_oferta (id),
  CONSTRAINT uk_beneficio_orden UNIQUE (oferta_id, orden)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Entidad intermedia: Oferta - Habilidad
CREATE TABLE tbl_oferta_habilidad (
  id BIGINT NOT NULL AUTO_INCREMENT,
  oferta_id BIGINT NOT NULL,
  habilidad VARCHAR(120) NOT NULL,
  orden INT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_habilidad_oferta FOREIGN KEY (oferta_id) REFERENCES tbl_oferta (id),
  CONSTRAINT uk_habilidad_orden UNIQUE (oferta_id, orden)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- PREGUNTAS Y OPCIONES (composicion 1:N pura, no es N:M)
-- =====================================================================

CREATE TABLE tbl_pregunta (
  id BIGINT NOT NULL AUTO_INCREMENT,
  oferta_id BIGINT NOT NULL,
  enunciado VARCHAR(500) NOT NULL,
  correcta INT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_pregunta_oferta FOREIGN KEY (oferta_id) REFERENCES tbl_oferta (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Las opciones pertenecen logicamente a la pregunta (composicion).
-- No se modeló como entidad intermedia porque no resuelve un N:M.
CREATE TABLE tbl_pregunta_opcion (
  pregunta_id BIGINT NOT NULL,
  indice INT NOT NULL,
  texto VARCHAR(255) NOT NULL,
  PRIMARY KEY (pregunta_id, indice),
  CONSTRAINT fk_opcion_pregunta FOREIGN KEY (pregunta_id) REFERENCES tbl_pregunta (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- POSTULANTES Y ENTIDADES RELACIONADAS
-- =====================================================================

CREATE TABLE tbl_postulante (
  id BIGINT NOT NULL AUTO_INCREMENT,
  usuario_id BIGINT,
  oferta_id BIGINT NOT NULL,
  estado_id BIGINT NOT NULL,
  nombre VARCHAR(120) NOT NULL,
  email VARCHAR(120) NOT NULL,
  telefono VARCHAR(30),
  experiencia TEXT,
  habilidades TEXT,
  cv VARCHAR(255),
  fecha_postulacion DATETIME NOT NULL,
  fecha_evaluacion DATETIME,
  anios_experiencia INT NOT NULL DEFAULT 0,
  nivel_estudios VARCHAR(80),
  carrera VARCHAR(120),
  disponibilidad VARCHAR(80),
  modalidad_preferida VARCHAR(40),
  pretension_salarial DOUBLE,
  linkedin VARCHAR(255),
  portafolio VARCHAR(255),
  observacion_admin TEXT,
  puntaje_cuestionario INT NOT NULL DEFAULT 0,
  puntaje_experiencia INT NOT NULL DEFAULT 0,
  puntaje_habilidades INT NOT NULL DEFAULT 0,
  puntaje_final INT NOT NULL DEFAULT 0,
  creado_en DATETIME NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_postulante_usuario FOREIGN KEY (usuario_id) REFERENCES tbl_usuario (id),
  CONSTRAINT fk_postulante_oferta FOREIGN KEY (oferta_id) REFERENCES tbl_oferta (id),
  CONSTRAINT fk_postulante_estado FOREIGN KEY (estado_id) REFERENCES tbl_estado (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_documento_postulante (
  id BIGINT NOT NULL AUTO_INCREMENT,
  postulante_id BIGINT NOT NULL,
  tipo_documento VARCHAR(40) NOT NULL,
  nombre_original VARCHAR(255) NOT NULL,
  nombre_archivo VARCHAR(255) NOT NULL,
  ruta_archivo VARCHAR(500) NOT NULL,
  extension VARCHAR(10) NOT NULL,
  tamanio BIGINT NOT NULL,
  fecha_subida DATETIME NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_documento_postulante FOREIGN KEY (postulante_id) REFERENCES tbl_postulante (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_historial_estado_postulante (
  id BIGINT NOT NULL AUTO_INCREMENT,
  postulante_id BIGINT NOT NULL,
  estado_anterior VARCHAR(40),
  estado_nuevo VARCHAR(40) NOT NULL,
  fecha_cambio DATETIME NOT NULL,
  usuario_admin VARCHAR(120),
  observacion_interna TEXT,
  observacion_postulante TEXT,
  PRIMARY KEY (id),
  CONSTRAINT fk_historial_postulante FOREIGN KEY (postulante_id) REFERENCES tbl_postulante (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_entrevista_postulante (
  id BIGINT NOT NULL AUTO_INCREMENT,
  postulante_id BIGINT NOT NULL,
  tipo_entrevista VARCHAR(40) NOT NULL,
  fecha_programada DATETIME NOT NULL,
  hora_inicio VARCHAR(5),
  hora_fin VARCHAR(5),
  modalidad VARCHAR(30) NOT NULL,
  lugar VARCHAR(255),
  enlace_virtual VARCHAR(500),
  entrevistador_nombre VARCHAR(120),
  entrevistador_cargo VARCHAR(120),
  estado_entrevista VARCHAR(30) NOT NULL,
  resultado VARCHAR(30) NOT NULL,
  observacion_interna TEXT,
  observacion_postulante TEXT,
  usuario_admin VARCHAR(120),
  creado_por_admin_id BIGINT,
  actualizado_por_admin_id BIGINT,
  actualizado_por_admin VARCHAR(120),
  observacion_cambio TEXT,
  creado_en DATETIME NOT NULL,
  actualizado_en DATETIME,
  PRIMARY KEY (id),
  CONSTRAINT fk_entrevista_postulante FOREIGN KEY (postulante_id) REFERENCES tbl_postulante (id),
  CONSTRAINT fk_entrevista_admin_creador FOREIGN KEY (creado_por_admin_id) REFERENCES tbl_usuario (id),
  CONSTRAINT fk_entrevista_admin_editor FOREIGN KEY (actualizado_por_admin_id) REFERENCES tbl_usuario (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_evaluacion_psicologica_postulante (
  id BIGINT NOT NULL AUTO_INCREMENT,
  postulante_id BIGINT NOT NULL,
  fecha_evaluacion DATETIME NOT NULL,
  resultado VARCHAR(40),
  observacion TEXT,
  usuario_admin VARCHAR(120),
  creado_en DATETIME NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_eval_psico_postulante FOREIGN KEY (postulante_id) REFERENCES tbl_postulante (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Entidad intermedia: Postulante - Pregunta (resuelve la N:M conceptual).
-- La unicidad por (postulante_id, pregunta_id) impide respuestas duplicadas.
CREATE TABLE tbl_postulante_respuesta (
  id BIGINT NOT NULL AUTO_INCREMENT,
  postulante_id BIGINT NOT NULL,
  pregunta_id BIGINT NOT NULL,
  opcion_elegida INT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_respuesta_postulante FOREIGN KEY (postulante_id) REFERENCES tbl_postulante (id),
  CONSTRAINT fk_respuesta_pregunta   FOREIGN KEY (pregunta_id)  REFERENCES tbl_pregunta (id),
  CONSTRAINT uk_respuesta_postulante_pregunta UNIQUE (postulante_id, pregunta_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- DATOS SEED (alineados con los que produce DataLoader.java)
-- =====================================================================

SET @ahora := CURRENT_TIMESTAMP;

INSERT INTO tbl_rol (id, codigo, nombre, descripcion, activo, creado_en) VALUES
(1, 'ADMIN', 'Administrador', 'Acceso total al sistema: gestion de usuarios, areas, ofertas, postulantes y metricas.', b'1', DATE_SUB(@ahora, INTERVAL 60 DAY)),
(2, 'POSTULANTE', 'Postulante', 'Usuario externo que postula a las ofertas publicadas por la institucion.', b'1', DATE_SUB(@ahora, INTERVAL 60 DAY));

INSERT INTO tbl_estado (id, codigo, nombre, descripcion, orden, terminal, activo, creado_en) VALUES
(1, 'POSTULADO',              'Postulado',              'Postulacion recibida y pendiente de evaluacion tecnica.',                  1, b'0', b'1', DATE_SUB(@ahora, INTERVAL 60 DAY)),
(2, 'EN_EVALUACION',          'En evaluacion',          'El postulante rindio la prueba pero no alcanzo el umbral de aprobacion.',  2, b'0', b'1', DATE_SUB(@ahora, INTERVAL 60 DAY)),
(3, 'APROBADO_TECNICO',       'Aprobado tecnico',       'Aprobo la evaluacion tecnica y avanza a entrevista.',                      3, b'0', b'1', DATE_SUB(@ahora, INTERVAL 60 DAY)),
(4, 'ENTREVISTA',             'Entrevista',             'Citado o citada para entrevista personal.',                                4, b'0', b'1', DATE_SUB(@ahora, INTERVAL 60 DAY)),
(5, 'EVALUACION_PSICOLOGICA', 'Evaluacion psicologica', 'Aprobada la entrevista, paso a evaluacion psicologica.',                   5, b'0', b'1', DATE_SUB(@ahora, INTERVAL 60 DAY)),
(6, 'ACEPTADO',               'Aceptado',               'Proceso completo. Candidato aceptado para la posicion.',                   6, b'1', b'1', DATE_SUB(@ahora, INTERVAL 60 DAY)),
(7, 'RECHAZADO',              'Rechazado',              'Candidato descartado en alguna etapa del proceso.',                        7, b'1', b'1', DATE_SUB(@ahora, INTERVAL 60 DAY));

INSERT INTO tbl_usuario (id, nombre, apellido, email, password, rol_id, activo, creado_en) VALUES
(1, 'Administrador', 'Continental', 'admin@contitalent.com', 'admin123', 1, b'1', DATE_SUB(@ahora, INTERVAL 30 DAY)),
(2, 'Lucia',         'Ramos',       'lucia@example.com',     'lucia123', 2, b'1', DATE_SUB(@ahora, INTERVAL 8 DAY)),
(3, 'Carlos',        'Mendoza',     'carlos@example.com',    'carlos123',2, b'1', DATE_SUB(@ahora, INTERVAL 5 DAY)),
(4, 'Maria',         'Torres',      'maria@example.com',     'maria123', 2, b'1', DATE_SUB(@ahora, INTERVAL 2 DAY)),
(5, 'Pedro',         'Salinas',     'pedro@example.com',     'pedro123', 2, b'1', DATE_SUB(@ahora, INTERVAL 7 DAY)),
(6, 'Andrea',        'Leon',        'andrea@example.com',    'andrea123',2, b'1', DATE_SUB(@ahora, INTERVAL 10 DAY)),
(7, 'Diego',         'Alvarez',     'diego@example.com',     'diego123', 2, b'1', DATE_SUB(@ahora, INTERVAL 15 DAY)),
(8, 'Fiorella',      'Rojas',       'fiorella@example.com',  'fiora123', 2, b'1', DATE_SUB(@ahora, INTERVAL 8 DAY)),
(9, 'Renato',        'Quispe',      'renato@example.com',    'renato123',2, b'1', DATE_SUB(@ahora, INTERVAL 4 DAY)),
(10,'Valeria',       'Campos',      'valeria@example.com',   'vale123',  2, b'1', DATE_SUB(@ahora, INTERVAL 6 DAY)),
(11,'Nicolas',       'Vargas',      'nicolas@example.com',   'nico123',  2, b'1', DATE_SUB(@ahora, INTERVAL 7 DAY)),
(12,'Gabriela',      'Paredes',     'gabriela@example.com',  'gaby123',  2, b'1', DATE_SUB(@ahora, INTERVAL 11 DAY)),
(13,'Sofia',         'Huaman',      'sofia@example.com',     'sofia123', 2, b'1', DATE_SUB(@ahora, INTERVAL 13 DAY)),
(14,'Mateo',         'Caceres',     'mateo@example.com',     'mateo123', 2, b'1', DATE_SUB(@ahora, INTERVAL 9 DAY));

INSERT INTO tbl_area (id, nombre, descripcion, icono, color) VALUES
(1, 'Ingenieria',                 'Sistemas, civil, industrial, mecatronica y minas.',                          'engineering', '#6366f1'),
(2, 'Ciencias de la Empresa',     'Administracion, contabilidad, marketing y negocios.',                        'chart',       '#06b6d4'),
(3, 'Derecho',                    'Derecho corporativo, civil, penal y constitucional.',                        'scale',       '#8b5cf6'),
(4, 'Humanidades',                'Comunicacion, psicologia y educacion.',                                      'books',       '#ec4899'),
(5, 'Ciencias de la Salud',       'Enfermeria, psicologia clinica y nutricion.',                                'stethoscope', '#10b981'),
(6, 'Investigacion y Desarrollo', 'Centros de investigacion e innovacion universitaria.',                       'microscope',  '#f59e0b'),
(7, 'Tecnologia y Sistemas',      'Equipo de TI institucional: infraestructura, soporte y software interno.',  'computer',    '#0ea5e9'),
(8, 'Bienestar Universitario',    'Soporte estudiantil, deportes, cultura y atencion psicopedagogica.',         'handshake',   '#f43f5e');

INSERT INTO tbl_oferta (id, titulo, tipo, area_id, modalidad, ubicacion, horario, vacantes, destacada, descripcion, creada_en) VALUES
(1,  'Profesor de Programacion I',              'Trabajo',  1, 'Presencial', 'Huancayo', 'Lunes, miercoles y viernes 08:00 - 12:00', 2, b'1', 'Docente para el curso de Programacion I (Java) en la Escuela de Ingenieria de Sistemas.',                  DATE_SUB(@ahora, INTERVAL 3 DAY)),
(2,  'Practica Pre-Profesional Sistemas',       'Practica', 1, 'Hibrido',    'Huancayo', 'Lunes a viernes 09:00 - 14:00',            4, b'1', 'Apoyo al area de Sistemas y TI: soporte, desarrollo de pequenias mejoras y gestion de tickets.',           DATE_SUB(@ahora, INTERVAL 2 DAY)),
(3,  'Profesor de Marketing Digital',           'Trabajo',  2, 'Presencial', 'Huancayo', 'Martes y jueves 18:00 - 21:00',            1, b'1', 'Docente para Marketing Digital y Estrategia Comercial.',                                                    DATE_SUB(@ahora, INTERVAL 4 DAY)),
(4,  'Practica Pre-Profesional Marketing',      'Practica', 2, 'Presencial', 'Huancayo', 'Lunes a viernes 08:30 - 13:30',            3, b'1', 'Apoyo en campanias digitales, contenidos y analitica para la marca Continental.',                          DATE_SUB(@ahora, INTERVAL 1 DAY)),
(5,  'Profesor de Derecho Constitucional',      'Trabajo',  3, 'Presencial', 'Huancayo', 'Sabados 08:00 - 13:00',                    1, b'0', 'Docente para el curso de Derecho Constitucional.',                                                          DATE_SUB(@ahora, INTERVAL 6 DAY)),
(6,  'Practica Profesional Derecho Civil',      'Practica', 3, 'Hibrido',    'Huancayo', 'Lunes a viernes 09:00 - 15:00',            2, b'0', 'Apoyo al consultorio juridico del area de Derecho.',                                                        DATE_SUB(@ahora, INTERVAL 5 DAY)),
(7,  'Profesor de Comunicacion Oral y Escrita', 'Trabajo',  4, 'Presencial', 'Huancayo', 'Lunes y miercoles 16:00 - 20:00',          2, b'0', 'Curso transversal en pregrado.',                                                                            DATE_SUB(@ahora, INTERVAL 8 DAY)),
(8,  'Asistente de Investigacion Salud Publica','Practica', 5, 'Hibrido',    'Huancayo', 'Lunes a viernes 08:00 - 13:00',            2, b'0', 'Apoyo a investigacion de campo en proyectos de salud publica.',                                             DATE_SUB(@ahora, INTERVAL 9 DAY)),
(9,  'Coordinador de Investigacion',            'Trabajo',  6, 'Presencial', 'Huancayo', 'Lunes a viernes 08:00 - 17:00',            1, b'0', 'Lidera proyectos del Centro de Investigacion.',                                                             DATE_SUB(@ahora, INTERVAL 11 DAY)),
(10, 'Practica Soporte de TI Universitario',    'Practica', 7, 'Presencial', 'Huancayo', 'Turno maniana 08:00 - 13:00',              3, b'1', 'Apoyo al equipo institucional de Tecnologia y Sistemas.',                                                   DATE_SUB(@ahora, INTERVAL 1 DAY)),
(11, 'Coordinador de Bienestar Estudiantil',    'Trabajo',  8, 'Presencial', 'Huancayo', 'Lunes a viernes 08:30 - 17:30',            1, b'0', 'Lidera el equipo de Bienestar Universitario.',                                                              DATE_SUB(@ahora, INTERVAL 4 DAY));

-- ===== Requisitos por oferta =====
INSERT INTO tbl_oferta_requisito (oferta_id, texto, orden) VALUES
(1, 'Ingeniero de Sistemas o afin', 0), (1, '2+ anios enseniando o desarrollando software', 1), (1, 'Manejo de Java y bases de datos', 2), (1, 'Experiencia en metodologias agiles', 3),
(2, 'Estudiante de Ingenieria de Sistemas', 0), (2, 'Conocimientos de HTML, CSS y JS', 1), (2, 'Buena comunicacion', 2),
(3, 'Profesional en Marketing o Negocios', 0), (3, 'Experiencia en performance digital', 1), (3, 'Maestria (deseable)', 2),
(4, 'Estudiante de Marketing/Administracion', 0), (4, 'Manejo de redes sociales', 1), (4, 'Curiosidad y proactividad', 2),
(5, 'Abogado titulado', 0), (5, 'Maestria o doctorado en Derecho', 1), (5, 'Publicaciones academicas', 2),
(6, 'Egresado o bachiller en Derecho', 0), (6, 'Buen manejo de redaccion', 1),
(7, 'Licenciado en Comunicacion o Educacion', 0), (7, 'Experiencia minima 2 anios', 1),
(8, 'Estudiante de Ciencias de la Salud', 0), (8, 'Estadistica basica', 1),
(9, 'Magister o doctor', 0), (9, 'Publicaciones indexadas', 1), (9, 'Liderazgo de equipos', 2),
(10, 'Estudios tecnicos o universitarios en TI', 0), (10, 'Conocimientos de redes y hardware', 1), (10, 'Buena atencion al usuario', 2),
(11, 'Profesional en Psicologia, Educacion o afin', 0), (11, '3+ anios en gestion estudiantil', 1), (11, 'Habilidades de liderazgo', 2);

-- ===== Beneficios por oferta =====
INSERT INTO tbl_oferta_beneficio (oferta_id, texto, orden) VALUES
(1, 'Carga horaria flexible', 0), (1, 'Capacitacion pedagogica', 1), (1, 'Convenios interinstitucionales', 2),
(2, 'Subvencion economica', 0), (2, 'Mentoria', 1), (2, 'Certificacion de practicas', 2),
(3, 'Plan de carrera docente', 0), (3, 'Investigacion remunerada', 1),
(4, 'Subvencion economica', 0), (4, 'Aprendizaje real', 1), (4, 'Certificacion', 2),
(5, 'Bonos por publicacion', 0), (5, 'Apoyo a investigacion', 1),
(6, 'Subvencion', 0), (6, 'Acompaniamiento profesional', 1),
(7, 'Plan docente', 0), (7, 'Becas para postgrado', 1),
(8, 'Subvencion', 0), (8, 'Co-autoria en publicaciones', 1),
(9, 'Sueldo competitivo', 0), (9, 'Asignacion de proyectos', 1),
(10, 'Subvencion economica', 0), (10, 'Certificacion de practicas', 1), (10, 'Plan de mentoria', 2),
(11, 'Plan de carrera', 0), (11, 'Capacitacion continua', 1), (11, 'Contrato estable', 2);

-- ===== Habilidades requeridas por oferta =====
INSERT INTO tbl_oferta_habilidad (oferta_id, habilidad, orden) VALUES
(1, 'Java', 0), (1, 'bases de datos', 1), (1, 'metodologias agiles', 2), (1, 'didactica', 3),
(2, 'HTML', 0), (2, 'CSS', 1), (2, 'JavaScript', 2), (2, 'soporte tecnico', 3),
(3, 'SEO', 0), (3, 'Ads', 1), (3, 'analitica', 2), (3, 'didactica', 3),
(4, 'redes sociales', 0), (4, 'contenido', 1), (4, 'Figma', 2),
(5, 'Derecho', 0), (5, 'publicaciones academicas', 1), (5, 'docencia', 2),
(6, 'redaccion', 0), (6, 'Derecho civil', 1),
(7, 'comunicacion', 0), (7, 'educacion', 1), (7, 'docencia', 2),
(8, 'estadistica', 0), (8, 'investigacion', 1),
(9, 'publicaciones indexadas', 0), (9, 'liderazgo', 1), (9, 'investigacion', 2),
(10, 'redes', 0), (10, 'hardware', 1), (10, 'atencion al usuario', 2),
(11, 'liderazgo', 0), (11, 'gestion estudiantil', 1), (11, 'psicologia', 2);

-- ===== Preguntas y opciones =====
INSERT INTO tbl_pregunta (id, oferta_id, enunciado, correcta) VALUES
(1, 1, 'Que patron de diseno desacopla logica de presentacion?', 1),
(2, 1, 'Cual es la principal ventaja de la inyeccion de dependencias?', 1),
(3, 1, 'Que hace una sentencia INNER JOIN en SQL?', 1),
(4, 1, 'Que metodologia activa promueve aprender por proyectos?', 1),
(5, 1, 'Cual NO es un metodo HTTP idempotente?', 3),
(6, 2, 'Que etiqueta HTML5 representa contenido principal?', 1),
(7, 2, 'Que propiedad CSS alinea elementos en eje horizontal con flex?', 1),
(8, 2, 'Buena practica para evitar XSS?', 1),
(9, 3, 'Que metrica mide el costo de adquirir un cliente?', 1),
(10, 3, 'Cual es el proposito principal del SEO tecnico?', 1),
(11, 3, 'En un funnel, que etapa va antes de la decision?', 1),
(12, 4, 'Herramienta estandar para piezas de redes sociales?', 0),
(13, 4, 'Red social con mayor alcance B2B en LATAM?', 1);

INSERT INTO tbl_pregunta_opcion (pregunta_id, indice, texto) VALUES
(1, 0, 'Singleton'), (1, 1, 'MVC'), (1, 2, 'Observer'), (1, 3, 'Factory'),
(2, 0, 'Mejor rendimiento en runtime'), (2, 1, 'Tests mas sencillos y bajo acoplamiento'), (2, 2, 'Reduce el bundle'), (2, 3, 'Reemplaza interfaces'),
(3, 0, 'Retorna todas las filas de ambas tablas'), (3, 1, 'Retorna filas con coincidencia en ambas tablas'), (3, 2, 'Retorna filas unicas'), (3, 3, 'Combina filas evitando duplicados'),
(4, 0, 'Conferencia magistral'), (4, 1, 'Aprendizaje basado en proyectos'), (4, 2, 'Examen oral'), (4, 3, 'Tutoriales pre-grabados'),
(5, 0, 'GET'), (5, 1, 'PUT'), (5, 2, 'DELETE'), (5, 3, 'POST'),
(6, 0, '<section>'), (6, 1, '<main>'), (6, 2, '<article>'), (6, 3, '<div>'),
(7, 0, 'align-items'), (7, 1, 'justify-content'), (7, 2, 'flex-wrap'), (7, 3, 'grid-template'),
(8, 0, 'Concatenar HTML con datos del usuario'), (8, 1, 'Escapar la salida y validar entradas'), (8, 2, 'Usar localStorage'), (8, 3, 'Cifrar las URLs'),
(9, 0, 'CTR'), (9, 1, 'CAC'), (9, 2, 'ROI'), (9, 3, 'CPM'),
(10, 0, 'Crear contenido viral'), (10, 1, 'Optimizar la indexacion y rendimiento'), (10, 2, 'Comprar enlaces'), (10, 3, 'Disenar logos'),
(11, 0, 'Awareness'), (11, 1, 'Consideration'), (11, 2, 'Retention'), (11, 3, 'Loyalty'),
(12, 0, 'Figma'), (12, 1, 'Photoshop'), (12, 2, 'Excel'), (12, 3, 'Notepad'),
(13, 0, 'Pinterest'), (13, 1, 'LinkedIn'), (13, 2, 'TikTok'), (13, 3, 'Snapchat');

-- ===== Postulantes =====
INSERT INTO tbl_postulante (id, usuario_id, oferta_id, estado_id, nombre, email, telefono, experiencia, habilidades, cv,
  fecha_postulacion, fecha_evaluacion, anios_experiencia, nivel_estudios, carrera, disponibilidad, modalidad_preferida,
  puntaje_cuestionario, puntaje_experiencia, puntaje_habilidades, puntaje_final, creado_en) VALUES
(1, 2, 2, 2, 'Lucia Ramos',     'lucia@example.com',    '+51 987 654 321', '3 anios apoyando areas de TI en universidades',     'JavaScript, soporte tecnico, atencion al usuario',          'lucia_cv.pdf',    DATE_SUB(@ahora, INTERVAL 4 DAY), DATE_ADD(DATE_SUB(@ahora, INTERVAL 4 DAY), INTERVAL 1 HOUR), 3, 'Universitario', 'Ingenieria de Sistemas', 'Inmediata', 'Hibrido',     67,  100, 60, 76,  DATE_SUB(@ahora, INTERVAL 4 DAY)),
(2, 3, 1, 3, 'Carlos Mendoza',  'carlos@example.com',   '+51 911 222 333', '5 anios en arquitectura de software y docencia',     'Java, Spring, Docker, didactica universitaria',             'carlos_cv.pdf',   DATE_SUB(@ahora, INTERVAL 3 DAY), DATE_ADD(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 1 HOUR), 5, 'Titulado',      'Ingenieria de Sistemas', 'Inmediata', 'Presencial',  100, 100, 60, 92,  DATE_SUB(@ahora, INTERVAL 3 DAY)),
(3, 4, 4, 1, 'Maria Torres',    'maria@example.com',    '+51 933 555 777', 'Estudiante de Marketing en ultimo ciclo',            'Community management, creacion de contenido',               'maria_cv.pdf',    DATE_SUB(@ahora, INTERVAL 1 DAY),        NULL,                          0, 'Universitario', 'Marketing',              '15 dias',  'Presencial',  0,   0,   60, 12,  DATE_SUB(@ahora, INTERVAL 1 DAY)),
(4, 5, 2, 4, 'Pedro Salinas',   'pedro@example.com',    '+51 922 444 666', 'Estudiante de Sistemas con practicas previas',       'JavaScript, HTML, CSS, soporte',                            'pedro_cv.pdf',    DATE_SUB(@ahora, INTERVAL 6 DAY), DATE_ADD(DATE_SUB(@ahora, INTERVAL 6 DAY), INTERVAL 1 HOUR), 1, 'Universitario', 'Ingenieria de Sistemas', 'Inmediata', 'Hibrido',     100, 30,  60, 71,  DATE_SUB(@ahora, INTERVAL 6 DAY)),
(5, 6, 3, 5, 'Andrea Leon',     'andrea@example.com',   '+51 944 888 111', '6 anios en marketing B2B y docencia universitaria',  'Estrategia digital, didactica, public speaking',            'andrea_cv.pdf',   DATE_SUB(@ahora, INTERVAL 9 DAY), DATE_ADD(DATE_SUB(@ahora, INTERVAL 9 DAY), INTERVAL 1 HOUR), 6, 'Maestria',      'Marketing',              'Inmediata', 'Presencial',  100, 100, 60, 92,  DATE_SUB(@ahora, INTERVAL 9 DAY)),
(6, 7, 3, 6, 'Diego Alvarez',   'diego@example.com',    '+51 955 777 222', '8 anios liderando agencias de marketing digital',    'Ads, SEO, analitica, formacion de equipos',                 'diego_cv.pdf',    DATE_SUB(@ahora, INTERVAL 14 DAY),DATE_ADD(DATE_SUB(@ahora, INTERVAL 14 DAY), INTERVAL 1 HOUR), 8, 'Titulado',      'Marketing',              'Inmediata', 'Presencial',  100, 100, 60, 92,  DATE_SUB(@ahora, INTERVAL 14 DAY)),
(7, 8, 4, 7, 'Fiorella Rojas',  'fiorella@example.com', '+51 966 333 444', '1 anio en diseno grafico',                            'Figma, Illustrator',                                        'fiorella_cv.pdf', DATE_SUB(@ahora, INTERVAL 7 DAY), DATE_ADD(DATE_SUB(@ahora, INTERVAL 7 DAY), INTERVAL 1 HOUR), 1, 'Universitario', 'Diseno',                 'Inmediata', 'Presencial',  50,  30,  60, 46,  DATE_SUB(@ahora, INTERVAL 7 DAY)),
(8, 9, 1, 3, 'Renato Quispe',   'renato@example.com',   '+51 900 111 222', '4 anios desarrollando backend Java y apoyando laboratorios', 'Java, Spring Boot, SQL, tutoria academica',             'renato_cv.pdf',   DATE_SUB(@ahora, INTERVAL 4 DAY), DATE_ADD(DATE_SUB(@ahora, INTERVAL 4 DAY), INTERVAL 1 HOUR), 4, 'Universitario', 'No especificada',       'Inmediata', 'Presencial',  80,  100, 60, 82,  DATE_SUB(@ahora, INTERVAL 4 DAY)),
(9, 10,2, 4, 'Valeria Campos',  'valeria@example.com',  '+51 900 333 444', '2 anios en mesa de ayuda universitaria',              'HTML, CSS, soporte tecnico, comunicacion',                  'valeria_cv.pdf',  DATE_SUB(@ahora, INTERVAL 6 DAY), DATE_ADD(DATE_SUB(@ahora, INTERVAL 6 DAY), INTERVAL 1 HOUR), 2, 'Universitario', 'No especificada',       'Inmediata', 'Hibrido',     100, 60,  60, 80,  DATE_SUB(@ahora, INTERVAL 6 DAY)),
(10,11,4, 4, 'Nicolas Vargas',  'nicolas@example.com',  '+51 900 555 666', '2 anios creando contenido y reportes de campanias',   'Figma, contenido, LinkedIn, analitica basica',              'nicolas_cv.pdf',  DATE_SUB(@ahora, INTERVAL 7 DAY), DATE_ADD(DATE_SUB(@ahora, INTERVAL 7 DAY), INTERVAL 1 HOUR), 2, 'Universitario', 'No especificada',       'Inmediata', 'Presencial',  100, 60,  60, 80,  DATE_SUB(@ahora, INTERVAL 7 DAY)),
(11,12,3, 5, 'Gabriela Paredes','gabriela@example.com', '+51 900 777 888', '7 anios en estrategia digital y clases de especializacion', 'SEO, Ads, analitica, didactica',                         'gabriela_cv.pdf', DATE_SUB(@ahora, INTERVAL 11 DAY),DATE_ADD(DATE_SUB(@ahora, INTERVAL 11 DAY), INTERVAL 1 HOUR), 7, 'Universitario', 'No especificada',       'Inmediata', 'Presencial',  100, 100, 60, 92,  DATE_SUB(@ahora, INTERVAL 11 DAY)),
(12,13,11,6, 'Sofia Huaman',    'sofia@example.com',    '+51 900 999 111', '6 anios liderando programas de bienestar estudiantil','psicologia, liderazgo, gestion estudiantil',                'sofia_cv.pdf',    DATE_SUB(@ahora, INTERVAL 13 DAY),NULL,                          6, 'Universitario', 'No especificada',       'Inmediata', 'Presencial',  0,   100, 60, 42,  DATE_SUB(@ahora, INTERVAL 13 DAY)),
(13,14,1, 7, 'Mateo Caceres',   'mateo@example.com',    '+51 901 222 333', '3 anios como desarrollador junior',                   'Java basico, SQL, Git',                                     'mateo_cv.pdf',    DATE_SUB(@ahora, INTERVAL 9 DAY), DATE_ADD(DATE_SUB(@ahora, INTERVAL 9 DAY), INTERVAL 1 HOUR), 3, 'Universitario', 'No especificada',       'Inmediata', 'Presencial',  60,  100, 60, 72,  DATE_SUB(@ahora, INTERVAL 9 DAY));

INSERT INTO tbl_documento_postulante (id, postulante_id, tipo_documento, nombre_original, nombre_archivo, ruta_archivo, extension, tamanio, fecha_subida) VALUES
(1, 1, 'CV',          'lucia_cv.pdf',           'lucia_cv.pdf',           'uploads/cv/lucia_cv.pdf',           'pdf', 4025, DATE_SUB(@ahora, INTERVAL 4 DAY)),
(2, 2, 'CV',          'carlos_cv.pdf',          'carlos_cv.pdf',          'uploads/cv/carlos_cv.pdf',          'pdf', 4021, DATE_SUB(@ahora, INTERVAL 3 DAY)),
(3, 2, 'CERTIFICADO', 'certificado_spring.pdf', 'certificado_spring.pdf', 'uploads/certificados/certificado_spring.pdf', 'pdf', 4019, DATE_SUB(@ahora, INTERVAL 3 DAY)),
(4, 4, 'CV',          'pedro_cv.pdf',           'pedro_cv.pdf',           'uploads/cv/pedro_cv.pdf',           'pdf', 4016, DATE_SUB(@ahora, INTERVAL 6 DAY)),
(5, 5, 'CV',          'andrea_cv.pdf',          'andrea_cv.pdf',          'uploads/cv/andrea_cv.pdf',          'pdf', 4026, DATE_SUB(@ahora, INTERVAL 9 DAY));

INSERT INTO tbl_historial_estado_postulante (postulante_id, estado_anterior, estado_nuevo, fecha_cambio, usuario_admin, observacion_interna, observacion_postulante) VALUES
(1, NULL,                     'POSTULADO',              DATE_SUB(@ahora, INTERVAL 4 DAY),                'Sistema',              'Postulacion registrada',         'Tu postulacion fue recibida correctamente.'),
(1, 'POSTULADO',              'EN_EVALUACION',          DATE_ADD(DATE_SUB(@ahora, INTERVAL 4 DAY), INTERVAL 1 HOUR),      'Sistema',              'Evaluacion tecnica registrada',  'Tu evaluacion tecnica fue registrada.'),
(2, NULL,                     'POSTULADO',              DATE_SUB(@ahora, INTERVAL 3 DAY),                'Sistema',              'Postulacion registrada',         'Tu postulacion fue recibida correctamente.'),
(2, 'POSTULADO',              'APROBADO_TECNICO',       DATE_ADD(DATE_SUB(@ahora, INTERVAL 3 DAY), INTERVAL 1 HOUR),      'Sistema',              'Evaluacion tecnica aprobada',    'Aprobaste la evaluacion tecnica.'),
(4, 'APROBADO_TECNICO',       'ENTREVISTA',             DATE_SUB(@ahora, INTERVAL 5 DAY),                'admin@contitalent.com', 'Entrevista agendada',           'Tu entrevista fue programada.'),
(5, 'ENTREVISTA',             'EVALUACION_PSICOLOGICA', DATE_SUB(@ahora, INTERVAL 8 DAY),                'admin@contitalent.com', 'Pasa a evaluacion psicologica', 'Continuas a evaluacion psicologica.'),
(6, 'EVALUACION_PSICOLOGICA', 'ACEPTADO',               DATE_SUB(@ahora, INTERVAL 12 DAY),               'admin@contitalent.com', 'Candidato aceptado',            'Fuiste aceptado para la posicion.'),
(7, 'EN_EVALUACION',          'RECHAZADO',              DATE_SUB(@ahora, INTERVAL 6 DAY),                'admin@contitalent.com', 'No cumple perfil requerido',    'El proceso finalizo para esta oferta.'),
(8, 'POSTULADO',              'APROBADO_TECNICO',       DATE_ADD(DATE_SUB(@ahora, INTERVAL 4 DAY), INTERVAL 1 HOUR),      'Sistema',              'Aprobado tecnico, pendiente de agendar entrevista', 'Aprobaste la evaluacion tecnica. RRHH puede programar tu entrevista.'),
(9, 'APROBADO_TECNICO',       'ENTREVISTA',             DATE_SUB(@ahora, INTERVAL 3 DAY),                'admin@contitalent.com', 'Entrevista normal agendada para demo', 'Tu entrevista normal esta programada.'),
(10,'APROBADO_TECNICO',       'ENTREVISTA',             DATE_SUB(@ahora, INTERVAL 4 DAY),                'admin@contitalent.com', 'Entrevista reprogramada por disponibilidad', 'Tu entrevista fue reprogramada.'),
(11,'ENTREVISTA',             'EVALUACION_PSICOLOGICA', DATE_SUB(@ahora, INTERVAL 6 DAY),                'admin@contitalent.com', 'Entrevista normal aprobada; psicologica creada', 'Tu evaluacion psicologica fue creada.'),
(12,'EVALUACION_PSICOLOGICA', 'ACEPTADO',               DATE_SUB(@ahora, INTERVAL 10 DAY),               'admin@contitalent.com', 'Evaluacion psicologica apta', 'Fuiste aceptada para la posicion.'),
(13,'ENTREVISTA',             'RECHAZADO',              DATE_SUB(@ahora, INTERVAL 7 DAY),                'admin@contitalent.com', 'Entrevista normal desaprobada', 'El proceso finalizo para esta oferta.');

INSERT INTO tbl_entrevista_postulante (
  id, postulante_id, tipo_entrevista, fecha_programada, hora_inicio, hora_fin, modalidad,
  lugar, enlace_virtual, entrevistador_nombre, entrevistador_cargo, estado_entrevista, resultado,
  observacion_interna, observacion_postulante, usuario_admin, creado_por_admin_id,
  actualizado_por_admin_id, actualizado_por_admin, observacion_cambio, creado_en, actualizado_en
) VALUES
(1, 4, 'ENTREVISTA_NORMAL',     DATE_ADD(@ahora, INTERVAL 2 DAY),  '10:00', '10:45', 'VIRTUAL',     NULL,                                  'https://meet.contitalent.com/entrevista-4', 'Ana Castillo',   'Lider de seleccion',          'PROGRAMADA', 'PENDIENTE', 'Entrevista tecnica con lider de area', 'Tu entrevista tecnica fue programada.',                                'admin@contitalent.com', 1, NULL, NULL,                       NULL,                                                  DATE_SUB(@ahora, INTERVAL 1 DAY),  NULL),
(2, 5, 'ENTREVISTA_NORMAL',     DATE_SUB(@ahora, INTERVAL 8 DAY),  '09:00', '09:40', 'PRESENCIAL',  'Campus principal - Oficina RRHH',     NULL,                                        'Rafael Vega',    'Coordinador academico',       'REALIZADA',  'APROBADO',  'Buen dominio del rol docente',         'Aprobaste la entrevista. Continuaras con evaluacion psicologica.',     'admin@contitalent.com', 1, 1,    'admin@contitalent.com',    'Resultado registrado luego de entrevista realizada.',  DATE_SUB(@ahora, INTERVAL 8 DAY),  DATE_SUB(@ahora, INTERVAL 8 DAY)),
(3, 6, 'ENTREVISTA_NORMAL',     DATE_SUB(@ahora, INTERVAL 13 DAY), '11:00', '11:50', 'VIRTUAL',     NULL,                                  'https://meet.contitalent.com/entrevista-6', 'Sofia Herrera',  'Gerente de area',             'REALIZADA',  'APROBADO',  'Perfil senior validado',               'Aprobaste la entrevista. Continuaras con evaluacion psicologica.',     'admin@contitalent.com', 1, 1,    'admin@contitalent.com',    'Resultado registrado por cierre de entrevista.',       DATE_SUB(@ahora, INTERVAL 13 DAY), DATE_SUB(@ahora, INTERVAL 13 DAY)),
(4, 6, 'EVALUACION_PSICOLOGICA',DATE_SUB(@ahora, INTERVAL 12 DAY), '15:00', '15:45', 'VIRTUAL',     NULL,                                  'https://meet.contitalent.com/psico-6',      'Paola Ruiz',     'Psicologa organizacional',    'REALIZADA',  'APROBADO',  'Sin observaciones restrictivas',       'Aprobaste la evaluacion psicologica.',                                  'admin@contitalent.com', 1, 1,    'admin@contitalent.com',    'Resultado psicologico registrado.',                    DATE_SUB(@ahora, INTERVAL 12 DAY), DATE_SUB(@ahora, INTERVAL 12 DAY)),
(5, 9, 'ENTREVISTA_NORMAL',     DATE_ADD(@ahora, INTERVAL 1 DAY),        '15:00', '15:30', 'PRESENCIAL',  'Campus Huancayo - Laboratorio TI',     NULL,                                        'Jorge Poma',     'Jefe de Soporte TI',          'PROGRAMADA', 'PENDIENTE', 'Demo: entrevista normal presencial programada.', 'Presentarse con DNI y portafolio de practicas.',                 'admin@contitalent.com', 1, NULL, NULL,                       NULL,                                                  DATE_SUB(@ahora, INTERVAL 2 DAY),  NULL),
(6, 10,'ENTREVISTA_NORMAL',     DATE_ADD(@ahora, INTERVAL 3 DAY),  '11:00', '11:40', 'VIRTUAL',     NULL,                                  'https://meet.contitalent.com/entrevista-nicolas', 'Mariana Solis', 'Coordinadora de Marketing',   'REPROGRAMADA','PENDIENTE','Demo: entrevista normal reprogramada.', 'Nueva fecha confirmada para tu entrevista normal.',                  'admin@contitalent.com', 1, NULL, NULL,                       NULL,                                                  DATE_SUB(@ahora, INTERVAL 3 DAY),  NULL),
(7, 11,'ENTREVISTA_NORMAL',     DATE_SUB(@ahora, INTERVAL 6 DAY),  '08:30', '09:10', 'VIRTUAL',     NULL,                                  'https://meet.contitalent.com/entrevista-gabriela','Rafael Vega', 'Coordinador academico',       'REALIZADA',  'APROBADO',  'Perfil docente validado.', 'Aprobaste la entrevista normal.',                                            'admin@contitalent.com', 1, 1,    'admin@contitalent.com',    'Resultado cargado como dato demo.',                    DATE_SUB(@ahora, INTERVAL 6 DAY),  DATE_SUB(@ahora, INTERVAL 6 DAY)),
(8, 11,'EVALUACION_PSICOLOGICA',DATE_ADD(@ahora, INTERVAL 4 DAY),  '16:00', '16:45', 'VIRTUAL',     NULL,                                  'https://meet.contitalent.com/psico-gabriela', 'Paola Ruiz',   'Psicologa organizacional',    'PROGRAMADA', 'PENDIENTE', 'Demo: evaluacion psicologica creada y pendiente.', 'Tu evaluacion psicologica esta programada.',                        'admin@contitalent.com', 1, NULL, NULL,                       NULL,                                                  DATE_SUB(@ahora, INTERVAL 2 DAY),  NULL),
(9, 12,'ENTREVISTA_NORMAL',     DATE_SUB(@ahora, INTERVAL 11 DAY), '10:00', '10:45', 'PRESENCIAL',  'Campus principal - Bienestar Universitario', NULL,                                  'Carmen Rios',    'Directora de Bienestar',      'REALIZADA',  'APROBADO',  'Experiencia alineada al cargo.', 'Aprobaste la entrevista normal.',                                      'admin@contitalent.com', 1, 1,    'admin@contitalent.com',    'Resultado cargado como dato demo.',                    DATE_SUB(@ahora, INTERVAL 11 DAY), DATE_SUB(@ahora, INTERVAL 11 DAY)),
(10,12,'EVALUACION_PSICOLOGICA',DATE_SUB(@ahora, INTERVAL 10 DAY), '14:00', '14:40', 'PRESENCIAL',  'Campus principal - Psicologia',         NULL,                                        'Paola Ruiz',     'Psicologa organizacional',    'REALIZADA',  'APROBADO',  'Sin observaciones restrictivas.', 'Aprobaste la evaluacion psicologica.',                                  'admin@contitalent.com', 1, 1,    'admin@contitalent.com',    'Resultado cargado como dato demo.',                    DATE_SUB(@ahora, INTERVAL 10 DAY), DATE_SUB(@ahora, INTERVAL 10 DAY)),
(11,13,'ENTREVISTA_NORMAL',     DATE_SUB(@ahora, INTERVAL 7 DAY),  '12:00', '12:30', 'VIRTUAL',     NULL,                                  'https://meet.contitalent.com/entrevista-mateo', 'Ana Castillo', 'Lider de seleccion',          'REALIZADA',  'DESAPROBADO','Necesita mayor experiencia docente.', 'Por ahora no continuas en el proceso.',                                'admin@contitalent.com', 1, 1,    'admin@contitalent.com',    'Resultado cargado como dato demo.',                    DATE_SUB(@ahora, INTERVAL 7 DAY),  DATE_SUB(@ahora, INTERVAL 7 DAY));

INSERT INTO tbl_evaluacion_psicologica_postulante (id, postulante_id, fecha_evaluacion, resultado, observacion, usuario_admin, creado_en) VALUES
(1, 6, DATE_SUB(@ahora, INTERVAL 12 DAY), 'APTO', 'Sin observaciones restrictivas', 'admin@contitalent.com', DATE_SUB(@ahora, INTERVAL 12 DAY)),
(2, 11,DATE_ADD(@ahora, INTERVAL 4 DAY),  'PENDIENTE', 'Demo: registro psicologico creado para mostrar la etapa pendiente.', 'admin@contitalent.com', DATE_SUB(@ahora, INTERVAL 2 DAY)),
(3, 12,DATE_SUB(@ahora, INTERVAL 10 DAY), 'APTO', 'Competencias socioemocionales acordes al puesto.', 'admin@contitalent.com', DATE_SUB(@ahora, INTERVAL 10 DAY));

-- ===== Respuestas (entidad intermedia Postulante - Pregunta) =====
INSERT INTO tbl_postulante_respuesta (postulante_id, pregunta_id, opcion_elegida) VALUES
(1, 6, 1), (1, 7, 1), (1, 8, 0),
(2, 1, 1), (2, 2, 1), (2, 3, 1), (2, 4, 1), (2, 5, 3),
(4, 6, 1), (4, 7, 1), (4, 8, 1),
(5, 9, 1), (5, 10, 1), (5, 11, 1),
(6, 9, 1), (6, 10, 1), (6, 11, 1),
(7, 12, 0), (7, 13, 0),
(8, 1, 1), (8, 2, 1), (8, 3, 1), (8, 4, 0), (8, 5, 3),
(9, 6, 1), (9, 7, 1), (9, 8, 1),
(10, 12, 0), (10, 13, 1),
(11, 9, 1), (11, 10, 1), (11, 11, 1),
(13, 1, 1), (13, 2, 0), (13, 3, 1), (13, 4, 0), (13, 5, 3);

-- =====================================================================
-- Ajuste de auto-increment (siguiente id disponible)
-- =====================================================================
ALTER TABLE tbl_area                              AUTO_INCREMENT = 9;
ALTER TABLE tbl_rol                               AUTO_INCREMENT = 3;
ALTER TABLE tbl_estado                            AUTO_INCREMENT = 8;
ALTER TABLE tbl_usuario                           AUTO_INCREMENT = 15;
ALTER TABLE tbl_oferta                            AUTO_INCREMENT = 12;
ALTER TABLE tbl_oferta_requisito                  AUTO_INCREMENT = 100;
ALTER TABLE tbl_oferta_beneficio                  AUTO_INCREMENT = 100;
ALTER TABLE tbl_oferta_habilidad                  AUTO_INCREMENT = 100;
ALTER TABLE tbl_pregunta                          AUTO_INCREMENT = 14;
ALTER TABLE tbl_postulante                        AUTO_INCREMENT = 14;
ALTER TABLE tbl_documento_postulante              AUTO_INCREMENT = 6;
ALTER TABLE tbl_historial_estado_postulante       AUTO_INCREMENT = 15;
ALTER TABLE tbl_entrevista_postulante             AUTO_INCREMENT = 12;
ALTER TABLE tbl_evaluacion_psicologica_postulante AUTO_INCREMENT = 4;
ALTER TABLE tbl_postulante_respuesta              AUTO_INCREMENT = 100;
