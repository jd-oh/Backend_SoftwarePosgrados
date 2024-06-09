package com.ucaldas.posgrados.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class EgresoInversionCDP extends EgresoCDP {

    @ManyToOne
    @JoinColumn(name = "idEgresoInversion", referencedColumnName = "id")
    private EgresosInversiones egresoInversion;
}
