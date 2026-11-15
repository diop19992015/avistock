package com.amadou.avicole.service;

import com.amadou.avicole.dto.RapportCommandeDto;
import com.amadou.avicole.dto.RapportLotDto;
import com.amadou.avicole.dto.RapportPerteDto;
import com.amadou.avicole.dto.RentabiliteLotDto;
import com.amadou.avicole.entity.Client;
import com.amadou.avicole.entity.Commande;
import com.amadou.avicole.entity.Lot;
import com.amadou.avicole.entity.Perte;
import com.amadou.avicole.repository.CommandeRepository;
import com.amadou.avicole.repository.LotRepository;
import com.amadou.avicole.repository.PerteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class RapportService {

    private final LotRepository lotRepository;
    private final CommandeRepository commandeRepository;
    private final PerteRepository perteRepository;
    private final RentabiliteService rentabiliteService;

    public RapportService(
            LotRepository lotRepository,
            CommandeRepository commandeRepository,
            PerteRepository perteRepository,
            RentabiliteService rentabiliteService) {
        this.lotRepository = lotRepository;
        this.commandeRepository = commandeRepository;
        this.perteRepository = perteRepository;
        this.rentabiliteService = rentabiliteService;
    }

    public RapportLotDto genererRapportLot(Long lotId) {
        Lot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lot introuvable."));

        RentabiliteLotDto rentabilite = rentabiliteService.calculerRentabiliteLot(lotId);

        List<RapportCommandeDto> commandes = commandeRepository.findByLotId(lotId).stream()
                .sorted(Comparator.comparing(Commande::getDateCommande, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::mapperCommande)
                .toList();

        List<RapportPerteDto> pertes = perteRepository.findByLotId(lotId).stream()
                .sorted(Comparator.comparing(Perte::getDatePerte, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::mapperPerte)
                .toList();

        RapportLotDto rapport = new RapportLotDto();
        rapport.setDateGeneration(LocalDate.now());
        rapport.setLotId(lot.getId());
        rapport.setNomLot(lot.getNomLot());
        rapport.setStatutLot(lot.getStatutLot());
        rapport.setDateArrivee(lot.getDateArrivee());
        rapport.setDateMaturitePrevue(lot.getDateMaturitePrevue());
        rapport.setQuantiteInitiale(rentabilite.getQuantiteInitiale());
        rapport.setQuantiteRestante(rentabilite.getQuantiteRestante());
        rapport.setQuantiteVendue(rentabilite.getQuantiteVendue());
        rapport.setQuantitePerdue(rentabilite.getQuantitePerdue());
        rapport.setChiffreAffaires(rentabilite.getChiffreAffaires());
        rapport.setChargesTotales(rentabilite.getChargesTotales());
        rapport.setBeneficeNet(rentabilite.getBeneficeNet());
        rapport.setMargePourcentage(rentabilite.getMargePourcentage());
        rapport.setTauxMortalite(rentabilite.getTauxMortalite());
        rapport.setRentable(rentabilite.isRentable());
        rapport.setCommandes(commandes);
        rapport.setPertes(pertes);
        return rapport;
    }

    private RapportCommandeDto mapperCommande(Commande commande) {
        RapportCommandeDto dto = new RapportCommandeDto();
        dto.setCommandeId(commande.getId());
        dto.setDateCommande(commande.getDateCommande());
        dto.setNomClient(construireNomClient(commande.getClient()));
        dto.setQuantite(commande.getQuantite());
        dto.setPrixTotal(commande.getPrixTotal());
        dto.setDescription(commande.getDescription());
        return dto;
    }

    private RapportPerteDto mapperPerte(Perte perte) {
        RapportPerteDto dto = new RapportPerteDto();
        dto.setPerteId(perte.getId());
        dto.setDatePerte(perte.getDatePerte());
        dto.setQuantite(perte.getQuantite());
        dto.setCause(perte.getCause());
        dto.setDescription(perte.getDescription());
        return dto;
    }

    private String construireNomClient(Client client) {
        if (client == null) {
            return "-";
        }

        String nom = client.getNom() == null ? "" : client.getNom().trim();
        String prenom = client.getPrenom() == null ? "" : client.getPrenom().trim();
        String nomComplet = (nom + " " + prenom).trim();
        return nomComplet.isBlank() ? "Client inconnu" : nomComplet;
    }
}
