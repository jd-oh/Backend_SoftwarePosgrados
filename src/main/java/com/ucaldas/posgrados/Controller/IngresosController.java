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
import com.ucaldas.posgrados.Entity.EjecucionPresupuestal;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Entity.Ingresos;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.EjecucionPresupuestalRepository;
import com.ucaldas.posgrados.Repository.IngresosRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/ingreso")
public class IngresosController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private IngresosRepository ingresoRepository;

    @Autowired
    private PresupuestoController presupuestoController;

    @Autowired
    private EgresosTransferenciasController egresosTransferenciasController;

    @Autowired
    private EjecucionPresupuestalRepository ejecucionPresupuestalRepository;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String concepto,
            @RequestParam double valor) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);

        // Verificar si la programa existe
        if (presupuesto.isPresent()) {

            Ingresos ingreso = new Ingresos();
            ingreso.setConcepto(concepto);
            ingreso.setValor(valor);
            ingreso.setPresupuesto(presupuesto.get());
            // La fecha y hora se asigna en el momento de la creación con la del sistema
            ingreso.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());
            ingreso.setFechaHoraUltimaModificacion("No ha sido modificado");

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no.
            // La etiqueta también es nula porque se usa en la ejecución presupuestal
            ingreso.setEjecucionPresupuestal(null);
            ingreso.setEtiquetaEgresoIngreso(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getIngresos().add(ingreso);

            int idPresupuesto = presupuesto.get().getId();
            double valorNuevo = ingreso.getValor();

            presupuestoController.actualizarIngresosTotales(idPresupuesto, valorNuevo, 0, "ingreso");

            // Si existen egresos de transferencias entonces llamamos al metodo
            // 'actualizarIngresosTotales'
            if (egresosTransferenciasController.listar().iterator().hasNext()) {
                egresosTransferenciasController.actualizarValoresTransferenciasPorIngresos();

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
            @RequestParam String concepto,
            @RequestParam double valor, @RequestParam int idIngreso) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(idEjecucionPresupuestal)
                .orElseThrow();

        Ingresos ingreso = new Ingresos();

        ingreso = guardarValoresEgresoEjecucion(ingreso, concepto, valor, ejecucionPresupuestal);

        Ingresos ingresoDelPresupuesto = ingresoRepository.findById(idIngreso).orElseThrow();

        // Si todos los datos del egresoDelPresupuesto son iguales a los que se quieren
        // guardar en este nuevo egreso, entonces poner la etiqueta MISMOVALOR, pues
        // significa que es el mismo que se presupuestó
        if (ingresoDelPresupuesto.getConcepto().equals(ingreso.getConcepto())
                && ingresoDelPresupuesto.getValor() == ingreso.getValor()) {
            ingreso.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            ingreso.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        ingresoRepository.save(ingreso);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private Ingresos guardarValoresEgresoEjecucion(Ingresos ingreso,
            @RequestParam String concepto,
            @RequestParam double valor, EjecucionPresupuestal ejecucionPresupuestal) {

        ingreso.setEjecucionPresupuestal(ejecucionPresupuestal);
        ingreso.setPresupuesto(null);
        ingreso.setConcepto(concepto);
        ingreso.setValor(valor);

        ingreso.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " "
                + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                + java.time.LocalDateTime.now().getSecond());
        ingreso.setFechaHoraUltimaModificacion("No ha sido modificado");
        return ingreso;
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que no se tuvo
     * en cuenta en el presupuesto. (Valores en blanco)
     */
    @PostMapping("/crearEgresoFueraDelPresupuesto")
    public @ResponseBody String crearEgresoFueraDelPresupuesto(@RequestParam int idEjecucionPresupuestal,
            @RequestParam String concepto,
            @RequestParam double valor) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(idEjecucionPresupuestal)
                .orElseThrow();

        Ingresos ingreso = new Ingresos();

        ingreso = guardarValoresEgresoEjecucion(ingreso, concepto, valor, ejecucionPresupuestal);

        ingreso.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        ingresoRepository.save(ingreso);

        return "OK";
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<Ingresos> listar() {
        return ingresoRepository.findAllByOrderByPresupuestoAsc();
    }

    // Listar por presupuesto
    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Iterable<Ingresos> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return ingresoRepository.findByPresupuestoId(idPresupuesto);
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<Ingresos> buscar(@RequestParam int id) {
        return ingresoRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam String concepto,
            @RequestParam double valor) {
        Optional<Ingresos> ingreso = ingresoRepository.findById(id);

        if (ingreso.isPresent()) {

            double valorAnterior = ingreso.get().getValor();
            Ingresos ingresoActualizado = ingreso.get();
            ingresoActualizado.setConcepto(concepto);
            ingresoActualizado.setValor(valor);
            ingresoActualizado.setFechaHoraUltimaModificacion(java.time.LocalDateTime.now().toString());

            int idPresupuesto = ingresoActualizado.getPresupuesto().getId();
            double valorNuevo = ingresoActualizado.getValor();
            presupuestoController.actualizarIngresosTotales(idPresupuesto, valorNuevo, valorAnterior, "ingreso");

            // Si existen egresos de transferencias entonces llamamos al metodo
            // 'actualizarIngresosTotales'
            if (egresosTransferenciasController.listar().iterator().hasNext()) {
                egresosTransferenciasController.actualizarValoresTransferenciasPorIngresos();
            }

            ingresoRepository.save(ingresoActualizado);

            return "OK";
        } else {
            return "Error: Ingreso o Presupuesto no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        Optional<Ingresos> ingreso = ingresoRepository.findById(id);

        if (!ingreso.isPresent()) {
            return "Error: Ingreso no encontrado";
        }

        int idPresupuesto = ingreso.get().getPresupuesto().getId();
        double valorAnterior = ingreso.get().getValor();

        presupuestoController.actualizarIngresosTotales(idPresupuesto, 0, valorAnterior, "ingreso");

        // Si existen egresos de transferencias entonces llamamos al metodo
        // 'actualizarIngresosTotales'
        if (egresosTransferenciasController.listar().iterator().hasNext()) {
            egresosTransferenciasController.actualizarValoresTransferenciasPorIngresos();
        }
        ingresoRepository.deleteById(id);

        return "OK";
    }

    // Este es para el presupuesto
    @GetMapping("/totalIngresos")
    public @ResponseBody double totalIngresos(int idPresupuesto) {
        double totalIngresos = 0;
        Iterable<Ingresos> ingresos = ingresoRepository.findByPresupuestoId(idPresupuesto);
        if (!ingresos.iterator().hasNext()) {
            return totalIngresos;
        }
        for (Ingresos ingreso : ingresos) {
            totalIngresos += ingreso.getValor();
        }
        return totalIngresos;
    }

    // Este es para la ejecución presupuestal
    @GetMapping("/totalIngresosEjecucion")
    public @ResponseBody double totalIngresosEjecucion(int idEjecucionPresupuestal) {
        double totalIngresos = 0;
        Iterable<Ingresos> ingresos = ingresoRepository.findByEjecucionPresupuestalId(idEjecucionPresupuestal);
        if (!ingresos.iterator().hasNext()) {
            return totalIngresos;
        }
        for (Ingresos ingreso : ingresos) {
            totalIngresos += ingreso.getValor();
        }
        return totalIngresos;
    }

}
