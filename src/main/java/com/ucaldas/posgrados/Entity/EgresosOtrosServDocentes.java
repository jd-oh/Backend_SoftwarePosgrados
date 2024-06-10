package com.ucaldas.posgrados.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data

@EqualsAndHashCode(callSuper = true)
public class EgresosOtrosServDocentes extends RegistroFinanciero {

    @Getter
    private final static String rubro = "Otros Servicios Docentes";
    private String servicio;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "idTipoCompensacion", referencedColumnName = "id")
    private TipoCompensacion tipoCompensacion;

    @ManyToOne
    @JoinColumn(name = "idTipoCosto", referencedColumnName = "id")
    private TipoCosto tipoCosto;

    private int numHoras;

    private double valorTotal;

}
