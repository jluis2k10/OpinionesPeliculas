package es.uned.services;

import es.uned.entities.Analysis;
import es.uned.entities.Corpus;

/**
 *
 */
public interface AnalysisService {

    Analysis findOne(Long analysisID);
    void save(Analysis analysis);
    void delete(Analysis analysis);

}
