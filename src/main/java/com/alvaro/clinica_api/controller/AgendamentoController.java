package com.alvaro.clinica_api.controller;

import java.util.List;
import com.alvaro.clinica_api.dto.AgendamentoRequestDTO;
import com.alvaro.clinica_api.dto.AgendamentoResponseDTO;
import com.alvaro.clinica_api.dto.CancelamentoRequestDTO;
import com.alvaro.clinica_api.model.Agendamento;
import com.alvaro.clinica_api.enums.StatusAgendamento;
import com.alvaro.clinica_api.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> criar(
            @RequestBody @Valid AgendamentoRequestDTO dados) {
        Agendamento agendamento = agendamentoService.criar(
            dados.pacienteId(), dados.profissionalId(),
            dados.dataHora(), dados.tipoAtendimento());
        return ResponseEntity.status(201).body(AgendamentoResponseDTO.de(agendamento));
    }

    @GetMapping
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
    public ResponseEntity<AgendamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        Agendamento agendamento = agendamentoService.buscarPorId(id);
        return ResponseEntity.ok(AgendamentoResponseDTO.de(agendamento));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AgendamentoResponseDTO> cancelar(
            @PathVariable Long id,
            @RequestBody @Valid CancelamentoRequestDTO dados) {
        Agendamento cancelado = agendamentoService.cancelar(id, dados.motivo());
        return ResponseEntity.ok(AgendamentoResponseDTO.de(cancelado));
    }
}