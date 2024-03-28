package com.ucaldas.posgrados.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class EgresosServDocentes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "idPresupuesto", referencedColumnName = "id")
    private Presupuesto presupuesto;

    @ManyToOne
    @JoinColumn(name = "idEjecucionPresupuestal", referencedColumnName = "id")
    private EjecucionPresupuestal ejecucionPresupuestal;

    private String nombreMateria;

    private boolean esDocentePlanta;

    private String nombreDocente;

    @ManyToOne
    @JoinColumn(name = "idTipoCompensacion", referencedColumnName = "id")
    private TipoCompensacion tipoCompensacion;

    private String escalafon;

    private String titulo;

    private int horasTeoricasMat;

    private int horasPracticasMat;

    private int totalHorasProfesor;

    private double valorHoraProfesor;

    private double totalPagoProfesor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getNombreMateria() {
        return nombreMateria;
    }

    public void setNombreMateria(String nombreMateria) {
        this.nombreMateria = nombreMateria;
    }

    public boolean isEsDocentePlanta() {
        return esDocentePlanta;
    }

    public void setEsDocentePlanta(boolean esDocentePlanta) {
        this.esDocentePlanta = esDocentePlanta;
    }

    public String getNombreDocente() {
        return nombreDocente;
    }

    public void setNombreDocente(String nombreDocente) {
        this.nombreDocente = nombreDocente;
    }

    public TipoCompensacion getTipoCompensacion() {
        return tipoCompensacion;
    }

    public void setTipoCompensacion(TipoCompensacion tipoCompensacion) {
        this.tipoCompensacion = tipoCompensacion;
    }

    public String getEscalafon() {
        return escalafon;
    }

    public void setEscalafon(String escalafon) {
        this.escalafon = escalafon;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getHorasTeoricasMat() {
        return horasTeoricasMat;
    }

    public void setHorasTeoricasMat(int horasTeoricasMat) {
        this.horasTeoricasMat = horasTeoricasMat;
    }

    public int getHorasPracticasMat() {
        return horasPracticasMat;
    }

    public void setHorasPracticasMat(int horasPracticasMat) {
        this.horasPracticasMat = horasPracticasMat;
    }

    public int getTotalHorasProfesor() {
        return totalHorasProfesor;
    }

    public void setTotalHorasProfesor(int totalHorasProfesor) {
        this.totalHorasProfesor = totalHorasProfesor;
    }

    public double getValorHoraProfesor() {
        return valorHoraProfesor;
    }

    public void setValorHoraProfesor(double valorHoraProfesor) {
        this.valorHoraProfesor = valorHoraProfesor;
    }

    public double getTotalPagoProfesor() {
        return totalPagoProfesor;
    }

    public void setTotalPagoProfesor(double totalPagoProfesor) {
        this.totalPagoProfesor = totalPagoProfesor;
    }
}
