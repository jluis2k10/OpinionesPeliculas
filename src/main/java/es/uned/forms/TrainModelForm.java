package es.uned.forms;

import es.uned.adapters.ClassifierType;
import es.uned.entities.LanguageModel;
import es.uned.entities.Opinion;
import es.uned.entities.Polarity;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Clase para respaldar el formulario que se utiliza para entrenar un modelo
 * de lenguaje ya existente.
 */
public class TrainModelForm extends SourceForm {

    private String sourceClass;
    private ClassifierType classifierType;
    private String modelLocation;
    private String adapterClass;
    private boolean neutralClassification;

    private String positivesText;
    private String negativesText;
    private String neutralsText;
    private String subjectivesText;
    private String objectivesText;
    private MultipartFile positivesFile;
    private MultipartFile negativesFile;
    private MultipartFile neutralsFile;
    private MultipartFile subjectivesFile;
    private MultipartFile objectivesFile;

    public TrainModelForm() {}

    /**
     * Constructor del formulario a partir de un modelo de lenguaje.
     * @param model Modelo de lenguaje a partir del cual se construye este formulario
     */
    public TrainModelForm(LanguageModel model) {
        setLang(model.getLanguage());
        this.classifierType = model.getClassifierType();
        this.modelLocation = model.getLocation();
        this.adapterClass = model.getAdapterClass();
        this.neutralClassification = model.isNeutralClassification();
    }

    /**
     * Crea un mapa con los textos que se utilizarán para entrenar el modelo de lenguaje,
     * clasificado por las categorías.
     * @return Mapa con los textos que se utilizan para entrenar el modelo de lenguaje
     */
    public Map<Enum, List<String>> buildDatasets() {
        return (classifierType == ClassifierType.POLARITY ? buildPolarityDatasets() : buildOpinionDatasets());
    }

    /**
     * Construir mapa con los textos que se utilizan para entrenar el modelo de lenguaje
     * en clasificadores de polaridad.
     * @return Mapa con los textos que se utilizan para entrenar el modelo de lenguaje
     */
    private Map<Enum, List<String>> buildPolarityDatasets() {
        Map<Enum, List<String>> datasets = new EnumMap(Polarity.class);

        if ("es.uned.adapters.sources.Dataset".equals(this.sourceClass)) {
            datasets.put(Polarity.POSITIVE, getSentences(this.positivesFile));
            datasets.put(Polarity.NEGATIVE, getSentences(this.negativesFile));
            if (isNeutralClassification())
                datasets.put(Polarity.NEUTRAL, getSentences(this.neutralsFile));
        }
        else if ("TextDataset".equals(this.sourceClass)) {
            datasets.put(Polarity.POSITIVE, getSentences(this.positivesText));
            datasets.put(Polarity.NEGATIVE, getSentences(this.negativesText));
            if (isNeutralClassification())
                datasets.put(Polarity.NEUTRAL, getSentences(this.neutralsText));
        }

        return datasets;
    }

    /**
     * Construir mapa con los textos que se utilizan para entrenar el modelo de lenguaje
     * en clasificadores de polaridad.
     * @return Mapa con los textos que se utilzian para entrenar el modelo de lenguaje
     */
    private Map<Enum, List<String>> buildOpinionDatasets() {
        Map<Enum, List<String>> datasets = new EnumMap(Opinion.class);

        if ("es.uned.adapters.sources.Dataset".equals(this.sourceClass)) {
            datasets.put(Opinion.SUBJECTIVE, getSentences(subjectivesFile));
            datasets.put(Opinion.OBJECTIVE, getSentences(objectivesFile));
        }
        else if ("TextDataset".equals(this.sourceClass)) {
            datasets.put(Opinion.SUBJECTIVE, getSentences(subjectivesText));
            datasets.put(Opinion.OBJECTIVE, getSentences(objectivesText));
        }

        return datasets;
    }

    /**
     * Lee el contenido de un archivo de texto y devuelve una lista de cadenas a partir del
     * contenido del archivo, añadiendo un elemento a la lista de salida con cada nueva línea
     * de texto en el archivo.
     * @param file Archivo de texto a leer
     * @return Lista con un elemento por cada nueva línea en el archivo de texto
     */
    private List<String> getSentences(MultipartFile file) {
        List<String> sentences = new ArrayList<>();
        try {
            InputStream is = file.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                sentences.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sentences;
    }

    /**
     * Devuelve una lista de cadenas de texto a partir de un texto cualquiera. Cada nueva línea
     * del texto de entrada es un elemento más en la lista de salida.
     * @param text Texto de entrada a convertir
     * @return Lista con un elemento por cada nueva línea en el texto de entrada
     */
    private List<String> getSentences(String text) {
        String[] lines = text.split("\\r?\\n");
        return new ArrayList<>(Arrays.asList(lines));
    }

    public String getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(String sourceClass) {
        this.sourceClass = sourceClass;
    }

    public ClassifierType getClassifierType() {
        return classifierType;
    }

    public void setClassifierType(ClassifierType classifierType) {
        this.classifierType = classifierType;
    }

    public String getModelLocation() {
        return modelLocation;
    }

    public void setModelLocation(String modelLocation) {
        this.modelLocation = modelLocation;
    }

    public String getAdapterClass() {
        return adapterClass;
    }

    public void setAdapterClass(String adapterClass) {
        this.adapterClass = adapterClass;
    }

    public boolean isNeutralClassification() {
        return neutralClassification;
    }

    public boolean getNeutralClassification() {
        return neutralClassification;
    }

    public void setNeutralClassification(boolean neutralClassification) {
        this.neutralClassification = neutralClassification;
    }

    public String getPositivesText() {
        return positivesText;
    }

    public void setPositivesText(String positivesText) {
        this.positivesText = positivesText;
    }

    public String getNegativesText() {
        return negativesText;
    }

    public void setNegativesText(String negativesText) {
        this.negativesText = negativesText;
    }

    public String getNeutralsText() {
        return neutralsText;
    }

    public void setNeutralsText(String neutralsText) {
        this.neutralsText = neutralsText;
    }

    public String getSubjectivesText() {
        return subjectivesText;
    }

    public void setSubjectivesText(String subjectivesText) {
        this.subjectivesText = subjectivesText;
    }

    public String getObjectivesText() {
        return objectivesText;
    }

    public void setObjectivesText(String objectivesText) {
        this.objectivesText = objectivesText;
    }

    public MultipartFile getPositivesFile() {
        return positivesFile;
    }

    public void setPositivesFile(MultipartFile positivesFile) {
        this.positivesFile = positivesFile;
    }

    public MultipartFile getNegativesFile() {
        return negativesFile;
    }

    public void setNegativesFile(MultipartFile negativesFile) {
        this.negativesFile = negativesFile;
    }

    public MultipartFile getNeutralsFile() {
        return neutralsFile;
    }

    public void setNeutralsFile(MultipartFile neutralsFile) {
        this.neutralsFile = neutralsFile;
    }

    public MultipartFile getSubjectivesFile() {
        return subjectivesFile;
    }

    public void setSubjectivesFile(MultipartFile subjectivesFile) {
        this.subjectivesFile = subjectivesFile;
    }

    public MultipartFile getObjectivesFile() {
        return objectivesFile;
    }

    public void setObjectivesFile(MultipartFile objectivesFile) {
        this.objectivesFile = objectivesFile;
    }
}
