package com.ucaldas.posgrados.Repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.EjecucionPresupuestal;

public interface EjecucionPresupuestalRepository extends CrudRepository<EjecucionPresupuestal, Integer> {

    Iterable<EjecucionPresupuestal> findAllByOrderByPresupuestoAsc();

    Optional<EjecucionPresupuestal> findByPresupuestoId(int idPresupuesto);

    Iterable<EjecucionPresupuestal> findByPresupuestoFacultadId(int idFacultad);

    Iterable<EjecucionPresupuestal> findByPresupuestoProgramaId(int idPrograma);
}
