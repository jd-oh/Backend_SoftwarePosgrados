package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.TipoDescuento;

public interface TipoDescuentoRepository extends CrudRepository<TipoDescuento, Integer> {

    Iterable<TipoDescuento> findAllByOrderByNombreTipoAsc();

}
