package com.amadou.avicole.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class PrevisionApprovisionnementRequestDto {

    @NotNull(message = "La date d'arrivee du lot est obligatoire.")
    private LocalDate dateArriveeLot;

    @Min(value = 35, message = "La duree d'elevage doit etre d'au moins 35 jours.")
    @Max(value = 75, message = "La duree d'elevage doit rester inferieure ou egale a 75 jours.")
    private Integer dureeElevageJours;

    @Min(value = 7, message = "La fenetre de vente doit etre d'au moins 7 jours.")
    @Max(value = 30, message = "La fenetre de vente doit rester inferieure ou egale a 30 jours.")
    private Integer fenetreVenteJours;

    public LocalDate getDateArriveeLot() {
        return dateArriveeLot;
    }

    public void setDateArriveeLot(LocalDate dateArriveeLot) {
        this.dateArriveeLot = dateArriveeLot;
    }

    public Integer getDureeElevageJours() {
        return dureeElevageJours;
    }

    public void setDureeElevageJours(Integer dureeElevageJours) {
        this.dureeElevageJours = dureeElevageJours;
    }

    public Integer getFenetreVenteJours() {
        return fenetreVenteJours;
    }

    public void setFenetreVenteJours(Integer fenetreVenteJours) {
        this.fenetreVenteJours = fenetreVenteJours;
    }
}
