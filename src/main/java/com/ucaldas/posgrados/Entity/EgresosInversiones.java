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
public class EgresosInversiones extends RegistroFinanciero {

    @Getter
    private final static String rubro = "Inversiones";
    private String concepto;
    private double valor;

    @ManyToOne
    @JoinColumn(name = "idTipoInversion", referencedColumnName = "id")
    private TipoInversion tipoInversion;

}
