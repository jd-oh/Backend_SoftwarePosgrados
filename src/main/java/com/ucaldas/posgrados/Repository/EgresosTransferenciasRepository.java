package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.EgresosTransferencias;

public interface EgresosTransferenciasRepository extends CrudRepository<EgresosTransferencias, Integer> {

    Iterable<EgresosTransferencias> findAllByOrderByPresupuestoAsc();

    Iterable<EgresosTransferencias> findByPresupuestoId(int idPresupuesto);

    Iterable<EgresosTransferencias> findByEjecucionPresupuestalId(int idEjecucionPresupuestal);

}
