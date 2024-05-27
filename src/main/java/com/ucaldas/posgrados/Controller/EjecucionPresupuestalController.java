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

}
