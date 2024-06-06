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
import com.ucaldas.posgrados.Entity.TipoCosto;
import com.ucaldas.posgrados.Entity.EgresosGenerales;
import com.ucaldas.posgrados.Entity.EjecucionPresupuestal;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoCostoRepository;
import com.ucaldas.posgrados.Repository.EgresosGeneralesRepository;
import com.ucaldas.posgrados.Repository.EjecucionPresupuestalRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoGeneral")
public class EgresosGeneralesController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EgresosGeneralesRepository egresoGeneralRepository;

    @Autowired
    private TipoCostoRepository tipoCostoRepository;

    @Autowired
    private PresupuestoController presupuestoController;

    @Autowired
    private EjecucionPresupuestalRepository ejecucionPresupuestalRepository;

    /**
     * Crea un egreso general para un presupuesto o una ejecución presupuestal
     * 
     * @param idPresupuestoEjecucion recibe ya sea el id del presupuesto o de la
     *                               ejecución presupuestal (para reutilizar el
     *                               metodo crear)
     * @param concepto
     * @param valorUnitario
     * @param cantidad
     * @param idTipoCosto
     * @return
     */
    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String concepto,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad, @RequestParam int idTipoCosto) {

        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);

        Optional<TipoCosto> tipoCosto = tipoCostoRepository.findById(idTipoCosto);

        if (presupuesto.isPresent() && tipoCosto.isPresent()) {
            EgresosGenerales egresosGenerales = new EgresosGenerales();
            egresosGenerales.setConcepto(concepto);
            egresosGenerales.setValorUnitario(valorUnitario);
            egresosGenerales.setCantidad(cantidad);
            egresosGenerales.setValorTotal(cantidad * valorUnitario);

            egresosGenerales.setPresupuesto(presupuesto.get());
            egresosGenerales.setTipoCosto(tipoCosto.get());
            // La fecha y hora se asigna en el momento de la creación con la del sistema
            egresosGenerales.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());
            egresosGenerales.setFechaHoraUltimaModificacion("No ha sido modificado");

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no.
            // La etiqueta también es nula porque se usa en la ejecución presupuestal
            egresosGenerales.setEjecucionPresupuestal(null);
            egresosGenerales.setEtiquetaEgresoIngreso(null);

            int idPresupuesto = egresosGenerales.getPresupuesto().getId();
            double valorNuevo = egresosGenerales.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, 0);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosGenerales().add(egresosGenerales);

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
     * El concepto no se puede modificar.
     */
    @PostMapping("/crearEgresoEjecucionDelPresupuesto")
    public @ResponseBody String crearEgresoEjecucionDelPresupuesto(@RequestParam int idEjecucionPresupuestal,
            @RequestParam String concepto,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad, @RequestParam int idTipoCosto, @RequestParam int idEgreso) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(idEjecucionPresupuestal)
                .orElseThrow();

        TipoCosto tipoCosto = tipoCostoRepository.findById(idTipoCosto).orElseThrow();

        EgresosGenerales egresoGeneral = new EgresosGenerales();

        egresoGeneral = guardarValoresEgresoEjecucion(egresoGeneral, concepto, valorUnitario, cantidad,
                ejecucionPresupuestal, tipoCosto);

        EgresosGenerales egresoDelPresupuesto = egresoGeneralRepository.findById(idEgreso).orElseThrow();

        // Si todos los datos del egresoDelPresupuesto son iguales a los que se quieren
        // guardar en este nuevo egreso, entonces poner la etiqueta MISMOVALOR, pues
        // significa que es el mismo que se presupuestó
        if (egresoDelPresupuesto.getConcepto().equals(concepto)
                && egresoDelPresupuesto.getValorUnitario() == valorUnitario
                && egresoDelPresupuesto.getCantidad() == cantidad
                && egresoDelPresupuesto.getTipoCosto().getId() == idTipoCosto) {
            egresoGeneral.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            egresoGeneral.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        egresoGeneralRepository.save(egresoGeneral);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private EgresosGenerales guardarValoresEgresoEjecucion(EgresosGenerales egresoGeneral, String concepto,
            double valorUnitario,
            int cantidad, EjecucionPresupuestal ejecucionPresupuestal, TipoCosto tipoCosto) {

        egresoGeneral.setConcepto(concepto);
        egresoGeneral.setValorUnitario(valorUnitario);
        egresoGeneral.setCantidad(cantidad);
        egresoGeneral.setValorTotal(cantidad * valorUnitario);

        egresoGeneral.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " "
                + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                + java.time.LocalDateTime.now().getSecond());
        egresoGeneral.setFechaHoraUltimaModificacion("No ha sido modificado");
        return egresoGeneral;
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que no se tuvo
     * en cuenta en el presupuesto. (Valores en blanco)
     */
    @PostMapping("/crearEgresoFueraDelPresupuesto")
    public @ResponseBody String crearEgresoFueraDelPresupuesto(@RequestParam int idEjecucionPresupuestal,
            @RequestParam String concepto,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad, @RequestParam int idTipoCosto) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(idEjecucionPresupuestal)
                .orElseThrow();

        TipoCosto tipoCosto = tipoCostoRepository.findById(idTipoCosto).orElseThrow();

        EgresosGenerales egresoGeneral = new EgresosGenerales();

        egresoGeneral = guardarValoresEgresoEjecucion(egresoGeneral, concepto, valorUnitario, cantidad,
                ejecucionPresupuestal, tipoCosto);

        egresoGeneral.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        egresoGeneralRepository.save(egresoGeneral);

        return "OK";
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EgresosGenerales> listar() {
        return egresoGeneralRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<EgresosGenerales> buscar(@RequestParam int id) {
        return egresoGeneralRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam int idTipoCosto,
            @RequestParam String concepto, @RequestParam double valorUnitario,
            @RequestParam int cantidad) {

        Optional<EgresosGenerales> egreso = egresoGeneralRepository.findById(id);
        Optional<TipoCosto> tipoCosto = tipoCostoRepository.findById(idTipoCosto);

        if (egreso.isPresent() && tipoCosto.isPresent()) {
            double valorAnterior = egreso.get().getValorTotal();

            EgresosGenerales egresosGeneralesActualizado = egreso.get();
            egresosGeneralesActualizado.setConcepto(concepto);
            egresosGeneralesActualizado.setValorUnitario(valorUnitario);
            egresosGeneralesActualizado.setCantidad(cantidad);
            egresosGeneralesActualizado.setValorTotal(cantidad * valorUnitario);
            egresosGeneralesActualizado.setFechaHoraUltimaModificacion(java.time.LocalDateTime.now().getDayOfMonth()
                    + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());

            egresosGeneralesActualizado.setTipoCosto(tipoCosto.get());
            int idPresupuesto = egresosGeneralesActualizado.getPresupuesto().getId();

            double valorNuevo = egresosGeneralesActualizado.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, valorAnterior);

            egresoGeneralRepository.save(egresosGeneralesActualizado);
            return "OK";
        } else {
            return "Error: Egreso general no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        Optional<EgresosGenerales> egreso = egresoGeneralRepository.findById(id);

        if (!egreso.isPresent()) {
            return "Error: Egreso general no encontrado";
        }

        int idPresupuesto = egreso.get().getPresupuesto().getId();
        double valorAnterior = egreso.get().getValorTotal();

        presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, 0, valorAnterior);
        egresoGeneralRepository.deleteById(id);
        return "OK";
    }

    // Listar por presupuesto
    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Iterable<EgresosGenerales> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return egresoGeneralRepository.findByPresupuestoId(idPresupuesto);
    }

    // Listar por ejecución presupuestal
    @GetMapping("/listarPorEjecucionPresupuestal")
    public @ResponseBody Iterable<EgresosGenerales> listarPorEjecucionPresupuestal(
            @RequestParam int idEjecucionPresupuestal) {
        return egresoGeneralRepository.findByEjecucionPresupuestalId(idEjecucionPresupuestal);
    }

    // Este es el total de egresos generales de un presupuesto
    @GetMapping("/totalEgresosGenerales")
    public @ResponseBody double totalEgresosGenerales(int idPresupuesto) {
        double total = 0;
        Iterable<EgresosGenerales> egresosGenerales = egresoGeneralRepository.findByPresupuestoId(idPresupuesto);
        // Si no hay egresos generales
        if (!egresosGenerales.iterator().hasNext()) {
            return total;
        }
        for (EgresosGenerales egresoGeneral : egresosGenerales) {
            total += egresoGeneral.getValorTotal();
        }
        return total;
    }

    // Este es el total de egresos generales de una ejecución presupuestal
    @GetMapping("/totalEgresosGeneralesEjecucion")
    public @ResponseBody double totalEgresosGeneralesEjecucion(int idEjecucionPresupuestal) {
        double total = 0;
        Iterable<EgresosGenerales> egresosGenerales = egresoGeneralRepository
                .findByEjecucionPresupuestalId(idEjecucionPresupuestal);
        // Si no hay egresos generales
        if (!egresosGenerales.iterator().hasNext()) {
            return total;
        }
        for (EgresosGenerales egresoGeneral : egresosGenerales) {
            total += egresoGeneral.getValorTotal();
        }
        return total;
    }

}
