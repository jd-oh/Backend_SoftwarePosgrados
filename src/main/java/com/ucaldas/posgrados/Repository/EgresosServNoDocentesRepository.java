package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.EgresosServNoDocentes;

public interface EgresosServNoDocentesRepository extends CrudRepository<EgresosServNoDocentes, Integer> {

    Iterable<EgresosServNoDocentes> findAllByOrderByPresupuestoAsc();

    Iterable<EgresosServNoDocentes> findByPresupuestoId(int idPresupuesto);

    Iterable<EgresosServNoDocentes> findByEjecucionPresupuestalId(int idEjecucionPresupuestal);

}
