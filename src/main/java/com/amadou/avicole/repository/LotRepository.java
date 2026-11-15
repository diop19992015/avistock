package com.amadou.avicole.repository;

import com.amadou.avicole.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LotRepository extends JpaRepository<Lot, Long> {
}