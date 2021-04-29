package com.challenge.authorizer.repository;

import com.challenge.authorizer.model.entities.AccountEntityImmutable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntityImmutable, Long> {

}
