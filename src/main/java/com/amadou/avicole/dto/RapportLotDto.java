package com.amadou.avicole.dto;

import java.time.LocalDate;
import java.util.List;

public class RapportLotDto {

    private LocalDate dateGeneration;
    private Long lotId;
    private String nomLot;
    private String statutLot;
    private LocalDate dateArrivee;
    private LocalDate dateMaturitePrevue;
    private int quantiteInitiale;
    private int quantiteRestante;
    private int quantiteVendue;
    private int quantitePerdue;
    private double chiffreAffaires;
    private double chargesTotales;
    private double beneficeNet;
    private double margePourcentage;
    private double tauxMortalite;
    private boolean rentable;
    private List<RapportCommandeDto> commandes;
    private List<RapportPerteDto> pertes;

    public LocalDate getDateGeneration() {
        return dateGeneration;
    }

    public void setDateGeneration(LocalDate dateGeneration) {
        this.dateGeneration = dateGeneration;
    }

    public Long getLotId() {
        return lotId;
    }

    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }

    public String getNomLot() {
        return nomLot;
    }

    public void setNomLot(String nomLot) {
        this.nomLot = nomLot;
    }

    public String getStatutLot() {
        return statutLot;
    }

    public void setStatutLot(String statutLot) {
        this.statutLot = statutLot;
    }

    public LocalDate getDateArrivee() {
        return dateArrivee;
    }

    public void setDateArrivee(LocalDate dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    public LocalDate getDateMaturitePrevue() {
        return dateMaturitePrevue;
    }

    public void setDateMaturitePrevue(LocalDate dateMaturitePrevue) {
        this.dateMaturitePrevue = dateMaturitePrevue;
    }

    public int getQuantiteInitiale() {
        return quantiteInitiale;
    }

    public void setQuantiteInitiale(int quantiteInitiale) {
        this.quantiteInitiale = quantiteInitiale;
    }

    public int getQuantiteRestante() {
        return quantiteRestante;
    }

    public void setQuantiteRestante(int quantiteRestante) {
        this.quantiteRestante = quantiteRestante;
    }

    public int getQuantiteVendue() {
        return quantiteVendue;
    }

    public void setQuantiteVendue(int quantiteVendue) {
        this.quantiteVendue = quantiteVendue;
    }

    public int getQuantitePerdue() {
        return quantitePerdue;
    }

    public void setQuantitePerdue(int quantitePerdue) {
        this.quantitePerdue = quantitePerdue;
    }

    public double getChiffreAffaires() {
        return chiffreAffaires;
    }

    public void setChiffreAffaires(double chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
    }

    public double getChargesTotales() {
        return chargesTotales;
    }

    public void setChargesTotales(double chargesTotales) {
        this.chargesTotales = chargesTotales;
    }

    public double getBeneficeNet() {
        return beneficeNet;
    }

    public void setBeneficeNet(double beneficeNet) {
        this.beneficeNet = beneficeNet;
    }

    public double getMargePourcentage() {
        return margePourcentage;
    }

    public void setMargePourcentage(double margePourcentage) {
        this.margePourcentage = margePourcentage;
    }

    public double getTauxMortalite() {
        return tauxMortalite;
    }

    public void setTauxMortalite(double tauxMortalite) {
        this.tauxMortalite = tauxMortalite;
    }

    public boolean isRentable() {
        return rentable;
    }

    public void setRentable(boolean rentable) {
        this.rentable = rentable;
    }

    public List<RapportCommandeDto> getCommandes() {
        return commandes;
    }

    public void setCommandes(List<RapportCommandeDto> commandes) {
        this.commandes = commandes;
    }

    public List<RapportPerteDto> getPertes() {
        return pertes;
    }

    public void setPertes(List<RapportPerteDto> pertes) {
        this.pertes = pertes;
    }
}
