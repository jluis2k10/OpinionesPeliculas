package es.uned.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
@Entity
@Table(name = "Comments_with_sentiment")
public class CommentWithSentiment implements Comparable<CommentWithSentiment> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "search_id", referencedColumnName = "id")
    private Search search;

    @Column(name = "source_url", length = 512)
    private String sourceURL;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date;

    @Lob
    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "tokenized")
    private boolean tokenized = false;

    @Lob
    @Column(name = "tokenized_comment")
    private String tokenizedComment;

    @Enumerated(EnumType.STRING)
    @Column(name = "sentiment")
    private Polarity sentiment;

    @Column(name = "sentiment_score", columnDefinition = "double")
    private double sentimentScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "subjectivity")
    private Opinion subjectivity;

    @Column(name = "subjectivity_score", columnDefinition = "double")
    private double subjectivityScore;

    @Column(name = "pos_score", columnDefinition = "double")
    private double positivityScore;

    @Column(name = "neg_score", columnDefinition = "double")
    private double negativityScore;

    @Column(name = "neu_score", columnDefinition = "double")
    private double neutralityScore;

    private CommentWithSentiment() {}

    private CommentWithSentiment(Builder builder) {
        this.search = builder.search;
        this.sourceURL = builder.sourceURL;
        this.date = builder.date;
        this.comment = builder.comment;
    }

    @Component
    public static class Builder {
        private Search search;
        private String sourceURL;
        private Date date;
        private String comment;

        public Builder search(Search search) {
            this.search = search;
            return this;
        }

        public Builder sourceUrl(String sourceURL) {
            this.sourceURL = sourceURL;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public CommentWithSentiment build() {
            return new CommentWithSentiment(this);
        }
    }

    public ObjectNode toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        ObjectNode commentNode = mapper.createObjectNode();

        commentNode.put("id", getId());
        commentNode.put("source_url", getSourceURL());
        commentNode.put("date", dateFormat.format(getDate()));
        commentNode.put("comment", getComment());
        if (getSentiment() != null) {
            commentNode.put("sentiment", getSentiment().getPolarity());
            commentNode.put("sentiment_score", getSentimentScore());
        } else {
            commentNode.putNull("sentiment");
            commentNode.putNull("sentiment_score");
        }
        if (getSubjectivity() != null) {
            commentNode.put("subjectivity", getSubjectivity().getOpinion());
            commentNode.put("subjectivity_score", getSubjectivityScore());
        } else {
            commentNode.putNull("subjectivity");
            commentNode.putNull("subjectivity_score");
        }

        ObjectNode scores = mapper.createObjectNode();
        scores.put("positivity", getPositivityScore());
        scores.put("negativity", getNegativityScore());
        scores.put("neutrality", getNeutralityScore());
        commentNode.set("scores", scores);

        return commentNode;
    }

    @Override
    public int compareTo(CommentWithSentiment c) {
        return getDate().compareTo(c.getDate());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isTokenized() {
        return tokenized;
    }

    public void setTokenized(boolean tokenized) {
        this.tokenized = tokenized;
    }

    public String getTokenizedComment() {
        return tokenizedComment;
    }

    public void setTokenizedComment(String tokenizedComment) {
        this.tokenizedComment = tokenizedComment;
    }

    public Polarity getSentiment() {
        return sentiment;
    }

    public void setSentiment(Polarity sentiment) {
        this.sentiment = sentiment;
    }

    public double getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public Opinion getSubjectivity() {
        return subjectivity;
    }

    public void setSubjectivity(Opinion subjectivity) {
        this.subjectivity = subjectivity;
    }

    public double getSubjectivityScore() {
        return subjectivityScore;
    }

    public void setSubjectivityScore(double subjectivityScore) {
        this.subjectivityScore = subjectivityScore;
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
}
