import { useEffect, useState } from "react";
import API, { getApiErrorMessage } from "../api/api";

const LOT_STATUS_LABELS = {
  EN_ELEVAGE: "En elevage",
  PRET_A_VENDRE: "Pret a vendre",
  EPUISE: "Epuise"
};

function getLotStatusLabel(status) {
  return LOT_STATUS_LABELS[status] ?? status ?? "-";
}

function getLotStatusClassName(status) {
  if (status === "PRET_A_VENDRE") {
    return "status-positive";
  }

  if (status === "EPUISE") {
    return "status-negative";
  }

  return "status-neutral";
}

function LotsPage() {
  const [lots, setLots] = useState([]);
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState("success");
  const [form, setForm] = useState({
    nomLot: "",
    dateArrivee: new Date().toISOString().split("T")[0],
    quantiteInitiale: "",
    coutAchat: "",
    coutAliment: "",
    coutSante: "",
    autresCharges: ""
  });

  const loadLots = async () => {
    try {
      const response = await API.get("/lots");
      setLots(response.data);
    } catch (error) {
      setMessage(getApiErrorMessage(error, "Impossible de charger les lots."));
      setMessageType("error");
    }
  };

  useEffect(() => {
    const initializeLots = async () => {
      try {
        const response = await API.get("/lots");
        setLots(response.data);
      } catch (error) {
        setMessage(getApiErrorMessage(error, "Impossible de charger les lots."));
        setMessageType("error");
      }
    };

    initializeLots();
  }, []);

  const resetForm = () => {
    setForm({
      nomLot: "",
      dateArrivee: new Date().toISOString().split("T")[0],
      quantiteInitiale: "",
      coutAchat: "",
      coutAliment: "",
      coutSante: "",
      autresCharges: ""
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage("");

    try {
      await API.post("/lots", {
        nomLot: form.nomLot.trim(),
        dateArrivee: form.dateArrivee,
        quantiteInitiale: Number(form.quantiteInitiale),
        coutAchat: Number(form.coutAchat || 0),
        coutAliment: Number(form.coutAliment || 0),
        coutSante: Number(form.coutSante || 0),
        autresCharges: Number(form.autresCharges || 0)
      });

      resetForm();
      setMessage("Lot enregistre avec succes.");
      setMessageType("success");
      loadLots();
    } catch (error) {
      setMessage(getApiErrorMessage(error, "Impossible d'enregistrer le lot."));
      setMessageType("error");
    }
  };

  const supprimerLot = async (id) => {
    setMessage("");

    try {
      await API.delete(`/lots/${id}`);
      setMessage("Lot supprime avec succes.");
      setMessageType("success");
      loadLots();
    } catch (error) {
      setMessage(getApiErrorMessage(error, "Impossible de supprimer le lot."));
      setMessageType("error");
    }
  };

  return (
    <section>
      <div className="section-header">
        <div>
          <p className="eyebrow">Gestion des lots</p>
          <h2>Lots de production</h2>
          <p className="helper-text">
            Chaque lot porte le stock disponible pour les ventes et son statut
            se met a jour automatiquement selon la maturite et le stock restant.
          </p>
        </div>
      </div>

      {message && (
        <p className={`alert ${messageType === "error" ? "alert-error" : "alert-success"}`}>
          {message}
        </p>
      )}

      <form className="form-card" onSubmit={handleSubmit}>
        <div className="form-grid">
          <label className="field">
            <span>Nom du lot</span>
            <input
              value={form.nomLot}
              onChange={(event) =>
                setForm({ ...form, nomLot: event.target.value })
              }
              placeholder="Exemple : Lot Avril 2026"
              required
            />
          </label>

          <label className="field">
            <span>Date d'arrivee</span>
            <input
              type="date"
              value={form.dateArrivee}
              onChange={(event) =>
                setForm({ ...form, dateArrivee: event.target.value })
              }
              required
            />
          </label>

          <label className="field">
            <span>Quantite initiale</span>
            <input
              type="number"
              min="1"
              value={form.quantiteInitiale}
              onChange={(event) =>
                setForm({ ...form, quantiteInitiale: event.target.value })
              }
              placeholder="Nombre de poulets"
              required
            />
          </label>

          <label className="field">
            <span>Cout d'achat</span>
            <input
              type="number"
              min="0"
              step="0.01"
              value={form.coutAchat}
              onChange={(event) =>
                setForm({ ...form, coutAchat: event.target.value })
              }
              placeholder="Prix des poussins"
            />
          </label>

          <label className="field">
            <span>Cout aliment</span>
            <input
              type="number"
              min="0"
              step="0.01"
              value={form.coutAliment}
              onChange={(event) =>
                setForm({ ...form, coutAliment: event.target.value })
              }
              placeholder="Depenses d'alimentation"
            />
          </label>

          <label className="field">
            <span>Cout sante</span>
            <input
              type="number"
              min="0"
              step="0.01"
              value={form.coutSante}
              onChange={(event) =>
                setForm({ ...form, coutSante: event.target.value })
              }
              placeholder="Vaccins et soins"
            />
          </label>

          <label className="field">
            <span>Autres charges</span>
            <input
              type="number"
              min="0"
              step="0.01"
              value={form.autresCharges}
              onChange={(event) =>
                setForm({ ...form, autresCharges: event.target.value })
              }
              placeholder="Transport, eau, energie..."
            />
          </label>
        </div>

        <p className="helper-text form-note">
          Le statut du lot est calcule automatiquement : en elevage, pret a
          vendre ou epuise.
        </p>

        <div className="actions-row">
          <button className="primary-button" type="submit">
            Enregistrer le lot
          </button>
        </div>
      </form>

      <div className="table-card">
        <table className="data-table">
          <thead>
            <tr>
              <th>Lot</th>
              <th>Arrivee</th>
              <th>Stock initial</th>
              <th>Stock restant</th>
              <th>Maturite prevue</th>
              <th>Charges</th>
              <th>Statut</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {lots.length === 0 && (
              <tr>
                <td colSpan="8" className="empty-state">
                  Aucun lot disponible pour le moment.
                </td>
              </tr>
            )}

            {lots.map((lot) => (
              <tr key={lot.id}>
                <td>{lot.nomLot || `Lot #${lot.id}`}</td>
                <td>{lot.dateArrivee || "-"}</td>
                <td>{lot.quantiteInitiale}</td>
                <td>
                  <span className="stock-pill">{lot.quantiteRestante}</span>
                </td>
                <td>{lot.dateMaturitePrevue || "-"}</td>
                <td>
                  {(
                    Number(lot.coutAchat || 0) +
                    Number(lot.coutAliment || 0) +
                    Number(lot.coutSante || 0) +
                    Number(lot.autresCharges || 0)
                  ).toFixed(2)} FCFA
                </td>
                <td>
                  <span
                    className={`status-badge ${getLotStatusClassName(lot.statutLot)}`}
                  >
                    {getLotStatusLabel(lot.statutLot)}
                  </span>
                </td>
                <td>
                  <button
                    type="button"
                    className="danger-button"
                    onClick={() => supprimerLot(lot.id)}
                  >
                    Supprimer
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}

export default LotsPage;
