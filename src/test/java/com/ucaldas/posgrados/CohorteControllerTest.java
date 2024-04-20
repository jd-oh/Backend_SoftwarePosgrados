package com.ucaldas.posgrados;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
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
import org.springframework.web.context.WebApplicationContext;

import com.ucaldas.posgrados.Controller.CohorteController;
import com.ucaldas.posgrados.Entity.Cohorte;
import com.ucaldas.posgrados.Entity.Programa;
import com.ucaldas.posgrados.Repository.CohorteRepository;
import com.ucaldas.posgrados.Repository.ProgramaRepository;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CohorteController.class)
public class CohorteControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private CohorteRepository cohorteRepository;

    @MockBean
    private ProgramaRepository programaRepository;

    @InjectMocks
    private CohorteController cohorteController;

    @BeforeEach
    public void setup(WebApplicationContext wac) {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(cohorteController).build();
    }

    @Test
    public void testCrearCohorte() throws Exception {
        int idPrograma = 1;
        Programa programa = new Programa();
        programa.setId(idPrograma);

        when(programaRepository.findById(idPrograma)).thenReturn(Optional.of(programa));

        mockMvc.perform(MockMvcRequestBuilders.post("/cohorte/crear")
                .param("numero", "2022")
                .param("fecha", "01-01-2022")
                .param("idPrograma", String.valueOf(idPrograma))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(cohorteRepository, times(1)).save(any(Cohorte.class));
    }

    @Test
    public void testListarCohortes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cohorte/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testBuscarCohorte() throws Exception {
        int idCohorte = 1;
        Cohorte cohorte = new Cohorte();
        cohorte.setId(idCohorte);

        when(cohorteRepository.findById(idCohorte)).thenReturn(Optional.of(cohorte));

        mockMvc.perform(MockMvcRequestBuilders.get("/cohorte/buscar")
                .param("id", String.valueOf(idCohorte))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idCohorte));
    }

    @Test
    public void testEliminarCohorte() throws Exception {
        int idCohorte = 1;
        Cohorte cohorte = new Cohorte();
        cohorte.setId(idCohorte);

        when(cohorteRepository.findById(idCohorte)).thenReturn(Optional.of(cohorte));

        mockMvc.perform(MockMvcRequestBuilders.delete("/cohorte/eliminar")
                .param("id", String.valueOf(idCohorte))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    public void testActualizarCohorte() throws Exception {
        int idCohorte = 1;
        int idPrograma = 1;
        String numero = "2023";
        LocalDate fecha = LocalDate.parse("2023-01-01");
        Cohorte cohorte = new Cohorte();
        cohorte.setId(idCohorte);
        Programa programa = new Programa();
        programa.setId(idPrograma);

        when(cohorteRepository.findById(idCohorte)).thenReturn(Optional.of(cohorte));
        when(programaRepository.findById(idPrograma)).thenReturn(Optional.of(programa));

        mockMvc.perform(MockMvcRequestBuilders.put("/cohorte/actualizar")
                .param("numero", numero)
                .param("fecha", fecha.toString())
                .param("idCohorte", String.valueOf(idCohorte))
                .param("idPrograma", String.valueOf(idPrograma))
                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(content().string("OK"))
                .andExpect(status().isOk());

        verify(cohorteRepository, times(1)).save(any(Cohorte.class));
    }
}
