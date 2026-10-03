package com.alvaro.clinica_api.repository;

import com.alvaro.clinica_api.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {
}