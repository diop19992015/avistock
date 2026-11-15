package com.amadou.avicole;

import com.amadou.avicole.dto.CommandeRequest;
import com.amadou.avicole.entity.Lot;
import com.amadou.avicole.repository.ClientRepository;
import com.amadou.avicole.repository.LotRepository;
import com.amadou.avicole.service.CommandeService;
import com.amadou.avicole.service.LotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class LotServiceIntegrationTest {

    @Autowired
    private LotService lotService;

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void creerLotDefinitUnStatutEnElevageAvantLaMaturite() {
        LocalDate aujourdhui = LocalDate.now();

        Lot lot = new Lot();
        lot.setNomLot("Lot Juin 2026");
        lot.setDateArrivee(aujourdhui);
        lot.setQuantiteInitiale(120);

        Lot lotCree = lotService.creerLot(lot);

        assertThat(lotCree.getQuantiteRestante()).isEqualTo(120);
        assertThat(lotCree.getDateMaturitePrevue()).isEqualTo(aujourdhui.plusDays(60));
        assertThat(lotCree.getStatutLot()).isEqualTo("EN_ELEVAGE");
    }

    @Test
    void creerLotDefinitUnStatutPretAVendreQuandLaMaturiteEstAtteinte() {
        LocalDate dateArrivee = LocalDate.now().minusDays(70);

        Lot lot = new Lot();
        lot.setNomLot("Lot Fevrier 2026");
        lot.setDateArrivee(dateArrivee);
        lot.setQuantiteInitiale(50);

        Lot lotCree = lotService.creerLot(lot);

        assertThat(lotCree.getDateMaturitePrevue()).isEqualTo(dateArrivee.plusDays(60));
        assertThat(lotCree.getStatutLot()).isEqualTo("PRET_A_VENDRE");
    }

    @Test
    void getLotParIdRafraichitLeStatutQuandLaMaturiteEstDepassee() {
        LocalDate dateArrivee = LocalDate.now().minusDays(70);

        Lot lot = new Lot();
        lot.setNomLot("Lot A Rafraichir");
        lot.setDateArrivee(dateArrivee);
        lot.setQuantiteInitiale(40);
        lot.setQuantiteRestante(40);
        lot.setDateMaturitePrevue(dateArrivee.plusDays(60));
        lot.setStatutLot("EN_ELEVAGE");
        lot = lotRepository.save(lot);

        Lot lotCharge = lotService.getLotParId(lot.getId()).orElseThrow();

        assertThat(lotCharge.getStatutLot()).isEqualTo("PRET_A_VENDRE");
        assertThat(lotRepository.findById(lot.getId()).orElseThrow().getStatutLot())
                .isEqualTo("PRET_A_VENDRE");
    }

    @Test
    void supprimerLotRefuseQuandDesCommandesOuPertesYFontReference() {
        LocalDate dateArrivee = LocalDate.now().minusDays(70);

        Lot lot = new Lot();
        lot.setNomLot("Lot Non Supprimable");
        lot.setDateArrivee(dateArrivee);
        lot.setQuantiteInitiale(50);
        lot = lotService.creerLot(lot);

        com.amadou.avicole.entity.Client client = new com.amadou.avicole.entity.Client();
        client.setNom("Ndiaye");
        client.setPrenom("Fatou");
        client = clientRepository.save(client);

        CommandeRequest commande = new CommandeRequest();
        commande.setDateCommande(LocalDate.now());
        commande.setQuantite(10);
        commande.setPrixTotal(50000.0);
        commande.setDescription("Commande de blocage");
        commande.setClient(reference(client.getId()));
        commande.setLot(reference(lot.getId()));
        commandeService.creerCommande(commande);

        Long lotId = lot.getId();

        assertThatThrownBy(() -> lotService.supprimerLot(lotId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> {
                    ResponseStatusException exception = (ResponseStatusException) error;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(exception.getReason()).contains("Impossible de supprimer le lot");
                    assertThat(exception.getReason()).contains("commande(s)");
                });

        assertThat(lotRepository.findById(lotId)).isPresent();
    }

    private CommandeRequest.EntityReference reference(Long id) {
        CommandeRequest.EntityReference reference = new CommandeRequest.EntityReference();
        reference.setId(id);
        return reference;
    }
}
