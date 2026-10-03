package com.alvaro.clinica_api.service;

import java.util.List;

import com.alvaro.clinica_api.exception.RecursoNaoEncontradoException;
import com.alvaro.clinica_api.exception.RegraNegocioException;
import com.alvaro.clinica_api.model.Paciente;
import com.alvaro.clinica_api.repository.PacienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PacienteService {

    private static final Logger log = LoggerFactory.getLogger(PacienteService.class);

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public Paciente criar(Paciente paciente) {
        if (pacienteRepository.existsByCpf(paciente.getCpf())) {
            log.warn("Tentativa de cadastro com CPF duplicado");
            throw new RegraNegocioException("Já existe um paciente cadastrado com este CPF");
        }
        Paciente salvo = pacienteRepository.save(paciente);
        log.info("Paciente cadastrado: id={}", salvo.getId());
        return salvo;
    }

    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado"));
    }

    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }
}