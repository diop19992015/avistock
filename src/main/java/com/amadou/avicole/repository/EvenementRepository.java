package com.amadou.avicole.repository;

import com.amadou.avicole.entity.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {
}