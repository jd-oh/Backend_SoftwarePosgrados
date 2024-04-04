package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.EgresosOtros;

public interface EgresosOtrosRepository extends CrudRepository<EgresosOtros, Integer> {

    Iterable<EgresosOtros> findAllByOrderByPresupuestoAsc();

}
