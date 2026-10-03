package com.alvaro.clinica_api.dto;

import com.alvaro.clinica_api.model.Paciente;

public record PacienteResponseDTO(Long id, String nome, String cpf,
                                   String email, String telefone) {

    public static PacienteResponseDTO de(Paciente paciente) {
        return new PacienteResponseDTO(
            paciente.getId(), paciente.getNome(), paciente.getCpf(),
            paciente.getEmail(), paciente.getTelefone());
    }
}