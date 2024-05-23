package com.ucaldas.posgrados.Entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Presupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne
    @JoinColumn(name = "idCohorte", referencedColumnName = "id")
    private Cohorte cohorte;

    private String estado;

    private String observaciones;

    // Son todos los ingresos menos los descuentos
    private double ingresosTotales;

    private String fechaHoraCreacion;

    private String fechaHoraUltimaModificacion;

    private String fechaHoraEnviadoRevision;

    private String fechaHoraAprobado;

    // Incluye gastos personales(ServDocentes parcial, ServNoDocentes total,
    // OtrosServDocentes total)
    // gastos generales, otros gastos, transferencias y gastos de viaje
    private double egresosProgramaTotales;

    // Incluye gastos de inversiones, recurrentes adm y gastos personales
    // (ServDocentes parcial)
    private double egresosRecurrentesUniversidadTotales;

    private double balanceGeneral;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosDescuentos> egresosDescuentos;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Ingresos> ingresos;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosInversiones> egresosInversiones;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosOtros> egresosOtros;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosOtrosServDocentes> egresosOtrosServDocentes;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosServDocentes> egresosServDocentes;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosServNoDocentes> egresosServNoDocentes;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosGenerales> egresosGenerales;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosRecurrentesAdm> egresosRecurrentesAdm;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosViajes> egresosViaje;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EgresosTransferencias> egresosTransferencias;

}
