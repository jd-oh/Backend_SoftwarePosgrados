package com.ucaldas.posgrados.Entity;

import jakarta.persistence.Entity;
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
public class EgresosRecurrentesAdm extends RegistroFinanciero {

    @Getter
    private final static String rubro = "Recurrentes Administrativos";
    private String unidad;
    private String cargo;

    private double valorHora;

    private int numHoras;

    private double valorTotal;

}
