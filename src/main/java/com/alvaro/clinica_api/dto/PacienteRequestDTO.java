package com.alvaro.clinica_api.dto;

import jakarta.validation.constraints.NotBlank;

public record PacienteRequestDTO(
    @NotBlank(message = "O nome é obrigatório")
    String nome,

    @NotBlank(message = "O CPF é obrigatório")
    String cpf,

    String email,
    String telefone
) {}