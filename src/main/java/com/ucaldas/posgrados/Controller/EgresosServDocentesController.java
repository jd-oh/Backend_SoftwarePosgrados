package com.ucaldas.posgrados.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.Entity.Presupuesto;
import com.ucaldas.posgrados.Entity.Programa;
import com.ucaldas.posgrados.Entity.TipoCompensacion;
import com.ucaldas.posgrados.Entity.Cohorte;
import com.ucaldas.posgrados.Entity.EgresosServDocentes;
import com.ucaldas.posgrados.Entity.EjecucionPresupuestal;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoCompensacionRepository;
import com.ucaldas.posgrados.Repository.EgresosServDocentesRepository;
import com.ucaldas.posgrados.Repository.EjecucionPresupuestalRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoServDocente")
public class EgresosServDocentesController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EgresosServDocentesRepository egresoServDocenteRepository;

    @Autowired
    private TipoCompensacionRepository tipoCompensacionRepository;

    @Autowired
    private PresupuestoController presupuestoController;

    @Autowired
    private EjecucionPresupuestalRepository ejecucionPresupuestalRepository;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String nombreMateria,
            @RequestParam boolean esDocentePlanta,
            @RequestParam String nombreDocente, @RequestParam String escalafon, @RequestParam String titulo,
            @RequestParam int horasTeoricasMat,
            @RequestParam int horasPracticasMat, @RequestParam double valorHoraProfesor,
            @RequestParam int idTipoCompensacion) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);
        Optional<TipoCompensacion> tipoCompensacion = tipoCompensacionRepository.findById(idTipoCompensacion);

        // Verificar si la programa existe
        if (presupuesto.isPresent() && tipoCompensacion.isPresent()) {
            EgresosServDocentes egresosServDocentes = new EgresosServDocentes();

            egresosServDocentes.setNombreMateria(nombreMateria);
            egresosServDocentes.setEsDocentePlanta(esDocentePlanta);
            egresosServDocentes.setNombreDocente(nombreDocente);
            egresosServDocentes.setEscalafon(escalafon);
            egresosServDocentes.setTitulo(titulo);
            egresosServDocentes.setHorasTeoricasMat(horasTeoricasMat);
            egresosServDocentes.setHorasPracticasMat(horasPracticasMat);

            egresosServDocentes.setTotalHorasProfesor(horasPracticasMat + horasTeoricasMat);

            egresosServDocentes.setValorHoraProfesor(valorHoraProfesor);

            egresosServDocentes.setTotalPagoProfesor(valorHoraProfesor * egresosServDocentes.getTotalHorasProfesor());

            egresosServDocentes.setPresupuesto(presupuesto.get());
            egresosServDocentes.setTipoCompensacion(tipoCompensacion.get());

            // La fecha y hora se asigna en el momento de la creación con la del sistema
            egresosServDocentes.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());
            egresosServDocentes.setFechaHoraUltimaModificacion("No ha sido modificado");

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no.
            // La etiqueta también es nula porque se usa en la ejecución presupuestal
            egresosServDocentes.setEjecucionPresupuestal(null);
            egresosServDocentes.setEtiquetaEgresoIngreso(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosServDocentes().add(egresosServDocentes);

            int idPresupuesto = idPresupuestoEjecucion;
            double valorNuevo = egresosServDocentes.getTotalPagoProfesor();

            Cohorte cohorte = presupuesto.get().getCohorte();
            Programa programa = cohorte.getPrograma();

            // Si el docente es de planta y además el programa es priorizado, se actualiza
            // el total de egresos de la universidad
            // Si el docente no es de planta, se actualiza el total de egresos del programa.
            // No importa si es priorizado o no
            if (egresosServDocentes.isEsDocentePlanta() && programa.isPriorizado()) {
                presupuestoController.actualizarEgresosRecurrentesUniversidadTotales(idPresupuesto, valorNuevo, 0);
            } else {
                presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, 0);
            }

            // Guardar el Presupuesto actualizado
            presupuestoRepository.save(presupuesto.get());

            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que se tuvo en
     * cuenta en el presupuesto.
     * En el frontend habrá una lista de egresos del presupuesto en la sección de
     * descuentos. Cuando se elija uno, se cargará
     * toda la información de este en los campos, si se guarda así tal como está
     * entonces se pondrá la etiqueta MISMOVALOR, en cambio
     * si se cambia algún valor entonces se pondrá la etiqueta OTROVALOR.
     * 
     */
    @PostMapping("/crearEgresoEjecucionDelPresupuesto")
    public @ResponseBody String crearEgresoEjecucionDelPresupuesto(@RequestParam int idEjecucionPresupuestal,
            @RequestParam String nombreMateria,
            @RequestParam boolean esDocentePlanta,
            @RequestParam String nombreDocente, @RequestParam String escalafon, @RequestParam String titulo,
            @RequestParam int horasTeoricasMat,
            @RequestParam int horasPracticasMat, @RequestParam double valorHoraProfesor,
            @RequestParam int idTipoCompensacion, @RequestParam int idEgreso) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(idEjecucionPresupuestal)
                .orElseThrow();

        TipoCompensacion tipoCompensacion = tipoCompensacionRepository.findById(idTipoCompensacion).orElseThrow();

        EgresosServDocentes egresoServDocente = new EgresosServDocentes();

        egresoServDocente = guardarValoresEgresoEjecucion(egresoServDocente, nombreMateria, esDocentePlanta,
                nombreDocente,
                escalafon, titulo, horasTeoricasMat, horasPracticasMat, valorHoraProfesor, tipoCompensacion,
                ejecucionPresupuestal);

        EgresosServDocentes egresoDelPresupuesto = egresoServDocenteRepository.findById(idEgreso).orElseThrow();

        // Si todos los datos del egresoDelPresupuesto son iguales a los que se quieren
        // guardar en este nuevo egreso, entonces poner la etiqueta MISMOVALOR, pues
        // significa que es el mismo que se presupuestó
        if (egresoDelPresupuesto.getNombreMateria().equals(nombreMateria)
                && egresoDelPresupuesto.isEsDocentePlanta() == esDocentePlanta
                && egresoDelPresupuesto.getNombreDocente().equals(nombreDocente)
                && egresoDelPresupuesto.getEscalafon().equals(escalafon)
                && egresoDelPresupuesto.getTitulo().equals(titulo)
                && egresoDelPresupuesto.getHorasTeoricasMat() == horasTeoricasMat
                && egresoDelPresupuesto.getHorasPracticasMat() == horasPracticasMat
                && egresoDelPresupuesto.getValorHoraProfesor() == valorHoraProfesor
                && egresoDelPresupuesto.getTipoCompensacion().getId() == idTipoCompensacion) {
            egresoServDocente.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            egresoServDocente.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        egresoServDocenteRepository.save(egresoServDocente);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private EgresosServDocentes guardarValoresEgresoEjecucion(EgresosServDocentes egresoServDocente,
            @RequestParam String nombreMateria,
            @RequestParam boolean esDocentePlanta,
            @RequestParam String nombreDocente, @RequestParam String escalafon, @RequestParam String titulo,
            @RequestParam int horasTeoricasMat,
            @RequestParam int horasPracticasMat, @RequestParam double valorHoraProfesor,
            @RequestParam TipoCompensacion tipoCompensacion, EjecucionPresupuestal ejecucionPresupuestal) {

        egresoServDocente.setEjecucionPresupuestal(ejecucionPresupuestal);
        egresoServDocente.setPresupuesto(null);
        egresoServDocente.setNombreMateria(nombreMateria);
        egresoServDocente.setEsDocentePlanta(esDocentePlanta);
        egresoServDocente.setNombreDocente(nombreDocente);
        egresoServDocente.setEscalafon(escalafon);
        egresoServDocente.setTitulo(titulo);
        egresoServDocente.setHorasTeoricasMat(horasTeoricasMat);
        egresoServDocente.setHorasPracticasMat(horasPracticasMat);
        egresoServDocente.setTotalHorasProfesor(horasPracticasMat + horasTeoricasMat);
        egresoServDocente.setValorHoraProfesor(valorHoraProfesor);
        egresoServDocente.setTotalPagoProfesor(valorHoraProfesor * egresoServDocente.getTotalHorasProfesor());
        egresoServDocente.setTipoCompensacion(tipoCompensacion);

        egresoServDocente.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " "
                + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                + java.time.LocalDateTime.now().getSecond());
        egresoServDocente.setFechaHoraUltimaModificacion("No ha sido modificado");
        return egresoServDocente;
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que no se tuvo
     * en cuenta en el presupuesto. (Valores en blanco)
     */
    @PostMapping("/crearEgresoFueraDelPresupuesto")
    public @ResponseBody String crearEgresoFueraDelPresupuesto(@RequestParam int idEjecucionPresupuestal,
            @RequestParam String nombreMateria,
            @RequestParam boolean esDocentePlanta,
            @RequestParam String nombreDocente, @RequestParam String escalafon, @RequestParam String titulo,
            @RequestParam int horasTeoricasMat,
            @RequestParam int horasPracticasMat, @RequestParam double valorHoraProfesor,
            @RequestParam int idTipoCompensacion) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(idEjecucionPresupuestal)
                .orElseThrow();

        TipoCompensacion tipoCompensacion = tipoCompensacionRepository.findById(idTipoCompensacion).orElseThrow();

        EgresosServDocentes egresoServDocente = new EgresosServDocentes();

        egresoServDocente = guardarValoresEgresoEjecucion(egresoServDocente, nombreMateria, esDocentePlanta,
                nombreDocente,
                escalafon, titulo, horasTeoricasMat, horasPracticasMat, valorHoraProfesor, tipoCompensacion,
                ejecucionPresupuestal);

        egresoServDocente.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        egresoServDocenteRepository.save(egresoServDocente);

        return "OK";
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EgresosServDocentes> listar() {
        return egresoServDocenteRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<EgresosServDocentes> buscar(@RequestParam int id) {
        return egresoServDocenteRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam int idTipoCompensacion,
            @RequestParam String nombreMateria, @RequestParam boolean esDocentePlanta,
            @RequestParam String nombreDocente, @RequestParam String escalafon, @RequestParam String titulo,
            @RequestParam int horasTeoricasMat, @RequestParam int horasPracticasMat,
            @RequestParam double valorHoraProfesor) {

        Optional<EgresosServDocentes> egreso = egresoServDocenteRepository.findById(id);
        Optional<TipoCompensacion> tipoCompensacion = tipoCompensacionRepository.findById(idTipoCompensacion);
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(egreso.get().getPresupuesto().getId());

        if (egreso.isPresent() && tipoCompensacion.isPresent()) {

            double valorAnterior = egreso.get().getTotalPagoProfesor();

            EgresosServDocentes egresosServDocentesActualizado = egreso.get();

            egresosServDocentesActualizado.setNombreMateria(nombreMateria);
            egresosServDocentesActualizado.setEsDocentePlanta(esDocentePlanta);
            egresosServDocentesActualizado.setNombreDocente(nombreDocente);
            egresosServDocentesActualizado.setEscalafon(escalafon);
            egresosServDocentesActualizado.setTitulo(titulo);
            egresosServDocentesActualizado.setHorasTeoricasMat(horasTeoricasMat);
            egresosServDocentesActualizado.setHorasPracticasMat(horasPracticasMat);
            egresosServDocentesActualizado.setFechaHoraUltimaModificacion(java.time.LocalDateTime.now().toString());

            egresosServDocentesActualizado.setTotalHorasProfesor(horasPracticasMat + horasTeoricasMat);

            egresosServDocentesActualizado.setValorHoraProfesor(valorHoraProfesor);

            egresosServDocentesActualizado
                    .setTotalPagoProfesor(valorHoraProfesor * egresosServDocentesActualizado.getTotalHorasProfesor());

            egresosServDocentesActualizado.setTipoCompensacion(tipoCompensacion.get());
            int idPresupuesto = egresosServDocentesActualizado.getPresupuesto().getId();
            double valorNuevo = egresosServDocentesActualizado.getTotalPagoProfesor();

            Cohorte cohorte = presupuesto.get().getCohorte();
            Programa programa = cohorte.getPrograma();

            // Si el docente es de planta y además el programa es priorizado, se actualiza
            // el total de egresos de la universidad
            // Si el docente no es de planta, se actualiza el total de egresos del programa.
            // No importa si es priorizado o no
            if (egresosServDocentesActualizado.isEsDocentePlanta() && programa.isPriorizado()) {
                presupuestoController.actualizarEgresosRecurrentesUniversidadTotales(idPresupuesto, valorNuevo,
                        valorAnterior);
            } else {
                presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, valorAnterior);
            }

            egresoServDocenteRepository.save(egresosServDocentesActualizado);

            return "OK";
        } else {
            return "Error: Egreso de servicios docentes no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        Optional<EgresosServDocentes> egreso = egresoServDocenteRepository.findById(id);

        if (!egreso.isPresent()) {
            return "Error: Egreso de transferencia no encontrado";
        }
        int idPresupuesto = egreso.get().getPresupuesto().getId();
        double valorAnterior = egreso.get().getTotalPagoProfesor();
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(egreso.get().getPresupuesto().getId());
        Cohorte cohorte = presupuesto.get().getCohorte();
        Programa programa = cohorte.getPrograma();

        if (egreso.get().isEsDocentePlanta() && programa.isPriorizado()) {
            presupuestoController.actualizarEgresosRecurrentesUniversidadTotales(idPresupuesto, 0, valorAnterior);
        } else {
            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, 0, valorAnterior);
        }
        egresoServDocenteRepository.deleteById(id);
        return "OK";
    }

    // Listar por presupuesto
    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Iterable<EgresosServDocentes> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return egresoServDocenteRepository.findByPresupuestoId(idPresupuesto);
    }

    @GetMapping("/totalEgresosServDocentes")
    public @ResponseBody double totalEgresosServDocentes(int idPresupuesto) {
        double total = 0;
        Iterable<EgresosServDocentes> egresosServDocentes = egresoServDocenteRepository
                .findByPresupuestoId(idPresupuesto);

        // Si no hay egresos de otros
        if (!egresosServDocentes.iterator().hasNext()) {
            return total;
        }
        for (EgresosServDocentes egresoServDocente : egresosServDocentes) {
            total += egresoServDocente.getTotalPagoProfesor();
        }
        return total;
    }

    @GetMapping("/totalEgresosServDocentesDePlanta")
    public @ResponseBody double totalEgresosServDocentesDePlanta(int idPresupuesto) {
        double total = 0;
        Iterable<EgresosServDocentes> egresosServDocentes = egresoServDocenteRepository
                .findByPresupuestoId(idPresupuesto);

        // Si no hay egresos de otros
        if (!egresosServDocentes.iterator().hasNext()) {
            return total;
        }
        for (EgresosServDocentes egresoServDocente : egresosServDocentes) {
            if (egresoServDocente.isEsDocentePlanta()) {
                total += egresoServDocente.getTotalPagoProfesor();
            }
        }
        return total;
    }

}
