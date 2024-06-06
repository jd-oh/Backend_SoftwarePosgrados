package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.Ingresos;

public interface IngresosRepository extends CrudRepository<Ingresos, Integer> {

    Iterable<Ingresos> findAllByOrderByPresupuestoAsc();

    Iterable<Ingresos> findByPresupuestoId(int idPresupuesto);

    Iterable<Ingresos> findByEjecucionPresupuestalId(int idEjecucionPresupuestal);

}
