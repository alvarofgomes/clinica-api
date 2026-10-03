package com.alvaro.clinica_api.controller;

import com.alvaro.clinica_api.model.Paciente;
import com.alvaro.clinica_api.model.Profissional;
import com.alvaro.clinica_api.repository.PacienteRepository;
import com.alvaro.clinica_api.repository.ProfissionalRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de integração da API de agendamentos")
class AgendamentoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    private Long pacienteId;
    private Long profissionalId;

    @BeforeEach
    void prepararDados() {
        Paciente paciente = new Paciente();
        paciente.setNome("Paciente Teste");
        paciente.setCpf("00011122233");
        pacienteId = pacienteRepository.save(paciente).getId();

        Profissional profissional = new Profissional();
        profissional.setNome("Profissional Teste");
        profissional.setEspecialidade("Clinica Geral");
        profissionalId = profissionalRepository.save(profissional).getId();
    }

    private String dataFutura() {
        return LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

    @Test
    @DisplayName("Deve criar um agendamento e retornar 201")
    void deveCriarAgendamento() throws Exception {
        String json = """
            {
              "pacienteId": %d,
              "profissionalId": %d,
              "dataHora": "%s",
              "tipoAtendimento": "CONSULTA"
            }
            """.formatted(pacienteId, profissionalId, dataFutura());

        mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("AGENDADO"))
            .andExpect(jsonPath("$.nomePaciente").value("Paciente Teste"));
    }

    @Test
    @DisplayName("Deve retornar 409 ao agendar em horário já ocupado")
    void deveRetornar409EmHorarioOcupado() throws Exception {
        String data = dataFutura();
        String json = """
            {
              "pacienteId": %d,
              "profissionalId": %d,
              "dataHora": "%s",
              "tipoAtendimento": "CONSULTA"
            }
            """.formatted(pacienteId, profissionalId, data);

        mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve cancelar um agendamento e manter o registro")
    void deveCancelarAgendamento() throws Exception {
        String json = """
            {
              "pacienteId": %d,
              "profissionalId": %d,
              "dataHora": "%s",
              "tipoAtendimento": "CONSULTA"
            }
            """.formatted(pacienteId, profissionalId, dataFutura());

        String resposta = mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        Long id = Long.valueOf(resposta.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(patch("/agendamentos/" + id + "/cancelar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"motivo\": \"Teste de cancelamento\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELADO"))
            .andExpect(jsonPath("$.motivoCancelamento").value("Teste de cancelamento"));

        mockMvc.perform(get("/agendamentos/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELADO"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao agendar com data no passado")
    void deveRetornar400ComDataNoPassado() throws Exception {
        String dataPassada = LocalDateTime.now().minusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        String json = """
            {
              "pacienteId": %d,
              "profissionalId": %d,
              "dataHora": "%s",
              "tipoAtendimento": "CONSULTA"
            }
            """.formatted(pacienteId, profissionalId, dataPassada);

        mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }
}