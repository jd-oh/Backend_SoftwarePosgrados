package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.TipoCompensacion;

public interface TipoCompensacionRepository extends CrudRepository<TipoCompensacion, Integer> {

    Iterable<TipoCompensacion> findAllByOrderByNombreTipoAsc();

}
