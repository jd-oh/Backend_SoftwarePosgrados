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
public class EgresosTransferencias extends RegistroFinanciero {

    @Getter
    private final static String rubro = "Transferencias";
    private String descripcion;
    private double porcentaje;

    @ManyToOne
    @JoinColumn(name = "idTipoTransferencia", referencedColumnName = "id")
    private TipoTransferencia tipoTransferencia;

    private double valorTotal;

}
