package com.ucaldas.posgrados.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.Entity.EjecucionPresupuestal;
import com.ucaldas.posgrados.Entity.Presupuesto;

import com.ucaldas.posgrados.Repository.EjecucionPresupuestalRepository;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;

import org.springframework.web.bind.annotation.PostMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/ejecucionPresupuestal")
public class EjecucionPresupuestalController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EjecucionPresupuestalRepository ejecucionPresupuestalRepository;

    // Se crea solo con el id del presupuesto porque al momento de que un
    // presupuesto es aprobado se crea una ejecución presupuestal
    // Pero aún sin gastos/ingresos, pues estos se van creando a medida que se van
    // necesitando
    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuesto) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        if (presupuesto.isPresent()) {
            // Comprobamos que no exista una ejecución presupuestal para el presupuesto
            if (ejecucionPresupuestalRepository.findByPresupuestoId(idPresupuesto).isPresent()) {
                return "Ya existe una ejecución presupuestal para este presupuesto";
            }

            EjecucionPresupuestal ejecucionPresupuestal = new EjecucionPresupuestal();
            ejecucionPresupuestal.setBalanceGeneralEjecucion(0);
            ejecucionPresupuestal.setEgresosProgramaTotalesEjecucion(0);
            ejecucionPresupuestal.setEgresosRecurrentesUniversidadTotalesEjecucion(0);
            ejecucionPresupuestal.setIngresosTotalesEjecucion(0);

            ejecucionPresupuestal.setPresupuesto(presupuesto.get());

            ejecucionPresupuestalRepository.save(ejecucionPresupuestal);
            return "OK";
        } else {
            return "No se encontró el presupuesto";
        }
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EjecucionPresupuestal> listar() {
        return ejecucionPresupuestalRepository.findAll();
    }

    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Optional<EjecucionPresupuestal> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return ejecucionPresupuestalRepository.findByPresupuestoId(idPresupuesto);
    }

    @GetMapping("/listarPorFacultad")
    public @ResponseBody Iterable<EjecucionPresupuestal> listarPorFacultad(@RequestParam int idFacultad) {
        return ejecucionPresupuestalRepository.findByPresupuestoFacultadId(idFacultad);
    }

    @GetMapping("/listarPorPrograma")
    public @ResponseBody Iterable<EjecucionPresupuestal> listarPorPrograma(@RequestParam int idPrograma) {
        return ejecucionPresupuestalRepository.findByPresupuestoProgramaId(idPrograma);
    }

    // Se utiliza para actualizar el atributo ingresosTotales de la clase
    // Presupuesto
    public String actualizarIngresosTotales(int idEjecucionPresupuestal,
            double nuevoValor, double antiguoValor, String tipo) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findById(idEjecucionPresupuestal);

        if (ejecucionPresupuestal.isPresent() && tipo.equals("ingreso")) {
            ejecucionPresupuestal.get().setIngresosTotalesEjecucion(
                    ejecucionPresupuestal.get().getIngresosTotalesEjecucion() - antiguoValor + nuevoValor);
            actualizarBalanceGeneral(idEjecucionPresupuestal);
            return "OK";
        } else if (ejecucionPresupuestal.isPresent() && tipo.equals("descuento")) {
            ejecucionPresupuestal.get().setIngresosTotalesEjecucion(
                    ejecucionPresupuestal.get().getIngresosTotalesEjecucion() - nuevoValor + antiguoValor);
            actualizarBalanceGeneral(idEjecucionPresupuestal);
            return "OK";
        } else {
            return "Error: Ejecucion presupuestal no encontrada";
        }

    }

    @GetMapping(path = "/ingresosTotales")
    public @ResponseBody double ingresosTotales(@RequestParam int id) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(id);

        if (ejecucionPresupuestal.isPresent()) {
            return ejecucionPresupuestal.get().getIngresosTotalesEjecucion();
        } else {
            return 0;
        }
    }

    // Se utiliza para actualizar el atributo egresosProgramaTotales de la clase
    // EjecucionPresupuestal
    // Cuando se crea: antiguo valor será 0
    // Cuando se modifica: antiguo valor será el valor que se quiere modificar y
    // nuevo valor será el valor nuevo
    // Cuando se elimina: nuevo valor será 0
    public String actualizarEgresosProgramaTotales(int id,
            double nuevoValor, double antiguoValor) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(id);

        if (ejecucionPresupuestal.isPresent()) {

            ejecucionPresupuestal.get().setEgresosProgramaTotalesEjecucion(
                    ejecucionPresupuestal.get().getEgresosProgramaTotalesEjecucion() - antiguoValor + nuevoValor);

            actualizarBalanceGeneral(id);

            ejecucionPresupuestalRepository.save(ejecucionPresupuestal.get());
            return "OK";
        } else {
            return "Error: Ejecucion presupuestal no encontrada";
        }

    }

    // Se utiliza para actualizar el atributo egresosProgramaTotales de la clase
    // EjecucionPresupuestal
    // Cuando se crea: antiguo valor será 0
    // Cuando se modifica: antiguo valor será el valor que se quiere modificar y
    // nuevo valor será el valor nuevo
    // Cuando se elimina: nuevo valor será 0
    public String actualizarEgresosRecurrentesUniversidadTotales(int id,
            double nuevoValor, double antiguoValor) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(id);

        if (ejecucionPresupuestal.isPresent()) {
            ejecucionPresupuestal.get().setEgresosRecurrentesUniversidadTotalesEjecucion(
                    ejecucionPresupuestal.get().getEgresosRecurrentesUniversidadTotalesEjecucion() - antiguoValor
                            + nuevoValor);

            actualizarBalanceGeneral(id);

            ejecucionPresupuestalRepository.save(ejecucionPresupuestal.get());
            return "OK";
        } else {
            return "Error: Ejecucion presupuestal no encontrada";
        }
    }

    // Cuando se crea un ejecucionPresupuestal, ingresos y los egresos tienen un
    // valor de 0,
    // por lo que no habrán excepciones al momento de realizar la operación
    public String actualizarBalanceGeneral(int id) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(id);

        if (ejecucionPresupuestal.isPresent()) {
            ejecucionPresupuestal.get()
                    .setBalanceGeneralEjecucion(ejecucionPresupuestal.get().getIngresosTotalesEjecucion()
                            - ejecucionPresupuestal.get().getEgresosProgramaTotalesEjecucion()
                            - ejecucionPresupuestal.get().getEgresosRecurrentesUniversidadTotalesEjecucion());

            // Hay un error acá
            ejecucionPresupuestalRepository.save(ejecucionPresupuestal.get());
            return "OK";
        } else {
            return "Error: Ejecucion presupuestal no encontrada";
        }
    }

}
