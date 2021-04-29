package com.challenge.authorizer.repository;

import com.challenge.authorizer.model.entities.TransactionEntityImmutable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntityImmutable, Long> {

    List<TransactionEntityImmutable>  findAllByTimeAfter(@Param("transactionDate") Date transactionDate);
    List<TransactionEntityImmutable> findTopByMerchantAndAmountOrderByTimeDesc(String merchant, Integer amount);

}
