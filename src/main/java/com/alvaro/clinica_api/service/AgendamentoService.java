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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AgendamentoService {

    private static final Logger log = LoggerFactory.getLogger(AgendamentoService.class);

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

        log.info("Criando agendamento: paciente={}, profissional={}, dataHora={}",
                 pacienteId, profissionalId, dataHora);

        if (dataHora.isBefore(LocalDateTime.now())) {
            log.warn("Tentativa de agendamento em data passada: {}", dataHora);
            throw new RegraNegocioException("Não é possível agendar em data/hora passada");
        }

        Paciente paciente = pacienteRepository.findById(pacienteId)
            .orElseThrow(() -> {
                log.warn("Paciente não encontrado: id={}", pacienteId);
                return new RecursoNaoEncontradoException("Paciente não encontrado");
            });

        Profissional profissional = profissionalRepository.findById(profissionalId)
            .orElseThrow(() -> {
                log.warn("Profissional não encontrado: id={}", profissionalId);
                return new RecursoNaoEncontradoException("Profissional não encontrado");
            });

        boolean horarioOcupado = agendamentoRepository
            .existsByProfissionalIdAndDataHoraAndStatusNot(
                profissionalId, dataHora, StatusAgendamento.CANCELADO);

        if (horarioOcupado) {
            log.warn("Conflito de horário: profissional={}, dataHora={}", profissionalId, dataHora);
            throw new ConflitoHorarioException(
                "O profissional já possui um agendamento neste horário");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setPaciente(paciente);
        agendamento.setProfissional(profissional);
        agendamento.setDataHora(dataHora);
        agendamento.setTipoAtendimento(tipoAtendimento);
        agendamento.setStatus(StatusAgendamento.AGENDADO);

        Agendamento salvo = agendamentoRepository.save(agendamento);
        log.info("Agendamento criado com sucesso: id={}", salvo.getId());
        return salvo;
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
        log.info("Cancelando agendamento: id={}", id);

        Agendamento agendamento = agendamentoRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Agendamento não encontrado para cancelamento: id={}", id);
                return new RecursoNaoEncontradoException("Agendamento não encontrado");
            });

        if (agendamento.getStatus() == StatusAgendamento.CANCELADO) {
            log.warn("Tentativa de cancelar agendamento já cancelado: id={}", id);
            throw new RegraNegocioException("Este agendamento já está cancelado");
        }

        agendamento.setStatus(StatusAgendamento.CANCELADO);
        agendamento.setMotivoCancelamento(motivo);

        Agendamento cancelado = agendamentoRepository.save(agendamento);
        log.info("Agendamento cancelado: id={}", id);
        return cancelado;
    }
}