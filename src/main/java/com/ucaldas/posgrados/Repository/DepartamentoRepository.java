package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.Departamento;

public interface DepartamentoRepository extends CrudRepository<Departamento, Integer> {

    Iterable<Departamento> findAllByOrderByNombreAsc();

}
