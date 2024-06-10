package com.ucaldas.posgrados.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class EgresoRecurrenteAdmCDP extends EgresoCDP {

    @ManyToOne
    @JoinColumn(name = "idEgresoRecurrenteAdm", referencedColumnName = "id")
    private EgresosRecurrentesAdm egresoRecurrenteAdm;
}
