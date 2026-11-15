package com.amadou.avicole.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CommandeRequest {

    private LocalDate dateCommande;

    @NotNull(message = "La quantite est obligatoire.")
    @Positive(message = "La quantite doit etre strictement positive.")
    private Integer quantite;

    @NotNull(message = "Le prix total est obligatoire.")
    @PositiveOrZero(message = "Le prix total ne peut pas etre negatif.")
    private Double prixTotal;

    @Size(max = 1000, message = "La description est trop longue.")
    private String description;

    @Valid
    @NotNull(message = "Le client est obligatoire.")
    private EntityReference client;

    @Valid
    @NotNull(message = "Le lot est obligatoire.")
    private EntityReference lot;

    public static class EntityReference {

        @NotNull(message = "L'identifiant est obligatoire.")
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    public LocalDate getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public Double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(Double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EntityReference getClient() {
        return client;
    }

    public void setClient(EntityReference client) {
        this.client = client;
    }

    public EntityReference getLot() {
        return lot;
    }

    public void setLot(EntityReference lot) {
        this.lot = lot;
    }
}
