package com.ucaldas.posgrados.Entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Cdp {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private int id;

    @Getter
    @Setter
    private String rubro;

    @Getter
    @Setter
    // Se usa para saber si está en revisión o aprobado
    private String estado;

    @Getter
    @Setter
    private double valorTotal;

    @Getter
    @Setter
    private String fechaHoraCreacion;

    @Getter
    @Setter
    private String fechaHoraEnviadoRevision;

    @Getter
    @Setter
    private String fechaHoraAprobado;

    @OneToMany(mappedBy = "cdp")
    @JsonManagedReference
    private Set<EgresoCDP> egresosCDP;

    @ManyToOne
    @JoinColumn(name = "idEjecucionPresupuestal", referencedColumnName = "id")
    @JsonBackReference
    @Getter
    @Setter
    private EjecucionPresupuestal ejecucionPresupuestal;

    public Set<EgresoCDP> getEgresosCDP() {
        return egresosCDP;
    }

    public void setEgresosCDP(Set<EgresoCDP> egresosCDP) {
        this.egresosCDP = egresosCDP;
    }

}
