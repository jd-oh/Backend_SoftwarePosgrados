package com.ucaldas.posgrados.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class EgresosDescuentos extends RegistroFinanciero {

    private int numEstudiantes;
    private double valor;
    private int numPeriodos;
    private double totalDescuento;

    @ManyToOne
    @JoinColumn(name = "idTipoDescuento", referencedColumnName = "id")
    private TipoDescuento tipoDescuento;

}
