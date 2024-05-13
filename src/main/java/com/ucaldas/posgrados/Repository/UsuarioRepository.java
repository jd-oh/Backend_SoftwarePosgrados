package com.ucaldas.posgrados.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ucaldas.posgrados.Entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(String username);

}
