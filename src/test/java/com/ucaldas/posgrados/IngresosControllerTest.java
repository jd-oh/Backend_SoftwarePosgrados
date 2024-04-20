package com.ucaldas.posgrados;

import static org.mockito.ArgumentMatchers.*;
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
import com.ucaldas.posgrados.Controller.IngresosController;
import com.ucaldas.posgrados.Controller.PresupuestoController;
import com.ucaldas.posgrados.Entity.Ingresos;
import com.ucaldas.posgrados.Entity.Presupuesto;
import com.ucaldas.posgrados.Repository.IngresosRepository;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;

@SpringBootTest
public class IngresosControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PresupuestoRepository presupuestoRepository;

    @Mock
    private IngresosRepository ingresoRepository;

    @InjectMocks
    private IngresosController ingresosController;

    @Mock
    private PresupuestoController presupuestoController;

    @Mock
    private EgresosTransferenciasController egresosTransferenciasController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(ingresosController).build();
    }

    @Test
    public void testCrearIngreso() throws Exception {
        int idPresupuestoEjecucion = 1;
        String concepto = "Test Ingreso";
        double valor = 100.0;

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(idPresupuestoEjecucion);

        // Inicializar el conjunto de EgresosDescuentos
        presupuesto.setIngresos(new HashSet<>());

        when(presupuestoRepository.findById(idPresupuestoEjecucion)).thenReturn(Optional.of(presupuesto));

        mockMvc.perform(MockMvcRequestBuilders.post("/ingreso/crear")
                .param("idPresupuestoEjecucion", String.valueOf(idPresupuestoEjecucion))
                .param("concepto", concepto)
                .param("valor", String.valueOf(valor))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(presupuestoRepository, times(1)).findById(idPresupuestoEjecucion);
        verify(presupuestoRepository, times(1)).save(any(Presupuesto.class));
    }

    @Test
    public void testListarIngresos() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/ingreso/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(ingresoRepository, times(1)).findAllByOrderByPresupuestoAsc();
    }

    @Test
    public void testListarIngresosPorPresupuesto() throws Exception {
        int idPresupuesto = 1;

        mockMvc.perform(MockMvcRequestBuilders.get("/ingreso/listarPorPresupuesto")
                .param("idPresupuesto", String.valueOf(idPresupuesto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(ingresoRepository, times(1)).findByPresupuestoId(idPresupuesto);
    }

    @Test
    public void testBuscarIngreso() throws Exception {
        int idIngreso = 1;
        Ingresos ingreso = new Ingresos();
        ingreso.setId(idIngreso);

        when(ingresoRepository.findById(idIngreso)).thenReturn(Optional.of(ingreso));

        mockMvc.perform(MockMvcRequestBuilders.get("/ingreso/buscar")
                .param("id", String.valueOf(idIngreso))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(idIngreso));

        verify(ingresoRepository, times(1)).findById(idIngreso);
    }

    @Test
    public void testActualizarIngreso() throws Exception {
        int idIngreso = 1;
        String concepto = "Updated Ingreso";
        double valor = 200.0;

        Ingresos ingreso = new Ingresos();
        ingreso.setId(idIngreso);

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1);
        ingreso.setPresupuesto(presupuesto);

        when(ingresoRepository.findById(idIngreso)).thenReturn(Optional.of(ingreso));

        mockMvc.perform(MockMvcRequestBuilders.put("/ingreso/actualizar")
                .param("id", String.valueOf(idIngreso))
                .param("concepto", concepto)
                .param("valor", String.valueOf(valor))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(ingresoRepository, times(1)).findById(idIngreso);
        verify(ingresoRepository, times(1)).save(any(Ingresos.class));
    }

    @Test
    public void testEliminarIngreso() throws Exception {
        int idIngreso = 1;
        Ingresos ingreso = new Ingresos();
        ingreso.setId(idIngreso);

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1);
        ingreso.setPresupuesto(presupuesto);

        when(ingresoRepository.findById(idIngreso)).thenReturn(Optional.of(ingreso));

        mockMvc.perform(MockMvcRequestBuilders.delete("/ingreso/eliminar")
                .param("id", String.valueOf(idIngreso))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(ingresoRepository, times(1)).findById(idIngreso);
        verify(ingresoRepository, times(1)).deleteById(idIngreso);
    }
}
