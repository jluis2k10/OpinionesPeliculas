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
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.util.Set;

/**
 * Implementación de la interfaz {@link LanguageModelService} para dar servicio
 * a operaciones sobre modelos de lenguaje ({@link LanguageModel}).
 */
@Service
public class MyLanguageModelService implements LanguageModelService {

    @Autowired private LanguageModelsRepo languageModelsRepo;
    @Autowired private AnalysisService analysisService;
    @Autowired private CorpusService corpusService;
    @Autowired private ResourceLoader resourceLoader;

    /**
     * {@inheritDoc}
     * @param id identificador del modelo de lenguaje
     * @return Modelo de lenguaje encontrado
     */
    @Override
    public LanguageModel findOne(Long id) {
        return languageModelsRepo.findOne(id);
    }

    /**
     * {@inheritDoc}
     * @param adapterClass adaptador (de clasificador) que utiliza los modelos de lenguaje
     * @param lang         idioma de los modelos de lenguaje
     * @param owner        usuario propietario (creador) de los modelos de lenguaje
     * @return Conjunto de modelos de lenguaje
     */
    @Override
    public Set<LanguageModel> findByAdapterClassAndLang(String adapterClass, String lang, Account owner) {
        return languageModelsRepo.findByAdapterClassAndLanguageAndOwner_OrAdapterClassAndLanguageAndIsPublicTrue(adapterClass, lang, owner, adapterClass, lang);
    }

    /**
     * {@inheritDoc}
     * @param account     usuario propietarios de los modelos de lenguaje
     * @param adapterType tipo de clasificador utilizado
     * @return Conjunto de modelos de lenguaje
     */
    @Override
    public Set<LanguageModel> findUserModels(Account account, ClassifierType adapterType) {
        return languageModelsRepo.findByOwnerAndClassifierType(account, adapterType);
    }

    /**
     * {@inheritDoc}
     * @param account     cuenta de usuario no propietario de los modelos de lenguaje
     * @param adapterType tipo de clasificador utilizado
     * @return Conjunto de modelos de lenguaje
     */
    @Override
    public Set<LanguageModel> findFromOthers(Account account, ClassifierType adapterType) {
        return languageModelsRepo.findByOwnerNotAndClassifierType(account, adapterType);
    }

    /**
     * {@inheritDoc}
     * @param adapterPath   ruta total o parcial donde se encuentra almacenado en disco el archivo
     *                      serializado del modelo
     * @param languageModel modelo de lenguaje a eliminar
     * @return true si la operación se ha ejecutado con éxito, false en caso contrario
     */
    @Override
    public boolean delete(String adapterPath, LanguageModel languageModel) {
        // Intentamos eliminar el modelo de lenguaje serializado que se encunetra en disco
        Resource dir = resourceLoader.getResource(adapterPath + languageModel.getLocation());
        try {
            FileSystemUtils.deleteRecursively(dir.getFile());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // Antes de borrar el modelo de lenguaje, hay que borrar también los posibles
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

    /**
     * {@inheritDoc}
     * @param languageModel modelo de lenguaje a persistir
     */
    @Override
    public void save(LanguageModel languageModel) {
        languageModelsRepo.save(languageModel);
    }
}
