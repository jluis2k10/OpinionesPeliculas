package es.uned.services;

import es.uned.entities.Analysis;
import es.uned.entities.Corpus;

/**
 *
 */
public interface AnalysisService {

    //Analysis findOne(Analysis.AnalysisPK pk);
    int countByCorpus(Corpus corpus);
    void save(Analysis analysis);
    void delete(Analysis analysis);

}
