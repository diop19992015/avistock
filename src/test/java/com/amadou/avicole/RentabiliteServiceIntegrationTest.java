package com.amadou.avicole;

import com.amadou.avicole.dto.CommandeRequest;
import com.amadou.avicole.dto.RentabiliteLotDto;
import com.amadou.avicole.entity.Client;
import com.amadou.avicole.entity.Lot;
import com.amadou.avicole.entity.Perte;
import com.amadou.avicole.repository.ClientRepository;
import com.amadou.avicole.repository.LotRepository;
import com.amadou.avicole.repository.PerteRepository;
import com.amadou.avicole.service.CommandeService;
import com.amadou.avicole.service.RentabiliteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RentabiliteServiceIntegrationTest {

    @Autowired
    private RentabiliteService rentabiliteService;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private PerteRepository perteRepository;

    @Test
    void calculeLaRentabiliteDUnLotAvecVentesEtPertes() {
        Client client = new Client();
        client.setNom("Ndiaye");
        client.setPrenom("Fatou");
        client = clientRepository.save(client);

        Lot lot = new Lot();
        lot.setNomLot("Lot Rentabilite");
        lot.setDateArrivee(LocalDate.now().minusDays(40));
        lot.setQuantiteInitiale(100);
        lot.setQuantiteRestante(100);
        lot.setDateMaturitePrevue(LocalDate.now().plusDays(20));
        lot.setStatutLot("EN_ELEVAGE");
        lot.setCoutAchat(120000);
        lot.setCoutAliment(90000);
        lot.setCoutSante(20000);
        lot.setAutresCharges(10000);
        lot = lotRepository.save(lot);

        CommandeRequest request = new CommandeRequest();
        request.setDateCommande(LocalDate.now());
        request.setQuantite(70);
        request.setPrixTotal(320000.0);
        request.setDescription("Vente principale");
        request.setClient(reference(client.getId()));
        request.setLot(reference(lot.getId()));
        commandeService.creerCommande(request);

        Perte perte = new Perte();
        perte.setDatePerte(LocalDate.now());
        perte.setQuantite(5);
        perte.setCause("Mortalite naturelle");
        perte.setDescription("Pertes sur le lot");
        perte.setLot(lot);
        perteRepository.save(perte);

        RentabiliteLotDto rentabilite = rentabiliteService.calculerRentabiliteLot(lot.getId());

        assertThat(rentabilite.getChiffreAffaires()).isEqualTo(320000.0);
        assertThat(rentabilite.getChargesTotales()).isEqualTo(240000.0);
        assertThat(rentabilite.getBeneficeNet()).isEqualTo(80000.0);
        assertThat(rentabilite.getQuantiteVendue()).isEqualTo(70);
        assertThat(rentabilite.getQuantitePerdue()).isEqualTo(5);
        assertThat(rentabilite.getTauxMortalite()).isEqualTo(5.0);
        assertThat(rentabilite.isRentable()).isTrue();
    }

    private CommandeRequest.EntityReference reference(Long id) {
        CommandeRequest.EntityReference reference = new CommandeRequest.EntityReference();
        reference.setId(id);
        return reference;
    }
}
