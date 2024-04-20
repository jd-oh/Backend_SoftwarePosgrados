package com.ucaldas.posgrados;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.times;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucaldas.posgrados.Controller.TipoTransferenciaController;
import com.ucaldas.posgrados.Entity.TipoTransferencia;
import com.ucaldas.posgrados.Repository.TipoTransferenciaRepository;

@SpringBootTest
public class TipoTransferenciaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TipoTransferenciaRepository tipoTransferenciaRepository;

    @InjectMocks
    private TipoTransferenciaController tipoTransferenciaController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(tipoTransferenciaController).build();
    }

    @Test
    public void testCrear() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tipoTransferencia/crear")
                .param("nombreTipo", "Tipo 1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(tipoTransferenciaRepository).save(any(TipoTransferencia.class));
    }

    @Test
    public void testEliminar() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/tipoTransferencia/eliminar")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OK"));

        verify(tipoTransferenciaRepository).deleteById(anyInt());
    }

    @Test
    public void testActualizarTipoTransferencia() throws Exception {
        int idTipoTransferencia = 1;
        String nombreTipo = "Tipo Actualizado";

        TipoTransferencia tipoTransferencia = new TipoTransferencia();
        tipoTransferencia.setId(idTipoTransferencia);
        tipoTransferencia.setNombreTipo(nombreTipo);

        when(tipoTransferenciaRepository.findById(idTipoTransferencia)).thenReturn(Optional.of(tipoTransferencia));

        mockMvc.perform(put("/tipoTransferencia/actualizar")
                .param("id", String.valueOf(idTipoTransferencia))
                .param("nombreTipo", nombreTipo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(tipoTransferenciaRepository, times(1)).save(tipoTransferencia);
    }

    @Test
    public void testListarTipoTransferencia() throws Exception {
        List<TipoTransferencia> tipoTransferencias = new ArrayList<>();

        TipoTransferencia tipoTransferencia = new TipoTransferencia();
        tipoTransferencia.setId(1);
        tipoTransferencia.setNombreTipo("Tipo 1");
        tipoTransferencias.add(tipoTransferencia);

        TipoTransferencia tipoTransferencia2 = new TipoTransferencia();
        tipoTransferencia2.setId(2);
        tipoTransferencia2.setNombreTipo("Tipo 2");
        tipoTransferencias.add(tipoTransferencia2);

        when(tipoTransferenciaRepository.findAllByOrderByNombreTipoAsc()).thenReturn(tipoTransferencias);

        mockMvc.perform(get("/tipoTransferencia/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(tipoTransferencias)));
    }

}
