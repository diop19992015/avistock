import { useState } from "react";
import "./App.css";
import ClientsPage from "./pages/ClientsPage";
import CommandesPage from "./pages/CommandesPage";
import Dashboard from "./pages/Dashboard";
import LotsPage from "./pages/LotsPage";
import PertesPage from "./pages/PertesPage";
import PrevisionPage from "./pages/PrevisionPage";
import RapportsPage from "./pages/RapportsPage";
import RentabilitePage from "./pages/RentabilitePage";

function App() {
  const [activePage, setActivePage] = useState("dashboard");

  const pages = {
    dashboard: <Dashboard />,
    clients: <ClientsPage />,
    lots: <LotsPage />,
    pertes: <PertesPage />,
    commandes: <CommandesPage />,
    prevision: <PrevisionPage />,
    rapports: <RapportsPage />,
    rentabilite: <RentabilitePage />
  };

  const navigationItems = [
    { id: "dashboard", label: "Tableau de bord" },
    { id: "clients", label: "Clients" },
    { id: "lots", label: "Lots" },
    { id: "pertes", label: "Pertes" },
    { id: "commandes", label: "Commandes" },
    { id: "prevision", label: "IA predictive" },
    { id: "rapports", label: "Rapports" },
    { id: "rentabilite", label: "Rentabilite" }
  ];

  return (
    <div className="app-shell">
      <header className="app-header">
        <div className="brand-panel">
          <p className="brand-kicker">Plateforme de gestion avicole</p>
          <h1 className="brand-name">Avistock</h1>
          <p className="page-description brand-summary">
            Pilotez les clients, les lots, les ventes, le stock vivant et la
            rentabilite depuis un seul espace.
          </p>
        </div>

        <nav className="app-nav" aria-label="Navigation principale">
          {navigationItems.map((item) => (
            <button
              key={item.id}
              type="button"
              className={`nav-button ${activePage === item.id ? "active" : ""}`}
              onClick={() => setActivePage(item.id)}
            >
              {item.label}
            </button>
          ))}
        </nav>
      </header>

      <main className="page-panel">{pages[activePage]}</main>
    </div>
  );
}

export default App;
