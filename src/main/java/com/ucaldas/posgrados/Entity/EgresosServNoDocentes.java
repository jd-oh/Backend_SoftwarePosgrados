package com.ucaldas.posgrados.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class EgresosServNoDocentes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String servicio;
    private double valorUnitario;

    @ManyToOne
    @JoinColumn(name = "idPresupuesto", referencedColumnName = "id")
    @JsonBackReference
    private Presupuesto presupuesto;

    @ManyToOne
    @JoinColumn(name = "idEjecucionPresupuestal", referencedColumnName = "id")
    private EjecucionPresupuestal ejecucionPresupuestal;

    @ManyToOne
    @JoinColumn(name = "idTipoCosto", referencedColumnName = "id")
    private TipoCosto tipoCosto;

    private int cantidad;

    private double valorTotal;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
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

    public TipoCosto getTipoCosto() {
        return tipoCosto;
    }

    public void setTipoCosto(TipoCosto tipoCosto) {
        this.tipoCosto = tipoCosto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

}
