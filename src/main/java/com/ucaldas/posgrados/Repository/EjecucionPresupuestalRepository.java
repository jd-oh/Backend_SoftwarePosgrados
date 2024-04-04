package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.EjecucionPresupuestal;

public interface EjecucionPresupuestalRepository extends CrudRepository<EjecucionPresupuestal, Integer> {

    Iterable<EjecucionPresupuestal> findAllByOrderByPresupuestoAsc();
}
