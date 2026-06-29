"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useForm, useFieldArray } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast } from "sonner";
import api from "@/lib/api";
import { NotaFiscal, Produto, Page } from "@/types";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Plus, Download, Trash2 } from "lucide-react";

const itemSchema = z.object({
  produtoId: z.coerce.number().optional(),
  descricao: z.string().min(1, "Obrigatório"),
  quantidade: z.coerce.number().positive(),
  unidadeMedida: z.string().min(1),
  precoUnitario: z.coerce.number().positive(),
});

const schema = z.object({
  tipo: z.enum(["ENTRADA", "SAIDA"]),
  destinatarioNome: z.string().min(1, "Obrigatório"),
  destinatarioCnpjCpf: z.string().optional(),
  destinatarioEndereco: z.string().optional(),
  observacao: z.string().optional(),
  itens: z.array(itemSchema).min(1, "Adicione pelo menos um item"),
});
type FormData = z.infer<typeof schema>;

export default function NotasFiscaisPage() {
  const qc = useQueryClient();
  const [open, setOpen] = useState(false);
  const [page, setPage] = useState(0);

  const { data, isLoading } = useQuery<Page<NotaFiscal>>({
    queryKey: ["notas-fiscais", page],
    queryFn: () => api.get(`/api/notas-fiscais?page=${page}&size=15`).then((r) => r.data),
  });

  const { data: produtos } = useQuery<Page<Produto>>({
    queryKey: ["produtos-select"],
    queryFn: () => api.get("/api/produtos?size=200&sort=nome").then((r) => r.data),
  });

  const { register, handleSubmit, control, reset, setValue, formState: { errors } } = useForm<FormData>({
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    resolver: zodResolver(schema) as any,
    defaultValues: { tipo: "SAIDA", itens: [{ descricao: "", quantidade: 1, unidadeMedida: "UN", precoUnitario: 0 }] },
  });

  const { fields, append, remove } = useFieldArray({ control, name: "itens" });

  const emitir = useMutation({
    mutationFn: (d: FormData) => api.post("/api/notas-fiscais", d),
    onSuccess: () => {
      toast.success("Nota fiscal emitida!");
      qc.invalidateQueries({ queryKey: ["notas-fiscais"] });
      setOpen(false);
    },
    onError: (e: any) => toast.error(e.response?.data?.detail ?? "Erro ao emitir nota fiscal"),
  });

  async function downloadPdf(id: number, numero: string) {
    try {
      const res = await api.get(`/api/notas-fiscais/${id}/pdf`, { responseType: "blob" });
      const url = URL.createObjectURL(res.data);
      const a = document.createElement("a");
      a.href = url;
      a.download = `NF-${numero}-001.pdf`;
      a.click();
      URL.revokeObjectURL(url);
    } catch {
      toast.error("Erro ao baixar PDF");
    }
  }

  function handleProdutoSelect(index: number, produtoId: string) {
    const produto = produtos?.content.find((p) => p.id === Number(produtoId));
    if (produto) {
      setValue(`itens.${index}.descricao`, produto.nome);
      setValue(`itens.${index}.unidadeMedida`, produto.unidadeMedida);
      setValue(`itens.${index}.precoUnitario`, produto.precoCusto);
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <p className="text-sm text-slate-500">Emissão e download de notas fiscais</p>
        <Button onClick={() => { reset(); setOpen(true); }} className="bg-blue-700 hover:bg-blue-800">
          <Plus size={16} className="mr-2" /> Emitir NF
        </Button>
      </div>

      <div className="rounded-lg border bg-white shadow-sm">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Número</TableHead>
              <TableHead>Tipo</TableHead>
              <TableHead>Destinatário</TableHead>
              <TableHead className="text-right">Valor total</TableHead>
              <TableHead>Emissão</TableHead>
              <TableHead>Status</TableHead>
              <TableHead />
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading && (
              <TableRow><TableCell colSpan={7} className="text-center py-8 text-slate-400">Carregando...</TableCell></TableRow>
            )}
            {data?.content.map((nf) => (
              <TableRow key={nf.id}>
                <TableCell className="font-mono text-sm">{nf.numero}/{nf.serie}</TableCell>
                <TableCell>
                  <Badge variant={nf.tipo === "ENTRADA" ? "default" : "secondary"}
                    className={nf.tipo === "ENTRADA" ? "bg-blue-100 text-blue-700 hover:bg-blue-100" : "bg-amber-100 text-amber-700 hover:bg-amber-100"}>
                    {nf.tipo}
                  </Badge>
                </TableCell>
                <TableCell>
                  <p className="font-medium text-sm">{nf.destinatarioNome}</p>
                  {nf.destinatarioCnpjCpf && <p className="text-xs text-slate-400">{nf.destinatarioCnpjCpf}</p>}
                </TableCell>
                <TableCell className="text-right font-medium">
                  R$ {nf.valorTotal.toLocaleString("pt-BR", { minimumFractionDigits: 2 })}
                </TableCell>
                <TableCell className="text-sm text-slate-500">
                  {new Date(nf.dataEmissao).toLocaleString("pt-BR", { dateStyle: "short", timeStyle: "short" })}
                </TableCell>
                <TableCell>
                  <Badge variant={nf.status === "EMITIDA" ? "default" : "destructive"}
                    className={nf.status === "EMITIDA" ? "bg-emerald-100 text-emerald-700 hover:bg-emerald-100" : ""}>
                    {nf.status}
                  </Badge>
                </TableCell>
                <TableCell className="text-right">
                  <Button size="icon" variant="ghost" onClick={() => downloadPdf(nf.id, nf.numero)}>
                    <Download size={14} />
                  </Button>
                </TableCell>
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
        <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Emitir Nota Fiscal</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit((d) => emitir.mutate(d))} className="space-y-4 mt-2">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1">
                <Label>Tipo</Label>
                <select className="w-full border rounded-md px-3 py-2 text-sm" {...register("tipo")}>
                  <option value="SAIDA">Saída</option>
                  <option value="ENTRADA">Entrada</option>
                </select>
              </div>
              <div className="space-y-1">
                <Label>Destinatário</Label>
                <Input {...register("destinatarioNome")} placeholder="Nome ou razão social" />
                {errors.destinatarioNome && <p className="text-xs text-red-500">{errors.destinatarioNome.message}</p>}
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1">
                <Label>CNPJ/CPF</Label>
                <Input {...register("destinatarioCnpjCpf")} placeholder="Opcional" />
              </div>
              <div className="space-y-1">
                <Label>Endereço</Label>
                <Input {...register("destinatarioEndereco")} placeholder="Opcional" />
              </div>
            </div>

            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <Label>Itens</Label>
                <Button type="button" size="sm" variant="outline"
                  onClick={() => append({ descricao: "", quantidade: 1, unidadeMedida: "UN", precoUnitario: 0 })}>
                  <Plus size={14} className="mr-1" /> Item
                </Button>
              </div>
              {fields.map((field, i) => (
                <div key={field.id} className="border rounded-md p-3 space-y-2 bg-slate-50">
                  <div className="flex items-center gap-2">
                    <select className="border rounded-md px-2 py-1 text-sm flex-1"
                      onChange={(e) => handleProdutoSelect(i, e.target.value)}>
                      <option value="">Selecionar produto...</option>
                      {produtos?.content.filter((p) => p.ativo).map((p) => (
                        <option key={p.id} value={p.id}>{p.nome}</option>
                      ))}
                    </select>
                    {fields.length > 1 && (
                      <Button type="button" size="icon" variant="ghost" onClick={() => remove(i)}>
                        <Trash2 size={14} className="text-red-400" />
                      </Button>
                    )}
                  </div>
                  <Input {...register(`itens.${i}.descricao`)} placeholder="Descrição do item" />
                  <div className="grid grid-cols-3 gap-2">
                    <Input type="number" step="0.001" {...register(`itens.${i}.quantidade`)} placeholder="Qtd" />
                    <select className="border rounded-md px-2 py-1 text-sm" {...register(`itens.${i}.unidadeMedida`)}>
                      {["UN","KG","G","L","ML","M","M2","M3","CX","PC","PAR","RL"].map((u) => <option key={u}>{u}</option>)}
                    </select>
                    <Input type="number" step="0.0001" {...register(`itens.${i}.precoUnitario`)} placeholder="Preço unit." />
                  </div>
                </div>
              ))}
              {errors.itens && <p className="text-xs text-red-500">Adicione pelo menos um item</p>}
            </div>

            <div className="space-y-1">
              <Label>Observação</Label>
              <Input {...register("observacao")} />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancelar</Button>
              <Button type="submit" className="bg-blue-700 hover:bg-blue-800" disabled={emitir.isPending}>
                {emitir.isPending ? "Emitindo..." : "Emitir NF"}
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
