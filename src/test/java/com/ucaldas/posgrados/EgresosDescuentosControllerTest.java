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

import com.ucaldas.posgrados.Controller.EgresosDescuentosController;
import com.ucaldas.posgrados.Controller.EgresosTransferenciasController;
import com.ucaldas.posgrados.Controller.PresupuestoController;
import com.ucaldas.posgrados.Entity.EgresosDescuentos;
import com.ucaldas.posgrados.Entity.Presupuesto;
import com.ucaldas.posgrados.Entity.TipoDescuento;
import com.ucaldas.posgrados.Repository.EgresosDescuentosRepository;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoDescuentoRepository;

@SpringBootTest
public class EgresosDescuentosControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PresupuestoRepository presupuestoRepository;

    @Mock
    private EgresosDescuentosRepository egresoDescuentoRepository;

    @Mock
    private TipoDescuentoRepository tipoDescuentoRepository;

    @Mock
    private PresupuestoController presupuestoController;

    @Mock
    private EgresosTransferenciasController egresosTransferenciasController;

    @InjectMocks
    private EgresosDescuentosController egresosDescuentosController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(egresosDescuentosController).build();
    }

    @Test
    public void testCrearEgresoDescuento() throws Exception {
        int idPresupuestoEjecucion = 102;
        int numEstudiantes = 10;
        double valor = 100.0;
        int numPeriodos = 2;
        int idTipoDescuento = 1;

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(idPresupuestoEjecucion);

        // Inicializar el conjunto de EgresosDescuentos
        presupuesto.setEgresosDescuentos(new HashSet<>());

        Optional<Presupuesto> optionalPresupuesto = Optional.of(presupuesto);
        when(presupuestoRepository.findById(idPresupuestoEjecucion)).thenReturn(optionalPresupuesto);

        TipoDescuento tipoDescuento = new TipoDescuento();
        tipoDescuento.setId(idTipoDescuento);
        Optional<TipoDescuento> optionalTipoDescuento = Optional.of(tipoDescuento);
        when(tipoDescuentoRepository.findById(idTipoDescuento)).thenReturn(optionalTipoDescuento);

        mockMvc.perform(MockMvcRequestBuilders.post("/egresoDescuento/crear")
                .param("idPresupuestoEjecucion", String.valueOf(idPresupuestoEjecucion))
                .param("numEstudiantes", String.valueOf(numEstudiantes))
                .param("valor", String.valueOf(valor))
                .param("numPeriodos", String.valueOf(numPeriodos))
                .param("idTipoDescuento", String.valueOf(idTipoDescuento))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(presupuestoRepository, times(1)).findById(idPresupuestoEjecucion);
        verify(tipoDescuentoRepository, times(1)).findById(idTipoDescuento);
        verify(presupuestoRepository, times(1)).save(any(Presupuesto.class));
    }

    @Test
    public void testListarEgresosDescuentos() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/egresoDescuento/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(egresoDescuentoRepository, times(1)).findAllByOrderByPresupuestoAsc();
    }

    @Test
    public void testBuscarEgresoDescuento() throws Exception {
        int idEgresoDescuento = 1;
        EgresosDescuentos egresoDescuento = new EgresosDescuentos();
        egresoDescuento.setId(idEgresoDescuento);
        Optional<EgresosDescuentos> optionalEgresoDescuento = Optional.of(egresoDescuento);
        when(egresoDescuentoRepository.findById(idEgresoDescuento)).thenReturn(optionalEgresoDescuento);

        mockMvc.perform(MockMvcRequestBuilders.get("/egresoDescuento/buscar")
                .param("id", String.valueOf(idEgresoDescuento))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(idEgresoDescuento));

        verify(egresoDescuentoRepository, times(1)).findById(idEgresoDescuento);
    }

    @Test
    public void testActualizarEgresoDescuento() throws Exception {
        int idEgresoDescuento = 1;
        int numEstudiantes = 10;
        double valor = 100.0;
        int numPeriodos = 2;
        int idTipoDescuento = 1;

        EgresosDescuentos egresoDescuento = new EgresosDescuentos();
        egresoDescuento.setId(idEgresoDescuento);

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1);
        egresoDescuento.setPresupuesto(presupuesto);
        Optional<EgresosDescuentos> optionalEgresoDescuento = Optional.of(egresoDescuento);
        when(egresoDescuentoRepository.findById(idEgresoDescuento)).thenReturn(optionalEgresoDescuento);

        TipoDescuento tipoDescuento = new TipoDescuento();
        tipoDescuento.setId(idTipoDescuento);
        Optional<TipoDescuento> optionalTipoDescuento = Optional.of(tipoDescuento);
        when(tipoDescuentoRepository.findById(idTipoDescuento)).thenReturn(optionalTipoDescuento);

        mockMvc.perform(MockMvcRequestBuilders.put("/egresoDescuento/actualizar")
                .param("id", String.valueOf(idEgresoDescuento))
                .param("numEstudiantes", String.valueOf(numEstudiantes))
                .param("valor", String.valueOf(valor))
                .param("numPeriodos", String.valueOf(numPeriodos))
                .param("idTipoDescuento", String.valueOf(idTipoDescuento))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(egresoDescuentoRepository, times(1)).findById(idEgresoDescuento);
        verify(tipoDescuentoRepository, times(1)).findById(idTipoDescuento);
        verify(egresoDescuentoRepository, times(1)).save(any(EgresosDescuentos.class));
    }

    @Test
    public void testEliminarEgresoDescuento() throws Exception {
        int idEgresoDescuento = 1;
        EgresosDescuentos egresoDescuento = new EgresosDescuentos();
        egresoDescuento.setId(idEgresoDescuento);

        // Crear un objeto Presupuesto y establecerlo en el objeto EgresosDescuentos
        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(1); // Asegúrate de establecer un ID que exista en tus datos de prueba

        egresoDescuento.setPresupuesto(presupuesto);

        Optional<EgresosDescuentos> optionalEgresoDescuento = Optional.of(egresoDescuento);
        when(egresoDescuentoRepository.findById(idEgresoDescuento)).thenReturn(optionalEgresoDescuento);

        mockMvc.perform(MockMvcRequestBuilders.delete("/egresoDescuento/eliminar")
                .param("id", String.valueOf(idEgresoDescuento))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(egresoDescuentoRepository, times(1)).findById(idEgresoDescuento);
        verify(egresoDescuentoRepository, times(1)).deleteById(idEgresoDescuento);
    }

    @Test
    public void testListarEgresosDescuentosPorPresupuesto() throws Exception {
        int idPresupuesto = 1;

        mockMvc.perform(MockMvcRequestBuilders.get("/egresoDescuento/listarPorPresupuesto")
                .param("idPresupuesto", String.valueOf(idPresupuesto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(egresoDescuentoRepository, times(1)).findByPresupuestoId(idPresupuesto);
    }
}
