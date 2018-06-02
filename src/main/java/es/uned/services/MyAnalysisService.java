package es.uned.services;

import es.uned.entities.Analysis;
import es.uned.entities.Corpus;
import es.uned.repositories.AnalysisRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Service
public class MyAnalysisService implements AnalysisService {

    @Autowired private AnalysisRepo analysisRepo;

    @Autowired
    RecordsService recordsService;

    @Override
    public Analysis findOne(Long analysisID) {
        return analysisRepo.findOne(analysisID);
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
