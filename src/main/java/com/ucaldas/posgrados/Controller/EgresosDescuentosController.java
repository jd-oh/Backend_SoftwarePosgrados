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
import com.ucaldas.posgrados.Entity.TipoDescuento;
import com.ucaldas.posgrados.Entity.EgresosDescuentos;
import com.ucaldas.posgrados.Entity.EjecucionPresupuestal;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoDescuentoRepository;
import com.ucaldas.posgrados.Repository.EgresosDescuentosRepository;
import com.ucaldas.posgrados.Repository.EjecucionPresupuestalRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoDescuento")
public class EgresosDescuentosController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EgresosDescuentosRepository egresoDescuentoRepository;

    @Autowired
    private EjecucionPresupuestalRepository ejecucionPresupuestalRepository;

    @Autowired
    private TipoDescuentoRepository tipoDescuentoRepository;

    @Autowired
    private PresupuestoController presupuestoController;

    @Autowired
    private EgresosTransferenciasController egresosTransferenciasController;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuesto, @RequestParam int numEstudiantes,
            @RequestParam double valor, @RequestParam int numPeriodos,
            @RequestParam int idTipoDescuento) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        Optional<TipoDescuento> tipoDescuento = tipoDescuentoRepository.findById(idTipoDescuento);

        // Verificar si la programa existe
        if (presupuesto.isPresent() && tipoDescuento.isPresent()) {
            EgresosDescuentos egresosDescuentos = new EgresosDescuentos();
            egresosDescuentos.setNumEstudiantes(numEstudiantes);
            egresosDescuentos.setValor(valor);
            egresosDescuentos.setNumPeriodos(numPeriodos);
            egresosDescuentos.setTotalDescuento(valor * numEstudiantes * numPeriodos);
            egresosDescuentos.setPresupuesto(presupuesto.get());
            egresosDescuentos.setTipoDescuento(tipoDescuento.get());
            // La fecha y hora se asigna en el momento de la creación con la del sistema
            egresosDescuentos.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());
            egresosDescuentos.setFechaHoraUltimaModificacion("No ha sido modificado");

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no.
            // La etiqueta también es nula porque se usa en la ejecución presupuestal
            egresosDescuentos.setEjecucionPresupuestal(null);
            egresosDescuentos.setEtiquetaEgresoIngreso(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosDescuentos().add(egresosDescuentos);

            double valorNuevo = egresosDescuentos.getTotalDescuento();

            presupuestoController.actualizarIngresosTotales(idPresupuesto, valorNuevo, 0, "descuento");

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
            @RequestParam int numEstudiantes,
            @RequestParam double valor, @RequestParam int numPeriodos,
            @RequestParam int idTipoDescuento, @RequestParam int idEgreso) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(idEjecucionPresupuestal)
                .orElseThrow();

        TipoDescuento tipoDescuento = tipoDescuentoRepository.findById(idTipoDescuento).orElseThrow();

        EgresosDescuentos egresoDescuento = new EgresosDescuentos();

        egresoDescuento = guardarValoresEgresoEjecucion(egresoDescuento, numEstudiantes, valor, numPeriodos,
                ejecucionPresupuestal, tipoDescuento);

        EgresosDescuentos egresoDelPresupuesto = egresoDescuentoRepository.findById(idEgreso).orElseThrow();

        // Si todos los datos del egresoDelPresupuesto son iguales a los que se quieren
        // guardar en este nuevo egreso, entonces poner la etiqueta MISMOVALOR, pues
        // significa que es el mismo que se presupuestó
        if (egresoDelPresupuesto.getNumEstudiantes() == numEstudiantes && egresoDelPresupuesto.getValor() == valor
                && egresoDelPresupuesto.getNumPeriodos() == numPeriodos
                && egresoDelPresupuesto.getTipoDescuento().getId() == tipoDescuento.getId()) {
            egresoDescuento.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            egresoDescuento.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        egresoDescuentoRepository.save(egresoDescuento);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private EgresosDescuentos guardarValoresEgresoEjecucion(EgresosDescuentos egresoDescuento, int numEstudiantes,
            double valor, int numPeriodos,
            EjecucionPresupuestal ejecucionPresupuestal, TipoDescuento tipoDescuento) {

        egresoDescuento.setEjecucionPresupuestal(ejecucionPresupuestal);
        egresoDescuento.setPresupuesto(null);
        egresoDescuento.setNumEstudiantes(numEstudiantes);
        egresoDescuento.setValor(valor);
        egresoDescuento.setNumPeriodos(numPeriodos);
        egresoDescuento.setTotalDescuento(valor * numEstudiantes * numPeriodos);
        egresoDescuento.setTipoDescuento(tipoDescuento);
        egresoDescuento.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " "
                + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                + java.time.LocalDateTime.now().getSecond());
        egresoDescuento.setFechaHoraUltimaModificacion("No ha sido modificado");
        return egresoDescuento;
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que no se tuvo
     * en cuenta en el presupuesto. (Valores en blanco)
     */
    @PostMapping("/crearEgresoFueraDelPresupuesto")
    public @ResponseBody String crearEgresoFueraDelPresupuesto(@RequestParam int idEjecucionPresupuestal,
            @RequestParam int numEstudiantes,
            @RequestParam double valor, @RequestParam int numPeriodos,
            @RequestParam int idTipoDescuento) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(idEjecucionPresupuestal)
                .orElseThrow();

        TipoDescuento tipoDescuento = tipoDescuentoRepository.findById(idTipoDescuento).orElseThrow();

        EgresosDescuentos egresoDescuento = new EgresosDescuentos();

        egresoDescuento = guardarValoresEgresoEjecucion(egresoDescuento, numEstudiantes, valor, numPeriodos,
                ejecucionPresupuestal, tipoDescuento);

        egresoDescuento.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        egresoDescuentoRepository.save(egresoDescuento);

        return "OK";
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EgresosDescuentos> listar() {
        return egresoDescuentoRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<EgresosDescuentos> buscar(@RequestParam int id) {
        return egresoDescuentoRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam int numEstudiantes,
            @RequestParam double valor,
            @RequestParam int numPeriodos, @RequestParam int idTipoDescuento) {

        Optional<EgresosDescuentos> egreso = egresoDescuentoRepository.findById(id);
        Optional<TipoDescuento> tipoDescuento = tipoDescuentoRepository.findById(idTipoDescuento);

        if (egreso.isPresent() && tipoDescuento.isPresent()) {
            double valorAnterior = egreso.get().getTotalDescuento();
            EgresosDescuentos egresosDescuentosActualizado = egreso.get();
            egresosDescuentosActualizado.setNumEstudiantes(numEstudiantes);
            egresosDescuentosActualizado.setValor(valor);
            egresosDescuentosActualizado.setNumPeriodos(numPeriodos);
            egresosDescuentosActualizado.setTotalDescuento(valor * numEstudiantes * numPeriodos);
            egresosDescuentosActualizado.setTipoDescuento(tipoDescuento.get());
            egresosDescuentosActualizado.setFechaHoraUltimaModificacion(java.time.LocalDateTime.now().getDayOfMonth()
                    + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());
            int idPresupuesto = egresosDescuentosActualizado.getPresupuesto().getId();
            double valorNuevo = egresosDescuentosActualizado.getTotalDescuento();
            presupuestoController.actualizarIngresosTotales(idPresupuesto, valorNuevo, valorAnterior, "descuento");

            // Si existen egresos de transferencias entonces llamamos al metodo
            // 'actualizarIngresosTotales'
            if (egresosTransferenciasController.listar().iterator().hasNext()) {
                egresosTransferenciasController.actualizarValoresTransferenciasPorIngresos();
            }
            egresoDescuentoRepository.save(egresosDescuentosActualizado);

            return "OK";
        } else {
            return "Error: Egreso de descuento no encontrado";
        }
    }

    // Listar por presupuesto
    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Iterable<EgresosDescuentos> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return egresoDescuentoRepository.findByPresupuestoId(idPresupuesto);
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        Optional<EgresosDescuentos> egreso = egresoDescuentoRepository.findById(id);

        if (!egreso.isPresent()) {
            return "Error: Egreso de descuento no encontrado";
        }

        int idPresupuesto = egreso.get().getPresupuesto().getId();
        double valorAnterior = egreso.get().getTotalDescuento();

        presupuestoController.actualizarIngresosTotales(idPresupuesto, 0, valorAnterior, "descuento");

        // Si existen egresos de transferencias entonces llamamos al metodo
        // 'actualizarIngresosTotales'
        if (egresosTransferenciasController.listar().iterator().hasNext()) {
            egresosTransferenciasController.actualizarValoresTransferenciasPorIngresos();
        }

        egresoDescuentoRepository.deleteById(id);

        return "OK";
    }

    @GetMapping("/totalEgresosDescuentos")
    public @ResponseBody double totalEgresosDescuentos(int idPresupuesto) {
        double total = 0;
        Iterable<EgresosDescuentos> egresosDescuentos = egresoDescuentoRepository.findByPresupuestoId(idPresupuesto);

        // Si no hay egresos de descuentos
        if (!egresosDescuentos.iterator().hasNext()) {
            return total;
        }
        for (EgresosDescuentos egresoDescuento : egresosDescuentos) {
            total += egresoDescuento.getTotalDescuento();
        }
        return total;
    }
}
