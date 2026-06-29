"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import api from "@/lib/api";
import { Categoria } from "@/types";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Plus, Pencil } from "lucide-react";

export default function CategoriasPage() {
  const qc = useQueryClient();
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<Categoria | null>(null);

  const { data = [], isLoading } = useQuery<Categoria[]>({
    queryKey: ["categorias"],
    queryFn: () => api.get("/api/categorias").then((r) => r.data),
  });

  const { register, handleSubmit, reset } = useForm<{ nome: string; descricao?: string }>();

  const save = useMutation({
    mutationFn: (d: { nome: string; descricao?: string }) =>
      editing ? api.put(`/api/categorias/${editing.id}`, d) : api.post("/api/categorias", d),
    onSuccess: () => {
      toast.success(editing ? "Categoria atualizada!" : "Categoria criada!");
      qc.invalidateQueries({ queryKey: ["categorias"] });
      setOpen(false);
    },
    onError: (e: any) => toast.error(e.response?.data?.detail ?? "Erro ao salvar"),
  });

  function openNew() {
    setEditing(null);
    reset({ nome: "", descricao: "" });
    setOpen(true);
  }

  function openEdit(c: Categoria) {
    setEditing(c);
    reset({ nome: c.nome, descricao: c.descricao ?? "" });
    setOpen(true);
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <p className="text-sm text-slate-500">Classificação dos produtos</p>
        <Button onClick={openNew} className="bg-blue-700 hover:bg-blue-800">
          <Plus size={16} className="mr-2" /> Nova categoria
        </Button>
      </div>

      <div className="rounded-lg border bg-white shadow-sm">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Nome</TableHead>
              <TableHead>Descrição</TableHead>
              <TableHead />
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading && (
              <TableRow><TableCell colSpan={3} className="text-center py-8 text-slate-400">Carregando...</TableCell></TableRow>
            )}
            {data.map((c) => (
              <TableRow key={c.id}>
                <TableCell className="font-medium">{c.nome}</TableCell>
                <TableCell className="text-slate-500">{c.descricao ?? "-"}</TableCell>
                <TableCell className="text-right">
                  <Button size="icon" variant="ghost" onClick={() => openEdit(c)}><Pencil size={14} /></Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="max-w-sm">
          <DialogHeader>
            <DialogTitle>{editing ? "Editar categoria" : "Nova categoria"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit((d) => save.mutate(d))} className="space-y-4 mt-2">
            <div className="space-y-1">
              <Label>Nome</Label>
              <Input {...register("nome")} />
            </div>
            <div className="space-y-1">
              <Label>Descrição</Label>
              <Input {...register("descricao")} />
            </div>
            <div className="flex justify-end gap-2">
              <Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancelar</Button>
              <Button type="submit" className="bg-blue-700 hover:bg-blue-800" disabled={save.isPending}>Salvar</Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
