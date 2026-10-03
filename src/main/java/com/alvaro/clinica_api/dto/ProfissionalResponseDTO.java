package com.alvaro.clinica_api.dto;

import com.alvaro.clinica_api.model.Profissional;

public record ProfissionalResponseDTO(Long id, String nome, String especialidade) {

    public static ProfissionalResponseDTO de(Profissional profissional) {
        return new ProfissionalResponseDTO(
            profissional.getId(), profissional.getNome(), profissional.getEspecialidade());
    }
}