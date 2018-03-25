package es.uned.entities;

import es.uned.adapters.ClassifierType;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
@Entity
@Table(name = "Models")
public class AdapterModels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50, unique = true, nullable = false)
    private String name;

    @Column(name = "adapter_type", length = 13)
    private ClassifierType adapterType;

    @Column(name = "adapter_class", nullable = false)
    private String adapterClass;

    @Column(name = "language", length = 2, nullable = false)
    private String language;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "trainable")
    private boolean trainable = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner")
    private Account owner;

    @Column(name = "open")
    private boolean open;

    @Column(name = "description", nullable = true)
    private String description;

    @Transient private boolean textDataset = true;
    @Transient private String psText; // positive or subjective text
    @Transient private String noText; // negative or objective text
    @Transient private MultipartFile psFile; // positive or subjective file
    @Transient private MultipartFile noFile; // negative or objective file

    @Transient
    private final String PARAMS_KEYS = "(?:id|name|adapterClass|language|location|trainable|description|" +
            "textDataset|psText|noText|psFile|noFile)";

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

    public List<String> getPositivesSubjectives() {
        if (isTextDataset())
            return getSentences(getPsText());
        else
            return getSentences(getPsFile());
    }

    public List<String> getNegativesObjectives() {
        if (isTextDataset())
            return getSentences(getNoText());
        else
            return getSentences(getNoFile());
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClassifierType getAdapterType() {
        return adapterType;
    }

    public void setAdapterType(ClassifierType adapterType) {
        this.adapterType = adapterType;
    }

    public String getAdapterClass() {
        return adapterClass;
    }

    public void setAdapterClass(String adapterClass) {
        this.adapterClass = adapterClass;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isTrainable() {
        return trainable;
    }

    public void setTrainable(boolean trainable) {
        this.trainable = trainable;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isTextDataset() {
        return textDataset;
    }

    public void setTextDataset(boolean textDataset) {
        this.textDataset = textDataset;
    }

    public String getPsText() {
        return psText;
    }

    public void setPsText(String psText) {
        this.psText = psText;
    }

    public String getNoText() {
        return noText;
    }

    public void setNoText(String noText) {
        this.noText = noText;
    }

    public MultipartFile getPsFile() {
        return psFile;
    }

    public void setPsFile(MultipartFile psFile) {
        this.psFile = psFile;
    }

    public MultipartFile getNoFile() {
        return noFile;
    }

    public void setNoFile(MultipartFile noFile) {
        this.noFile = noFile;
    }
}
