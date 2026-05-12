CREATE TABLE Usuario (
    username VARCHAR(20),
    senha VARCHAR(255) NOT NULL,
    
    PRIMARY KEY (username)
);

CREATE TABLE Dataset (
    id_dataset INT,
    nome VARCHAR(20) NOT NULL,
    username_criador VARCHAR(20) NOT NULL,
    
    PRIMARY KEY (id_dataset),
    FOREIGN KEY (username_criador)
		REFERENCES Usuario(username)
        ON DELETE CASCADE
);

CREATE TABLE Versao (
    id_dataset INT,
    num_versao INT,
    arquivo_csv VARCHAR(100) NOT NULL,
    detalhes_feature VARCHAR(100),
    data_registro DATE NOT NULL,
    hora_registro TIME NOT NULL,
    descricao_modificacoes TEXT,
    username_autor VARCHAR(20) NOT NULL,
    id_dataset_base INT,
    num_versao_base INT,
    
    PRIMARY KEY (id_dataset, num_versao),
    FOREIGN KEY (id_dataset)
		REFERENCES Dataset(id_dataset)
        ON DELETE CASCADE,
    FOREIGN KEY (username_autor)
		REFERENCES Usuario(username),
    FOREIGN KEY (id_dataset_base, num_versao_base)
		REFERENCES Versao(id_dataset, num_versao)
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
		REFERENCES Usuario(username),
    FOREIGN KEY (id_dataset_acessada, num_versao_acessada)
		REFERENCES Versao(id_dataset, num_versao)
);