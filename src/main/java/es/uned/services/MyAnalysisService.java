package es.uned.services;

import es.uned.entities.Analysis;
import es.uned.entities.LanguageModel;
import es.uned.repositories.AnalysisRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Implementación de la interfaz {@link AnalysisService} para dar servicio
 * a operaciones sobre cuentas de usuario ({@link Analysis}).
 */
@Service
public class MyAnalysisService implements AnalysisService {

    @Autowired private AnalysisRepo analysisRepo;
    @Autowired private RecordsService recordsService;

    /**
     * {@inheritDoc}
     * @param analysisID identificador del análisis
     * @return Análisis encontrado
     */
    @Override
    public Analysis findOne(Long analysisID) {
        return analysisRepo.findOne(analysisID);
    }

    /**
     * {@inheritDoc}
     * @param languageModel Modelo de lenguaje utilizado por los análisis
     * @return Conjunto de análisis
     */
    @Override
    public Set<Analysis> findByLanguageModel(LanguageModel languageModel) {
        return analysisRepo.findByLanguageModel(languageModel);
    }

    /**
     * {@inheritDoc}
     * @param languageModel modelo de lenguaje utilizado por los análisis
     * @return número total de análisis que utilizan el modelo de lenguaje
     */
    @Override
    public int countByLanguageModel(LanguageModel languageModel) {
        return analysisRepo.countByLanguageModel(languageModel);
    }

    /**
     * {@inheritDoc}
     * @param analysis análisis a persistir
     */
    @Override
    public void save(Analysis analysis) {
        analysisRepo.save(analysis);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Para mantener la consistencia en la base de datos se eliminan también todos los
     * records que hayan sido generados por este análisis.
     * @param analysis análisis a eliminar
     */
    @Override
    public void delete(Analysis analysis) {
        recordsService.deleteByAnalysis(analysis.getId());
        analysis.clearAllRecords();
        analysisRepo.delete(analysis);
    }
}
