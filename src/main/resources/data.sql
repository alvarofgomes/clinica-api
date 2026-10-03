-- Profissionais
INSERT INTO profissional (nome, especialidade) VALUES ('Dra. Ana Lima', 'Cardiologia');
INSERT INTO profissional (nome, especialidade) VALUES ('Dr. Carlos Souza', 'Ortopedia');
INSERT INTO profissional (nome, especialidade) VALUES ('Dra. Marina Costa', 'Dermatologia');

-- Pacientes
INSERT INTO paciente (nome, cpf, email, telefone) VALUES ('Joao Pereira', '11122233344', 'joao@exemplo.com', '81988880000');
INSERT INTO paciente (nome, cpf, email, telefone) VALUES ('Maria Santos', '55566677788', 'maria@exemplo.com', '81977770000');
INSERT INTO paciente (nome, cpf, email, telefone) VALUES ('Pedro Alves', '99900011122', 'pedro@exemplo.com', '81966660000');

-- Agendamentos
INSERT INTO agendamento (paciente_id, profissional_id, data_hora, tipo_atendimento, status)
VALUES (1, 1, '2026-12-10 09:00:00', 'CONSULTA', 'AGENDADO');

INSERT INTO agendamento (paciente_id, profissional_id, data_hora, tipo_atendimento, status)
VALUES (2, 1, '2026-12-10 10:00:00', 'RETORNO', 'AGENDADO');

INSERT INTO agendamento (paciente_id, profissional_id, data_hora, tipo_atendimento, status)
VALUES (3, 2, '2026-12-11 14:00:00', 'EXAME', 'AGENDADO');

-- Um cancelado, para demonstrar o histórico preservado
INSERT INTO agendamento (paciente_id, profissional_id, data_hora, tipo_atendimento, status, motivo_cancelamento)
VALUES (1, 3, '2026-12-12 15:00:00', 'CONSULTA', 'CANCELADO', 'Paciente remarcou para outra data');