package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.EgresosDescuentos;

public interface EgresosDescuentosRepository extends CrudRepository<EgresosDescuentos, Integer> {

    Iterable<EgresosDescuentos> findAllByOrderByPresupuestoAsc();

    Iterable<EgresosDescuentos> findByPresupuestoId(int idPresupuesto);

}
