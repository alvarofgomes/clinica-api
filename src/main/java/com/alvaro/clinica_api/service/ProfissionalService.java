package com.alvaro.clinica_api.service;

import java.util.List;

import com.alvaro.clinica_api.exception.RecursoNaoEncontradoException;
import com.alvaro.clinica_api.model.Profissional;
import com.alvaro.clinica_api.repository.ProfissionalRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;

    public ProfissionalService(ProfissionalRepository profissionalRepository) {
        this.profissionalRepository = profissionalRepository;
    }

    public Profissional criar(Profissional profissional) {
        return profissionalRepository.save(profissional);
    }

    public List<Profissional> listarTodos() {
        return profissionalRepository.findAll();
    }

    public Profissional buscarPorId(Long id) {
        return profissionalRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrado"));
    }

}