package es.uned.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
 *
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

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    private Collection<Record> records = new LinkedList<>();

    private Comment(){
    }

    private Comment(Builder builder) {
        setContent(builder.content);
        setSource(builder.source);
        setUrl(builder.url);
        setDate(builder.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        setCorpus(builder.corpus);
    }

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

    public void removeRecord(Record record) {
        if (!records.contains(record))
            return;
        record.setComment(null);
        records.remove(record);
    }

    public ObjectNode toJson() {
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
