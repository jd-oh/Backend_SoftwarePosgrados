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
import com.ucaldas.posgrados.Entity.EgresosOtros;
import com.ucaldas.posgrados.Entity.EjecucionPresupuestal;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoCostoRepository;
import com.ucaldas.posgrados.Repository.EgresosOtrosRepository;
import com.ucaldas.posgrados.Repository.EjecucionPresupuestalRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoOtro")
public class EgresosOtrosController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EgresosOtrosRepository egresoOtroRepository;

    @Autowired
    private TipoCostoRepository tipoCostoRepository;

    @Autowired
    private PresupuestoController presupuestoController;

    @Autowired
    private EgresosTransferenciasController egresosTransferenciasController;

    @Autowired
    private EjecucionPresupuestalRepository ejecucionPresupuestalRepository;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String concepto,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad, @RequestParam int idTipoCosto) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);
        Optional<TipoCosto> tipoCosto = tipoCostoRepository.findById(idTipoCosto);

        // Verificar si la programa existe
        if (presupuesto.isPresent()) {
            EgresosOtros egresosOtros = new EgresosOtros();
            egresosOtros.setConcepto(concepto);
            egresosOtros.setValorUnitario(valorUnitario);
            egresosOtros.setCantidad(cantidad);
            egresosOtros.setValorTotal(cantidad * valorUnitario);

            egresosOtros.setPresupuesto(presupuesto.get());
            egresosOtros.setTipoCosto(tipoCosto.get());
            // La fecha y hora se asigna en el momento de la creación con la del sistema
            egresosOtros.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());
            egresosOtros.setFechaHoraUltimaModificacion("No ha sido modificado");

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no.
            // La etiqueta también es nula porque se usa en la ejecución presupuestal
            egresosOtros.setEjecucionPresupuestal(null);
            egresosOtros.setEtiquetaEgresoIngreso(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosOtros().add(egresosOtros);

            int idPresupuesto = egresosOtros.getPresupuesto().getId();
            double valorNuevo = egresosOtros.getValorTotal();

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
            @RequestParam String concepto,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad, @RequestParam int idTipoCosto, @RequestParam int idEgreso) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(idEjecucionPresupuestal)
                .orElseThrow();

        TipoCosto tipoCosto = tipoCostoRepository.findById(idTipoCosto).orElseThrow();

        EgresosOtros egresoOtro = new EgresosOtros();

        egresoOtro = guardarValoresEgresoEjecucion(egresoOtro, concepto, valorUnitario, cantidad, tipoCosto,
                ejecucionPresupuestal);

        EgresosOtros egresoDelPresupuesto = egresoOtroRepository.findById(idEgreso).orElseThrow();

        // Si todos los datos del egresoDelPresupuesto son iguales a los que se quieren
        // guardar en este nuevo egreso, entonces poner la etiqueta MISMOVALOR, pues
        // significa que es el mismo que se presupuestó
        if (egresoDelPresupuesto.getConcepto().equals(concepto)
                && egresoDelPresupuesto.getValorUnitario() == valorUnitario
                && egresoDelPresupuesto.getCantidad() == cantidad
                && egresoDelPresupuesto.getTipoCosto().getId() == idTipoCosto) {
            egresoOtro.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            egresoOtro.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        egresoOtroRepository.save(egresoOtro);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private EgresosOtros guardarValoresEgresoEjecucion(EgresosOtros egresoOtro, @RequestParam String concepto,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad, @RequestParam TipoCosto tipoCosto,
            EjecucionPresupuestal ejecucionPresupuestal) {

        egresoOtro.setEjecucionPresupuestal(ejecucionPresupuestal);
        egresoOtro.setPresupuesto(null);
        egresoOtro.setConcepto(concepto);
        egresoOtro.setValorUnitario(valorUnitario);
        egresoOtro.setCantidad(cantidad);
        egresoOtro.setValorTotal(cantidad * valorUnitario);
        egresoOtro.setTipoCosto(tipoCosto);
        egresoOtro.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " "
                + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                + java.time.LocalDateTime.now().getSecond());
        egresoOtro.setFechaHoraUltimaModificacion("No ha sido modificado");
        return egresoOtro;
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

        EgresosOtros egresoOtro = new EgresosOtros();

        egresoOtro = guardarValoresEgresoEjecucion(egresoOtro, concepto, valorUnitario, cantidad, tipoCosto,
                ejecucionPresupuestal);

        egresoOtro.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        egresoOtroRepository.save(egresoOtro);

        return "OK";
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EgresosOtros> listar() {
        return egresoOtroRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<EgresosOtros> buscar(@RequestParam int id) {
        return egresoOtroRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam int idTipoCosto,
            @RequestParam String concepto, @RequestParam double valorUnitario,
            @RequestParam int cantidad) {

        Optional<EgresosOtros> egreso = egresoOtroRepository.findById(id);
        Optional<TipoCosto> tipoCosto = tipoCostoRepository.findById(idTipoCosto);

        if (egreso.isPresent() && tipoCosto.isPresent()) {

            double valorAnterior = egreso.get().getValorTotal();

            EgresosOtros egresosOtrosActualizado = egreso.get();
            egresosOtrosActualizado.setConcepto(concepto);
            egresosOtrosActualizado.setValorUnitario(valorUnitario);
            egresosOtrosActualizado.setCantidad(cantidad);
            egresosOtrosActualizado.setValorTotal(cantidad * valorUnitario);

            egresosOtrosActualizado.setTipoCosto(tipoCosto.get());
            egresosOtrosActualizado.setFechaHoraUltimaModificacion(java.time.LocalDateTime.now().toString());

            int idPresupuesto = egresosOtrosActualizado.getPresupuesto().getId();
            double valorNuevo = egresosOtrosActualizado.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, valorAnterior);
            egresoOtroRepository.save(egresosOtrosActualizado);
            return "OK";
        } else {
            return "Error: Egreso de otros no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {
        Optional<EgresosOtros> egreso = egresoOtroRepository.findById(id);

        if (!egreso.isPresent()) {
            return "Error: Egreso de otros no encontrado";
        }
        double valorAnterior = egreso.get().getValorTotal();
        int idPresupuesto = egreso.get().getPresupuesto().getId();

        egresoOtroRepository.deleteById(id);

        presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, 0, valorAnterior);
        return "OK";
    }

    // Listar por presupuesto
    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Iterable<EgresosOtros> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return egresoOtroRepository.findByPresupuestoId(idPresupuesto);
    }

    @GetMapping("/totalEgresosOtros")
    public @ResponseBody double totalEgresosOtros(int idPresupuesto) {
        double total = 0;
        Iterable<EgresosOtros> egresosOtros = egresoOtroRepository.findByPresupuestoId(idPresupuesto);

        // Si no hay egresos de otros
        if (!egresosOtros.iterator().hasNext()) {
            return total;
        }
        for (EgresosOtros egresoOtro : egresosOtros) {
            total += egresoOtro.getValorTotal();
        }
        return total;
    }

}
