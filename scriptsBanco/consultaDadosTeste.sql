SELECT
    d.nome AS "Dataset",
    v.num_versao AS "Versão",
    u.username AS "Autor da Versão",
    v.arquivo_csv AS "Arquivo"
FROM Dataset d
JOIN Versao v ON d.id_dataset = v.id_dataset
JOIN Usuario u ON v.username_autor = u.username;
