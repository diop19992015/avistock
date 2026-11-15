package com.amadou.avicole.service;

import com.amadou.avicole.entity.Lot;
import com.amadou.avicole.repository.CommandeRepository;
import com.amadou.avicole.repository.LotRepository;
import com.amadou.avicole.repository.PerteRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LotService {

    private final LotRepository lotRepository;
    private final CommandeRepository commandeRepository;
    private final PerteRepository perteRepository;
    private final LotStatusService lotStatusService;

    public LotService(
            LotRepository lotRepository,
            CommandeRepository commandeRepository,
            PerteRepository perteRepository,
            LotStatusService lotStatusService) {
        this.lotRepository = lotRepository;
        this.commandeRepository = commandeRepository;
        this.perteRepository = perteRepository;
        this.lotStatusService = lotStatusService;
    }

    // 🔹 Créer un nouveau lot
    public Lot creerLot(Lot lot) {

        // Quantité restante = quantité initiale au départ
        lot.setQuantiteRestante(lot.getQuantiteInitiale());
        normaliserCharges(lot);

        // Calcul automatique de la maturité max (60 jours)
        if (lot.getDateArrivee() != null) {
            lot.setDateMaturitePrevue(
                    lot.getDateArrivee().plusDays(60)
            );
        }

        lotStatusService.synchroniserStatut(lot);

        return lotRepository.save(lot);
    }

    // 🔹 Récupérer tous les lots
    public List<Lot> getTousLesLots() {
        List<Lot> lots = lotRepository.findAll();
        lots.forEach(this::synchroniserEtSauvegarderLeStatutSiNecessaire);
        return lots;
    }

    // 🔹 Récupérer un lot par ID
    public Optional<Lot> getLotParId(Long id) {
        Optional<Lot> lot = lotRepository.findById(id);
        lot.ifPresent(this::synchroniserEtSauvegarderLeStatutSiNecessaire);
        return lot;
    }

    // 🔹 Mettre à jour un lot
    public Lot mettreAJourLot(Lot lot) {
        normaliserCharges(lot);
        if (lot.getDateArrivee() != null) {
            lot.setDateMaturitePrevue(lot.getDateArrivee().plusDays(60));
        }
        lotStatusService.synchroniserStatut(lot);
        return lotRepository.save(lot);
    }

    // 🔹 Supprimer un lot
    public void supprimerLot(Long id) {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lot introuvable."));

        long nombreCommandes = commandeRepository.countByLotId(id);
        long nombrePertes = perteRepository.countByLotId(id);

        if (nombreCommandes > 0 || nombrePertes > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Impossible de supprimer le lot \"" + lot.getNomLot()
                            + "\" car il est lie a "
                            + nombreCommandes + " commande(s) et "
                            + nombrePertes + " perte(s). Supprimez d'abord les donnees associees."
            );
        }

        lotRepository.deleteById(id);
    }

    // 🔹 Nombre total de poulets disponibles
    public int calculerTotalDisponible() {
        return lotRepository.findAll()
                .stream()
                .mapToInt(Lot::getQuantiteRestante)
                .sum();
    }

    // 🔹 Nombre de jours depuis l'arrivée
    public long joursDepuisArrivee(Lot lot) {
        if (lot.getDateArrivee() == null) return 0;
        return LocalDate.now().toEpochDay()
                - lot.getDateArrivee().toEpochDay();
    }

    private void normaliserCharges(Lot lot) {
        lot.setCoutAchat(Math.max(lot.getCoutAchat(), 0));
        lot.setCoutAliment(Math.max(lot.getCoutAliment(), 0));
        lot.setCoutSante(Math.max(lot.getCoutSante(), 0));
        lot.setAutresCharges(Math.max(lot.getAutresCharges(), 0));
    }

    private void synchroniserEtSauvegarderLeStatutSiNecessaire(Lot lot) {
        String statutAvant = lot.getStatutLot();
        lotStatusService.synchroniserStatut(lot);

        if (!Objects.equals(statutAvant, lot.getStatutLot())) {
            lotRepository.save(lot);
        }
    }
}
