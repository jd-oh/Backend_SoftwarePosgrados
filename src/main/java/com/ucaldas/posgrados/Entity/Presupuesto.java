package com.ucaldas.posgrados.Entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Presupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne
    @JoinColumn(name = "idCohorte", referencedColumnName = "id")
    private Cohorte cohorte;

    private String estado;

    private String observaciones;

    private double ingresosTotales;

    // Incluye gastos personales(ServDocentes parcial, ServNoDocentes total,
    // OtrosServDocentes total)
    // gastos generales, otros gastos y gastos de viaje
    private double egresosProgramaTotales;

    // Incluye gastos de inversiones, recurrentes adm y gastos personales
    // (ServDocentes, ServNoDocentes, OtrosServDocentes)
    private double egresosRecurrentesUniversidadTotales;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosDescuentos> egresosDescuentos;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Ingresos> ingresos;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosInversiones> egresosInversiones;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosOtros> egresosOtros;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosOtrosServDocentes> egresosOtrosServDocentes;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosServDocentes> egresosServDocentes;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosServNoDocentes> egresosServNoDocentes;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosGenerales> egresosGenerales;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosRecurrentesAdm> egresosRecurrentesAdm;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosViajes> egresosViaje;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosTransferencias> egresosTransferencias;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cohorte getCohorte() {
        return cohorte;
    }

    public void setCohorte(Cohorte cohorte) {
        this.cohorte = cohorte;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public double getIngresosTotales() {
        return ingresosTotales;
    }

    public void setIngresosTotales(double ingresosTotales) {
        this.ingresosTotales = ingresosTotales;
    }

    public double getEgresosProgramaTotales() {
        return egresosProgramaTotales;
    }

    public void setEgresosProgramaTotales(double egresosTotales) {
        this.egresosProgramaTotales = egresosTotales;
    }

    public double getEgresosRecurrentesUniversidadTotales() {
        return egresosRecurrentesUniversidadTotales;
    }

    public void setEgresosRecurrentesUniversidadTotales(double egresosRecurrentesTotales) {
        this.egresosRecurrentesUniversidadTotales = egresosRecurrentesTotales;
    }

    public Set<EgresosDescuentos> getEgresosDescuentos() {
        return egresosDescuentos;
    }

    public void setEgresosDescuentos(Set<EgresosDescuentos> egresosDescuentos) {
        this.egresosDescuentos = egresosDescuentos;
    }

    public Set<Ingresos> getIngresos() {
        return ingresos;
    }

    public void setIngresos(Set<Ingresos> ingresos) {
        this.ingresos = ingresos;
    }

    public Set<EgresosInversiones> getEgresosInversiones() {
        return egresosInversiones;
    }

    public void setEgresosInversiones(Set<EgresosInversiones> egresosInversiones) {
        this.egresosInversiones = egresosInversiones;
    }

    public Set<EgresosOtros> getEgresosOtros() {
        return egresosOtros;
    }

    public void setEgresosOtros(Set<EgresosOtros> egresosOtros) {
        this.egresosOtros = egresosOtros;
    }

    public Set<EgresosOtrosServDocentes> getEgresosOtrosServDocentes() {
        return egresosOtrosServDocentes;
    }

    public void setEgresosOtrosServDocentes(Set<EgresosOtrosServDocentes> egresosOtrosServDocentes) {
        this.egresosOtrosServDocentes = egresosOtrosServDocentes;
    }

    public Set<EgresosServDocentes> getEgresosServDocentes() {
        return egresosServDocentes;
    }

    public void setEgresosServDocentes(Set<EgresosServDocentes> egresosServDocentes) {
        this.egresosServDocentes = egresosServDocentes;
    }

    public Set<EgresosServNoDocentes> getEgresosServNoDocentes() {
        return egresosServNoDocentes;
    }

    public void setEgresosServNoDocentes(Set<EgresosServNoDocentes> egresosServNoDocentes) {
        this.egresosServNoDocentes = egresosServNoDocentes;
    }

    public Set<EgresosGenerales> getEgresosGenerales() {
        return egresosGenerales;
    }

    public void setEgresosGenerales(Set<EgresosGenerales> egresosGenerales) {
        this.egresosGenerales = egresosGenerales;
    }

    public Set<EgresosRecurrentesAdm> getEgresosRecurrentesAdm() {
        return egresosRecurrentesAdm;
    }

    public void setEgresosRecurrentesAdm(Set<EgresosRecurrentesAdm> egresosRecurrentesAdm) {
        this.egresosRecurrentesAdm = egresosRecurrentesAdm;
    }

    public Set<EgresosViajes> getEgresosViaje() {
        return egresosViaje;
    }

    public void setEgresosViaje(Set<EgresosViajes> egresosViaje) {
        this.egresosViaje = egresosViaje;
    }

    public Set<EgresosTransferencias> getEgresosTransferencias() {
        return egresosTransferencias;
    }

    public void setEgresosTransferencias(Set<EgresosTransferencias> egresosTransferencias) {
        this.egresosTransferencias = egresosTransferencias;
    }

}
