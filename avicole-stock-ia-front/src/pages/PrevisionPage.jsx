import { useEffect, useState } from "react";
import API, { getApiErrorMessage } from "../api/api";

const EVENT_TEMPLATES = [
  {
    nomEvenement: "Grand Magal de Touba",
    typeEvenement: "RELIGIEUX",
    importance: "5",
    coefficientDemande: "38",
    joursImpactAvant: "10",
    joursImpactApres: "8",
    coefficientBaisseApres: "18",
    description: "Tres forte hausse de la demande autour du Magal, suivie d'un creux marque."
  },
  {
    nomEvenement: "Korite",
    typeEvenement: "RELIGIEUX",
    importance: "5",
    coefficientDemande: "42",
    joursImpactAvant: "8",
    joursImpactApres: "7",
    coefficientBaisseApres: "20",
    description: "Pic majeur sur les achats de poulets juste avant et pendant la fete."
  },
  {
    nomEvenement: "Nouvel An",
    typeEvenement: "CIVIL",
    importance: "4",
    coefficientDemande: "28",
    joursImpactAvant: "6",
    joursImpactApres: "5",
    coefficientBaisseApres: "14",
    description: "Hausse des achats festifs en fin d'annee, puis ralentissement debut janvier."
  },
  {
    nomEvenement: "Tabaski",
    typeEvenement: "RELIGIEUX",
    importance: "3",
    coefficientDemande: "12",
    joursImpactAvant: "5",
    joursImpactApres: "5",
    coefficientBaisseApres: "10",
    description: "Effet plus modere sur le poulet, mais a prendre en compte dans la planification."
  },
  {
    nomEvenement: "Gamou",
    typeEvenement: "RELIGIEUX",
    importance: "3",
    coefficientDemande: "16",
    joursImpactAvant: "5",
    joursImpactApres: "4",
    coefficientBaisseApres: "9",
    description: "Rassemblements et receptions pouvant soutenir les ventes sur quelques jours."
  },
  {
    nomEvenement: "Noel",
    typeEvenement: "CIVIL",
    importance: "2",
    coefficientDemande: "12",
    joursImpactAvant: "4",
    joursImpactApres: "4",
    coefficientBaisseApres: "8",
    description: "Hausse ponctuelle de fin d'annee, surtout en cumul avec les fetes de decembre."
  }
];

const ACTION_DETAILS = {
  AUGMENTER: {
    label: "Augmenter la prochaine bande",
    className: "decision-increase"
  },
  MAINTENIR: {
    label: "Maintenir le rythme habituel",
    className: "decision-maintain"
  },
  REDUIRE: {
    label: "Reduire pour eviter les invendus",
    className: "decision-decrease"
  }
};

const DEFAULT_FORECAST_FORM = {
  dateArriveeLot: new Date().toISOString().split("T")[0],
  dureeElevageJours: "50",
  fenetreVenteJours: "14"
};

const DEFAULT_EVENT_FORM = {
  nomEvenement: "",
  typeEvenement: "RELIGIEUX",
  dateDebut: "",
  dateFin: "",
  importance: "3",
  coefficientDemande: "15",
  joursImpactAvant: "5",
  joursImpactApres: "4",
  coefficientBaisseApres: "8"
};

function formatCoefficient(value) {
  return `${(Number(value || 0) * 100).toFixed(0)} %`;
}

function formatVariation(value) {
  const numericValue = Number(value || 0);
  const prefix = numericValue > 0 ? "+" : "";
  return `${prefix}${numericValue.toFixed(1)} %`;
}

function getVariationClassName(value) {
  if (value > 0) {
    return "factor-variation-positive";
  }

  if (value < 0) {
    return "factor-variation-negative";
  }

  return "factor-variation-neutral";
}

function getActionDetails(action) {
  return ACTION_DETAILS[action] ?? ACTION_DETAILS.MAINTENIR;
}

