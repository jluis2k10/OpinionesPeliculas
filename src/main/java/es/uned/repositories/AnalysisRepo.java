package es.uned.repositories;

import es.uned.entities.Analysis;
import es.uned.entities.Corpus;
import es.uned.entities.LanguageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 *
 */
@Repository
public interface AnalysisRepo extends JpaRepository<Analysis, Long> {

    Set<Analysis> findByLanguageModel(LanguageModel languageModel);
    int countByLanguageModel(LanguageModel languageModel);

}
