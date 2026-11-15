package com.amadou.avicole.dto;

public class RentabiliteGlobaleDto {

    private int nombreLots;
    private int quantiteVendue;
    private int quantitePerdue;
    private int stockRestant;
    private double chiffreAffairesTotal;
    private double chargesTotales;
    private double beneficeNet;
    private double margePourcentage;
    private double tauxMortaliteGlobal;

    public int getNombreLots() {
        return nombreLots;
    }

    public void setNombreLots(int nombreLots) {
        this.nombreLots = nombreLots;
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

    public int getStockRestant() {
        return stockRestant;
    }

    public void setStockRestant(int stockRestant) {
        this.stockRestant = stockRestant;
    }

    public double getChiffreAffairesTotal() {
        return chiffreAffairesTotal;
    }

    public void setChiffreAffairesTotal(double chiffreAffairesTotal) {
        this.chiffreAffairesTotal = chiffreAffairesTotal;
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

    public double getTauxMortaliteGlobal() {
        return tauxMortaliteGlobal;
    }

    public void setTauxMortaliteGlobal(double tauxMortaliteGlobal) {
        this.tauxMortaliteGlobal = tauxMortaliteGlobal;
    }
}
