package es.uned.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner")
    private Account owner;

    @Column(name = "source", length = 50, nullable = false)
    private String source;

    @Column(name = "term", nullable = false)
    private String term;

    @Column(name = "title")
    private String title;

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
    @Column(name = "classify_subjectivity")
    private boolean classifySubjectivity;
    @Column(name = "subjectivity_adapter")
    private String subjectivityAdapter;
    @Column(name = "subjectivity_model")
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
            "subjectivityModel|discardNonSubjective|owner|_csrf)";

    public Search() {}

    /**
     * Construir objeto Search a partir de objeto @link{es.uned.entities.TrainParams}
     * @param trainParams objeto @link{es.uned.entities.TrainParams}
     */
    public Search(TrainParams trainParams) {
        setTerm(trainParams.getTerm());
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

    public ObjectNode toJSON(boolean withComments) {
        ObjectMapper mapper = new ObjectMapper();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        ObjectNode searchNode = mapper.createObjectNode();

        searchNode.put("id", getId());
        if (getOwner() != null)
            searchNode.put("owner", getOwner().getUserName());
        else
            searchNode.putNull("owner");
        if (getTerm().startsWith("tt"))
            searchNode.put("term", getTitle());
        else
            searchNode.put("term", getTerm());
        searchNode.put("source", getSource());
        searchNode.put("lang", getLang());
        if (isClassifySubjectivity() && isDiscardNonSubjective())
            searchNode.put("subjectivity", "Sí (descartar)");
        else if (isClassifySubjectivity())
            searchNode.put("subjectivity", "Sí");
        else
            searchNode.put("subjectivity", "No");
        searchNode.put("total_comments", getComments().size());
        if (withComments) {
            ArrayNode commentsArrayNode = mapper.createArrayNode();
            getComments().forEach(comment -> {
                commentsArrayNode.add(comment.toJSON());
            });
            searchNode.set("comments", commentsArrayNode);
        }
        searchNode.put("created", dateFormat.format(getCreated()));
        if (getUpdated() != null)
            searchNode.put("updated", dateFormat.format(getUpdated()));
        else
            searchNode.putNull("updated");
        searchNode.put("source_class", getSourceClass());
        searchNode.put("sentiment_adapter", getSentimentAdapter());
        searchNode.put("sentiment_model", getSentimentModel());
        searchNode.put("subjectivity_adapter", getSubjectivityAdapter());
        searchNode.put("subjectivity_model", getSubjectivityModel());

        return searchNode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setComments(List<CommentWithSentiment> comments) {
        this.comments = comments;
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

    public Map<String, String> getExtraParameters() {
        return extraParameters;
    }

    public void setExtraParameters(Map<String, String> extraParameters) {
        this.extraParameters = extraParameters;
    }
}
