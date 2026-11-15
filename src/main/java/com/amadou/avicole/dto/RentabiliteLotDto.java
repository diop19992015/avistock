package com.amadou.avicole.dto;

public class RentabiliteLotDto {

    private Long lotId;
    private String nomLot;
    private int quantiteInitiale;
    private int quantiteVendue;
    private int quantitePerdue;
    private int quantiteRestante;
    private double chiffreAffaires;
    private double chargesTotales;
    private double beneficeNet;
    private double margePourcentage;
    private double tauxMortalite;
    private boolean rentable;

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

    public int getQuantiteInitiale() {
        return quantiteInitiale;
    }

    public void setQuantiteInitiale(int quantiteInitiale) {
        this.quantiteInitiale = quantiteInitiale;
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

    public int getQuantiteRestante() {
        return quantiteRestante;
    }

    public void setQuantiteRestante(int quantiteRestante) {
        this.quantiteRestante = quantiteRestante;
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
}