function PrevisionPage() {
  const [forecastForm, setForecastForm] = useState(DEFAULT_FORECAST_FORM);
  const [eventForm, setEventForm] = useState(DEFAULT_EVENT_FORM);
  const [events, setEvents] = useState([]);
  const [prediction, setPrediction] = useState(null);
  const [predictionError, setPredictionError] = useState("");
  const [eventMessage, setEventMessage] = useState("");
  const [eventMessageType, setEventMessageType] = useState("success");
  const [predictionLoading, setPredictionLoading] = useState(false);
  const [eventsLoading, setEventsLoading] = useState(false);

  const loadEvents = async () => {
    setEventsLoading(true);

    try {
      const response = await API.get("/evenements");
      setEvents(response.data);
    } catch (error) {
      setEventMessage(getApiErrorMessage(error, "Impossible de charger les fetes configurees."));
      setEventMessageType("error");
    } finally {
      setEventsLoading(false);
    }
  };

  useEffect(() => {
    loadEvents();
  }, []);

  const handleForecastSubmit = async (event) => {
    event.preventDefault();
    setPredictionError("");

    try {
      setPredictionLoading(true);

      const response = await API.post("/previsions/approvisionnement", {
        dateArriveeLot: forecastForm.dateArriveeLot,
        dureeElevageJours: Number(forecastForm.dureeElevageJours),
        fenetreVenteJours: Number(forecastForm.fenetreVenteJours)
      });

      setPrediction(response.data);
    } catch (error) {
      setPrediction(null);
      setPredictionError(getApiErrorMessage(error, "Impossible de calculer la prevision."));
    } finally {
      setPredictionLoading(false);
    }
  };

  const applyTemplate = (template) => {
    setEventForm({
      ...DEFAULT_EVENT_FORM,
      nomEvenement: template.nomEvenement,
      typeEvenement: template.typeEvenement,
      importance: template.importance,
      coefficientDemande: template.coefficientDemande,
      joursImpactAvant: template.joursImpactAvant,
      joursImpactApres: template.joursImpactApres,
      coefficientBaisseApres: template.coefficientBaisseApres
    });

    setEventMessage(
      `Modele "${template.nomEvenement}" charge. Renseignez maintenant la date exacte de cette edition.`
    );
    setEventMessageType("success");
  };

  const handleEventSubmit = async (event) => {
    event.preventDefault();
    setEventMessage("");

    try {
      await API.post("/evenements", {
        nomEvenement: eventForm.nomEvenement.trim(),
        typeEvenement: eventForm.typeEvenement,
        dateDebut: eventForm.dateDebut,
        dateFin: eventForm.dateFin || null,
        importance: Number(eventForm.importance),
        coefficientDemande: Number(eventForm.coefficientDemande || 0) / 100,
        joursImpactAvant: Number(eventForm.joursImpactAvant || 0),
        joursImpactApres: Number(eventForm.joursImpactApres || 0),
        coefficientBaisseApres: Number(eventForm.coefficientBaisseApres || 0) / 100
      });

      setEventForm(DEFAULT_EVENT_FORM);
      setEventMessage("Evenement enregistre avec succes dans le moteur de prevision.");
      setEventMessageType("success");
      loadEvents();
    } catch (error) {
      setEventMessage(getApiErrorMessage(error, "Impossible d'enregistrer cet evenement."));
      setEventMessageType("error");
    }
  };

  const supprimerEvenement = async (id) => {
    setEventMessage("");

    try {
      await API.delete(`/evenements/${id}`);
      setEventMessage("Evenement retire du moteur de prevision.");
      setEventMessageType("success");
      loadEvents();
    } catch (error) {
      if (error?.response?.status === 404) {
        setEventMessage(
          "La suppression des evenements n'est pas disponible sur le backend actif. Redemarrez Spring Boot pour charger la derniere version d'Avistock."
        );
      } else {
        setEventMessage(getApiErrorMessage(error, "Impossible de supprimer cet evenement."));
      }
      setEventMessageType("error");
    }
  };

  const actionDetails = getActionDetails(prediction?.actionRecommandee);

  return (
    <section className="prediction-page">
      <div className="section-header">
        <div>
          <p className="eyebrow">IA predictive</p>
          <h2>Prevision d'approvisionnement</h2>
          <p className="helper-text">
            Anticipez les ventes de poulets selon l'historique, le stock deja
            projete, la mortalite et les grandes fetes au Senegal.
          </p>
        </div>
      </div>

      <div className="prediction-layout">
        <form className="form-card prediction-card" onSubmit={handleForecastSubmit}>
          <div>
            <p className="eyebrow">Simulation</p>
            <h3 className="sheet-title">Quand lancer la prochaine bande ?</h3>
            <p className="helper-text">
              Saisissez la date d'arrivee des poussins et la fenetre de vente
              cible pour obtenir une recommandation intelligente.
            </p>
          </div>

          <div className="form-grid">
            <label className="field">
              <span>Date d'arrivee du prochain lot</span>
              <input
                type="date"
                value={forecastForm.dateArriveeLot}
                onChange={(event) =>
                  setForecastForm({
                    ...forecastForm,
                    dateArriveeLot: event.target.value
                  })
                }
                required
              />
            </label>

            <label className="field">
              <span>Duree d'elevage (jours)</span>
              <input
                type="number"
                min="35"
                max="75"
                value={forecastForm.dureeElevageJours}
                onChange={(event) =>
                  setForecastForm({
                    ...forecastForm,
                    dureeElevageJours: event.target.value
                  })
                }
                required
              />
            </label>

            <label className="field">
              <span>Fenetre de vente (jours)</span>
              <input
                type="number"
                min="7"
                max="30"
                value={forecastForm.fenetreVenteJours}
                onChange={(event) =>
                  setForecastForm({
                    ...forecastForm,
                    fenetreVenteJours: event.target.value
                  })
                }
                required
              />
            </label>
          </div>

          <div className="actions-row">
            <button
              type="submit"
              className="primary-button"
              disabled={predictionLoading}
            >
              {predictionLoading ? "Analyse en cours..." : "Lancer la prevision"}
            </button>
          </div>

          <p className="helper-text form-note">
            Les fetes mobiles comme la Korite, la Tabaski et le Grand Magal
            doivent etre mises a jour chaque annee avec leur date exacte.
          </p>
        </form>

        <div className="form-card prediction-card prediction-result-card">
          {predictionError && <p className="alert alert-error">{predictionError}</p>}

          {!prediction && !predictionLoading && !predictionError && (
            <div className="prediction-empty">
              <p className="eyebrow">Resultat attendu</p>
              <h3 className="sheet-title">Aucune simulation pour le moment</h3>
              <p className="helper-text">
                Lancez une prevision pour obtenir une suggestion d'augmentation,
                de maintien ou de reduction du nombre de poussins.
              </p>
            </div>
          )}

          {predictionLoading && (
            <div className="prediction-empty">
              <p className="eyebrow">Analyse en cours</p>
              <h3 className="sheet-title">Le moteur calcule la meilleure decision</h3>
              <p className="helper-text">
                Historique, stock projete, mortalite et fetes sont en train
                d'etre combines.
              </p>
            </div>
          )}

          {prediction && (
            <div className="prediction-result">
              <div className="prediction-banner">
                <div>
                  <p className="eyebrow">Recommendation</p>
                  <h3 className="sheet-title">{actionDetails.label}</h3>
                  <p className="helper-text">{prediction.resume}</p>
                </div>

                <span className={`decision-badge ${actionDetails.className}`}>
                  {prediction.actionRecommandee}
                </span>
              </div>

              <div className="prediction-metrics">
                <div className="summary-card">
                  <p className="summary-label">Periode de vente estimee</p>
                  <p className="summary-value">
                    {prediction.dateDebutVentePrevue} au {prediction.dateFinVentePrevue}
                  </p>
                </div>
                <div className="summary-card">
                  <p className="summary-label">Demande historique</p>
                  <p className="summary-value">{prediction.demandeHistoriqueReference}</p>
                </div>
                <div className="summary-card">
                  <p className="summary-label">Demande previsionnelle</p>
                  <p className="summary-value">{prediction.demandePrevisionnelle}</p>
                </div>
                <div className="summary-card">
                  <p className="summary-label">Stock projete disponible</p>
                  <p className="summary-value">{prediction.stockProjeteDisponible}</p>
                </div>
                <div className="summary-card">
                  <p className="summary-label">Poussins conseilles</p>
                  <p className="summary-value">{prediction.quantiteConseilleePoussins}</p>
                </div>
                <div className="summary-card">
                  <p className="summary-label">Ecart vs tendance normale</p>
                  <p className="summary-value">
                    {prediction.ecartSuggestion > 0 ? "+" : ""}
                    {prediction.ecartSuggestion}
                  </p>
                </div>
                <div className="summary-card">
                  <p className="summary-label">Hausse due aux fetes</p>
                  <p className="summary-value">
                    {formatCoefficient(prediction.coefficientHausseEvenements)}
                  </p>
                </div>
                <div className="summary-card">
                  <p className="summary-label">Creux post-fete estime</p>
                  <p className="summary-value">
                    {formatCoefficient(prediction.coefficientBaissePostFete)}
                  </p>
                </div>
                <div className="summary-card">
                  <p className="summary-label">Taux de mortalite integre</p>
                  <p className="summary-value">
                    {formatCoefficient(prediction.tauxMortaliteHistorique)}
                  </p>
                </div>
              </div>

              <div>
                <div className="section-header compact-header">
                  <div>
                    <p className="eyebrow">Facteurs d'aide a la decision</p>
                    <h3>Pourquoi cette suggestion ?</h3>
                  </div>
                </div>

                <div className="factor-list">
                  {prediction.facteurs?.map((facteur) => (
                    <article className="factor-card" key={`${facteur.categorie}-${facteur.titre}`}>
                      <div className="factor-head">
                        <div>
                          <p className="factor-category">{facteur.categorie}</p>
                          <h4 className="factor-title">{facteur.titre}</h4>
                        </div>

                        <span
                          className={`factor-variation ${getVariationClassName(
                            facteur.variationPourcentage
                          )}`}
                        >
                          {formatVariation(facteur.variationPourcentage)}
                        </span>
                      </div>

                      <p className="helper-text">{facteur.description}</p>
                    </article>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      <div className="section-stack">
        <div className="section-header compact-header">
          <div>
            <p className="eyebrow">Calendrier des fetes</p>
            <h3>Evenements pris en compte par l'IA</h3>
            <p className="helper-text">
              Enregistrez les dates et les coefficients de vos grandes periodes
              de consommation pour adapter les previsions a votre realite.
            </p>
          </div>
        </div>

        {eventMessage && (
          <p className={`alert ${eventMessageType === "error" ? "alert-error" : "alert-success"}`}>
            {eventMessage}
          </p>
        )}

        <div className="form-card prediction-card">
          <div>
            <p className="eyebrow">Modeles Senegal</p>
            <h3 className="sheet-title">Precharger un type de fete</h3>
            <p className="helper-text">
              Ces modeles servent de point de depart. Ajustez ensuite la date
              exacte et les coefficients selon votre experience terrain.
            </p>
          </div>

          <div className="template-grid">
            {EVENT_TEMPLATES.map((template) => (
              <button
                key={template.nomEvenement}
                type="button"
                className="template-chip"
                onClick={() => applyTemplate(template)}
              >
                <strong>{template.nomEvenement}</strong>
                <span>{template.description}</span>
              </button>
            ))}
          </div>
        </div>

        <form className="form-card prediction-card" onSubmit={handleEventSubmit}>
          <div className="form-grid">
            <label className="field">
              <span>Nom de l'evenement</span>
              <input
                value={eventForm.nomEvenement}
                onChange={(event) =>
                  setEventForm({ ...eventForm, nomEvenement: event.target.value })
                }
                placeholder="Exemple : Grand Magal de Touba"
                required
              />
            </label>

            <label className="field">
              <span>Type</span>
              <select
                value={eventForm.typeEvenement}
                onChange={(event) =>
                  setEventForm({ ...eventForm, typeEvenement: event.target.value })
                }
              >
                <option value="RELIGIEUX">Religieux</option>
                <option value="CIVIL">Civil</option>
                <option value="COMMERCIAL">Commercial</option>
                <option value="SAISONNIER">Saisonnier</option>
              </select>
            </label>

            <label className="field">
              <span>Date de debut</span>
              <input
                type="date"
                value={eventForm.dateDebut}
                onChange={(event) =>
                  setEventForm({ ...eventForm, dateDebut: event.target.value })
                }
                required
              />
            </label>

            <label className="field">
              <span>Date de fin</span>
              <input
                type="date"
                value={eventForm.dateFin}
                onChange={(event) =>
                  setEventForm({ ...eventForm, dateFin: event.target.value })
                }
              />
            </label>

            <label className="field">
              <span>Importance (1 a 5)</span>
              <input
                type="number"
                min="1"
                max="5"
                value={eventForm.importance}
                onChange={(event) =>
                  setEventForm({ ...eventForm, importance: event.target.value })
                }
                required
              />
            </label>

            <label className="field">
              <span>Hausse de demande (%)</span>
              <input
                type="number"
                min="0"
                max="100"
                value={eventForm.coefficientDemande}
                onChange={(event) =>
                  setEventForm({ ...eventForm, coefficientDemande: event.target.value })
                }
                required
              />
            </label>

            <label className="field">
              <span>Jours d'impact avant</span>
              <input
                type="number"
                min="0"
                max="30"
                value={eventForm.joursImpactAvant}
                onChange={(event) =>
                  setEventForm({ ...eventForm, joursImpactAvant: event.target.value })
                }
                required
              />
            </label>

            <label className="field">
              <span>Jours de creux apres</span>
              <input
                type="number"
                min="0"
                max="30"
                value={eventForm.joursImpactApres}
                onChange={(event) =>
                  setEventForm({ ...eventForm, joursImpactApres: event.target.value })
                }
                required
              />
            </label>

            <label className="field">
              <span>Baisse post-fete (%)</span>
              <input
                type="number"
                min="0"
                max="100"
                value={eventForm.coefficientBaisseApres}
                onChange={(event) =>
                  setEventForm({
                    ...eventForm,
                    coefficientBaisseApres: event.target.value
                  })
                }
                required
              />
            </label>
          </div>

          <div className="actions-row">
            <button type="submit" className="primary-button">
              Ajouter au moteur de prevision
            </button>
          </div>
        </form>

        <div className="table-card">
          <table className="data-table">
            <thead>
              <tr>
                <th>Evenement</th>
                <th>Periode</th>
                <th>Hausse</th>
                <th>Creux apres</th>
                <th>Importance</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {!eventsLoading && events.length === 0 && (
                <tr>
                  <td colSpan="6" className="empty-state">
                    Aucune fete n'est encore configuree dans le moteur de prevision.
                  </td>
                </tr>
              )}

              {eventsLoading && (
                <tr>
                  <td colSpan="6" className="empty-state">
                    Chargement des evenements...
                  </td>
                </tr>
              )}

              {events.map((event) => (
                <tr key={event.id}>
                  <td>
                    <strong>{event.nomEvenement}</strong>
                    <div className="helper-text">{event.typeEvenement || "-"}</div>
                  </td>
                  <td>
                    {event.dateDebut}
                    {event.dateFin && event.dateFin !== event.dateDebut
                      ? ` au ${event.dateFin}`
                      : ""}
                  </td>
                  <td>
                    {formatCoefficient(event.coefficientDemande)} sur {event.joursImpactAvant} j
                  </td>
                  <td>
                    {formatCoefficient(event.coefficientBaisseApres)} sur {event.joursImpactApres} j
                  </td>
                  <td>{event.importance}/5</td>
                  <td>
                    <button
                      type="button"
                      className="danger-button"
                      onClick={() => supprimerEvenement(event.id)}
                    >
                      Retirer
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </section>
  );
}

export default PrevisionPage;
