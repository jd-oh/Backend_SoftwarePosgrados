package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.Cdp;

public interface CdpRepository extends CrudRepository<Cdp, Integer> {

    Iterable<Cdp> findByEjecucionPresupuestalId(int ejecucionPresupuestal);

}
