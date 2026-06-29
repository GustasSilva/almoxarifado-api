"use client";

import { useQuery } from "@tanstack/react-query";
import api from "@/lib/api";
import { DashboardResponse } from "@/types";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { Package, TrendingUp, AlertTriangle, DollarSign } from "lucide-react";
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, Legend,
} from "recharts";

function StatCard({
  title, value, sub, icon: Icon, bg, fg,
}: {
  title: string; value: string; sub?: string;
  icon: React.ElementType; bg: string; fg: string;
}) {
  return (
    <Card className="hover:shadow-md transition-shadow">
      <CardContent className="p-5">
        <div className="flex items-start justify-between gap-4">
          <div className="min-w-0 flex-1">
            <p className="text-xs font-medium text-slate-500 uppercase tracking-wide truncate">{title}</p>
            <p className="text-2xl font-bold text-slate-800 mt-1 leading-tight">{value}</p>
            {sub && <p className="text-xs text-slate-400 mt-1">{sub}</p>}
          </div>
          <div className={`p-2.5 rounded-xl shrink-0 ${bg}`}>
            <Icon size={18} className={fg} />
          </div>
        </div>
      </CardContent>
    </Card>
  );
}

export default function DashboardPage() {
  const { data, isLoading } = useQuery<DashboardResponse>({
    queryKey: ["dashboard"],
    queryFn: () => api.get("/api/dashboard").then((r) => r.data),
    refetchInterval: 60_000,
  });

  const chartData = data
    ? [
        {
          name: "Mês atual",
          Entradas: data.movimentacoes.entradasMes.quantidade,
          Saídas: data.movimentacoes.saidasMes.quantidade,
        },
      ]
    : [];

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
          {[...Array(4)].map((_, i) => <Skeleton key={i} className="h-24" />)}
        </div>
        <Skeleton className="h-72" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Produtos ativos"
          value={String(data?.estoque.produtosAtivos ?? 0)}
          sub={`${data?.estoque.totalProdutos ?? 0} no total`}
          icon={Package}
          bg="bg-blue-50"
          fg="text-blue-600"
        />
        <StatCard
          title="Valor em estoque"
          value={`R$ ${(data?.estoque.valorTotalEmEstoque ?? 0).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}`}
          icon={DollarSign}
          bg="bg-emerald-50"
          fg="text-emerald-600"
        />
        <StatCard
          title="Movimentações hoje"
          value={String(data?.movimentacoes.hoje ?? 0)}
          sub={`${data?.movimentacoes.mes ?? 0} no mês`}
          icon={TrendingUp}
          bg="bg-violet-50"
          fg="text-violet-600"
        />
        <StatCard
          title="Alertas de estoque"
          value={String(data?.estoque.produtosAbaixoDoMinimo ?? 0)}
          sub="abaixo do mínimo"
          icon={AlertTriangle}
          bg="bg-amber-50"
          fg="text-amber-600"
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <Card className="col-span-2">
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-semibold text-slate-600 uppercase tracking-wide">
              Movimentações do mês
            </CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={220}>
              <BarChart data={chartData} barSize={36} barCategoryGap="30%">
                <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" vertical={false} />
                <XAxis dataKey="name" tick={{ fontSize: 12, fill: "#94a3b8" }} axisLine={false} tickLine={false} />
                <YAxis tick={{ fontSize: 12, fill: "#94a3b8" }} axisLine={false} tickLine={false} />
                <Tooltip
                  contentStyle={{ borderRadius: "8px", border: "1px solid #e2e8f0", fontSize: 12 }}
                  cursor={{ fill: "#f8fafc" }}
                />
                <Legend wrapperStyle={{ fontSize: 12, paddingTop: 8 }} />
                <Bar dataKey="Entradas" fill="#3b82f6" radius={[4, 4, 0, 0]} />
                <Bar dataKey="Saídas" fill="#f59e0b" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
            <div className="grid grid-cols-2 gap-4 mt-2 pt-4 border-t border-slate-100">
              <div className="text-center">
                <p className="text-xs text-slate-400 mb-0.5">Valor entradas</p>
                <p className="font-semibold text-blue-600">
                  R$ {(data?.movimentacoes.entradasMes.valorTotal ?? 0).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}
                </p>
              </div>
              <div className="text-center">
                <p className="text-xs text-slate-400 mb-0.5">Valor saídas</p>
                <p className="font-semibold text-amber-600">
                  R$ {(data?.movimentacoes.saidasMes.valorTotal ?? 0).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-semibold text-slate-600 uppercase tracking-wide flex items-center gap-2">
              <AlertTriangle size={14} className="text-amber-500" />
              Alertas de estoque
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {data?.alertasEstoqueMinimo.length === 0 && (
              <div className="py-8 text-center">
                <p className="text-sm text-slate-400">Nenhum alerta</p>
                <p className="text-xs text-slate-300 mt-1">Todos os produtos estão no nível adequado</p>
              </div>
            )}
            {data?.alertasEstoqueMinimo.map((a) => (
              <div key={a.id} className="flex items-center justify-between py-1">
                <div className="min-w-0">
                  <p className="text-sm font-medium text-slate-700 truncate">{a.nome}</p>
                  <p className="text-xs text-slate-400">{a.codigo}</p>
                </div>
                <Badge variant="destructive" className="ml-2 shrink-0 text-xs">
                  {a.estoqueAtual}/{a.estoqueMinimo}
                </Badge>
              </div>
            ))}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
