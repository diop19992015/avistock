package com.amadou.avicole.service;

import com.amadou.avicole.entity.Evenement;
import com.amadou.avicole.repository.EvenementRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvenementService {

    private final EvenementRepository evenementRepository;

    public EvenementService(EvenementRepository evenementRepository) {
        this.evenementRepository = evenementRepository;
    }

    public Evenement creerEvenement(Evenement evenement) {
        normaliserEvenement(evenement);
        return evenementRepository.save(evenement);
    }

    public List<Evenement> getTousLesEvenements() {
        return evenementRepository.findAll(Sort.by(Sort.Direction.ASC, "dateDebut", "dateFin"));
    }

    public void supprimerEvenement(Long id) {
        evenementRepository.deleteById(id);
    }

    private void normaliserEvenement(Evenement evenement) {
        evenement.setImportance(Math.max(1, evenement.getImportance()));
        evenement.setCoefficientDemande(Math.max(evenement.getCoefficientDemande(), 0));
        evenement.setJoursImpactAvant(Math.max(evenement.getJoursImpactAvant(), 0));
        evenement.setJoursImpactApres(Math.max(evenement.getJoursImpactApres(), 0));
        evenement.setCoefficientBaisseApres(Math.max(evenement.getCoefficientBaisseApres(), 0));

        if (evenement.getDateDebut() != null && evenement.getDateFin() == null) {
            evenement.setDateFin(evenement.getDateDebut());
        }
    }
}
