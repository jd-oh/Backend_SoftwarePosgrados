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
import com.ucaldas.posgrados.Controller.TipoInversionController;
import com.ucaldas.posgrados.Entity.TipoInversion;
import com.ucaldas.posgrados.Repository.TipoInversionRepository;

@SpringBootTest
public class TipoInversionControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private TipoInversionRepository tipoInversionRepository;

    @InjectMocks
    private TipoInversionController tipoInversionController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(tipoInversionController).build();
    }

    @Test
    public void testCrearTipoInversion() throws Exception {
        String nombreTipo = "Tipo Test";

        mockMvc.perform(post("/tipoInversion/crear")
                .param("nombreTipo", nombreTipo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        TipoInversion tipoInversion = new TipoInversion();
        tipoInversion.setNombreTipo(nombreTipo);
        verify(tipoInversionRepository, times(1)).save(any(TipoInversion.class));
    }

    @Test
    public void testEliminarTipoInversion() throws Exception {
        int idTipoInversion = 1;

        mockMvc.perform(delete("/tipoInversion/eliminar")
                .param("id", String.valueOf(idTipoInversion))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(tipoInversionRepository, times(1)).deleteById(idTipoInversion);
    }

    @Test
    public void testActualizarTipoInversion() throws Exception {
        int idTipoInversion = 1;
        String nombreTipo = "Tipo Actualizado";

        TipoInversion tipoInversion = new TipoInversion();
        tipoInversion.setId(idTipoInversion);
        tipoInversion.setNombreTipo(nombreTipo);

        when(tipoInversionRepository.findById(idTipoInversion)).thenReturn(Optional.of(tipoInversion));

        mockMvc.perform(put("/tipoInversion/actualizar")
                .param("id", String.valueOf(idTipoInversion))
                .param("nombreTipo", nombreTipo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(tipoInversionRepository, times(1)).save(tipoInversion);
    }

    @Test
    public void testListarTipoInversion() throws Exception {
        List<TipoInversion> tipoInversiones = new ArrayList<>();

        TipoInversion tipoInversion = new TipoInversion();
        tipoInversion.setId(1);
        tipoInversion.setNombreTipo("Tipo 1");
        tipoInversiones.add(tipoInversion);

        TipoInversion tipoInversion2 = new TipoInversion();
        tipoInversion2.setId(2);
        tipoInversion2.setNombreTipo("Tipo 2");
        tipoInversiones.add(tipoInversion2);

        when(tipoInversionRepository.findAllByOrderByNombreTipoAsc()).thenReturn(tipoInversiones);

        mockMvc.perform(get("/tipoInversion/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(tipoInversiones)));
    }
}
