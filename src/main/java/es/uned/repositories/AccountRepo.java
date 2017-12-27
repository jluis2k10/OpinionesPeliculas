package es.uned.repositories;

import es.uned.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {
    Account findByUserName(String userName);
}
