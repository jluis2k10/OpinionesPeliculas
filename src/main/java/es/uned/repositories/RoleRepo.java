package es.uned.repositories;

import es.uned.entities.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface RoleRepo extends JpaRepository<AccountRole, Long> {
    AccountRole findByRole(String role);
}
