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

    public ObjectNode toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        ObjectNode commentNode = mapper.createObjectNode();

        commentNode.put("id", getId());
        commentNode.put("source_url", getSourceURL());
        commentNode.put("date", dateFormat.format(getDate()));
        commentNode.put("comment", getComment());
        if (getSentiment() != null) {
            commentNode.put("sentiment", getSentiment().getSentiment());
            commentNode.put("sentiment_score", getSentimentScore());
        } else {
            commentNode.putNull("sentiment");
            commentNode.putNull("sentiment_score");
        }
        if (getSubjectivity() != null) {
            commentNode.put("subjectivity", getSubjectivity().getSubjectivity());
            commentNode.put("subjectivity_score", getSubjectivityScore());
        } else {
            commentNode.putNull("subjectivity");
            commentNode.putNull("subjectivity_score");
        }
        return commentNode;
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
