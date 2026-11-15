package com.amadou.avicole.controller;

import com.amadou.avicole.entity.Client;
import com.amadou.avicole.service.ClientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public Client creerClient(@RequestBody Client client) {
        return clientService.creerClient(client);
    }

    @GetMapping
    public List<Client> getTousLesClients() {
        return clientService.getTousLesClients();
    }

    @GetMapping("/{id}")
    public Client getClientParId(@PathVariable Long id) {
        return clientService.getClientParId(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Client modifierClient(@PathVariable Long id, @RequestBody Client client) {
        client.setId(id);
        return clientService.mettreAJourClient(client);
    }

    @DeleteMapping("/{id}")
    public void supprimerClient(@PathVariable Long id) {
        clientService.supprimerClient(id);
    }
}
