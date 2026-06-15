
DROP TABLE IF EXISTS Registro_Acesso CASCADE;
DROP TABLE IF EXISTS Versao_Fontes CASCADE;
DROP TABLE IF EXISTS Versao CASCADE;
DROP TABLE IF EXISTS Dataset CASCADE;
DROP TABLE IF EXISTS Usuario CASCADE;

CREATE TABLE Usuario (
    username VARCHAR(20),
    senha VARCHAR(255) NOT NULL,
    PRIMARY KEY (username)
);

CREATE TABLE Dataset (
    id_dataset SERIAL,
    nome VARCHAR(20) NOT NULL,
    username_criador VARCHAR(20),

    PRIMARY KEY (id_dataset),
    FOREIGN KEY (username_criador)
        REFERENCES Usuario(username)
        ON DELETE SET NULL
);

CREATE TABLE Versao (
    id_dataset INT,
    num_versao INT,
    arquivo_csv VARCHAR(100) NOT NULL,
    detalhes_feature TEXT,
    nivel_maturidade INT NOT NULL DEFAULT 1,
    data_registro DATE NOT NULL,
    hora_registro TIME NOT NULL,
    descricao_modificacoes TEXT,
    username_autor VARCHAR(20),
    id_dataset_base INT,
    num_versao_base INT,

    PRIMARY KEY (id_dataset, num_versao),
    FOREIGN KEY (id_dataset)
        REFERENCES Dataset(id_dataset)
        ON DELETE CASCADE,
    FOREIGN KEY (username_autor)
        REFERENCES Usuario(username)
        ON DELETE SET NULL,
    FOREIGN KEY (id_dataset_base, num_versao_base)
        REFERENCES Versao(id_dataset, num_versao)
        ON DELETE SET NULL
);

CREATE TABLE Versao_Fontes (
    id_dataset INT,
    num_versao INT,
    fonte VARCHAR(100),

    PRIMARY KEY (id_dataset, num_versao, fonte),
    FOREIGN KEY (id_dataset, num_versao)
        REFERENCES Versao(id_dataset, num_versao)
        ON DELETE CASCADE
);

CREATE TABLE Registro_Acesso (
    id_acesso SERIAL,
    data_acesso DATE,
    hora_acesso TIME,
    tipo_acao VARCHAR(50),
    username_leitor VARCHAR(20),
    id_dataset_acessada INT,
    num_versao_acessada INT,

    PRIMARY KEY(id_acesso),
    FOREIGN KEY (username_leitor)
        REFERENCES Usuario(username)
        ON DELETE SET NULL,
    FOREIGN KEY (id_dataset_acessada, num_versao_acessada)
        REFERENCES Versao(id_dataset, num_versao)
        ON DELETE CASCADE
);

ALTER TABLE Dataset ADD CONSTRAINT dataset_nome_unique UNIQUE (nome);
