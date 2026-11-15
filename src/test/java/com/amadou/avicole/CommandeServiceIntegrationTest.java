package com.amadou.avicole;

import com.amadou.avicole.dto.CommandeRequest;
import com.amadou.avicole.entity.Client;
import com.amadou.avicole.entity.Commande;
import com.amadou.avicole.entity.Lot;
import com.amadou.avicole.repository.ClientRepository;
import com.amadou.avicole.repository.LotRepository;
import com.amadou.avicole.service.CommandeService;
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
class CommandeServiceIntegrationTest {

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LotRepository lotRepository;

    @Test
    void creerCommandeDiminueLeStockDuLot() {
        LocalDate dateArrivee = LocalDate.now().minusDays(70);

        Client client = new Client();
        client.setNom("Diallo");
        client.setPrenom("Awa");

        Lot lot = new Lot();
        lot.setNomLot("Lot Avril 2026");
        lot.setDateArrivee(dateArrivee);
        lot.setQuantiteInitiale(100);
        lot.setQuantiteRestante(100);
        lot.setDateMaturitePrevue(dateArrivee.plusDays(60));
        lot.setStatutLot("EN_ELEVAGE");

        CommandeRequest request = new CommandeRequest();
        request.setDateCommande(LocalDate.now());
        request.setQuantite(30);
        request.setPrixTotal(120000.0);
        request.setDescription("Vente test");
        request.setClient(reference(saveClient(client).getId()));
        request.setLot(reference(saveLot(lot).getId()));

        Commande commande = commandeService.creerCommande(request);
        Lot lotMisAJour = lotRepository.findById(commande.getLot().getId()).orElseThrow();

        assertThat(commande.getId()).isNotNull();
        assertThat(lotMisAJour.getQuantiteRestante()).isEqualTo(70);
        assertThat(lotMisAJour.getStatutLot()).isEqualTo("PRET_A_VENDRE");
    }

    @Test
    void creerCommandeRefuseQuandLeStockEstInsuffisant() {
        Client client = new Client();
        client.setNom("Sow");
        client.setPrenom("Mariam");

        Lot lot = new Lot();
        lot.setNomLot("Lot Mai 2026");
        lot.setDateArrivee(LocalDate.now());
        lot.setQuantiteInitiale(20);
        lot.setQuantiteRestante(20);
        lot.setDateMaturitePrevue(LocalDate.now().plusDays(60));
        lot.setStatutLot("EN_ELEVAGE");

        CommandeRequest request = new CommandeRequest();
        request.setDateCommande(LocalDate.now());
        request.setQuantite(25);
        request.setPrixTotal(90000.0);
        request.setDescription("Vente impossible");
        request.setClient(reference(saveClient(client).getId()));
        request.setLot(reference(saveLot(lot).getId()));

        assertThatThrownBy(() -> commandeService.creerCommande(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> {
                    ResponseStatusException exception = (ResponseStatusException) error;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(exception.getReason()).contains("Stock insuffisant");
                });
    }

    private Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    private Lot saveLot(Lot lot) {
        return lotRepository.save(lot);
    }

    private CommandeRequest.EntityReference reference(Long id) {
        CommandeRequest.EntityReference reference = new CommandeRequest.EntityReference();
        reference.setId(id);
        return reference;
    }
}
