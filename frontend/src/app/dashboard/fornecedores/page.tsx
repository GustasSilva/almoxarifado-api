"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import api from "@/lib/api";
import { Fornecedor } from "@/types";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Plus, Pencil } from "lucide-react";

type FormData = { razaoSocial: string; cnpj?: string; email?: string; telefone?: string };

export default function FornecedoresPage() {
  const qc = useQueryClient();
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<Fornecedor | null>(null);

  const { data = [], isLoading } = useQuery<Fornecedor[]>({
    queryKey: ["fornecedores"],
    queryFn: () => api.get("/api/fornecedores").then((r) => r.data),
  });

  const { register, handleSubmit, reset } = useForm<FormData>();

  const save = useMutation({
    mutationFn: (d: FormData) =>
      editing ? api.put(`/api/fornecedores/${editing.id}`, d) : api.post("/api/fornecedores", d),
    onSuccess: () => {
      toast.success(editing ? "Fornecedor atualizado!" : "Fornecedor cadastrado!");
      qc.invalidateQueries({ queryKey: ["fornecedores"] });
      setOpen(false);
    },
    onError: (e: any) => toast.error(e.response?.data?.detail ?? "Erro ao salvar"),
  });

  function openNew() {
    setEditing(null);
    reset({ razaoSocial: "", cnpj: "", email: "", telefone: "" });
    setOpen(true);
  }

  function openEdit(f: Fornecedor) {
    setEditing(f);
    reset({ razaoSocial: f.razaoSocial, cnpj: f.cnpj ?? "", email: f.email ?? "", telefone: f.telefone ?? "" });
    setOpen(true);
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Fornecedores</h1>
          <p className="text-sm text-slate-500">Cadastro de fornecedores</p>
        </div>
        <Button onClick={openNew} className="bg-blue-700 hover:bg-blue-800">
          <Plus size={16} className="mr-2" /> Novo fornecedor
        </Button>
      </div>

      <div className="rounded-lg border bg-white shadow-sm">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Razão Social</TableHead>
              <TableHead>CNPJ</TableHead>
              <TableHead>E-mail</TableHead>
              <TableHead>Telefone</TableHead>
              <TableHead />
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading && (
              <TableRow><TableCell colSpan={5} className="text-center py-8 text-slate-400">Carregando...</TableCell></TableRow>
            )}
            {data.map((f) => (
              <TableRow key={f.id}>
                <TableCell className="font-medium">{f.razaoSocial}</TableCell>
                <TableCell className="font-mono text-sm">{f.cnpj ?? "-"}</TableCell>
                <TableCell className="text-slate-500">{f.email ?? "-"}</TableCell>
                <TableCell className="text-slate-500">{f.telefone ?? "-"}</TableCell>
                <TableCell className="text-right">
                  <Button size="icon" variant="ghost" onClick={() => openEdit(f)}><Pencil size={14} /></Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="max-w-sm">
          <DialogHeader>
            <DialogTitle>{editing ? "Editar fornecedor" : "Novo fornecedor"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit((d) => save.mutate(d))} className="space-y-4 mt-2">
            <div className="space-y-1">
              <Label>Razão Social</Label>
              <Input {...register("razaoSocial")} />
            </div>
            <div className="space-y-1">
              <Label>CNPJ</Label>
              <Input {...register("cnpj")} placeholder="00.000.000/0001-00" />
            </div>
            <div className="space-y-1">
              <Label>E-mail</Label>
              <Input type="email" {...register("email")} />
            </div>
            <div className="space-y-1">
              <Label>Telefone</Label>
              <Input {...register("telefone")} />
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
