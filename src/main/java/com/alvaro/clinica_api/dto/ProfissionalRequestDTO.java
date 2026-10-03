package com.alvaro.clinica_api.dto;

import jakarta.validation.constraints.NotBlank;

public record ProfissionalRequestDTO(
    @NotBlank(message = "O nome é obrigatório")
    String nome,

    @NotBlank(message = "A especialidade é obrigatória")
    String especialidade
) {}