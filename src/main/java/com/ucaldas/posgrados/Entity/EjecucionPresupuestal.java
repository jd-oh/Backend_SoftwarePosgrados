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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor

public class EjecucionPresupuestal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private int id;

    @ManyToOne
    @JoinColumn(name = "idPresupuesto", referencedColumnName = "id")
    @Getter
    @Setter
    private Presupuesto presupuesto;

    // Son todos los ingresos menos los descuentos
    @Getter
    @Setter
    private double ingresosTotalesEjecucion;

    // Incluye gastos personales(ServDocentes parcial, ServNoDocentes total,
    // OtrosServDocentes total)
    // gastos generales, otros gastos, transferencias y gastos de viaje
    @Getter
    @Setter
    private double egresosProgramaTotalesEjecucion;

    // Incluye gastos de inversiones, recurrentes adm y gastos personales
    // (ServDocentes parcial)
    @Getter
    @Setter
    private double egresosRecurrentesUniversidadTotalesEjecucion;

    @Getter
    @Setter
    private double balanceGeneralEjecucion;

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

    // Getters y setters de los Set

    public Set<EgresosDescuentos> getEgresosDescuentosEjecucion() {
        return egresosDescuentosEjecucion;
    }

    public void setEgresosDescuentosEjecucion(Set<EgresosDescuentos> egresosDescuentosEjecucion) {
        this.egresosDescuentosEjecucion = egresosDescuentosEjecucion;
    }

    public Set<Ingresos> getIngresosEjecucion() {
        return ingresosEjecucion;
    }

    public void setIngresosEjecucion(Set<Ingresos> ingresosEjecucion) {
        this.ingresosEjecucion = ingresosEjecucion;
    }

    public Set<EgresosInversiones> getEgresosInversionesEjecucion() {
        return egresosInversionesEjecucion;
    }

    public void setEgresosInversionesEjecucion(Set<EgresosInversiones> egresosInversionesEjecucion) {
        this.egresosInversionesEjecucion = egresosInversionesEjecucion;
    }

    public Set<EgresosOtros> getEgresosOtrosEjecucion() {
        return egresosOtrosEjecucion;
    }

    public void setEgresosOtrosEjecucion(Set<EgresosOtros> egresosOtrosEjecucion) {
        this.egresosOtrosEjecucion = egresosOtrosEjecucion;
    }

    public Set<EgresosOtrosServDocentes> getEgresosOtrosServDocentesEjecucion() {
        return egresosOtrosServDocentesEjecucion;
    }

    public void setEgresosOtrosServDocentesEjecucion(Set<EgresosOtrosServDocentes> egresosOtrosServDocentesEjecucion) {
        this.egresosOtrosServDocentesEjecucion = egresosOtrosServDocentesEjecucion;
    }

    public Set<EgresosServDocentes> getEgresosServDocentesEjecucion() {
        return egresosServDocentesEjecucion;
    }

    public void setEgresosServDocentesEjecucion(Set<EgresosServDocentes> egresosServDocentesEjecucion) {
        this.egresosServDocentesEjecucion = egresosServDocentesEjecucion;
    }

    public Set<EgresosServNoDocentes> getEgresosServNoDocentesEjecucion() {
        return egresosServNoDocentesEjecucion;
    }

    public void setEgresosServNoDocentesEjecucion(Set<EgresosServNoDocentes> egresosServNoDocentesEjecucion) {
        this.egresosServNoDocentesEjecucion = egresosServNoDocentesEjecucion;
    }

    public Set<EgresosGenerales> getEgresosGeneralesEjecucion() {
        return egresosGeneralesEjecucion;
    }

    public void setEgresosGeneralesEjecucion(Set<EgresosGenerales> egresosGeneralesEjecucion) {
        this.egresosGeneralesEjecucion = egresosGeneralesEjecucion;
    }

    public Set<EgresosRecurrentesAdm> getEgresosRecurrentesAdmEjecucion() {
        return egresosRecurrentesAdmEjecucion;
    }

    public void setEgresosRecurrentesAdmEjecucion(Set<EgresosRecurrentesAdm> egresosRecurrentesAdmEjecucion) {
        this.egresosRecurrentesAdmEjecucion = egresosRecurrentesAdmEjecucion;
    }

    public Set<EgresosViajes> getEgresosViajeEjecucion() {
        return egresosViajeEjecucion;
    }

    public void setEgresosViajeEjecucion(Set<EgresosViajes> egresosViajeEjecucion) {
        this.egresosViajeEjecucion = egresosViajeEjecucion;
    }

    public Set<EgresosTransferencias> getEgresosTransferenciasEjecucion() {
        return egresosTransferenciasEjecucion;
    }

    public void setEgresosTransferenciasEjecucion(Set<EgresosTransferencias> egresosTransferenciasEjecucion) {
        this.egresosTransferenciasEjecucion = egresosTransferenciasEjecucion;
    }

}
