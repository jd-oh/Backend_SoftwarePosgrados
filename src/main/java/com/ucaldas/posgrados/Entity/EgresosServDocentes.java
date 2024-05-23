package com.ucaldas.posgrados.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EgresosServDocentes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "idPresupuesto", referencedColumnName = "id")
    @JsonBackReference
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

    private String fechaHoraCreacion;

    private String fechaHoraUltimaModificacion;
}
