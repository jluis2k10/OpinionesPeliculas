package es.uned.forms;

import es.uned.adapters.ClassifierType;
import es.uned.entities.LanguageModel;
import es.uned.entities.Opinion;
import es.uned.entities.Polarity;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateLanguageModelForm {

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

    private final String PARAMS_KEYS = "(?:name|adapterClass|language|location|trainable|description|" +
            "isPublic|textDataset|\\w*Text\\b|\\w*File\\b)";

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

    public Map<Enum, List<String>> buildDatasets() {
        return (classifierType == ClassifierType.POLARITY ? buildPolarityDatasets() : buildOpinionDatasets());
    }

    private Map<Enum, List<String>> buildPolarityDatasets() {
        Map<Enum, List<String>> datasets = new EnumMap(Polarity.class);
        if (isTextDataset()) {
            datasets.put(Polarity.POSITIVE, getSentences(getPositivesText()));
            datasets.put(Polarity.NEGATIVE, getSentences(getNegativesText()));
            if (null != neutralsText && !neutralsText.isEmpty())
                datasets.put(Polarity.NEUTRAL, getSentences(getNeutralsText()));
        }
        else {
            datasets.put(Polarity.POSITIVE, getSentences(getPositivesFile()));
            datasets.put(Polarity.NEGATIVE, getSentences(getNegativesFile()));
            if (!neutralsFile.isEmpty())
                datasets.put(Polarity.NEUTRAL, getSentences(getNeutralsFile()));
        }
        return datasets;
    }

    private Map<Enum, List<String>> buildOpinionDatasets() {
        Map<Enum, List<String>> datasets = new EnumMap(Opinion.class);
        if (isTextDataset()) {
            datasets.put(Opinion.SUBJECTIVE, getSentences(getSubjectivesText()));
            datasets.put(Opinion.OBJECTIVE, getSentences(getObjectivesText()));
        }
        else {
            datasets.put(Opinion.SUBJECTIVE, getSentences(getSubjectivesFile()));
            datasets.put(Opinion.OBJECTIVE, getSentences(getObjectivesFile()));
        }
        return datasets;
    }

    private List<String> getSentences(String text) {
        String[] lines = text.split("\\r?\\n");
        return Arrays.asList(lines);
    }

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
