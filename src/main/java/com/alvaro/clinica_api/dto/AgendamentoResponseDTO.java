package com.alvaro.clinica_api.dto;

import java.time.LocalDateTime;
import com.alvaro.clinica_api.model.Agendamento;
import com.alvaro.clinica_api.enums.StatusAgendamento;
import com.alvaro.clinica_api.enums.TipoAtendimento;

public record AgendamentoResponseDTO(
    Long id,
    String nomePaciente,
    String nomeProfissional,
    LocalDateTime dataHora,
    TipoAtendimento tipoAtendimento,
    StatusAgendamento status,
    String motivoCancelamento
) {
    public static AgendamentoResponseDTO de(Agendamento agendamento) {
        return new AgendamentoResponseDTO(
            agendamento.getId(),
            agendamento.getPaciente().getNome(),
            agendamento.getProfissional().getNome(),
            agendamento.getDataHora(),
            agendamento.getTipoAtendimento(),
            agendamento.getStatus(),
            agendamento.getMotivoCancelamento());
    }
}