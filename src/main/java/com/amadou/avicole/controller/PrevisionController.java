package com.amadou.avicole.controller;

import com.amadou.avicole.dto.PrevisionApprovisionnementDto;
import com.amadou.avicole.dto.PrevisionApprovisionnementRequestDto;
import com.amadou.avicole.service.PrevisionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/previsions")
public class PrevisionController {

    private final PrevisionService previsionService;

    public PrevisionController(PrevisionService previsionService) {
        this.previsionService = previsionService;
    }

    @PostMapping("/approvisionnement")
    public PrevisionApprovisionnementDto calculerSuggestion(
            @Valid @RequestBody PrevisionApprovisionnementRequestDto request) {
        return previsionService.calculerSuggestionApprovisionnement(request);
    }
}
