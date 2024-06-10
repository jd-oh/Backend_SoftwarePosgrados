package com.ucaldas.posgrados.Entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CascadeType;
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

    @OneToMany(mappedBy = "ejecucionPresupuestal")
    @JsonManagedReference
    private Set<Cdp> cdps;

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

    @OneToMany(mappedBy = "ejecucionPresupuestal", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Ingresos> ingresosEjecucion;

    public Set<Cdp> getCdps() {
        return cdps;
    }

    public void setCdps(Set<Cdp> cdps) {
        this.cdps = cdps;
    }

    public Set<Ingresos> getIngresosEjecucion() {
        return ingresosEjecucion;
    }

    public void setIngresosEjecucion(Set<Ingresos> ingresosEjecucion) {
        this.ingresosEjecucion = ingresosEjecucion;
    }

}
