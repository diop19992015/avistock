package com.amadou.avicole;

import com.amadou.avicole.dto.PrevisionApprovisionnementDto;
import com.amadou.avicole.dto.PrevisionApprovisionnementRequestDto;
import com.amadou.avicole.entity.Client;
import com.amadou.avicole.entity.Commande;
import com.amadou.avicole.entity.Evenement;
import com.amadou.avicole.entity.Lot;
import com.amadou.avicole.repository.ClientRepository;
import com.amadou.avicole.repository.CommandeRepository;
import com.amadou.avicole.repository.EvenementRepository;
import com.amadou.avicole.repository.LotRepository;
import com.amadou.avicole.service.PrevisionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PrevisionServiceIntegrationTest {

    @Autowired
    private PrevisionService previsionService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private EvenementRepository evenementRepository;

    @Test
    void recommandeDAugmenterAvantUneGrandeFete() {
        LocalDate dateArriveeLot = LocalDate.now().plusDays(20);
        LocalDate dateDebutVente = dateArriveeLot.plusDays(40);

        Client client = enregistrerClient("Ndiaye", "Mame");
        Lot lotHistorique = enregistrerLot(
                "Lot Historique Magal",
                LocalDate.now().minusDays(150),
                700,
                0,
                LocalDate.now().minusDays(90)
        );

        enregistrerCommande(client, lotHistorique, LocalDate.now().minusDays(110), 130, 390000.0);
        enregistrerCommande(client, lotHistorique, LocalDate.now().minusDays(80), 140, 420000.0);
        enregistrerCommande(client, lotHistorique, LocalDate.now().minusDays(55), 125, 375000.0);
        enregistrerCommande(client, lotHistorique, LocalDate.now().minusDays(25), 135, 405000.0);

        evenementRepository.save(construireEvenement(
                "Grand Magal de Touba",
                "RELIGIEUX",
                dateDebutVente.plusDays(10),
                dateDebutVente.plusDays(10),
                5,
                0.45,
                10,
                8,
                0.18
        ));

        PrevisionApprovisionnementDto prevision = previsionService.calculerSuggestionApprovisionnement(
                construireRequest(dateArriveeLot, 40, 14)
        );

        assertThat(prevision.getActionRecommandee()).isEqualTo("AUGMENTER");
        assertThat(prevision.getDemandePrevisionnelle())
                .isGreaterThan(prevision.getDemandeHistoriqueReference());
        assertThat(prevision.getQuantiteConseilleePoussins())
                .isGreaterThan(prevision.getQuantiteReferenceSansEffetFete());
        assertThat(prevision.getFacteurs())
                .extracting("categorie")
                .contains("FETE");
    }

    @Test
    void recommandeDeReduireQuandUnCreuxPostFeteEtUnStockProjeteSeCumulent() {
        LocalDate dateArriveeLot = LocalDate.now().plusDays(18);
        LocalDate dateDebutVente = dateArriveeLot.plusDays(42);

        Client client = enregistrerClient("Sarr", "Aicha");
        Lot lotHistorique = enregistrerLot(
                "Lot Historique Korite",
                LocalDate.now().minusDays(140),
                600,
                0,
                LocalDate.now().minusDays(85)
        );

        enregistrerCommande(client, lotHistorique, LocalDate.now().minusDays(100), 120, 360000.0);
        enregistrerCommande(client, lotHistorique, LocalDate.now().minusDays(70), 130, 390000.0);
        enregistrerCommande(client, lotHistorique, LocalDate.now().minusDays(40), 125, 375000.0);
        enregistrerCommande(client, lotHistorique, LocalDate.now().minusDays(12), 120, 360000.0);

        enregistrerLot(
                "Lot deja disponible",
                LocalDate.now().plusDays(5),
                120,
                90,
                dateDebutVente.plusDays(2)
        );

        evenementRepository.save(construireEvenement(
                "Korite",
                "RELIGIEUX",
                dateDebutVente.minusDays(1),
                dateDebutVente.minusDays(1),
                5,
                0.40,
                8,
                10,
                0.30
        ));

        PrevisionApprovisionnementDto prevision = previsionService.calculerSuggestionApprovisionnement(
                construireRequest(dateArriveeLot, 42, 14)
        );

        assertThat(prevision.getActionRecommandee()).isEqualTo("REDUIRE");
        assertThat(prevision.getStockProjeteDisponible()).isEqualTo(90);
        assertThat(prevision.getDemandePrevisionnelle())
                .isLessThan(prevision.getDemandeHistoriqueReference());
        assertThat(prevision.getQuantiteConseilleePoussins()).isZero();
        assertThat(prevision.getFacteurs())
                .extracting("categorie")
                .contains("POST_FETE", "STOCK");
    }

    private Client enregistrerClient(String nom, String prenom) {
        Client client = new Client();
        client.setNom(nom);
        client.setPrenom(prenom);
        return clientRepository.save(client);
    }

    private Lot enregistrerLot(
            String nomLot,
            LocalDate dateArrivee,
            int quantiteInitiale,
            int quantiteRestante,
            LocalDate dateMaturite) {
        Lot lot = new Lot();
        lot.setNomLot(nomLot);
        lot.setDateArrivee(dateArrivee);
        lot.setQuantiteInitiale(quantiteInitiale);
        lot.setQuantiteRestante(quantiteRestante);
        lot.setDateMaturitePrevue(dateMaturite);
        lot.setStatutLot(dateMaturite.isAfter(LocalDate.now()) ? "EN_ELEVAGE" : "PRET_A_VENDRE");
        return lotRepository.save(lot);
    }

    private void enregistrerCommande(
            Client client,
            Lot lot,
            LocalDate dateCommande,
            int quantite,
            double prixTotal) {
        Commande commande = new Commande();
        commande.setClient(client);
        commande.setLot(lot);
        commande.setDateCommande(dateCommande);
        commande.setQuantite(quantite);
        commande.setPrixTotal(prixTotal);
        commande.setDescription("Historique de vente");
        commandeRepository.save(commande);
    }

    private Evenement construireEvenement(
            String nom,
            String type,
            LocalDate dateDebut,
            LocalDate dateFin,
            int importance,
            double coefficientDemande,
            int joursImpactAvant,
            int joursImpactApres,
            double coefficientBaisseApres) {
        Evenement evenement = new Evenement();
        evenement.setNomEvenement(nom);
        evenement.setTypeEvenement(type);
        evenement.setDateDebut(dateDebut);
        evenement.setDateFin(dateFin);
        evenement.setImportance(importance);
        evenement.setCoefficientDemande(coefficientDemande);
        evenement.setJoursImpactAvant(joursImpactAvant);
        evenement.setJoursImpactApres(joursImpactApres);
        evenement.setCoefficientBaisseApres(coefficientBaisseApres);
        return evenement;
    }

    private PrevisionApprovisionnementRequestDto construireRequest(
            LocalDate dateArriveeLot,
            int dureeElevage,
            int fenetreVente) {
        PrevisionApprovisionnementRequestDto request = new PrevisionApprovisionnementRequestDto();
        request.setDateArriveeLot(dateArriveeLot);
        request.setDureeElevageJours(dureeElevage);
        request.setFenetreVenteJours(fenetreVente);
        return request;
    }
}
