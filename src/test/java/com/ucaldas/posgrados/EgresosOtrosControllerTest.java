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

import com.ucaldas.posgrados.Controller.EgresosOtrosController;
import com.ucaldas.posgrados.Controller.PresupuestoController;
import com.ucaldas.posgrados.Entity.EgresosOtros;
import com.ucaldas.posgrados.Entity.Presupuesto;
import com.ucaldas.posgrados.Entity.TipoCosto;
import com.ucaldas.posgrados.Repository.EgresosOtrosRepository;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoCostoRepository;

@SpringBootTest
public class EgresosOtrosControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PresupuestoRepository presupuestoRepository;

    @Mock
    private EgresosOtrosRepository egresoOtroRepository;

    @Mock
    private TipoCostoRepository tipoCostoRepository;

    @InjectMocks
    private EgresosOtrosController egresosOtrosController;

    @Mock
    private PresupuestoController presupuestoController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(egresosOtrosController).build();
    }

    @Test
    public void testCrearEgresoOtro() throws Exception {
        int idPresupuestoEjecucion = 1;
        int idTipoCosto = 1;
        String concepto = "Test Concepto";
        double valorUnitario = 10.0;
        int cantidad = 2;

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(idPresupuestoEjecucion);

        // Inicializar el conjunto de EgresosDescuentos
        presupuesto.setEgresosOtros(new HashSet<>());
        Optional<Presupuesto> optionalPresupuesto = Optional.of(presupuesto);

        TipoCosto tipoCosto = new TipoCosto();
        tipoCosto.setId(idTipoCosto);
        Optional<TipoCosto> optionalTipoCosto = Optional.of(tipoCosto);

        when(presupuestoRepository.findById(idPresupuestoEjecucion)).thenReturn(optionalPresupuesto);
        when(tipoCostoRepository.findById(idTipoCosto)).thenReturn(optionalTipoCosto);

        mockMvc.perform(MockMvcRequestBuilders.post("/egresoOtro/crear")
                .param("idPresupuestoEjecucion", String.valueOf(idPresupuestoEjecucion))
                .param("concepto", concepto)
                .param("valorUnitario", String.valueOf(valorUnitario))
                .param("cantidad", String.valueOf(cantidad))
                .param("idTipoCosto", String.valueOf(idTipoCosto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(presupuestoRepository, times(1)).findById(idPresupuestoEjecucion);
        verify(tipoCostoRepository, times(1)).findById(idTipoCosto);
        verify(presupuestoRepository, times(1)).save(any(Presupuesto.class));
    }

    @Test
    public void testListarEgresosOtros() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/egresoOtro/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(egresoOtroRepository, times(1)).findAllByOrderByPresupuestoAsc();
    }

    @Test
    public void testBuscarEgresoOtro() throws Exception {
        int idEgresoOtro = 1;
        EgresosOtros egresoOtro = new EgresosOtros();
        egresoOtro.setId(idEgresoOtro);

        when(egresoOtroRepository.findById(idEgresoOtro)).thenReturn(Optional.of(egresoOtro));

        mockMvc.perform(MockMvcRequestBuilders.get("/egresoOtro/buscar")
                .param("id", String.valueOf(idEgresoOtro))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(idEgresoOtro));

        verify(egresoOtroRepository, times(1)).findById(idEgresoOtro);
    }

    @Test
    public void testActualizarEgresoOtro() throws Exception {
        int idEgresoOtro = 1;
        int idTipoCosto = 1;
        String concepto = "Test Concepto";
        double valorUnitario = 10.0;
        int cantidad = 2;

        EgresosOtros egresoOtro = new EgresosOtros();
        egresoOtro.setId(idEgresoOtro);
        Optional<EgresosOtros> optionalEgresoOtro = Optional.of(egresoOtro);

        TipoCosto tipoCosto = new TipoCosto();
        tipoCosto.setId(idTipoCosto);
        Optional<TipoCosto> optionalTipoCosto = Optional.of(tipoCosto);

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1);
        egresoOtro.setPresupuesto(presupuesto);

        when(egresoOtroRepository.findById(idEgresoOtro)).thenReturn(optionalEgresoOtro);
        when(tipoCostoRepository.findById(idTipoCosto)).thenReturn(optionalTipoCosto);

        mockMvc.perform(MockMvcRequestBuilders.put("/egresoOtro/actualizar")
                .param("id", String.valueOf(idEgresoOtro))
                .param("idTipoCosto", String.valueOf(idTipoCosto))
                .param("concepto", concepto)
                .param("valorUnitario", String.valueOf(valorUnitario))
                .param("cantidad", String.valueOf(cantidad))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(egresoOtroRepository, times(1)).findById(idEgresoOtro);
        verify(tipoCostoRepository, times(1)).findById(idTipoCosto);
        verify(egresoOtroRepository, times(1)).save(any(EgresosOtros.class));
    }

    @Test
    public void testEliminarEgresoOtro() throws Exception {
        int idEgresoOtro = 1;
        EgresosOtros egresoOtro = new EgresosOtros();
        egresoOtro.setId(idEgresoOtro);

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1);
        egresoOtro.setPresupuesto(presupuesto);

        when(egresoOtroRepository.findById(idEgresoOtro)).thenReturn(Optional.of(egresoOtro));

        mockMvc.perform(MockMvcRequestBuilders.delete("/egresoOtro/eliminar")
                .param("id", String.valueOf(idEgresoOtro))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(egresoOtroRepository, times(1)).findById(idEgresoOtro);
        verify(egresoOtroRepository, times(1)).deleteById(idEgresoOtro);
    }

    @Test
    public void testListarEgresosOtrosPorPresupuesto() throws Exception {
        int idPresupuesto = 1;

        mockMvc.perform(MockMvcRequestBuilders.get("/egresoOtro/listarPorPresupuesto")
                .param("idPresupuesto", String.valueOf(idPresupuesto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(egresoOtroRepository, times(1)).findByPresupuestoId(idPresupuesto);
    }

}
