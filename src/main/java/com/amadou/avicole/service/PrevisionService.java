package com.amadou.avicole.service;

import com.amadou.avicole.dto.PrevisionApprovisionnementDto;
import com.amadou.avicole.dto.PrevisionApprovisionnementRequestDto;
import com.amadou.avicole.dto.PrevisionFacteurDto;
import com.amadou.avicole.entity.Commande;
import com.amadou.avicole.entity.Evenement;
import com.amadou.avicole.entity.Lot;
import com.amadou.avicole.entity.Perte;
import com.amadou.avicole.repository.CommandeRepository;
import com.amadou.avicole.repository.EvenementRepository;
import com.amadou.avicole.repository.LotRepository;
import com.amadou.avicole.repository.PerteRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrevisionService {

    private static final int HISTORY_DAYS = 120;
    private static final int DEFAULT_ELEVAGE_DAYS = 50;
    private static final int DEFAULT_SALES_WINDOW_DAYS = 14;

    private final CommandeRepository commandeRepository;
    private final LotRepository lotRepository;
    private final PerteRepository perteRepository;
    private final EvenementRepository evenementRepository;

    public PrevisionService(
            CommandeRepository commandeRepository,
            LotRepository lotRepository,
            PerteRepository perteRepository,
            EvenementRepository evenementRepository) {
        this.commandeRepository = commandeRepository;
        this.lotRepository = lotRepository;
        this.perteRepository = perteRepository;
        this.evenementRepository = evenementRepository;
    }

    public PrevisionApprovisionnementDto calculerSuggestionApprovisionnement(
            PrevisionApprovisionnementRequestDto request) {
        LocalDate dateArriveeLot = request.getDateArriveeLot();
        int dureeElevage = request.getDureeElevageJours() != null
                ? request.getDureeElevageJours()
                : DEFAULT_ELEVAGE_DAYS;
        int fenetreVente = request.getFenetreVenteJours() != null
                ? request.getFenetreVenteJours()
                : DEFAULT_SALES_WINDOW_DAYS;

        LocalDate dateDebutVente = dateArriveeLot.plusDays(dureeElevage);
        LocalDate dateFinVente = dateDebutVente.plusDays(fenetreVente - 1L);

        int demandeHistoriqueReference = calculerDemandeHistoriqueReference(fenetreVente);
        double tauxMortalite = calculerTauxMortaliteHistorique();
        int stockProjete = calculerStockProjete(dateDebutVente, dateFinVente);

        List<PrevisionFacteurDto> facteurs = new ArrayList<>();
        double hausseEvenements = calculerHausseEvenements(dateDebutVente, dateFinVente, facteurs);
        double baissePostFete = calculerBaissePostFete(dateDebutVente, dateFinVente, facteurs);

        int demandePrevisionnelle = (int) Math.ceil(
                demandeHistoriqueReference * Math.max(0.35, 1 + hausseEvenements - baissePostFete)
        );
        int quantiteReferenceSansEffetFete = calculerBesoinEnPoussins(
                demandeHistoriqueReference,
                tauxMortalite,
                stockProjete
        );
        int quantiteConseillee = calculerBesoinEnPoussins(
                demandePrevisionnelle,
                tauxMortalite,
                stockProjete
        );
        int ecartSuggestion = quantiteConseillee - quantiteReferenceSansEffetFete;

        ajouterFacteursSysteme(stockProjete, tauxMortalite, facteurs);

        PrevisionApprovisionnementDto dto = new PrevisionApprovisionnementDto();
        dto.setDateArriveeLot(dateArriveeLot);
        dto.setDateDebutVentePrevue(dateDebutVente);
        dto.setDateFinVentePrevue(dateFinVente);
        dto.setDureeElevageJours(dureeElevage);
        dto.setFenetreVenteJours(fenetreVente);
        dto.setDemandeHistoriqueReference(demandeHistoriqueReference);
        dto.setDemandePrevisionnelle(demandePrevisionnelle);
        dto.setStockProjeteDisponible(stockProjete);
        dto.setQuantiteConseilleePoussins(quantiteConseillee);
        dto.setQuantiteReferenceSansEffetFete(quantiteReferenceSansEffetFete);
        dto.setEcartSuggestion(ecartSuggestion);
        dto.setTauxMortaliteHistorique(tauxMortalite);
        dto.setCoefficientHausseEvenements(hausseEvenements);
        dto.setCoefficientBaissePostFete(baissePostFete);
        dto.setActionRecommandee(determinerAction(quantiteConseillee, quantiteReferenceSansEffetFete, stockProjete));
        dto.setResume(construireResume(dto));
        dto.setFacteurs(facteurs);
        return dto;
    }

    private int calculerDemandeHistoriqueReference(int fenetreVente) {
        LocalDate finHistorique = LocalDate.now();
        LocalDate debutHistorique = finHistorique.minusDays(HISTORY_DAYS);
        List<Commande> commandes = commandeRepository.findByDateCommandeBetween(debutHistorique, finHistorique);

        if (!commandes.isEmpty()) {
            int quantiteVendue = commandes.stream().mapToInt(Commande::getQuantite).sum();
            double demandeQuotidienne = quantiteVendue / (double) (HISTORY_DAYS + 1);
            return Math.max((int) Math.ceil(demandeQuotidienne * fenetreVente), 20);
        }

        return (int) Math.ceil(
                lotRepository.findAll().stream()
                        .mapToInt(Lot::getQuantiteInitiale)
                        .average()
                        .orElse(80) * 0.55
        );
    }

    private double calculerTauxMortaliteHistorique() {
        List<Lot> lots = lotRepository.findAll();
        int quantiteInitiale = lots.stream().mapToInt(Lot::getQuantiteInitiale).sum();
        List<Perte> pertes = perteRepository.findAll();
        int quantitePerdue = pertes.stream().mapToInt(Perte::getQuantite).sum();

        if (quantiteInitiale <= 0) {
            return 0.05;
        }

        return Math.min(Math.max(quantitePerdue / (double) quantiteInitiale, 0), 0.25);
    }

    private int calculerStockProjete(LocalDate dateDebutVente, LocalDate dateFinVente) {
        LocalDate projectionStart = dateDebutVente.minusDays(14);

        return lotRepository.findAll().stream()
                .filter(lot -> lot.getQuantiteRestante() > 0)
                .filter(lot -> lot.getDateMaturitePrevue() != null)
                .filter(lot -> !lot.getDateMaturitePrevue().isBefore(projectionStart))
                .filter(lot -> !lot.getDateMaturitePrevue().isAfter(dateFinVente))
                .mapToInt(Lot::getQuantiteRestante)
                .sum();
    }

    private double calculerHausseEvenements(
            LocalDate dateDebutVente,
            LocalDate dateFinVente,
            List<PrevisionFacteurDto> facteurs) {
        double hausse = 0;

        for (Evenement evenement : evenementRepository.findAll(Sort.by(Sort.Direction.ASC, "dateDebut"))) {
            if (evenement.getDateDebut() == null) {
                continue;
            }

            LocalDate dateFinEvenement = evenement.getDateFin() != null
                    ? evenement.getDateFin()
                    : evenement.getDateDebut();
            LocalDate debutImpact = evenement.getDateDebut().minusDays(getJoursImpactAvant(evenement));

            if (!intersecte(dateDebutVente, dateFinVente, debutImpact, dateFinEvenement)) {
                continue;
            }

            double variation = getCoefficientDemande(evenement) * calculerTauxRecouvrement(
                    dateDebutVente,
                    dateFinVente,
                    debutImpact,
                    dateFinEvenement
            );
            hausse += variation;
            facteurs.add(facteur(
                    "FETE",
                    evenement.getNomEvenement(),
                    "Hausse attendue de la demande autour de cette fete.",
                    variation * 100
            ));
        }

        return Math.min(hausse, 1.2);
    }

    private double calculerBaissePostFete(
            LocalDate dateDebutVente,
            LocalDate dateFinVente,
            List<PrevisionFacteurDto> facteurs) {
        double baisse = 0;

        for (Evenement evenement : evenementRepository.findAll(Sort.by(Sort.Direction.ASC, "dateDebut"))) {
            if (evenement.getDateDebut() == null) {
                continue;
            }

            LocalDate dateFinEvenement = evenement.getDateFin() != null
                    ? evenement.getDateFin()
                    : evenement.getDateDebut();
            int joursImpactApres = getJoursImpactApres(evenement);

            if (joursImpactApres <= 0) {
                continue;
            }

            LocalDate debutCreux = dateFinEvenement.plusDays(1);
            LocalDate finCreux = dateFinEvenement.plusDays(joursImpactApres);

            if (!intersecte(dateDebutVente, dateFinVente, debutCreux, finCreux)) {
                continue;
            }

            double variation = getCoefficientBaisseApres(evenement) * calculerTauxRecouvrement(
                    dateDebutVente,
                    dateFinVente,
                    debutCreux,
                    finCreux
            );
            baisse += variation;
            facteurs.add(facteur(
                    "POST_FETE",
                    "Creux apres " + evenement.getNomEvenement(),
                    "Risque de baisse de consommation juste apres la fete.",
                    -variation * 100
            ));
        }

        return Math.min(baisse, 0.85);
    }

    private int calculerBesoinEnPoussins(int demande, double tauxMortalite, int stockProjete) {
        double tauxSurvie = Math.max(1 - tauxMortalite, 0.7);
        int besoinProduction = (int) Math.ceil(demande / tauxSurvie);
        return Math.max(0, besoinProduction - stockProjete);
    }

    private void ajouterFacteursSysteme(
            int stockProjete,
            double tauxMortalite,
            List<PrevisionFacteurDto> facteurs) {
        if (stockProjete > 0) {
            facteurs.add(facteur(
                    "STOCK",
                    "Stock deja projete",
                    "Une partie de la demande future peut etre couverte par les lots deja prevus.",
                    0
            ));
        }

        facteurs.add(facteur(
                "MORTALITE",
                "Marge de survie",
                "Le calcul integre les pertes historiques pour eviter une sous-commande.",
                tauxMortalite * 100
        ));
    }

    private String determinerAction(int quantiteConseillee, int quantiteReference, int stockProjete) {
        if (quantiteConseillee == 0 || stockProjete > quantiteReference) {
            return "REDUIRE";
        }

        if (quantiteConseillee >= quantiteReference + 10) {
            return "AUGMENTER";
        }

        if (quantiteConseillee <= Math.max(quantiteReference - 10, 0)) {
            return "REDUIRE";
        }

        return "MAINTENIR";
    }

    private String construireResume(PrevisionApprovisionnementDto dto) {
        return switch (dto.getActionRecommandee()) {
            case "AUGMENTER" -> "La demande previsionnelle est tiree vers le haut par les fetes proches. "
                    + "Il est conseille d'augmenter la prochaine bande a environ "
                    + dto.getQuantiteConseilleePoussins() + " poussins.";
            case "REDUIRE" -> "Le moteur detecte soit un creux post-fete, soit un stock deja suffisant. "
                    + "Il est conseille de reduire la commande pour limiter les invendus.";
            default -> "Le niveau de demande attendu reste proche de la tendance normale. "
                    + "Il est conseille de maintenir une commande proche du rythme habituel.";
        };
    }

    private boolean intersecte(LocalDate debutA, LocalDate finA, LocalDate debutB, LocalDate finB) {
        return !debutA.isAfter(finB) && !finA.isBefore(debutB);
    }

    private double calculerTauxRecouvrement(
            LocalDate debutA,
            LocalDate finA,
            LocalDate debutB,
            LocalDate finB) {
        LocalDate debut = debutA.isAfter(debutB) ? debutA : debutB;
        LocalDate fin = finA.isBefore(finB) ? finA : finB;
        long overlapDays = ChronoUnit.DAYS.between(debut, fin) + 1;
        long periodeReference = Math.max(ChronoUnit.DAYS.between(debutB, finB) + 1, 1);
        return Math.max(overlapDays / (double) periodeReference, 0.35);
    }

    private double getCoefficientDemande(Evenement evenement) {
        if (evenement.getCoefficientDemande() > 0) {
            return evenement.getCoefficientDemande();
        }
        return Math.min(0.08 * Math.max(evenement.getImportance(), 1), 0.6);
    }

    private double getCoefficientBaisseApres(Evenement evenement) {
        if (evenement.getCoefficientBaisseApres() > 0) {
            return evenement.getCoefficientBaisseApres();
        }
        return Math.min(0.04 * Math.max(evenement.getImportance(), 1), 0.35);
    }

    private int getJoursImpactAvant(Evenement evenement) {
        if (evenement.getJoursImpactAvant() > 0) {
            return evenement.getJoursImpactAvant();
        }
        return Math.max(4, evenement.getImportance() * 2);
    }

    private int getJoursImpactApres(Evenement evenement) {
        if (evenement.getJoursImpactApres() > 0) {
            return evenement.getJoursImpactApres();
        }
        return Math.max(3, evenement.getImportance() + 2);
    }

    private PrevisionFacteurDto facteur(
            String categorie,
            String titre,
            String description,
            double variationPourcentage) {
        PrevisionFacteurDto facteur = new PrevisionFacteurDto();
        facteur.setCategorie(categorie);
        facteur.setTitre(titre);
        facteur.setDescription(description);
        facteur.setVariationPourcentage(variationPourcentage);
        return facteur;
    }
}
