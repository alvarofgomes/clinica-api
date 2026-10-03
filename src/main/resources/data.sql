-- Profissionais
INSERT INTO profissional (id, nome, especialidade) VALUES (1, 'Dra. Ana Lima', 'Cardiologia') ON CONFLICT (id) DO NOTHING;
INSERT INTO profissional (id, nome, especialidade) VALUES (2, 'Dr. Carlos Souza', 'Ortopedia') ON CONFLICT (id) DO NOTHING;
INSERT INTO profissional (id, nome, especialidade) VALUES (3, 'Dra. Marina Costa', 'Dermatologia') ON CONFLICT (id) DO NOTHING;

-- Pacientes
INSERT INTO paciente (id, nome, cpf, email, telefone) VALUES (1, 'Joao Pereira', '11122233344', 'joao@exemplo.com', '81988880000') ON CONFLICT (id) DO NOTHING;
INSERT INTO paciente (id, nome, cpf, email, telefone) VALUES (2, 'Maria Santos', '55566677788', 'maria@exemplo.com', '81977770000') ON CONFLICT (id) DO NOTHING;
INSERT INTO paciente (id, nome, cpf, email, telefone) VALUES (3, 'Pedro Alves', '99900011122', 'pedro@exemplo.com', '81966660000') ON CONFLICT (id) DO NOTHING;

-- Agendamentos
INSERT INTO agendamento (id, paciente_id, profissional_id, data_hora, tipo_atendimento, status)
VALUES (1, 1, 1, '2026-12-10 09:00:00', 'CONSULTA', 'AGENDADO') ON CONFLICT (id) DO NOTHING;

INSERT INTO agendamento (id, paciente_id, profissional_id, data_hora, tipo_atendimento, status)
VALUES (2, 2, 1, '2026-12-10 10:00:00', 'RETORNO', 'AGENDADO') ON CONFLICT (id) DO NOTHING;

INSERT INTO agendamento (id, paciente_id, profissional_id, data_hora, tipo_atendimento, status)
VALUES (3, 3, 2, '2026-12-11 14:00:00', 'EXAME', 'AGENDADO') ON CONFLICT (id) DO NOTHING;

-- Um cancelado, para demonstrar o histórico preservado
INSERT INTO agendamento (id, paciente_id, profissional_id, data_hora, tipo_atendimento, status, motivo_cancelamento)
VALUES (4, 1, 3, '2026-12-12 15:00:00', 'CONSULTA', 'CANCELADO', 'Paciente remarcou para outra data') ON CONFLICT (id) DO NOTHING;

SELECT setval('profissional_id_seq', (SELECT MAX(id) FROM profissional));
SELECT setval('paciente_id_seq', (SELECT MAX(id) FROM paciente));
SELECT setval('agendamento_id_seq', (SELECT MAX(id) FROM agendamento));