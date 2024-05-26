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
public class EgresosOtrosServDocentes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String servicio;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "idPresupuesto", referencedColumnName = "id")
    @JsonBackReference
    private Presupuesto presupuesto;

    @ManyToOne
    @JoinColumn(name = "idEjecucionPresupuestal", referencedColumnName = "id")
    @JsonBackReference
    private EjecucionPresupuestal ejecucionPresupuestal;

    @ManyToOne
    @JoinColumn(name = "idTipoCompensacion", referencedColumnName = "id")
    private TipoCompensacion tipoCompensacion;

    @ManyToOne
    @JoinColumn(name = "idTipoCosto", referencedColumnName = "id")
    private TipoCosto tipoCosto;

    private int numHoras;

    private double valorTotal;

    private String fechaHoraCreacion;

    private String fechaHoraUltimaModificacion;

    private EtiquetaEgresoIngreso etiquetaEgresoIngreso;
}
