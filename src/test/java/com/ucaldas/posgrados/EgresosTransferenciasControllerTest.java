package com.ucaldas.posgrados;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ucaldas.posgrados.Controller.EgresosTransferenciasController;
import com.ucaldas.posgrados.Controller.PresupuestoController;
import com.ucaldas.posgrados.Entity.EgresosTransferencias;
import com.ucaldas.posgrados.Entity.Presupuesto;
import com.ucaldas.posgrados.Entity.TipoTransferencia;
import com.ucaldas.posgrados.Repository.EgresosTransferenciasRepository;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoTransferenciaRepository;

@SpringBootTest
public class EgresosTransferenciasControllerTest {

        private MockMvc mockMvc;

        @Mock
        private PresupuestoRepository presupuestoRepository;

        @Mock
        private EgresosTransferenciasRepository egresoTransferenciaRepository;

        @Mock
        private TipoTransferenciaRepository tipoTransferenciaRepository;

        @Mock
        private PresupuestoController presupuestoController;

        @InjectMocks
        private EgresosTransferenciasController egresosTransferenciasController;

        @BeforeEach
        public void setup() {
                MockitoAnnotations.openMocks(this);
                this.mockMvc = MockMvcBuilders.standaloneSetup(egresosTransferenciasController).build();
        }

        @Test
        public void testCrearEgresoTransferencia() throws Exception {
                int idPresupuestoEjecucion = 1;
                String descripcion = "Test Egreso Transferencia";
                double porcentaje = 10.0;
                int idTipoTransferencia = 1;

                Presupuesto presupuesto = new Presupuesto();
                presupuesto.setId(idPresupuestoEjecucion);

                // Inicializar el conjunto de EgresosDescuentos
                presupuesto.setEgresosTransferencias(new HashSet<>());

                TipoTransferencia tipoTransferencia = new TipoTransferencia();
                tipoTransferencia.setId(idTipoTransferencia);

                when(presupuestoRepository.findById(idPresupuestoEjecucion)).thenReturn(Optional.of(presupuesto));
                when(tipoTransferenciaRepository.findById(idTipoTransferencia))
                                .thenReturn(Optional.of(tipoTransferencia));

                mockMvc.perform(MockMvcRequestBuilders.post("/egresoTransferencia/crear")
                                .param("idPresupuestoEjecucion", String.valueOf(idPresupuestoEjecucion))
                                .param("descripcion", descripcion)
                                .param("porcentaje", String.valueOf(porcentaje))
                                .param("idTipoTransferencia", String.valueOf(idTipoTransferencia))
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.content().string("OK"));

                verify(presupuestoController, times(1)).actualizarEgresosProgramaTotales(eq(idPresupuestoEjecucion),
                                anyDouble(), eq(0.0));
                verify(presupuestoRepository, times(1)).save(any(Presupuesto.class));
        }

        @Test
        public void testListarEgresosTransferencias() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.get("/egresoTransferencia/listar")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk());
        }

        @Test
        public void testBuscarEgresoTransferencia() throws Exception {
                int idEgresoTransferencia = 1;
                EgresosTransferencias egresoTransferencia = new EgresosTransferencias();
                egresoTransferencia.setId(idEgresoTransferencia);

                when(egresoTransferenciaRepository.findById(idEgresoTransferencia))
                                .thenReturn(Optional.of(egresoTransferencia));

                mockMvc.perform(MockMvcRequestBuilders.get("/egresoTransferencia/buscar")
                                .param("id", String.valueOf(idEgresoTransferencia))
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(idEgresoTransferencia));
        }

        @Test
        public void testActualizarEgresoTransferencia() throws Exception {
                int idEgresoTransferencia = 1;
                String descripcion = "Updated Egreso Transferencia";
                double porcentaje = 20.0;
                int idTipoTransferencia = 2;

                EgresosTransferencias egresoTransferencia = new EgresosTransferencias();
                egresoTransferencia.setId(idEgresoTransferencia);

                Presupuesto presupuesto = new Presupuesto();
                presupuesto.setId(1);
                egresoTransferencia.setPresupuesto(presupuesto);

                TipoTransferencia tipoTransferencia = new TipoTransferencia();
                tipoTransferencia.setId(idTipoTransferencia);

                when(egresoTransferenciaRepository.findById(idEgresoTransferencia))
                                .thenReturn(Optional.of(egresoTransferencia));
                when(presupuestoRepository.findById(presupuesto.getId())).thenReturn(Optional.of(presupuesto));
                when(tipoTransferenciaRepository.findById(idTipoTransferencia))
                                .thenReturn(Optional.of(tipoTransferencia));

                mockMvc.perform(MockMvcRequestBuilders.put("/egresoTransferencia/actualizar")
                                .param("id", String.valueOf(idEgresoTransferencia))
                                .param("descripcion", descripcion)
                                .param("porcentaje", String.valueOf(porcentaje))
                                .param("idTipoTransferencia", String.valueOf(idTipoTransferencia))
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.content().string("OK"));

                verify(presupuestoController, times(1)).actualizarEgresosProgramaTotales(eq(presupuesto.getId()),
                                anyDouble(),
                                anyDouble());
                verify(egresoTransferenciaRepository, times(1)).save(any(EgresosTransferencias.class));
        }

        @Test
        public void testEliminarEgresoTransferencia() throws Exception {
                int idEgresoTransferencia = 1;
                EgresosTransferencias egresoTransferencia = new EgresosTransferencias();
                egresoTransferencia.setId(idEgresoTransferencia);

                Presupuesto presupuesto = new Presupuesto();
                presupuesto.setId(1);
                egresoTransferencia.setPresupuesto(presupuesto);

                when(egresoTransferenciaRepository.findById(idEgresoTransferencia))
                                .thenReturn(Optional.of(egresoTransferencia));

                mockMvc.perform(MockMvcRequestBuilders.delete("/egresoTransferencia/eliminar")
                                .param("id", String.valueOf(idEgresoTransferencia))
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.content().string("OK"));

                verify(presupuestoController, times(1)).actualizarEgresosProgramaTotales(
                                eq(egresoTransferencia.getPresupuesto().getId()), eq(0.0), anyDouble());
                verify(egresoTransferenciaRepository, times(1)).deleteById(idEgresoTransferencia);
        }

        @Test
        public void testListarEgresosTransferenciasPorPresupuesto() throws Exception {
                int idPresupuesto = 1;

                mockMvc.perform(MockMvcRequestBuilders.get("/egresoTransferencia/listarPorPresupuesto")
                                .param("idPresupuesto", String.valueOf(idPresupuesto))
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk());
        }

}
