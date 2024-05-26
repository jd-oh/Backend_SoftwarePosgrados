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
import com.ucaldas.posgrados.Entity.EgresosGenerales;
import com.ucaldas.posgrados.Entity.EgresosDescuentos;
import com.ucaldas.posgrados.Entity.EgresosTransferencias;
import com.ucaldas.posgrados.Entity.EgresosInversiones;
import com.ucaldas.posgrados.Entity.EgresosOtros;
import com.ucaldas.posgrados.Entity.EgresosServDocentes;
import com.ucaldas.posgrados.Entity.EgresosServNoDocentes;
import com.ucaldas.posgrados.Entity.EgresosOtrosServDocentes;
import com.ucaldas.posgrados.Entity.EgresosViajes;
import com.ucaldas.posgrados.Entity.EgresosRecurrentesAdm;
import com.ucaldas.posgrados.Entity.Ingresos;

import com.ucaldas.posgrados.Repository.EjecucionPresupuestalRepository;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;

import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

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
            if (ejecucionPresupuestalRepository.findByPresupuesto(presupuesto.get()).isPresent()) {
                return "Ya existe una ejecución presupuestal para este presupuesto";
            }

            EjecucionPresupuestal ejecucionPresupuestal = new EjecucionPresupuestal();
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

    @GetMapping("/listarEgresosGenerales")
    public @ResponseBody Set<EgresosGenerales> listarEgresosGenerales(
            @RequestParam int idEjecucionPresupuestal) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findById(idEjecucionPresupuestal);

        if (ejecucionPresupuestal.isPresent()) {
            Presupuesto presupuesto = ejecucionPresupuestal.get().getPresupuesto();
            return presupuesto.getEgresosGenerales();
        } else {
            return null;
        }
    }

    @GetMapping("/listarEgresosDescuentos")
    public @ResponseBody Set<EgresosDescuentos> listarEgresosDescuentos(@RequestParam int idEjecucionPresupuestal) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findById(idEjecucionPresupuestal);

        if (ejecucionPresupuestal.isPresent()) {
            Presupuesto presupuesto = ejecucionPresupuestal.get().getPresupuesto();
            return presupuesto.getEgresosDescuentos();
        } else {
            return null;
        }

    }

    @GetMapping("/listarEgresosTransferencias")
    public @ResponseBody Set<EgresosTransferencias> listarEgresosTransferencias(
            @RequestParam int idEjecucionPresupuestal) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findById(idEjecucionPresupuestal);

        if (ejecucionPresupuestal.isPresent()) {
            Presupuesto presupuesto = ejecucionPresupuestal.get().getPresupuesto();
            return presupuesto.getEgresosTransferencias();
        } else {
            return null;
        }

    }

    @GetMapping("/listarEgresosInversiones")
    public @ResponseBody Set<EgresosInversiones> listarEgresosInversiones(@RequestParam int idEjecucionPresupuestal) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findById(idEjecucionPresupuestal);

        if (ejecucionPresupuestal.isPresent()) {
            Presupuesto presupuesto = ejecucionPresupuestal.get().getPresupuesto();
            return presupuesto.getEgresosInversiones();
        } else {
            return null;
        }

    }

    @GetMapping("/listarEgresosOtros")
    public @ResponseBody Set<EgresosOtros> listarEgresosOtros(@RequestParam int idEjecucionPresupuestal) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findById(idEjecucionPresupuestal);

        if (ejecucionPresupuestal.isPresent()) {
            Presupuesto presupuesto = ejecucionPresupuestal.get().getPresupuesto();
            return presupuesto.getEgresosOtros();
        } else {
            return null;
        }
    }

    @GetMapping("/listarEgresosServDocentes")
    public @ResponseBody Set<EgresosServDocentes> listarEgresosServDocentes(@RequestParam int idEjecucionPresupuestal) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findById(idEjecucionPresupuestal);

        if (ejecucionPresupuestal.isPresent()) {
            Presupuesto presupuesto = ejecucionPresupuestal.get().getPresupuesto();
            return presupuesto.getEgresosServDocentes();
        } else {
            return null;
        }
    }

    @GetMapping("/listarEgresosServNoDocentes")
    public @ResponseBody Set<EgresosServNoDocentes> listarEgresosServNoDocentes(
            @RequestParam int idEjecucionPresupuestal) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findById(idEjecucionPresupuestal);

        if (ejecucionPresupuestal.isPresent()) {
            Presupuesto presupuesto = ejecucionPresupuestal.get().getPresupuesto();
            return presupuesto.getEgresosServNoDocentes();
        } else {
            return null;
        }
    }

    @GetMapping("/listarEgresosOtrosServDocentes")
    public @ResponseBody Set<EgresosOtrosServDocentes> listarEgresosOtrosServDocentes(
            @RequestParam int idEjecucionPresupuestal) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findById(idEjecucionPresupuestal);

        if (ejecucionPresupuestal.isPresent()) {
            Presupuesto presupuesto = ejecucionPresupuestal.get().getPresupuesto();
            return presupuesto.getEgresosOtrosServDocentes();
        } else {
            return null;
        }
    }

    @GetMapping("/listarEgresosViajes")
    public @ResponseBody Set<EgresosViajes> listarEgresosViajes(@RequestParam int idEjecucionPresupuestal) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findById(idEjecucionPresupuestal);

        if (ejecucionPresupuestal.isPresent()) {
            Presupuesto presupuesto = ejecucionPresupuestal.get().getPresupuesto();
            return presupuesto.getEgresosViaje();
        } else {
            return null;
        }
    }

    @GetMapping("/listarEgresosRecurrentesAdm")
    public @ResponseBody Set<EgresosRecurrentesAdm> listarEgresosRecurrentesAdm(
            @RequestParam int idEjecucionPresupuestal) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findById(idEjecucionPresupuestal);

        if (ejecucionPresupuestal.isPresent()) {
            Presupuesto presupuesto = ejecucionPresupuestal.get().getPresupuesto();
            return presupuesto.getEgresosRecurrentesAdm();
        } else {
            return null;
        }
    }

    @GetMapping("/listarIngresos")
    public @ResponseBody Set<Ingresos> listarIngresos(@RequestParam int idEjecucionPresupuestal) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findById(idEjecucionPresupuestal);

        if (ejecucionPresupuestal.isPresent()) {
            Presupuesto presupuesto = ejecucionPresupuestal.get().getPresupuesto();
            return presupuesto.getIngresos();
        } else {
            return null;
        }
    }

}
