package es.uned.adapters.sentiment;

import es.uned.adapters.ClassifierType;
import es.uned.entities.Analysis;
import es.uned.entities.Corpus;

import java.util.List;
import java.util.Map;

/**
 * Interfaz para los adaptadores de análisis de opinión.
 * Las implementaciones utilizaran las librerías propias del clasificador utilizado.
 */
public interface SentimentAdapter {

    /**
     * Directorio raíz donde se guardarán los modelos de lenguaje de este tipo de clasificadores
     */
    String MODELS_DIR = "/models/sentiment";

    /**
     * Tipo de clasificadores del adaptador
     */
    ClassifierType adapterType = ClassifierType.POLARITY;

    /**
     * Devuelve la ruta completa hacia el directorio donde se guardan los modelos de lenguaje
     * del clasificador
     * @return String con la ruta
     */
    String get_adapter_path();

    /**
     * Analizar los comentarios de un corpus según las opciones indicadas
     * @param corpus   Corpus sobre el que se ejecutará el análisis
     * @param analysis Análisis que se ejecutará y sus opciones
     */
    void analyze(Corpus corpus, Analysis analysis);

    /**
     * Entrenar modelo de lenguaje indicado
     * @param modelLocation Ruta donde se encuentra el modelo de lenguaje a entrenar
     * @param datasets      Listado con los datasets categorizados
     */
    void trainModel(String modelLocation, Map<Enum, List<String>> datasets);

    /**
     * Crear un nuevo modelo de lenguaje
     * @param modelLocation Ruta en la que guardar el modelo de lenguaje generado
     * @param options       Opciones que indican al adaptador cómo se debe crear el nuevo modelo
     *                      de lenguaje
     * @param datasets      Listado de datasets categorizados con los que entrenar inicialmente
     *                      el modelo de lenguaje
     */
    void createModel(String modelLocation, Map<String,String> options, Map<Enum, List<String>> datasets);

}
