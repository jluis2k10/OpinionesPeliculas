package es.uned.repositories;

import es.uned.entities.Account;
import es.uned.entities.Corpus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 */
@Repository
public interface CorporaRepo extends JpaRepository<Corpus, Long> {

    List<Corpus> findAllByOwner(Account owner);

}
