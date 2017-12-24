package es.uned.repositories;

import es.uned.entities.Search;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface SearchRepo extends JpaRepository<Search, Long> {



}
