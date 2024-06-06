package com.ucaldas.posgrados.DTO;

import java.util.Set;

import com.ucaldas.posgrados.Entity.RegistroFinanciero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CdpDTO {

    private Long id;
    private String numero;
    private String fecha;
    private String vigencia;
    private String valor;
    private String estado;
    private String tipo;
    private String descripcion;
    private String observaciones;
    private String fechaCreacion;
    private String fechaModificacion;
    private String usuarioCreacion;
    private String usuarioModificacion;
    private String idProyecto;
    private String idProveedor;
    private String idRubro;
    private String idFuente;
    private String idSolicitud;
    private String idContrato;
    private String idOrden;
    private String idFactura;
    private String idPago;
    private String idCuenta;
    private String idCuentaCdp;
    private String idCuentaCdpPago;
    private Set<RegistroFinanciero> registrosFinancieros;

}
