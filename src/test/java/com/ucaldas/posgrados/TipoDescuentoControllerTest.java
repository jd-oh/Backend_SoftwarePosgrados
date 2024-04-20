package com.ucaldas.posgrados;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucaldas.posgrados.Controller.TipoDescuentoController;
import com.ucaldas.posgrados.Entity.TipoDescuento;
import com.ucaldas.posgrados.Repository.TipoDescuentoRepository;

@SpringBootTest
public class TipoDescuentoControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private TipoDescuentoRepository tipoDescuentoRepository;

    @InjectMocks
    private TipoDescuentoController tipoDescuentoController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(tipoDescuentoController).build();
    }

    @Test
    public void testCrearTipoDescuento() throws Exception {
        String nombreTipo = "Tipo Test";

        mockMvc.perform(post("/tipoDescuento/crear")
                .param("nombreTipo", nombreTipo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        TipoDescuento tipoDescuento = new TipoDescuento();
        tipoDescuento.setNombreTipo(nombreTipo);
        verify(tipoDescuentoRepository, times(1)).save(any(TipoDescuento.class));
    }

    @Test
    public void testEliminarTipoDescuento() throws Exception {
        int idTipoDescuento = 1;

        mockMvc.perform(delete("/tipoDescuento/eliminar")
                .param("id", String.valueOf(idTipoDescuento))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(tipoDescuentoRepository, times(1)).deleteById(idTipoDescuento);
    }

    @Test
    public void testActualizarTipoDescuento() throws Exception {
        int idTipoDescuento = 1;
        String nombreTipo = "Tipo Actualizado";

        TipoDescuento tipoDescuento = new TipoDescuento();
        tipoDescuento.setId(idTipoDescuento);
        tipoDescuento.setNombreTipo(nombreTipo);

        when(tipoDescuentoRepository.findById(idTipoDescuento)).thenReturn(Optional.of(tipoDescuento));

        mockMvc.perform(put("/tipoDescuento/actualizar")
                .param("id", String.valueOf(idTipoDescuento))
                .param("nombreTipo", nombreTipo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(tipoDescuentoRepository, times(1)).save(tipoDescuento);
    }

    @Test
    public void testListarTipoDescuento() throws Exception {
        List<TipoDescuento> tipoDescuentos = new ArrayList<>();

        TipoDescuento tipoDescuento = new TipoDescuento();
        tipoDescuento.setId(1);
        tipoDescuento.setNombreTipo("Tipo 1");
        tipoDescuentos.add(tipoDescuento);

        TipoDescuento tipoDescuento2 = new TipoDescuento();
        tipoDescuento2.setId(2);
        tipoDescuento2.setNombreTipo("Tipo 2");
        tipoDescuentos.add(tipoDescuento2);

        when(tipoDescuentoRepository.findAllByOrderByNombreTipoAsc()).thenReturn(tipoDescuentos);

        mockMvc.perform(get("/tipoDescuento/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(tipoDescuentos)));
    }
}
