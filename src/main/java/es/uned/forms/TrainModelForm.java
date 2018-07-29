package es.uned.forms;

import es.uned.adapters.ClassifierType;
import es.uned.components.DatasetsBuilder;
import es.uned.entities.LanguageModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Clase para respaldar el formulario que se utiliza para entrenar un modelo
 * de lenguaje ya existente.
 */
public class TrainModelForm extends SourceForm {

    private DatasetsBuilder datasetsBuilder = new DatasetsBuilder();

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
        if ("es.uned.adapters.sources.Dataset".equals(this.sourceClass) && classifierType == ClassifierType.POLARITY) {
            datasetsBuilder.put("positives", this.positivesFile);
            datasetsBuilder.put("negatives", this.negativesFile);
            if (isNeutralClassification())
                datasetsBuilder.put("neutrals", this.neutralsFile);
        }
        else if ("TextDataset".equals(this.sourceClass) && classifierType == ClassifierType.POLARITY) {
            datasetsBuilder.put("positives", this.positivesText);
            datasetsBuilder.put("negatives", this.negativesText);
            if (isNeutralClassification())
                datasetsBuilder.put("neutrals", this.negativesText);
        }
        else if ("es.uned.adapters.sources.Dataset".equals(this.sourceClass) && classifierType == ClassifierType.OPINION) {
            datasetsBuilder.put("subjectives", this.subjectivesFile);
            datasetsBuilder.put("objectives", this.objectivesFile);
        }
        else if ("TextDataset".equals(this.sourceClass) && classifierType == ClassifierType.OPINION) {
            datasetsBuilder.put("subjectives", this.subjectivesText);
            datasetsBuilder.put("objectives", this.objectivesText);
        }
        datasetsBuilder.setClassifierType(classifierType);
        return datasetsBuilder.build();
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
