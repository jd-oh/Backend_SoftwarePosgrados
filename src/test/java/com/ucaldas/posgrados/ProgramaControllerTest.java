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

import com.ucaldas.posgrados.Controller.ProgramaController;
import com.ucaldas.posgrados.Entity.Departamento;
import com.ucaldas.posgrados.Entity.Programa;
import com.ucaldas.posgrados.Repository.DepartamentoRepository;
import com.ucaldas.posgrados.Repository.ProgramaRepository;

@WebMvcTest(ProgramaController.class)
public class ProgramaControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private ProgramaRepository programaRepository;

    @MockBean
    private DepartamentoRepository departamentoRepository;

    @InjectMocks
    private ProgramaController programaController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(programaController).build();
    }

    @Test
    public void testCrearPrograma() throws Exception {
        int idDepartamento = 1;
        Departamento departamento = new Departamento();
        departamento.setId(idDepartamento);

        when(departamentoRepository.findById(idDepartamento)).thenReturn(Optional.of(departamento));

        mockMvc.perform(MockMvcRequestBuilders.post("/programa/crear")
                .param("nombre", "Programa Test")
                .param("idDepartamento", String.valueOf(idDepartamento))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(programaRepository, times(1)).save(any(Programa.class));
    }

    @Test
    public void testListarProgramas() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/programa/listar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testBuscarPrograma() throws Exception {
        int idPrograma = 1;
        Programa programa = new Programa();
        programa.setId(idPrograma);

        when(programaRepository.findById(idPrograma)).thenReturn(Optional.of(programa));

        mockMvc.perform(MockMvcRequestBuilders.get("/programa/buscar")
                .param("id", String.valueOf(idPrograma))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(programa.toString()));
    }

    @Test
    public void testEliminarPrograma() throws Exception {
        int idPrograma = 1;
        Programa programa = new Programa();
        programa.setId(idPrograma);

        when(programaRepository.findById(idPrograma)).thenReturn(Optional.of(programa));

        mockMvc.perform(MockMvcRequestBuilders.delete("/programa/eliminar")
                .param("id", String.valueOf(idPrograma))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(programaRepository, times(1)).deleteById(idPrograma);
    }

    @Test
    public void testActualizarPrograma() throws Exception {
        int idPrograma = 1;
        int idDepartamento = 1;
        Programa programa = new Programa();
        programa.setId(idPrograma);

        Departamento departamento = new Departamento();
        departamento.setId(idDepartamento);

        when(programaRepository.findById(idPrograma)).thenReturn(Optional.of(programa));
        when(departamentoRepository.findById(idDepartamento)).thenReturn(Optional.of(departamento));

        mockMvc.perform(MockMvcRequestBuilders.put("/programa/actualizar")
                .param("nombre", "Programa Actualizado")
                .param("idPrograma", String.valueOf(idPrograma))
                .param("idDepartamento", String.valueOf(idDepartamento))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(programaRepository, times(1)).save(any(Programa.class));
    }
}
