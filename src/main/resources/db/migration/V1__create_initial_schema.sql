-- Categorias
CREATE TABLE categorias (
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(100) NOT NULL UNIQUE,
    descricao     VARCHAR(255),
    ativo         BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Fornecedores
CREATE TABLE fornecedores (
    id            BIGSERIAL PRIMARY KEY,
    razao_social  VARCHAR(150) NOT NULL,
    cnpj          VARCHAR(18) UNIQUE,
    contato       VARCHAR(100),
    telefone      VARCHAR(20),
    email         VARCHAR(150),
    ativo         BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Produtos
CREATE TABLE produtos (
    id              BIGSERIAL PRIMARY KEY,
    codigo          VARCHAR(50) NOT NULL UNIQUE,
    nome            VARCHAR(150) NOT NULL,
    descricao       VARCHAR(500),
    categoria_id    BIGINT REFERENCES categorias(id),
    unidade_medida  VARCHAR(10) NOT NULL,
    estoque_atual   NUMERIC(10, 3) NOT NULL DEFAULT 0,
    estoque_minimo  NUMERIC(10, 3) NOT NULL DEFAULT 0,
    preco_custo     NUMERIC(15, 2),
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em       TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Usuários
CREATE TABLE usuarios (
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    senha         VARCHAR(255) NOT NULL,
    perfil        VARCHAR(20) NOT NULL DEFAULT 'OPERADOR',
    ativo         BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Movimentações
CREATE TABLE movimentacoes (
    id               BIGSERIAL PRIMARY KEY,
    tipo             VARCHAR(10) NOT NULL,
    motivo           VARCHAR(30) NOT NULL,
    produto_id       BIGINT NOT NULL REFERENCES produtos(id),
    quantidade       NUMERIC(10, 3) NOT NULL,
    preco_unitario   NUMERIC(15, 2),
    valor_total      NUMERIC(15, 2),
    fornecedor_id    BIGINT REFERENCES fornecedores(id),
    usuario_id       BIGINT NOT NULL REFERENCES usuarios(id),
    observacao       VARCHAR(500),
    numero_documento VARCHAR(50),
    criado_em        TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Índices
CREATE INDEX idx_produtos_categoria ON produtos(categoria_id);
CREATE INDEX idx_produtos_ativo ON produtos(ativo);
CREATE INDEX idx_movimentacoes_produto ON movimentacoes(produto_id);
CREATE INDEX idx_movimentacoes_tipo ON movimentacoes(tipo);
CREATE INDEX idx_movimentacoes_criado_em ON movimentacoes(criado_em);
