package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.EgresosOtrosServDocentes;

public interface EgresosOtrosServDocentesRepository extends CrudRepository<EgresosOtrosServDocentes, Integer> {

    Iterable<EgresosOtrosServDocentes> findAllByOrderByPresupuestoAsc();

    Iterable<EgresosOtrosServDocentes> findByPresupuestoId(int idPresupuesto);

}
