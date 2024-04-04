package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.TipoCosto;

public interface TipoCostoRepository extends CrudRepository<TipoCosto, Integer> {

    Iterable<TipoCosto> findAllByOrderByNombreTipoAsc();

}
