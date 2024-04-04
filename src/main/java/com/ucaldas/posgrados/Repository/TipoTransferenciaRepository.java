package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.TipoTransferencia;

public interface TipoTransferenciaRepository extends CrudRepository<TipoTransferencia, Integer> {

    Iterable<TipoTransferencia> findAllByOrderByNombreTipoAsc();

}
