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
  title, value, sub, icon: Icon, color,
}: {
  title: string; value: string; sub?: string;
  icon: React.ElementType; color: string;
}) {
  return (
    <Card>
      <CardContent className="pt-6">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-slate-500">{title}</p>
            <p className="text-2xl font-bold mt-1">{value}</p>
            {sub && <p className="text-xs text-slate-400 mt-1">{sub}</p>}
          </div>
          <div className={`p-3 rounded-full ${color}`}>
            <Icon size={20} className="text-white" />
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
        <Skeleton className="h-8 w-48" />
        <div className="grid grid-cols-4 gap-4">
          {[...Array(4)].map((_, i) => <Skeleton key={i} className="h-28" />)}
        </div>
        <Skeleton className="h-72" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-800">Dashboard</h1>
        <p className="text-sm text-slate-500">Visão geral do almoxarifado</p>
      </div>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Produtos ativos"
          value={String(data?.estoque.produtosAtivos ?? 0)}
          sub={`${data?.estoque.totalProdutos ?? 0} no total`}
          icon={Package}
          color="bg-blue-500"
        />
        <StatCard
          title="Valor em estoque"
          value={`R$ ${(data?.estoque.valorTotalEmEstoque ?? 0).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}`}
          icon={DollarSign}
          color="bg-emerald-500"
        />
        <StatCard
          title="Movimentações hoje"
          value={String(data?.movimentacoes.hoje ?? 0)}
          sub={`${data?.movimentacoes.mes ?? 0} no mês`}
          icon={TrendingUp}
          color="bg-violet-500"
        />
        <StatCard
          title="Alertas de estoque"
          value={String(data?.estoque.produtosAbaixoDoMinimo ?? 0)}
          sub="abaixo do mínimo"
          icon={AlertTriangle}
          color="bg-amber-500"
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <Card className="col-span-2">
          <CardHeader>
            <CardTitle className="text-base">Movimentações do mês</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={220}>
              <BarChart data={chartData} barSize={40}>
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                <YAxis tick={{ fontSize: 12 }} />
                <Tooltip />
                <Legend />
                <Bar dataKey="Entradas" fill="#3b82f6" radius={[4, 4, 0, 0]} />
                <Bar dataKey="Saídas" fill="#f59e0b" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
            <div className="grid grid-cols-2 gap-4 mt-4">
              <div className="text-center">
                <p className="text-xs text-slate-400">Valor entradas</p>
                <p className="font-semibold text-blue-600">
                  R$ {(data?.movimentacoes.entradasMes.valorTotal ?? 0).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}
                </p>
              </div>
              <div className="text-center">
                <p className="text-xs text-slate-400">Valor saídas</p>
                <p className="font-semibold text-amber-600">
                  R$ {(data?.movimentacoes.saidasMes.valorTotal ?? 0).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-base flex items-center gap-2">
              <AlertTriangle size={16} className="text-amber-500" />
              Alertas de estoque
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {data?.alertasEstoqueMinimo.length === 0 && (
              <p className="text-sm text-slate-400 text-center py-4">Nenhum alerta</p>
            )}
            {data?.alertasEstoqueMinimo.map((a) => (
              <div key={a.id} className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium">{a.nome}</p>
                  <p className="text-xs text-slate-400">{a.codigo}</p>
                </div>
                <div className="text-right">
                  <Badge variant="destructive" className="text-xs">
                    {a.estoqueAtual} / {a.estoqueMinimo}
                  </Badge>
                </div>
              </div>
            ))}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
