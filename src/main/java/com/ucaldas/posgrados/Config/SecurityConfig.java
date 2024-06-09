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
                                                .requestMatchers("/cdp/**").permitAll()
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
                                                                "/presupuesto/eliminar")
                                                .hasAnyAuthority("DIRECTOR", "ADMIN")
                                                // DECANO
                                                .requestMatchers("/presupuesto/aprobar",
                                                                "/presupuesto/listarPorFacultad")
                                                .hasAnyAuthority("DECANO", "ADMIN")
                                                // DIRECTOR Y DECANO
                                                .requestMatchers("/presupuesto/buscarPorCohorte",
                                                                "/presupuesto/listarPorPrograma",
                                                                "/presupuesto/ingresosTotales")
                                                .hasAnyAuthority("DECANO", "DIRECTOR", "ADMIN")

                                                /* Permisos de Ejecución Presupuestal */
                                                // DIRECTOR
                                                .requestMatchers("/ejecucionPresupuestal/crear",
                                                                "/ejecucionPresupuestal/listarPorPrograma")
                                                .hasAnyAuthority("DIRECTOR", "ADMIN")
                                                // DECANO
                                                .requestMatchers("/ejecucionPresupuestal/listarPorFacultad")
                                                .hasAnyAuthority("DECANO", "ADMIN")
                                                // DIRECTOR Y DECANO
                                                .requestMatchers("/ejecucionPresupuestal/listarPorPresupuesto",
                                                                "/ejecucionPresupuestal/ingresosTotales")
                                                .hasAnyAuthority("DECANO",
                                                                "DIRECTOR", "ADMIN")

                                                /* Permisos de Facultad: Sólo admin */
                                                .requestMatchers("/facultad/crear", "/facultad/actualizar",
                                                                "/facultad/eliminar", "/facultad/listar")
                                                .hasAuthority("ADMIN")

                                                /* Permisos de Programa: Sólo admin */
                                                .requestMatchers("/programa/crear", "/programa/actualizar",
                                                                "/programa/eliminar", "/programa/listar")
                                                .hasAuthority("ADMIN")

                                                /* Permisos de Rol: Sólo admin */
                                                .requestMatchers("/rol/crear", "/rol/actualizar",
                                                                "/rol/eliminar", "/rol/listar")
                                                .hasAuthority("ADMIN")

                                                /* Permisos de Usuario */
                                                // DIRECTOR Y DECANO
                                                .requestMatchers("/usuario/editarDatosBasicos")
                                                .hasAnyAuthority("DECANO", "DIRECTOR", "ADMIN")

                                                // ADMIN
                                                .requestMatchers("/usuario/desactivar", "/usuario/listar",
                                                                "/usuario/activar")
                                                .hasAuthority("ADMIN")

                                                /* Permisos de Cohorte */
                                                // DIRECTOR
                                                .requestMatchers("/cohorte/crear", "/cohorte/actualizar",
                                                                "/cohorte/eliminar")
                                                .hasAnyAuthority("DIRECTOR", "ADMIN")

                                                // DIRECTOR Y DECANO
                                                .requestMatchers("/cohorte/listar")
                                                .hasAnyAuthority("DECANO", "DIRECTOR", "ADMIN")

                                                /* Permisos de Egreso Descuento */
                                                // DIRECTOR
                                                .requestMatchers("/egresoDescuento/crear",
                                                                "/egresoDescuento/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoDescuento/crearEgresoFueraDelPresupuesto",
                                                                "/egresoDescuento/actualizar",
                                                                "egresoDescuento/eliminar")
                                                .hasAnyAuthority("DIRECTOR", "ADMIN")
                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoDescuento/listarPorPresupuesto",
                                                                "egresoDescuento/listarPorEjecucionPresupuestal",

                                                                "egresoDescuento/totalEgresosDescuentos",
                                                                "egresoDescuento/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR", "ADMIN")

                                                /* Permisos de Egreso General */
                                                // DIRECTOR
                                                .requestMatchers("/egresoGeneral/crear",
                                                                "/egresoGeneral/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoGeneral/crearEgresoFueraDelPresupuesto",
                                                                "/egresoGeneral/actualizar",
                                                                "egresoGeneral/eliminar")
                                                .hasAnyAuthority("DIRECTOR", "ADMIN")
                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoGeneral/listarPorPresupuesto",
                                                                "egresoGeneral/listarPorEjecucionPresupuestal",

                                                                "egresoGeneral/totalEgresosDescuentos",
                                                                "egresoGeneral/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR", "ADMIN")

                                                /* Permisos de Egreso Inversion */
                                                // DIRECTOR
                                                .requestMatchers("/egresoInversion/crear",
                                                                "/egresoInversion/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoInversion/crearEgresoFueraDelPresupuesto",
                                                                "/egresoInversion/actualizar",
                                                                "egresoInversion/eliminar")
                                                .hasAnyAuthority("DIRECTOR", "ADMIN")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoInversion/listarPorPresupuesto",
                                                                "egresoInversion/listarPorEjecucionPresupuestal",

                                                                "egresoInversion/totalEgresosDescuentos",
                                                                "egresoInversion/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR", "ADMIN")

                                                /* Permisos de Egreso Otro */
                                                // DIRECTOR
                                                .requestMatchers("/egresoOtro/crear",
                                                                "/egresoOtro/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoOtro/crearEgresoFueraDelPresupuesto",
                                                                "/egresoOtro/actualizar",
                                                                "egresoOtro/eliminar")
                                                .hasAnyAuthority("DIRECTOR", "ADMIN")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoOtro/listarPorPresupuesto",
                                                                "egresoOtro/listarPorEjecucionPresupuestal",

                                                                "egresoOtro/totalEgresosDescuentos",
                                                                "egresoOtro/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR", "ADMIN")

                                                /* Permisos de Egreso Otros Serv Docente */
                                                // DIRECTOR
                                                .requestMatchers("/egresoOtrosServDocente/crear",
                                                                "/egresoOtrosServDocente/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoOtrosServDocente/crearEgresoFueraDelPresupuesto",
                                                                "/egresoOtrosServDocente/actualizar",
                                                                "egresoOtrosServDocente/eliminar")
                                                .hasAnyAuthority("DIRECTOR", "ADMIN")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoOtrosServDocente/listarPorPresupuesto",
                                                                "egresoOtrosServDocente/listarPorEjecucionPresupuestal",

                                                                "egresoOtrosServDocente/totalEgresosDescuentos",
                                                                "egresoOtrosServDocente/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR", "ADMIN")

                                                /* Permisos de Egreso Recurrente Adm */
                                                // DIRECTOR
                                                .requestMatchers("/egresoRecurrenteAdm/crear",
                                                                "/egresoRecurrenteAdm/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoRecurrenteAdm/crearEgresoFueraDelPresupuesto",
                                                                "/egresoRecurrenteAdm/actualizar",
                                                                "egresoRecurrenteAdm/eliminar")
                                                .hasAnyAuthority("DIRECTOR", "ADMIN")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoRecurrenteAdm/listarPorPresupuesto",
                                                                "egresoRecurrenteAdm/listarPorEjecucionPresupuestal",

                                                                "egresoRecurrenteAdm/totalEgresosDescuentos",
                                                                "egresoRecurrenteAdm/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR", "ADMIN")

                                                /* Permisos de Egreso Serv Docente */
                                                // DIRECTOR
                                                .requestMatchers("/egresoServDocente/crear",
                                                                "/egresoServDocente/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoServDocente/crearEgresoFueraDelPresupuesto",
                                                                "/egresoServDocente/actualizar",
                                                                "egresoServDocente/eliminar")
                                                .hasAnyAuthority("DIRECTOR", "ADMIN")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoServDocente/listarPorPresupuesto",
                                                                "egresoServDocente/listarPorEjecucionPresupuestal",

                                                                "egresoServDocente/totalEgresosDescuentos",
                                                                "egresoServDocente/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR", "ADMIN")

                                                /* Permisos de Egreso Serv No Docente */
                                                // DIRECTOR
                                                .requestMatchers("/egresoServNoDocente/crear",
                                                                "/egresoServNoDocente/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoServNoDocente/crearEgresoFueraDelPresupuesto",
                                                                "/egresoServNoDocente/actualizar",
                                                                "egresoServNoDocente/eliminar")
                                                .hasAnyAuthority("DIRECTOR", "ADMIN")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoServNoDocente/listarPorPresupuesto",
                                                                "egresoServNoDocente/listarPorEjecucionPresupuestal",

                                                                "egresoServNoDocente/totalEgresosDescuentos",
                                                                "egresoServNoDocente/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR", "ADMIN")

                                                /* Permisos de Egreso Transferencia */
                                                // DIRECTOR
                                                .requestMatchers("/egresoTransferencia/crear",
                                                                "/egresoTransferencia/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoTransferencia/crearEgresoFueraDelPresupuesto",
                                                                "/egresoTransferencia/actualizar",
                                                                "egresoTransferencia/eliminar")
                                                .hasAnyAuthority("DIRECTOR", "ADMIN")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoTransferencia/listarPorPresupuesto",
                                                                "egresoTransferencia/listarPorEjecucionPresupuestal",

                                                                "egresoTransferencia/totalEgresosDescuentos",
                                                                "egresoTransferencia/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR", "ADMIN")

                                                /* Permisos de Egreso Viaje */
                                                // DIRECTOR
                                                .requestMatchers("/egresoViaje/crear",
                                                                "/egresoViaje/crearEgresoEjecucionDelPresupuesto",
                                                                "/egresoViaje/crearEgresoFueraDelPresupuesto",
                                                                "/egresoViaje/actualizar",
                                                                "egresoViaje/eliminar")
                                                .hasAnyAuthority("DIRECTOR", "ADMIN")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "egresoViaje/listarPorPresupuesto",
                                                                "egresoViaje/listarPorEjecucionPresupuestal",

                                                                "egresoViaje/totalEgresosDescuentos",
                                                                "egresoViaje/totalEgresosDescuentosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR", "ADMIN")

                                                /* Permisos de Ingreso */
                                                // DIRECTOR
                                                .requestMatchers("/ingreso/crear",
                                                                "/ingreso/crearIngresoEjecucionDelPresupuesto",
                                                                "/ingreso/crearIngresoFueraDelPresupuesto",
                                                                "/ingreso/actualizar", "ingreso/eliminar")
                                                .hasAnyAuthority("DIRECTOR", "ADMIN")

                                                // DECANO Y DIRECTOR
                                                .requestMatchers(
                                                                "ingreso/listarPorPresupuesto",
                                                                "ingreso/listarPorEjecucionPresupuestal",

                                                                "ingreso/totalIngresos",
                                                                "ingreso/totalIngresosEjecucion")
                                                .hasAnyAuthority("DECANO", "DIRECTOR", "ADMIN")

                                                /* Permisos de Tipo Compensación: Sólo admin */
                                                .requestMatchers("/tipoCompensacion/crear",
                                                                "/tipoCompensacion/actualizar",
                                                                "/tipoCompensacion/eliminar",
                                                                "/tipoCompensacion/listar")
                                                .hasAuthority("ADMIN")

                                                /* Permisos de Tipo Costo: Sólo admin */
                                                .requestMatchers("/tipoCosto/crear", "/tipoCosto/actualizar",
                                                                "/tipoCosto/eliminar", "/tipoCosto/listar")
                                                .hasAuthority("ADMIN")

                                                /* Permisos de Tipo Descuento: Sólo admin */
                                                .requestMatchers("/tipoDescuento/crear", "/tipoDescuento/actualizar",
                                                                "/tipoDescuento/eliminar", "/tipoDescuento/listar")
                                                .hasAuthority("ADMIN")

                                                /* Permisos de Tipo Inversion: Sólo admin */
                                                .requestMatchers("/tipoInversion/crear", "/tipoInversion/actualizar",
                                                                "/tipoInversion/eliminar", "/tipoInversion/listar")
                                                .hasAuthority("ADMIN")

                                                /* Permisos de Tipo Transferencia: Sólo admin */
                                                .requestMatchers("/tipoTransferencia/crear",
                                                                "/tipoTransferencia/actualizar",
                                                                "/tipoTransferencia/eliminar",
                                                                "/tipoTransferencia/listar")
                                                .hasAuthority("ADMIN")
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
