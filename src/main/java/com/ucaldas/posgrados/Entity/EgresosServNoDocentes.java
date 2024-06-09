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
public class EgresosServNoDocentes extends RegistroFinanciero {

    @Getter
    private final static String rubro = "Servicios No Docentes";
    private String servicio;
    private double valorUnitario;

    @ManyToOne
    @JoinColumn(name = "idTipoCosto", referencedColumnName = "id")
    private TipoCosto tipoCosto;

    private int cantidad;

    private double valorTotal;

}
