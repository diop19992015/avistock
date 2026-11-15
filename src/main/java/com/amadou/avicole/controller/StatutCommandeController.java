package com.amadou.avicole.controller;

import com.amadou.avicole.entity.StatutCommande;
import com.amadou.avicole.service.StatutCommandeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuts")
public class StatutCommandeController {

    private final StatutCommandeService statutService;

    public StatutCommandeController(StatutCommandeService statutService) {
        this.statutService = statutService;
    }

    @PostMapping
    public StatutCommande creerStatut(@RequestBody StatutCommande statut) {
        return statutService.creerStatut(statut);
    }

    @GetMapping
    public List<StatutCommande> getTousLesStatuts() {
        return statutService.getTousLesStatuts();
    }
}