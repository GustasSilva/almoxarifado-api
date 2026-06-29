export type Perfil = "ADMIN" | "OPERADOR" | "VISUALIZADOR";
export type TipoMovimentacao = "ENTRADA" | "SAIDA";
export type StatusNotaFiscal = "EMITIDA" | "CANCELADA";
export type MotivoMovimentacao =
  | "COMPRA" | "DEVOLUCAO_FORNECEDOR" | "DEVOLUCAO_CLIENTE"
  | "TRANSFERENCIA" | "USO_INTERNO" | "AJUSTE_INVENTARIO"
  | "PERDA" | "VENDA" | "OUTRO";

export interface Usuario {
  id: number;
  nome: string;
  email: string;
  perfil: Perfil;
  ativo: boolean;
}

export interface Categoria {
  id: number;
  nome: string;
  descricao?: string;
}

export interface Fornecedor {
  id: number;
  razaoSocial: string;
  cnpj?: string;
  email?: string;
  telefone?: string;
}

export interface Produto {
  id: number;
  codigo: string;
  nome: string;
  descricao?: string;
  unidadeMedida: string;
  estoqueAtual: number;
  estoqueMinimo: number;
  precoCusto: number;
  ativo: boolean;
  categoria?: Categoria;
  fornecedor?: Fornecedor;
}

export interface Movimentacao {
  id: number;
  tipo: TipoMovimentacao;
  motivo: MotivoMovimentacao;
  quantidade: number;
  valorTotal: number;
  estoqueAntes: number;
  estoqueDepois: number;
  observacao?: string;
  produto: { id: number; nome: string; codigo: string };
  usuarioEmail: string;
  criadoEm: string;
}

export interface NotaFiscal {
  id: number;
  numero: string;
  serie: string;
  tipo: TipoMovimentacao;
  status: StatusNotaFiscal;
  emitenteRazaoSocial: string;
  emitenteCnpj: string;
  destinatarioNome: string;
  destinatarioCnpjCpf?: string;
  dataEmissao: string;
  valorTotal: number;
  observacao?: string;
  itens: NotaFiscalItem[];
  criadoEm: string;
}

export interface NotaFiscalItem {
  id: number;
  produtoId?: number;
  descricao: string;
  quantidade: number;
  unidadeMedida: string;
  precoUnitario: number;
  valorTotal: number;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface DashboardResponse {
  estoque: {
    totalProdutos: number;
    produtosAtivos: number;
    produtosAbaixoDoMinimo: number;
    valorTotalEmEstoque: number;
  };
  movimentacoes: {
    hoje: number;
    mes: number;
    entradasMes: { quantidade: number; valorTotal: number };
    saidasMes: { quantidade: number; valorTotal: number };
  };
  alertasEstoqueMinimo: {
    id: number;
    codigo: string;
    nome: string;
    estoqueAtual: number;
    estoqueMinimo: number;
  }[];
}
