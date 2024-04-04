package com.ucaldas.posgrados.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class EgresosViajes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String descripcion;
    private int numPersonas;

    @ManyToOne
    @JoinColumn(name = "idPresupuesto", referencedColumnName = "id")
    private Presupuesto presupuesto;

    @ManyToOne
    @JoinColumn(name = "idEjecucionPresupuestal", referencedColumnName = "id")
    private EjecucionPresupuestal ejecucionPresupuestal;

    private double valorTransporte;

    private double apoyoDesplazamiento;

    private int numViajesPorPersona;

    private double valorTotal;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getValorTransporte() {
        return valorTransporte;
    }

    public void setValorTransporte(double valorTransporte) {
        this.valorTransporte = valorTransporte;
    }

    public int getNumPersonas() {
        return numPersonas;
    }

    public void setNumPersonas(int numPersonas) {
        this.numPersonas = numPersonas;
    }

    public double getApoyoDesplazamiento() {
        return apoyoDesplazamiento;
    }

    public void setApoyoDesplazamiento(double apoyoDesplazamiento) {
        this.apoyoDesplazamiento = apoyoDesplazamiento;
    }

    public int getNumViajesPorPersona() {
        return numViajesPorPersona;
    }

    public void setNumViajesPorPersona(int numViajesPorPersona) {
        this.numViajesPorPersona = numViajesPorPersona;
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

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

}
