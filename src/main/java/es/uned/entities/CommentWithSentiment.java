package es.uned.entities;

/**
 *
 */
public class CommentWithSentiment {

    private String searchTerm;
    private String comment;
    private boolean isTokenized;
    private String tokenizedComment;
    private String sentimentClassifier;
    private String predictedSentiment;
    private double sentimentScore;

    private CommentWithSentiment() {}

    public static class Builder {
        private String searchTerm;
        private String comment;

        public Builder searchTerm(String val) {
            searchTerm = val;
            return this;
        }

        public Builder comment(String val) {
            comment = val;
            return this;
        }

        public CommentWithSentiment build() {
            return new CommentWithSentiment(this);
        }
    }

    private CommentWithSentiment(Builder builder) {
        searchTerm = builder.searchTerm;
        comment = builder.comment;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isTokenized() {
        return isTokenized;
    }

    public void setTokenized(boolean tokenized) {
        isTokenized = tokenized;
    }

    public String getTokenizedComment() {
        return tokenizedComment;
    }

    public void setTokenizedComment(String tokenizedComment) {
        this.tokenizedComment = tokenizedComment;
    }

    public String getSentimentClassifier() {
        return sentimentClassifier;
    }

    public void setSentimentClassifier(String sentimentClassifier) {
        this.sentimentClassifier = sentimentClassifier;
    }

    public String getPredictedSentiment() {
        return predictedSentiment;
    }

    public void setPredictedSentiment(String predictedSentiment) {
        this.predictedSentiment = predictedSentiment;
    }

    public double getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }
}
