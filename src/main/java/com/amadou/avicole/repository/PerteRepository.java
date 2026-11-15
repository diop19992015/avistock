package com.amadou.avicole.repository;

import com.amadou.avicole.entity.Perte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerteRepository extends JpaRepository<Perte, Long> {

    List<Perte> findByLotId(Long lotId);
    long countByLotId(Long lotId);
}
