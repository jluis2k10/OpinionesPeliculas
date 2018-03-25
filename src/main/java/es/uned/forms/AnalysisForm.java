package es.uned.forms;

import java.util.Map;

/**
 * Elementos del formulario para análisis.
 */
public class AnalysisForm {

    private String classifierName;

    private String adapterClass;

    private String languageModel;

    private String languageModelLocation;

    private String language;

    private boolean deleteStopWords;

    private boolean ignoreObjectives;

    private String classifierType;

    private Map<String, String> options;

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

    public String getLanguageModel() {
        return languageModel;
    }

    public String getLanguageModelLocation() {
        return languageModelLocation;
    }

    public void setLanguageModelLocation(String languageModelLocation) {
        this.languageModelLocation = languageModelLocation;
    }

    public void setAdapterClass(String adapterClass) {
        this.adapterClass = adapterClass;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguageModel(String languageModel) {
        this.languageModel = languageModel;
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
