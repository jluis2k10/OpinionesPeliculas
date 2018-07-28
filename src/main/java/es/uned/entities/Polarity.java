package es.uned.entities;

/**
 * Clasificación de comentarios por tipo de sentimiento
 * <li>{@link #POSITIVE}</li>
 * <li>{@link #NEGATIVE}</li>
 * <li>{@link #NEUTRAL}</li>
 */
public enum Polarity {

    /**
     * Comentario positivo
     */
    POSITIVE("Positive"),

    /**
     * Comentario negativo
     */
    NEGATIVE("Negative"),

    /**
     * Comentario neutral
     */
    NEUTRAL("Neutral");

    String polarity;

    Polarity(String polarity) {
        this.polarity = polarity;
    }

    public String getPolarity() {
        return this.polarity;
    }

}
