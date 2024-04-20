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

import com.ucaldas.posgrados.Controller.EgresosViajesController;
import com.ucaldas.posgrados.Controller.PresupuestoController;
import com.ucaldas.posgrados.Entity.EgresosViajes;
import com.ucaldas.posgrados.Entity.Presupuesto;
import com.ucaldas.posgrados.Repository.EgresosViajesRepository;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;

@SpringBootTest
public class EgresosViajesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PresupuestoRepository presupuestoRepository;

    @Mock
    private EgresosViajesRepository egresoViajeRepository;

    @InjectMocks
    private EgresosViajesController egresosViajesController;

    @Mock
    private PresupuestoController presupuestoController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(egresosViajesController).build();
    }

    @Test
    public void testCrearEgresoViaje() throws Exception {
        int idPresupuestoEjecucion = 1;
        String descripcion = "Test Egreso Viaje";
        int numPersonas = 5;
        double apoyoDesplazamiento = 100.0;
        int numViajesPorPersona = 2;
        double valorTransporte = 50.0;

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(idPresupuestoEjecucion);

        // Inicializar el conjunto de EgresosDescuentos
        presupuesto.setEgresosViaje(new HashSet<>());

        when(presupuestoRepository.findById(idPresupuestoEjecucion)).thenReturn(Optional.of(presupuesto));

        mockMvc.perform(MockMvcRequestBuilders.post("/egresoViaje/crear")
                .param("idPresupuestoEjecucion", String.valueOf(idPresupuestoEjecucion))
                .param("descripcion", descripcion)
                .param("numPersonas", String.valueOf(numPersonas))
                .param("apoyoDesplazamiento", String.valueOf(apoyoDesplazamiento))
                .param("numViajesPorPersona", String.valueOf(numViajesPorPersona))
                .param("valorTransporte", String.valueOf(valorTransporte))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(presupuestoRepository, times(1)).findById(idPresupuestoEjecucion);
        verify(presupuestoRepository, times(1)).save(any(Presupuesto.class));
    }

    @Test
    public void testListarEgresosViajes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/egresoViaje/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(egresoViajeRepository, times(1)).findAllByOrderByPresupuestoAsc();
    }

    @Test
    public void testBuscarEgresoViaje() throws Exception {
        int idEgresoViaje = 1;
        EgresosViajes egresoViaje = new EgresosViajes();
        egresoViaje.setId(idEgresoViaje);

        when(egresoViajeRepository.findById(idEgresoViaje)).thenReturn(Optional.of(egresoViaje));

        mockMvc.perform(MockMvcRequestBuilders.get("/egresoViaje/buscar")
                .param("id", String.valueOf(idEgresoViaje))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(idEgresoViaje));

        verify(egresoViajeRepository, times(1)).findById(idEgresoViaje);
    }

    @Test
    public void testActualizarEgresoViaje() throws Exception {
        int idEgresoViaje = 1;
        String descripcion = "Updated Egreso Viaje";
        int numPersonas = 3;
        double apoyoDesplazamiento = 50.0;
        int numViajesPorPersona = 1;
        double valorTransporte = 25.0;

        EgresosViajes egresoViaje = new EgresosViajes();
        egresoViaje.setId(idEgresoViaje);

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1);
        egresoViaje.setPresupuesto(presupuesto);

        when(egresoViajeRepository.findById(idEgresoViaje)).thenReturn(Optional.of(egresoViaje));

        mockMvc.perform(MockMvcRequestBuilders.put("/egresoViaje/actualizar")
                .param("id", String.valueOf(idEgresoViaje))
                .param("descripcion", descripcion)
                .param("numPersonas", String.valueOf(numPersonas))
                .param("apoyoDesplazamiento", String.valueOf(apoyoDesplazamiento))
                .param("numViajesPorPersona", String.valueOf(numViajesPorPersona))
                .param("valorTransporte", String.valueOf(valorTransporte))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(egresoViajeRepository, times(1)).findById(idEgresoViaje);
        verify(egresoViajeRepository, times(1)).save(any(EgresosViajes.class));
    }

    @Test
    public void testEliminarEgresoViaje() throws Exception {
        int idEgresoViaje = 1;
        EgresosViajes egresoViaje = new EgresosViajes();
        egresoViaje.setId(idEgresoViaje);

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1);
        egresoViaje.setPresupuesto(presupuesto);

        when(egresoViajeRepository.findById(idEgresoViaje)).thenReturn(Optional.of(egresoViaje));

        mockMvc.perform(MockMvcRequestBuilders.delete("/egresoViaje/eliminar")
                .param("id", String.valueOf(idEgresoViaje))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(egresoViajeRepository, times(1)).findById(idEgresoViaje);
        verify(egresoViajeRepository, times(1)).deleteById(idEgresoViaje);
    }

    @Test
    public void testListarEgresosViajesPorPresupuesto() throws Exception {
        int idPresupuesto = 1;

        mockMvc.perform(MockMvcRequestBuilders.get("/egresoViaje/listarPorPresupuesto")
                .param("idPresupuesto", String.valueOf(idPresupuesto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(egresoViajeRepository, times(1)).findByPresupuestoId(idPresupuesto);
    }
}
