package com.ucaldas.posgrados.Repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.EjecucionPresupuestal;
import com.ucaldas.posgrados.Entity.Presupuesto;

public interface EjecucionPresupuestalRepository extends CrudRepository<EjecucionPresupuestal, Integer> {

    Iterable<EjecucionPresupuestal> findAllByOrderByPresupuestoAsc();

    Optional<Presupuesto> findByPresupuesto(Presupuesto presupuesto);
}
