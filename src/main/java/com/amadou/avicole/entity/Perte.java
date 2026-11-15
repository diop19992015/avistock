package com.amadou.avicole.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "pertes")
public class Perte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate datePerte;

    private int quantite;

    private String cause;

    private String description;

    @ManyToOne
    @JoinColumn(name = "lot_id")
    private Lot lot;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Lot getLot() {
        return lot;
    }

    public void setLot(Lot lot) {
        this.lot = lot;
    }
}
