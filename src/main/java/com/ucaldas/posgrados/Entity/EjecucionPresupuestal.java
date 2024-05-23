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
    private Set<EgresosDescuentos> egresosDescuentos;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<Ingresos> ingresos;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosInversiones> egresosInversiones;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosOtros> egresosOtros;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosOtrosServDocentes> egresosOtrosServDocentes;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosServDocentes> egresosServDocentes;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosServNoDocentes> egresosServNoDocentes;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosGenerales> egresosGenerales;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosRecurrentesAdm> egresosRecurrentesAdm;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosViajes> egresosViaje;

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<EgresosTransferencias> egresosTransferencias;

}
