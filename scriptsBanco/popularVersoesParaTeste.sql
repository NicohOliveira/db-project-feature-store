

INSERT INTO Usuario (username, senha)
VALUES ('kaster', 'senha123');

INSERT INTO Dataset (nome, username_criador)
VALUES ('Sensores_Agro', 'teste');

SELECT id_dataset, nome FROM Dataset WHERE nome = 'Sensores_Agro';
INSERT INTO Versao
(id_dataset, num_versao, arquivo_csv, detalhes_feature, nivel_maturidade, data_registro, hora_registro, descricao_modificacoes, username_autor, id_dataset_base, num_versao_base)
VALUES

(20, 1, '/home/nicooliveira/db-project-feature-store/backend/arquivos_csv/dataset_v1.csv', 'Colunas: Data, Temp, Umidade. Possui nulos.', 1, CURRENT_DATE, CURRENT_TIME, 'Dataset Original Bruto capturado', 'teste', NULL, NULL),

(20, 2, '/home/nicooliveira/db-project-feature-store/backend/arquivos_csv/dataset_v2.csv', 'Colunas: Data, Temp, Umidade. Sem nulos.', 2, CURRENT_DATE, CURRENT_TIME, 'Remoção de valores nulos na Temperatura', 'kaster', 20, 1),

(20, 3, '/home/nicooliveira/db-project-feature-store/backend/arquivos_csv/dataset_v3.csv', 'Colunas: Data, Temp, Umidade, Alerta.', 2, CURRENT_DATE, CURRENT_TIME, 'Adição de coluna de Alerta de Geada', 'teste', 20, 1),

(20, 4, '/home/nicooliveira/db-project-feature-store/backend/arquivos_csv/dataset_v4.csv', 'Colunas: Data, Temp, Umidade.', 3, CURRENT_DATE, CURRENT_TIME, 'Filtro aplicado: Apenas dias acima de 30 graus', 'kaster', 20, 2);

INSERT INTO Versao 
(id_dataset, num_versao, arquivo_csv, detalhes_feature, nivel_maturidade, data_registro, hora_registro, descricao_modificacoes, username_autor, id_dataset_base, num_versao_base)
VALUES
(20, 8, '/home/nicooliveira/db-project-feature-store/backend/arquivos_csv/dataset_v4.csv', 'Colunas: Data, Temp, Umidade.', 3, CURRENT_DATE, CURRENT_TIME, 'teste pra deletar', 'teste', 20, 8);
