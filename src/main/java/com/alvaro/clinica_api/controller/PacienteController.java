package com.alvaro.clinica_api.controller;

import java.util.List;
import com.alvaro.clinica_api.dto.PacienteRequestDTO;
import com.alvaro.clinica_api.dto.PacienteResponseDTO;
import com.alvaro.clinica_api.model.Paciente;
import com.alvaro.clinica_api.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    public ResponseEntity<PacienteResponseDTO> criar(@RequestBody @Valid PacienteRequestDTO dados) {
        Paciente paciente = new Paciente();
        paciente.setNome(dados.nome());
        paciente.setCpf(dados.cpf());
        paciente.setEmail(dados.email());
        paciente.setTelefone(dados.telefone());

        Paciente salvo = pacienteService.criar(paciente);
        return ResponseEntity.status(201).body(PacienteResponseDTO.de(salvo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(PacienteResponseDTO.de(pacienteService.buscarPorId(id)));
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> listar() {
        List<PacienteResponseDTO> lista = pacienteService.listarTodos()
            .stream()
            .map(PacienteResponseDTO::de)
            .toList();
        return ResponseEntity.ok(lista);
    }
}