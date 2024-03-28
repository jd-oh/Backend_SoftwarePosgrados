package com.ucaldas.posgrados.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class EgresosInversiones {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String concepto;
    private double valor;

    @ManyToOne
    @JoinColumn(name = "idPresupuesto", referencedColumnName = "id")
    private Presupuesto presupuesto;

    @ManyToOne
    @JoinColumn(name = "idEjecucionPresupuestal", referencedColumnName = "id")
    private EjecucionPresupuestal ejecucionPresupuestal;

    @ManyToOne
    @JoinColumn(name = "idTipoInversion", referencedColumnName = "id")
    private TipoInversion tipoInversion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Presupuesto getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(Presupuesto presupuesto) {
        this.presupuesto = presupuesto;
    }

    public EjecucionPresupuestal getEjecucionPresupuestal() {
        return ejecucionPresupuestal;
    }

    public void setEjecucionPresupuestal(EjecucionPresupuestal ejecucionPresupuestal) {
        this.ejecucionPresupuestal = ejecucionPresupuestal;
    }

    public TipoInversion getTipoInversion() {
        return tipoInversion;
    }

    public void setTipoInversion(TipoInversion tipoInversion) {
        this.tipoInversion = tipoInversion;
    }

}
