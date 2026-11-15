import { useEffect, useState } from "react";
import API, { getApiErrorMessage } from "../api/api";
import StatCard from "../components/StatCard";

function Dashboard() {
  const [stats, setStats] = useState({
    clients: 0,
    commandes: 0,
    pertes: 0,
    stock: 0,
    chiffreAffaires: 0,
    beneficeNet: 0
  });
  const [error, setError] = useState("");

  useEffect(() => {
    const loadStats = async () => {
      try {
        const [clients, commandes, lots, pertes, rentabilite] = await Promise.all([
          API.get("/clients"),
          API.get("/commandes"),
          API.get("/lots"),
          API.get("/pertes"),
          API.get("/rentabilite/global")
        ]);

        const totalStock = lots.data.reduce(
          (sum, lot) => sum + Number(lot.quantiteRestante || 0),
          0
        );

        const totalCA = commandes.data.reduce(
          (sum, commande) => sum + Number(commande.prixTotal || 0),
          0
        );

        setStats({
          clients: clients.data.length,
          commandes: commandes.data.length,
          pertes: pertes.data.reduce(
            (sum, perte) => sum + Number(perte.quantite || 0),
            0
          ),
          stock: totalStock,
          chiffreAffaires: totalCA,
          beneficeNet: Number(rentabilite.data.beneficeNet || 0)
        });
      } catch (loadError) {
        setError(
          getApiErrorMessage(loadError, "Impossible de charger le tableau de bord.")
        );
      }
    };

    loadStats();
  }, []);

  return (
    <section>
      <div className="section-header">
        <div>
          <p className="eyebrow">Vue d'ensemble</p>
          <h2>Tableau de bord</h2>
          <p className="helper-text">
            Les indicateurs se recalculent a partir des clients, lots et commandes.
          </p>
        </div>
      </div>

      {error && <p className="alert alert-error">{error}</p>}

      <div className="metric-grid">
        <StatCard title="Clients" value={stats.clients} icon="👥" />
        <StatCard title="Commandes" value={stats.commandes} icon="📦" />
        <StatCard title="Pertes" value={stats.pertes} icon="⚠️" />
        <StatCard title="Stock vivant" value={stats.stock} icon="🐔" />
        <StatCard
          title="Chiffre d'affaires"
          value={`${stats.chiffreAffaires} FCFA`}
          icon="💰"
        />
        <StatCard
          title="Benefice net"
          value={`${stats.beneficeNet} FCFA`}
          icon="📈"
        />
      </div>
    </section>
  );
}

export default Dashboard;
