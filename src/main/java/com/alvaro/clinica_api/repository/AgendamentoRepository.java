package com.alvaro.clinica_api.repository;

import java.time.LocalDateTime;
import java.util.List;
import com.alvaro.clinica_api.model.Agendamento;
import com.alvaro.clinica_api.enums.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    boolean existsByProfissionalIdAndDataHoraAndStatusNot(
            Long profissionalId,
            LocalDateTime dataHora,
            StatusAgendamento status);

    List<Agendamento> findByPacienteId(Long pacienteId);

    List<Agendamento> findByProfissionalId(Long profissionalId);

    List<Agendamento> findByStatus(StatusAgendamento status);
}