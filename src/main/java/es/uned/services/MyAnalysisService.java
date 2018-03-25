package es.uned.services;

import es.uned.entities.Analysis;
import es.uned.entities.Corpus;
import es.uned.repositories.AnalysisRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class MyAnalysisService implements AnalysisService {

    @Autowired private AnalysisRepo analysisRepo;

    @Autowired
    RecordsService recordsService;

    @Override
    public int countByCorpus(Corpus corpus) {
        return analysisRepo.countAllByCorpus(corpus);
    }

    @Override
    public void save(Analysis analysis) {
        analysisRepo.save(analysis);
    }

    @Override
    public void delete(Analysis analysis) {
        analysisRepo.delete(analysis);
    }
}
