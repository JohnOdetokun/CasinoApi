package com.assessment.casinoapi.respository;

import com.assessment.casinoapi.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByPlayerId(Integer playerId, PageRequest pageRequest);
}
