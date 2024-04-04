package com.ucaldas.posgrados.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class EgresosDescuentos {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int numEstudiantes;
    private double valor;
    private int numPeriodos;
    private double totalDescuento;

    @ManyToOne
    @JoinColumn(name = "idPresupuesto", referencedColumnName = "id")
    @JsonBackReference
    private Presupuesto presupuesto;

    @ManyToOne
    @JoinColumn(name = "idEjecucionPresupuestal", referencedColumnName = "id")
    private EjecucionPresupuestal ejecucionPresupuestal;

    @ManyToOne
    @JoinColumn(name = "idTipoDescuento", referencedColumnName = "id")
    private TipoDescuento tipoDescuento;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumEstudiantes() {
        return numEstudiantes;
    }

    public void setNumEstudiantes(int numEstudiantes) {
        this.numEstudiantes = numEstudiantes;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getNumPeriodos() {
        return numPeriodos;
    }

    public void setNumPeriodos(int numPeriodos) {
        this.numPeriodos = numPeriodos;
    }

    public double getTotalDescuento() {
        return totalDescuento;
    }

    public void setTotalDescuento(double totalDescuento) {
        this.totalDescuento = totalDescuento;
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

    public TipoDescuento getTipoDescuento() {
        return tipoDescuento;
    }

    public void setTipoDescuento(TipoDescuento tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

}
