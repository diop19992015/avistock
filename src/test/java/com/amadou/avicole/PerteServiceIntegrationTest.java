package com.amadou.avicole;

import com.amadou.avicole.dto.PerteRequest;
import com.amadou.avicole.entity.Lot;
import com.amadou.avicole.repository.LotRepository;
import com.amadou.avicole.repository.PerteRepository;
import com.amadou.avicole.service.PerteService;
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
class PerteServiceIntegrationTest {

    @Autowired
    private PerteService perteService;

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private PerteRepository perteRepository;

    @Test
    void enregistrerPerteDiminueLeStockDuLot() {
        LocalDate dateArrivee = LocalDate.now().minusDays(70);

        Lot lot = new Lot();
        lot.setNomLot("Lot Pertes");
        lot.setDateArrivee(dateArrivee);
        lot.setQuantiteInitiale(6);
        lot.setQuantiteRestante(6);
        lot.setDateMaturitePrevue(dateArrivee.plusDays(60));
        lot.setStatutLot("PRET_A_VENDRE");
        lot = lotRepository.save(lot);

        PerteRequest request = new PerteRequest();
        request.setDatePerte(LocalDate.now());
        request.setQuantite(6);
        request.setCause("Mortalite");
        request.setDescription("Perte test");
        request.setLot(reference(lot.getId()));

        perteService.enregistrerPerte(request);

        Lot lotMisAJour = lotRepository.findById(lot.getId()).orElseThrow();
        assertThat(lotMisAJour.getQuantiteRestante()).isEqualTo(0);
        assertThat(lotMisAJour.getStatutLot()).isEqualTo("EPUISE");
    }

    @Test
    void enregistrerPerteRefuseQuandLeStockEstInsuffisant() {
        Lot lot = new Lot();
        lot.setNomLot("Lot Pertes Insuffisant");
        lot.setDateArrivee(LocalDate.now());
        lot.setQuantiteInitiale(10);
        lot.setQuantiteRestante(10);
        lot.setDateMaturitePrevue(LocalDate.now().plusDays(60));
        lot.setStatutLot("EN_ELEVAGE");
        lot = lotRepository.save(lot);

        PerteRequest request = new PerteRequest();
        request.setDatePerte(LocalDate.now());
        request.setQuantite(12);
        request.setCause("Mortalite");
        request.setLot(reference(lot.getId()));

        assertThatThrownBy(() -> perteService.enregistrerPerte(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> {
                    ResponseStatusException exception = (ResponseStatusException) error;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(exception.getReason()).contains("Stock insuffisant");
                });
    }

    @Test
    void supprimerPerteRecrediteLeStockDuLot() {
        LocalDate dateArrivee = LocalDate.now().minusDays(70);

        Lot lot = new Lot();
        lot.setNomLot("Lot Suppression Perte");
        lot.setDateArrivee(dateArrivee);
        lot.setQuantiteInitiale(20);
        lot.setQuantiteRestante(20);
        lot.setDateMaturitePrevue(dateArrivee.plusDays(60));
        lot.setStatutLot("PRET_A_VENDRE");
        lot = lotRepository.save(lot);

        PerteRequest request = new PerteRequest();
        request.setDatePerte(LocalDate.now());
        request.setQuantite(4);
        request.setCause("Accident");
        request.setDescription("Perte a annuler");
        request.setLot(reference(lot.getId()));

        Long perteId = perteService.enregistrerPerte(request).getId();

        perteService.supprimerPerte(perteId);

        Lot lotMisAJour = lotRepository.findById(lot.getId()).orElseThrow();
        assertThat(lotMisAJour.getQuantiteRestante()).isEqualTo(20);
        assertThat(lotMisAJour.getStatutLot()).isEqualTo("PRET_A_VENDRE");
        assertThat(perteRepository.findById(perteId)).isEmpty();
    }

    private PerteRequest.EntityReference reference(Long id) {
        PerteRequest.EntityReference reference = new PerteRequest.EntityReference();
        reference.setId(id);
        return reference;
    }
}
