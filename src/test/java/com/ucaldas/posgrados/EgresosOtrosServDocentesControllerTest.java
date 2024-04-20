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

import com.ucaldas.posgrados.Controller.EgresosOtrosServDocentesController;
import com.ucaldas.posgrados.Controller.PresupuestoController;
import com.ucaldas.posgrados.Entity.EgresosOtrosServDocentes;
import com.ucaldas.posgrados.Entity.Presupuesto;
import com.ucaldas.posgrados.Entity.TipoCosto;
import com.ucaldas.posgrados.Repository.EgresosOtrosServDocentesRepository;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoCostoRepository;

@SpringBootTest
public class EgresosOtrosServDocentesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PresupuestoRepository presupuestoRepository;

    @Mock
    private EgresosOtrosServDocentesRepository egresoOtrosServDocenteRepository;

    @Mock
    private TipoCostoRepository tipoCostoRepository;

    @InjectMocks
    private EgresosOtrosServDocentesController egresosOtrosServDocentesController;

    @Mock
    private PresupuestoController presupuestoController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(egresosOtrosServDocentesController).build();
    }

    @Test
    public void testCrearEgresoOtrosServDocente() throws Exception {
        int idPresupuestoEjecucion = 1;
        int idTipoCosto = 1;
        String servicio = "Servicio";
        String descripcion = "Descripción";
        int numHoras = 10;
        double valorTotal = 100.0;

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(idPresupuestoEjecucion);

        // Inicializar el conjunto de EgresosDescuentos
        presupuesto.setEgresosOtrosServDocentes(new HashSet<>());
        Optional<Presupuesto> optionalPresupuesto = Optional.of(presupuesto);
        when(presupuestoRepository.findById(idPresupuestoEjecucion)).thenReturn(optionalPresupuesto);

        TipoCosto tipoCosto = new TipoCosto();
        tipoCosto.setId(idTipoCosto);
        Optional<TipoCosto> optionalTipoCosto = Optional.of(tipoCosto);
        when(tipoCostoRepository.findById(idTipoCosto)).thenReturn(optionalTipoCosto);

        mockMvc.perform(MockMvcRequestBuilders.post("/egresoOtrosServDocente/crear")
                .param("idPresupuestoEjecucion", String.valueOf(idPresupuestoEjecucion))
                .param("servicio", servicio)
                .param("descripcion", descripcion)
                .param("numHoras", String.valueOf(numHoras))
                .param("valorTotal", String.valueOf(valorTotal))
                .param("idTipoCosto", String.valueOf(idTipoCosto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(presupuestoRepository, times(1)).findById(idPresupuestoEjecucion);
        verify(tipoCostoRepository, times(1)).findById(idTipoCosto);
        verify(presupuestoRepository, times(1)).save(any(Presupuesto.class));
    }

    @Test
    public void testListarEgresosOtrosServDocentes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/egresoOtrosServDocente/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(egresoOtrosServDocenteRepository, times(1)).findAllByOrderByPresupuestoAsc();
    }

    @Test
    public void testBuscarEgresoOtrosServDocente() throws Exception {
        int idEgresoOtrosServDocente = 1;
        EgresosOtrosServDocentes egresoOtrosServDocentes = new EgresosOtrosServDocentes();
        egresoOtrosServDocentes.setId(idEgresoOtrosServDocente);

        when(egresoOtrosServDocenteRepository.findById(idEgresoOtrosServDocente))
                .thenReturn(Optional.of(egresoOtrosServDocentes));

        mockMvc.perform(MockMvcRequestBuilders.get("/egresoOtrosServDocente/buscar")
                .param("id", String.valueOf(idEgresoOtrosServDocente))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(idEgresoOtrosServDocente));

        verify(egresoOtrosServDocenteRepository, times(1)).findById(idEgresoOtrosServDocente);
    }

    @Test
    public void testActualizarEgresoOtrosServDocente() throws Exception {
        int idEgresoOtrosServDocente = 1;
        int idTipoCosto = 1;
        String servicio = "Servicio";
        String descripcion = "Descripción";
        int numHoras = 10;
        double valorTotal = 100.0;

        EgresosOtrosServDocentes egresoOtrosServDocentes = new EgresosOtrosServDocentes();
        egresoOtrosServDocentes.setId(idEgresoOtrosServDocente);

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1);
        egresoOtrosServDocentes.setPresupuesto(presupuesto);

        Optional<EgresosOtrosServDocentes> optionalEgreso = Optional.of(egresoOtrosServDocentes);
        when(egresoOtrosServDocenteRepository.findById(idEgresoOtrosServDocente)).thenReturn(optionalEgreso);

        TipoCosto tipoCosto = new TipoCosto();
        tipoCosto.setId(idTipoCosto);
        Optional<TipoCosto> optionalTipoCosto = Optional.of(tipoCosto);
        when(tipoCostoRepository.findById(idTipoCosto)).thenReturn(optionalTipoCosto);

        mockMvc.perform(MockMvcRequestBuilders.put("/egresoOtrosServDocente/actualizar")
                .param("id", String.valueOf(idEgresoOtrosServDocente))
                .param("idTipoCosto", String.valueOf(idTipoCosto))
                .param("servicio", servicio)
                .param("descripcion", descripcion)
                .param("numHoras", String.valueOf(numHoras))
                .param("valorTotal", String.valueOf(valorTotal))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(egresoOtrosServDocenteRepository, times(1)).findById(idEgresoOtrosServDocente);
        verify(tipoCostoRepository, times(1)).findById(idTipoCosto);
        verify(egresoOtrosServDocenteRepository, times(1)).save(any(EgresosOtrosServDocentes.class));
    }

    @Test
    public void testEliminarEgresoOtrosServDocente() throws Exception {
        int idEgresoOtrosServDocente = 1;
        EgresosOtrosServDocentes egresoOtrosServDocentes = new EgresosOtrosServDocentes();
        egresoOtrosServDocentes.setId(idEgresoOtrosServDocente);

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1);
        egresoOtrosServDocentes.setPresupuesto(presupuesto);

        when(egresoOtrosServDocenteRepository.findById(idEgresoOtrosServDocente))
                .thenReturn(Optional.of(egresoOtrosServDocentes));

        mockMvc.perform(MockMvcRequestBuilders.delete("/egresoOtrosServDocente/eliminar")
                .param("id", String.valueOf(idEgresoOtrosServDocente))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(egresoOtrosServDocenteRepository, times(1)).findById(idEgresoOtrosServDocente);
        verify(egresoOtrosServDocenteRepository, times(1)).deleteById(idEgresoOtrosServDocente);
    }

    @Test
    public void testListarEgresosOtrosServDocentesPorPresupuesto() throws Exception {
        int idPresupuesto = 1;

        mockMvc.perform(MockMvcRequestBuilders.get("/egresoOtrosServDocente/listarPorPresupuesto")
                .param("idPresupuesto", String.valueOf(idPresupuesto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(egresoOtrosServDocenteRepository, times(1)).findByPresupuestoId(idPresupuesto);
    }

}
