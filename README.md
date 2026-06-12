# FeatureTrack: Middleware de Feature Store

**Disciplina:** Banco de Dados - UEL (Universidade Estadual de Londrina)  
**Professor:** Daniel Kaster  
**Desenvolvedores:** Nicolas Henrique de Lima Oliveira e Caio Sweiver de Carvalho  

---

## Resumo do Projeto
O **FeatureTrack** é um sistema middleware construído sobre um SGBD relacional, projetado para atuar como uma *Feature Store* simplificada. O sistema permite o armazenamento, versionamento e rastreabilidade de datasets (arquivos CSV) voltados para tarefas de Machine Learning.

O foco principal do projeto é a implementação de boas práticas de integração de banco de dados em aplicações multicamadas, explorando consultas SQL avançadas, normalização de dados e arquitetura de rastreabilidade (linhagem).

## Tecnologias
* **Back-end:** Java/J2EE com persistência via camada DAO utilizando JDBC puro (sem frameworks ORM como JPA/Hibernate, para controle explícito do SQL).
* **Front-end:** React.js / JavaScript.
* **Banco de Dados:** PostgreSQL.

---

## Referências Visuais de Modelagem

Abaixo estão as representações gráficas da arquitetura do banco de dados desenvolvida para o escopo do projeto.

### 1. Diagrama Entidade-Relacionamento (Modelo Conceitual)
Representação das regras de negócio puras, focando nas entidades, dependências e cardinalidades antes da conversão para tabelas.

![Diagrama ER Inicial](./docs/Diagrama_ER_ProjetoFeatureStore.png)

### 2. Mapeamento de Tabelas (Modelo Lógico)
Conversão do modelo conceitual para relações, destacando as Chaves Primárias (PK), Chaves Estrangeiras (FK), resolução de atributos multivalorados e reificação.

![Mapeamento Tabelas](./docs/Mapeamento_de_Tabelas_Digital.png)

---

##  Dicionário de Dados e Justificativas Técnicas

A arquitetura foi desenhada seguindo rigorosamente a teoria do Modelo Relacional. Abaixo está a estrutura de cada tabela e as justificativas para as decisões de modelagem:

* **`Usuario`**
  Tabela fundamental para a autenticação e auditoria do sistema. Atua como o ponto de partida para a rastreabilidade, garantindo que toda ação (criação, upload ou download) seja vinculada a um autor através de chaves estrangeiras.

* **`Dataset` (Entidade Forte)**
  O repositório lógico. Foi classificado como entidade forte por possuir **independência existencial e identificadora**. Seu atributo `id_dataset` é exclusivo e suficiente para localizá-lo no banco. Armazena os metadados fixos do conjunto de dados e o usuário criador.

* **`Versao` (Entidade Fraca e Auto-relacionamento)**
  O núcleo do versionamento. Sofre de **dependência existencial**, pois não faz sentido existir uma "Versão 1.0" solta no banco sem saber a qual dataset ela pertence. Por isso, sua PK é obrigatoriamente composta: `(id_dataset, num_versao)`.
  * **A Rastreabilidade (Linked List):** Para rastrear a linhagem de qual arquivo gerou qual, utilizamos um auto-relacionamento (`id_dataset_base`, `num_versao_base`). Isso cria uma estrutura de Lista Encadeada no banco, onde cada versão só precisa apontar para seu "pai" imediato. A primeira versão de qualquer dataset recebe `NULL` nestes campos.

* **`Versao_Fontes`**
  Tabela gerada puramente por normalização estrutural para lidar com o atributo **Multivalorado** de "fontes". 
  
* **`Registro_Acesso` (A Reificação)**
  Entidade criada através do problema identificado do relacionamento N:M entre Usuário e Versão. 
  * **O Problema:** Um relacionamento N:M tradicional bloquearia (por restrição de PK) que o mesmo usuário baixasse a mesma versão em dias diferentes.
  * **A Solução:** Elevamos a ação de "acessar" a uma Entidade Forte temporal, com um `id_acesso` auto-incrementado. O Log atua como o lado "N" de dois relacionamentos 1:N (um usuário possui N logs; uma versão recebe N logs). Cada registro é um evento atômico (único) contendo carimbos de tempo (data/hora), viabilizando os cálculos estatísticos.

---

## Relatórios e Analytics (Consultas)

Para cumprir o requisito de consultas SQL avançadas, o painel administrativo consome os dados gerados pela tabela de acessos e pelo histórico de versões utilizando o motor do PostgreSQL, onde geraremos os seguintes gráficos até o momento decididos, podendo haver adições futuras:

