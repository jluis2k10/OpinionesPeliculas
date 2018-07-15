package es.uned.services;

import es.uned.adapters.ClassifierType;
import es.uned.entities.Account;
import es.uned.entities.Analysis;
import es.uned.entities.Corpus;
import es.uned.entities.LanguageModel;
import es.uned.repositories.LanguageModelsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 *
 */
@Service
public class MyLanguageModelService implements LanguageModelService {

    @Autowired
    private LanguageModelsRepo languageModelsRepo;

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private CorpusService corpusService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public LanguageModel findOne(Long id) {
        return languageModelsRepo.findOne(id);
    }

    @Override
    public Set<LanguageModel> findByAdapterClassAndLang(String adapterClass, String lang, Account owner) {
        return languageModelsRepo.findByAdapterClassAndLanguageAndOwner_OrAdapterClassAndLanguageAndIsPublicTrue(adapterClass, lang, owner, adapterClass, lang);
    }

    @Override
    public Set<LanguageModel> findUserModels(Account account, ClassifierType adapterType) {
        return languageModelsRepo.findByOwnerAndClassifierType(account, adapterType);
    }

    @Override
    public Set<LanguageModel> findFromOthers(Account account, ClassifierType adapterType) {
        return languageModelsRepo.findByOwnerNotAndClassifierType(account, adapterType);
    }

    @Override
    public boolean delete(String adapterPath, LanguageModel languageModel) {
        Resource dir = resourceLoader.getResource(adapterPath + languageModel.getLocation());
        try {
            FileSystemUtils.deleteRecursively(dir.getFile());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // Antes de borrar el modelo de lenguaje, hay que borrar los posibles
        // análisis que hayan hecho uso de él
        Set<Analysis> analyses = analysisService.findByLanguageModel(languageModel);
        analyses.forEach(analysis -> {
            Corpus corpus = analysis.getCorpus();
            analysisService.delete(analysis);
            corpus.refreshScores();
            corpusService.save(corpus);
        });
        languageModelsRepo.delete(languageModel.getId());
        return true;
    }

    @Override
    public void save(LanguageModel languageModel) {
        languageModelsRepo.save(languageModel);
    }
}
