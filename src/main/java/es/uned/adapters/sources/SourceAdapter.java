package es.uned.adapters.sources;

import es.uned.entities.Corpus;
import es.uned.forms.SourceForm;

/**
 * Interfaz para los adaptadores de fuentes de comentarios.
 * Las implementaciones usarán las librerías que se necesiten en cada caso.
 */
public interface SourceAdapter {

    /**
     * Opciones indicadas para recuperar los comentarios.
     * @param sourceForm Formulario con los parámetros opcionales
     */
    void setOptions(SourceForm sourceForm);

    /**
     * Recuperar comentarios y añadirlos al corpus.
     * @param corpus Corpus sobre el que se está trabajando
     */
    void generateCorpus(Corpus corpus);

    /**
     * Recuperar comentarios según las opciones indicadas y añadirlos al corpus.
     * @param sourceForm Formulario con los parámetros opcionales
     * @param corpus     Corpus sobre el que se está trabajando
     * @return Número de nuevos comentarios añadidos al corpus
     */
    int updateCorpus(SourceForm sourceForm, Corpus corpus);

}
