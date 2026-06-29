"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast } from "sonner";
import api from "@/lib/api";
import { Movimentacao, Produto, Page } from "@/types";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Plus, TrendingUp, TrendingDown } from "lucide-react";

const schema = z.object({
  produtoId: z.coerce.number().min(1, "Selecione um produto"),
  tipo: z.enum(["ENTRADA", "SAIDA"]),
  motivo: z.string().min(1, "Selecione o motivo"),
  quantidade: z.coerce.number().positive("Quantidade deve ser maior que zero"),
  precoUnitario: z.coerce.number().min(0),
  observacao: z.string().optional(),
});
type FormData = z.infer<typeof schema>;

const MOTIVOS = [
  "COMPRA", "DEVOLUCAO_FORNECEDOR", "DEVOLUCAO_CLIENTE",
  "TRANSFERENCIA", "USO_INTERNO", "AJUSTE_INVENTARIO", "PERDA", "VENDA", "OUTRO",
];

const MOTIVO_LABEL: Record<string, string> = {
  COMPRA: "Compra", DEVOLUCAO_FORNECEDOR: "Dev. Fornecedor",
  DEVOLUCAO_CLIENTE: "Dev. Cliente", TRANSFERENCIA: "Transferência",
  USO_INTERNO: "Uso Interno", AJUSTE_INVENTARIO: "Ajuste Inventário",
  PERDA: "Perda", VENDA: "Venda", OUTRO: "Outro",
};

