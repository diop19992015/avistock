package com.amadou.avicole.controller;

import com.amadou.avicole.dto.RapportLotDto;
import com.amadou.avicole.service.RapportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rapports")
public class RapportController {

    private final RapportService rapportService;

    public RapportController(RapportService rapportService) {
        this.rapportService = rapportService;
    }

    @GetMapping("/lots/{lotId}")
    public RapportLotDto getRapportLot(@PathVariable Long lotId) {
        return rapportService.genererRapportLot(lotId);
    }
}
