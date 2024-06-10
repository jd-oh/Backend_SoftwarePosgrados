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
public class EgresosServDocentes extends RegistroFinanciero {

    @Getter
    private final static String rubro = "Servicios Docentes";
    private String nombreMateria;

    private boolean esDocentePlanta;

    private String nombreDocente;

    @ManyToOne
    @JoinColumn(name = "idTipoCompensacion", referencedColumnName = "id")
    private TipoCompensacion tipoCompensacion;

    private String escalafon;

    private String titulo;

    private int horasTeoricasMat;

    private int horasPracticasMat;

    private int totalHorasProfesor;

    private double valorHoraProfesor;

    private double totalPagoProfesor;

}
