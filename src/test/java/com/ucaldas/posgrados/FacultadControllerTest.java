package com.ucaldas.posgrados;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ucaldas.posgrados.Controller.FacultadController;
import com.ucaldas.posgrados.Entity.Facultad;
import com.ucaldas.posgrados.Repository.FacultadRepository;

@WebMvcTest(FacultadController.class)
public class FacultadControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private FacultadRepository facultadRepository;

    @InjectMocks
    private FacultadController facultadController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(facultadController).build();
    }

    @Test
    public void testCrearFacultad() throws Exception {
        String nombre = "Facultad de Ingeniería";

        mockMvc.perform(MockMvcRequestBuilders.post("/facultad/crear")
                .param("nombre", nombre)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("FOK"));

        verify(facultadRepository, times(1)).save(any(Facultad.class));
    }

    @Test
    public void testListarFacultades() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/facultad/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(facultadRepository, times(1)).findAllByOrderByNombreAsc();
    }

    @Test
    public void testBuscarFacultad() throws Exception {
        int idFacultad = 1;
        Facultad facultad = new Facultad();
        facultad.setId(idFacultad);

        when(facultadRepository.findById(idFacultad)).thenReturn(Optional.of(facultad));

        mockMvc.perform(MockMvcRequestBuilders.get("/facultad/buscar")
                .param("id", String.valueOf(idFacultad))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(facultad.toString()));
    }

    @Test
    public void testEliminarFacultad() throws Exception {
        int idFacultad = 1;

        mockMvc.perform(MockMvcRequestBuilders.delete("/facultad/eliminar")
                .param("id", String.valueOf(idFacultad))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(facultadRepository, times(1)).deleteById(idFacultad);
    }

    @Test
    public void testActualizarFacultad() throws Exception {
        int idFacultad = 1;
        String nombre = "Facultad de Ciencias Naturales";

        Facultad facultad = new Facultad();
        facultad.setId(idFacultad);

        when(facultadRepository.findById(idFacultad)).thenReturn(Optional.of(facultad));

        mockMvc.perform(MockMvcRequestBuilders.put("/facultad/actualizar")
                .param("nombre", nombre)
                .param("id", String.valueOf(idFacultad))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(facultadRepository, times(1)).save(facultad);
    }
}
