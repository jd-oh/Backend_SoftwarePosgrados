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
import com.ucaldas.posgrados.Entity.EgresosRecurrentesAdm;
import com.ucaldas.posgrados.Entity.EjecucionPresupuestal;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.EgresosRecurrentesAdmRepository;
import com.ucaldas.posgrados.Repository.EjecucionPresupuestalRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoRecurrenteAdm")
public class EgresosRecurrentesAdmController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EgresosRecurrentesAdmRepository egresoRecurrenteAdmRepository;

    @Autowired
    private PresupuestoController presupuestoController;

    @Autowired
    private EjecucionPresupuestalRepository ejecucionPresupuestalRepository;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String unidad,
            @RequestParam String cargo,
            @RequestParam double valorHora, @RequestParam int numHoras) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);

        // Verificar si la programa existe
        if (presupuesto.isPresent()) {
            EgresosRecurrentesAdm egresoRecurrenteAdm = new EgresosRecurrentesAdm();

            egresoRecurrenteAdm.setUnidad(unidad);
            egresoRecurrenteAdm.setCargo(cargo);
            egresoRecurrenteAdm.setValorHora(valorHora);
            egresoRecurrenteAdm.setNumHoras(numHoras);
            egresoRecurrenteAdm.setValorTotal(valorHora * numHoras);

            egresoRecurrenteAdm.setPresupuesto(presupuesto.get());
            // La fecha y hora se asigna en el momento de la creación con la del sistema
            egresoRecurrenteAdm.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());
            egresoRecurrenteAdm.setFechaHoraUltimaModificacion("No ha sido modificado");

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no.
            // La etiqueta también es nula porque se usa en la ejecución presupuestal
            egresoRecurrenteAdm.setEjecucionPresupuestal(null);
            egresoRecurrenteAdm.setEtiquetaEgresoIngreso(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosRecurrentesAdm().add(egresoRecurrenteAdm);

            int idPresupuesto = presupuesto.get().getId();
            double valorNuevo = egresoRecurrenteAdm.getValorTotal();

            presupuestoController.actualizarEgresosRecurrentesUniversidadTotales(idPresupuesto, valorNuevo, 0);

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
            @RequestParam String unidad,
            @RequestParam String cargo,
            @RequestParam double valorHora, @RequestParam int numHoras, @RequestParam int idEgreso) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(idEjecucionPresupuestal)
                .orElseThrow();

        EgresosRecurrentesAdm egresoRecurrenteAdm = new EgresosRecurrentesAdm();

        egresoRecurrenteAdm = guardarValoresEgresoEjecucion(egresoRecurrenteAdm, unidad, cargo, valorHora, numHoras,
                ejecucionPresupuestal);

        EgresosRecurrentesAdm egresoDelPresupuesto = egresoRecurrenteAdmRepository.findById(idEgreso).orElseThrow();

        // Si todos los datos del egresoDelPresupuesto son iguales a los que se quieren
        // guardar en este nuevo egreso, entonces poner la etiqueta MISMOVALOR, pues
        // significa que es el mismo que se presupuestó
        if (egresoDelPresupuesto.getUnidad().equals(unidad) && egresoDelPresupuesto.getCargo().equals(cargo)
                && egresoDelPresupuesto.getValorHora() == valorHora && egresoDelPresupuesto.getNumHoras() == numHoras) {
            egresoRecurrenteAdm.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            egresoRecurrenteAdm.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        egresoRecurrenteAdmRepository.save(egresoRecurrenteAdm);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private EgresosRecurrentesAdm guardarValoresEgresoEjecucion(EgresosRecurrentesAdm egresoRecurrenteAdm,
            @RequestParam String unidad,
            @RequestParam String cargo,
            @RequestParam double valorHora, @RequestParam int numHoras, EjecucionPresupuestal ejecucionPresupuestal) {

        egresoRecurrenteAdm.setEjecucionPresupuestal(ejecucionPresupuestal);
        egresoRecurrenteAdm.setPresupuesto(null);
        egresoRecurrenteAdm.setUnidad(unidad);
        egresoRecurrenteAdm.setCargo(cargo);
        egresoRecurrenteAdm.setValorHora(valorHora);
        egresoRecurrenteAdm.setNumHoras(numHoras);
        egresoRecurrenteAdm.setValorTotal(valorHora * numHoras);

        egresoRecurrenteAdm.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " "
                + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                + java.time.LocalDateTime.now().getSecond());
        egresoRecurrenteAdm.setFechaHoraUltimaModificacion("No ha sido modificado");
        return egresoRecurrenteAdm;
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que no se tuvo
     * en cuenta en el presupuesto. (Valores en blanco)
     */
    @PostMapping("/crearEgresoFueraDelPresupuesto")
    public @ResponseBody String crearEgresoFueraDelPresupuesto(@RequestParam int idEjecucionPresupuestal,
            @RequestParam String unidad,
            @RequestParam String cargo,
            @RequestParam double valorHora, @RequestParam int numHoras) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(idEjecucionPresupuestal)
                .orElseThrow();

        EgresosRecurrentesAdm egresoRecurrenteAdm = new EgresosRecurrentesAdm();

        egresoRecurrenteAdm = guardarValoresEgresoEjecucion(egresoRecurrenteAdm, unidad, cargo, valorHora, numHoras,
                ejecucionPresupuestal);

        egresoRecurrenteAdm.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        egresoRecurrenteAdmRepository.save(egresoRecurrenteAdm);

        return "OK";
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EgresosRecurrentesAdm> listar() {
        return egresoRecurrenteAdmRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<EgresosRecurrentesAdm> buscar(@RequestParam int id) {
        return egresoRecurrenteAdmRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam String unidad,
            @RequestParam String cargo,
            @RequestParam double valorHora, @RequestParam int numHoras) {
        Optional<EgresosRecurrentesAdm> egresoRecurrenteAdm = egresoRecurrenteAdmRepository.findById(id);

        if (egresoRecurrenteAdm.isPresent()) {

            double valorAnterior = egresoRecurrenteAdm.get().getValorTotal();

            EgresosRecurrentesAdm egresoRecurrenteAdmActualizado = egresoRecurrenteAdm.get();
            egresoRecurrenteAdmActualizado.setUnidad(unidad);
            egresoRecurrenteAdmActualizado.setCargo(cargo);
            egresoRecurrenteAdmActualizado.setValorHora(valorHora);
            egresoRecurrenteAdmActualizado.setNumHoras(numHoras);
            egresoRecurrenteAdmActualizado.setValorTotal(valorHora * numHoras);
            egresoRecurrenteAdmActualizado.setFechaHoraUltimaModificacion(java.time.LocalDateTime.now().toString());

            int idPresupuesto = egresoRecurrenteAdmActualizado.getPresupuesto().getId();
            double valorNuevo = egresoRecurrenteAdmActualizado.getValorTotal();
            presupuestoController.actualizarEgresosRecurrentesUniversidadTotales(idPresupuesto, valorNuevo,
                    valorAnterior);

            egresoRecurrenteAdmRepository.save(egresoRecurrenteAdmActualizado);
            return "OK";
        } else {
            return "Error: Egreso recurrente administracion o Presupuesto no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        Optional<EgresosRecurrentesAdm> egresoRecurrenteAdm = egresoRecurrenteAdmRepository.findById(id);

        if (!egresoRecurrenteAdm.isPresent()) {
            return "Error: Egreso recurrente administracion no encontrado";
        }
        int idPresupuesto = egresoRecurrenteAdm.get().getPresupuesto().getId();
        double valorAnterior = egresoRecurrenteAdm.get().getValorTotal();
        presupuestoController.actualizarEgresosRecurrentesUniversidadTotales(idPresupuesto, 0, valorAnterior);
        egresoRecurrenteAdmRepository.deleteById(id);
        return "OK";
    }

    // Listar por presupuesto
    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Iterable<EgresosRecurrentesAdm> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return egresoRecurrenteAdmRepository.findByPresupuestoId(idPresupuesto);
    }

    // Este es para presupuesto
    @GetMapping("/totalEgresosRecurrentesAdm")
    public @ResponseBody double totalEgresosRecurrentesAdm(int idPresupuesto) {
        double total = 0;
        Iterable<EgresosRecurrentesAdm> egresosRecurrentesAdm = egresoRecurrenteAdmRepository
                .findByPresupuestoId(idPresupuesto);

        // Si no hay egresos
        if (!egresosRecurrentesAdm.iterator().hasNext()) {
            return total;
        }
        for (EgresosRecurrentesAdm egresoRecurrenteAdm : egresosRecurrentesAdm) {
            total += egresoRecurrenteAdm.getValorTotal();
        }
        return total;
    }

    // Este es para ejecucion presupuestal
    @GetMapping("/totalEgresosRecurrentesAdmEjecucion")
    public @ResponseBody double totalEgresosRecurrentesAdmEjecucion(int idEjecucionPresupuestal) {
        double total = 0;
        Iterable<EgresosRecurrentesAdm> egresosRecurrentesAdm = egresoRecurrenteAdmRepository
                .findByEjecucionPresupuestalId(idEjecucionPresupuestal);

        // Si no hay egresos
        if (!egresosRecurrentesAdm.iterator().hasNext()) {
            return total;
        }
        for (EgresosRecurrentesAdm egresoRecurrenteAdm : egresosRecurrentesAdm) {
            total += egresoRecurrenteAdm.getValorTotal();
        }
        return total;
    }

}