1. **Ranking de Popularidade (Top 5 Datasets mais estourados):**
   * **Objetivo:** Identificar os conjuntos de dados com maior tração na plataforma.
  
2. **Evolução Temporal de Engajamento:**
   * **Objetivo:** Visualizar picos de uso da plataforma ao longo do tempo.

3. **Proporção de Tipos de Ação (Download vs. Visualização):**
   * **Objetivo:** Entender a taxa de conversão (quantos usuários olham os detalhes vs. quantos efetivamente baixam a feature).

4. **Tabela de Linhagem (Histórico de Versões):**
   * **Objetivo:** Exibir a árvore genealógica de um arquivo (ex: v3.0 veio da v2.0 que veio da v1.0).

## Especificação de Regras de Negócio (Restrições do Sistema)

* **Indexação de Maturidade por Auto-Declaração:** O middleware delega o polimento dos dados do CSV para ferramentas externas de engenharia, mas atua como um indexador rigoroso de qualidade através de níveis auto-declarados pelo usuário no formulário de upload:
    * **BRONZE (Bruto):** Carga inicial. Aceita dados em estado bruto (com nulos ou inconsistências), focando no registro original da captura.
    * **PRATA (Tratado):** Versões que passaram por limpeza, tratamento de tipos ou imputação de valores ausentes.
    * **OURO (Otimizado):** Versões consolidadas, com features validadas e prontas para consumo direto por pipelines de Machine Learning.
* **Mecanismo de Linhagem Transparente (UI Context):** O preenchimento das colunas `id_dataset_base` e `num_versao_base` é obrigatório para manter a integridade referencial do auto-relacionamento. Contudo, essa amarração é invisível para o usuário: o sistema captura o ID da versão que está sendo visualizada na tela no momento do clique em "Nova Versão" e injeta a herança automaticamente no backend.
* **Colaboração Aberta e Governança por Rastreabilidade:** O sistema adota um modelo descentralizado de contribuição (similar ao ecossistema Git). Usuários possuem permissão para submeter novas versões derivadas (v2, v3) em repositórios lógicos criados por terceiros. A governança da plataforma não se baseia no bloqueio de escrita, mas na auditoria estrita: o banco vincula o `username_autor` a cada modificação, permitindo a responsabilização técnica por eventuais inconsistências na linhagem.
## Fluxo de Funcionalidades (Ciclo de Vida do Dado)
1. **Autenticação e Auto-Cadastro:** O usuário acessa a plataforma e, caso não possua credenciais, pode realizar o auto-cadastro instantâneo para obter acesso ao Dashboard analítico.
2. **Ingestão Base (Nível Bronze):** Na página de criação, o usuário realiza o upload do primeiro arquivo CSV. O sistema captura o contexto logado, infere a versão como `1` e exige a marcação de maturidade inicial e o registro de ao menos uma fonte de origem.
3. **Navegação e Descoberta:** Usuários navegam pelo catálogo e acessam os detalhes de qualquer dataset disponível na rede interna. O frontend carrega a árvore de versões de forma dinâmica.
4. **Consumo Auditado:** Ao disparar o download de uma versão específica, a camada de persistência intercepta a requisição e grava um log atômico contendo o carimbo de tempo e o autor do consumo, alimentando os relatórios.
5. **Evolução Colaborativa (Linhagem Automática):** A partir da tela de detalhes de uma versão existente, qualquer usuário pode clicar em "Submeter Nova Versão". O frontend infere automaticamente qual é a versão-pai baseando-se no nó atual de navegação, eliminando a necessidade de inserção manual de IDs pelo desenvolvedor.
## Decisões de Design e Simplificação de Escopo

* **Ausência de RBAC (Controle de Acesso Baseado em Papéis):** Para manter o desenvolvimento focado nas restrições relacionais e viável dentro do cronograma da disciplina, optou-se por não implementar hierarquias complexas de usuários (como perfis de 'Líder de Grupo' ou 'Administrador Geral'). A segurança da plataforma apoia-se no pilar da **rastreabilidade total**, onde cada ação gera um registro imutável associado a uma chave estrangeira de usuário.
* **Módulo de Cadastro Aberto para Fins Avaliativos:** Em um ambiente de produção real, a tabela `Usuario` seria integrada de forma fechada a um serviço de federação de identidades corporativo (SSO/LDAP). Para a validação acadêmica e facilidade de testes por parte da banca avaliadora, a interface mantém a funcionalidade de auto-cadastro aberta, permitindo a criação dinâmica de perfis de teste diretamente pela aplicação web.
---
*Este projeto é parte das entregas avaliativas do 1º e 2º bimestres da disciplina.*
