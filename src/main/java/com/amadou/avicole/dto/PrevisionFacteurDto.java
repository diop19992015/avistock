package com.amadou.avicole.dto;

public class PrevisionFacteurDto {

    private String categorie;
    private String titre;
    private String description;
    private double variationPourcentage;

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getVariationPourcentage() {
        return variationPourcentage;
    }

    public void setVariationPourcentage(double variationPourcentage) {
        this.variationPourcentage = variationPourcentage;
    }
}
