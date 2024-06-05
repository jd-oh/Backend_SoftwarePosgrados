package com.ucaldas.posgrados.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ucaldas.posgrados.Jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final AuthenticationProvider authProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(csrf -> csrf
                                                .disable())
                                .authorizeHttpRequests(authRequest -> authRequest
                                                /* Permisos de autenticación */
                                                // Para estas no se necesita estar autenticado
                                                .requestMatchers("/autenticacion/login",
                                                                "/autenticacion/olvideMiPassword")
                                                .permitAll()
                                                // DIRECTOR, ADMIN Y DECANO
                                                .requestMatchers("/autenticacion/cambiarPassword",
                                                                "/autenticacion/refrescarToken")
                                                .hasAnyAuthority("ADMIN", "DECANO", "DIRECTOR")
                                                // ADMIN
                                                .requestMatchers("/autenticacion/registro").hasAuthority("ADMIN")

                                                /* Permisos de presupuesto */
                                                // DIRECTOR
                                                .requestMatchers("/presupuesto/crear",
                                                                "/presupuesto/actualizar",
                                                                "/presupuesto/enviarParaRevision",
                                                                "/presupuesto/enviarParaRevision",
                                                                "/presupuesto/eliminar,")
                                                .hasAuthority("DIRECTOR")
                                                // DECANO
                                                .requestMatchers("/presupuesto/aprobar",
                                                                "/presupuesto/listarPorFacultad")
                                                .hasAuthority("DECANO")
                                                // DIRECTOR Y DECANO
                                                .requestMatchers("/presupuesto/buscarPorCohorte",
                                                                "/presupuesto/listarPorPrograma",
                                                                "/presupuesto/ingresosTotales")
                                                .hasAnyAuthority("DECANO", "DIRECTOR")
                                                // ADMIN
                                                .requestMatchers("/presupuesto/**").hasAuthority("ADMIN")

                                                /* Permisos de Ejecución Presupuestal */
                                                // DIRECTOR
                                                .requestMatchers("/ejecucionPresupuestal/crear",
                                                                "/ejecucionPresupuestal/listarPorPrograma")
                                                .hasAuthority("DIRECTOR")
                                                // DECANO
                                                .requestMatchers("/ejecucionPresupuestal/listarPorFacultad")
                                                .hasAuthority("DECANO")
                                                // DIRECTOR Y DECANO
                                                .requestMatchers("/ejecucionPresupuestal/listarPorPresupuesto",
                                                                "/ejecucionPresupuestal/ingresosTotales")
                                                .hasAnyAuthority("DECANO",
                                                                "DIRECTOR")

                                                // ADMIN
                                                .requestMatchers("/ejecucionPresupuestal/**").hasAuthority("ADMIN")

                                                /* Permisos de Facultad: Sólo admin */
                                                .requestMatchers("/facultad/**").hasAuthority("ADMIN")

                                                /* Permisos de Programa: Sólo admin */
                                                .requestMatchers("/programa/**").hasAuthority("ADMIN")

                                                /* Permisos de Rol: Sólo admin */
                                                .requestMatchers("/rol/**").hasAuthority("ADMIN")

                                                /* Permisos de Usuario */
                                                // DIRECTOR Y DECANO
                                                .requestMatchers("/usuario/editarDatosBasicos")
                                                .hasAnyAuthority("DECANO", "DIRECTOR")

                                                // ADMIN
                                                .requestMatchers("/usuario/**").hasAuthority("ADMIN")

                                                /* Permisos de Cohorte */
                                                // DIRECTOR
                                                .requestMatchers("/cohorte/crear", "/cohorte/actualizar",
                                                                "/cohorte/eliminar", "/cohorte/listar")
                                                .hasAuthority("DIRECTOR")

                                                // ADMIN
                                                .requestMatchers("/cohorte/**").hasAuthority("ADMIN")

                                                /* Permisos de Egreso Descuento */
                                                // DIRECTOR
                                                .requestMatchers("/egresoDescuento/crear",
                                                                "/egresoDescuento/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoDescuento/crearEgresoFueraDelPresupuesto",
                                                                "/egresoDescuento/actualizar",
                                                                "egresoDescuento/eliminar")
                                                .hasAuthority("DIRECTOR")
                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoDescuento/listarPorPresupuesto",
                                                                "egresoDescuento/listarPorEjecucionPresupuestal",

                                                                "egresoDescuento/totalEgresosDescuentos",
                                                                "egresoDescuento/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR")
                                                // ADMIN
                                                .requestMatchers("/egresoDescuento/**").hasAuthority("ADMIN")

                                                /* Permisos de Egreso General */
                                                // DIRECTOR
                                                .requestMatchers("/egresoGeneral/crear",
                                                                "/egresoGeneral/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoGeneral/crearEgresoFueraDelPresupuesto",
                                                                "/egresoGeneral/actualizar",
                                                                "egresoGeneral/eliminar")
                                                .hasAuthority("DIRECTOR")
                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoGeneral/listarPorPresupuesto",
                                                                "egresoGeneral/listarPorEjecucionPresupuestal",

                                                                "egresoGeneral/totalEgresosDescuentos",
                                                                "egresoGeneral/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR")
                                                // ADMIN
                                                .requestMatchers("/egresoGeneral/**").hasAuthority("ADMIN")

                                                /* Permisos de Egreso Inversion */
                                                // DIRECTOR
                                                .requestMatchers("/egresoInversion/crear",
                                                                "/egresoInversion/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoInversion/crearEgresoFueraDelPresupuesto",
                                                                "/egresoInversion/actualizar",
                                                                "egresoInversion/eliminar")
                                                .hasAuthority("DIRECTOR")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoInversion/listarPorPresupuesto",
                                                                "egresoInversion/listarPorEjecucionPresupuestal",

                                                                "egresoInversion/totalEgresosDescuentos",
                                                                "egresoInversion/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR")

                                                // ADMIN
                                                .requestMatchers("/egresoInversion/**").hasAuthority("ADMIN")

                                                /* Permisos de Egreso Otro */
                                                // DIRECTOR
                                                .requestMatchers("/egresoOtro/crear",
                                                                "/egresoOtro/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoOtro/crearEgresoFueraDelPresupuesto",
                                                                "/egresoOtro/actualizar",
                                                                "egresoOtro/eliminar")
                                                .hasAuthority("DIRECTOR")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoOtro/listarPorPresupuesto",
                                                                "egresoOtro/listarPorEjecucionPresupuestal",

                                                                "egresoOtro/totalEgresosDescuentos",
                                                                "egresoOtro/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR")

                                                // ADMIN
                                                .requestMatchers("/egresoOtro/**").hasAuthority("ADMIN")

                                                /* Permisos de Egreso Otros Serv Docente */
                                                // DIRECTOR
                                                .requestMatchers("/egresoOtrosServDocente/crear",
                                                                "/egresoOtrosServDocente/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoOtrosServDocente/crearEgresoFueraDelPresupuesto",
                                                                "/egresoOtrosServDocente/actualizar",
                                                                "egresoOtrosServDocente/eliminar")
                                                .hasAuthority("DIRECTOR")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoOtrosServDocente/listarPorPresupuesto",
                                                                "egresoOtrosServDocente/listarPorEjecucionPresupuestal",

                                                                "egresoOtrosServDocente/totalEgresosDescuentos",
                                                                "egresoOtrosServDocente/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR")

                                                // ADMIN
                                                .requestMatchers("/egresoOtrosServDocente/**").hasAuthority("ADMIN")

                                                /* Permisos de Egreso Recurrente Adm */
                                                // DIRECTOR
                                                .requestMatchers("/egresoRecurrenteAdm/crear",
                                                                "/egresoRecurrenteAdm/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoRecurrenteAdm/crearEgresoFueraDelPresupuesto",
                                                                "/egresoRecurrenteAdm/actualizar",
                                                                "egresoRecurrenteAdm/eliminar")
                                                .hasAuthority("DIRECTOR")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoRecurrenteAdm/listarPorPresupuesto",
                                                                "egresoRecurrenteAdm/listarPorEjecucionPresupuestal",

                                                                "egresoRecurrenteAdm/totalEgresosDescuentos",
                                                                "egresoRecurrenteAdm/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR")

                                                // ADMIN
                                                .requestMatchers("/egresoRecurrenteAdm/**").hasAuthority("ADMIN")

                                                /* Permisos de Egreso Serv Docente */
                                                // DIRECTOR
                                                .requestMatchers("/egresoServDocente/crear",
                                                                "/egresoServDocente/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoServDocente/crearEgresoFueraDelPresupuesto",
                                                                "/egresoServDocente/actualizar",
                                                                "egresoServDocente/eliminar")
                                                .hasAuthority("DIRECTOR")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoServDocente/listarPorPresupuesto",
                                                                "egresoServDocente/listarPorEjecucionPresupuestal",

                                                                "egresoServDocente/totalEgresosDescuentos",
                                                                "egresoServDocente/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR")

                                                // ADMIN
                                                .requestMatchers("/egresoServDocente/**").hasAuthority("ADMIN")

                                                /* Permisos de Egreso Serv No Docente */
                                                // DIRECTOR
                                                .requestMatchers("/egresoServNoDocente/crear",
                                                                "/egresoServNoDocente/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoServNoDocente/crearEgresoFueraDelPresupuesto",
                                                                "/egresoServNoDocente/actualizar",
                                                                "egresoServNoDocente/eliminar")
                                                .hasAuthority("DIRECTOR")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoServNoDocente/listarPorPresupuesto",
                                                                "egresoServNoDocente/listarPorEjecucionPresupuestal",

                                                                "egresoServNoDocente/totalEgresosDescuentos",
                                                                "egresoServNoDocente/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR")

                                                // ADMIN
                                                .requestMatchers("/egresoServNoDocente/**").hasAuthority("ADMIN")

                                                /* Permisos de Egreso Transferencia */
                                                // DIRECTOR
                                                .requestMatchers("/egresoTransferencia/crear",
                                                                "/egresoTransferencia/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoTransferencia/crearEgresoFueraDelPresupuesto",
                                                                "/egresoTransferencia/actualizar",
                                                                "egresoTransferencia/eliminar")
                                                .hasAuthority("DIRECTOR")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoTransferencia/listarPorPresupuesto",
                                                                "egresoTransferencia/listarPorEjecucionPresupuestal",

                                                                "egresoTransferencia/totalEgresosDescuentos",
                                                                "egresoTransferencia/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR")

                                                // ADMIN
                                                .requestMatchers("/egresoTransferencia/**").hasAuthority("ADMIN")

                                                /* Permisos de Egreso Viaje */
                                                // DIRECTOR
                                                .requestMatchers("/egresoViaje/crear",
                                                                "/egresoViaje/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoViaje/crearEgresoFueraDelPresupuesto",
                                                                "/egresoViaje/actualizar",
                                                                "egresoViaje/eliminar")
                                                .hasAuthority("DIRECTOR")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoViaje/listarPorPresupuesto",
                                                                "egresoViaje/listarPorEjecucionPresupuestal",

                                                                "egresoViaje/totalEgresosDescuentos",
                                                                "egresoViaje/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR")

                                                // ADMIN
                                                .requestMatchers("/egresoViaje/**").hasAuthority("ADMIN")

                                                /* Permisos de Ingresp */
                                                // DIRECTOR
                                                .requestMatchers("/ingreso/crear",
                                                                "/ingreso/crearIngresoEjecucionDelPresupuesto",
                                                                "/ingreso/crearIngresoFueraDelPresupuesto",
                                                                "/ingreso/actualizar", "ingreso/eliminar")
                                                .hasAuthority("DIRECTOR")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "ingreso/listarPorPresupuesto",
                                                                "ingreso/listarPorEjecucionPresupuestal",

                                                                "ingreso/totalIngresos",
                                                                "ingreso/totalIngresosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR")

                                                // ADMIN
                                                .requestMatchers("/ingreso/**").hasAuthority("ADMIN")

                                                // Se debe estar autenticado para acceder a cualquier otra ruta que no
                                                // sea login o registro
                                                .anyRequest().authenticated())
                                .sessionManagement(sessionManager -> sessionManager
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authProvider)
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .build();

        }

}
