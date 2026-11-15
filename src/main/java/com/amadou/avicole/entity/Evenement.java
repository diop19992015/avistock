package com.amadou.avicole.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "evenements")
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomEvenement;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private String typeEvenement;

    private int importance;
    private double coefficientDemande;
    private int joursImpactAvant;
    private int joursImpactApres;
    private double coefficientBaisseApres;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomEvenement() {
        return nomEvenement;
    }

    public void setNomEvenement(String nomEvenement) {
        this.nomEvenement = nomEvenement;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getTypeEvenement() {
        return typeEvenement;
    }

    public void setTypeEvenement(String typeEvenement) {
        this.typeEvenement = typeEvenement;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public double getCoefficientDemande() {
        return coefficientDemande;
    }

    public void setCoefficientDemande(double coefficientDemande) {
        this.coefficientDemande = coefficientDemande;
    }

    public int getJoursImpactAvant() {
        return joursImpactAvant;
    }

    public void setJoursImpactAvant(int joursImpactAvant) {
        this.joursImpactAvant = joursImpactAvant;
    }

    public int getJoursImpactApres() {
        return joursImpactApres;
    }

    public void setJoursImpactApres(int joursImpactApres) {
        this.joursImpactApres = joursImpactApres;
    }

    public double getCoefficientBaisseApres() {
        return coefficientBaisseApres;
    }

    public void setCoefficientBaisseApres(double coefficientBaisseApres) {
        this.coefficientBaisseApres = coefficientBaisseApres;
    }
}
