package com.amadou.avicole.controller;

import com.amadou.avicole.dto.CommandeRequest;
import com.amadou.avicole.entity.Commande;
import com.amadou.avicole.service.CommandeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    // ⭐ Ajouter une commande
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Commande creerCommande(@Valid @RequestBody CommandeRequest commandeRequest) {
        return commandeService.creerCommande(commandeRequest);
    }

    // 📋 Liste des commandes
    @GetMapping
    public List<Commande> getToutesLesCommandes() {
        return commandeService.getToutesLesCommandes();
    }

    // 🔎 Par ID
    @GetMapping("/{id}")
    public Commande getCommandeParId(@PathVariable Long id) {
        return commandeService.getCommandeParId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande introuvable."));
    }

    // ❌ Supprimer
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerCommande(@PathVariable Long id) {
        commandeService.supprimerCommande(id);
    }

    // 📊 Commandes entre deux dates
    @GetMapping("/periode")
    public List<Commande> commandesEntre(
            @RequestParam String debut,
            @RequestParam String fin) {

        return commandeService.commandesEntre(
                LocalDate.parse(debut),
                LocalDate.parse(fin));
    }
}
