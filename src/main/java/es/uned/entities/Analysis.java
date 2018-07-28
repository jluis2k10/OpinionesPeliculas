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
 * Entidad para análisis. Modela un cierto análisis de clasificación ejecutado
 * sobre un corpus.
 * Tabla ANALYSIS en base de datos.
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_model")
    private LanguageModel languageModel;

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

    /**
     * Los análisis son dinámicos. Contienen opciones diferentes en función del clasificador
     * y del modelo de lenguaje que se utilice. Por lo tanto los formularios que se utilzian
     * en las vistas de la capa de presentación también deben ser dinámicos, conteniendo
     * campos diferentes para cada tipo de análisis disponible.
     * <p>
     * Con este constructor recogemos la información introducida en un formulario y la
     * convertimos a una entidad análisis que puede ser respaldada en la base de datos.
     * @param analysisForm Formulario de análisis con campos dinámicos
     */
    public Analysis(AnalysisForm analysisForm) {
        if (analysisForm.getClassifierType().equals("polarity"))
            this.analysisType = ClassifierType.POLARITY;
        else if (analysisForm.getClassifierType().equals("opinion"))
            this.analysisType = ClassifierType.OPINION;
        this.classifier = analysisForm.getClassifierName();
        this.adapterClass = analysisForm.getAdapterClass();
        this.languageModel = analysisForm.getLanguageModel();
        this.lang = analysisForm.getLanguage();
        this.deleteStopWords = analysisForm.isDeleteStopWords();
        this.onlyOpinions = analysisForm.isIgnoreObjectives();
        this.options = analysisForm.getOptions();
    }

    /**
     * Convertir la entidad a un objeto en formato JSON.
     * @param withRecords True si se deben incluir los {@link es.uned.entities.Record} generados tras la ejecución
     *                    del análisis. False en caso contrario.
     * @return Entidad con formato JSON
     */
    public ObjectNode toJson(boolean withRecords) {
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
            analysisNode.put("language_model", getLanguageModel().getName());
        else
            analysisNode.putNull("language_model");
        analysisNode.put("lang", getLang());
        analysisNode.put("total_records", getRecords().size());
        if (withRecords) {
            ArrayNode recordsArray = mapper.createArrayNode();
            getRecords().forEach(record -> recordsArray.add(record.toJson()));
            analysisNode.set("records", recordsArray);
        }
        analysisNode.put("stop_words_deletion", isDeleteStopWords());
        analysisNode.put("opinions_only", isOnlyOpinions());
        if (getOptions().size() > 0) {
            ObjectNode optionsNode = mapper.createObjectNode();
            getOptions().forEach((key, val) ->
                    optionsNode.put(key, val)
            );
            analysisNode.set("options", optionsNode);
        }
        else {
            analysisNode.putNull("options");
        }
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

    /**
     * Especifica el {@link es.uned.entities.Corpus} sobre el cual se ejecuta este análisis.
     * <p>
     * Comprobamos si es el mismo que ya tenía o si hay que desvincular la asociación
     * Corpus <-> Análisis para no crear inconsistencias en la base de datos.
     * @param corpus Corpus con el que asociar el análisis
     */
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

    /**
     * Devuelve cierto si el corpus de entrada (nuevo corpus) es el mismo que el que tiene
     * asociado actualmente.
     * @param newCorpus Nuevo corpus con el que se quiere asociar el análisis
     * @return true si es el mismo que el que ya tiene, false en caso contrario
     */
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

    public LanguageModel getLanguageModel() {
        return languageModel;
    }

    public void setLanguageModel(LanguageModel languageModel) {
        this.languageModel = languageModel;
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
        return this.options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public Collection<Record> getRecords() {
        return records;
    }

    /**
     * Añade un nuevo {@link es.uned.entities.Record} al análisis. Se comprueba si el record ya está
     * presente y se vincula al mismo con este análisis para no generar inconsistencias con la base
     * de datos.
     * @param record Record a añadir al análisis
     */
    public void addRecord(Record record) {
        if (records.contains(record))
            return;
        records.add(record);
        record.setAnalysis(this);
    }

    /**
     * Elimina un {@link es.uned.entities.Record} del análisis. Se elimina la asociación por ambos
     * lados de la misma para no crear inconsistencias en la base de datos (es decir se elimina
     * también el análisis del objeto Record).
     * @param record Record a eliminar del análisis
     */
    public void removeRecord(Record record) {
        if (!records.contains(record))
            return;
        record.setAnalysis(null);
        records.remove(record);
    }

    public void clearAllRecords() {
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
                Objects.equals(getLanguageModel(), analysis.getLanguageModel()) &&
                Objects.equals(getClassifier(), analysis.getClassifier()) &&
                Objects.equals(getAdapterClass(), analysis.getAdapterClass()) &&
                Objects.equals(getLang(), analysis.getLang()) &&
                Objects.equals(getOptions(), analysis.getOptions()) &&
                Objects.equals(getRecords().size(), analysis.getRecords().size());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAnalysisType(), getLanguageModel(), getClassifier(), getAdapterClass(), getLang(), isDeleteStopWords(), isOnlyOpinions(), getOptions(), getRecords().size());
    }
}
