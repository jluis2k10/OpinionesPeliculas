package es.uned.repositories;

import es.uned.entities.Corpus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface CorporaRepo extends JpaRepository<Corpus, Long> {



}
