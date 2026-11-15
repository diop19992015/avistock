package com.amadou.avicole.controller;

import com.amadou.avicole.entity.Evenement;
import com.amadou.avicole.service.EvenementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evenements")
public class EvenementController {

    private final EvenementService evenementService;

    public EvenementController(EvenementService evenementService) {
        this.evenementService = evenementService;
    }

    @PostMapping
    public Evenement creerEvenement(@RequestBody Evenement evenement) {
        return evenementService.creerEvenement(evenement);
    }

    @GetMapping
    public List<Evenement> getTousLesEvenements() {
        return evenementService.getTousLesEvenements();
    }

    @DeleteMapping("/{id}")
    public void supprimerEvenement(@PathVariable Long id) {
        evenementService.supprimerEvenement(id);
    }
}
