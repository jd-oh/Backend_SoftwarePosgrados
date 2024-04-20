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
import com.ucaldas.posgrados.Controller.TipoCompensacionController;
import com.ucaldas.posgrados.Entity.TipoCompensacion;
import com.ucaldas.posgrados.Repository.TipoCompensacionRepository;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class TipoCompensacionControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private TipoCompensacionRepository tipoCompensacionRepository;

    @InjectMocks
    private TipoCompensacionController tipoCompensacionController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(tipoCompensacionController).build();
    }

    @Test
    public void testCrearTipoCompensacion() throws Exception {
        String nombreTipo = "Tipo Test";

        mockMvc.perform(post("/tipoCompensacion/crear")
                .param("nombreTipo", nombreTipo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        TipoCompensacion tipoCompensacion = new TipoCompensacion();
        tipoCompensacion.setNombreTipo(nombreTipo);
        verify(tipoCompensacionRepository, times(1)).save(any(TipoCompensacion.class));
    }

    @Test
    public void testEliminarTipoCompensacion() throws Exception {
        int idTipoCompensacion = 1;

        mockMvc.perform(delete("/tipoCompensacion/eliminar")
                .param("id", String.valueOf(idTipoCompensacion))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(tipoCompensacionRepository, times(1)).deleteById(idTipoCompensacion);
    }

    @Test
    public void testActualizarTipoCompensacion() throws Exception {
        int idTipoCompensacion = 1;
        String nombreTipo = "Tipo Actualizado";

        TipoCompensacion tipoCompensacion = new TipoCompensacion();
        tipoCompensacion.setId(idTipoCompensacion);
        tipoCompensacion.setNombreTipo(nombreTipo);

        when(tipoCompensacionRepository.findById(idTipoCompensacion)).thenReturn(Optional.of(tipoCompensacion));

        mockMvc.perform(put("/tipoCompensacion/actualizar")
                .param("id", String.valueOf(idTipoCompensacion))
                .param("nombreTipo", nombreTipo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(tipoCompensacionRepository, times(1)).save(tipoCompensacion);
    }

    @Test
    public void testListarTipoCompensacion() throws Exception {
        List<TipoCompensacion> tipoCompensaciones = new ArrayList<>();
        TipoCompensacion tipo1 = new TipoCompensacion();
        tipo1.setId(1);
        tipo1.setNombreTipo("Tipo 1");
        tipoCompensaciones.add(tipo1);

        TipoCompensacion tipo2 = new TipoCompensacion();
        tipo2.setId(2);
        tipo2.setNombreTipo("Tipo 2");
        tipoCompensaciones.add(tipo2);

        when(tipoCompensacionRepository.findAllByOrderByNombreTipoAsc()).thenReturn(tipoCompensaciones);

        mockMvc.perform(get("/tipoCompensacion/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(tipoCompensaciones)));
    }
}
