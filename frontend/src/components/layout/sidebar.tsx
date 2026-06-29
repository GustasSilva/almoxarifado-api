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
    <aside className="w-60 min-h-screen bg-slate-900 text-slate-100 flex flex-col">
      <div className="h-16 flex items-center gap-3 px-5 border-b border-slate-700">
        <div className="w-8 h-8 bg-blue-500 rounded-md flex items-center justify-center">
          <span className="text-white text-sm font-bold">A</span>
        </div>
        <span className="font-semibold text-sm">Almoxarifado</span>
      </div>

      <nav className="flex-1 py-4 space-y-1 px-2">
        {links.map(({ href, label, icon: Icon }) => (
          <Link
            key={href}
            href={href}
            className={cn(
              "flex items-center gap-3 px-3 py-2 rounded-md text-sm transition-colors",
              pathname === href
                ? "bg-blue-600 text-white"
                : "text-slate-400 hover:bg-slate-800 hover:text-white"
            )}
          >
            <Icon size={16} />
            {label}
          </Link>
        ))}
      </nav>

      <div className="p-2 border-t border-slate-700">
        <button
          onClick={logout}
          className="flex items-center gap-3 px-3 py-2 rounded-md text-sm text-slate-400 hover:bg-slate-800 hover:text-white w-full transition-colors"
        >
          <LogOut size={16} />
          Sair
        </button>
      </div>
    </aside>
  );
}
