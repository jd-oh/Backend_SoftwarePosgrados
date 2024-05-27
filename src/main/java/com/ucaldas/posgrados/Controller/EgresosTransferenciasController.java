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
import com.ucaldas.posgrados.Entity.TipoTransferencia;
import com.ucaldas.posgrados.Entity.EgresosTransferencias;
import com.ucaldas.posgrados.Entity.EjecucionPresupuestal;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoTransferenciaRepository;
import com.ucaldas.posgrados.Repository.EgresosTransferenciasRepository;
import com.ucaldas.posgrados.Repository.EjecucionPresupuestalRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoTransferencia")
public class EgresosTransferenciasController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EgresosTransferenciasRepository egresoTransferenciaRepository;

    @Autowired
    private TipoTransferenciaRepository tipoTransferenciaRepository;

    @Autowired
    private PresupuestoController presupuestoController;

    @Autowired
    private EjecucionPresupuestalRepository ejecucionPresupuestalRepository;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String descripcion,
            @RequestParam double porcentaje,
            @RequestParam int idTipoTransferencia) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);
        Optional<TipoTransferencia> tipoTransferencia = tipoTransferenciaRepository.findById(idTipoTransferencia);

        // Verificar si la programa existe
        if (presupuesto.isPresent() && tipoTransferencia.isPresent()) {
            EgresosTransferencias egresosTransferencias = new EgresosTransferencias();

            egresosTransferencias.setDescripcion(descripcion);
            egresosTransferencias.setPorcentaje(porcentaje);

            // La creación de los gastos por transferencia deben hacerse después de haber
            // calculado los ingresos totales

            egresosTransferencias.setValorTotal(presupuesto.get().getIngresosTotales() * porcentaje / 100);

            egresosTransferencias.setPresupuesto(presupuesto.get());
            egresosTransferencias.setTipoTransferencia(tipoTransferencia.get());

            // La fecha y hora se asigna en el momento de la creación con la del sistema
            egresosTransferencias.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());
            egresosTransferencias.setFechaHoraUltimaModificacion("No ha sido modificado");

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no.
            // La etiqueta también es nula porque se usa en la ejecución presupuestal
            egresosTransferencias.setEjecucionPresupuestal(null);
            egresosTransferencias.setEtiquetaEgresoIngreso(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosTransferencias().add(egresosTransferencias);

            int idPresupuesto = egresosTransferencias.getPresupuesto().getId();
            double valorNuevo = egresosTransferencias.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, 0);

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
            @RequestParam String descripcion,
            @RequestParam double porcentaje,
            @RequestParam int idTipoTransferencia, @RequestParam int idEgreso) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(idEjecucionPresupuestal)
                .orElseThrow();

        TipoTransferencia tipoTransferencia = tipoTransferenciaRepository.findById(idTipoTransferencia).orElseThrow();

        EgresosTransferencias egresoTransferencia = new EgresosTransferencias();

        egresoTransferencia = guardarValoresEgresoEjecucion(egresoTransferencia, descripcion, porcentaje,
                tipoTransferencia, ejecucionPresupuestal);
        EgresosTransferencias egresoDelPresupuesto = egresoTransferenciaRepository.findById(idEgreso).orElseThrow();

        // Si todos los datos del egresoDelPresupuesto son iguales a los que se quieren
        // guardar en este nuevo egreso, entonces poner la etiqueta MISMOVALOR, pues
        // significa que es el mismo que se presupuestó
        if (egresoDelPresupuesto.getDescripcion().equals(descripcion)
                && egresoDelPresupuesto.getPorcentaje() == porcentaje
                && egresoDelPresupuesto.getTipoTransferencia().getId() == idTipoTransferencia) {
            egresoTransferencia.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            egresoTransferencia.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        egresoTransferenciaRepository.save(egresoTransferencia);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private EgresosTransferencias guardarValoresEgresoEjecucion(EgresosTransferencias egresoTransferencia,
            @RequestParam String descripcion,
            @RequestParam double porcentaje,
            @RequestParam TipoTransferencia tipoTransferencia, EjecucionPresupuestal ejecucionPresupuestal) {

        egresoTransferencia.setEjecucionPresupuestal(ejecucionPresupuestal);
        egresoTransferencia.setPresupuesto(null);
        egresoTransferencia.setDescripcion(descripcion);
        egresoTransferencia.setPorcentaje(porcentaje);
        egresoTransferencia.setTipoTransferencia(tipoTransferencia);
        egresoTransferencia
                .setValorTotal(ejecucionPresupuestal.getPresupuesto().getIngresosTotales() * porcentaje / 100);

        egresoTransferencia.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " "
                + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                + java.time.LocalDateTime.now().getSecond());
        egresoTransferencia.setFechaHoraUltimaModificacion("No ha sido modificado");
        return egresoTransferencia;
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que no se tuvo
     * en cuenta en el presupuesto. (Valores en blanco)
     */
    @PostMapping("/crearEgresoFueraDelPresupuesto")
    public @ResponseBody String crearEgresoFueraDelPresupuesto(@RequestParam int idEjecucionPresupuestal,
            @RequestParam String descripcion,
            @RequestParam double porcentaje,
            @RequestParam int idTipoTransferencia) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(idEjecucionPresupuestal)
                .orElseThrow();

        TipoTransferencia tipoTransferencia = tipoTransferenciaRepository.findById(idTipoTransferencia).orElseThrow();

        EgresosTransferencias egresoTransferencia = new EgresosTransferencias();

        egresoTransferencia = guardarValoresEgresoEjecucion(egresoTransferencia, descripcion, porcentaje,
                tipoTransferencia, ejecucionPresupuestal);

        egresoTransferencia.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        egresoTransferenciaRepository.save(egresoTransferencia);

        return "OK";
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EgresosTransferencias> listar() {
        return egresoTransferenciaRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<EgresosTransferencias> buscar(@RequestParam int id) {
        return egresoTransferenciaRepository.findById(id);
    }

    // Este metodo se hizo para que cuando se edite o se cree un ingreso después de
    // haber creado una transferencia,
    // se actualice el valor total de la transferencia, ya que este depende del
    // ingreso total del presupuesto
    public void actualizarValoresTransferenciasPorIngresos() {
        Iterable<EgresosTransferencias> egresosTransferencias = egresoTransferenciaRepository.findAll();
        for (EgresosTransferencias egreso : egresosTransferencias) {
            egreso.setValorTotal(egreso.getPresupuesto().getIngresosTotales() * egreso.getPorcentaje() / 100);
            egresoTransferenciaRepository.save(egreso);
        }
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam String descripcion,
            @RequestParam double porcentaje,
            @RequestParam int idTipoTransferencia) {

        Optional<EgresosTransferencias> egreso = egresoTransferenciaRepository.findById(id);
        int idPresupuesto = egreso.get().getPresupuesto().getId();
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        Optional<TipoTransferencia> tipoTransferencia = tipoTransferenciaRepository.findById(idTipoTransferencia);

        if (egreso.isPresent() && presupuesto.isPresent() && tipoTransferencia.isPresent()) {

            double valorAnterior = egreso.get().getValorTotal();

            EgresosTransferencias egresosTransferenciasActualizado = egreso.get();

            egresosTransferenciasActualizado.setDescripcion(descripcion);
            egresosTransferenciasActualizado.setPorcentaje(porcentaje);
            egresosTransferenciasActualizado.setValorTotal(presupuesto.get().getIngresosTotales() * porcentaje / 100);
            egresosTransferenciasActualizado.setTipoTransferencia(tipoTransferencia.get());

            egresosTransferenciasActualizado.setFechaHoraUltimaModificacion(java.time.LocalDateTime.now().toString());

            double valorNuevo = egresosTransferenciasActualizado.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, valorAnterior);

            egresoTransferenciaRepository.save(egresosTransferenciasActualizado);
            return "OK";
        } else {
            return "Error: Egreso de descuento no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        Optional<EgresosTransferencias> egreso = egresoTransferenciaRepository.findById(id);

        if (!egreso.isPresent()) {
            return "Error: Egreso de transferencia no encontrado";
        }
        int idPresupuesto = egreso.get().getPresupuesto().getId();
        double valorAnterior = egreso.get().getValorTotal();

        presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, 0, valorAnterior);
        egresoTransferenciaRepository.deleteById(id);
        return "OK";
    }

    // Listar por presupuesto
    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Iterable<EgresosTransferencias> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return egresoTransferenciaRepository.findByPresupuestoId(idPresupuesto);
    }

    // Este es para el presupuesto
    @GetMapping("/totalEgresosTransferencias")
    public @ResponseBody double totalEgresosTransferencias(int idPresupuesto) {
        Iterable<EgresosTransferencias> egresosTransferencias = egresoTransferenciaRepository
                .findByPresupuestoId(idPresupuesto);
        double total = 0;

        if (!egresosTransferencias.iterator().hasNext()) {
            return 0;
        }

        for (EgresosTransferencias egreso : egresosTransferencias) {
            total += egreso.getValorTotal();
        }
        return total;
    }

    // Este es para la ejecución presupuestal
    @GetMapping("/totalEgresosTransferenciasEjecucion")
    public @ResponseBody double totalEgresosTransferenciasEjecucion(int idEjecucionPresupuestal) {
        Iterable<EgresosTransferencias> egresosTransferencias = egresoTransferenciaRepository
                .findByEjecucionPresupuestalId(idEjecucionPresupuestal);
        double total = 0;

        if (!egresosTransferencias.iterator().hasNext()) {
            return 0;
        }

        for (EgresosTransferencias egreso : egresosTransferencias) {
            total += egreso.getValorTotal();
        }
        return total;
    }

}
