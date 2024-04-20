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

import com.ucaldas.posgrados.Controller.DepartamentoController;
import com.ucaldas.posgrados.Entity.Departamento;
import com.ucaldas.posgrados.Entity.Facultad;
import com.ucaldas.posgrados.Repository.DepartamentoRepository;
import com.ucaldas.posgrados.Repository.FacultadRepository;

@WebMvcTest(DepartamentoController.class)
public class DepartamentoControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private DepartamentoRepository departamentoRepository;

    @MockBean
    private FacultadRepository facultadRepository;

    @InjectMocks
    private DepartamentoController departamentoController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(departamentoController).build();
    }

    @Test
    public void testCrearDepartamento() throws Exception {
        int idFacultad = 1;
        String nombre = "Departamento de Ingeniería";
        Facultad facultad = new Facultad();
        facultad.setId(idFacultad);

        when(facultadRepository.findById(idFacultad)).thenReturn(Optional.of(facultad));

        mockMvc.perform(MockMvcRequestBuilders.post("/departamento/crear")
                .param("nombre", nombre)
                .param("idFacultad", String.valueOf(idFacultad))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(departamentoRepository, times(1)).save(any(Departamento.class));
    }

    @Test
    public void testListarDepartamentos() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/departamento/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testBuscarDepartamento() throws Exception {
        int idDepartamento = 1;
        Departamento departamento = new Departamento();
        departamento.setId(idDepartamento);

        when(departamentoRepository.findById(idDepartamento)).thenReturn(Optional.of(departamento));

        mockMvc.perform(MockMvcRequestBuilders.get("/departamento/buscar")
                .param("id", String.valueOf(idDepartamento))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(departamento.toString()));
    }

    @Test
    public void testEliminarDepartamento() throws Exception {
        int idDepartamento = 1;

        mockMvc.perform(MockMvcRequestBuilders.delete("/departamento/eliminar")
                .param("id", String.valueOf(idDepartamento))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(departamentoRepository, times(1)).deleteById(idDepartamento);
    }

    @Test
    public void testActualizarDepartamento() throws Exception {
        int idDepartamento = 1;
        int idFacultad = 1;
        String nombre = "Departamento de Ciencias";

        Departamento departamento = new Departamento();
        departamento.setId(idDepartamento);

        Facultad facultad = new Facultad();
        facultad.setId(idFacultad);

        when(departamentoRepository.findById(idDepartamento)).thenReturn(Optional.of(departamento));
        when(facultadRepository.findById(idFacultad)).thenReturn(Optional.of(facultad));

        mockMvc.perform(MockMvcRequestBuilders.put("/departamento/actualizar")
                .param("nombre", nombre)
                .param("idDepartamento", String.valueOf(idDepartamento))
                .param("idFacultad", String.valueOf(idFacultad))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(departamentoRepository, times(1)).save(any(Departamento.class));
    }

}
