import { useEffect, useState } from "react";
import API, { getApiErrorMessage } from "../api/api";

function ClientsPage() {
  const [clients, setClients] = useState([]);
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState("success");
  const [form, setForm] = useState({
    nom: "",
    prenom: "",
    telephone: "",
    adresse: ""
  });

  const [recherche, setRecherche] = useState("");

  // Charger clients
  const loadClients = async () => {
    try {
      const response = await API.get("/clients");
      setClients(response.data);
    } catch (error) {
      setMessage(getApiErrorMessage(error, "Impossible de charger les clients."));
      setMessageType("error");
    }
  };

  useEffect(() => {
    const initializeClients = async () => {
      try {
        const response = await API.get("/clients");
        setClients(response.data);
      } catch (error) {
        setMessage(getApiErrorMessage(error, "Impossible de charger les clients."));
        setMessageType("error");
      }
    };

    initializeClients();
  }, []);

  // Ajouter client
  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage("");

    try {
      await API.post("/clients", form);
      setForm({ nom: "", prenom: "", telephone: "", adresse: "" });
      setMessage("Client enregistre avec succes.");
      setMessageType("success");
      loadClients();
    } catch (error) {
      setMessage(getApiErrorMessage(error, "Impossible d'enregistrer le client."));
      setMessageType("error");
    }
  };

  // Supprimer client
  const supprimerClient = async (id) => {
    setMessage("");

    try {
      await API.delete(`/clients/${id}`);
      setMessage("Client supprime avec succes.");
      setMessageType("success");
      loadClients();
    } catch (error) {
      setMessage(getApiErrorMessage(error, "Impossible de supprimer le client."));
      setMessageType("error");
    }
  };

  // Filtrage recherche
  const clientsFiltres = clients.filter((client) =>
    `${client.nom} ${client.prenom}`.toLowerCase().includes(recherche.toLowerCase())
  );

  return (
    <section>
      <div className="section-header">
        <div>
          <p className="eyebrow">Gestion commerciale</p>
          <h2>Clients</h2>
          <p className="helper-text">
            Les clients enregistres sont reutilises directement dans les ventes.
          </p>
        </div>

        <label className="field search-field">
          <span>Recherche</span>
          <input
            placeholder="Rechercher un client..."
            value={recherche}
            onChange={(event) => setRecherche(event.target.value)}
          />
        </label>
      </div>

      {message && (
        <p className={`alert ${messageType === "error" ? "alert-error" : "alert-success"}`}>
          {message}
        </p>
      )}

      <form className="form-card" onSubmit={handleSubmit}>
        <div className="form-grid">
          <label className="field">
            <span>Nom</span>
            <input
              value={form.nom}
              onChange={(event) => setForm({ ...form, nom: event.target.value })}
              required
            />
          </label>
          <label className="field">
            <span>Prenom</span>
            <input
              value={form.prenom}
              onChange={(event) => setForm({ ...form, prenom: event.target.value })}
              required
            />
          </label>
          <label className="field">
            <span>Telephone</span>
            <input
              value={form.telephone}
              onChange={(event) => setForm({ ...form, telephone: event.target.value })}
            />
          </label>
          <label className="field">
            <span>Adresse</span>
            <input
              value={form.adresse}
              onChange={(event) => setForm({ ...form, adresse: event.target.value })}
            />
          </label>
        </div>

        <div className="actions-row">
          <button className="primary-button" type="submit">
            Ajouter le client
          </button>
        </div>
      </form>

      <div className="table-card">
        <table className="data-table">
          <thead>
            <tr>
              <th>Nom</th>
              <th>Prenom</th>
              <th>Telephone</th>
              <th>Adresse</th>
              <th>Action</th>
            </tr>
          </thead>

          <tbody>
            {clientsFiltres.length === 0 && (
              <tr>
                <td colSpan="5" className="empty-state">
                  Aucun client ne correspond a votre recherche.
                </td>
              </tr>
            )}

            {clientsFiltres.map((client) => (
              <tr key={client.id}>
                <td>{client.nom}</td>
                <td>{client.prenom}</td>
                <td>{client.telephone || "-"}</td>
                <td>{client.adresse || "-"}</td>
                <td>
                  <button
                    type="button"
                    className="danger-button"
                    onClick={() => supprimerClient(client.id)}
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

export default ClientsPage;
