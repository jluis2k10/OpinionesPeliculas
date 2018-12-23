package es.uned.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.adapters.ClassifierType;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Entidad para comentarios. Modela los textos recuperados sobre los que se
 * ejecutarán los diferentes análisis.
 * <p>
 * Contienen la puntuación media obtenida tras ejecutar análisis de opinión o
 * polaridad sobre ellos, así como el tipo de Opinión ({@link es.uned.entities.Opinion}) o
 * Sentimiento ({@link es.uned.entities.Polarity}) más probable en función de
 * dichos análisis.
 * <p>
 * Tabla COMMENTS de la base de datos.
 */
@Entity
@Table(name = "comments")
public class Comment implements Comparable<Comment> {

    private static Log logger = LogFactory.getLog(Comment.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_hash", nullable = false)
    private int hash;

    @ManyToOne
    @JoinColumn(name = "corpus_id", insertable = false, updatable = false)
    private Corpus corpus;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "url")
    private String url;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "polarity")
    private Polarity polarity;

    @Column(name = "polarity_score", columnDefinition = "double")
    private double polarityScore;

    @Column(name = "pos_score", columnDefinition = "double")
    private double positivityScore;

    @Column(name = "neg_score", columnDefinition = "double")
    private double negativityScore;

    @Column(name = "neu_score", columnDefinition = "double")
    private double neutralityScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "opinion")
    private Opinion opinion;

    @Column(name = "opinion_score", columnDefinition = "double")
    private double opinionScore;

    @Column(name = "domain")
    private String domain;

    @Column(name = "domain_score", columnDefinition = "double")
    private double domainScore;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    private Collection<Record> records = new LinkedList<>();

    private Comment(){
    }

    /**
     * Constructor a partir del {@link Builder} (patrón Builder).
     * @param builder Builder para el comentario
     */
    private Comment(Builder builder) {
        setContent(builder.content);
        setSource(builder.source);
        setUrl(builder.url);
        setDate(builder.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        setCorpus(builder.corpus);
    }

    /**
     * Añade un nuevo {@link es.uned.entities.Record} al comentario. Los records son el resultado
     * de ejecuciones individuales de un análisis sobre el comentario. Con cada ejecución de un
     * nuevo análisis sobre el comentario, hay que actulizar las puntuaciones medias de dicho
     * comentario para evitar tener que hacerlo en tiempo real cuando se soliciten.
     * <p>
     * Además, añadimos también este comentario al propio record para no crear inconsistencias
     * en la base de datos.
     * @param record Record a añadir al comentario tras la ejecución de un análisis
     */
    public void addRecord(Record record) {
        if (records.contains(record))
            return;

        // Calcular polaridad y opinión medias
        if (record.getPolarity() != null) {
            int nAnalysis = (int) this.records.stream()
                    .filter(r -> r.getPolarity() != null)
                    .count();
            this.positivityScore = ( (getPositivityScore() * nAnalysis) + record.getPositiveScore() ) / ( nAnalysis + 1 );
            this.negativityScore = ( (getNegativityScore() * nAnalysis) + record.getNegativeScore() ) / ( nAnalysis + 1 );
            this.neutralityScore = ( (getNeutralityScore() * nAnalysis) + record.getNeutralScore()) / ( nAnalysis + 1 );
            if (this.positivityScore >= this.negativityScore && this.positivityScore >= this.neutralityScore) {
                setPolarity(Polarity.POSITIVE);
                setPolarityScore(this.positivityScore);
            }
            else if (this.negativityScore > this.positivityScore && this.negativityScore > this.neutralityScore) {
                setPolarity(Polarity.NEGATIVE);
                setPolarityScore(this.negativityScore);
            }
            else {
                setPolarity(Polarity.NEUTRAL);
                setPolarityScore(this.neutralityScore);
            }
        }
        else if (record.getOpinion() != null) {
            int nAnalysis = (int) this.records.stream()
                    .filter(r -> r.getOpinion() != null)
                    .count();
            this.opinionScore = ( (getOpinionScore() * nAnalysis) + record.getSubjectiveScore() ) / ( nAnalysis + 1);
            if (this.opinionScore >= 0.5)
                setOpinion(Opinion.SUBJECTIVE);
            else
                setOpinion(Opinion.OBJECTIVE);
        }

        this.records.add(record);
        record.setComment(this);
    }

    /**
     * Elimina un {@link es.uned.entities.Record} del comentario. Asimismo se elimina también el comentario
     * del propio record para evitar inconsistencias en la base de datos.
     * <p>
     * Ojo! Tras esta operación hay que refrescar las puntuaciones medias del comentario mediante el método
     * {@link #refreshScores()}. No se hace aquí directamente por motivos de eficiencia en caso de borrar
     * varios records de forma simultánea (evitamos hacer cálculos innecesarios tras eliminar cada record
     * haciendo una única llamada al método {@link #refreshScores()}
     * @param record El record que se desea eliminar
     */
    public void removeRecord(Record record) {
        if (!records.contains(record))
            return;
        record.setComment(null);
        records.remove(record);
    }

    /**
     * Encuentra el {@link es.uned.entities.Record} correspondiente al {@link es.uned.entities.Analysis}
     * indicado.
     * @param analysisID ID del análisis del record a encontrar
     * @return el record encontrado
     */
    public Record findRecord(Long analysisID) {
        if (records.size() == 0 || null == analysisID)
            return new Record();
        return records.stream()
                .filter(record -> analysisID.equals(record.getAnalysis().getId()))
                .findAny()
                .orElse(new Record());
    }

    /**
     * Actualiza/recalcula las puntuaciones medias del comentario en función de los análisis que
     * se hayan ejecutado sobre él.
     */
    public void refreshScores() {
        setPolarityScore(0L);
        setOpinionScore(0L);
        setPositivityScore(0L);
        setNegativityScore(0L);
        setNeutralityScore(0L);

        long totalOpinionAnalyses = getRecords().stream()
                .filter(record -> record.getAnalysis().getAnalysisType() == ClassifierType.OPINION)
                .count();
        totalOpinionAnalyses = totalOpinionAnalyses > 0 ? totalOpinionAnalyses : 1L; // No queremos dividir /0
        long totalPolarityAnalyses = getRecords().stream()
                .filter(record -> record.getAnalysis().getAnalysisType() == ClassifierType.POLARITY)
                .count();
        totalPolarityAnalyses = totalPolarityAnalyses > 0 ? totalPolarityAnalyses : 1L; // Idem

        getRecords().forEach(record -> {
            if (record.getAnalysis().getAnalysisType() == ClassifierType.OPINION) {
                this.opinionScore += record.getSubjectiveScore();
            } else {
                this.positivityScore += record.getPositiveScore();
                this.negativityScore += record.getNegativeScore();
                this.neutralityScore += record.getNeutralScore();
            }
        });
        this.opinionScore = this.opinionScore / totalOpinionAnalyses;
        this.positivityScore = this.positivityScore / totalPolarityAnalyses;
        this.negativityScore = this.negativityScore / totalPolarityAnalyses;
        this.neutralityScore = this.neutralityScore / totalPolarityAnalyses;

        if (null != getOpinion() && this.opinionScore >= 0.5)
            setOpinion(Opinion.SUBJECTIVE);
        else if (null != getOpinion() && this.opinionScore < 0.5)
            setOpinion(Opinion.OBJECTIVE);

        if (null != getPolarity() && this.positivityScore >= this.negativityScore && this.positivityScore >= this.neutralityScore) {
            setPolarity(Polarity.POSITIVE);
            this.polarityScore = this.positivityScore;
        }
        else if (null != getPolarity() && this.negativityScore > this.positivityScore && this.negativityScore >= this.neutralityScore) {
            setPolarity(Polarity.NEGATIVE);
            this.polarityScore = this.negativityScore;
        }
        else if (null != getPolarity() && this.neutralityScore > this.positivityScore && this.neutralityScore > this.negativityScore) {
            setPolarity(Polarity.NEUTRAL);
            this.polarityScore = this.neutralityScore;
        }
    }

    /**
     * Converir la entidad a un objeto en formato JSON
     * @param withRecords True si se deben incluir los {@link es.uned.entities.Record} que contiene
     * @return Entidad con formato JSON
     */
    public ObjectNode toJson(boolean withRecords) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode commentNode = mapper.createObjectNode();

        if (getId() != null)
            commentNode.put("id", getId());
        else
            commentNode.putNull("id");
        commentNode.put("hash", getHash());
        commentNode.put("date", getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        commentNode.put("source", getSource());
        commentNode.put("url", getUrl());
        commentNode.put("content", getContent());
        if (getPolarity() != null)
            commentNode.put("polarity", getPolarity().getPolarity());
        else
            commentNode.putNull("polarity");
        commentNode.put("polarityScore", getPolarityScore());
        if (getOpinion() != null)
            commentNode.put("opinion", getOpinion().getOpinion());
        else
            commentNode.putNull("opinion");
        commentNode.put("opinionScore", getOpinionScore());
        commentNode.put("positivityScore", getPositivityScore());
        commentNode.put("negativityScore", getNegativityScore());
        commentNode.put("neutralityScore", getNeutralityScore());
        if (getDomain() != null && !getDomain().isEmpty())
            commentNode.put("domain", getDomain());
        else
            commentNode.putNull("domain");
        commentNode.put("domainScore", getDomainScore());

        if (withRecords) {
            ArrayNode sentimentRecordsArray = mapper.createArrayNode();
            ArrayNode opinionRecordsArray = mapper.createArrayNode();
            getRecords().forEach(record -> {
                ObjectNode recordNode = mapper.createObjectNode();
                String classifierName = record.getAnalysis().getClassifier();
                if (null != record.getAnalysis().getLanguageModel())
                    classifierName += " (" + record.getAnalysis().getLanguageModel().getName() + ")";
                recordNode.put("classifier", classifierName);
                recordNode.set("record", record.toJson());
                if (record.getAnalysis().getAnalysisType() == ClassifierType.POLARITY)
                    sentimentRecordsArray.add(recordNode);
                else if (record.getAnalysis().getAnalysisType() == ClassifierType.OPINION)
                    opinionRecordsArray.add(recordNode);
            });
            if (sentimentRecordsArray.size() > 0)
                commentNode.set("sentimentRecords", sentimentRecordsArray);
            else
                commentNode.putNull("sentimentRecords");
            if (opinionRecordsArray.size() > 0)
                commentNode.set("opinionRecords", opinionRecordsArray);
            else
                commentNode.putNull("opinionRecords");
        }
        return commentNode;
    }

    @Override
    public int compareTo (Comment c) {
        return getDate().compareTo(c.getDate());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.records.forEach(record -> record.getId().setComment(id));
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public Corpus getCorpus() {
        return corpus;
    }

    /**
     * Especifica el {@link es.uned.entities.Corpus} al que corresponde este comentario. Se añade también el
     * propio comentario al corpus indicado para evitar inconsistencias en la base de datos.
     * <p>
     * Si el corpus es null, se elimina este comentario del corpus.
     * @param corpus El corpus al cual pertenece este comentario
     */
    public void setCorpus(Corpus corpus) {
        if (sameAsFormer(corpus))
            return;
        Corpus oldCorpus = this.corpus;
        this.corpus = corpus;
        if (oldCorpus != null)
            oldCorpus.removeComment(this);
        if (corpus != null)
            corpus.addComment(this);
    }

    /**
     * Devuelve cierto si el {@link es.uned.entities.Corpus} de entrada es el mismo que el actual.
     * @param newCorpus Nuevo corpus con el que se quiere asociar el comentario
     * @return true si es el mismo que el actual, false en caso contrario
     */
    public boolean sameAsFormer(Corpus newCorpus) {
        return corpus == null ? newCorpus == null : corpus.equals(newCorpus);
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    /**
     * Especificamos el texto de la entidad, es decir el comentario en sí mismo. Al mismo tiempo, calculamos
     * el hash de dicho texto y actualizamos su valor en la propiedad {@link #hash} de la entidad.
     * @param content Contenido (texto) del comentario
     */
    public void setContent(String content) {
        this.content = content;
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getContent());
        setHash(hashCodeBuilder.toHashCode());
    }

    public Polarity getPolarity() {
        return polarity;
    }

    public void setPolarity(Polarity polarity) {
        this.polarity = polarity;
    }

    public Opinion getOpinion() {
        return opinion;
    }

    public void setOpinion(Opinion opinion) {
        this.opinion = opinion;
    }

    public double getOpinionScore() {
        return opinionScore;
    }

    public void setOpinionScore(double opinionScore) {
        this.opinionScore = opinionScore;
    }

    public double getPolarityScore() {
        return polarityScore;
    }

    public void setPolarityScore(double polarityScore) {
        this.polarityScore = polarityScore;
    }

    public double getPositivityScore() {
        return positivityScore;
    }

    public void setPositivityScore(double positivityScore) {
        this.positivityScore = positivityScore;
    }

    public double getNegativityScore() {
        return negativityScore;
    }

    public void setNegativityScore(double negativityScore) {
        this.negativityScore = negativityScore;
    }

    public double getNeutralityScore() {
        return neutralityScore;
    }

    public void setNeutralityScore(double neutralityScore) {
        this.neutralityScore = neutralityScore;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public double getDomainScore() {
        return domainScore;
    }

    public void setDomainScore(double domainScore) {
        this.domainScore = domainScore;
    }

    public Collection<Record> getRecords() {
        return records;
    }

    public void clearRecords() {
        records.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return getHash() == comment.getHash() &&
                Objects.equals(getSource(), comment.getSource()) &&
                Objects.equals(getUrl(), comment.getUrl()) &&
                Objects.equals(getContent(), comment.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHash(), getSource(), getUrl(), getContent());
    }

    /**
     * Clase para construir un nuevo comentario de forma más versátil y fácil de leer.
     * Clásico patrón Builder.
     */
    @Component
    public static class Builder {
        private Corpus corpus;
        private String source;
        private String url;
        private Date date;
        private String content;

        public Builder corpus(Corpus corpus) {
            this.corpus = corpus;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Comment build() {
            return new Comment(this);
        }
    }
}
