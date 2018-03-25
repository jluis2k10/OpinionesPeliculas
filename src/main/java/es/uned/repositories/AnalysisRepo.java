package es.uned.repositories;

import es.uned.entities.Analysis;
import es.uned.entities.Corpus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface AnalysisRepo extends JpaRepository<Analysis, Long> {

    int countAllByCorpus(Corpus corpus);

}
