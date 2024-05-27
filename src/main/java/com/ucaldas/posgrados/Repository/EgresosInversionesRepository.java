package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.EgresosInversiones;

public interface EgresosInversionesRepository extends CrudRepository<EgresosInversiones, Integer> {

    Iterable<EgresosInversiones> findAllByOrderByPresupuestoAsc();

    Iterable<EgresosInversiones> findByPresupuestoId(int idPresupuesto);

    Iterable<EgresosInversiones> findByEjecucionPresupuestalId(int idEjecucionPresupuestal);

}
