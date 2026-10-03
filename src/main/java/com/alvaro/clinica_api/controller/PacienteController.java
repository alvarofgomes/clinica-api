package com.alvaro.clinica_api.controller;

import java.util.List;
import com.alvaro.clinica_api.dto.PacienteRequestDTO;
import com.alvaro.clinica_api.dto.PacienteResponseDTO;
import com.alvaro.clinica_api.model.Paciente;
import com.alvaro.clinica_api.service.PacienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pacientes")
@Tag(name = "Pacientes", description = "Cadastro e consulta de pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    @Operation(summary = "Cadastra um paciente", description = "O CPF não pode estar duplicado")
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
    @Operation(summary = "Busca um paciente por id")
    public ResponseEntity<PacienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(PacienteResponseDTO.de(pacienteService.buscarPorId(id)));
    }

    @GetMapping
    @Operation(summary = "Lista todos os pacientes")
    public ResponseEntity<List<PacienteResponseDTO>> listar() {
        List<PacienteResponseDTO> lista = pacienteService.listarTodos()
            .stream()
            .map(PacienteResponseDTO::de)
            .toList();
        return ResponseEntity.ok(lista);
    }
}