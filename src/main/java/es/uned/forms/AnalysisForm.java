package es.uned.forms;

import es.uned.entities.LanguageModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Elementos del formulario con los parámetros necesarios para ejecutar análisis sobre
 * comentarios.
 */
public class AnalysisForm {

    private String classifierName;

    private String adapterClass;

    private LanguageModel languageModel;

    private String language;

    private boolean deleteStopWords;

    private boolean ignoreObjectives;

    private String classifierType;

    /**
     * Todos los campos del formulario generados de forma dinámica y dependientes de cada
     * tipo de clasificador se almacenan en esta variable, en forma de mapa en el cual
     * la clave es el nombre de la opción y el valor es la opción seleccionada por el usuario
     * en el formulario.
     */
    private Map<String, String> options = new HashMap<>();

    public AnalysisForm() {
    }

    public String getClassifierName() {
        return classifierName;
    }

    public void setClassifierName(String classifierName) {
        this.classifierName = classifierName;
    }

    public String getAdapterClass() {
        return adapterClass;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LanguageModel getLanguageModel() {
        return languageModel;
    }

    public void setLanguageModel(LanguageModel languageModel) {
        this.languageModel = languageModel;
    }

    public void setAdapterClass(String adapterClass) {
        this.adapterClass = adapterClass;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isDeleteStopWords() {
        return deleteStopWords;
    }

    public void setDeleteStopWords(boolean deleteStopWords) {
        this.deleteStopWords = deleteStopWords;
    }

    public boolean isIgnoreObjectives() {
        return ignoreObjectives;
    }

    public void setIgnoreObjectives(boolean ignoreObjectives) {
        this.ignoreObjectives = ignoreObjectives;
    }

    public String getClassifierType() {
        return classifierType;
    }

    public void setClassifierType(String classifierType) {
        this.classifierType = classifierType;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }
}
