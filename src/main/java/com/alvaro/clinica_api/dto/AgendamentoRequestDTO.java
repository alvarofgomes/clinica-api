package com.alvaro.clinica_api.dto;

import java.time.LocalDateTime;
import com.alvaro.clinica_api.enums.TipoAtendimento;
import jakarta.validation.constraints.NotNull;

public record AgendamentoRequestDTO(
    @NotNull(message = "O paciente é obrigatório")
    Long pacienteId,

    @NotNull(message = "O profissional é obrigatório")
    Long profissionalId,

    @NotNull(message = "A data e hora são obrigatórias")
    LocalDateTime dataHora,

    @NotNull(message = "O tipo de atendimento é obrigatório")
    TipoAtendimento tipoAtendimento
) {}