package com.amadou.avicole.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "lots")
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomLot;

    private LocalDate dateArrivee;

    private int quantiteInitiale;
    private int quantiteRestante;

    private LocalDate dateMaturitePrevue;

    private String statutLot;

    private double coutAchat;
    private double coutAliment;
    private double coutSante;
    private double autresCharges;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    @JsonIgnore
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "lot")
    @JsonIgnore
    private List<Perte> pertes;

    @OneToMany(mappedBy = "lot")
    @JsonIgnore
    private List<Commande> commandes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomLot() {
        return nomLot;
    }

    public void setNomLot(String nomLot) {
        this.nomLot = nomLot;
    }

    public LocalDate getDateArrivee() {
        return dateArrivee;
    }

    public void setDateArrivee(LocalDate dateArrivee) {
        this.dateArrivee = dateArrivee;
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

    public LocalDate getDateMaturitePrevue() {
        return dateMaturitePrevue;
    }

    public void setDateMaturitePrevue(LocalDate dateMaturitePrevue) {
        this.dateMaturitePrevue = dateMaturitePrevue;
    }

    public String getStatutLot() {
        return statutLot;
    }

    public void setStatutLot(String statutLot) {
        this.statutLot = statutLot;
    }

    public double getCoutAchat() {
        return coutAchat;
    }

    public void setCoutAchat(double coutAchat) {
        this.coutAchat = coutAchat;
    }

    public double getCoutAliment() {
        return coutAliment;
    }

    public void setCoutAliment(double coutAliment) {
        this.coutAliment = coutAliment;
    }

    public double getCoutSante() {
        return coutSante;
    }

    public void setCoutSante(double coutSante) {
        this.coutSante = coutSante;
    }

    public double getAutresCharges() {
        return autresCharges;
    }

    public void setAutresCharges(double autresCharges) {
        this.autresCharges = autresCharges;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public List<Perte> getPertes() {
        return pertes;
    }

    public void setPertes(List<Perte> pertes) {
        this.pertes = pertes;
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    public void setCommandes(List<Commande> commandes) {
        this.commandes = commandes;
    }
}
