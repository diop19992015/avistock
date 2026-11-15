package com.amadou.avicole.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class PerteRequest {

    private LocalDate datePerte;

    @NotNull(message = "La quantite de perte est obligatoire.")
    @Positive(message = "La quantite de perte doit etre strictement positive.")
    private Integer quantite;

    private String cause;
    private String description;

    @Valid
    @NotNull(message = "Le lot est obligatoire.")
    private EntityReference lot;

    public static class EntityReference {

        @NotNull(message = "L'identifiant du lot est obligatoire.")
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    public LocalDate getDatePerte() {
        return datePerte;
    }

    public void setDatePerte(LocalDate datePerte) {
        this.datePerte = datePerte;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
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

    public EntityReference getLot() {
        return lot;
    }

    public void setLot(EntityReference lot) {
        this.lot = lot;
    }
}
