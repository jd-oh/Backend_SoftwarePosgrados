package com.ucaldas.posgrados;

import static org.mockito.Mockito.*;

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

import com.ucaldas.posgrados.Controller.PresupuestoController;
import com.ucaldas.posgrados.Entity.Cohorte;
import com.ucaldas.posgrados.Entity.Presupuesto;
import com.ucaldas.posgrados.Repository.CohorteRepository;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;

@SpringBootTest
public class PresupuestoControllerTest {

	private MockMvc mockMvc;

	@Mock
	private CohorteRepository cohorteRepository;

	@Mock
	private PresupuestoRepository presupuestoRepository;

	@InjectMocks
	private PresupuestoController presupuestoController;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(presupuestoController).build();
	}

	@Test
	public void testCrearPresupuesto() throws Exception {
		int idCohorte = 1;
		Cohorte cohorte = new Cohorte();
		cohorte.setId(idCohorte);

		when(cohorteRepository.findById(idCohorte)).thenReturn(Optional.of(cohorte));

		mockMvc.perform(MockMvcRequestBuilders.post("/presupuesto/crear")
				.param("idCohorte", String.valueOf(idCohorte))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("OK"));

		verify(presupuestoRepository, times(1)).save(any(Presupuesto.class));
	}

	@Test
	public void testBuscarPresupuesto() throws Exception {
		int idPresupuesto = 1;
		Presupuesto presupuesto = new Presupuesto();
		presupuesto.setId(idPresupuesto);

		when(presupuestoRepository.findById(idPresupuesto)).thenReturn(Optional.of(presupuesto));

		mockMvc.perform(MockMvcRequestBuilders.get("/presupuesto/buscar")
				.param("id", String.valueOf(idPresupuesto))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(idPresupuesto));
	}

	@Test
	public void testActualizarPresupuesto() throws Exception {
		int idPresupuesto = 1;
		Presupuesto presupuesto = new Presupuesto();
		presupuesto.setId(idPresupuesto);

		Cohorte cohorte = new Cohorte();
		cohorte.setId(1);
		presupuesto.setCohorte(cohorte);

		when(presupuestoRepository.findById(idPresupuesto)).thenReturn(Optional.of(presupuesto));
		when(cohorteRepository.findById(cohorte.getId())).thenReturn(Optional.of(cohorte));

		mockMvc.perform(MockMvcRequestBuilders.put("/presupuesto/actualizar")
				.param("id", String.valueOf(idPresupuesto))
				.param("idCohorte", String.valueOf(cohorte.getId()))

				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("OK"));
	}

	@Test
	public void testEliminarPresupuesto() throws Exception {
		int idPresupuesto = 1;
		Presupuesto presupuesto = new Presupuesto();
		presupuesto.setId(idPresupuesto);

		when(presupuestoRepository.findById(idPresupuesto)).thenReturn(Optional.of(presupuesto));

		mockMvc.perform(MockMvcRequestBuilders.delete("/presupuesto/eliminar")
				.param("id", String.valueOf(idPresupuesto))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("OK"));
	}

	@Test
	public void testListarPresupuestos() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/presupuesto/listar")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testBuscarPresupuestosPorCohorte() throws Exception {
		int idCohorte = 53;
		Cohorte cohorte = new Cohorte();
		cohorte.setId(idCohorte);

		when(cohorteRepository.findById(idCohorte)).thenReturn(Optional.of(cohorte));

		mockMvc.perform(MockMvcRequestBuilders.get("/presupuesto/buscarPorCohorte")
				.param("idCohorte", String.valueOf(idCohorte))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

}