export default function MovimentacoesPage() {
  const qc = useQueryClient();
  const [open, setOpen] = useState(false);
  const [page, setPage] = useState(0);

  const { data, isLoading } = useQuery<Page<Movimentacao>>({
    queryKey: ["movimentacoes", page],
    queryFn: () => api.get(`/api/movimentacoes?page=${page}&size=15`).then((r) => r.data),
  });

  const { data: produtos } = useQuery<Page<Produto>>({
    queryKey: ["produtos-select"],
    queryFn: () => api.get("/api/produtos?size=200&sort=nome").then((r) => r.data),
  });

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormData>({
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    resolver: zodResolver(schema) as any,
    defaultValues: { tipo: "ENTRADA", motivo: "COMPRA", precoUnitario: 0 },
  });

  const registrar = useMutation({
    mutationFn: (d: FormData) => api.post("/api/movimentacoes", d),
    onSuccess: () => {
      toast.success("Movimentação registrada!");
      qc.invalidateQueries({ queryKey: ["movimentacoes"] });
      qc.invalidateQueries({ queryKey: ["dashboard"] });
      qc.invalidateQueries({ queryKey: ["produtos"] });
      setOpen(false);
    },
    onError: (e: any) => toast.error(e.response?.data?.detail ?? "Erro ao registrar movimentação"),
  });

  function openNew() {
    reset({ tipo: "ENTRADA", motivo: "COMPRA", quantidade: 1, precoUnitario: 0 });
    setOpen(true);
  }

  function formatDate(str: string) {
    return new Date(str).toLocaleString("pt-BR", { dateStyle: "short", timeStyle: "short" });
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Movimentações</h1>
          <p className="text-sm text-slate-500">Entradas e saídas de estoque</p>
        </div>
        <Button onClick={openNew} className="bg-blue-700 hover:bg-blue-800">
          <Plus size={16} className="mr-2" /> Nova movimentação
        </Button>
      </div>

      <div className="rounded-lg border bg-white shadow-sm">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Tipo</TableHead>
              <TableHead>Produto</TableHead>
              <TableHead>Motivo</TableHead>
              <TableHead className="text-right">Qtd</TableHead>
              <TableHead className="text-right">Valor</TableHead>
              <TableHead className="text-right">Estoque após</TableHead>
              <TableHead>Usuário</TableHead>
              <TableHead>Data</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading && (
              <TableRow><TableCell colSpan={8} className="text-center py-8 text-slate-400">Carregando...</TableCell></TableRow>
            )}
            {data?.content.map((m) => (
              <TableRow key={m.id}>
                <TableCell>
                  <Badge
                    variant={m.tipo === "ENTRADA" ? "default" : "secondary"}
                    className={m.tipo === "ENTRADA" ? "bg-emerald-100 text-emerald-700 hover:bg-emerald-100" : "bg-amber-100 text-amber-700 hover:bg-amber-100"}
                  >
                    {m.tipo === "ENTRADA"
                      ? <><TrendingUp size={12} className="inline mr-1" />Entrada</>
                      : <><TrendingDown size={12} className="inline mr-1" />Saída</>
                    }
                  </Badge>
                </TableCell>
                <TableCell>
                  <p className="font-medium text-sm">{m.produto.nome}</p>
                  <p className="text-xs text-slate-400">{m.produto.codigo}</p>
                </TableCell>
                <TableCell className="text-sm">{MOTIVO_LABEL[m.motivo] ?? m.motivo}</TableCell>
                <TableCell className="text-right font-mono">{m.quantidade}</TableCell>
                <TableCell className="text-right">
                  R$ {m.valorTotal.toLocaleString("pt-BR", { minimumFractionDigits: 2 })}
                </TableCell>
                <TableCell className="text-right font-mono">{m.estoqueDepois}</TableCell>
                <TableCell className="text-sm text-slate-500">{m.usuarioEmail}</TableCell>
                <TableCell className="text-sm text-slate-500 whitespace-nowrap">{formatDate(m.criadoEm)}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      {data && data.totalPages > 1 && (
        <div className="flex justify-end gap-2">
          <Button variant="outline" size="sm" onClick={() => setPage((p) => p - 1)} disabled={page === 0}>Anterior</Button>
          <span className="text-sm text-slate-500 self-center">Página {page + 1} de {data.totalPages}</span>
          <Button variant="outline" size="sm" onClick={() => setPage((p) => p + 1)} disabled={page >= data.totalPages - 1}>Próxima</Button>
        </div>
      )}

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>Nova movimentação</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit((d) => registrar.mutate(d))} className="space-y-4 mt-2">
            <div className="space-y-1">
              <Label>Produto</Label>
              <select className="w-full border rounded-md px-3 py-2 text-sm" {...register("produtoId")}>
                <option value="">Selecione...</option>
                {produtos?.content.filter((p) => p.ativo).map((p) => (
                  <option key={p.id} value={p.id}>{p.nome} ({p.codigo})</option>
                ))}
              </select>
              {errors.produtoId && <p className="text-xs text-red-500">{errors.produtoId.message}</p>}
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1">
                <Label>Tipo</Label>
                <select className="w-full border rounded-md px-3 py-2 text-sm" {...register("tipo")}>
                  <option value="ENTRADA">Entrada</option>
                  <option value="SAIDA">Saída</option>
                </select>
              </div>
              <div className="space-y-1">
                <Label>Motivo</Label>
                <select className="w-full border rounded-md px-3 py-2 text-sm" {...register("motivo")}>
                  {MOTIVOS.map((m) => <option key={m} value={m}>{MOTIVO_LABEL[m]}</option>)}
                </select>
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1">
                <Label>Quantidade</Label>
                <Input type="number" step="0.001" {...register("quantidade")} />
                {errors.quantidade && <p className="text-xs text-red-500">{errors.quantidade.message}</p>}
              </div>
              <div className="space-y-1">
                <Label>Preço unitário</Label>
                <Input type="number" step="0.01" {...register("precoUnitario")} />
              </div>
            </div>
            <div className="space-y-1">
              <Label>Observação</Label>
              <Input {...register("observacao")} />
            </div>
            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancelar</Button>
              <Button type="submit" className="bg-blue-700 hover:bg-blue-800" disabled={registrar.isPending}>
                {registrar.isPending ? "Registrando..." : "Registrar"}
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
