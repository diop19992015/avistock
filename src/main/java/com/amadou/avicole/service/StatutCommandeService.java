package com.amadou.avicole.service;

import com.amadou.avicole.entity.StatutCommande;
import com.amadou.avicole.repository.StatutCommandeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatutCommandeService {

    private final StatutCommandeRepository statutRepository;

    public StatutCommandeService(StatutCommandeRepository statutRepository) {
        this.statutRepository = statutRepository;
    }

    public StatutCommande creerStatut(StatutCommande statut) {
        return statutRepository.save(statut);
    }

    public List<StatutCommande> getTousLesStatuts() {
        return statutRepository.findAll();
    }
}