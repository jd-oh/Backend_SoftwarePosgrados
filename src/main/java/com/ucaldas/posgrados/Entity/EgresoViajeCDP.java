package com.ucaldas.posgrados.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class EgresoViajeCDP extends EgresoCDP {

    @ManyToOne
    @JoinColumn(name = "idEgresoViaje", referencedColumnName = "id")
    private EgresosViajes egresoViaje;
}
