package com.ucaldas.posgrados.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class EgresosInversiones extends RegistroFinanciero {

    private String concepto;
    private double valor;

    @ManyToOne
    @JoinColumn(name = "idTipoInversion", referencedColumnName = "id")
    private TipoInversion tipoInversion;

}
