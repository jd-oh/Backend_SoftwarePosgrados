package com.ucaldas.posgrados.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
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
    @JsonBackReference
    private EjecucionPresupuestal ejecucionPresupuestal;

    @ManyToOne
    @JoinColumn(name = "idTipoDescuento", referencedColumnName = "id")
    private TipoDescuento tipoDescuento;

    private String fechaHoraCreacion;

    private String fechaHoraUltimaModificacion;

    private EtiquetaEgresoIngreso etiquetaEgresoIngreso;

}
