import { useEffect, useState } from "react";
import API, { getApiErrorMessage } from "../api/api";

function CommandesPage() {
  const [commandes, setCommandes] = useState([]);
  const [clients, setClients] = useState([]);
  const [lots, setLots] = useState([]);
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState("success");

  const [form, setForm] = useState({
    dateCommande: new Date().toISOString().split("T")[0],
    clientId: "",
    lotId: "",
    quantite: "",
    prixTotal: "",
    description: ""
  });

  const loadData = async () => {
    try {
      const [commandesResponse, clientsResponse, lotsResponse] = await Promise.all([
        API.get("/commandes"),
        API.get("/clients"),
        API.get("/lots")
      ]);

      setCommandes(commandesResponse.data);
      setClients(clientsResponse.data);
      setLots(lotsResponse.data);
    } catch (error) {
      setMessage(getApiErrorMessage(error, "Impossible de charger les commandes."));
      setMessageType("error");
    }
  };

  useEffect(() => {
    const initializeCommandes = async () => {
      try {
        const [commandesResponse, clientsResponse, lotsResponse] = await Promise.all([
          API.get("/commandes"),
          API.get("/clients"),
          API.get("/lots")
        ]);

        setCommandes(commandesResponse.data);
        setClients(clientsResponse.data);
        setLots(lotsResponse.data);
      } catch (error) {
        setMessage(getApiErrorMessage(error, "Impossible de charger les commandes."));
        setMessageType("error");
      }
    };

    initializeCommandes();
  }, []);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage("");

    const data = {
      dateCommande: form.dateCommande,
      quantite: Number(form.quantite),
      prixTotal: Number(form.prixTotal),
      description: form.description.trim(),
      client: { id: Number(form.clientId) },
      lot: { id: Number(form.lotId) }
    };

    try {
      await API.post("/commandes", data);
      setForm({
        dateCommande: new Date().toISOString().split("T")[0],
        clientId: "",
        lotId: "",
        quantite: "",
        prixTotal: "",
        description: ""
      });
      setMessage("Commande enregistree et stock mis a jour.");
      setMessageType("success");
      loadData();
    } catch (error) {
      setMessage(getApiErrorMessage(error, "Erreur lors de l'ajout de la commande."));
      setMessageType("error");
    }
  };

  const supprimerCommande = async (id) => {
    setMessage("");

    try {
      await API.delete(`/commandes/${id}`);
      setMessage("Commande supprimee et stock recredite.");
      setMessageType("success");
      loadData();
    } catch (error) {
      setMessage(getApiErrorMessage(error, "Impossible de supprimer la commande."));
      setMessageType("error");
    }
  };

  const lotsDisponibles = lots.filter((lot) => Number(lot.quantiteRestante) > 0);
  const lotSelectionne = lots.find((lot) => lot.id === Number(form.lotId));

  return (
    <section>
      <div className="section-header">
        <div>
          <p className="eyebrow">Flux de vente</p>
          <h2>Commandes</h2>
          <p className="helper-text">
            Une commande diminue automatiquement le stock restant du lot selectionne.
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
            <span>Date de commande</span>
            <input
              type="date"
              value={form.dateCommande}
              onChange={(event) =>
                setForm({ ...form, dateCommande: event.target.value })
              }
              required
            />
          </label>

          <label className="field">
            <span>Client</span>
            <select
              value={form.clientId}
              onChange={(event) =>
                setForm({ ...form, clientId: event.target.value })
              }
              required
            >
              <option value="">Selectionner un client</option>
              {clients.map((client) => (
                <option key={client.id} value={client.id}>
                  {client.nom} {client.prenom}
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            <span>Lot</span>
            <select
              value={form.lotId}
              onChange={(event) =>
                setForm({ ...form, lotId: event.target.value })
              }
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
            <span>Quantite vendue</span>
            <input
              type="number"
              min="1"
              placeholder="Quantite"
              value={form.quantite}
              onChange={(event) =>
                setForm({ ...form, quantite: event.target.value })
              }
              required
            />
          </label>

          <label className="field">
            <span>Prix total</span>
            <input
              type="number"
              min="0"
              step="0.01"
              placeholder="Prix total"
              value={form.prixTotal}
              onChange={(event) =>
                setForm({ ...form, prixTotal: event.target.value })
              }
              required
            />
          </label>

          <label className="field field-wide">
            <span>Description</span>
            <input
              placeholder="Observation sur la vente"
              value={form.description}
              onChange={(event) =>
                setForm({ ...form, description: event.target.value })
              }
            />
          </label>
        </div>

        <div className="actions-row">
          <button className="primary-button" type="submit">
            Enregistrer la commande
          </button>

          {lotSelectionne && (
            <span className="stock-pill">
              Stock disponible : {lotSelectionne.quantiteRestante}
            </span>
          )}
        </div>
      </form>

      <div className="table-card">
        <table className="data-table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Client</th>
              <th>Lot</th>
              <th>Quantite</th>
              <th>Prix total</th>
              <th>Description</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {commandes.length === 0 && (
              <tr>
                <td colSpan="7" className="empty-state">
                  Aucune commande enregistree pour le moment.
                </td>
              </tr>
            )}

            {commandes.map((commande) => (
              <tr key={commande.id}>
                <td>{commande.dateCommande || "-"}</td>
                <td>
                  {commande.client
                    ? `${commande.client.nom} ${commande.client.prenom}`
                    : "-"}
                </td>
                <td>{commande.lot?.nomLot || `Lot #${commande.lot?.id ?? "-"}`}</td>
                <td>{commande.quantite}</td>
                <td>{commande.prixTotal}</td>
                <td>{commande.description || "-"}</td>
                <td>
                  <button
                    type="button"
                    className="danger-button"
                    onClick={() => supprimerCommande(commande.id)}
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

export default CommandesPage;
