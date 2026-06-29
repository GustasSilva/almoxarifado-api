"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import Cookies from "js-cookie";
import { cn } from "@/lib/utils";
import {
  LayoutDashboard, Package, ArrowLeftRight, FileText,
  Tag, Truck, LogOut,
} from "lucide-react";

const links = [
  { href: "/dashboard", label: "Dashboard", icon: LayoutDashboard },
  { href: "/dashboard/produtos", label: "Produtos", icon: Package },
  { href: "/dashboard/movimentacoes", label: "Movimentações", icon: ArrowLeftRight },
  { href: "/dashboard/notas-fiscais", label: "Notas Fiscais", icon: FileText },
  { href: "/dashboard/categorias", label: "Categorias", icon: Tag },
  { href: "/dashboard/fornecedores", label: "Fornecedores", icon: Truck },
];

export function Sidebar() {
  const pathname = usePathname();
  const router = useRouter();

  function logout() {
    Cookies.remove("token");
    router.push("/login");
  }

  return (
    <aside className="w-60 min-h-screen bg-slate-900 text-slate-100 flex flex-col shrink-0">
      <div className="h-14 flex items-center gap-3 px-5 border-b border-slate-800">
        <div className="w-7 h-7 bg-blue-500 rounded-lg flex items-center justify-center">
          <span className="text-white text-xs font-bold">A</span>
        </div>
        <span className="font-semibold text-sm tracking-tight">Almoxarifado</span>
      </div>

      <nav className="flex-1 py-3 space-y-0.5 px-3">
        {links.map(({ href, label, icon: Icon }) => {
          const active = pathname === href;
          return (
            <Link
              key={href}
              href={href}
              className={cn(
                "flex items-center gap-3 px-3 py-2.5 rounded-md text-sm transition-colors relative",
                active
                  ? "bg-slate-800 text-white font-medium"
                  : "text-slate-400 hover:bg-slate-800 hover:text-slate-100"
              )}
            >
              {active && (
                <span className="absolute left-0 top-1/2 -translate-y-1/2 w-0.5 h-5 bg-blue-400 rounded-r-full" />
              )}
              <Icon size={16} />
              {label}
            </Link>
          );
        })}
      </nav>

      <div className="p-3 border-t border-slate-800">
        <button
          onClick={logout}
          className="flex items-center gap-3 px-3 py-2.5 rounded-md text-sm text-slate-400 hover:bg-slate-800 hover:text-slate-100 w-full transition-colors"
        >
          <LogOut size={16} />
          Sair
        </button>
      </div>
    </aside>
  );
}
