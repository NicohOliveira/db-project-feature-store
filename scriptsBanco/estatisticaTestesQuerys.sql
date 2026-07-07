
-- 1.1 Total de Datasets e Versões no sistema
SELECT COUNT(*) as total_datasets FROM Dataset;
SELECT COUNT(*) as total_versoes FROM Versao;

-- 1.2 Proporção de Visualizações x Downloads GLOBAL
SELECT tipo_acao, COUNT(*) as quantidade 
FROM Registro_Acesso 
GROUP BY tipo_acao;

-- 1.3 Ranking Global de Contribuidores (quem criou mais versões no sistema todo)
SELECT username_autor, COUNT(*) as criacoes 
FROM Versao 
GROUP BY username_autor 
ORDER BY criacoes DESC 
LIMIT 5;

-- 1.4 Ranking de Datasets Mais Populares (O termômetro do sistema)
SELECT d.nome, COUNT(r.id_acesso) as total_interacoes
FROM Dataset d
LEFT JOIN Registro_Acesso r ON d.id_dataset = r.id_dataset_acessada
GROUP BY d.id_dataset, d.nome
ORDER BY total_interacoes DESC 
LIMIT 5;

-- 2. consultas para dataset específico

-- 2.1 Total de Versões apenas do Dataset 20
SELECT COUNT(*) as total_versoes 
FROM Versao 
WHERE id_dataset = 20;

-- 2.2 Proporção de Visualizações x Downloads apenas do Dataset 20
SELECT tipo_acao, COUNT(*) as quantidade 
FROM Registro_Acesso 
WHERE id_dataset_acessada = 20 
GROUP BY tipo_acao;

-- 2.3 Ranking de Contribuidores específicos do Dataset 20
SELECT username_autor, COUNT(*) as criacoes 
FROM Versao 
WHERE id_dataset = 20 
GROUP BY username_autor 
ORDER BY criacoes DESC 
LIMIT 5;

-- 2.4 Versões Mais Populares dentro do Dataset 20 (Qual ramificação a galera mais baixou/viu?)
SELECT num_versao_acessada, COUNT(*) as interacoes 
FROM Registro_Acesso 
WHERE id_dataset_acessada = 20 
GROUP BY num_versao_acessada 
ORDER BY interacoes DESC 
LIMIT 5;

--3 testes de maturidade

--3.1 maturidade de dataset

SELECT nivel_maturidade, COUNT(*) as qtd 
FROM Versao 
WHERE id_dataset = 20
GROUP BY nivel_maturidade;

--3.2 maturidade global

SELECT nivel_maturidade, COUNT(*) as qtd 
FROM Versao 
GROUP BY nivel_maturidade;

--3.3 datasets com mais versão nivel 3/ouro

SELECT d.nome, COUNT(v.num_versao) as qtd_ouro 
FROM Dataset d 
INNER JOIN Versao v ON d.id_dataset = v.id_dataset 
WHERE v.nivel_maturidade = 3 
GROUP BY d.nome 
ORDER BY qtd_ouro DESC 
LIMIT 5;

