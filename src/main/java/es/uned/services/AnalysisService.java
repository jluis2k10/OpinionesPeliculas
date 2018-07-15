package es.uned.services;

import es.uned.entities.Analysis;
import es.uned.entities.Corpus;
import es.uned.entities.LanguageModel;

import java.util.Set;

/**
 *
 */
public interface AnalysisService {

    Analysis findOne(Long analysisID);
    Set<Analysis> findByLanguageModel(LanguageModel languageModel);
    int countByLanguageModel(LanguageModel languageModel);
    void save(Analysis analysis);
    void delete(Analysis analysis);

}
