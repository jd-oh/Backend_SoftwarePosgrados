package com.ucaldas.posgrados.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class EgresoDescuentoCDP extends EgresoCDP {

    @ManyToOne
    @JoinColumn(name = "idEgresoDescuento", referencedColumnName = "id")
    private EgresosDescuentos egresoDescuento;
}
