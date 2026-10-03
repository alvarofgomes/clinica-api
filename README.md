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
- **Docker** (recomendado) **ou** PostgreSQL instalado na porta 5432
- **Maven** (ou use o wrapper `mvnw` incluso no projeto)

---

## Como executar

### 1. Clone o repositório

```bash
git clone https://github.com/alvarofgomes/clinica-api.git
cd clinica-api
```

### 2. Suba o banco de dados

**Opção A — com Docker (recomendado):**

```bash
docker compose up -d
```

Isso sobe um PostgreSQL já configurado com o banco `clinica`, usuário e senha que a
aplicação espera por padrão. Nada mais precisa ser ajustado.

Para parar depois:

```bash
docker compose down
```

**Opção B — com PostgreSQL instalado localmente:**

Crie um banco chamado `clinica`:

```sql
CREATE DATABASE clinica;
```

Pelo terminal:

```bash
psql -U postgres -c "CREATE DATABASE clinica;"
```

> Em ambos os casos, as tabelas são criadas automaticamente pelo Hibernate e o banco é
> populado com dados de exemplo na primeira execução.

### 3. Configure as credenciais (se necessário)

A aplicação usa os seguintes valores por padrão:

| Variável      | Padrão      | Descrição            |
| ------------- | ----------- | -------------------- |
| `DB_HOST`     | `localhost` | Host do PostgreSQL   |
| `DB_PORT`     | `5432`      | Porta do PostgreSQL  |
| `DB_NAME`     | `clinica`   | Nome do banco        |
| `DB_USER`     | `postgres`  | Usuário              |
| `DB_PASSWORD` | `1234`      | Senha                |

Esses são exatamente os valores configurados no `docker-compose.yml`, então usando a
opção A nada precisa ser alterado. Se a sua configuração for diferente, defina as
variáveis de ambiente antes de executar. Exemplo no Linux/macOS:

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

O projeto inclui 13 testes automatizados, divididos em dois níveis:

**Testes unitários** (JUnit 5 + Mockito) — validam as regras de negócio de forma
isolada, sem banco de dados:

- Agendamento com data no passado é rejeitado
- Agendamento em horário já ocupado pelo profissional é rejeitado
- Agendamento com paciente ou profissional inexistente é rejeitado
- Agendamento válido é criado com status `AGENDADO`
- Cancelamento altera o status para `CANCELADO` e registra o motivo
- Cancelamento de agendamento já cancelado é rejeitado
- Cancelamento de agendamento inexistente é rejeitado

**Testes de integração** (MockMvc + H2 em memória) — exercitam o fluxo completo,
da requisição HTTP até o banco:

- Criação de agendamento retorna `201` com os dados corretos
- Horário já ocupado retorna `409`
- Data no passado retorna `400`
- Cancelamento retorna `200`, muda o status e mantém o registro consultável

```bash
./mvnw test
```

---

## Decisões técnicas

As principais decisões de modelagem e implementação, o que foi priorizado e o que ficou de
fora estão documentados em [DECISOES.md](DECISOES.md).