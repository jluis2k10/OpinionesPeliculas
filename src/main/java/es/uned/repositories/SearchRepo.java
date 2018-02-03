package es.uned.repositories;

import es.uned.entities.Account;
import es.uned.entities.Search;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 *
 */
@Repository
public interface SearchRepo extends JpaRepository<Search, Long> {

    Set<Search> findByOwner(Account account);
    /* Búsquedas que NO sean del usuario 'owner' y que no sean de otros usuarios con rol 'role' */
    Set<Search> findByOwnerNotAndOwner_Roles_RoleNot(Account account, String role);

}
