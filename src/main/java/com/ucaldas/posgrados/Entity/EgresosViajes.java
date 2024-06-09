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
public class EgresosViajes extends RegistroFinanciero {

    @Getter
    private final static String rubro = "Viajes";
    private String descripcion;

    private int numPersonas;

    private double valorTransporte;

    private double apoyoDesplazamiento;

    private int numViajesPorPersona;

    private double valorTotal;

}
