# Decisões Técnicas

Documento com as principais decisões tomadas durante o desenvolvimento da API de
agendamentos, o que foi priorizado, o que ficou de fora e como a IA foi utilizada.

---

## 1. Principais decisões técnicas

### Modelagem

**Três entidades: `Paciente`, `Profissional` e `Agendamento`.**
O enunciado pedia endpoints apenas para paciente e agendamento, mas as regras de
negócio exigem um profissional (um profissional não pode ter dois agendamentos no
mesmo horário). Optei por modelar `Profissional` como entidade própria, com endpoints
de cadastro e listagem, em vez de tratá-lo como um campo texto no agendamento. Isso
mantém a modelagem coerente e permite validar a existência do profissional ao agendar.

**Relacionamentos `@ManyToOne`.**
Um agendamento pertence a um paciente e a um profissional, mas um mesmo paciente ou
profissional pode ter vários agendamentos. Ambos os relacionamentos são obrigatórios
(`optional = false`).

**Enums para `TipoAtendimento` e `StatusAgendamento`.**
Garantem que apenas valores válidos cheguem ao banco. Gravados como texto
(`@Enumerated(EnumType.STRING)`) e não como ordinal, para que a inclusão futura de
novos valores não altere o significado dos registros existentes.

**Status do agendamento: `AGENDADO` e `CANCELADO`.**
Mantive apenas os dois estados necessários ao escopo pedido. A estrutura permite
evoluir facilmente para outros estados (ex: `EM_ATENDIMENTO`, `ATENDIDO`, `FALTOU`)
adicionando valores ao enum e os endpoints de transição correspondentes.

### Cancelamento (exclusão lógica)

O cancelamento é feito via `PATCH /agendamentos/{id}/cancelar`, recebendo o motivo no
corpo da requisição. Optei por `PATCH` em vez de `DELETE` porque a operação **não é
uma exclusão**: o registro é mantido, apenas muda de estado. O status passa a
`CANCELADO` e o motivo é persistido em `motivoCancelamento`.

Um horário liberado por cancelamento volta a ficar disponível: a verificação de
conflito desconsidera agendamentos cancelados.

### Ausência de exclusão de pacientes e profissionais

Não implementei exclusão física de pacientes e profissionais. Além de não estar no
escopo pedido, a remoção comprometeria a integridade do histórico de agendamentos
(registros órfãos) — algo crítico em contexto clínico. Caso fosse necessário,
implementaria exclusão lógica (campo `ativo`), mantendo o histórico, no mesmo padrão
adotado para o cancelamento.

### Banco de dados

**PostgreSQL como banco principal, H2 em memória nos testes de integração.**
O PostgreSQL garante persistência real dos dados; o H2, ativado pelo profile `test`,
permite que os testes de integração rodem de forma rápida e isolada, sem depender de
infraestrutura externa.

**Dados iniciais via `data.sql`**, para que a API já suba com registros disponíveis —
incluindo um agendamento cancelado, que demonstra a preservação do histórico. Os
comandos usam `ON CONFLICT DO NOTHING` e ajustam as sequências de id, de modo que a
carga é idempotente e a aplicação pode ser reiniciada sem erro.

**Credenciais por variáveis de ambiente**, com valores padrão para execução local
(`${DB_PASSWORD:1234}`). Isso permite que o avaliador execute o projeto sem
configuração adicional, mas mantém a configuração externalizável. Em um projeto com
credenciais sensíveis, usaria um `application.properties.example` versionado com o
arquivo real no `.gitignore`.

**Compatibilidade com Oracle.** Toda a persistência é feita via JPA/Hibernate, sem SQL
nativo. Isso mantém o código portável: para rodar em Oracle bastaria trocar o driver e
o dialect, sem alterar a camada de domínio.

### Organização do código

Estrutura em camadas, separada por responsabilidade:

```
controller/   recebe as requisições e devolve as respostas HTTP
service/      regras de negócio e validações
repository/   acesso ao banco de dados
model/        entidades JPA
enums/        TipoAtendimento e StatusAgendamento
dto/          objetos de entrada e saída da API
exception/    exceções de domínio e tratamento centralizado de erros
```

