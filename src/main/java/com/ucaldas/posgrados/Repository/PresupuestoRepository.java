package com.ucaldas.posgrados.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.Presupuesto;

public interface PresupuestoRepository extends CrudRepository<Presupuesto, Integer> {

    Iterable<Presupuesto> findAllByOrderByEstadoAsc();

    Optional<Presupuesto> findByCohorteId(int idCohorte);

    Iterable<Presupuesto> findByEstado(String string);

    Iterable<Presupuesto> findByCohorteProgramaId(int idPrograma);

    Iterable<Presupuesto> findByCohorteProgramaFacultadId(int idFacultad);

    Iterable<Presupuesto> findByCohorteProgramaFacultadIdAndEstado(int idFacultad, String estado);

    Iterable<Presupuesto> findByCohorteProgramaFacultadIdAndEstadoIn(int idFacultad, List<String> estados);

    Iterable<Presupuesto> findByCohorteProgramaIdAndEstado(int idPrograma, String string);

}
