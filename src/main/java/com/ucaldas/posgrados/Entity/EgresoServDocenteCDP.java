package com.ucaldas.posgrados.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class EgresoServDocenteCDP extends EgresoCDP {

    @ManyToOne
    @JoinColumn(name = "idEgresoServDocente", referencedColumnName = "id")
    private EgresosServDocentes egresoServDocente;
}
