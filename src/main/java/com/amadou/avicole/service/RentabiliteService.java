package com.amadou.avicole.service;

import com.amadou.avicole.dto.RentabiliteGlobaleDto;
import com.amadou.avicole.dto.RentabiliteLotDto;
import com.amadou.avicole.entity.Commande;
import com.amadou.avicole.entity.Lot;
import com.amadou.avicole.entity.Perte;
import com.amadou.avicole.repository.CommandeRepository;
import com.amadou.avicole.repository.LotRepository;
import com.amadou.avicole.repository.PerteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RentabiliteService {

    private final LotRepository lotRepository;
    private final CommandeRepository commandeRepository;
    private final PerteRepository perteRepository;

    public RentabiliteService(
            LotRepository lotRepository,
            CommandeRepository commandeRepository,
            PerteRepository perteRepository) {
        this.lotRepository = lotRepository;
        this.commandeRepository = commandeRepository;
        this.perteRepository = perteRepository;
    }

    public List<RentabiliteLotDto> calculerRentabiliteParLot() {
        List<Commande> commandes = commandeRepository.findAll();
        List<Perte> pertes = perteRepository.findAll();

        Map<Long, List<Commande>> commandesParLot = commandes.stream()
                .filter(commande -> commande.getLot() != null)
                .collect(Collectors.groupingBy(commande -> commande.getLot().getId()));

        Map<Long, List<Perte>> pertesParLot = pertes.stream()
                .filter(perte -> perte.getLot() != null)
                .collect(Collectors.groupingBy(perte -> perte.getLot().getId()));

        return lotRepository.findAll().stream()
                .map(lot -> construireRentabiliteLot(
                        lot,
                        commandesParLot.getOrDefault(lot.getId(), List.of()),
                        pertesParLot.getOrDefault(lot.getId(), List.of())
                ))
                .toList();
    }

    public RentabiliteLotDto calculerRentabiliteLot(Long lotId) {
        Lot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lot introuvable."));

        return construireRentabiliteLot(
                lot,
                commandeRepository.findByLotId(lotId),
                perteRepository.findByLotId(lotId)
        );
    }

    public RentabiliteGlobaleDto calculerRentabiliteGlobale() {
        List<RentabiliteLotDto> rentabilites = calculerRentabiliteParLot();

        int nombreLots = rentabilites.size();
        int quantiteVendue = rentabilites.stream().mapToInt(RentabiliteLotDto::getQuantiteVendue).sum();
        int quantitePerdue = rentabilites.stream().mapToInt(RentabiliteLotDto::getQuantitePerdue).sum();
        int stockRestant = rentabilites.stream().mapToInt(RentabiliteLotDto::getQuantiteRestante).sum();
        double chiffreAffairesTotal = rentabilites.stream().mapToDouble(RentabiliteLotDto::getChiffreAffaires).sum();
        double chargesTotales = rentabilites.stream().mapToDouble(RentabiliteLotDto::getChargesTotales).sum();
        double beneficeNet = rentabilites.stream().mapToDouble(RentabiliteLotDto::getBeneficeNet).sum();
        int quantiteInitialeTotale = rentabilites.stream().mapToInt(RentabiliteLotDto::getQuantiteInitiale).sum();

        RentabiliteGlobaleDto dto = new RentabiliteGlobaleDto();
        dto.setNombreLots(nombreLots);
        dto.setQuantiteVendue(quantiteVendue);
        dto.setQuantitePerdue(quantitePerdue);
        dto.setStockRestant(stockRestant);
        dto.setChiffreAffairesTotal(chiffreAffairesTotal);
        dto.setChargesTotales(chargesTotales);
        dto.setBeneficeNet(beneficeNet);
        dto.setMargePourcentage(calculerMarge(beneficeNet, chiffreAffairesTotal));
        dto.setTauxMortaliteGlobal(calculerTaux(quantitePerdue, quantiteInitialeTotale));
        return dto;
    }

    private RentabiliteLotDto construireRentabiliteLot(
            Lot lot,
            List<Commande> commandes,
            List<Perte> pertes) {
        int quantiteVendue = commandes.stream().mapToInt(Commande::getQuantite).sum();
        int quantitePerdue = pertes.stream().mapToInt(Perte::getQuantite).sum();
        double chiffreAffaires = commandes.stream().mapToDouble(Commande::getPrixTotal).sum();
        double chargesTotales = lot.getCoutAchat() + lot.getCoutAliment() + lot.getCoutSante() + lot.getAutresCharges();
        double beneficeNet = chiffreAffaires - chargesTotales;

        RentabiliteLotDto dto = new RentabiliteLotDto();
        dto.setLotId(lot.getId());
        dto.setNomLot(lot.getNomLot());
        dto.setQuantiteInitiale(lot.getQuantiteInitiale());
        dto.setQuantiteVendue(quantiteVendue);
        dto.setQuantitePerdue(quantitePerdue);
        dto.setQuantiteRestante(lot.getQuantiteRestante());
        dto.setChiffreAffaires(chiffreAffaires);
        dto.setChargesTotales(chargesTotales);
        dto.setBeneficeNet(beneficeNet);
        dto.setMargePourcentage(calculerMarge(beneficeNet, chiffreAffaires));
        dto.setTauxMortalite(calculerTaux(quantitePerdue, lot.getQuantiteInitiale()));
        dto.setRentable(beneficeNet >= 0);
        return dto;
    }

    private double calculerMarge(double beneficeNet, double chiffreAffaires) {
        if (chiffreAffaires <= 0) {
            return 0;
        }
        return (beneficeNet / chiffreAffaires) * 100;
    }

    private double calculerTaux(int valeur, int total) {
        if (total <= 0) {
            return 0;
        }
        return (valeur * 100.0) / total;
    }
}
