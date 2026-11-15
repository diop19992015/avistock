package com.amadou.avicole.controller;

import com.amadou.avicole.entity.Lot;
import com.amadou.avicole.service.LotService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lots")
public class LotController {

    private final LotService lotService;

    public LotController(LotService lotService) {
        this.lotService = lotService;
    }

    @PostMapping
    public Lot creerLot(@RequestBody Lot lot) {
        return lotService.creerLot(lot);
    }

    @GetMapping
    public List<Lot> getTousLesLots() {
        return lotService.getTousLesLots();
    }

    @GetMapping("/{id}")
    public Lot getLotParId(@PathVariable Long id) {
        return lotService.getLotParId(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void supprimerLot(@PathVariable Long id) {
        lotService.supprimerLot(id);
    }
}