package com.alvaro.clinica_api.controller;

import java.util.List;
import com.alvaro.clinica_api.dto.AgendamentoRequestDTO;
import com.alvaro.clinica_api.dto.AgendamentoResponseDTO;
import com.alvaro.clinica_api.dto.CancelamentoRequestDTO;
import com.alvaro.clinica_api.model.Agendamento;
import com.alvaro.clinica_api.enums.StatusAgendamento;
import com.alvaro.clinica_api.service.AgendamentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agendamentos")
@Tag(name = "Agendamentos", description = "Criação, consulta e cancelamento de agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping
    @Operation(summary = "Cria um agendamento",
        description = "Valida se o profissional está livre no horário e se a data não está no passado")
    public ResponseEntity<AgendamentoResponseDTO> criar(
            @RequestBody @Valid AgendamentoRequestDTO dados) {
        Agendamento agendamento = agendamentoService.criar(
            dados.pacienteId(), dados.profissionalId(),
            dados.dataHora(), dados.tipoAtendimento());
        return ResponseEntity.status(201).body(AgendamentoResponseDTO.de(agendamento));
    }

    @GetMapping
    @Operation(summary = "Lista agendamentos",
        description = "Permite filtrar por paciente, profissional ou status")
    public ResponseEntity<List<AgendamentoResponseDTO>> listar(
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) Long profissionalId,
            @RequestParam(required = false) StatusAgendamento status) {

        List<AgendamentoResponseDTO> lista = agendamentoService
            .listar(pacienteId, profissionalId, status)
            .stream()
            .map(AgendamentoResponseDTO::de)
            .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um agendamento por id")
    public ResponseEntity<AgendamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        Agendamento agendamento = agendamentoService.buscarPorId(id);
        return ResponseEntity.ok(AgendamentoResponseDTO.de(agendamento));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancela um agendamento",
        description = "Altera o status para CANCELADO e registra o motivo, mantendo o registro")
    public ResponseEntity<AgendamentoResponseDTO> cancelar(
            @PathVariable Long id,
            @RequestBody @Valid CancelamentoRequestDTO dados) {
        Agendamento cancelado = agendamentoService.cancelar(id, dados.motivo());
        return ResponseEntity.ok(AgendamentoResponseDTO.de(cancelado));
    }
}