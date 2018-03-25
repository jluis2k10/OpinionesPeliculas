package es.uned.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.adapters.ClassifierType;
import es.uned.forms.AnalysisForm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 *
 */
@Entity
@Table(name = "analysis")
public class Analysis {

    private static Log logger = LogFactory.getLog(Analysis.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "corpus_id", insertable = false, updatable = false)
    private Corpus corpus;

    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();

    @Column(name = "updated")
    private LocalDateTime updated;

    @Enumerated
    @Column(name = "analysis_type", nullable = false, columnDefinition = "smallint")
    private ClassifierType analysisType;

    @Column(name = "classifier", nullable = false)
    private String classifier;

    @Column(name = "adapterClass", nullable = false)
    private String adapterClass;

    @Column(name = "language_model")
    private String languageModel;

    @Column(name = "language_model_location")
    private String languageModelLocation;

    @Column(name = "language")
    private String lang;

    @Column(name = "delete_stopwords")
    private boolean deleteStopWords;

    @Column(name = "only_opinions")
    private boolean onlyOpinions;

    @ElementCollection
    @MapKeyColumn(name = "option")
    @Column(name = "value")
    private Map<String, String> options = new HashMap<>();

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "analysis_id")
    private Collection<Record> records = new LinkedList<>();

    public Analysis() {}

    public Analysis(AnalysisForm analysisForm) {
        if (analysisForm.getClassifierType().equals("polarity"))
            this.analysisType = ClassifierType.POLARITY;
        else if (analysisForm.getClassifierType().equals("opinion"))
            this.analysisType = ClassifierType.OPINION;
        this.classifier = analysisForm.getClassifierName();
        this.adapterClass = analysisForm.getAdapterClass();
        this.languageModel = analysisForm.getLanguageModel();
        this.languageModelLocation = analysisForm.getLanguageModelLocation();
        this.lang = analysisForm.getLanguage();
        this.deleteStopWords = analysisForm.isDeleteStopWords();
        this.onlyOpinions = analysisForm.isIgnoreObjectives();
        this.options = analysisForm.getOptions();
    }

    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode analysisNode = mapper.createObjectNode();

        if (getId() != null)
            analysisNode.put("id", getId());
        else
            analysisNode.putNull("id");
        analysisNode.put("created", getCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        if (getUpdated() != null)
            analysisNode.put("updated", getUpdated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        else
            analysisNode.putNull("updated");
        if (getAnalysisType() == ClassifierType.OPINION)
            analysisNode.put("type", "opinion");
        else if (getAnalysisType() == ClassifierType.POLARITY)
            analysisNode.put("type", "polarity");
        analysisNode.put("classifier", getClassifier());
        if (getLanguageModel() != null)
            analysisNode.put("language_model", getLanguageModel());
        else
            analysisNode.putNull("language_model");
        analysisNode.put("lang", getLang());


        ArrayNode recordsArray = mapper.createArrayNode();
        getRecords().forEach(record -> recordsArray.add(record.toJson(getAnalysisType())));
        analysisNode.set("records", recordsArray);

        return analysisNode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.records.forEach(record -> record.getId().setAnalysis(id));
    }

    public Corpus getCorpus() {
        return corpus;
    }

    public void setCorpus(Corpus corpus) {
        if (sameAsFormer(corpus))
            return;
        Corpus oldCorpus = this.corpus;
        this.corpus = corpus;
        if (oldCorpus != null)
            oldCorpus.removeAnalysis(this);
        if (corpus != null)
            corpus.addAnalysis(this);
    }

    public boolean sameAsFormer(Corpus newCorpus) {
        return corpus == null ? newCorpus == null : corpus.equals(newCorpus);
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public ClassifierType getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(ClassifierType analysisType) {
        this.analysisType = analysisType;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getAdapterClass() {
        return adapterClass;
    }

    public void setAdapterClass(String adapterClass) {
        this.adapterClass = adapterClass;
    }

    public String getLanguageModelLocation() {
        return languageModelLocation;
    }

    public String getLanguageModel() {
        return languageModel;
    }

    public void setLanguageModel(String languageModel) {
        this.languageModel = languageModel;
    }

    public void setLanguageModelLocation(String languageModelLocation) {
        this.languageModelLocation = languageModelLocation;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public boolean isDeleteStopWords() {
        return deleteStopWords;
    }

    public void setDeleteStopWords(boolean deleteStopWords) {
        this.deleteStopWords = deleteStopWords;
    }

    public boolean isOnlyOpinions() {
        return onlyOpinions;
    }

    public void setOnlyOpinions(boolean onlyOpinions) {
        this.onlyOpinions = onlyOpinions;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public Collection<Record> getRecords() {
        return records;
    }

    public void addRecord(Record record) {
        if (records.contains(record))
            return;
        records.add(record);
        record.setAnalysis(this);
    }

    public void removeRecord(Record record) {
        if (!records.contains(record))
            return;
        record.setAnalysis(null);
        records.remove(record);
    }

    public void clearRecords() {
        records.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Analysis analysis = (Analysis) o;
        return Objects.equals(getId(), analysis.getId()) &&
                isDeleteStopWords() == analysis.isDeleteStopWords() &&
                isOnlyOpinions() == analysis.isOnlyOpinions() &&
                getAnalysisType() == analysis.getAnalysisType() &&
                Objects.equals(getClassifier(), analysis.getClassifier()) &&
                Objects.equals(getAdapterClass(), analysis.getAdapterClass()) &&
                Objects.equals(getLanguageModel(), analysis.getLanguageModel()) &&
                Objects.equals(getLanguageModelLocation(), analysis.getLanguageModelLocation()) &&
                Objects.equals(getLang(), analysis.getLang()) &&
                Objects.equals(getOptions(), analysis.getOptions()) &&
                Objects.equals(getRecords().size(), analysis.getRecords().size());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getAnalysisType(), getClassifier(), getAdapterClass(), getLanguageModel(), getLanguageModelLocation(), getLang(), isDeleteStopWords(), isOnlyOpinions(), getOptions(), getRecords().size());
    }
}
