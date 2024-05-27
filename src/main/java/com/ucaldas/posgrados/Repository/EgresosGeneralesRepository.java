package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.EgresosGenerales;

public interface EgresosGeneralesRepository extends CrudRepository<EgresosGenerales, Integer> {

    Iterable<EgresosGenerales> findAllByOrderByPresupuestoAsc();

    Iterable<EgresosGenerales> findByPresupuestoId(int idPresupuesto);

    Iterable<EgresosGenerales> findByEjecucionPresupuestalId(int idEjecucionPresupuestal);

}
