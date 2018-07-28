package es.uned.services;

import es.uned.entities.Analysis;
import es.uned.entities.Corpus;
import es.uned.entities.LanguageModel;

import java.util.Set;

/**
 * Proporciona servicio para manejar análisis ({@link Analysis}).
 * <p>
 * Forma parte de la capa de servicio de la aplicación, es decir se encarga
 * de tratar los datos antes de enviarlos a la capa de persistencia o tras
 * recibirlos de la capa de persistencia.
 */
public interface AnalysisService {

    /**
     * Busca un análisis por su identificador
     * @param analysisID identificador del análisis
     * @return Análisis encontrado
     */
    Analysis findOne(Long analysisID);

    /**
     * Busca los análisis que hayan utilizado un modelo de lenguaje dado
     * @param languageModel Modelo de lenguaje utilizado por los análisis
     * @return conjunto de análisis
     */
    Set<Analysis> findByLanguageModel(LanguageModel languageModel);

    /**
     * Devuelve el número de análisis que han utilizado un modelo de lenguaje dado
     * @param languageModel modelo de lenguaje utilizado por los análisis
     * @return número total de análisis que utilizan el modelo de lenguaje
     */
    int countByLanguageModel(LanguageModel languageModel);

    /**
     * Persiste un análisis
     * @param analysis análisis a persistir
     */
    void save(Analysis analysis);

    /**
     * Elimina un análisis de la base de datos
     * @param analysis análisis a eliminar
     */
    void delete(Analysis analysis);

}
