CREATE TABLE notas_fiscais (
    id                        BIGSERIAL PRIMARY KEY,
    numero                    VARCHAR(9)     NOT NULL,
    serie                     VARCHAR(3)     NOT NULL DEFAULT '001',
    tipo                      VARCHAR(10)    NOT NULL,
    status                    VARCHAR(20)    NOT NULL DEFAULT 'EMITIDA',

    emitente_razao_social     VARCHAR(150)   NOT NULL,
    emitente_cnpj             VARCHAR(20)    NOT NULL,
    emitente_endereco         VARCHAR(255)   NOT NULL,

    destinatario_nome         VARCHAR(150)   NOT NULL,
    destinatario_cnpj_cpf     VARCHAR(20),
    destinatario_endereco     VARCHAR(255),

    data_emissao              TIMESTAMP      NOT NULL,
    valor_total               NUMERIC(14, 2) NOT NULL,
    observacao                TEXT,
    pdf_path                  VARCHAR(500),

    criado_em                 TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE TABLE notas_fiscais_itens (
    id                BIGSERIAL       PRIMARY KEY,
    nota_fiscal_id    BIGINT          NOT NULL REFERENCES notas_fiscais(id),
    produto_id        BIGINT          REFERENCES produtos(id),
    descricao         VARCHAR(255)    NOT NULL,
    quantidade        NUMERIC(10, 3)  NOT NULL,
    unidade_medida    VARCHAR(10)     NOT NULL,
    preco_unitario    NUMERIC(14, 4)  NOT NULL,
    valor_total       NUMERIC(14, 2)  NOT NULL
);

CREATE SEQUENCE nota_fiscal_numero_seq START 1;

CREATE INDEX idx_notas_fiscais_numero ON notas_fiscais(numero);
CREATE INDEX idx_notas_fiscais_data_emissao ON notas_fiscais(data_emissao);
CREATE INDEX idx_notas_fiscais_itens_nf ON notas_fiscais_itens(nota_fiscal_id);
