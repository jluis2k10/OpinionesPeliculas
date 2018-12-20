package es.uned.adapters;

/**
 * Tipos de clasificadores.
 * <li>{@link #POLARITY}</li>
 * <li>{@link #OPINION}</li>
 */
public enum ClassifierType {

    /**
     * Clasificador de polaridad
     */
    POLARITY("Polarity"),

    /**
     * Clasificador de opinión
     */
    OPINION("Opinion"),

    /**
     * Clasificador de dominio
     */
    DOMAIN("Domain");

    String classifierType;

    ClassifierType(String classifierType) {
        this.classifierType = classifierType;
    }

    public String getClassifierType() {
        return this.classifierType;
    }
}
