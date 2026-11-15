package com.amadou.avicole.service;

import com.amadou.avicole.entity.Utilisateur;
import com.amadou.avicole.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    private final UtilisateurRepository repository;

    public UtilisateurService(UtilisateurRepository repository) {
        this.repository = repository;
    }

    public Utilisateur save(Utilisateur u) {
        return repository.save(u);
    }

    public List<Utilisateur> getAll() {
        return repository.findAll();
    }

    public Optional<Utilisateur> getById(Long id) {
        return repository.findById(id);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}