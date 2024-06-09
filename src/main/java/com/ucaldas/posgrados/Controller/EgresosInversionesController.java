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
import com.ucaldas.posgrados.Entity.TipoInversion;
import com.ucaldas.posgrados.Entity.EgresosInversiones;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoInversionRepository;
import com.ucaldas.posgrados.Repository.EgresosInversionesRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoInversion")
public class EgresosInversionesController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EgresosInversionesRepository egresoInversionRepository;

    @Autowired
    private TipoInversionRepository tipoInversionRepository;

    @Autowired
    private PresupuestoController presupuestoController;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String concepto,
            @RequestParam double valor,
            @RequestParam int idTipoInversion) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);
        Optional<TipoInversion> tipoInversion = tipoInversionRepository.findById(idTipoInversion);

        // Verificar si la programa existe
        if (presupuesto.isPresent() && tipoInversion.isPresent()) {
            EgresosInversiones egresosInversiones = new EgresosInversiones();
            egresosInversiones.setConcepto(concepto);
            egresosInversiones.setValor(valor);
            egresosInversiones.setPresupuesto(presupuesto.get());
            egresosInversiones.setTipoInversion(tipoInversion.get());
            // La fecha y hora se asigna en el momento de la creación con la del sistema
            egresosInversiones.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());
            egresosInversiones.setFechaHoraUltimaModificacion("No ha sido modificado");

            // La etiqueta sólo se usa en la ejecución presupuestal
            egresosInversiones.setEtiquetaEgresoIngreso(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosInversiones().add(egresosInversiones);

            int idPresupuesto = presupuesto.get().getId();
            double valorNuevo = egresosInversiones.getValor();

            presupuestoController.actualizarEgresosRecurrentesUniversidadTotales(idPresupuesto, valorNuevo, 0);

            // Guardar el Presupuesto actualizado
            presupuestoRepository.save(presupuesto.get());

            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EgresosInversiones> listar() {
        return egresoInversionRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<EgresosInversiones> buscar(@RequestParam int id) {
        return egresoInversionRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id,
            @RequestParam String concepto, @RequestParam double valor,
            @RequestParam int idTipoInversion) {

        Optional<EgresosInversiones> egreso = egresoInversionRepository.findById(id);
        Optional<TipoInversion> tipoInversion = tipoInversionRepository.findById(idTipoInversion);

        if (egreso.isPresent() && tipoInversion.isPresent()) {

            double valorAnterior = egreso.get().getValor();

            EgresosInversiones egresosInversionesActualizado = egreso.get();
            egresosInversionesActualizado.setConcepto(concepto);
            egresosInversionesActualizado.setValor(valor);
            egresosInversionesActualizado.setTipoInversion(tipoInversion.get());
            egresosInversionesActualizado.setFechaHoraUltimaModificacion(java.time.LocalDateTime.now().getDayOfMonth()
                    + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());

            int idPresupuesto = egresosInversionesActualizado.getPresupuesto().getId();

            double valorNuevo = egresosInversionesActualizado.getValor();

            presupuestoController.actualizarEgresosRecurrentesUniversidadTotales(idPresupuesto, valorNuevo,
                    valorAnterior);

            egresoInversionRepository.save(egresosInversionesActualizado);
            return "OK";
        } else {
            return "Error: Egreso de descuento no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        Optional<EgresosInversiones> egreso = egresoInversionRepository.findById(id);

        if (!egreso.isPresent()) {
            return "Error: Egreso de descuento no encontrado";
        }

        int idPresupuesto = egreso.get().getPresupuesto().getId();
        double valorAnterior = egreso.get().getValor();

        presupuestoController.actualizarEgresosRecurrentesUniversidadTotales(idPresupuesto, 0, valorAnterior);
        egresoInversionRepository.deleteById(id);
        return "OK";
    }

    // Listar por presupuesto
    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Iterable<EgresosInversiones> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return egresoInversionRepository.findByPresupuestoId(idPresupuesto);
    }

    // Este es para listar los egresos de inversiones que se hicieron en el
    // presupuesto
    @GetMapping("/totalEgresosInversiones")
    public @ResponseBody double totalEgresosInversiones(int idPresupuesto) {
        double total = 0;
        Iterable<EgresosInversiones> egresosInversiones = egresoInversionRepository.findByPresupuestoId(idPresupuesto);

        if (!egresosInversiones.iterator().hasNext()) {
            return total;
        }
        for (EgresosInversiones egreso : egresosInversiones) {
            total += egreso.getValor();
        }
        return total;
    }

    /*
     * // Este es para listar los egresos de inversiones que se hicieron en la
     * // ejecución presupuestal
     * 
     * @GetMapping("/totalEgresosInversionesEjecucion")
     * public @ResponseBody double totalEgresosInversionesEjecucion(int
     * idEjecucionPresupuestal) {
     * double total = 0;
     * Iterable<EgresosInversiones> egresosInversiones = egresoInversionRepository
     * .findByEjecucionPresupuestalId(idEjecucionPresupuestal);
     * 
     * if (!egresosInversiones.iterator().hasNext()) {
     * return total;
     * }
     * for (EgresosInversiones egreso : egresosInversiones) {
     * total += egreso.getValor();
     * }
     * return total;
     * }
     */
}
