package com.amadou.avicole.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "statuts_commande")
public class StatutCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomStatut;

    @OneToMany(mappedBy = "statut")
    @JsonIgnore
    private List<Commande> commandes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomStatut() {
        return nomStatut;
    }

    public void setNomStatut(String nomStatut) {
        this.nomStatut = nomStatut;
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    public void setCommandes(List<Commande> commandes) {
        this.commandes = commandes;
    }
}
