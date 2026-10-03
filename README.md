# API de Agendamentos de Clínica

API REST para controle de agendamentos de consultas, desenvolvida com Java e Spring Boot.

Permite cadastrar pacientes e profissionais, criar agendamentos com validação de regras de
negócio, listar com filtros e cancelar mantendo o histórico.

---

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Data JPA / Hibernate
- PostgreSQL (banco principal)
- H2 (testes automatizados)
- Bean Validation
- JUnit 5 + Mockito
- Swagger / OpenAPI
- Maven

---

## Pré-requisitos

- **JDK 21** ou superior
- **PostgreSQL** em execução (porta padrão 5432)
- **Maven** (ou use o wrapper `mvnw` incluso no projeto)

---

## Como executar

### 1. Clone o repositório

```bash
git clone https://github.com/alvarofgomes/clinica-api.git
cd clinica-api
```

### 2. Crie o banco de dados

No PostgreSQL, crie um banco chamado `clinica`:

```sql
CREATE DATABASE clinica;
```

Pelo terminal:

```bash
psql -U postgres -c "CREATE DATABASE clinica;"
```

> As tabelas são criadas automaticamente pelo Hibernate na primeira execução.

### 3. Configure as credenciais (se necessário)

A aplicação usa os seguintes valores por padrão:

| Variável      | Padrão      | Descrição            |
| ------------- | ----------- | -------------------- |
| `DB_HOST`     | `localhost` | Host do PostgreSQL   |
| `DB_PORT`     | `5432`      | Porta do PostgreSQL  |
| `DB_NAME`     | `clinica`   | Nome do banco        |
| `DB_USER`     | `postgres`  | Usuário              |
| `DB_PASSWORD` | `1234`      | Senha                |

Se a sua configuração for diferente, defina as variáveis de ambiente antes de executar.
Exemplo no Linux/macOS:

```bash
export DB_USER=meu_usuario
export DB_PASSWORD=minha_senha
```

No Windows (PowerShell):

```powershell
$env:DB_USER="meu_usuario"
$env:DB_PASSWORD="minha_senha"
```

### 4. Execute a aplicação

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

### 5. Execute os testes

```bash
./mvnw test
```

---

## Documentação dos endpoints

Com a aplicação em execução, a documentação interativa (Swagger UI) fica disponível em:

**http://localhost:8080/swagger-ui.html**

Por ela é possível visualizar e testar todos os endpoints diretamente no navegador.

---

## Endpoints

### Pacientes

| Método | Rota              | Descrição              |
| ------ | ----------------- | ---------------------- |
| POST   | `/pacientes`      | Cadastra um paciente   |
| GET    | `/pacientes`      | Lista todos            |
| GET    | `/pacientes/{id}` | Busca por id           |

### Profissionais

| Método | Rota                  | Descrição                |
| ------ | --------------------- | ------------------------ |
| POST   | `/profissionais`      | Cadastra um profissional |
| GET    | `/profissionais`      | Lista todos              |
| GET    | `/profissionais/{id}` | Busca por id             |

### Agendamentos

| Método | Rota                           | Descrição                     |
| ------ | ------------------------------ | ----------------------------- |
| POST   | `/agendamentos`                | Cria um agendamento           |
| GET    | `/agendamentos`                | Lista (com filtros opcionais) |
| GET    | `/agendamentos/{id}`           | Busca por id                  |
| PATCH  | `/agendamentos/{id}/cancelar`  | Cancela, registrando o motivo |

**Filtros disponíveis na listagem** (query params, todos opcionais):

```
GET /agendamentos?pacienteId=1
GET /agendamentos?profissionalId=1
GET /agendamentos?status=CANCELADO
```

---

## Exemplos de uso

### Cadastrar um profissional

```http
POST /profissionais
Content-Type: application/json

{
  "nome": "Dra. Ana Lima",
  "especialidade": "Cardiologia"
}
```

### Cadastrar um paciente

```http
POST /pacientes
Content-Type: application/json

{
  "nome": "Alvaro Gomes",
  "cpf": "12345678900",
  "email": "alvaro@exemplo.com",
  "telefone": "81999990000"
}
```

### Criar um agendamento

```http
POST /agendamentos
Content-Type: application/json

{
  "pacienteId": 1,
  "profissionalId": 1,
  "dataHora": "2026-12-15T14:00:00",
  "tipoAtendimento": "CONSULTA"
}
```

Valores aceitos em `tipoAtendimento`: `CONSULTA`, `RETORNO`, `EXAME`.

### Cancelar um agendamento

```http
PATCH /agendamentos/1/cancelar
Content-Type: application/json

{
  "motivo": "Paciente não poderá comparecer"
}
```

O registro é mantido: o status passa a `CANCELADO` e o motivo é persistido.

---

## Regras de negócio

| Regra                                                            | Resposta em caso de violação |
| ---------------------------------------------------------------- | ---------------------------- |
| Um profissional não pode ter dois agendamentos no mesmo horário   | `409 Conflict`               |
| Não é permitido agendar com data/hora no passado                  | `400 Bad Request`            |
| O cancelamento exige um motivo                                    | `400 Bad Request`            |
| Não é permitido cancelar um agendamento já cancelado              | `400 Bad Request`            |
| Paciente ou profissional inexistente                              | `404 Not Found`              |
| CPF de paciente duplicado                                         | `400 Bad Request`            |

Um horário liberado por cancelamento volta a ficar disponível para novos agendamentos.

### Formato das respostas de erro

```json
{
  "timestamp": "2026-10-03T11:34:05.899",
  "status": 409,
  "erro": "O profissional já possui um agendamento neste horário"
}
```

Erros de validação incluem os campos com problema:

```json
{
  "timestamp": "2026-10-03T11:35:51.905",
  "status": 400,
  "erro": "Dados inválidos",
  "campos": {
    "pacienteId": "O paciente é obrigatório",
    "dataHora": "A data e hora são obrigatórias"
  }
}
```

---

## Estrutura do projeto

```
src/main/java/com/alvaro/clinica_api/
├── controller/   endpoints REST
├── service/      regras de negócio
├── repository/   acesso ao banco
├── model/        entidades JPA
├── enums/        TipoAtendimento e StatusAgendamento
├── dto/          objetos de entrada e saída
└── exception/    exceções de domínio e tratamento centralizado
```

---

## Testes

O projeto inclui testes unitários das regras de negócio, com JUnit 5 e Mockito:

- Agendamento com data no passado é rejeitado
- Agendamento em horário já ocupado pelo profissional é rejeitado
- Cancelamento altera o status para `CANCELADO` e registra o motivo

```bash
./mvnw test
```

---

## Decisões técnicas

As principais decisões de modelagem e implementação, o que foi priorizado e o que ficou de
fora estão documentados em [DECISOES.md](DECISOES.md).