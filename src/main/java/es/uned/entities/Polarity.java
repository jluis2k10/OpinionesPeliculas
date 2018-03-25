package es.uned.entities;

/**
 *
 */
public enum Polarity {

    POSITIVE("Positive"),
    NEGATIVE("Negative"),
    NEUTRAL("Neutral");

    String polarity;

    Polarity(String polarity) {
        this.polarity = polarity;
    }

    public String getPolarity() {
        return this.polarity;
    }

}
