package com.ucaldas.posgrados;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import com.ucaldas.posgrados.Controller.TipoCostoController;
import com.ucaldas.posgrados.Entity.TipoCosto;
import com.ucaldas.posgrados.Repository.TipoCostoRepository;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class TipoCostoControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private TipoCostoRepository tipoCostoRepository;

    @InjectMocks
    private TipoCostoController tipoCostoController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(tipoCostoController).build();
    }

    @Test
    public void testCrearTipoCosto() throws Exception {
        String nombreTipo = "Tipo Test";

        mockMvc.perform(post("/tipoCosto/crear")
                .param("nombreTipo", nombreTipo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        TipoCosto tipoCosto = new TipoCosto();
        tipoCosto.setNombreTipo(nombreTipo);
        verify(tipoCostoRepository, times(1)).save(any(TipoCosto.class));
    }

    @Test
    public void testEliminarTipoCosto() throws Exception {
        int idTipoCosto = 1;

        mockMvc.perform(delete("/tipoCosto/eliminar")
                .param("id", String.valueOf(idTipoCosto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(tipoCostoRepository, times(1)).deleteById(idTipoCosto);
    }

    @Test
    public void testActualizarTipoCosto() throws Exception {
        int idTipoCosto = 1;
        String nombreTipo = "Tipo Actualizado";

        TipoCosto tipoCosto = new TipoCosto();
        tipoCosto.setId(idTipoCosto);
        tipoCosto.setNombreTipo(nombreTipo);

        when(tipoCostoRepository.findById(idTipoCosto)).thenReturn(Optional.of(tipoCosto));

        mockMvc.perform(put("/tipoCosto/actualizar")
                .param("id", String.valueOf(idTipoCosto))
                .param("nombreTipo", nombreTipo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(tipoCostoRepository, times(1)).save(tipoCosto);
    }

    @Test
    public void testListarTipoCosto() throws Exception {
        List<TipoCosto> tipoCostos = new ArrayList<>();

        TipoCosto tipoCosto = new TipoCosto();
        tipoCosto.setId(1);
        tipoCosto.setNombreTipo("Tipo 1");
        tipoCostos.add(tipoCosto);

        TipoCosto tipoCosto2 = new TipoCosto();
        tipoCosto2.setId(2);
        tipoCosto2.setNombreTipo("Tipo 2");
        tipoCostos.add(tipoCosto2);

        when(tipoCostoRepository.findAllByOrderByNombreTipoAsc()).thenReturn(tipoCostos);

        mockMvc.perform(get("/tipoCosto/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(tipoCostos)));
    }
}
