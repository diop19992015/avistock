package com.amadou.avicole.controller;

import com.amadou.avicole.dto.PerteRequest;
import com.amadou.avicole.entity.Perte;
import com.amadou.avicole.service.PerteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pertes")
public class PerteController {

    private final PerteService perteService;

    public PerteController(PerteService perteService) {
        this.perteService = perteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Perte enregistrerPerte(@Valid @RequestBody PerteRequest perteRequest) {
        return perteService.enregistrerPerte(perteRequest);
    }

    @GetMapping
    public List<Perte> getToutesLesPertes() {
        return perteService.getToutesLesPertes();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerPerte(@PathVariable Long id) {
        perteService.supprimerPerte(id);
    }
}
