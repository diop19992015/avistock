package com.amadou.avicole.service;

import com.amadou.avicole.entity.Lot;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LotStatusService {

    public static final String STATUT_EN_ELEVAGE = "EN_ELEVAGE";
    public static final String STATUT_PRET_A_VENDRE = "PRET_A_VENDRE";
    public static final String STATUT_EPUISE = "EPUISE";

    public void synchroniserStatut(Lot lot) {
        lot.setStatutLot(determinerStatut(lot));
    }

    public String determinerStatut(Lot lot) {
        if (lot.getQuantiteRestante() <= 0) {
            return STATUT_EPUISE;
        }

        if (lot.getDateMaturitePrevue() != null
                && !lot.getDateMaturitePrevue().isAfter(LocalDate.now())) {
            return STATUT_PRET_A_VENDRE;
        }

        return STATUT_EN_ELEVAGE;
    }
}
