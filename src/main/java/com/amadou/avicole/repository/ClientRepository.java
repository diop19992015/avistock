package com.amadou.avicole.repository;

import com.amadou.avicole.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByTelephone(String telephone);

}