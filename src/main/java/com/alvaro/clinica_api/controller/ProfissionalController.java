package com.alvaro.clinica_api.controller;

import java.util.List;
import com.alvaro.clinica_api.dto.ProfissionalRequestDTO;
import com.alvaro.clinica_api.dto.ProfissionalResponseDTO;
import com.alvaro.clinica_api.model.Profissional;
import com.alvaro.clinica_api.service.ProfissionalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalController {

    private final ProfissionalService profissionalService;

    public ProfissionalController(ProfissionalService profissionalService) {
        this.profissionalService = profissionalService;
    }

    @PostMapping
    public ResponseEntity<ProfissionalResponseDTO> criar(
            @RequestBody @Valid ProfissionalRequestDTO dados) {
        Profissional profissional = new Profissional();
        profissional.setNome(dados.nome());
        profissional.setEspecialidade(dados.especialidade());

        Profissional salvo = profissionalService.criar(profissional);
        return ResponseEntity.status(201).body(ProfissionalResponseDTO.de(salvo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ProfissionalResponseDTO.de(profissionalService.buscarPorId(id)));
    }

    @GetMapping
    public ResponseEntity<List<ProfissionalResponseDTO>> listar() {
        List<ProfissionalResponseDTO> lista = profissionalService.listarTodos()
            .stream()
            .map(ProfissionalResponseDTO::de)
            .toList();
        return ResponseEntity.ok(lista);
    }
}