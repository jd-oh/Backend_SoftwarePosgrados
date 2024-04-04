package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.TipoInversion;

public interface TipoInversionRepository extends CrudRepository<TipoInversion, Integer> {

    Iterable<TipoInversion> findAllByOrderByNombreTipoAsc();

}
