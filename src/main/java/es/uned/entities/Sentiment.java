package es.uned.entities;

/**
 *
 */
public enum Sentiment {

    POSITIVE("Positive"),
    NEGATIVE("Negative"),
    NEUTRAL("Neutral");

    String sentiment;

    Sentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String getSentiment() {
        return this.sentiment;
    }

}
