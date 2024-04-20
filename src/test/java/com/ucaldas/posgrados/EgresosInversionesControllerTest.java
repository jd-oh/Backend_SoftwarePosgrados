package com.ucaldas.posgrados;

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

import com.ucaldas.posgrados.Controller.EgresosInversionesController;
import com.ucaldas.posgrados.Controller.PresupuestoController;
import com.ucaldas.posgrados.Entity.EgresosInversiones;
import com.ucaldas.posgrados.Entity.Presupuesto;
import com.ucaldas.posgrados.Entity.TipoInversion;
import com.ucaldas.posgrados.Repository.EgresosInversionesRepository;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoInversionRepository;

@SpringBootTest
public class EgresosInversionesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PresupuestoRepository presupuestoRepository;

    @Mock
    private EgresosInversionesRepository egresoInversionRepository;

    @Mock
    private TipoInversionRepository tipoInversionRepository;

    @Mock
    private PresupuestoController presupuestoController;

    @InjectMocks
    private EgresosInversionesController egresosInversionesController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(egresosInversionesController).build();
    }

    @Test
    public void testCrearEgresoInversion() throws Exception {
        int idPresupuesto = 1;
        int idTipoInversion = 1;
        String concepto = "Concepto Test";
        double valor = 1000.0;

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(idPresupuesto);

        TipoInversion tipoInversion = new TipoInversion();
        tipoInversion.setId(idTipoInversion);

        // Inicializar el conjunto de EgresosDescuentos
        presupuesto.setEgresosInversiones(new HashSet<>());

        when(presupuestoRepository.findById(idPresupuesto)).thenReturn(Optional.of(presupuesto));
        when(tipoInversionRepository.findById(idTipoInversion)).thenReturn(Optional.of(tipoInversion));

        mockMvc.perform(MockMvcRequestBuilders.post("/egresoInversion/crear")
                .param("idPresupuestoEjecucion", String.valueOf(idPresupuesto))
                .param("concepto", concepto)
                .param("valor", String.valueOf(valor))
                .param("idTipoInversion", String.valueOf(idTipoInversion))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(presupuestoRepository, times(1)).save(any(Presupuesto.class));
    }

    @Test
    public void testActualizarEgresoInversion() throws Exception {
        int idEgresoInversion = 1;
        String concepto = "Concepto Actualizado";
        double valor = 2000.0;
        int idTipoInversion = 2;

        EgresosInversiones egresoInversion = new EgresosInversiones();
        egresoInversion.setId(idEgresoInversion);

        TipoInversion tipoInversion = new TipoInversion();
        tipoInversion.setId(idTipoInversion);

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1);
        egresoInversion.setPresupuesto(presupuesto);

        when(egresoInversionRepository.findById(idEgresoInversion)).thenReturn(Optional.of(egresoInversion));
        when(tipoInversionRepository.findById(idTipoInversion)).thenReturn(Optional.of(tipoInversion));

        mockMvc.perform(MockMvcRequestBuilders.put("/egresoInversion/actualizar")
                .param("id", String.valueOf(idEgresoInversion))
                .param("concepto", concepto)
                .param("valor", String.valueOf(valor))
                .param("idTipoInversion", String.valueOf(idTipoInversion))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(egresoInversionRepository, times(1)).save(any(EgresosInversiones.class));
    }

    @Test
    public void testEliminarEgresoInversion() throws Exception {
        int idEgresoInversion = 1;
        EgresosInversiones egresoGeneral = new EgresosInversiones();
        egresoGeneral.setId(idEgresoInversion);

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1);
        egresoGeneral.setPresupuesto(presupuesto);

        when(egresoInversionRepository.findById(idEgresoInversion)).thenReturn(Optional.of(egresoGeneral));

        mockMvc.perform(MockMvcRequestBuilders.delete("/egresoInversion/eliminar")
                .param("id", String.valueOf(idEgresoInversion))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(egresoInversionRepository, times(1)).deleteById(idEgresoInversion);
    }

    @Test
    public void testListarEgresosInversiones() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/egresoInversion/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(egresoInversionRepository, times(1)).findAllByOrderByPresupuestoAsc();
    }

}
