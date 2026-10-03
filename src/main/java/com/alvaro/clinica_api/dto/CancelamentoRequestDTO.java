package com.alvaro.clinica_api.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelamentoRequestDTO(
    @NotBlank(message = "O motivo do cancelamento é obrigatório")
    String motivo
) {}