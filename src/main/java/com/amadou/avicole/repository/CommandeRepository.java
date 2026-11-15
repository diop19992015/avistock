package com.amadou.avicole.repository;

import com.amadou.avicole.entity.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

    List<Commande> findByDateCommandeBetween(LocalDate debut, LocalDate fin);
    List<Commande> findByLotId(Long lotId);
    long countByLotId(Long lotId);

}
