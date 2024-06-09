package com.ucaldas.posgrados.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TotalEjecucionResponse {

    double totalDescuentos;
    double totalIngresos;
    double totalServDocentes;
    double totalGenerales;
    double totalOtros;
    double totalOtrosServDocentes;
    double totalInversiones;
    double totalViajes;
    double totalServNoDocentes;
    double totalRecurrentesAdm;
    double totalTransferencias;

}
