package es.uned.forms;

import es.uned.adapters.ClassifierType;
import es.uned.components.DatasetsBuilder;
import es.uned.entities.LanguageModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase para respaldar el formulario de crear nuevo modelo de lenguaje.
 * <p>
 * Es un formulario dinámico que puede tener parámetros opcionales en función
 * de la librería de clasificación para la que se está creando el modelo
 * de lenguaje. Por lo tanto cuenta con métodos específicos para trabajar
 * sobre este tipo de parámetros.
 */
public class CreateLanguageModelForm {

    private DatasetsBuilder datasetsBuilder = new DatasetsBuilder();

    private ClassifierType classifierType;
    private String name;
    private String location;
    private String language;
    private boolean trainable = true;
    private boolean isPublic = true;
    private String description;
    private String adapterClass;
    private boolean textDataset = true;

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

    /**
     * Usamos esta cadena de texto para diferenciar los parámetros opcionales que aparecen en
     * el formulario de los que no lo son. Toda variable cuyo nombre no encaje con este patrón
     * se considerará un parámetro opcional (dinámico) del formulario.
     */
    private final String PARAMS_KEYS = "(?:classifierType|name|adapterClass|language|location|trainable|description|" +
            "isPublic|textDataset|\\w*Text\\b|\\w*File\\b)";

    /**
     * Devuelve un mapa con los parámetros dinámicos que aparecen en el formulario, en la forma de
     * clave: nombre del parámetro, valor: valor del parámetro en el formulario.
     * @param parameters Mapa con todos los parámetros que aparecen en el formulario, tanto los
     *                   que son dinámicos como los que no.
     * @return Mapa con sólo los parámetros dinámicos del formulario
     */
    public Map<String,String> getModelParameters(Map<String,String[]> parameters) {
        Map<String,String> modelParameters = new HashMap<>();
        Pattern pattern = Pattern.compile(PARAMS_KEYS);
        parameters.forEach((key, value) -> {
            Matcher matcher = pattern.matcher(key);
            if (!matcher.matches())
                modelParameters.put(key, StringUtils.join(value, ""));
        });
        return modelParameters;
    }

    /**
     * Genera una instancia de {@link es.uned.entities.LanguageModel} a partir de los
     * datos introducidos en los campos del formulario y que puede ser respaldada directamente
     * en la base de datos.
     * @return Instancia LanguageModel
     */
    public LanguageModel generateLanguageModel() {
        LanguageModel lm = new LanguageModel();
        lm.setClassifierType(this.classifierType);
        lm.setName(this.name);
        lm.setAdapterClass(this.adapterClass);
        lm.setLanguage(this.language);
        lm.setLocation(this.location);
        lm.setTrainable(this.trainable);
        lm.setPublic(this.isPublic);
        lm.setDescription(this.description);
        if (isTextDataset() && null != neutralsText && !neutralsText.isEmpty())
            lm.setNeutralClassification(true);
        if (!isTextDataset() && !neutralsFile.isEmpty())
            lm.setNeutralClassification(true);
        return lm;
    }

    /**
     * Crea un mapa con los textos que se utilzarán para entrenar el modelo de lenguaje,
     * clasificado por las categorías.
     * <p>
     * Por ejemplo si el modelo de lenguaje es para un clasificador de análisis de
     * opinión, se devolverá un mapa de la forma:
     * <code>
     *     mapa(Subjetivos, Textos subjetivos)
     *     mapa(Objetivos, Textos objetivos)
     * </code>
     * @return Mapa con los textos que se utilizan para entrenar el modelo de lenguaje
     */
    public Map<Enum, List<String>> buildDatasets() {
        if (isTextDataset() && classifierType == ClassifierType.POLARITY) {
            datasetsBuilder.put("positives", this.positivesText);
            datasetsBuilder.put("negatives", this.negativesText);
            if (null != this.neutralsText && !neutralsText.isEmpty())
                datasetsBuilder.put("neutrals", this.neutralsText);
        }
        else if (!isTextDataset() && classifierType == ClassifierType.POLARITY) {
            datasetsBuilder.put("positives", this.positivesFile);
            datasetsBuilder.put("negatives", this.negativesFile);
            if (!this.neutralsFile.isEmpty())
                datasetsBuilder.put("neutrals", this.neutralsFile);
        }
        else if (isTextDataset() && classifierType == ClassifierType.OPINION) {
            datasetsBuilder.put("subjectives", this.subjectivesText);
            datasetsBuilder.put("objectives", this.objectivesText);
        }
        else if (!isTextDataset() && classifierType == ClassifierType.OPINION) {
            datasetsBuilder.put("subjectives", this.subjectivesFile);
            datasetsBuilder.put("objectives", this.objectivesFile);
        }
        datasetsBuilder.setClassifierType(this.classifierType);
        return datasetsBuilder.build();
    }

    public ClassifierType getClassifierType() {
        return classifierType;
    }

    public void setClassifierType(ClassifierType classifierType) {
        this.classifierType = classifierType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isTrainable() {
        return trainable;
    }

    public void setTrainable(boolean trainable) {
        this.trainable = trainable;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdapterClass() {
        return adapterClass;
    }

    public void setAdapterClass(String adapterClass) {
        this.adapterClass = adapterClass;
    }

    public boolean isTextDataset() {
        return textDataset;
    }

    public void setTextDataset(boolean textDataset) {
        this.textDataset = textDataset;
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
