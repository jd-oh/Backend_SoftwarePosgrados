package com.ucaldas.posgrados.Repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ucaldas.posgrados.Entity.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    Usuario findByFacultadNombreAndRolNombre(String nombreFacultad, String string);

}
