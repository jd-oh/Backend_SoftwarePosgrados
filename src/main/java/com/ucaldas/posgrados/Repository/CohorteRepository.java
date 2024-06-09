package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.Cohorte;

public interface CohorteRepository extends CrudRepository<Cohorte, Integer> {

    Iterable<Cohorte> findAllByOrderByNumeroAsc();

    Iterable<Cohorte> findAllByOrderByFechaDesc();

    Iterable<Cohorte> findByProgramaId(int idPrograma);

}
