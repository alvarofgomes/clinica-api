package com.alvaro.clinica_api.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.alvaro.clinica_api.exception.ConflitoHorarioException;
import com.alvaro.clinica_api.exception.RegraNegocioException;
import com.alvaro.clinica_api.model.Agendamento;
import com.alvaro.clinica_api.model.Paciente;
import com.alvaro.clinica_api.model.Profissional;
import com.alvaro.clinica_api.enums.StatusAgendamento;
import com.alvaro.clinica_api.enums.TipoAtendimento;
import com.alvaro.clinica_api.repository.AgendamentoRepository;
import com.alvaro.clinica_api.repository.PacienteRepository;
import com.alvaro.clinica_api.repository.ProfissionalRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes das regras de negócio de agendamento")
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private ProfissionalRepository profissionalRepository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    @Test
    @DisplayName("Não deve permitir agendamento com data e hora no passado")
    void naoDevePermitirAgendamentoNoPassado() {
        LocalDateTime dataPassada = LocalDateTime.now().minusDays(1);

        RegraNegocioException excecao = assertThrows(
            RegraNegocioException.class,
            () -> agendamentoService.criar(1L, 1L, dataPassada, TipoAtendimento.CONSULTA)
        );

        assertEquals("Não é possível agendar em data/hora passada", excecao.getMessage());
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve permitir dois agendamentos do mesmo profissional no mesmo horário")
    void naoDevePermitirConflitoDeHorario() {
        LocalDateTime dataFutura = LocalDateTime.now().plusDays(5);

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(new Paciente()));
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(new Profissional()));
        when(agendamentoRepository.existsByProfissionalIdAndDataHoraAndStatusNot(
                1L, dataFutura, StatusAgendamento.CANCELADO)).thenReturn(true);

        ConflitoHorarioException excecao = assertThrows(
            ConflitoHorarioException.class,
            () -> agendamentoService.criar(1L, 1L, dataFutura, TipoAtendimento.CONSULTA)
        );

        assertEquals("O profissional já possui um agendamento neste horário", excecao.getMessage());
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cancelamento deve alterar o status para CANCELADO e registrar o motivo")
    void cancelamentoDeveAlterarStatusERegistrarMotivo() {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setStatus(StatusAgendamento.AGENDADO);

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);

        String motivo = "Paciente não poderá comparecer";
        Agendamento cancelado = agendamentoService.cancelar(1L, motivo);

        assertEquals(StatusAgendamento.CANCELADO, cancelado.getStatus());
        assertEquals(motivo, cancelado.getMotivoCancelamento());
        verify(agendamentoRepository).save(agendamento);
    }
}