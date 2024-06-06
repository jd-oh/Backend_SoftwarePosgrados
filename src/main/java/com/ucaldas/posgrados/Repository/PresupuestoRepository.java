package com.ucaldas.posgrados.Repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.Presupuesto;

public interface PresupuestoRepository extends CrudRepository<Presupuesto, Integer> {

    Iterable<Presupuesto> findAllByOrderByEstadoAsc();

    Optional<Presupuesto> findByCohorteId(int idCohorte);

    Iterable<Presupuesto> findByEstado(String string);

    Iterable<Presupuesto> findByCohorteProgramaId(int idPrograma);

    Iterable<Presupuesto> findByCohorteProgramaFacultadId(int idFacultad);

}
