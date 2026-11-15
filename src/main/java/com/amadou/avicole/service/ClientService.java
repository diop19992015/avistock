package com.amadou.avicole.service;

import com.amadou.avicole.entity.Client;
import com.amadou.avicole.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client creerClient(Client client) {
        return clientRepository.save(client);
    }

    public List<Client> getTousLesClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClientParId(Long id) {
        return clientRepository.findById(id);
    }

    public Client mettreAJourClient(Client client) {
        return clientRepository.save(client);
    }

    public void supprimerClient(Long id) {
        clientRepository.deleteById(id);
    }
}