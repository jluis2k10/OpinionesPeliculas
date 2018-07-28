package es.uned.entities;

/**
 * Clasificación de comentarios
 * <li>{@link #SUBJECTIVE}</li>
 * <li>{@link #OBJECTIVE}</li>
 */
public enum Opinion {

    /**
     * Comentario subjetivo (opinión)
     */
    SUBJECTIVE("Subjective"),

    /**
     * Comentario objetivo (hecho)
     */
    OBJECTIVE("Objective");

    String opinion;

    Opinion(String opinion) {
        this.opinion = opinion;
    }

    public String getOpinion() {
        return this.opinion;
    }

}
