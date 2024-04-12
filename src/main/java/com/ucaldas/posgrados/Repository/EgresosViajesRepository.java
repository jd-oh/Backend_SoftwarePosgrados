package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.EgresosViajes;

public interface EgresosViajesRepository extends CrudRepository<EgresosViajes, Integer> {

    Iterable<EgresosViajes> findAllByOrderByPresupuestoAsc();

    Iterable<EgresosViajes> findByPresupuestoId(int idPresupuesto);

}
