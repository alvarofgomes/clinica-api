package com.alvaro.clinica_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI clinicaOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API de Agendamentos de Clínica")
                .version("1.0")
                .description("""
                    API REST para controle de agendamentos de consultas.

                    Regras de negócio aplicadas:
                    - Um profissional não pode ter dois agendamentos no mesmo horário
                    - Não é permitido agendar com data/hora no passado
                    - O cancelamento exige um motivo e mantém o registro no histórico
                    """));
    }
}