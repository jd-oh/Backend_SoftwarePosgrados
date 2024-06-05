package com.ucaldas.posgrados.DTO;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    String nombre;
    String apellido;
    String email;
    String username;
    String password;
    int idRol;
    Integer idFacultad;
    Integer idPrograma;

}
