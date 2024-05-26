package com.ucaldas.posgrados.Entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EjecucionPresupuestal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "idPresupuesto", referencedColumnName = "id")
    private Presupuesto presupuesto;

    // Son todos los ingresos menos los descuentos
    private double ingresosTotales;

    // Incluye gastos personales(ServDocentes parcial, ServNoDocentes total,
    // OtrosServDocentes total)
    // gastos generales, otros gastos, transferencias y gastos de viaje
    private double egresosProgramaTotales;

    // Incluye gastos de inversiones, recurrentes adm y gastos personales
    // (ServDocentes parcial)
    private double egresosRecurrentesUniversidadTotales;

    private double balanceGeneral;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosDescuentos> egresosDescuentosEjecucion;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<Ingresos> ingresosEjecucion;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosInversiones> egresosInversionesEjecucion;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosOtros> egresosOtrosEjecucion;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosOtrosServDocentes> egresosOtrosServDocentesEjecucion;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosServDocentes> egresosServDocentesEjecucion;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosServNoDocentes> egresosServNoDocentesEjecucion;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosGenerales> egresosGeneralesEjecucion;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosRecurrentesAdm> egresosRecurrentesAdmEjecucion;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosViajes> egresosViajeEjecucion;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosTransferencias> egresosTransferenciasEjecucion;

}
