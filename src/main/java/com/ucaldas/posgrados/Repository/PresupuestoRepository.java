package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.Presupuesto;

public interface PresupuestoRepository extends CrudRepository<Presupuesto, Integer> {

    Iterable<Presupuesto> findAllByOrderByEstadoAsc();

}
