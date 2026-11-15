package com.amadou.avicole.dto;

import java.time.LocalDate;
import java.util.List;

public class PrevisionApprovisionnementDto {

    private LocalDate dateArriveeLot;
    private LocalDate dateDebutVentePrevue;
    private LocalDate dateFinVentePrevue;
    private int dureeElevageJours;
    private int fenetreVenteJours;
    private int demandeHistoriqueReference;
    private int demandePrevisionnelle;
    private int stockProjeteDisponible;
    private int quantiteConseilleePoussins;
    private int quantiteReferenceSansEffetFete;
    private int ecartSuggestion;
    private double tauxMortaliteHistorique;
    private double coefficientHausseEvenements;
    private double coefficientBaissePostFete;
    private String actionRecommandee;
    private String resume;
    private List<PrevisionFacteurDto> facteurs;

    public LocalDate getDateArriveeLot() {
        return dateArriveeLot;
    }

    public void setDateArriveeLot(LocalDate dateArriveeLot) {
        this.dateArriveeLot = dateArriveeLot;
    }

    public LocalDate getDateDebutVentePrevue() {
        return dateDebutVentePrevue;
    }

    public void setDateDebutVentePrevue(LocalDate dateDebutVentePrevue) {
        this.dateDebutVentePrevue = dateDebutVentePrevue;
    }

    public LocalDate getDateFinVentePrevue() {
        return dateFinVentePrevue;
    }

    public void setDateFinVentePrevue(LocalDate dateFinVentePrevue) {
        this.dateFinVentePrevue = dateFinVentePrevue;
    }

    public int getDureeElevageJours() {
        return dureeElevageJours;
    }

    public void setDureeElevageJours(int dureeElevageJours) {
        this.dureeElevageJours = dureeElevageJours;
    }

    public int getFenetreVenteJours() {
        return fenetreVenteJours;
    }

    public void setFenetreVenteJours(int fenetreVenteJours) {
        this.fenetreVenteJours = fenetreVenteJours;
    }

    public int getDemandeHistoriqueReference() {
        return demandeHistoriqueReference;
    }

    public void setDemandeHistoriqueReference(int demandeHistoriqueReference) {
        this.demandeHistoriqueReference = demandeHistoriqueReference;
    }

    public int getDemandePrevisionnelle() {
        return demandePrevisionnelle;
    }

    public void setDemandePrevisionnelle(int demandePrevisionnelle) {
        this.demandePrevisionnelle = demandePrevisionnelle;
    }

    public int getStockProjeteDisponible() {
        return stockProjeteDisponible;
    }

    public void setStockProjeteDisponible(int stockProjeteDisponible) {
        this.stockProjeteDisponible = stockProjeteDisponible;
    }

    public int getQuantiteConseilleePoussins() {
        return quantiteConseilleePoussins;
    }

    public void setQuantiteConseilleePoussins(int quantiteConseilleePoussins) {
        this.quantiteConseilleePoussins = quantiteConseilleePoussins;
    }

    public int getQuantiteReferenceSansEffetFete() {
        return quantiteReferenceSansEffetFete;
    }

    public void setQuantiteReferenceSansEffetFete(int quantiteReferenceSansEffetFete) {
        this.quantiteReferenceSansEffetFete = quantiteReferenceSansEffetFete;
    }

    public int getEcartSuggestion() {
        return ecartSuggestion;
    }

    public void setEcartSuggestion(int ecartSuggestion) {
        this.ecartSuggestion = ecartSuggestion;
    }

    public double getTauxMortaliteHistorique() {
        return tauxMortaliteHistorique;
    }

    public void setTauxMortaliteHistorique(double tauxMortaliteHistorique) {
        this.tauxMortaliteHistorique = tauxMortaliteHistorique;
    }

    public double getCoefficientHausseEvenements() {
        return coefficientHausseEvenements;
    }

    public void setCoefficientHausseEvenements(double coefficientHausseEvenements) {
        this.coefficientHausseEvenements = coefficientHausseEvenements;
    }

    public double getCoefficientBaissePostFete() {
        return coefficientBaissePostFete;
    }

    public void setCoefficientBaissePostFete(double coefficientBaissePostFete) {
        this.coefficientBaissePostFete = coefficientBaissePostFete;
    }

    public String getActionRecommandee() {
        return actionRecommandee;
    }

    public void setActionRecommandee(String actionRecommandee) {
        this.actionRecommandee = actionRecommandee;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public List<PrevisionFacteurDto> getFacteurs() {
        return facteurs;
    }

    public void setFacteurs(List<PrevisionFacteurDto> facteurs) {
        this.facteurs = facteurs;
    }
}