**Todas as regras de negócio vivem na camada de service**, nunca nos controllers.

**DTOs separados para entrada e saída.** Os de entrada não expõem o `id` (gerado pelo
servidor) e carregam as validações; os de saída controlam exatamente o que a API
devolve — o agendamento retorna o nome do paciente e do profissional, e não as
entidades completas.

**Tratamento de erros centralizado** em um `@RestControllerAdvice`, que traduz as
exceções de domínio em respostas HTTP apropriadas (404, 409, 400) com corpo JSON, em
vez de expor stack traces.

**Exceções específicas por tipo de falha** (`RecursoNaoEncontradoException`,
`ConflitoHorarioException`, `RegraNegocioException`), cada uma mapeada para um status
HTTP distinto.

**Lombok com `@Getter`/`@Setter` nas entidades**, em vez de `@Data`. O `@Data` gera
`equals()` e `hashCode()` usando todos os campos, o que pode causar comportamento
inesperado em entidades JPA.

### Testes

Optei por dois níveis de teste. Os **unitários** (Mockito) verificam as regras de
negócio isoladamente, sem subir contexto nem banco — são rápidos e falham apontando
exatamente a regra quebrada. Os **de integração** (MockMvc + H2) exercitam o fluxo
completo, da requisição HTTP até a persistência, validando também os status de
resposta e o formato do JSON. Os dois se complementam: o unitário garante a lógica, o
de integração garante que as camadas estão corretamente conectadas.

### Nomenclatura

Domínio em português (entidades, campos, métodos, endpoints) e palavras-chave de
framework em inglês (anotações e query methods do Spring Data, que são interpretados
pelo próprio framework).

---

## 2. O que priorizei e o que ficou de fora

### Priorizei

- As regras de negócio obrigatórias e sua cobertura por testes
- Separação clara de responsabilidades entre as camadas
- Tratamento de erros consistente, com status HTTP adequados
- Documentação dos endpoints

### Ficou de fora

**Interface web (Angular/Vue).** Optei por aprofundar o back-end, os testes e o
tratamento de erros em vez de construir uma interface. A API é documentada via Swagger
e pode ser consumida diretamente.

**Autenticação e autorização.** Avaliei incluir Spring Security com JWT, considerando
que dados de pacientes são sensíveis. Optei por não implementar nesta entrega para
priorizar as regras de negócio pedidas e manter a API facilmente testável pelo
avaliador. Em um cenário real, implementaria JWT com perfis distintos (ex: ADMIN para
cadastros, ATENDENTE para agendamentos), protegendo especialmente os endpoints que
expõem dados de pacientes.

**Instalação do Oracle.** Em vez disso, demonstrei compatibilidade mantendo a
persistência agnóstica ao banco (conforme descrito acima).

**Containerização com Docker.** Optei por instruções diretas de configuração no README,
priorizando a conclusão dos requisitos. Em um projeto real, incluiria um
`docker-compose.yml` para padronizar o ambiente de execução.

---

## 3. Uso de IA

Utilizei IA como apoio durante todo o desenvolvimento, principalmente para entender
conceitos que eu ainda não dominava — como relacionamentos JPA (`@ManyToOne`) e testes
com mocks — e para revisar decisões de modelagem e organização do código.

Toda a implementação foi acompanhada e compreendida por mim. Em vários pontos
questionei e alterei sugestões: troquei `@Data` por `@Getter`/`@Setter` nas entidades
após entender o impacto em `equals()`/`hashCode()`, reorganizei os enums em pacote
próprio, e optei por PostgreSQL em vez de H2 como banco principal por considerar que
persistência real atendia melhor ao requisito.

A validação foi feita em duas frentes: testei manualmente todos os endpoints no
Postman, incluindo os casos de erro (conflito de horário, data no passado, CPF
duplicado, validações de campo e cancelamento duplicado), conferindo status HTTP e
corpo das respostas; e escrevi testes automatizados unitários e de integração
cobrindo as regras de negócio. Durante os testes manuais identifiquei que a busca por
id de agendamento não existia e retornava um erro fora do padrão da API, e implementei
o endpoint para manter a consistência das respostas.