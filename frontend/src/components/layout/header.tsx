"use client";

import { useEffect, useState } from "react";
import { usePathname } from "next/navigation";
import { Bell } from "lucide-react";
import { Button } from "@/components/ui/button";
import Cookies from "js-cookie";

const PAGE_TITLES: Record<string, string> = {
  "/dashboard": "Dashboard",
  "/dashboard/produtos": "Produtos",
  "/dashboard/movimentacoes": "Movimentações",
  "/dashboard/notas-fiscais": "Notas Fiscais",
  "/dashboard/categorias": "Categorias",
  "/dashboard/fornecedores": "Fornecedores",
};

function getInitials(email: string) {
  return email.split("@")[0].slice(0, 2).toUpperCase();
}

export function Header() {
  const pathname = usePathname();
  const title = PAGE_TITLES[pathname] ?? "Dashboard";
  const [initials, setInitials] = useState("U");
  const [email, setEmail] = useState("");

  useEffect(() => {
    try {
      const token = Cookies.get("token");
      if (token) {
        const b64 = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
        const payload = JSON.parse(atob(b64));
        const sub: string = payload.sub ?? "";
        setEmail(sub);
        setInitials(getInitials(sub));
      }
    } catch {
      // token inválido — ignora
    }
  }, []);

  return (
    <header className="h-14 border-b border-slate-200 bg-white flex items-center justify-between px-6 sticky top-0 z-10 shrink-0">
      <h2 className="font-semibold text-slate-700">{title}</h2>
      <div className="flex items-center gap-3">
        <Button variant="ghost" size="icon" className="text-slate-400 hover:text-slate-600">
          <Bell size={17} />
        </Button>
        <div
          className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center cursor-default"
          title={email}
        >
          <span className="text-white text-xs font-semibold leading-none">{initials}</span>
        </div>
      </div>
    </header>
  );
}
