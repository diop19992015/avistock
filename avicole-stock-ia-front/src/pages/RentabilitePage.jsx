import { useEffect, useState } from "react";
import API, { getApiErrorMessage } from "../api/api";
import StatCard from "../components/StatCard";

function RentabilitePage() {
  const [globalStats, setGlobalStats] = useState({
    nombreLots: 0,
    chiffreAffairesTotal: 0,
    chargesTotales: 0,
    beneficeNet: 0,
    margePourcentage: 0,
    tauxMortaliteGlobal: 0,
    quantiteVendue: 0,
    quantitePerdue: 0,
    stockRestant: 0
  });
  const [lots, setLots] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadRentabilite = async () => {
      try {
        const [globalResponse, lotsResponse] = await Promise.all([
          API.get("/rentabilite/global"),
          API.get("/rentabilite/lots")
        ]);

        setGlobalStats(globalResponse.data);
        setLots(lotsResponse.data);
      } catch (loadError) {
        setError(
          getApiErrorMessage(loadError, "Impossible de charger la rentabilite.")
        );
      }
    };

    loadRentabilite();
  }, []);

  return (
    <section>
      <div className="section-header">
        <div>
          <p className="eyebrow">Performance economique</p>
          <h2>Rentabilite</h2>
          <p className="helper-text">
            Le benefice net est calcule par lot a partir des ventes moins les charges.
          </p>
        </div>
      </div>

      {error && <p className="alert alert-error">{error}</p>}

      <div className="metric-grid">
        <StatCard
          title="Benefice net"
          value={`${globalStats.beneficeNet.toFixed(2)} FCFA`}
          icon="📈"
        />
        <StatCard
          title="Chiffre d'affaires"
          value={`${globalStats.chiffreAffairesTotal.toFixed(2)} FCFA`}
          icon="💰"
        />
        <StatCard
          title="Charges"
          value={`${globalStats.chargesTotales.toFixed(2)} FCFA`}
          icon="🧾"
        />
        <StatCard
          title="Mortalite"
          value={`${globalStats.tauxMortaliteGlobal.toFixed(2)} %`}
          icon="⚠️"
        />
      </div>

      <div className="table-card">
        <table className="data-table">
          <thead>
            <tr>
              <th>Lot</th>
              <th>Vendus</th>
              <th>Pertes</th>
              <th>Stock</th>
              <th>CA</th>
              <th>Charges</th>
              <th>Benefice</th>
              <th>Marge</th>
              <th>Etat</th>
            </tr>
          </thead>
          <tbody>
            {lots.length === 0 && (
              <tr>
                <td colSpan="9" className="empty-state">
                  Aucun lot n'est encore disponible pour le calcul de rentabilite.
                </td>
              </tr>
            )}

            {lots.map((lot) => (
              <tr key={lot.lotId}>
                <td>{lot.nomLot || `Lot #${lot.lotId}`}</td>
                <td>{lot.quantiteVendue}</td>
                <td>{lot.quantitePerdue}</td>
                <td>{lot.quantiteRestante}</td>
                <td>{lot.chiffreAffaires.toFixed(2)} FCFA</td>
                <td>{lot.chargesTotales.toFixed(2)} FCFA</td>
                <td>{lot.beneficeNet.toFixed(2)} FCFA</td>
                <td>{lot.margePourcentage.toFixed(2)} %</td>
                <td>
                  <span
                    className={`status-badge ${
                      lot.rentable ? "status-positive" : "status-negative"
                    }`}
                  >
                    {lot.rentable ? "Rentable" : "A surveiller"}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}

export default RentabilitePage;
