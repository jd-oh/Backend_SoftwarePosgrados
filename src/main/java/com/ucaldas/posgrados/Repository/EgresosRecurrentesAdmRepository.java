package com.ucaldas.posgrados.Repository;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.EgresosRecurrentesAdm;

public interface EgresosRecurrentesAdmRepository extends CrudRepository<EgresosRecurrentesAdm, Integer> {

    Iterable<EgresosRecurrentesAdm> findAllByOrderByPresupuestoAsc();

    Iterable<EgresosRecurrentesAdm> findByPresupuestoId(int idPresupuesto);

}
