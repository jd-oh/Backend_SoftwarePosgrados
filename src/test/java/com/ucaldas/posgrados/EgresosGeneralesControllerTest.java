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

import com.ucaldas.posgrados.Controller.EgresosGeneralesController;
import com.ucaldas.posgrados.Controller.PresupuestoController;
import com.ucaldas.posgrados.Entity.EgresosGenerales;
import com.ucaldas.posgrados.Entity.Presupuesto;
import com.ucaldas.posgrados.Entity.TipoCosto;
import com.ucaldas.posgrados.Repository.EgresosGeneralesRepository;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoCostoRepository;

@SpringBootTest
public class EgresosGeneralesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PresupuestoRepository presupuestoRepository;

    @Mock
    private EgresosGeneralesRepository egresoGeneralRepository;

    @Mock
    private TipoCostoRepository tipoCostoRepository;

    @InjectMocks
    private EgresosGeneralesController egresosGeneralesController;

    @Mock
    private PresupuestoController presupuestoController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(egresosGeneralesController).build();
    }

    @Test
    public void testCrearEgresoGeneral() throws Exception {
        int idPresupuesto = 1;
        int idTipoCosto = 1;
        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(idPresupuesto);

        // Inicializar el conjunto de EgresosDescuentos
        presupuesto.setEgresosGenerales(new HashSet<>());
        TipoCosto tipoCosto = new TipoCosto();
        tipoCosto.setId(idTipoCosto);

        when(presupuestoRepository.findById(idPresupuesto)).thenReturn(Optional.of(presupuesto));
        when(tipoCostoRepository.findById(idTipoCosto)).thenReturn(Optional.of(tipoCosto));

        mockMvc.perform(MockMvcRequestBuilders.post("/egresoGeneral/crear")
                .param("idPresupuestoEjecucion", String.valueOf(idPresupuesto))
                .param("concepto", "Test Concepto")
                .param("valorUnitario", "10.0")
                .param("cantidad", "5")
                .param("idTipoCosto", String.valueOf(idTipoCosto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(presupuestoRepository, times(1)).findById(idPresupuesto);
        verify(tipoCostoRepository, times(1)).findById(idTipoCosto);
        verify(presupuestoRepository, times(1)).save(any(Presupuesto.class));
    }

    @Test
    public void testListarEgresosGenerales() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/egresoGeneral/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(egresoGeneralRepository, times(1)).findAllByOrderByPresupuestoAsc();
    }

    @Test
    public void testBuscarEgresoGeneral() throws Exception {
        int idEgresoGeneral = 1;
        EgresosGenerales egresoGeneral = new EgresosGenerales();
        egresoGeneral.setId(idEgresoGeneral);

        when(egresoGeneralRepository.findById(idEgresoGeneral)).thenReturn(Optional.of(egresoGeneral));

        mockMvc.perform(MockMvcRequestBuilders.get("/egresoGeneral/buscar")
                .param("id", String.valueOf(idEgresoGeneral))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(idEgresoGeneral));

        verify(egresoGeneralRepository, times(1)).findById(idEgresoGeneral);
    }

    @Test
    public void testActualizarEgresoGeneral() throws Exception {
        int idEgresoGeneral = 1;
        int idTipoCosto = 1;
        EgresosGenerales egresoGeneral = new EgresosGenerales();
        egresoGeneral.setId(idEgresoGeneral);
        TipoCosto tipoCosto = new TipoCosto();
        tipoCosto.setId(idTipoCosto);

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1);
        egresoGeneral.setPresupuesto(presupuesto);

        when(egresoGeneralRepository.findById(idEgresoGeneral)).thenReturn(Optional.of(egresoGeneral));
        when(tipoCostoRepository.findById(idTipoCosto)).thenReturn(Optional.of(tipoCosto));

        mockMvc.perform(MockMvcRequestBuilders.put("/egresoGeneral/actualizar")
                .param("id", String.valueOf(idEgresoGeneral))
                .param("idTipoCosto", String.valueOf(idTipoCosto))
                .param("concepto", "Test Concepto")
                .param("valorUnitario", "10.0")
                .param("cantidad", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(egresoGeneralRepository, times(1)).findById(idEgresoGeneral);
        verify(tipoCostoRepository, times(1)).findById(idTipoCosto);
        verify(egresoGeneralRepository, times(1)).save(any(EgresosGenerales.class));
    }

    @Test
    public void testEliminarEgresoGeneral() throws Exception {
        int idEgresoGeneral = 1;
        EgresosGenerales egresoGeneral = new EgresosGenerales();
        egresoGeneral.setId(idEgresoGeneral);

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1);
        egresoGeneral.setPresupuesto(presupuesto);

        when(egresoGeneralRepository.findById(idEgresoGeneral)).thenReturn(Optional.of(egresoGeneral));

        mockMvc.perform(MockMvcRequestBuilders.delete("/egresoGeneral/eliminar")
                .param("id", String.valueOf(idEgresoGeneral))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(egresoGeneralRepository, times(1)).findById(idEgresoGeneral);
        verify(egresoGeneralRepository, times(1)).deleteById(idEgresoGeneral);
    }

    @Test
    public void testListarEgresosGeneralesPorPresupuesto() throws Exception {
        int idPresupuesto = 1;
        mockMvc.perform(MockMvcRequestBuilders.get("/egresoGeneral/listarPorPresupuesto")
                .param("idPresupuesto", String.valueOf(idPresupuesto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(egresoGeneralRepository, times(1)).findByPresupuestoId(idPresupuesto);
    }
}
