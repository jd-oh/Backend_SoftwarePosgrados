package com.ucaldas.posgrados.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
/*
 * Esta entidad se creó para agregarle los atributos descripcion y cpc a cada
 * Egreso que se vaya a usar en un CDP
 */

public abstract class EgresoCDP {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String descripcion;
    private String cpc;

    @ManyToOne
    @JoinColumn(name = "idCdp", referencedColumnName = "id")
    @JsonBackReference
    private Cdp cdp;

}
