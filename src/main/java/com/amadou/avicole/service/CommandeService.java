package com.amadou.avicole.service;

import com.amadou.avicole.dto.CommandeRequest;
import com.amadou.avicole.entity.Client;
import com.amadou.avicole.entity.Commande;
import com.amadou.avicole.entity.Lot;
import com.amadou.avicole.repository.ClientRepository;
import com.amadou.avicole.repository.CommandeRepository;
import com.amadou.avicole.repository.LotRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;
    private final LotRepository lotRepository;
    private final LotStatusService lotStatusService;

    public CommandeService(
            CommandeRepository commandeRepository,
            ClientRepository clientRepository,
            LotRepository lotRepository,
            LotStatusService lotStatusService) {
        this.commandeRepository = commandeRepository;
        this.clientRepository = clientRepository;
        this.lotRepository = lotRepository;
        this.lotStatusService = lotStatusService;
    }

    @Transactional
    public Commande creerCommande(CommandeRequest commandeRequest) {
        Client client = clientRepository.findById(commandeRequest.getClient().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable."));

        Lot lot = lotRepository.findById(commandeRequest.getLot().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lot introuvable."));

        if (lot.getQuantiteRestante() < commandeRequest.getQuantite()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Stock insuffisant pour ce lot. Quantite disponible : " + lot.getQuantiteRestante()
            );
        }

        Commande commande = new Commande();
        commande.setDateCommande(
                commandeRequest.getDateCommande() != null ? commandeRequest.getDateCommande() : LocalDate.now()
        );
        commande.setQuantite(commandeRequest.getQuantite());
        commande.setPrixTotal(commandeRequest.getPrixTotal());
        commande.setDescription(commandeRequest.getDescription());
        commande.setClient(client);
        commande.setLot(lot);

        lot.setQuantiteRestante(lot.getQuantiteRestante() - commande.getQuantite());
        lotStatusService.synchroniserStatut(lot);
        lotRepository.save(lot);

        return commandeRepository.save(commande);
    }

    public List<Commande> getToutesLesCommandes() {
        return commandeRepository.findAll();
    }

    public Optional<Commande> getCommandeParId(Long id) {
        return commandeRepository.findById(id);
    }

    @Transactional
    public void supprimerCommande(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande introuvable."));

        Lot lot = commande.getLot();
        if (lot != null) {
            lot.setQuantiteRestante(lot.getQuantiteRestante() + commande.getQuantite());
            lotStatusService.synchroniserStatut(lot);
            lotRepository.save(lot);
        }

        commandeRepository.delete(commande);
    }

    // 📊 Commandes entre deux dates
    public List<Commande> commandesEntre(LocalDate debut, LocalDate fin) {
        return commandeRepository.findByDateCommandeBetween(debut, fin);
    }
}
