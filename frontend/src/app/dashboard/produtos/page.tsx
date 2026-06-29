"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast } from "sonner";
import api from "@/lib/api";
import { Produto, Categoria, Page } from "@/types";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Plus, Pencil, Power } from "lucide-react";

const schema = z.object({
  codigo: z.string().min(1, "Obrigatório"),
  nome: z.string().min(1, "Obrigatório"),
  descricao: z.string().optional(),
  unidadeMedida: z.string().min(1, "Obrigatório"),
  estoqueMinimo: z.coerce.number().min(0),
  precoCusto: z.coerce.number().min(0),
  categoriaId: z.coerce.number().optional().nullable(),
});
type FormData = z.infer<typeof schema>;

const UNIDADES = ["UN", "KG", "G", "L", "ML", "M", "M2", "M3", "CX", "PC", "PAR", "RL"];

export default function ProdutosPage() {
  const qc = useQueryClient();
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<Produto | null>(null);
  const [page, setPage] = useState(0);

  const { data, isLoading } = useQuery<Page<Produto>>({
    queryKey: ["produtos", page],
    queryFn: () => api.get(`/api/produtos?page=${page}&size=10&sort=nome`).then((r) => r.data),
  });

  const { data: categorias = [] } = useQuery<Categoria[]>({
    queryKey: ["categorias"],
    queryFn: () => api.get("/api/categorias").then((r) => r.data),
  });

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormData>({
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    resolver: zodResolver(schema) as any,
  });

  const save = useMutation({
    mutationFn: (d: FormData) =>
      editing
        ? api.put(`/api/produtos/${editing.id}`, d)
        : api.post("/api/produtos", d),
    onSuccess: () => {
      toast.success(editing ? "Produto atualizado!" : "Produto cadastrado!");
      qc.invalidateQueries({ queryKey: ["produtos"] });
      setOpen(false);
    },
    onError: (e: any) => toast.error(e.response?.data?.detail ?? "Erro ao salvar produto"),
  });

  const toggle = useMutation({
    mutationFn: (p: Produto) =>
      p.ativo
        ? api.delete(`/api/produtos/${p.id}`)
        : api.patch(`/api/produtos/${p.id}/reativar`),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["produtos"] });
      toast.success("Status atualizado!");
    },
  });

  function openNew() {
    setEditing(null);
    reset({ codigo: "", nome: "", descricao: "", unidadeMedida: "UN", estoqueMinimo: 0, precoCusto: 0, categoriaId: null });
    setOpen(true);
  }

  function openEdit(p: Produto) {
    setEditing(p);
    reset({
      codigo: p.codigo, nome: p.nome, descricao: p.descricao ?? "",
      unidadeMedida: p.unidadeMedida, estoqueMinimo: p.estoqueMinimo,
      precoCusto: p.precoCusto, categoriaId: p.categoriaId ?? null,
    });
    setOpen(true);
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <p className="text-sm text-slate-500">Catálogo de produtos do almoxarifado</p>
        <Button onClick={openNew} className="bg-blue-700 hover:bg-blue-800">
          <Plus size={16} className="mr-2" /> Novo produto
        </Button>
      </div>

      <div className="rounded-lg border bg-white shadow-sm">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Código</TableHead>
              <TableHead>Nome</TableHead>
              <TableHead>Categoria</TableHead>
              <TableHead>UN</TableHead>
              <TableHead className="text-right">Estoque</TableHead>
              <TableHead className="text-right">Mínimo</TableHead>
              <TableHead className="text-right">Custo</TableHead>
              <TableHead>Status</TableHead>
              <TableHead />
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading && (
              <TableRow><TableCell colSpan={9} className="text-center py-8 text-slate-400">Carregando...</TableCell></TableRow>
            )}
            {data?.content.map((p) => (
              <TableRow key={p.id}>
                <TableCell className="font-mono text-sm">{p.codigo}</TableCell>
                <TableCell className="font-medium">{p.nome}</TableCell>
                <TableCell className="text-sm text-slate-500">{p.categoriaNome ?? <span className="text-slate-300">—</span>}</TableCell>
                <TableCell>{p.unidadeMedida}</TableCell>
                <TableCell className={`text-right font-medium ${p.estoqueAtual < p.estoqueMinimo ? "text-red-500" : ""}`}>
                  {p.estoqueAtual}
                </TableCell>
                <TableCell className="text-right text-slate-500">{p.estoqueMinimo}</TableCell>
                <TableCell className="text-right">
                  R$ {p.precoCusto.toLocaleString("pt-BR", { minimumFractionDigits: 2 })}
                </TableCell>
                <TableCell>
                  <Badge variant={p.ativo ? "default" : "secondary"}>
                    {p.ativo ? "Ativo" : "Inativo"}
                  </Badge>
                </TableCell>
                <TableCell className="text-right">
                  <div className="flex gap-1 justify-end">
                    <Button size="icon" variant="ghost" onClick={() => openEdit(p)}><Pencil size={14} /></Button>
                    <Button size="icon" variant="ghost" onClick={() => toggle.mutate(p)}>
                      <Power size={14} className={p.ativo ? "text-red-400" : "text-emerald-500"} />
                    </Button>
                  </div>
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
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>{editing ? "Editar produto" : "Novo produto"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit((d) => save.mutate(d))} className="space-y-4 mt-2">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1">
                <Label>Código</Label>
                <Input {...register("codigo")} />
                {errors.codigo && <p className="text-xs text-red-500">{errors.codigo.message}</p>}
              </div>
              <div className="space-y-1">
                <Label>Unidade</Label>
                <select {...register("unidadeMedida")}>
                  {UNIDADES.map((u) => <option key={u}>{u}</option>)}
                </select>
              </div>
            </div>
            <div className="space-y-1">
              <Label>Nome</Label>
              <Input {...register("nome")} />
              {errors.nome && <p className="text-xs text-red-500">{errors.nome.message}</p>}
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1">
                <Label>Categoria</Label>
                <select {...register("categoriaId")}>
                  <option value="">Sem categoria</option>
                  {categorias.map((c) => (
                    <option key={c.id} value={c.id}>{c.nome}</option>
                  ))}
                </select>
              </div>
              <div className="space-y-1">
                <Label>Descrição</Label>
                <Input {...register("descricao")} />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1">
                <Label>Estoque mínimo</Label>
                <Input type="number" step="0.001" {...register("estoqueMinimo")} />
              </div>
              <div className="space-y-1">
                <Label>Preço de custo</Label>
                <Input type="number" step="0.01" {...register("precoCusto")} />
              </div>
            </div>
            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancelar</Button>
              <Button type="submit" className="bg-blue-700 hover:bg-blue-800" disabled={save.isPending}>
                {save.isPending ? "Salvando..." : "Salvar"}
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
