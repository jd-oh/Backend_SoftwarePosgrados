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

import com.ucaldas.posgrados.Controller.EgresosServNoDocentesController;
import com.ucaldas.posgrados.Controller.PresupuestoController;
import com.ucaldas.posgrados.Entity.EgresosServNoDocentes;
import com.ucaldas.posgrados.Entity.Presupuesto;
import com.ucaldas.posgrados.Entity.TipoCosto;
import com.ucaldas.posgrados.Repository.EgresosServNoDocentesRepository;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoCostoRepository;

@SpringBootTest
public class EgresosServNoDocentesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PresupuestoRepository presupuestoRepository;

    @Mock
    private EgresosServNoDocentesRepository egresoServNoDocenteRepository;

    @Mock
    private TipoCostoRepository tipoCostoRepository;

    @InjectMocks
    private EgresosServNoDocentesController egresosServNoDocentesController;

    @Mock
    private PresupuestoController presupuestoController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(egresosServNoDocentesController).build();
    }

    @Test
    public void testCrearEgresoServNoDocente() throws Exception {
        int idPresupuestoEjecucion = 1;
        String servicio = "Servicio 1";
        double valorUnitario = 10.0;
        int cantidad = 2;
        int idTipoCosto = 1;

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(idPresupuestoEjecucion);

        // Inicializar el conjunto de EgresosDescuentos
        presupuesto.setEgresosServNoDocentes(new HashSet<>());
        Optional<Presupuesto> optionalPresupuesto = Optional.of(presupuesto);

        TipoCosto tipoCosto = new TipoCosto();
        tipoCosto.setId(idTipoCosto);
        Optional<TipoCosto> optionalTipoCosto = Optional.of(tipoCosto);

        when(presupuestoRepository.findById(idPresupuestoEjecucion)).thenReturn(optionalPresupuesto);
        when(tipoCostoRepository.findById(idTipoCosto)).thenReturn(optionalTipoCosto);

        mockMvc.perform(MockMvcRequestBuilders.post("/egresoServNoDocente/crear")
                .param("idPresupuestoEjecucion", String.valueOf(idPresupuestoEjecucion))
                .param("servicio", servicio)
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
    public void testListarEgresosServNoDocentes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/egresoServNoDocente/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(egresoServNoDocenteRepository, times(1)).findAllByOrderByPresupuestoAsc();
    }

    @Test
    public void testBuscarEgresoServNoDocente() throws Exception {
        int idEgresoServNoDocente = 1;
        EgresosServNoDocentes egresoServNoDocente = new EgresosServNoDocentes();
        egresoServNoDocente.setId(idEgresoServNoDocente);

        when(egresoServNoDocenteRepository.findById(idEgresoServNoDocente))
                .thenReturn(Optional.of(egresoServNoDocente));

        mockMvc.perform(MockMvcRequestBuilders.get("/egresoServNoDocente/buscar")
                .param("id", String.valueOf(idEgresoServNoDocente))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(idEgresoServNoDocente));

        verify(egresoServNoDocenteRepository, times(1)).findById(idEgresoServNoDocente);
    }

    @Test
    public void testActualizarEgresoServNoDocente() throws Exception {
        int idEgresoServNoDocente = 1;
        int idTipoCosto = 1;
        String servicio = "Servicio 1";
        double valorUnitario = 10.0;
        int cantidad = 2;

        EgresosServNoDocentes egresoServNoDocente = new EgresosServNoDocentes();
        egresoServNoDocente.setId(idEgresoServNoDocente);
        Optional<EgresosServNoDocentes> optionalEgresoServNoDocente = Optional.of(egresoServNoDocente);

        TipoCosto tipoCosto = new TipoCosto();
        tipoCosto.setId(idTipoCosto);
        Optional<TipoCosto> optionalTipoCosto = Optional.of(tipoCosto);

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1);
        egresoServNoDocente.setPresupuesto(presupuesto);

        when(egresoServNoDocenteRepository.findById(idEgresoServNoDocente)).thenReturn(optionalEgresoServNoDocente);
        when(tipoCostoRepository.findById(idTipoCosto)).thenReturn(optionalTipoCosto);

        mockMvc.perform(MockMvcRequestBuilders.put("/egresoServNoDocente/actualizar")
                .param("id", String.valueOf(idEgresoServNoDocente))
                .param("idTipoCosto", String.valueOf(idTipoCosto))
                .param("servicio", servicio)
                .param("valorUnitario", String.valueOf(valorUnitario))
                .param("cantidad", String.valueOf(cantidad))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(egresoServNoDocenteRepository, times(1)).findById(idEgresoServNoDocente);
        verify(tipoCostoRepository, times(1)).findById(idTipoCosto);
        verify(egresoServNoDocenteRepository, times(1)).save(any(EgresosServNoDocentes.class));
    }

    @Test
    public void testEliminarEgresoServNoDocente() throws Exception {
        int idEgresoServNoDocente = 1;
        EgresosServNoDocentes egresoServNoDocente = new EgresosServNoDocentes();
        egresoServNoDocente.setId(idEgresoServNoDocente);

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1);
        egresoServNoDocente.setPresupuesto(presupuesto);

        when(egresoServNoDocenteRepository.findById(idEgresoServNoDocente))
                .thenReturn(Optional.of(egresoServNoDocente));

        mockMvc.perform(MockMvcRequestBuilders.delete("/egresoServNoDocente/eliminar")
                .param("id", String.valueOf(idEgresoServNoDocente))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(egresoServNoDocenteRepository, times(1)).findById(idEgresoServNoDocente);
        verify(egresoServNoDocenteRepository, times(1)).deleteById(idEgresoServNoDocente);
    }

    @Test
    public void testListarEgresosServNoDocentesPorPresupuesto() throws Exception {
        int idPresupuesto = 1;

        mockMvc.perform(MockMvcRequestBuilders.get("/egresoServNoDocente/listarPorPresupuesto")
                .param("idPresupuesto", String.valueOf(idPresupuesto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(egresoServNoDocenteRepository, times(1)).findByPresupuestoId(idPresupuesto);
    }

}
