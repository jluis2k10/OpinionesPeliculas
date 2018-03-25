package es.uned.entities;

/**
 *
 */
public enum Opinion {

    SUBJECTIVE("Subjective"),
    OBJECTIVE("Objective");

    String opinion;

    Opinion(String opinion) {
        this.opinion = opinion;
    }

    public String getOpinion() {
        return this.opinion;
    }

}
