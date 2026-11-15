import { useEffect, useState } from "react";
import API, { getApiErrorMessage } from "../api/api";
import {
  downloadReportCsv,
  downloadReportPdf,
  formatAmountLabel,
  formatDateLabel,
  formatPercentLabel,
  formatStatusLabel
} from "../utils/reportExport";

function RapportsPage() {
  const [lots, setLots] = useState([]);
  const [selectedLotId, setSelectedLotId] = useState("");
  const [report, setReport] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const loadLots = async () => {
      try {
        const response = await API.get("/lots");
        setLots(response.data);

        if (response.data.length > 0) {
          setSelectedLotId(String(response.data[0].id));
        }
      } catch (loadError) {
        setError(getApiErrorMessage(loadError, "Impossible de charger les rapports."));
      }
    };

    loadLots();
  }, []);

  useEffect(() => {
    if (!selectedLotId) {
      setReport(null);
      return;
    }

    const loadReport = async () => {
      setLoading(true);
      setError("");

      try {
        const response = await API.get(`/rapports/lots/${selectedLotId}`);
        setReport(response.data);
      } catch (loadError) {
        setError(getApiErrorMessage(loadError, "Impossible de charger ce rapport."));
      } finally {
        setLoading(false);
      }
    };

    loadReport();
  }, [selectedLotId]);

  const exporterExcel = () => {
    if (!report) {
      return;
    }

    downloadReportCsv(report);
  };

  const exporterPdf = () => {
    if (!report) {
      return;
    }

    downloadReportPdf(report);
  };

  return (
    <section className="report-page">
      <div className="section-header print-hidden">
        <div>
          <p className="eyebrow">Pilotage et impression</p>
          <h2>Rapports</h2>
          <p className="helper-text">
            Consultez l'historique d'un lot, ses ventes, ses pertes et sa
            rentabilite dans un format pret a imprimer.
          </p>
        </div>
      </div>

      {error && <p className="alert alert-error print-hidden">{error}</p>}

      <div className="form-card report-toolbar print-hidden">
        <div className="form-grid">
          <label className="field">
            <span>Lot a analyser</span>
            <select
              value={selectedLotId}
              onChange={(event) => setSelectedLotId(event.target.value)}
            >
              {lots.length === 0 && <option value="">Aucun lot disponible</option>}
              {lots.map((lot) => (
                <option key={lot.id} value={lot.id}>
                  {lot.nomLot || `Lot #${lot.id}`}
                </option>
              ))}
            </select>
          </label>
        </div>

        <div className="actions-row">
          <button
            type="button"
            className="primary-button"
            onClick={exporterPdf}
            disabled={!report}
          >
            Exporter PDF
          </button>

          <button
            type="button"
            className="secondary-button"
            onClick={exporterExcel}
            disabled={!report}
          >
            Exporter Excel
          </button>
        </div>

        <p className="helper-text form-note">
          Le PDF est telecharge directement. Le fichier Excel est fourni au
          format CSV, lisible dans Excel.
        </p>
      </div>

      {!loading && !report && !error && (
        <div className="table-card">
          <p className="empty-state">
            Aucun rapport disponible pour le moment.
          </p>
        </div>
      )}

      {loading && (
        <div className="table-card">
          <p className="empty-state">Chargement du rapport en cours...</p>
        </div>
      )}

      {report && (
        <article className="report-sheet">
          <div className="report-header">
            <div>
              <p className="eyebrow">Avistock</p>
              <h3 className="sheet-title">Rapport de lot</h3>
              <p className="helper-text">
                Document de suivi pour l'exploitation et l'impression.
              </p>
            </div>

            <div className="report-badge-wrap">
              <span
                className={`status-badge ${
                  report.rentable ? "status-positive" : "status-negative"
                }`}
              >
                {report.rentable ? "Rentable" : "A surveiller"}
              </span>
            </div>
          </div>

          <div className="report-meta">
            <div className="summary-card">
              <p className="summary-label">Lot</p>
              <p className="summary-value">{report.nomLot || `Lot #${report.lotId}`}</p>
            </div>
            <div className="summary-card">
              <p className="summary-label">Statut</p>
              <p className="summary-value">{formatStatusLabel(report.statutLot)}</p>
            </div>
            <div className="summary-card">
              <p className="summary-label">Arrivee</p>
              <p className="summary-value">{formatDateLabel(report.dateArrivee)}</p>
            </div>
            <div className="summary-card">
              <p className="summary-label">Maturite prevue</p>
              <p className="summary-value">{formatDateLabel(report.dateMaturitePrevue)}</p>
            </div>
            <div className="summary-card">
              <p className="summary-label">Genere le</p>
              <p className="summary-value">{formatDateLabel(report.dateGeneration)}</p>
            </div>
          </div>

          <div className="report-summary-grid">
            <div className="summary-card">
              <p className="summary-label">Stock initial</p>
              <p className="summary-value">{report.quantiteInitiale}</p>
            </div>
            <div className="summary-card">
              <p className="summary-label">Quantite vendue</p>
              <p className="summary-value">{report.quantiteVendue}</p>
            </div>
            <div className="summary-card">
              <p className="summary-label">Quantite perdue</p>
              <p className="summary-value">{report.quantitePerdue}</p>
            </div>
            <div className="summary-card">
              <p className="summary-label">Stock restant</p>
              <p className="summary-value">{report.quantiteRestante}</p>
            </div>
            <div className="summary-card">
              <p className="summary-label">Chiffre d'affaires</p>
              <p className="summary-value">{formatAmountLabel(report.chiffreAffaires)}</p>
            </div>
            <div className="summary-card">
              <p className="summary-label">Charges</p>
              <p className="summary-value">{formatAmountLabel(report.chargesTotales)}</p>
            </div>
            <div className="summary-card">
              <p className="summary-label">Benefice net</p>
              <p className="summary-value">{formatAmountLabel(report.beneficeNet)}</p>
            </div>
            <div className="summary-card">
              <p className="summary-label">Marge</p>
              <p className="summary-value">{formatPercentLabel(report.margePourcentage)}</p>
            </div>
            <div className="summary-card">
              <p className="summary-label">Taux de mortalite</p>
              <p className="summary-value">{formatPercentLabel(report.tauxMortalite)}</p>
            </div>
          </div>

          <section className="report-section">
            <div className="section-header compact-header">
              <div>
                <p className="eyebrow">Historique commercial</p>
                <h3>Ventes du lot</h3>
              </div>
            </div>

            <div className="table-card">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Client</th>
                    <th>Quantite</th>
                    <th>Prix total</th>
                    <th>Description</th>
                  </tr>
                </thead>
                <tbody>
                  {report.commandes.length === 0 && (
                    <tr>
                      <td colSpan="5" className="empty-state">
                        Aucune vente enregistree pour ce lot.
                      </td>
                    </tr>
                  )}

                  {report.commandes.map((commande) => (
                    <tr key={commande.commandeId}>
                      <td>{formatDateLabel(commande.dateCommande)}</td>
                      <td>{commande.nomClient || "-"}</td>
                      <td>{commande.quantite}</td>
                      <td>{formatAmountLabel(commande.prixTotal)}</td>
                      <td>{commande.description || "-"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>

          <section className="report-section">
            <div className="section-header compact-header">
              <div>
                <p className="eyebrow">Historique sanitaire</p>
                <h3>Pertes du lot</h3>
              </div>
            </div>

            <div className="table-card">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Quantite</th>
                    <th>Cause</th>
                    <th>Description</th>
                  </tr>
                </thead>
                <tbody>
                  {report.pertes.length === 0 && (
                    <tr>
                      <td colSpan="4" className="empty-state">
                        Aucune perte enregistree pour ce lot.
                      </td>
                    </tr>
                  )}

                  {report.pertes.map((perte) => (
                    <tr key={perte.perteId}>
                      <td>{formatDateLabel(perte.datePerte)}</td>
                      <td>{perte.quantite}</td>
                      <td>{perte.cause || "-"}</td>
                      <td>{perte.description || "-"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        </article>
      )}
    </section>
  );
}

export default RapportsPage;
