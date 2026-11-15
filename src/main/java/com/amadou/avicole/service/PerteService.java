package com.amadou.avicole.service;

import com.amadou.avicole.dto.PerteRequest;
import com.amadou.avicole.entity.Lot;
import com.amadou.avicole.entity.Perte;
import com.amadou.avicole.repository.LotRepository;
import com.amadou.avicole.repository.PerteRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.time.LocalDate;

@Service
public class PerteService {

    private final PerteRepository perteRepository;
    private final LotRepository lotRepository;
    private final LotStatusService lotStatusService;

    public PerteService(
            PerteRepository perteRepository,
            LotRepository lotRepository,
            LotStatusService lotStatusService) {
        this.perteRepository = perteRepository;
        this.lotRepository = lotRepository;
        this.lotStatusService = lotStatusService;
    }

    @Transactional
    public Perte enregistrerPerte(PerteRequest perteRequest) {
        Lot lot = lotRepository.findById(perteRequest.getLot().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lot introuvable."));

        if (lot.getQuantiteRestante() < perteRequest.getQuantite()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Stock insuffisant pour enregistrer cette perte. Quantite disponible : " + lot.getQuantiteRestante()
            );
        }

        Perte perte = new Perte();
        perte.setDatePerte(perteRequest.getDatePerte() != null ? perteRequest.getDatePerte() : LocalDate.now());
        perte.setQuantite(perteRequest.getQuantite());
        perte.setCause(perteRequest.getCause());
        perte.setDescription(perteRequest.getDescription());
        perte.setLot(lot);

        lot.setQuantiteRestante(lot.getQuantiteRestante() - perte.getQuantite());
        lotStatusService.synchroniserStatut(lot);
        lotRepository.save(lot);

        return perteRepository.save(perte);
    }

    public List<Perte> getToutesLesPertes() {
        return perteRepository.findAll();
    }

    @Transactional
    public void supprimerPerte(Long id) {
        Perte perte = perteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perte introuvable."));

        Lot lot = perte.getLot();
        if (lot != null) {
            lot.setQuantiteRestante(lot.getQuantiteRestante() + perte.getQuantite());
            lotStatusService.synchroniserStatut(lot);
            lotRepository.save(lot);
        }

        perteRepository.delete(perte);
    }
}
