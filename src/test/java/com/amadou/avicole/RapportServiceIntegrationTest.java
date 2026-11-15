package com.amadou.avicole;

import com.amadou.avicole.dto.CommandeRequest;
import com.amadou.avicole.dto.PerteRequest;
import com.amadou.avicole.dto.RapportLotDto;
import com.amadou.avicole.entity.Client;
import com.amadou.avicole.entity.Lot;
import com.amadou.avicole.repository.ClientRepository;
import com.amadou.avicole.service.CommandeService;
import com.amadou.avicole.service.LotService;
import com.amadou.avicole.service.PerteService;
import com.amadou.avicole.service.RapportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RapportServiceIntegrationTest {

    @Autowired
    private RapportService rapportService;

    @Autowired
    private LotService lotService;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private PerteService perteService;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void genererRapportLotAgregeVentesPertesEtRentabilite() {
        Client client = new Client();
        client.setNom("Ba");
        client.setPrenom("Aissatou");
        client = clientRepository.save(client);

        Lot lot = new Lot();
        lot.setNomLot("Lot Rapport");
        lot.setDateArrivee(LocalDate.now().minusDays(75));
        lot.setQuantiteInitiale(100);
        lot.setCoutAchat(60000);
        lot.setCoutAliment(25000);
        lot.setCoutSante(5000);
        lot.setAutresCharges(10000);
        lot = lotService.creerLot(lot);

        CommandeRequest commande = new CommandeRequest();
        commande.setDateCommande(LocalDate.now().minusDays(2));
        commande.setQuantite(30);
        commande.setPrixTotal(150000.0);
        commande.setDescription("Vente du marche local");
        commande.setClient(referenceClient(client.getId()));
        commande.setLot(referenceLot(lot.getId()));
        commandeService.creerCommande(commande);

        PerteRequest perte = new PerteRequest();
        perte.setDatePerte(LocalDate.now().minusDays(1));
        perte.setQuantite(5);
        perte.setCause("Mortalite");
        perte.setDescription("Perte sanitaire");
        perte.setLot(referencePerteLot(lot.getId()));
        perteService.enregistrerPerte(perte);

        RapportLotDto rapport = rapportService.genererRapportLot(lot.getId());

        assertThat(rapport.getLotId()).isEqualTo(lot.getId());
        assertThat(rapport.getCommandes()).hasSize(1);
        assertThat(rapport.getPertes()).hasSize(1);
        assertThat(rapport.getQuantiteVendue()).isEqualTo(30);
        assertThat(rapport.getQuantitePerdue()).isEqualTo(5);
        assertThat(rapport.getQuantiteRestante()).isEqualTo(65);
        assertThat(rapport.getChiffreAffaires()).isEqualTo(150000.0);
        assertThat(rapport.getChargesTotales()).isEqualTo(100000.0);
        assertThat(rapport.getBeneficeNet()).isEqualTo(50000.0);
        assertThat(rapport.getCommandes().get(0).getNomClient()).isEqualTo("Ba Aissatou");
    }

    private CommandeRequest.EntityReference referenceClient(Long id) {
        CommandeRequest.EntityReference reference = new CommandeRequest.EntityReference();
        reference.setId(id);
        return reference;
    }

    private CommandeRequest.EntityReference referenceLot(Long id) {
        CommandeRequest.EntityReference reference = new CommandeRequest.EntityReference();
        reference.setId(id);
        return reference;
    }

    private PerteRequest.EntityReference referencePerteLot(Long id) {
        PerteRequest.EntityReference reference = new PerteRequest.EntityReference();
        reference.setId(id);
        return reference;
    }
}
