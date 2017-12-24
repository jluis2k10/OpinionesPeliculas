package es.uned.entities;

import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
@Entity
@Table(name = "Searches")
public class Search {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source", length = 50, nullable = false)
    private String source;

    @Column(name = "term", nullable = false)
    private String term;

    @Temporal(value = TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "created")
    private Date created;

    @Temporal(value = TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "updated")
    private Date updated;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "search")
    private List<CommentWithSentiment> comments = new LinkedList<>();

    @Column(name = "lang", nullable = false)
    private String lang;
    @Column(name = "clean_tweet")
    private boolean cleanTweet;
    @Column(name = "del_stopwords")
    private boolean delStopWords;
    @Column(name = "sentiment_adapter", nullable = false)
    private String sentimentAdapter;
    @Column(name = "sentiment_model", nullable = false)
    private String sentimentModel;
    @Column(name = "classify_subjectivity", nullable = false)
    private boolean classifySubjectivity;
    @Column(name = "subjectivity_adapter", nullable = false)
    private String subjectivityAdapter;
    @Column(name = "subjectivity_model", nullable = false)
    private String subjectivityModel;
    @Column(name = "discard_non_subjective")
    private boolean discardNonSubjective;
    @Column(name = "source_class", nullable = false)
    private String sourceClass;

    @Transient
    private int limit;
    @Transient
    private String sinceDate;
    @Transient
    private String untilDate;

    @ElementCollection
    @MapKeyColumn(name = "parameter")
    @Column(name = "value")
    Map<String, String> extraParameters = new HashMap<>();

    /*
    Campos que no deben tenerse en cuenta para crear los parámetros extra @see #makeExtraParams
     */
    @Transient
    private final String PARAMS_KEYS = "(?:source|term|created|updated|sourceClass|limit|sinceDate|untilDate|lang|" +
            "cleanTweet|delStopWords|sentimentAdapter|sentimentModel|classifySubjectivity|subjectivityAdapter|" +
            "subjectivityModel|discardNonSubjective)";

    public Search() {}

    /**
     * Construir objeto Search a partir de objeto @link{es.uned.entities.TrainParams}
     * @param trainParams objeto @link{es.uned.entities.TrainParams}
     */
    public Search(TrainParams trainParams) {
        setTerm(trainParams.getSearchTerm());
        setSourceClass(trainParams.getSourceClass());
        setLimit(trainParams.getLimit());
        setSinceDate(trainParams.getSinceDate());
        setUntilDate(trainParams.getUntilDate());
        setLang(trainParams.getLang());
    }

    /**
     * Generar los parámetros extra de la búsqueda.
     * Son parámetros no contemplados en la clase pero que pueden darse a partir del archivo de
     * configuración XML de los Adaptadores de Búsqueda pero que están presentes en el POST
     * del formulario de búsqueda.
     * Por ejemplo para twitter podríamos tener un adaptador que buscara tweets de un usuario
     * concreto. Esta opción no la contempla esta clase pero podríamos pasársela al adaptador
     * mediante estos parámetros extra.
     * @param parameters Todos los parámetros (POST) provenientes del formulario.
     */
    public void makeExtraParams(Map<String,String[]> parameters) {
        Pattern pattern = Pattern.compile(PARAMS_KEYS);
        parameters.forEach((key, value) -> {
            Matcher matcher = pattern.matcher(key);
            if (!matcher.matches())
                this.extraParameters.put(key, StringUtils.join(value, ""));
        });
    }

    public String getPARAMS_KEYS() {
        return PARAMS_KEYS;
    }

    public String getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(String sourceClass) {
        this.sourceClass = sourceClass;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getSinceDate() {
        return sinceDate;
    }

    public void setSinceDate(String sinceDate) {
        this.sinceDate = sinceDate;
    }

    public String getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(String untilDate) {
        this.untilDate = untilDate;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public boolean isCleanTweet() {
        return cleanTweet;
    }

    public void setCleanTweet(boolean cleanTweet) {
        this.cleanTweet = cleanTweet;
    }

    public boolean isDelStopWords() {
        return delStopWords;
    }

    public void setDelStopWords(boolean delStopWords) {
        this.delStopWords = delStopWords;
    }

    public String getSentimentAdapter() {
        return sentimentAdapter;
    }

    public void setSentimentAdapter(String sentimentAdapter) {
        this.sentimentAdapter = sentimentAdapter;
    }

    public String getSentimentModel() {
        return sentimentModel;
    }

    public void setSentimentModel(String sentimentModel) {
        this.sentimentModel = sentimentModel;
    }

    public boolean isClassifySubjectivity() {
        return classifySubjectivity;
    }

    public void setClassifySubjectivity(boolean classifySubjectivity) {
        this.classifySubjectivity = classifySubjectivity;
    }

    public String getSubjectivityAdapter() {
        return subjectivityAdapter;
    }

    public void setSubjectivityAdapter(String subjectivityAdapter) {
        this.subjectivityAdapter = subjectivityAdapter;
    }

    public String getSubjectivityModel() {
        return subjectivityModel;
    }

    public void setSubjectivityModel(String subjectivityModel) {
        this.subjectivityModel = subjectivityModel;
    }

    public boolean isDiscardNonSubjective() {
        return discardNonSubjective;
    }

    public void setDiscardNonSubjective(boolean discardNonSubjective) {
        this.discardNonSubjective = discardNonSubjective;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public List<CommentWithSentiment> getComments() {
        return comments;
    }

    public void setComments(LinkedList<CommentWithSentiment> comments) {
        this.comments = comments;
    }

    public Map<String, String> getExtraParameters() {
        return extraParameters;
    }

    public void setExtraParameters(Map<String, String> extraParameters) {
        this.extraParameters = extraParameters;
    }
}
