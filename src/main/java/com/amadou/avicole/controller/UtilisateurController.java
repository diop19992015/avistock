package com.amadou.avicole.controller;

import com.amadou.avicole.entity.Utilisateur;
import com.amadou.avicole.service.UtilisateurService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping
    public Utilisateur creer(@RequestBody Utilisateur utilisateur) {
        return utilisateurService.save(utilisateur);
    }

    @GetMapping
    public List<Utilisateur> getAll() {
        return utilisateurService.getAll();
    }

    @GetMapping("/{id}")
    public Utilisateur getById(@PathVariable Long id) {
        return utilisateurService.getById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        utilisateurService.delete(id);
    }
}
