package com.amadou.avicole.controller;

import com.amadou.avicole.dto.RentabiliteGlobaleDto;
import com.amadou.avicole.dto.RentabiliteLotDto;
import com.amadou.avicole.service.RentabiliteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rentabilite")
public class RentabiliteController {

    private final RentabiliteService rentabiliteService;

    public RentabiliteController(RentabiliteService rentabiliteService) {
        this.rentabiliteService = rentabiliteService;
    }

    @GetMapping("/lots")
    public List<RentabiliteLotDto> getRentabiliteParLot() {
        return rentabiliteService.calculerRentabiliteParLot();
    }

    @GetMapping("/lots/{lotId}")
    public RentabiliteLotDto getRentabiliteLot(@PathVariable Long lotId) {
        return rentabiliteService.calculerRentabiliteLot(lotId);
    }

    @GetMapping("/global")
    public RentabiliteGlobaleDto getRentabiliteGlobale() {
        return rentabiliteService.calculerRentabiliteGlobale();
    }
}
