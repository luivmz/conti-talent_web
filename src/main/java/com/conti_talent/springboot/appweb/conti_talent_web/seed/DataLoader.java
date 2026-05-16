package com.conti_talent.springboot.appweb.conti_talent_web.seed;

import java.time.LocalDateTime;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.MetricasDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.*;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.RolCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Carga inicial de datos en memoria. Solo se ejecuta si el repositorio de
 * usuarios esta vacio (seed idempotente).
 *
 * Orden de carga:
 *   1. Roles      (catalogo)
 *   2. Estados    (catalogo)
 *   3. Usuarios   (FK a Rol)
 *   4. Areas
 *   5. Ofertas    (FK a Area)
 *   6. Preguntas  (FK a Oferta)
 *   7. Postulantes (FK a Usuario, Oferta, Estado)
 *   8. Metricas   (snapshot en memoria)
 *
 * Los IDs se generan automaticamente por JPA (auto-increment). Las relaciones
 * se establecen con setters tipados que reciben las entidades resueltas.
 */
@Component
@Order(1)
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);
        private final IRolRepository rolRepository;
    private final IEstadoRepository estadoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IAreaRepository areaRepository;
    private final IOfertaRepository ofertaRepository;
    private final IPreguntaRepository preguntaRepository;
    private final IPostulanteRepository postulanteRepository;
    private final IDocumentoPostulanteRepository documentoRepository;
    private final IHistorialEstadoPostulanteRepository historialRepository;
    private final IEntrevistaPostulanteRepository entrevistaRepository;
    private final IEvaluacionPsicologicaPostulanteRepository psicologicaRepository;
    private final IMetricasRepository metricasRepository;

    public DataLoader(IRolRepository rolRepository,
                      IEstadoRepository estadoRepository,
                      IUsuarioRepository usuarioRepository,
                      IAreaRepository areaRepository,
                      IOfertaRepository ofertaRepository,
                      IPreguntaRepository preguntaRepository,
                      IPostulanteRepository postulanteRepository,
                      IDocumentoPostulanteRepository documentoRepository,
                      IHistorialEstadoPostulanteRepository historialRepository,
                      IEntrevistaPostulanteRepository entrevistaRepository,
                      IEvaluacionPsicologicaPostulanteRepository psicologicaRepository,
                      IMetricasRepository metricasRepository) {
        this.rolRepository = rolRepository;
        this.estadoRepository = estadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.areaRepository = areaRepository;
        this.ofertaRepository = ofertaRepository;
        this.preguntaRepository = preguntaRepository;
        this.postulanteRepository = postulanteRepository;
        this.documentoRepository = documentoRepository;
        this.historialRepository = historialRepository;
        this.entrevistaRepository = entrevistaRepository;
        this.psicologicaRepository = psicologicaRepository;
        this.metricasRepository = metricasRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Las metricas (snapshot en memoria) se cargan siempre.
        cargarMetricas();

        if (usuarioRepository.count() > 0) {
            log.info("[DataLoader] Datos ya cargados en memoria; omito seed de entidades.");
            return;
        }
        log.info("[DataLoader] Cargando seed inicial de Conti Talent en memoria...");

        LocalDateTime ahora = LocalDateTime.now();

        Map<String, Rol> rolesPorCodigo = cargarRoles(ahora);
        Map<String, Estado> estadosPorCodigo = cargarEstados(ahora);

        List<Usuario> usuarios = cargarUsuarios(ahora, rolesPorCodigo);
        Map<String, Area> areasPorNombre = cargarAreas();
        Map<String, Oferta> ofertasPorTitulo = cargarOfertas(ahora, areasPorNombre);
        Map<String, Pregunta> preguntasPorClave = cargarPreguntas(ofertasPorTitulo);
        cargarPostulantes(ahora, usuarios, ofertasPorTitulo, estadosPorCodigo, preguntasPorClave);

        log.info("[DataLoader] Seed completado: {} roles, {} estados, {} usuarios, {} areas, {} ofertas, {} preguntas, {} postulantes",
                rolRepository.count(),
                estadoRepository.count(),
                usuarioRepository.count(),
                areaRepository.count(),
                ofertaRepository.count(),
                preguntaRepository.count(),
                postulanteRepository.count());
    }

    /* =================== ROLES =================== */
    private Map<String, Rol> cargarRoles(LocalDateTime ahora) {
        Map<String, Rol> mapa = new HashMap<>();
        mapa.put(RolCodigo.ADMIN.name(), rolRepository.save(new Rol(
                RolCodigo.ADMIN.name(), "Administrador",
                "Acceso total al sistema: gestion de usuarios, areas, ofertas, postulantes y metricas.",
                true, ahora.minusDays(60))));
        mapa.put(RolCodigo.POSTULANTE.name(), rolRepository.save(new Rol(
                RolCodigo.POSTULANTE.name(), "Postulante",
                "Usuario externo que postula a las ofertas publicadas por la institucion.",
                true, ahora.minusDays(60))));
        return mapa;
    }

    /* =================== ESTADOS =================== */
    private Map<String, Estado> cargarEstados(LocalDateTime ahora) {
        Map<String, Estado> mapa = new LinkedHashMap<>();
        mapa.put(EstadoCodigo.POSTULADO.name(), estadoRepository.save(new Estado(
                EstadoCodigo.POSTULADO.name(), "Postulado",
                "Postulacion recibida y pendiente de evaluacion tecnica.",
                1, false, true, ahora.minusDays(60))));
        mapa.put(EstadoCodigo.EN_EVALUACION.name(), estadoRepository.save(new Estado(
                EstadoCodigo.EN_EVALUACION.name(), "En evaluacion",
                "El postulante rindio la prueba pero no alcanzo el umbral de aprobacion.",
                2, false, true, ahora.minusDays(60))));
        mapa.put(EstadoCodigo.APROBADO_TECNICO.name(), estadoRepository.save(new Estado(
                EstadoCodigo.APROBADO_TECNICO.name(), "Aprobado tecnico",
                "Aprobo la evaluacion tecnica y avanza a entrevista.",
                3, false, true, ahora.minusDays(60))));
        mapa.put(EstadoCodigo.ENTREVISTA.name(), estadoRepository.save(new Estado(
                EstadoCodigo.ENTREVISTA.name(), "Entrevista",
                "Citado o citada para entrevista personal.",
                4, false, true, ahora.minusDays(60))));
        mapa.put(EstadoCodigo.EVALUACION_PSICOLOGICA.name(), estadoRepository.save(new Estado(
                EstadoCodigo.EVALUACION_PSICOLOGICA.name(), "Evaluacion psicologica",
                "Aprobada la entrevista, paso a evaluacion psicologica.",
                5, false, true, ahora.minusDays(60))));
        mapa.put(EstadoCodigo.ACEPTADO.name(), estadoRepository.save(new Estado(
                EstadoCodigo.ACEPTADO.name(), "Aceptado",
                "Proceso completo. Candidato aceptado para la posicion.",
                6, true, true, ahora.minusDays(60))));
        mapa.put(EstadoCodigo.RECHAZADO.name(), estadoRepository.save(new Estado(
                EstadoCodigo.RECHAZADO.name(), "Rechazado",
                "Candidato descartado en alguna etapa del proceso.",
                7, true, true, ahora.minusDays(60))));
        return mapa;
    }

    /* =================== USUARIOS =================== */
    private List<Usuario> cargarUsuarios(LocalDateTime ahora, Map<String, Rol> rolesPorCodigo) {
        Rol rolAdmin       = rolesPorCodigo.get(RolCodigo.ADMIN.name());
        Rol rolPostulante  = rolesPorCodigo.get(RolCodigo.POSTULANTE.name());

        List<Usuario> creados = new ArrayList<>();
        creados.add(usuarioRepository.save(new Usuario("Administrador", "Continental", "admin@contitalent.com", "admin123", rolAdmin,      true, ahora.minusDays(30))));
        creados.add(usuarioRepository.save(new Usuario("Lucia",         "Ramos",       "lucia@example.com",     "lucia123",  rolPostulante, true, ahora.minusDays(8))));
        creados.add(usuarioRepository.save(new Usuario("Carlos",        "Mendoza",     "carlos@example.com",    "carlos123", rolPostulante, true, ahora.minusDays(5))));
        creados.add(usuarioRepository.save(new Usuario("Maria",         "Torres",      "maria@example.com",     "maria123",  rolPostulante, true, ahora.minusDays(2))));
        creados.add(usuarioRepository.save(new Usuario("Pedro",         "Salinas",     "pedro@example.com",     "pedro123",  rolPostulante, true, ahora.minusDays(7))));
        creados.add(usuarioRepository.save(new Usuario("Andrea",        "Leon",        "andrea@example.com",    "andrea123", rolPostulante, true, ahora.minusDays(10))));
        creados.add(usuarioRepository.save(new Usuario("Diego",         "Alvarez",     "diego@example.com",     "diego123",  rolPostulante, true, ahora.minusDays(15))));
        creados.add(usuarioRepository.save(new Usuario("Fiorella",      "Rojas",       "fiorella@example.com",  "fiora123",  rolPostulante, true, ahora.minusDays(8))));
        creados.add(usuarioRepository.save(new Usuario("Renato",        "Quispe",      "renato@example.com",    "renato123", rolPostulante, true, ahora.minusDays(4))));
        creados.add(usuarioRepository.save(new Usuario("Valeria",       "Campos",      "valeria@example.com",   "vale123",   rolPostulante, true, ahora.minusDays(6))));
        creados.add(usuarioRepository.save(new Usuario("Nicolas",       "Vargas",      "nicolas@example.com",   "nico123",   rolPostulante, true, ahora.minusDays(7))));
        creados.add(usuarioRepository.save(new Usuario("Gabriela",      "Paredes",     "gabriela@example.com",  "gaby123",   rolPostulante, true, ahora.minusDays(11))));
        creados.add(usuarioRepository.save(new Usuario("Sofia",         "Huaman",      "sofia@example.com",     "sofia123",  rolPostulante, true, ahora.minusDays(13))));
        creados.add(usuarioRepository.save(new Usuario("Mateo",         "Caceres",     "mateo@example.com",     "mateo123",  rolPostulante, true, ahora.minusDays(9))));
        return creados;
    }

    /* =================== AREAS =================== */
    private Map<String, Area> cargarAreas() {
        Map<String, Area> mapa = new LinkedHashMap<>();
        mapa.put("Ingenieria",                 areaRepository.save(new Area("Ingenieria",                 "Sistemas, civil, industrial, mecatronica y minas.",                                       "engineering", "#6366f1")));
        mapa.put("Ciencias de la Empresa",     areaRepository.save(new Area("Ciencias de la Empresa",     "Administracion, contabilidad, marketing y negocios.",                                     "chart",       "#06b6d4")));
        mapa.put("Derecho",                    areaRepository.save(new Area("Derecho",                    "Derecho corporativo, civil, penal y constitucional.",                                     "scale",       "#8b5cf6")));
        mapa.put("Humanidades",                areaRepository.save(new Area("Humanidades",                "Comunicacion, psicologia y educacion.",                                                   "books",       "#ec4899")));
        mapa.put("Ciencias de la Salud",       areaRepository.save(new Area("Ciencias de la Salud",       "Enfermeria, psicologia clinica y nutricion.",                                             "stethoscope", "#10b981")));
        mapa.put("Investigacion y Desarrollo", areaRepository.save(new Area("Investigacion y Desarrollo", "Centros de investigacion e innovacion universitaria.",                                    "microscope",  "#f59e0b")));
        mapa.put("Tecnologia y Sistemas",      areaRepository.save(new Area("Tecnologia y Sistemas",      "Equipo de TI institucional: infraestructura, soporte y software interno.",                "computer",    "#0ea5e9")));
        mapa.put("Bienestar Universitario",    areaRepository.save(new Area("Bienestar Universitario",    "Soporte estudiantil, deportes, cultura y atencion psicopedagogica.",                      "handshake",   "#f43f5e")));
        return mapa;
    }

    /* =================== OFERTAS =================== */
    private Map<String, Oferta> cargarOfertas(LocalDateTime ahora, Map<String, Area> areas) {
        Map<String, Oferta> mapa = new LinkedHashMap<>();

        mapa.put("Profesor de Programacion I", ofertaRepository.save(new Oferta(
                "Profesor de Programacion I", "Trabajo", areas.get("Ingenieria"),
                "Presencial", "Huancayo", 2, true,
                "Docente para el curso de Programacion I (Java) en la Escuela de Ingenieria de Sistemas.",
                List.of("Ingeniero de Sistemas o afin", "2+ anios enseniando o desarrollando software", "Manejo de Java y bases de datos", "Experiencia en metodologias agiles"),
                List.of("Carga horaria flexible", "Capacitacion pedagogica", "Convenios interinstitucionales"),
                ahora.minusDays(3))));
        mapa.get("Profesor de Programacion I").setHorario("Lunes, miercoles y viernes 08:00 - 12:00");

        mapa.put("Practica Sistemas", ofertaRepository.save(new Oferta(
                "Practica Pre-Profesional Sistemas", "Practica", areas.get("Ingenieria"),
                "Hibrido", "Huancayo", 4, true,
                "Apoyo al area de Sistemas y TI: soporte, desarrollo de pequenias mejoras y gestion de tickets.",
                List.of("Estudiante de Ingenieria de Sistemas", "Conocimientos de HTML, CSS y JS", "Buena comunicacion"),
                List.of("Subvencion economica", "Mentoria", "Certificacion de practicas"),
                ahora.minusDays(2))));
        mapa.get("Practica Sistemas").setHorario("Lunes a viernes 09:00 - 14:00");

        mapa.put("Profesor Marketing Digital", ofertaRepository.save(new Oferta(
                "Profesor de Marketing Digital", "Trabajo", areas.get("Ciencias de la Empresa"),
                "Presencial", "Huancayo", 1, true,
                "Docente para Marketing Digital y Estrategia Comercial.",
                List.of("Profesional en Marketing o Negocios", "Experiencia en performance digital", "Maestria (deseable)"),
                List.of("Plan de carrera docente", "Investigacion remunerada"),
                ahora.minusDays(4))));
        mapa.get("Profesor Marketing Digital").setHorario("Martes y jueves 18:00 - 21:00");

        mapa.put("Practica Marketing", ofertaRepository.save(new Oferta(
                "Practica Pre-Profesional Marketing", "Practica", areas.get("Ciencias de la Empresa"),
                "Presencial", "Huancayo", 3, true,
                "Apoyo en campanias digitales, contenidos y analitica para la marca Continental.",
                List.of("Estudiante de Marketing/Administracion", "Manejo de redes sociales", "Curiosidad y proactividad"),
                List.of("Subvencion economica", "Aprendizaje real", "Certificacion"),
                ahora.minusDays(1))));
        mapa.get("Practica Marketing").setHorario("Lunes a viernes 08:30 - 13:30");

        mapa.put("Profesor Derecho Constitucional", ofertaRepository.save(new Oferta(
                "Profesor de Derecho Constitucional", "Trabajo", areas.get("Derecho"),
                "Presencial", "Huancayo", 1, false,
                "Docente para el curso de Derecho Constitucional.",
                List.of("Abogado titulado", "Maestria o doctorado en Derecho", "Publicaciones academicas"),
                List.of("Bonos por publicacion", "Apoyo a investigacion"),
                ahora.minusDays(6))));
        mapa.get("Profesor Derecho Constitucional").setHorario("Sabados 08:00 - 13:00");

        mapa.put("Practica Derecho Civil", ofertaRepository.save(new Oferta(
                "Practica Profesional Derecho Civil", "Practica", areas.get("Derecho"),
                "Hibrido", "Huancayo", 2, false,
                "Apoyo al consultorio juridico del area de Derecho.",
                List.of("Egresado o bachiller en Derecho", "Buen manejo de redaccion"),
                List.of("Subvencion", "Acompaniamiento profesional"),
                ahora.minusDays(5))));
        mapa.get("Practica Derecho Civil").setHorario("Lunes a viernes 09:00 - 15:00");

        mapa.put("Profesor Comunicacion", ofertaRepository.save(new Oferta(
                "Profesor de Comunicacion Oral y Escrita", "Trabajo", areas.get("Humanidades"),
                "Presencial", "Huancayo", 2, false,
                "Curso transversal en pregrado.",
                List.of("Licenciado en Comunicacion o Educacion", "Experiencia minima 2 anios"),
                List.of("Plan docente", "Becas para postgrado"),
                ahora.minusDays(8))));
        mapa.get("Profesor Comunicacion").setHorario("Lunes y miercoles 16:00 - 20:00");

        mapa.put("Asistente Salud Publica", ofertaRepository.save(new Oferta(
                "Asistente de Investigacion Salud Publica", "Practica", areas.get("Ciencias de la Salud"),
                "Hibrido", "Huancayo", 2, false,
                "Apoyo a investigacion de campo en proyectos de salud publica.",
                List.of("Estudiante de Ciencias de la Salud", "Estadistica basica"),
                List.of("Subvencion", "Co-autoria en publicaciones"),
                ahora.minusDays(9))));
        mapa.get("Asistente Salud Publica").setHorario("Lunes a viernes 08:00 - 13:00");

        mapa.put("Coordinador Investigacion", ofertaRepository.save(new Oferta(
                "Coordinador de Investigacion", "Trabajo", areas.get("Investigacion y Desarrollo"),
                "Presencial", "Huancayo", 1, false,
                "Lidera proyectos del Centro de Investigacion.",
                List.of("Magister o doctor", "Publicaciones indexadas", "Liderazgo de equipos"),
                List.of("Sueldo competitivo", "Asignacion de proyectos"),
                ahora.minusDays(11))));
        mapa.get("Coordinador Investigacion").setHorario("Lunes a viernes 08:00 - 17:00");

        mapa.put("Practica Soporte TI", ofertaRepository.save(new Oferta(
                "Practica Soporte de TI Universitario", "Practica", areas.get("Tecnologia y Sistemas"),
                "Presencial", "Huancayo", 3, true,
                "Apoyo al equipo institucional de Tecnologia y Sistemas.",
                List.of("Estudios tecnicos o universitarios en TI", "Conocimientos de redes y hardware", "Buena atencion al usuario"),
                List.of("Subvencion economica", "Certificacion de practicas", "Plan de mentoria"),
                ahora.minusDays(1))));
        mapa.get("Practica Soporte TI").setHorario("Turno maniana 08:00 - 13:00");

        mapa.put("Coordinador Bienestar", ofertaRepository.save(new Oferta(
                "Coordinador de Bienestar Estudiantil", "Trabajo", areas.get("Bienestar Universitario"),
                "Presencial", "Huancayo", 1, false,
                "Lidera el equipo de Bienestar Universitario.",
                List.of("Profesional en Psicologia, Educacion o afin", "3+ anios en gestion estudiantil", "Habilidades de liderazgo"),
                List.of("Plan de carrera", "Capacitacion continua", "Contrato estable"),
                ahora.minusDays(4))));
        mapa.get("Coordinador Bienestar").setHorario("Lunes a viernes 08:30 - 17:30");

        mapa.get("Profesor de Programacion I").setHabilidadesRequeridas(List.of("Java", "bases de datos", "metodologias agiles", "didactica"));
        mapa.get("Practica Sistemas").setHabilidadesRequeridas(List.of("HTML", "CSS", "JavaScript", "soporte tecnico"));
        mapa.get("Profesor Marketing Digital").setHabilidadesRequeridas(List.of("SEO", "Ads", "analitica", "didactica"));
        mapa.get("Practica Marketing").setHabilidadesRequeridas(List.of("redes sociales", "contenido", "Figma"));
        mapa.get("Profesor Derecho Constitucional").setHabilidadesRequeridas(List.of("Derecho", "publicaciones academicas", "docencia"));
        mapa.get("Practica Derecho Civil").setHabilidadesRequeridas(List.of("redaccion", "Derecho civil"));
        mapa.get("Profesor Comunicacion").setHabilidadesRequeridas(List.of("comunicacion", "educacion", "docencia"));
        mapa.get("Asistente Salud Publica").setHabilidadesRequeridas(List.of("estadistica", "investigacion"));
        mapa.get("Coordinador Investigacion").setHabilidadesRequeridas(List.of("publicaciones indexadas", "liderazgo", "investigacion"));
        mapa.get("Practica Soporte TI").setHabilidadesRequeridas(List.of("redes", "hardware", "atencion al usuario"));
        mapa.get("Coordinador Bienestar").setHabilidadesRequeridas(List.of("liderazgo", "gestion estudiantil", "psicologia"));
        mapa.values().forEach(ofertaRepository::save);

        return mapa;
    }

    /* =================== PREGUNTAS =================== */
    private Map<String, Pregunta> cargarPreguntas(Map<String, Oferta> ofertas) {
        Map<String, Pregunta> mapa = new LinkedHashMap<>();
        Oferta progI = ofertas.get("Profesor de Programacion I");
        Oferta sistemas = ofertas.get("Practica Sistemas");
        Oferta marketing = ofertas.get("Profesor Marketing Digital");
        Oferta marketingPrac = ofertas.get("Practica Marketing");

        mapa.put("q1", preguntaRepository.save(new Pregunta(progI,
                "Que patron de diseno desacopla logica de presentacion?",
                List.of("Singleton", "MVC", "Observer", "Factory"), 1)));
        mapa.put("q2", preguntaRepository.save(new Pregunta(progI,
                "Cual es la principal ventaja de la inyeccion de dependencias?",
                List.of("Mejor rendimiento en runtime", "Tests mas sencillos y bajo acoplamiento", "Reduce el bundle", "Reemplaza interfaces"), 1)));
        mapa.put("q3", preguntaRepository.save(new Pregunta(progI,
                "Que hace una sentencia INNER JOIN en SQL?",
                List.of("Retorna todas las filas de ambas tablas", "Retorna filas con coincidencia en ambas tablas", "Retorna filas unicas", "Combina filas evitando duplicados"), 1)));
        mapa.put("q4", preguntaRepository.save(new Pregunta(progI,
                "Que metodologia activa promueve aprender por proyectos?",
                List.of("Conferencia magistral", "Aprendizaje basado en proyectos", "Examen oral", "Tutoriales pre-grabados"), 1)));
        mapa.put("q5", preguntaRepository.save(new Pregunta(progI,
                "Cual NO es un metodo HTTP idempotente?",
                List.of("GET", "PUT", "DELETE", "POST"), 3)));

        mapa.put("q6", preguntaRepository.save(new Pregunta(sistemas,
                "Que etiqueta HTML5 representa contenido principal?",
                List.of("<section>", "<main>", "<article>", "<div>"), 1)));
        mapa.put("q7", preguntaRepository.save(new Pregunta(sistemas,
                "Que propiedad CSS alinea elementos en eje horizontal con flex?",
                List.of("align-items", "justify-content", "flex-wrap", "grid-template"), 1)));
        mapa.put("q8", preguntaRepository.save(new Pregunta(sistemas,
                "Buena practica para evitar XSS?",
                List.of("Concatenar HTML con datos del usuario", "Escapar la salida y validar entradas", "Usar localStorage", "Cifrar las URLs"), 1)));

        mapa.put("q9",  preguntaRepository.save(new Pregunta(marketing,
                "Que metrica mide el costo de adquirir un cliente?",
                List.of("CTR", "CAC", "ROI", "CPM"), 1)));
        mapa.put("q10", preguntaRepository.save(new Pregunta(marketing,
                "Cual es el proposito principal del SEO tecnico?",
                List.of("Crear contenido viral", "Optimizar la indexacion y rendimiento", "Comprar enlaces", "Disenar logos"), 1)));
        mapa.put("q11", preguntaRepository.save(new Pregunta(marketing,
                "En un funnel, que etapa va antes de la decision?",
                List.of("Awareness", "Consideration", "Retention", "Loyalty"), 1)));

        mapa.put("q12", preguntaRepository.save(new Pregunta(marketingPrac,
                "Herramienta estandar para piezas de redes sociales?",
                List.of("Figma", "Photoshop", "Excel", "Notepad"), 0)));
        mapa.put("q13", preguntaRepository.save(new Pregunta(marketingPrac,
                "Red social con mayor alcance B2B en LATAM?",
                List.of("Pinterest", "LinkedIn", "TikTok", "Snapchat"), 1)));

        return mapa;
    }

    /* =================== POSTULANTES =================== */
    private void cargarPostulantes(LocalDateTime ahora, List<Usuario> usuarios,
                                   Map<String, Oferta> ofertas, Map<String, Estado> estados,
                                   Map<String, Pregunta> preguntas) {
        // Resolucion de los usuarios por su posicion en el seed (admin=0, postulantes 1..7).
        Usuario lucia    = usuarios.get(1);
        Usuario carlos   = usuarios.get(2);
        Usuario maria    = usuarios.get(3);
        Usuario pedro    = usuarios.get(4);
        Usuario andrea   = usuarios.get(5);
        Usuario diego    = usuarios.get(6);
        Usuario fiorella = usuarios.get(7);
        Usuario renato   = usuarios.get(8);
        Usuario valeria  = usuarios.get(9);
        Usuario nicolas  = usuarios.get(10);
        Usuario gabriela = usuarios.get(11);
        Usuario sofia    = usuarios.get(12);
        Usuario mateo    = usuarios.get(13);

        Postulante postulanteLucia = postulanteRepository.save(crearPostulante(lucia, ofertas.get("Practica Sistemas"),
                estados.get(EstadoCodigo.EN_EVALUACION.name()),
                "Lucia Ramos", "lucia@example.com", "+51 987 654 321",
                "3 anios apoyando areas de TI en universidades", "JavaScript, soporte tecnico, atencion al usuario",
                "lucia_cv.pdf", 67, ahora.minusDays(4),
                Map.of(preguntas.get("q6").getId(), 1, preguntas.get("q7").getId(), 1, preguntas.get("q8").getId(), 0)));

        Postulante postulanteCarlos = postulanteRepository.save(crearPostulante(carlos, ofertas.get("Profesor de Programacion I"),
                estados.get(EstadoCodigo.APROBADO_TECNICO.name()),
                "Carlos Mendoza", "carlos@example.com", "+51 911 222 333",
                "5 anios en arquitectura de software y docencia", "Java, Spring, Docker, didactica universitaria",
                "carlos_cv.pdf", 100, ahora.minusDays(3),
                Map.of(preguntas.get("q1").getId(), 1, preguntas.get("q2").getId(), 1,
                       preguntas.get("q3").getId(), 1, preguntas.get("q4").getId(), 1, preguntas.get("q5").getId(), 3)));

        postulanteRepository.save(crearPostulante(maria, ofertas.get("Practica Marketing"),
                estados.get(EstadoCodigo.POSTULADO.name()),
                "Maria Torres", "maria@example.com", "+51 933 555 777",
                "Estudiante de Marketing en ultimo ciclo", "Community management, creacion de contenido",
                "maria_cv.pdf", 0, ahora.minusDays(1), Map.of()));

        Postulante postulantePedro = postulanteRepository.save(crearPostulante(pedro, ofertas.get("Practica Sistemas"),
                estados.get(EstadoCodigo.ENTREVISTA.name()),
                "Pedro Salinas", "pedro@example.com", "+51 922 444 666",
                "Estudiante de Sistemas con practicas previas", "JavaScript, HTML, CSS, soporte",
                "pedro_cv.pdf", 100, ahora.minusDays(6),
                Map.of(preguntas.get("q6").getId(), 1, preguntas.get("q7").getId(), 1, preguntas.get("q8").getId(), 1)));

        Postulante postulanteAndrea = postulanteRepository.save(crearPostulante(andrea, ofertas.get("Profesor Marketing Digital"),
                estados.get(EstadoCodigo.EVALUACION_PSICOLOGICA.name()),
                "Andrea Leon", "andrea@example.com", "+51 944 888 111",
                "6 anios en marketing B2B y docencia universitaria", "Estrategia digital, didactica, public speaking",
                "andrea_cv.pdf", 100, ahora.minusDays(9),
                Map.of(preguntas.get("q9").getId(), 1, preguntas.get("q10").getId(), 1, preguntas.get("q11").getId(), 1)));

        postulanteRepository.save(crearPostulante(diego, ofertas.get("Profesor Marketing Digital"),
                estados.get(EstadoCodigo.ACEPTADO.name()),
                "Diego Alvarez", "diego@example.com", "+51 955 777 222",
                "8 anios liderando agencias de marketing digital", "Ads, SEO, analitica, formacion de equipos",
                "diego_cv.pdf", 100, ahora.minusDays(14),
                Map.of(preguntas.get("q9").getId(), 1, preguntas.get("q10").getId(), 1, preguntas.get("q11").getId(), 1)));

        postulanteRepository.save(crearPostulante(fiorella, ofertas.get("Practica Marketing"),
                estados.get(EstadoCodigo.RECHAZADO.name()),
                "Fiorella Rojas", "fiorella@example.com", "+51 966 333 444",
                "1 anio en diseno grafico", "Figma, Illustrator",
                "fiorella_cv.pdf", 50, ahora.minusDays(7),
                Map.of(preguntas.get("q12").getId(), 0, preguntas.get("q13").getId(), 0)));

        Postulante postulanteRenato = postulanteRepository.save(crearPostulante(renato, ofertas.get("Profesor de Programacion I"),
                estados.get(EstadoCodigo.APROBADO_TECNICO.name()),
                "Renato Quispe", "renato@example.com", "+51 900 111 222",
                "4 anios desarrollando backend Java y apoyando laboratorios", "Java, Spring Boot, SQL, tutoria academica",
                "renato_cv.pdf", 80, ahora.minusDays(4),
                Map.of(preguntas.get("q1").getId(), 1, preguntas.get("q2").getId(), 1,
                       preguntas.get("q3").getId(), 1, preguntas.get("q4").getId(), 0, preguntas.get("q5").getId(), 3)));

        Postulante postulanteValeria = postulanteRepository.save(crearPostulante(valeria, ofertas.get("Practica Sistemas"),
                estados.get(EstadoCodigo.ENTREVISTA.name()),
                "Valeria Campos", "valeria@example.com", "+51 900 333 444",
                "2 anios en mesa de ayuda universitaria", "HTML, CSS, soporte tecnico, comunicacion",
                "valeria_cv.pdf", 100, ahora.minusDays(6),
                Map.of(preguntas.get("q6").getId(), 1, preguntas.get("q7").getId(), 1, preguntas.get("q8").getId(), 1)));

        Postulante postulanteNicolas = postulanteRepository.save(crearPostulante(nicolas, ofertas.get("Practica Marketing"),
                estados.get(EstadoCodigo.ENTREVISTA.name()),
                "Nicolas Vargas", "nicolas@example.com", "+51 900 555 666",
                "2 anios creando contenido y reportes de campanias", "Figma, contenido, LinkedIn, analitica basica",
                "nicolas_cv.pdf", 100, ahora.minusDays(7),
                Map.of(preguntas.get("q12").getId(), 0, preguntas.get("q13").getId(), 1)));

        Postulante postulanteGabriela = postulanteRepository.save(crearPostulante(gabriela, ofertas.get("Profesor Marketing Digital"),
                estados.get(EstadoCodigo.EVALUACION_PSICOLOGICA.name()),
                "Gabriela Paredes", "gabriela@example.com", "+51 900 777 888",
                "7 anios en estrategia digital y clases de especializacion", "SEO, Ads, analitica, didactica",
                "gabriela_cv.pdf", 100, ahora.minusDays(11),
                Map.of(preguntas.get("q9").getId(), 1, preguntas.get("q10").getId(), 1, preguntas.get("q11").getId(), 1)));

        Postulante postulanteSofia = postulanteRepository.save(crearPostulante(sofia, ofertas.get("Coordinador Bienestar"),
                estados.get(EstadoCodigo.ACEPTADO.name()),
                "Sofia Huaman", "sofia@example.com", "+51 900 999 111",
                "6 anios liderando programas de bienestar estudiantil", "psicologia, liderazgo, gestion estudiantil",
                "sofia_cv.pdf", 0, ahora.minusDays(13), Map.of()));

        Postulante postulanteMateo = postulanteRepository.save(crearPostulante(mateo, ofertas.get("Profesor de Programacion I"),
                estados.get(EstadoCodigo.RECHAZADO.name()),
                "Mateo Caceres", "mateo@example.com", "+51 901 222 333",
                "3 anios como desarrollador junior", "Java basico, SQL, Git",
                "mateo_cv.pdf", 60, ahora.minusDays(9),
                Map.of(preguntas.get("q1").getId(), 1, preguntas.get("q2").getId(), 0,
                       preguntas.get("q3").getId(), 1, preguntas.get("q4").getId(), 0, preguntas.get("q5").getId(), 3)));

        cargarDocumentosSeed(ahora, postulanteLucia, postulanteCarlos, postulantePedro, postulanteAndrea);
        cargarFlujoDemo(ahora, postulanteLucia, postulanteCarlos, postulantePedro, postulanteAndrea,
                postulanteRenato, postulanteValeria, postulanteNicolas, postulanteGabriela, postulanteSofia, postulanteMateo);
    }

    private void cargarFlujoDemo(LocalDateTime ahora, Postulante lucia, Postulante carlos,
                                 Postulante pedro, Postulante andrea, Postulante renato, Postulante valeria,
                                 Postulante nicolas, Postulante gabriela, Postulante sofia, Postulante mateo) {
        crearHistorial(lucia, null, EstadoCodigo.POSTULADO.name(), lucia.getFechaPostulacion(),
                "Sistema", "Postulacion registrada", "Tu postulacion fue recibida correctamente.");
        crearHistorial(lucia, EstadoCodigo.POSTULADO.name(), EstadoCodigo.EN_EVALUACION.name(), lucia.getFechaEvaluacion(),
                "Sistema", "Evaluacion tecnica registrada con puntaje bajo el umbral", "Tu evaluacion tecnica fue registrada.");

        crearHistorial(carlos, null, EstadoCodigo.POSTULADO.name(), carlos.getFechaPostulacion(),
                "Sistema", "Postulacion registrada", "Tu postulacion fue recibida correctamente.");
        crearHistorial(carlos, EstadoCodigo.POSTULADO.name(), EstadoCodigo.APROBADO_TECNICO.name(), carlos.getFechaEvaluacion(),
                "Sistema", "Evaluacion tecnica aprobada", "Aprobaste la evaluacion tecnica. Ya puedes ser citado a entrevista.");

        crearHistorial(pedro, EstadoCodigo.APROBADO_TECNICO.name(), EstadoCodigo.ENTREVISTA.name(), ahora.minusDays(5),
                "admin@contitalent.com", "Entrevista normal programada", "Tu entrevista normal fue programada.");
        crearEntrevista(pedro, "ENTREVISTA_NORMAL", ahora.plusDays(2),
                "10:00", "10:45", "VIRTUAL", null, "https://meet.contitalent.com/entrevista-pedro",
                "Ana Castillo", "Lider de seleccion", "PROGRAMADA", "PENDIENTE",
                "Demo: postulante en estado ENTREVISTA con entrevista normal pendiente.",
                "Tu entrevista normal fue programada para validar experiencia y disponibilidad.", ahora.minusDays(1));

        crearHistorial(andrea, EstadoCodigo.ENTREVISTA.name(), EstadoCodigo.EVALUACION_PSICOLOGICA.name(), ahora.minusDays(8),
                "admin@contitalent.com", "Entrevista aprobada; pasa a psicologica", "Continuas a evaluacion psicologica.");
        crearEntrevista(andrea, "ENTREVISTA_NORMAL", ahora.minusDays(8),
                "09:00", "09:40", "PRESENCIAL", "Campus principal - Oficina RRHH", null,
                "Rafael Vega", "Coordinador academico", "REALIZADA", "APROBADO",
                "Buen dominio del rol docente.", "Aprobaste la entrevista. Continuaras con evaluacion psicologica.",
                ahora.minusDays(8));

        crearHistorial(renato, EstadoCodigo.POSTULADO.name(), EstadoCodigo.APROBADO_TECNICO.name(), renato.getFechaEvaluacion(),
                "Sistema", "Aprobado tecnico, pendiente de agendar entrevista", "Aprobaste la evaluacion tecnica. RRHH puede programar tu entrevista.");

        crearHistorial(valeria, EstadoCodigo.APROBADO_TECNICO.name(), EstadoCodigo.ENTREVISTA.name(), ahora.minusDays(3),
                "admin@contitalent.com", "Entrevista normal agendada para demo", "Tu entrevista normal esta programada.");
        crearEntrevista(valeria, "ENTREVISTA_NORMAL", ahora.plusDays(1),
                "15:00", "15:30", "PRESENCIAL", "Campus Huancayo - Laboratorio TI", null,
                "Jorge Poma", "Jefe de Soporte TI", "PROGRAMADA", "PENDIENTE",
                "Demo: entrevista normal presencial programada.", "Presentarse con DNI y portafolio de practicas.",
                ahora.minusDays(2));

        crearHistorial(nicolas, EstadoCodigo.APROBADO_TECNICO.name(), EstadoCodigo.ENTREVISTA.name(), ahora.minusDays(4),
                "admin@contitalent.com", "Entrevista reprogramada por disponibilidad", "Tu entrevista fue reprogramada.");
        crearEntrevista(nicolas, "ENTREVISTA_NORMAL", ahora.plusDays(3),
                "11:00", "11:40", "VIRTUAL", null, "https://meet.contitalent.com/entrevista-nicolas",
                "Mariana Solis", "Coordinadora de Marketing", "REPROGRAMADA", "PENDIENTE",
                "Demo: entrevista normal reprogramada.", "Nueva fecha confirmada para tu entrevista normal.",
                ahora.minusDays(3));

        crearHistorial(gabriela, EstadoCodigo.ENTREVISTA.name(), EstadoCodigo.EVALUACION_PSICOLOGICA.name(), ahora.minusDays(6),
                "admin@contitalent.com", "Entrevista normal aprobada; psicologica creada", "Tu evaluacion psicologica fue creada.");
        crearEntrevista(gabriela, "ENTREVISTA_NORMAL", ahora.minusDays(6),
                "08:30", "09:10", "VIRTUAL", null, "https://meet.contitalent.com/entrevista-gabriela",
                "Rafael Vega", "Coordinador academico", "REALIZADA", "APROBADO",
                "Perfil docente validado.", "Aprobaste la entrevista normal.",
                ahora.minusDays(6));
        crearEntrevista(gabriela, "EVALUACION_PSICOLOGICA", ahora.plusDays(4),
                "16:00", "16:45", "VIRTUAL", null, "https://meet.contitalent.com/psico-gabriela",
                "Paola Ruiz", "Psicologa organizacional", "PROGRAMADA", "PENDIENTE",
                "Demo: evaluacion psicologica creada y pendiente.", "Tu evaluacion psicologica esta programada.",
                ahora.minusDays(2));
        crearEvaluacionPsicologica(gabriela, ahora.plusDays(4), "PENDIENTE",
                "Demo: registro psicologico creado para mostrar la etapa pendiente.", ahora.minusDays(2));

        crearHistorial(sofia, EstadoCodigo.EVALUACION_PSICOLOGICA.name(), EstadoCodigo.ACEPTADO.name(), ahora.minusDays(10),
                "admin@contitalent.com", "Evaluacion psicologica apta", "Fuiste aceptada para la posicion.");
        crearEntrevista(sofia, "ENTREVISTA_NORMAL", ahora.minusDays(11),
                "10:00", "10:45", "PRESENCIAL", "Campus principal - Bienestar Universitario", null,
                "Carmen Rios", "Directora de Bienestar", "REALIZADA", "APROBADO",
                "Experiencia alineada al cargo.", "Aprobaste la entrevista normal.",
                ahora.minusDays(11));
        crearEntrevista(sofia, "EVALUACION_PSICOLOGICA", ahora.minusDays(10),
                "14:00", "14:40", "PRESENCIAL", "Campus principal - Psicologia", null,
                "Paola Ruiz", "Psicologa organizacional", "REALIZADA", "APROBADO",
                "Sin observaciones restrictivas.", "Aprobaste la evaluacion psicologica.",
                ahora.minusDays(10));
        crearEvaluacionPsicologica(sofia, ahora.minusDays(10), "APTO",
                "Competencias socioemocionales acordes al puesto.", ahora.minusDays(10));

        crearHistorial(mateo, EstadoCodigo.ENTREVISTA.name(), EstadoCodigo.RECHAZADO.name(), ahora.minusDays(7),
                "admin@contitalent.com", "Entrevista normal desaprobada", "El proceso finalizo para esta oferta.");
        crearEntrevista(mateo, "ENTREVISTA_NORMAL", ahora.minusDays(7),
                "12:00", "12:30", "VIRTUAL", null, "https://meet.contitalent.com/entrevista-mateo",
                "Ana Castillo", "Lider de seleccion", "REALIZADA", "DESAPROBADO",
                "Necesita mayor experiencia docente.", "Por ahora no continuas en el proceso.",
                ahora.minusDays(7));
    }

    private void crearHistorial(Postulante postulante, String estadoAnterior, String estadoNuevo, LocalDateTime fechaCambio,
                                String usuarioAdmin, String observacionInterna, String observacionPostulante) {
        if (fechaCambio == null) return;
        HistorialEstadoPostulante historial = new HistorialEstadoPostulante();
        historial.setPostulante(postulante);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(estadoNuevo);
        historial.setFechaCambio(fechaCambio);
        historial.setUsuarioAdmin(usuarioAdmin);
        historial.setObservacionInterna(observacionInterna);
        historial.setObservacionPostulante(observacionPostulante);
        historialRepository.save(historial);
        postulante.getHistorialEstados().add(historial);
    }

    private void crearEntrevista(Postulante postulante, String tipo, LocalDateTime fechaProgramada, String horaInicio,
                                 String horaFin, String modalidad, String lugar, String enlaceVirtual,
                                 String entrevistadorNombre, String entrevistadorCargo, String estadoEntrevista,
                                 String resultado, String observacionInterna, String observacionPostulante,
                                 LocalDateTime creadoEn) {
        EntrevistaPostulante entrevista = new EntrevistaPostulante();
        entrevista.setPostulante(postulante);
        entrevista.setTipoEntrevista(tipo);
        entrevista.setFechaProgramada(fechaProgramada);
        entrevista.setHoraInicio(horaInicio);
        entrevista.setHoraFin(horaFin);
        entrevista.setModalidad(modalidad);
        entrevista.setLugar(lugar);
        entrevista.setEnlaceVirtual(enlaceVirtual);
        entrevista.setEntrevistadorNombre(entrevistadorNombre);
        entrevista.setEntrevistadorCargo(entrevistadorCargo);
        entrevista.setEstadoEntrevista(estadoEntrevista);
        entrevista.setResultado(resultado);
        entrevista.setObservacionInterna(observacionInterna);
        entrevista.setObservacionPostulante(observacionPostulante);
        entrevista.setUsuarioAdmin("admin@contitalent.com");
        entrevista.setCreadoEn(creadoEn);
        if ("REALIZADA".equals(estadoEntrevista)) {
            entrevista.setActualizadoPorAdminNombre("admin@contitalent.com");
            entrevista.setActualizadoEn(creadoEn);
            entrevista.setObservacionCambio("Resultado cargado como dato demo.");
        }
        entrevistaRepository.save(entrevista);
        postulante.getEntrevistas().add(entrevista);
    }

    private void crearEvaluacionPsicologica(Postulante postulante, LocalDateTime fechaEvaluacion, String resultado,
                                            String observacion, LocalDateTime creadoEn) {
        EvaluacionPsicologicaPostulante evaluacion = new EvaluacionPsicologicaPostulante();
        evaluacion.setPostulante(postulante);
        evaluacion.setFechaEvaluacion(fechaEvaluacion);
        evaluacion.setResultado(resultado);
        evaluacion.setObservacion(observacion);
        evaluacion.setUsuarioAdmin("admin@contitalent.com");
        evaluacion.setCreadoEn(creadoEn);
        psicologicaRepository.save(evaluacion);
        postulante.getEvaluacionesPsicologicas().add(evaluacion);
    }

    private void cargarDocumentosSeed(LocalDateTime ahora, Postulante lucia, Postulante carlos, Postulante pedro, Postulante andrea) {
        crearDocumentoSeed(lucia, "CV", "lucia_cv.pdf", "lucia_cv.pdf", ahora.minusDays(4));
        crearDocumentoSeed(carlos, "CV", "carlos_cv.pdf", "carlos_cv.pdf", ahora.minusDays(3));
        crearDocumentoSeed(carlos, "CERTIFICADO", "certificado_spring.pdf", "certificado_spring.pdf", ahora.minusDays(3));
        crearDocumentoSeed(pedro, "CV", "pedro_cv.pdf", "pedro_cv.pdf", ahora.minusDays(6));
        crearDocumentoSeed(andrea, "CV", "andrea_cv.pdf", "andrea_cv.pdf", ahora.minusDays(9));
    }

    private void crearDocumentoSeed(Postulante postulante, String tipo, String nombreOriginal, String nombreArchivo, LocalDateTime fechaSubida) {
        Path carpeta = "CV".equalsIgnoreCase(tipo) ? Paths.get("uploads", "cv") : Paths.get("uploads", "certificados");
        Path ruta = carpeta.resolve(nombreArchivo).toAbsolutePath().normalize();
        DocumentoPostulante documento = new DocumentoPostulante();
        documento.setPostulante(postulante);
        documento.setTipoDocumento(tipo);
        documento.setNombreOriginal(nombreOriginal);
        documento.setNombreArchivo(nombreArchivo);
        documento.setRutaArchivo(ruta.toString());
        documento.setExtension("pdf");
        try {
            documento.setTamanio(Files.exists(ruta) ? Files.size(ruta) : 0L);
        } catch (Exception ignored) {
            documento.setTamanio(0L);
        }
        documento.setFechaSubida(fechaSubida);
        documentoRepository.save(documento);
        postulante.getDocumentos().add(documento);
    }

    private Postulante crearPostulante(Usuario usuario, Oferta oferta, Estado estado,
                                       String nombre, String email, String telefono,
                                       String experiencia, String habilidades, String cv,
                                       int puntaje, LocalDateTime creadoEn,
                                       Map<Long, Integer> respuestas) {
        Postulante postulante = new Postulante();
        postulante.setUsuario(usuario);
        postulante.setOferta(oferta);
        postulante.setEstado(estado);
        postulante.setNombre(nombre);
        postulante.setEmail(email);
        postulante.setTelefono(telefono);
        postulante.setExperiencia(experiencia);
        postulante.setHabilidades(habilidades);
        postulante.setCv(cv);
        postulante.setPuntaje(puntaje);
        postulante.setFechaPostulacion(creadoEn);
        postulante.setFechaEvaluacion(respuestas != null && !respuestas.isEmpty() ? creadoEn.plusHours(1) : null);
        postulante.setAniosExperiencia(extraerAniosExperiencia(experiencia));
        postulante.setNivelEstudios("Universitario");
        postulante.setCarrera("No especificada");
        postulante.setDisponibilidad("Inmediata");
        postulante.setModalidadPreferida(oferta.getModalidad());
        postulante.setPuntajeExperiencia(calcularPuntajeExperiencia(postulante.getAniosExperiencia()));
        postulante.setPuntajeHabilidades(60);
        postulante.setPuntajeFinal((int) Math.round((puntaje * 0.50) + (postulante.getPuntajeExperiencia() * 0.30) + (postulante.getPuntajeHabilidades() * 0.20)));
        postulante.setRespuestas(new HashMap<>(respuestas));
        postulante.setCreadoEn(creadoEn);
        return postulante;
    }

    private int extraerAniosExperiencia(String experiencia) {
        if (experiencia == null) return 0;
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+)").matcher(experiencia);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    private int calcularPuntajeExperiencia(int anios) {
        if (anios <= 0) return 0;
        if (anios == 1) return 30;
        if (anios == 2) return 60;
        return 100;
    }

    /* =================== METRICAS (snapshot en memoria) =================== */
    private void cargarMetricas() {
        MetricasDTO metricas = new MetricasDTO();
        Map<String, MetricasDTO.Serie> series = new LinkedHashMap<>();

        series.put("postulaciones", new MetricasDTO.Serie(
                "Postulaciones recibidas (ultimos 8 meses)", "postulantes",
                List.of(
                        new MetricasDTO.Punto("sep-25", 125), new MetricasDTO.Punto("oct-25", 143),
                        new MetricasDTO.Punto("nov-25", 132), new MetricasDTO.Punto("dic-25", 156),
                        new MetricasDTO.Punto("ene-26", 162), new MetricasDTO.Punto("feb-26", 148),
                        new MetricasDTO.Punto("mar-26", 171), new MetricasDTO.Punto("abr-26", 183))));

        series.put("contrataciones", new MetricasDTO.Serie(
                "Contrataciones efectivas", "personas",
                List.of(
                        new MetricasDTO.Punto("sep-25", 12), new MetricasDTO.Punto("oct-25", 18),
                        new MetricasDTO.Punto("nov-25", 14), new MetricasDTO.Punto("dic-25", 22),
                        new MetricasDTO.Punto("ene-26", 25), new MetricasDTO.Punto("feb-26", 19),
                        new MetricasDTO.Punto("mar-26", 28), new MetricasDTO.Punto("abr-26", 31))));

        series.put("tiempoCierre", new MetricasDTO.Serie(
                "Tiempo promedio de cierre (dias)", "dias",
                List.of(
                        new MetricasDTO.Punto("sep-25", 28), new MetricasDTO.Punto("oct-25", 26),
                        new MetricasDTO.Punto("nov-25", 27), new MetricasDTO.Punto("dic-25", 24),
                        new MetricasDTO.Punto("ene-26", 22), new MetricasDTO.Punto("feb-26", 23),
                        new MetricasDTO.Punto("mar-26", 21), new MetricasDTO.Punto("abr-26", 19))));

        series.put("puntajePromedio", new MetricasDTO.Serie(
                "Puntaje promedio en evaluacion tecnica", "pts",
                List.of(
                        new MetricasDTO.Punto("sep-25", 70), new MetricasDTO.Punto("oct-25", 72),
                        new MetricasDTO.Punto("nov-25", 71), new MetricasDTO.Punto("dic-25", 74),
                        new MetricasDTO.Punto("ene-26", 76), new MetricasDTO.Punto("feb-26", 75),
                        new MetricasDTO.Punto("mar-26", 78), new MetricasDTO.Punto("abr-26", 81))));

        series.put("tasaAceptacion", new MetricasDTO.Serie(
                "Tasa de aceptacion (%)", "%",
                List.of(
                        new MetricasDTO.Punto("sep-25", 9.6), new MetricasDTO.Punto("oct-25", 12.6),
                        new MetricasDTO.Punto("nov-25", 10.6), new MetricasDTO.Punto("dic-25", 14.1),
                        new MetricasDTO.Punto("ene-26", 15.4), new MetricasDTO.Punto("feb-26", 12.8),
                        new MetricasDTO.Punto("mar-26", 16.4), new MetricasDTO.Punto("abr-26", 16.9))));

        metricas.setSeries(series);

        MetricasDTO.EstadoActual estadoActual = new MetricasDTO.EstadoActual();
        estadoActual.setPostulantesActivos(183);
        estadoActual.setOfertasAbiertas(12);
        estadoActual.setEntrevistasHoy(7);
        estadoActual.setOfertasEsteMes(31);
        estadoActual.setTiempoPromedio("19 dias");
        metricas.setEstadoActual(estadoActual);

        metricasRepository.save(metricas);
    }
}
