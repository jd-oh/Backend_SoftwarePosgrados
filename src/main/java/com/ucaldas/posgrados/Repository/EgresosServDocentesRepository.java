package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.EgresosServDocentes;

public interface EgresosServDocentesRepository extends CrudRepository<EgresosServDocentes, Integer> {

    Iterable<EgresosServDocentes> findAllByOrderByPresupuestoAsc();

    Iterable<EgresosServDocentes> findByPresupuestoId(int idPresupuesto);

}
