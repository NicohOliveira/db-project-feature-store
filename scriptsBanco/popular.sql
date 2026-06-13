-- popula usuario
INSERT INTO Usuario (username, senha) VALUES
('David_id','senha234'),
('Fabricio_id','senha123');

-- popula datasets
INSERT INTO Dataset (id_dataset, nome, username_criador) VALUES
(1,'Vendas_Norte','Fabricio_id'),
(2,'Sensores_IOT','David_id');

-- popula versao (ja com linhagem)
INSERT INTO Versao (id_dataset, num_versao, arquivo_csv, detalhes_feature, data_registro, hora_registro, descricao_modificacoes, username_autor, id_dataset_base, num_versao_base) VALUES
(1, 1, 'vendas_raw.csv', 'Colunas: id, valor, data', '2026-05-01', '10:00:00', 'Carga inicial do sistema', 'Fabricio_id', NULL, NULL),
(1, 2, 'vendas_limpo.csv', 'Colunas: id, valor, data, status', '2026-05-05', '14:20:00', 'Remoção de valores nulos', 'David_id', 1, 1),
(2, 1, 'iot_v1.csv', 'Temp, Humidade', '2026-05-02', '08:30:00', 'Dados brutos do sensor A1', 'David_id', NULL, NULL);

-- popula tabela versao fontes
INSERT INTO Versao_Fontes (id_dataset, num_versao, fonte) VALUES
(1, 1, 'API_Financeiro_Interno'),
(1, 1, 'Planilha_Custos_Final'),
(2, 1, 'Sensor_Hardware_v3');

-- popula registro acesso
INSERT INTO Registro_Acesso (id_acesso, data_acesso, hora_acesso, tipo_acao, username_leitor, id_dataset_acessada, num_versao_acessada) VALUES
(1, '2026-05-10', '09:00:00', 'LEITURA', 'David_id', 1, 1),
(2, '2026-05-11', '11:30:00', 'DOWNLOAD', 'David_id', 1, 2),
(3, '2026-05-11', '15:45:00', 'LEITURA', 'Fabricio_id', 2, 1);
