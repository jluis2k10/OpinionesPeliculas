package es.uned.services;

import es.uned.entities.Analysis;
import es.uned.entities.Corpus;
import es.uned.entities.LanguageModel;
import es.uned.repositories.AnalysisRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 *
 */
@Service
public class MyAnalysisService implements AnalysisService {

    @Autowired
    private AnalysisRepo analysisRepo;

    @Autowired
    private RecordsService recordsService;

    @Override
    public Analysis findOne(Long analysisID) {
        return analysisRepo.findOne(analysisID);
    }

    @Override
    public Set<Analysis> findByLanguageModel(LanguageModel languageModel) {
        return analysisRepo.findByLanguageModel(languageModel);
    }

    @Override
    public int countByLanguageModel(LanguageModel languageModel) {
        return analysisRepo.countByLanguageModel(languageModel);
    }

    @Override
    public void save(Analysis analysis) {
        analysisRepo.save(analysis);
    }

    @Override
    public void delete(Analysis analysis) {
        recordsService.deleteByAnalysis(analysis.getId());
        analysis.clearAllRecords();
        analysisRepo.delete(analysis);
    }
}
