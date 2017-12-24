package es.uned.entities;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;

/**
 *
 */
@Entity
@Table(name = "Comments")
public class CommentWithSentiment {

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

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "tokenized")
    private boolean tokenized = false;

    @Column(name = "tokenized_comment")
    private String tokenizedComment;

    @Enumerated(EnumType.STRING)
    @Column(name = "sentiment")
    private Sentiment sentiment;

    @Column(name = "sentiment_score", columnDefinition = "double")
    private double sentimentScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "subjectivity")
    private Subjectivity subjectivity;

    @Column(name = "subjectivity_score", columnDefinition = "double")
    private double subjectivityScore;

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

    public Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }

    public double getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public Subjectivity getSubjectivity() {
        return subjectivity;
    }

    public void setSubjectivity(Subjectivity subjectivity) {
        this.subjectivity = subjectivity;
    }

    public double getSubjectivityScore() {
        return subjectivityScore;
    }

    public void setSubjectivityScore(double subjectivityScore) {
        this.subjectivityScore = subjectivityScore;
    }
}
