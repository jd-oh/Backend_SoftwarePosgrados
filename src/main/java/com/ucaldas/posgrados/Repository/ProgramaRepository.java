package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.Facultad;
import com.ucaldas.posgrados.Entity.Programa;

public interface ProgramaRepository extends CrudRepository<Programa, Integer> {

    Iterable<Programa> findAllByOrderByNombreAsc();

    Iterable<Programa> findAllByFacultad(Facultad facultad);

    Iterable<Programa> findAllByNombre(String nombrePrograma);

}
