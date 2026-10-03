package com.alvaro.clinica_api.service;

import java.time.LocalDateTime;
import java.util.List;

import com.alvaro.clinica_api.exception.ConflitoHorarioException;
import com.alvaro.clinica_api.exception.RecursoNaoEncontradoException;
import com.alvaro.clinica_api.exception.RegraNegocioException;
import com.alvaro.clinica_api.model.Agendamento;
import com.alvaro.clinica_api.model.Paciente;
import com.alvaro.clinica_api.model.Profissional;
import com.alvaro.clinica_api.enums.StatusAgendamento;
import com.alvaro.clinica_api.enums.TipoAtendimento;
import com.alvaro.clinica_api.repository.AgendamentoRepository;
import com.alvaro.clinica_api.repository.PacienteRepository;
import com.alvaro.clinica_api.repository.ProfissionalRepository;

import org.springframework.stereotype.Service;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalRepository profissionalRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                              PacienteRepository pacienteRepository,
                              ProfissionalRepository profissionalRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.pacienteRepository = pacienteRepository;
        this.profissionalRepository = profissionalRepository;
    }

    public Agendamento criar(Long pacienteId, Long profissionalId,
                             LocalDateTime dataHora, TipoAtendimento tipoAtendimento) {

        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new RegraNegocioException("Não é possível agendar em data/hora passada");
        }

        Paciente paciente = pacienteRepository.findById(pacienteId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado"));

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrado"));

        boolean horarioOcupado = agendamentoRepository
            .existsByProfissionalIdAndDataHoraAndStatusNot(
                profissionalId, dataHora, StatusAgendamento.CANCELADO);

        if (horarioOcupado) {
            throw new ConflitoHorarioException(
                "O profissional já possui um agendamento neste horário");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setPaciente(paciente);
        agendamento.setProfissional(profissional);
        agendamento.setDataHora(dataHora);
        agendamento.setTipoAtendimento(tipoAtendimento);
        agendamento.setStatus(StatusAgendamento.AGENDADO);

        return agendamentoRepository.save(agendamento);
    }

    public List<Agendamento> listar(Long pacienteId, Long profissionalId,
                                    StatusAgendamento status) {
        if (pacienteId != null) {
            return agendamentoRepository.findByPacienteId(pacienteId);
        }
        if (profissionalId != null) {
            return agendamentoRepository.findByProfissionalId(profissionalId);
        }
        if (status != null) {
            return agendamentoRepository.findByStatus(status);
        }
        return agendamentoRepository.findAll();
    }

    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado"));
    }

    public Agendamento cancelar(Long id, String motivo) {
        Agendamento agendamento = agendamentoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado"));

        if (agendamento.getStatus() == StatusAgendamento.CANCELADO) {
            throw new RegraNegocioException("Este agendamento já está cancelado");
        }

        agendamento.setStatus(StatusAgendamento.CANCELADO);
        agendamento.setMotivoCancelamento(motivo);

        return agendamentoRepository.save(agendamento);
    }
}