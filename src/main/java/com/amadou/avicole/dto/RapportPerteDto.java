package com.amadou.avicole.dto;

import java.time.LocalDate;

public class RapportPerteDto {

    private Long perteId;
    private LocalDate datePerte;
    private int quantite;
    private String cause;
    private String description;

    public Long getPerteId() {
        return perteId;
    }

    public void setPerteId(Long perteId) {
        this.perteId = perteId;
    }

    public LocalDate getDatePerte() {
        return datePerte;
    }

    public void setDatePerte(LocalDate datePerte) {
        this.datePerte = datePerte;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
