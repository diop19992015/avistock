import { useEffect, useState } from "react";
import API, { getApiErrorMessage } from "../api/api";

const CAUSES = [
  "Mortalite naturelle",
  "Maladie",
  "Accident",
  "Predation",
  "Autre"
];

function PertesPage() {
  const [pertes, setPertes] = useState([]);
  const [lots, setLots] = useState([]);
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState("success");
  const [form, setForm] = useState({
    datePerte: new Date().toISOString().split("T")[0],
    lotId: "",
    quantite: "",
    cause: "Mortalite naturelle",
    description: ""
  });

  const loadData = async () => {
    try {
      const [pertesResponse, lotsResponse] = await Promise.all([
        API.get("/pertes"),
        API.get("/lots")
      ]);

      setPertes(pertesResponse.data);
      setLots(lotsResponse.data);
    } catch (error) {
      setMessage(getApiErrorMessage(error, "Impossible de charger les pertes."));
      setMessageType("error");
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const lotsDisponibles = lots.filter((lot) => Number(lot.quantiteRestante) > 0);
  const lotSelectionne = lots.find((lot) => lot.id === Number(form.lotId));
  const totalPertes = pertes.reduce(
    (sum, perte) => sum + Number(perte.quantite || 0),
    0
  );

  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage("");

    try {
      await API.post("/pertes", {
        datePerte: form.datePerte,
        quantite: Number(form.quantite),
        cause: form.cause,
        description: form.description.trim(),
        lot: { id: Number(form.lotId) }
      });

      setForm({
        datePerte: new Date().toISOString().split("T")[0],
        lotId: "",
        quantite: "",
        cause: "Mortalite naturelle",
        description: ""
      });
      setMessage("Perte enregistree et stock mis a jour.");
      setMessageType("success");
      loadData();
    } catch (error) {
      setMessage(getApiErrorMessage(error, "Impossible d'enregistrer la perte."));
      setMessageType("error");
    }
  };

  const supprimerPerte = async (id) => {
    setMessage("");

    try {
      await API.delete(`/pertes/${id}`);
      setMessage("Perte annulee et stock recredite.");
      setMessageType("success");
      loadData();
    } catch (error) {
      setMessage(getApiErrorMessage(error, "Impossible de supprimer la perte."));
      setMessageType("error");
    }
  };

  return (
    <section>
      <div className="section-header">
        <div>
          <p className="eyebrow">Suivi sanitaire</p>
          <h2>Pertes</h2>
          <p className="helper-text">
            Chaque perte enregistree diminue automatiquement le stock vivant du
            lot concerne.
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
            <span>Date de perte</span>
            <input
              type="date"
              value={form.datePerte}
              onChange={(event) =>
                setForm({ ...form, datePerte: event.target.value })
              }
              required
            />
          </label>

          <label className="field">
            <span>Lot</span>
            <select
              value={form.lotId}
              onChange={(event) => setForm({ ...form, lotId: event.target.value })}
              required
            >
              <option value="">Selectionner un lot</option>
              {lotsDisponibles.map((lot) => (
                <option key={lot.id} value={lot.id}>
                  {(lot.nomLot || `Lot #${lot.id}`)} - stock {lot.quantiteRestante}
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            <span>Quantite perdue</span>
            <input
              type="number"
              min="1"
              value={form.quantite}
              onChange={(event) =>
                setForm({ ...form, quantite: event.target.value })
              }
              placeholder="Nombre de sujets"
              required
            />
          </label>

          <label className="field">
            <span>Cause</span>
            <select
              value={form.cause}
              onChange={(event) => setForm({ ...form, cause: event.target.value })}
            >
              {CAUSES.map((cause) => (
                <option key={cause} value={cause}>
                  {cause}
                </option>
              ))}
            </select>
          </label>

          <label className="field field-wide">
            <span>Description</span>
            <input
              value={form.description}
              onChange={(event) =>
                setForm({ ...form, description: event.target.value })
              }
              placeholder="Observation sur la perte ou le contexte sanitaire"
            />
          </label>
        </div>

        <div className="actions-row">
          <button className="primary-button" type="submit">
            Enregistrer la perte
          </button>

          {lotSelectionne && (
            <span className="stock-pill">
              Stock vivant : {lotSelectionne.quantiteRestante}
            </span>
          )}

          <span className="stock-pill">Pertes cumulees : {totalPertes}</span>
        </div>
      </form>

      <div className="table-card">
        <table className="data-table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Lot</th>
              <th>Quantite</th>
              <th>Cause</th>
              <th>Description</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {pertes.length === 0 && (
              <tr>
                <td colSpan="6" className="empty-state">
                  Aucune perte enregistree pour le moment.
                </td>
              </tr>
            )}

            {pertes.map((perte) => (
              <tr key={perte.id}>
                <td>{perte.datePerte || "-"}</td>
                <td>{perte.lot?.nomLot || `Lot #${perte.lot?.id ?? "-"}`}</td>
                <td>{perte.quantite}</td>
                <td>{perte.cause || "-"}</td>
                <td>{perte.description || "-"}</td>
                <td>
                  <button
                    type="button"
                    className="danger-button"
                    onClick={() => supprimerPerte(perte.id)}
                  >
                    Annuler
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

export default PertesPage;
